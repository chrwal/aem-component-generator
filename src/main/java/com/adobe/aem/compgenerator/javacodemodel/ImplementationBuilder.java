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

import com.adobe.acs.commons.models.injectors.annotation.SharedValueMapValue;
import com.adobe.aem.compgenerator.Constants;
import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.models.Property;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sun.codemodel.JMod.PRIVATE;

public class ImplementationBuilder extends JavaCodeBuilder {
    private static final Logger LOG = LogManager.getLogger(ImplementationBuilder.class);
    private static final String INJECTION_NAME = "name";
    private static final String INJECTION_STRATEGY = "injectionStrategy";
    private static final String OPTIONAL_INJECTION_STRATEGY = "OPTIONAL";
    private static final String SLING_MODEL_EXPORTER_NAME = "SLING_MODEL_EXPORTER_NAME";
    private static final String SLING_MODEL_EXTENSION = "SLING_MODEL_EXTENSION";

    private final String className;
    private final JClass interfaceClass;
    private final JPackage implPackage;
    private final String[] adaptables;
    private final boolean isAllowExporting;
    private final boolean isAllowExportingExportedType;

    private Map<String, Boolean> fieldJsonExposeMap = new HashMap<>();
    private Map<String, String> fieldJsonPropertyMap = new HashMap<>();

    /**
     * Construct a new Sling Model implementation class.
     *
     * @param codeModel        ..
     * @param generationConfig ..
     * @param className        ..
     * @param interfaceClass   ..
     */
    ImplementationBuilder(JCodeModel codeModel, GenerationConfig generationConfig, String className,
                          JClass interfaceClass) {
        super(codeModel, generationConfig);
        this.className = className;
        this.interfaceClass = interfaceClass;
        this.implPackage = codeModel._package(generationConfig.getProjectSettings().getModelImplPackage());
        this.adaptables = generationConfig.getOptions().getModelAdaptables();
        this.isAllowExporting = generationConfig.getOptions().isAllowExporting();
        this.isAllowExportingExportedType = generationConfig.getOptions().isAllowExportingTypeField();
    }

    public void build(String resourceType) throws JClassAlreadyExistsException {
        JDefinedClass jc = this.implPackage._class(this.className)._implements(this.interfaceClass);
        addSlingAnnotations(jc, this.interfaceClass, resourceType);

        addFieldVars(jc, globalProperties, false);
        addFieldVars(jc, sharedProperties, false);
        addFieldVars(jc, privateProperties, false);

        addGetters(jc);
        addExportedTypeMethod(jc);
    }

    private void addSlingAnnotations(JDefinedClass jDefinedClass, JClass adapterClass, String resourceType) {
        JAnnotationUse jAUse = jDefinedClass.annotate(codeModel.ref(Model.class));
        JAnnotationArrayMember adaptablesArray = jAUse.paramArray("adaptables");
        for (String adaptable : adaptables) {
            if ("resource".equalsIgnoreCase(adaptable)) {
                adaptablesArray.param(codeModel.ref(Resource.class));
            }
            if ("request".equalsIgnoreCase(adaptable)) {
                adaptablesArray.param(codeModel.ref(SlingHttpServletRequest.class));
            }
        }
        if (this.isAllowExporting) {
            jAUse.paramArray("adapters").param(adapterClass).param(codeModel.ref(ComponentExporter.class));
        } else {
            jAUse.param("adapters", adapterClass);
        }
        if (StringUtils.isNotBlank(resourceType)) {
            jAUse.param("resourceType", resourceType);
        }
        if (this.isAllowExporting) {
            jAUse = jDefinedClass.annotate(codeModel.ref(Exporter.class));
            jAUse.param("name", codeModel.ref(ExporterConstants.class).staticRef(SLING_MODEL_EXPORTER_NAME));
            jAUse.param("extensions", codeModel.ref(ExporterConstants.class).staticRef(SLING_MODEL_EXTENSION));
        }
    }

    /**
     * adds fields to java model.
     *
     * @param properties            ..
     * @param handleAsChildProperty
     */
    private void addFieldVars(JDefinedClass jc, List<Property> properties, boolean handleAsChildProperty) {
        for (Property property : properties) {
            if (property != null && StringUtils.isNotBlank(property.getField())) {
                property.setChildResource(true);
                addFieldVar(jc, property);
            }
        }
    }

