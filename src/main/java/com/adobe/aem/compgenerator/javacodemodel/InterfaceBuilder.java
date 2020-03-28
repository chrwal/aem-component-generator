/*
 * #%L
 * AEM Component Generator
 * %%
 * Copyright (C) 2019 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.aem.compgenerator.javacodemodel;

import com.adobe.aem.compgenerator.Constants;
import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.models.Property;
import com.adobe.aem.compgenerator.utils.CommonUtils;
import com.adobe.cq.export.json.ComponentExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.adobe.aem.compgenerator.javacodemodel.JavaCodeModel.getFieldType;
import static com.sun.codemodel.JMod.NONE;

/**
 * <p>
 * Manages generating the necessary details to create the sling model interface.
 * </p>
 */
public class InterfaceBuilder extends JavaCodeBuilder {
    private static final Logger LOG = LogManager.getLogger(InterfaceBuilder.class);

    private final boolean isAllowExporting;
    private String interfaceClassName;

    /**
     * Construct a interface class builder
     *
     * @param codeModel        The {@link JCodeModel codeModel}
     * @param generationConfig The {@link GenerationConfig generationConfig}
     * @param interfaceName    The name of the interface
     */
    InterfaceBuilder(JCodeModel codeModel, GenerationConfig generationConfig, String interfaceName) {
        super(codeModel, generationConfig);
        this.interfaceClassName = interfaceName;
        this.isAllowExporting = generationConfig.getOptions().isAllowExporting();
    }

    /**
     * Builds the interface class based on the configuration file.
     *
     * @return reference to the Interface
     */
    public JDefinedClass build() {
        String comment = "Defines the {@code "
                + generationConfig.getJavaFormatedName()
                + "} Sling Model used for the {@code "
                + CommonUtils.getResourceType(generationConfig)
                + "} component.";

        LOG.debug("build interface for class [{}]", this.interfaceClassName);
        return buildInterface(this.interfaceClassName, null, comment, globalProperties, sharedProperties,
                privateProperties);
    }

    /**
     * method just adds getters based on the properties of generationConfig
     *
     * @param jc         the interface class
     * @param properties the list of properties
     */
    private void addGettersWithoutFields(JDefinedClass jc, List<Property> properties) {
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                if (property != null && !property.getUseExistingField()) {
                    try {
                        LOG.debug("build getter for property [{}]", property.getModelName());
                        final JType getterMethodReturnType = getGetterMethodReturnType(property);
                        if (getterMethodReturnType != null) {
                            final String methodName = Constants.STRING_GET + property.getFieldGetterName();
                            if (jc.getMethod(methodName, new JType[]{}) != null && property.getUseExistingField()) {
                                return;
                            }
                            JMethod method = jc.method(NONE, getterMethodReturnType, methodName);
                            addJavadocToMethod(method, property);

                            if (this.isAllowExporting) {
                                if (!property.isShouldExporterExpose()) {
                                    method.annotate(codeModel.ref(JsonIgnore.class));
                                }

                                if (StringUtils.isNotBlank(property.getJsonProperty())) {
                                    method.annotate(codeModel.ref(JsonProperty.class))
                                            .param("value", property.getJsonProperty());
                                }
                            }
                        }

                        if ((property.getTypeAsFieldType().equals(Property.FieldType.MULTIFIELD) &&
                                property.getItems().size() > 1) ||
                                property.getTypeAsFieldType().equals(Property.FieldType.HIDDEN_MULTIFIELD)) {
                            buildMultifieldInterface(property, null);
                        } else if (property.getTypeAsFieldType().equals(Property.FieldType.CONTAINER)) {
                            if (StringUtils.isNotBlank(property.getModelName())) {
                                buildMultifieldInterface(property, null);
                            } else {
                                buildMultifieldInterface(property, jc);
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to generate getter for [" + property.getModelName() + "] getter [" +
                                property.getFieldGetterName() + "]", e);
                    }
                }
            }
        }
    }

