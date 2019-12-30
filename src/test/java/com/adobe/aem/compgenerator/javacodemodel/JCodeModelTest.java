package com.adobe.aem.compgenerator.javacodemodel;

import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.utils.CommonUtils;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() throws IOException {
        configFilePath = "/component-generator/data-config-containerTest.json";
        configFile = new File(this.getClass().getResource(configFilePath).getFile());
        generationConfig = CommonUtils.getComponentData(configFile);
        final File file = new File(generationConfig.getProjectSettings().getBundlePath());
        file.delete();
        file.mkdirs();
    }

    @Test
    void testBuildJavaCodeModel() {
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
        Assertions.assertEquals(2, generatedClasses.size());

        JDefinedClass demoImpl = generatedClasses.get(0);
        Assertions.assertEquals("DemoCompImpl", demoImpl.name());
        Assertions.assertEquals(2, demoImpl.methods().size());
        Assertions.assertEquals("getJcrContainerColors1", demoImpl.methods().toArray(new JMethod[]{})[0].name());
        Assertions.assertEquals("getContainer1TextfieldTest", demoImpl.methods().toArray(new JMethod[]{})[1].name());
        Assertions.assertEquals(2, demoImpl.fields().size());
        Assertions.assertEquals("ValueMapValue",
                demoImpl.fields().get("jcrContainerColors1").annotations().toArray(new JAnnotationUse[]{})[0]
                        .getAnnotationClass().name());

        JDefinedClass demoApi = generatedClasses.get(1);
        Assertions.assertEquals("DemoComp", demoApi.name());
        Assertions.assertEquals(2, demoApi.methods().size());
        Assertions.assertEquals("getJcrContainerColors1", demoApi.methods().toArray(new JMethod[]{})[0].name());
        Assertions.assertEquals("getContainer1TextfieldTest", demoApi.methods().toArray(new JMethod[]{})[1].name());
    }

}
