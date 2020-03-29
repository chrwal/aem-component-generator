const shell = require('shelljs');
const glob = require('glob');
const fs = require('fs');
const fse = require("fs-extra");
const childProcess = require('child_process');
const path = require('path');

const argv = require('optimist')
    .usage('Generates AEM components.\nUsage: $0')
    .default('workdir', '.')
    .describe('workdir', 'Relative path to script caller or absolute path of workdir')
    .demand('dataConfigRoot')
    .describe('dataConfigRoot', 'Directory of data-config.json files, relative to workdir')
    .default('generatorJarSource', '')
    .describe('generatorJarSource', 'Path of generator.jar file, relative to workdir.')
    .default('dataConfigGlob', '**/data-config_*.json')
    .describe('dataConfigGlob', 'Glob search for data-config files below \'dataConfigRoot\'')
    .default('appsDataCopyStr', 'apps-data-copy')
    .describe('appsDataCopyStr', 'Search for data-copy files or folders below \'dataConfigRoot\'')
    .default('createBundleTarget', false)
    .describe('createBundleTarget', '[true|false] for creating missing target')
    .argv
;

console.log(`\n## Setup vars and dirs`);
const optionWorkDir = argv.workdir;
const workdir = path.normalize(process.cwd() + '/' + (optionWorkDir !== '\'.\'' ? optionWorkDir : ''));
const dataConfigRoot = path.relative(workdir + '/', path.normalize(workdir + '/' + argv.dataConfigRoot));
const generatorJarSource = path.relative(workdir + '/', path.normalize(workdir + '/' + argv.generatorJarSource));
const dataConfigGlob = argv.dataConfigGlob;
const appsDataCopyStr = argv.appsDataCopyStr;
const isCreateBundleTarget = (argv.createBundleTarget === 'true' || argv.createBundleTarget === true);

console.log(`workdir: ${workdir}`);
console.log(`dataConfigRoot dir: ${dataConfigRoot}`);
console.log(`generatorJarSource dir: ${generatorJarSource}`);

try {
    process.chdir(workdir);
    console.log('CWD changed to workdir: ' + process.cwd());
} catch (err) {
    throw err;
}
if (!shell.which('java')) {
    shell.echo('Sorry, this script requires java. Please restart your IDE after a java installation.');
    shell.exit(1);
}

console.log(`\n## Check if the generator jar source exists at ${generatorJarSource}`);
if (fs.existsSync(generatorJarSource)) {
    console.log(`Using generator jar source available at ${generatorJarSource}`);
} else {
    console.log(`No generator jar source available at ${generatorJarSource}`);
}

async function app() {
    await execGenerator()
}

app();

async function execGenerator() {
    console.log(`\n## Execute generator jar with found configs at ${dataConfigRoot}`);
    return new Promise(function (resolve, reject) {
        glob(`${dataConfigGlob}`, {cwd: `${dataConfigRoot}`}, function (err, files) {
            if (err) {
                throw err;
            }
            // files is an array of filenames.
            for (const file of files) {
                console.log('------');
                if (isCreateBundleTarget) {
                    try {
                        let dataConfigJson = JSON.parse(fs.readFileSync(path.join(dataConfigRoot, file), 'utf8'));
                        let bundleTargetPath = dataConfigJson['project-settings']['bundle-path'];
                        let bundleTargetPathFromWorkdir = path.join(workdir, bundleTargetPath);
                        console.log(`Check if target exist ${bundleTargetPathFromWorkdir}`);
                        if (!fs.existsSync(bundleTargetPathFromWorkdir)) {
                            fs.mkdirSync(bundleTargetPathFromWorkdir, {recursive: true});
                            console.log(`bundle-path created at ${bundleTargetPathFromWorkdir}`);
                        }
                    } catch (err) {
                        console.log(`Error: Missing target path from data-json ['project-settings']['bundle-path']`);
                        throw err;
                    }
                }
                let dataConfigFile = path.join(dataConfigRoot, file);
                let dataConfigDir = path.dirname(dataConfigFile);
                try {
                    console.log(`Run generation for ${generatorJarSource} ${dataConfigFile}`);
                    let out = childProcess.execSync(`java -jar ${generatorJarSource} ${dataConfigFile}`);
                    console.log(out.toString());
                } catch (err) {
                    throw err;
                }
                let appsDataCopyGlob = `**/${appsDataCopyStr}/*`;
                console.log(`\n## Copy configs at ${dataConfigDir} with glob ${appsDataCopyGlob}`);
                if (!fs.existsSync(dataConfigDir)) {
                    throw `dataConfigDir not exists at ${dataConfigDir}`;
                }
                let filesToCopy = glob.sync(appsDataCopyGlob, {cwd: dataConfigDir});
                for (const fileToCopy of filesToCopy) {
                    console.log('------');
                    //read target folder for copy
                    let appsTargetPathFromWorkdir;
                    try {
                        let dataConfigJson = JSON.parse(fs.readFileSync(dataConfigFile, 'utf8'));
                        let appsTargetPath = dataConfigJson['project-settings']['apps-path'] + "/" +
                            dataConfigJson['project-settings']['component-path'] + "/" +
                            dataConfigJson['type'] + "/" +
                            dataConfigJson['name'];
                        appsTargetPathFromWorkdir = path.join(workdir, appsTargetPath);
                    } catch (err) {
                        console.log(`Error: Missing target path from data-json ['project-settings']['apps-path']`);
                        throw err;
                    }
                    try {
                        // copy appsTargetPathFromWorkdir
                        let fileToCopyWithoutMarker = fileToCopy.substring(fileToCopy.lastIndexOf(appsDataCopyStr + "/") + appsDataCopyStr.length);
                        let fileToCopyWithoutMarkerWithDir = path.join(appsTargetPathFromWorkdir, fileToCopyWithoutMarker);
                        try {
                            fse.copySync(path.join(dataConfigDir, fileToCopy), fileToCopyWithoutMarkerWithDir);
                            console.log(`${fileToCopy} was copied to ${fileToCopyWithoutMarkerWithDir}`);
                        } catch (err) {
                            throw err;
                        }
                        //let out = childProcess.execSync(`java -jar ${generatorJarSource} ${dataConfigFile}`);
                        //console.log(out.toString());
                    } catch (err) {
                        throw err;
                    }
                }
            }
        });
    })
}