    /**
     * add field variable to to jc.
     *
     * @param property ..
     */
    private void addFieldVar(JDefinedClass jc, Property property) {
        LOG.debug("addFieldVar for property.getField [{}]", property.getField());
        boolean propertyIsMultifield = property.getTypeAsFieldType().equals(Property.FieldType.MULTIFIELD);
        boolean propertyHasSeveralItems = property.getItems() != null && property.getItems().size() > 1;
        boolean propertyIsHiddenMultifield = property.getTypeAsFieldType().equals(Property.FieldType.HIDDEN_MULTIFIELD);
        boolean containerTypeWithModelName = property.getTypeAsFieldType().equals(Property.FieldType.CONTAINER) &&
                StringUtils.isNotBlank(property.getModelName());

        if ((propertyIsMultifield && propertyHasSeveralItems) || propertyIsHiddenMultifield || containerTypeWithModelName) {
            addPropertyAsChildResource(jc, property);
        } else if (property.getTypeAsFieldType().equals(Property.FieldType.CONTAINER)) {
            addFieldVars(jc, property.getItems(), true);
        } else {
            addPropertyAsValueMap(jc, property);
        }
    }

    /**
     * method that add the fieldname as private to jc.
     *
     * @param property ..
     */
    private void addPropertyAsValueMap(JDefinedClass jc, Property property) {
        LOG.debug("addPropertyAsValueMap for property.getField [{}]", property.getField());
        String fieldType = JavaCodeModel.getFieldType(property);
        if (property.getUseExistingField() || fieldType == null) {
            return;
        }
        JClass fieldClass = property.getTypeAsFieldType().equals(Property.FieldType.MULTIFIELD) ?
                codeModel.ref(fieldType)
                        .narrow(codeModel.ref(JavaCodeModel.getFieldType(property.getItems().get(0)))) :
                codeModel.ref(fieldType);
        JFieldVar jFieldVar = jc.field(PRIVATE, fieldClass, property.getField());
        JAnnotationUse param;
        if (property.getTypeAsFieldType().equals(Property.FieldType.IMAGE)) {
            param = jFieldVar.annotate(codeModel.ref(ChildResource.class)).param(INJECTION_STRATEGY,
                    codeModel.ref(InjectionStrategy.class).staticRef(OPTIONAL_INJECTION_STRATEGY));

        } else if (Property.PropertyType.PRIVATE.equals(property.getPropertyType()) || property.isChildResource()) {
            //Current implementation does not support child resources other than private
            param = jFieldVar.annotate(codeModel.ref(ValueMapValue.class)).param(INJECTION_STRATEGY,
                    codeModel.ref(InjectionStrategy.class).staticRef(OPTIONAL_INJECTION_STRATEGY));
        } else {
            param = jFieldVar.annotate(codeModel.ref(SharedValueMapValue.class)).param(INJECTION_STRATEGY,
                    codeModel.ref(InjectionStrategy.class).staticRef(OPTIONAL_INJECTION_STRATEGY));
        }
        String annotationFieldName = getAnnotationFieldName(generationConfig, property);
        param.param(INJECTION_NAME, annotationFieldName);

        setupFieldGetterAnnotations(jFieldVar, property);
    }

    /**
     * method that add the fieldname as private and adds a class to jc
     */
    private void addPropertyAsChildResource(JDefinedClass jc, Property property) {
        String modelClassName = JavaCodeModel.getMultifieldInterfaceName(property);

        // Create the multifield item
        if (!property.getUseExistingModel()) {
            buildChildImplementation(property.getItems(), modelClassName);
        }

        String fieldType = JavaCodeModel.getFieldType(property);
        if (fieldType != null) {
            String absolutModelClassName = JavaCodeModel
                    .getFullyQualifiedModelClassName(generationConfig.getProjectSettings(), modelClassName);
            LOG.debug("Use UseExistingModel [{}] for modelClassName [{}] and fieldType [{}] absolutModelClassName [{}]",
                    property.getUseExistingModel(), modelClassName, fieldType, absolutModelClassName);
            JClass fieldClass;
            if (property.getTypeAsFieldType().equals(Property.FieldType.CONTAINER)) {
                fieldClass = codeModel.ref(fieldType);
            } else {
                JClass narrowedClass = codeModel.ref(absolutModelClassName);
                fieldClass = codeModel.ref(fieldType).narrow(narrowedClass);
            }
            JFieldVar jFieldVar = jc.field(PRIVATE, fieldClass, property.getField());
            jFieldVar.annotate(codeModel.ref(ChildResource.class)).param(INJECTION_STRATEGY,
                    codeModel.ref(InjectionStrategy.class).staticRef(OPTIONAL_INJECTION_STRATEGY)).
                    param(INJECTION_NAME, getAnnotationFieldName(generationConfig, property));
            setupFieldGetterAnnotations(jFieldVar, property);
        }
    }

    /**
     * adds getters to all the fields available in the java class.
     *
     * @param jc ..
     */
    private void addGetters(JDefinedClass jc) {
        Map<String, JFieldVar> fieldVars = jc.fields();
        if (!fieldVars.isEmpty()) {
            for (Map.Entry<String, JFieldVar> entry : fieldVars.entrySet()) {
                if (entry.getValue() != null) {
                    addGetter(jc, entry.getValue());
                }
            }
        }
    }

