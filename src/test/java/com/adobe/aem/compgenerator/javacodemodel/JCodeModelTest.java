package com.adobe.aem.compgenerator.javacodemodel;

import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.utils.CommonUtils;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class JCodeModelTest {
    private String configFilePath;
    private File configFile;
    private GenerationConfig generationConfig;

    List<JDefinedClass> setUpTestAndGenerate(String configFilePathParam) throws IOException {
        configFilePath = configFilePathParam;
        configFile = new File(this.getClass().getResource(configFilePath).getFile());
        generationConfig = CommonUtils.getComponentData(configFile);
        final File file = new File(generationConfig.getProjectSettings().getBundlePath());
        file.delete();
        file.mkdirs();

        final JavaCodeModel codeModel = new JavaCodeModel();
        codeModel.buildSlingModel(generationConfig);

        List<JDefinedClass> generatedClasses = new ArrayList<>();
        final Iterator<JPackage> packages = codeModel.getCodeModel().packages();
        StringBuffer stringBuffer = new StringBuffer();
        for (Iterator<JPackage> it = packages; it.hasNext(); ) {
            for (Iterator<JDefinedClass> iter = it.next().getPackage().classes(); iter.hasNext(); ) {
                generatedClasses.add(iter.next());
            }
        }
        return generatedClasses;
    }

    @Test
    void testBuildJavaCodeModelContainer() throws IOException {
        List<JDefinedClass> generatedClasses = setUpTestAndGenerate("/component-generator/data-config-containerTest.json");
        Assertions.assertEquals(4, generatedClasses.size());

        JDefinedClass demoImpl = generatedClasses.get(0);
        Assertions.assertEquals("DemoCompContainertestImpl", demoImpl.name());
        Assertions.assertEquals(5, demoImpl.methods().size());
        Assertions.assertEquals("getJcrContainerColors1", demoImpl.methods().toArray(new JMethod[]{})[0].name());
        Assertions.assertEquals("getContainer1TextfieldTest", demoImpl.methods().toArray(new JMethod[]{})[1].name());
        Assertions.assertEquals(5, demoImpl.fields().size());
        Assertions.assertEquals("ValueMapValue",
                demoImpl.fields().get("jcrContainerColors1").annotations().toArray(new JAnnotationUse[]{})[0]
                        .getAnnotationClass().name());

        Assertions.assertEquals("ExistingModelFalse",
                demoImpl.fields().get("containerUseExsitingModelFalse").type().name());
        Assertions.assertEquals("ExistingModel",
                demoImpl.fields().get("containerUseExsitingModelTrue").type().name());

        JDefinedClass demoApi = generatedClasses.get(2);
        Assertions.assertEquals("DemoCompContainertest", demoApi.name());
        Assertions.assertEquals(5, demoApi.methods().size());
        Assertions.assertEquals("getJcrContainerColors1", demoApi.methods().toArray(new JMethod[]{})[0].name());
        Assertions.assertEquals("getContainer1TextfieldTest", demoApi.methods().toArray(new JMethod[]{})[1].name());

        demoApi = generatedClasses.get(3);
        Assertions.assertEquals("ExistingModelFalse", demoApi.name());
    }

    @Test
    void testBuildJavaCodeModelHiddenMultifield() throws IOException {
        List<JDefinedClass> generatedClasses = setUpTestAndGenerate("/component-generator/data-config-hiddenMultifieldTest.json");
        Assertions.assertEquals(6, generatedClasses.size());

        JDefinedClass demoImpl = generatedClasses.get(0);
        Assertions.assertEquals("DemoCompHiddenmultifieldtestImpl", demoImpl.name());
        Assertions.assertEquals(3, demoImpl.methods().size());
        Assertions.assertEquals("getHiddenMultifield1Item", demoImpl.methods().toArray(new JMethod[]{})[0].name());

        Assertions.assertEquals(3, demoImpl.fields().size());
        Assertions.assertEquals("ChildResource",
                demoImpl.fields().get("hiddenMultifield1Item").annotations().toArray(new JAnnotationUse[]{})[0]
                        .getAnnotationClass().name());
        Assertions.assertEquals("ChildResource",
                demoImpl.fields().get("hiddenMultifieldMoreThan1Items").annotations().toArray(new JAnnotationUse[]{})[0]
                        .getAnnotationClass().name());
        Assertions.assertEquals("ChildResource",
                demoImpl.fields().get("hiddenMultifieldUseExsitingModelTrue").annotations().toArray(new JAnnotationUse[]{})[0]
                        .getAnnotationClass().name());

    }

}