    private void buildMultifieldInterface(Property property, JDefinedClass interfaceClass) {
        if (!property.getUseExistingModel()) {
            String modelInterfaceName = JavaCodeModel.getMultifieldInterfaceName(property);
            String childComment = "Defines the {@code "
                    + modelInterfaceName
                    + "} Sling Model used for the multifield in {@code "
                    + CommonUtils.getResourceType(generationConfig)
                    + "} component.";

            buildInterface(modelInterfaceName, interfaceClass, childComment, property.getItems());
        }
    }

    @SafeVarargs
    private final JDefinedClass buildInterface(String interfaceName, JDefinedClass interfaceClass, String comment,
            List<Property>... propertiesLists) {
        try {
            JDefinedClass interfaceClassLocal = interfaceClass;
            if (interfaceClass == null) {
                JPackage jPackage =
                        codeModel._package(generationConfig.getProjectSettings().getModelInterfacePackage());
                interfaceClassLocal = jPackage._interface(interfaceName);
                interfaceClassLocal.javadoc().append(comment);
                interfaceClassLocal.annotate(codeModel.ref("org.osgi.annotation.versioning.ConsumerType"));

                if (this.isAllowExporting) {
                    interfaceClassLocal._extends(codeModel.ref(ComponentExporter.class));
                }
            }
            if (propertiesLists != null) {
                for (List<Property> properties : propertiesLists) {
                    addGettersWithoutFields(interfaceClassLocal, properties);
                }
            }
            return interfaceClassLocal;
        } catch (Exception e) {
            LOG.error("Failed to generate child interface for '" + interfaceName + "' and comment '" + comment + "'.",
                    e);
        }

        return null;
    }

    /**
     * Gets the return type of the getter method based on what type of property it is referring to.
     *
     * @param property the property for which the return type is calculated.
     * @return the type being returned by the getter
     */
    private JType getGetterMethodReturnType(final Property property) {
        String fieldType = getFieldType(property);
        if (fieldType != null) {
            LOG.debug("getGetterMethodReturnType for property {} with fieldType '{}'", property, fieldType);
            final boolean isMultifield = property.getTypeAsFieldType().equals(Property.FieldType.MULTIFIELD);
            final boolean isHiddenMultifield =
                    property.getTypeAsFieldType().equals(Property.FieldType.HIDDEN_MULTIFIELD);
            if (isMultifield || isHiddenMultifield) {
                if (isMultifield && property.getItems().size() == 1) {
                    return codeModel.ref(fieldType).narrow(codeModel.ref(getFieldType(property.getItems().get(0))));
                } else {
                    String narrowedClassName = StringUtils.defaultString(property.getModelName(),
                            CaseUtils.toCamelCase(property.getField(), true) + "Multifield");
                    LOG.debug("narrowedClassName for ModelName {} with fieldType '{}'", property.getModelName(),
                            fieldType);
                    return codeModel.ref(fieldType).narrow(codeModel.ref(narrowedClassName));
                }
            } else {
                return codeModel.ref(fieldType);
            }
        }
        return null;
    }

    /**
     * Adds Javadoc to the method based on the information in the property and the generation config options.
     *
     * @param method ..
     * @param property ..
     */
    private void addJavadocToMethod(JMethod method, Property property) {
        JType getterMethodReturnType = getGetterMethodReturnType(property);
        if (getterMethodReturnType == null) {
            return;
        }
        String javadocStr = null;
        if (StringUtils.isNotBlank(property.getJavadoc())) {
            javadocStr = property.getJavadoc();
        } else if (generationConfig.getOptions() != null && generationConfig.getOptions().isHasGenericJavadoc()) {
            javadocStr = "Get the " + property.getField() + ".";
        }
        if (StringUtils.isNotBlank(javadocStr)) {
            JDocComment javadoc = method.javadoc();
            javadoc.append(javadocStr).append("");
            javadoc.append("\n\n@return " + StringEscapeUtils.escapeHtml4(getterMethodReturnType.name()));
        }
    }
}
