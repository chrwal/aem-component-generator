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

import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.models.Property;
import com.sun.codemodel.JCodeModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class JavaCodeBuilder {

    protected final JCodeModel codeModel;
    protected final GenerationConfig generationConfig;
    protected final List<Property> globalProperties;
    protected final List<Property> sharedProperties;
    protected final List<Property> privateProperties;

    protected JavaCodeBuilder(JCodeModel codeModel, GenerationConfig generationConfig) {
        this.codeModel = codeModel;
        this.generationConfig = generationConfig;

        Set<Property> occurredProperties = new HashSet<>();

        this.globalProperties = filterProperties(occurredProperties, generationConfig.getOptions().getGlobalProperties(), Property.PropertyType.GLOBAL);
        occurredProperties.addAll(this.globalProperties);

        this.sharedProperties = filterProperties(occurredProperties, generationConfig.getOptions().getSharedProperties(), Property.PropertyType.SHARED);
        occurredProperties.addAll(this.sharedProperties);

        this.privateProperties = filterProperties(occurredProperties, generationConfig.getOptions().getProperties(), Property.PropertyType.PRIVATE);
    }

    /**
     * Filters the given properties for invalid fields and returns all that are not contained in occurredProperties.
     *
     * @param occurredProperties
     * @param originalProperties
     * @return filtered properties
     */
    private static List<Property> filterProperties(Set<Property> occurredProperties, List<Property> originalProperties, Property.PropertyType propertyType) {
        List<Property> properties;
        if (originalProperties != null) {
            properties = originalProperties.stream().filter(Objects::nonNull)
                    .filter(property -> StringUtils.isNotBlank(property.getField()))
                    .filter(property -> !property.getTypeAsFieldType().equals(Property.FieldType.EMPTY))
                    .filter(property -> !(occurredProperties.contains(property)))
                    .collect(Collectors.toList());
        } else {
            properties = Collections.emptyList();
        }
        for (Property property : properties) {
            property.setPropertyType(propertyType);
        }
        return properties;
    }
}
