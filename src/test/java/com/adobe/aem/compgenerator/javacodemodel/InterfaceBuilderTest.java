package com.adobe.aem.compgenerator.javacodemodel;

import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.utils.CommonUtils;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class InterfaceBuilderTest {
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
    void testBuild() {
        final JCodeModel codeModel = new JCodeModel();
        InterfaceBuilder builder =
                new InterfaceBuilder(codeModel, generationConfig, generationConfig.getJavaFormatedName());
        JDefinedClass jc = builder.build();

    }
}
