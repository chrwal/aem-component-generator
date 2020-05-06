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
package com.adobe.aem.compgenerator.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.util.List;
import java.util.Map;

public class Property implements BaseModel {
    public enum PropertyType {GLOBAL, SHARED, PRIVATE}

    public enum FieldType {
        BUTTONGROUP("buttongroup", true, false, null),
        TEXTFIELD("textfield", true, false, "java.lang.String"),
        NUMBERFIELD("numberfield", true, false, "java.lang.Long"),
        CHECKBOX("checkbox", true, false, "java.lang.Boolean"),
        CONTAINER("container", false, true, null),
        PATHFIELD("pathfield", true, false, "java.lang.String"),
        TEXTAREA("textarea", true, false, "java.lang.String"),
        HIDDEN("hidden", true, true, "java.lang.String"),
        DATEPICKER("datepicker", true, false, "java.util.Calendar"),
        SELECT("select", true, false, "java.lang.String"),
        SWITCH("switch", true, false, "java.lang.Boolean"),
        RADIOGROUP("radiogroup", true, false, "java.lang.String"),
        RADIO("radio", false, false, null),
        IMAGE("image", false, false, "com.adobe.cq.wcm.core.components.models.Image"),
        MULTIFIELD("multifield", false, false, "java.util.List"),
        HIDDEN_MULTIFIELD("hidden-multifield", false, true, "java.util.List"),
        UNKNOWN("unknown", true, false, null),
        EMPTY("", false, false, null);

        private final String fieldType;
        private final boolean createNameAndLockable;
        private final boolean handleItemsAsJcrProperties;
        private final String defaultModelType;

        FieldType(String fieldType, boolean createNameAndLockable, boolean handleItemsAsJcrProperties, String defaultModelType) {
            this.fieldType = fieldType;
            this.createNameAndLockable = createNameAndLockable;
            this.handleItemsAsJcrProperties = handleItemsAsJcrProperties;
            this.defaultModelType = defaultModelType;
        }

        public static FieldType valueForType(String type) {
            if (StringUtils.isBlank(type)) {
                return EMPTY;
            }
            for (FieldType value : values()) {
                if (value.getFieldType().equals(type)) {
                    return value;
                }
            }
            return UNKNOWN;
        }

        public String getFieldType() {
            return fieldType;
        }

        public String toString() {
            return fieldType;
        }

        public boolean isCreateNameAndLockable() {
            return createNameAndLockable;
        }

        public boolean isHandleItemsAsJcrProperties() {
            return handleItemsAsJcrProperties;
        }

        public String getDefaultModelType() {
            return defaultModelType;
        }
    }

    private Property.PropertyType propertyType;
    private boolean isChildResource = false;
    @JsonProperty("field")
    private String field;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private FieldType type;
    private String typeOriginal;
    @JsonProperty("label")
    private String label;
    @JsonProperty("description")
    private String description;
    @JsonProperty("javadoc")
    private String javadoc;
    @JsonProperty("json-property")
    private String jsonProperty;
    @JsonProperty("json-expose")
    private boolean shouldExporterExpose;
    @JsonProperty("attributes")
    private Map<String, String> attributes;
    @JsonProperty(value = "items")
    private List<Property> items;
    @JsonProperty("granite:data")
    private Map<String, String> graniteDate;
    @JsonProperty("datasource")
    private Map<String, String> dataSource;

    @JsonProperty(value = "model-name")
    private String modelName;
    @JsonProperty(value = "use-existing-model", defaultValue = "false")
    private boolean useExistingModel;
    @JsonProperty(value = "use-existing-field", defaultValue = "false")
    private boolean useExistingField;

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public boolean isChildResource() {
        return isChildResource;
    }

    public void setChildResource(boolean childResource) {
        isChildResource = childResource;
    }

    public String getField() {
        if (StringUtils.isNotBlank(field)) {
            return field;
        } else if (StringUtils.isNoneBlank(label)) {
            return CaseUtils.toCamelCase(label.replaceAll("[^A-Za-z0-9+]", " "),
                    false);
        }
        return field;
    }

    public String getFieldGetterName() {
        if (StringUtils.isNotBlank(field)) {
            return StringUtils.capitalize(field);
        } else if (StringUtils.isNoneBlank(label)) {
            return CaseUtils.toCamelCase(label.replaceAll("[^A-Za-z0-9+]", " "),
                    true);
        }
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        if (StringUtils.isBlank(name)) {
            return getField();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getTypeAsFieldType() {
        return type;
    }

    public void setType(String type) {
        this.type = FieldType.valueForType(type);
        typeOriginal = type;
    }

    public String getTypeOriginal() {
        return typeOriginal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    public List<Property> getItems() {
        return items;
    }

    public void setItems(List<Property> items) {
        this.items = items;
    }

    public Map<String, String> getGraniteDate() {
        return graniteDate;
    }

    public void setGraniteDate(Map<String, String> graniteDate) {
        this.graniteDate = graniteDate;
    }

    public Map<String, String> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public boolean getUseExistingModel() {
        return useExistingModel;
    }

    public void setUseExistingModel(boolean useExistingModel) {
        this.useExistingModel = useExistingModel;
    }

    public boolean getUseExistingField() {
        return useExistingField;
    }

    public void setUseExistingField(boolean useExistingField) {
        this.useExistingField = useExistingField;
    }

    public boolean isShouldExporterExpose() {
        return shouldExporterExpose;
    }

    public void setShouldExporterExpose(boolean shouldExporterExpose) {
        this.shouldExporterExpose = shouldExporterExpose;
    }

    public String getJsonProperty() {
        return jsonProperty;
    }

    public void setJsonProperty(String jsonProperty) {
        this.jsonProperty = jsonProperty;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Property)) {
            return false;
        }

        Property property = (Property) obj;
        return property.getField().equals(this.getField()) && property.getTypeOriginal().equals(this.getTypeOriginal());
    }

    @Override
    public int hashCode() {
        return getField().hashCode() + getTypeOriginal().hashCode();
    }
}
