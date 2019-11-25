package com.adobe.aem.compgenerator.utils;

import com.adobe.aem.compgenerator.Constants;
import com.adobe.aem.compgenerator.models.CqEditConfig;
import com.adobe.aem.compgenerator.models.GenerationConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

class EditConfigUtilsTest {
    private String configFilePath;
    private File configFile;
    private GenerationConfig generationConfig;

    @BeforeEach
    void setUp() throws IOException {
        configFilePath = "/component-generator/data-config.json";
        configFile = new File(this.getClass().getResource(configFilePath).getFile());
        generationConfig = CommonUtils.getComponentData(configFile);
    }

    @Test
    void testCreateEditConfigXml() throws Exception {
        final CqEditConfig editConfigType = generationConfig.getOptions().getEditorConfig().getCqEditConfig();
        EditConfigUtils.createEditConfigXml(generationConfig, editConfigType, EditConfigUtils.DIALOG_EDIT_CONFIG_NAME,
                EditConfigUtils.DIALOG_EDIT_CONFIG_TYPE);
        String dialogPath = generationConfig.getCompDir() + "/" +
                StringUtils.replace(EditConfigUtils.DIALOG_EDIT_CONFIG_TYPE, "cq:", "_cq_");

        Assertions.assertTrue(new File(dialogPath).exists());
        final String filePath = dialogPath + "/" + Constants.FILENAME_CONTENT_XML;
        final String stringFromFile = FileUtils.readFileToString(new File(filePath), "UTF-8");
        Assertions.assertTrue(StringUtils
                        .contains(stringFromFile, "jcr:primaryType=\"" + EditConfigUtils.DIALOG_EDIT_CONFIG_TYPE + "\""),
                stringFromFile);
        Assertions.assertTrue(
                StringUtils.contains(stringFromFile, EditConfigUtils.DIALOG_EDIT_CONFIG_ISCONTAINER_NAME + "=\"true\""),
                stringFromFile);
        Assertions.assertTrue(
                StringUtils.contains(stringFromFile, EditConfigUtils.DIALOG_EDIT_CONFIG_ACTIONS_NAME + "=\"["),
                stringFromFile);
        Assertions.assertTrue(StringUtils.contains(stringFromFile,
                "jcr:primaryType=\"" + EditConfigUtils.DIALOG_EDIT_LISTENER_CONFIG_TYPE + "\""), stringFromFile);
        Assertions.assertTrue(StringUtils.contains(stringFromFile, "<cq:listeners"), stringFromFile);
    }

    @Test
    void testCreateChildEditConfigXml() throws Exception {
        final CqEditConfig editConfigType = generationConfig.getOptions().getEditorConfig().getCqChildEditConfig();
        EditConfigUtils
                .createEditConfigXml(generationConfig, editConfigType, EditConfigUtils.DIALOG_CHILD_EDIT_CONFIG_NAME,
                        EditConfigUtils.DIALOG_CHILD_EDIT_CONFIG_TYPE);
        String dialogPath = generationConfig.getCompDir() + "/" +
                StringUtils.replace(EditConfigUtils.DIALOG_CHILD_EDIT_CONFIG_TYPE, "cq:", "_cq_");

        Assertions.assertTrue(new File(dialogPath).exists());
        final String filePath = dialogPath + "/" + Constants.FILENAME_CONTENT_XML;
        final String stringFromFile = FileUtils.readFileToString(new File(filePath), "UTF-8");
        Assertions.assertTrue(StringUtils
                        .contains(stringFromFile, "jcr:primaryType=\"" + EditConfigUtils.DIALOG_CHILD_EDIT_CONFIG_TYPE + "\""),
                stringFromFile);
        Assertions.assertTrue(
                StringUtils.contains(stringFromFile, EditConfigUtils.DIALOG_EDIT_CONFIG_ISCONTAINER_NAME + "=\"true\""),
                stringFromFile);
        Assertions.assertTrue(
                StringUtils.contains(stringFromFile, EditConfigUtils.DIALOG_EDIT_CONFIG_ACTIONS_NAME + "=\"["),
                stringFromFile);
        Assertions.assertTrue(StringUtils.contains(stringFromFile,
                "jcr:primaryType=\"" + EditConfigUtils.DIALOG_EDIT_LISTENER_CONFIG_TYPE + "\""), stringFromFile);
        Assertions.assertTrue(StringUtils.contains(stringFromFile, "<cq:listeners"), stringFromFile);
    }

    @Test
    void testGetCreateEditorConfig() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = XMLUtils.createRootElement(doc, generationConfig);
        final Element cqEditorRoot = EditConfigUtils.createCqEditorRoot(doc, generationConfig,
                generationConfig.getOptions().getEditorConfig().getCqEditConfig(),
                EditConfigUtils.DIALOG_EDIT_CONFIG_TYPE);
        Assertions.assertEquals(7, cqEditorRoot.getAttributes().getLength());

        final Element cqChildEditorRoot = EditConfigUtils.createCqEditorRoot(doc, generationConfig,
                generationConfig.getOptions().getEditorConfig().getCqChildEditConfig(),
                EditConfigUtils.DIALOG_CHILD_EDIT_CONFIG_TYPE);
        Assertions.assertEquals(7, cqChildEditorRoot.getAttributes().getLength());
    }
}