    /**
     * add getter method for jFieldVar passed in.
     *
     * @param jc        ..
     * @param jFieldVar ..
     */
    private void addGetter(JDefinedClass jc, JFieldVar jFieldVar) {
        JMethod getMethod = jc.method(JMod.PUBLIC, jFieldVar.type(), getMethodFormattedString(jFieldVar.name()));
        getMethod.annotate(codeModel.ref(Override.class));

        if (this.isAllowExporting) {
            if (!this.fieldJsonExposeMap.get(jFieldVar.name())) {
                getMethod.annotate(codeModel.ref(JsonIgnore.class));
            }

            if (StringUtils.isNotBlank(this.fieldJsonPropertyMap.get(jFieldVar.name()))) {
                getMethod.annotate(codeModel.ref(JsonProperty.class))
                        .param("value", this.fieldJsonPropertyMap.get(jFieldVar.name()));
            }
        }


        if (jFieldVar.type().erasure().fullName().equals(List.class.getName())) {
            JExpression condition = new IsNullExpression(jFieldVar, false);
            JExpression ifTrue = codeModel.ref(Collections.class).staticInvoke("unmodifiableList").arg(jFieldVar);
            JExpression ifFalse = JExpr._null();
            getMethod.body()._return(new TernaryOperator(condition, ifTrue, ifFalse));
        } else {
            getMethod.body()._return(jFieldVar);
        }
    }

    /**
     * builds method name out of field variable.
     *
     * @param fieldVariable ..
     * @return String returns formatted getter method name.
     */
    private String getMethodFormattedString(String fieldVariable) {
        if (StringUtils.isNotBlank(fieldVariable) && StringUtils.length(fieldVariable) > 0) {
            return Constants.STRING_GET + Character.toTitleCase(fieldVariable.charAt(0)) + fieldVariable.substring(1);
        }
        return fieldVariable;
    }

    private void buildChildImplementation(List<Property> properties, String modelClassName) {
        if (properties != null && !properties.isEmpty()) {
            try {
                String absolutModelClassName = JavaCodeModel
                        .getFullyQualifiedModelClassName(generationConfig.getProjectSettings(), modelClassName);
                JClass childInterfaceClass = codeModel.ref(absolutModelClassName);
                JDefinedClass implClass =
                        this.implPackage._class(StringUtils.substringAfterLast(absolutModelClassName, ".") + "Impl")
                                ._implements(childInterfaceClass);
                addSlingAnnotations(implClass, childInterfaceClass, null);
                // Child properties are marked as ChildResource and can be handled properly later
                addFieldVars(implClass, properties, false);
                addGetters(implClass);
                addExportedTypeMethod(implClass);
            } catch (JClassAlreadyExistsException ex) {
                LOG.error("Failed to generate child implementation classes.", ex);
            }
        }
    }

    private void setupFieldGetterAnnotations(JFieldVar jFieldVar, Property property) {
        boolean isFieldJsonExpose = false;
        String fieldJsonPropertyValue = "";

        if (this.isAllowExporting) {
            isFieldJsonExpose = property.isShouldExporterExpose();
            fieldJsonPropertyValue = property.getJsonProperty();
        }

        this.fieldJsonExposeMap.put(jFieldVar.name(), isFieldJsonExpose);
        this.fieldJsonPropertyMap.put(jFieldVar.name(), fieldJsonPropertyValue);
    }

    private void addExportedTypeMethod(JDefinedClass jc) {
        if (this.isAllowExporting) {
            JFieldVar jFieldVar = jc.field(PRIVATE, codeModel.ref(Resource.class), "resource");
            jFieldVar.annotate(codeModel.ref(SlingObject.class));
            JMethod method = jc.method(JMod.PUBLIC, codeModel.ref(String.class), "getExportedType");
            method.annotate(codeModel.ref(Override.class));
            if (this.isAllowExportingExportedType) {
                method.body()._return(jFieldVar.invoke("getResourceType"));
            } else {
                method.body()._return(JExpr._null());
            }
        }
    }

    private static String getAnnotationFieldName(GenerationConfig generationConfig, Property property) {
        String nameForField = property.getName();
        if (nameForField != null) {
            String groupFieldsByName = StringUtils.upperCase(generationConfig.getOptions().isGroupFieldsByName());
            if (BooleanUtils.toBoolean(groupFieldsByName)) {
                nameForField = "./" + generationConfig.getName() + "/" + nameForField;
            } else {
                nameForField = "./" + nameForField;

            }
        }
        return nameForField;
    }

}
