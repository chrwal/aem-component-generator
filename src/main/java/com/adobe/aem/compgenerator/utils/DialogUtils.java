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
package com.adobe.aem.compgenerator.utils;

import com.adobe.aem.compgenerator.Constants;
import com.adobe.aem.compgenerator.exceptions.GeneratorException;
import com.adobe.aem.compgenerator.models.GenerationConfig;
import com.adobe.aem.compgenerator.models.Property;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

public class DialogUtils {
    private static final Logger LOG = LogManager.getLogger(DialogUtils.class);

    /**
     * Creates dialog xml by adding the properties in data-config json file.
     *
     * @param generationConfig The {@link GenerationConfig} object with all the populated values
     * @param dialogType The type of dialog to create (regular, shared or global)
     */
    public static void createDialogXml(final GenerationConfig generationConfig, final String dialogType) {
        String dialogPath = generationConfig.getCompDir() + "/" + dialogType;
        try {
            CommonUtils.createFolder(dialogPath);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = createDialogRoot(doc, generationConfig, dialogType);
            List<Property> properties = generationConfig.getOptions().getProperties();

            if (dialogType.equalsIgnoreCase(Constants.DIALOG_TYPE_GLOBAL)) {
                properties = generationConfig.getOptions().getGlobalProperties();
            } else if (dialogType.equalsIgnoreCase(Constants.DIALOG_TYPE_SHARED)) {
                properties = generationConfig.getOptions().getSharedProperties();
            }
            Node rootNode = updateDefaultNodeStructure(doc, rootElement, "content");
            handleProperties(doc, rootNode, generationConfig, properties, true);
            doc.appendChild(rootElement);
            XMLUtils.transformDomToFile(doc, dialogPath + "/" + Constants.FILENAME_CONTENT_XML);
        } catch (Exception e) {
            throw new GeneratorException("Exception while creating Dialog xml : " + dialogPath, e);
        }
    }

    private static void handleProperties(Document document, Node rootNode, GenerationConfig generationConfig,
            List<Property> properties,
            boolean rederItemsNode) {
        if (properties != null && properties.size() > 0) {
            LOG.debug("handleProperties for element [{}] ", rootNode.getNodeName());
            Node subNode = rootNode;
            if (rederItemsNode) {
                subNode = rootNode.appendChild(createUnStructuredNode(document, "items"));
            }
            for (Property property : properties) {
                if (property != null) {
                    Element a = createPropertyNode(document, subNode, generationConfig, property);
                    if (a != null) {
                        subNode.appendChild(a);
                    }
                }
            }
        }
    }

    /**
     * Generates the root elements of what will be the _cq_dialog/.content.xml.
     *
     * @param document The {@link Document} object
     * @param generationConfig The {@link GenerationConfig} object with all the populated values
     * @param dialogType The type of dialog to create (regular, shared or global)
     * @return Element
     */
    protected static Element createDialogRoot(Document document, GenerationConfig generationConfig, String dialogType) {
        Element rootElement = XMLUtils.createRootElement(document, generationConfig);

        rootElement.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        rootElement.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_DIALOG);

        if (dialogType.equalsIgnoreCase(Constants.DIALOG_TYPE_GLOBAL)) {
            rootElement.setAttribute(Constants.PROPERTY_JCR_TITLE, generationConfig.getTitle() + " (Global Properties)");
        } else if (dialogType.equalsIgnoreCase(Constants.DIALOG_TYPE_SHARED)) {
            rootElement.setAttribute(Constants.PROPERTY_JCR_TITLE, generationConfig.getTitle() + " (Shared Properties)");
        } else if (dialogType.equalsIgnoreCase(Constants.DIALOG_TYPE_DESIGN_DIALOG)) {
            rootElement.setAttribute(Constants.PROPERTY_JCR_TITLE, generationConfig.getTitle() + " Design Dialog");
        } else {
            rootElement.setAttribute(Constants.PROPERTY_JCR_TITLE, generationConfig.getTitle());
        }

        return rootElement;
    }

    /**
     * Adds a dialog property xml node with all input attr under the document.
     *
     * @param document The {@link Document} object
     * @param generationConfig ..
     * @param property The {@link Property} object contains attributes
     * @return Element
     */
    private static Element createPropertyNode(Document document, Node currentNode, GenerationConfig generationConfig,
            Property property) {
        String propertyField = property.getField();
        LOG.debug("createPropertyNode for node [{}] and property [{}]", currentNode.getNodeName(), propertyField);
        Element propertyNode = document.createElement(propertyField);

        propertyNode.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        setSlingResourceType(propertyNode, property);

        // Some of the properties are optional based on the different types available.
        addBasicProperties(propertyNode, property);

        String nameForField = getPropertyFieldName(generationConfig, property);
        if (StringUtils.isNotEmpty(propertyField) && property.getTypeAsFieldType() != null &&
                (property.getTypeAsFieldType().isCreateNameAndLockable()) &&
                BooleanUtils.isNotTrue(property.isChildResource())) {
            LOG.debug("createNameAndLockable for field [{}] and type [{}]", propertyField,
                    property.getTypeAsFieldType());
            propertyNode.setAttribute(Constants.PROPERTY_NAME, nameForField);
            propertyNode.setAttribute(Constants.PROPERTY_CQ_MSM_LOCKABLE, nameForField);
        } else {
            LOG.debug("No createNameAndLockable for field [{}] and type [{}] isChild [{}]", nameForField,
                    property.getTypeAsFieldType(), property.isChildResource());
        }
        processAttributes(propertyNode, property);
        processGraniteData(document, propertyNode, property);

        if (property.getItems() != null && !property.getItems().isEmpty()) {
            if (Property.FieldType.MULTIFIELD == property.getTypeAsFieldType()) {
                handleMultifieldProperty(document, generationConfig, property, propertyNode);
            } else if (property.getTypeAsFieldType().isHandleItemsAsJcrProperties()) {
                handleProperties(document, propertyNode, generationConfig, property.getItems(), true);
            } else {
                Node items = propertyNode.appendChild(createUnStructuredNode(document, "items"));
                processItems(document, generationConfig, items, property);
            }
        }

        if (Property.FieldType.IMAGE.equals(property.getTypeAsFieldType())) {
            addImagePropertyValues(propertyNode, generationConfig, property);
            currentNode.appendChild(propertyNode);

            Element hiddenImageNode = document.createElement(propertyField + "ResType");
            addImageHiddenProperyValues(hiddenImageNode, property);
            return hiddenImageNode;
        }

        return propertyNode;
    }

    private static String getPropertyFieldName(GenerationConfig generationConfig, Property property) {
        String nameForField = property.getName();
        if (generationConfig.getOptions().isGroupFieldsByName()) {
            nameForField = "./" + generationConfig.getName() + "/" + nameForField;
        } else {
            nameForField = "./" + nameForField;
        }
        return nameForField;
    }

    private static void handleMultifieldProperty(Document document, GenerationConfig generationConfig,
            Property property, Element propertyNode) {
        Element field = document.createElement("field");
        String nameForField = getPropertyFieldName(generationConfig, property);
        field.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        field.setAttribute(Constants.PROPERTY_NAME, nameForField);
        field.setAttribute(Constants.PROPERTY_CQ_MSM_LOCKABLE, nameForField);
        field.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_FIELDSET);

        if (property.getItems().size() == 1) {
            Element layout = document.createElement("layout");
            layout.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
            layout.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_FIXEDCOLUMNS);
            layout.setAttribute("method", "absolute");
            field.appendChild(layout);

            Node items = field.appendChild(createUnStructuredNode(document, "items"));
            Element column = document.createElement("column");
            column.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
            column.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_CONTAINER);
            items.appendChild(column);

            items = column.appendChild(createUnStructuredNode(document, "items"));

            Element actualField = document.createElement("field");
            actualField.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
            actualField.setAttribute(Constants.PROPERTY_NAME, nameForField);
            actualField.setAttribute(Constants.PROPERTY_CQ_MSM_LOCKABLE, nameForField);

            Property prop = property.getItems().get(0);
            setSlingResourceType(actualField, prop);
            addBasicProperties(actualField, prop);
            processAttributes(actualField, prop);
            items.appendChild(actualField);
        } else {
            propertyNode.setAttribute(Constants.PROPERTY_COMPOSITE, "{Boolean}true");
            Node items = field.appendChild(createUnStructuredNode(document, "items"));
            processItems(document, generationConfig, items, property);
        }

        propertyNode.appendChild(field);
    }

    /**
     * Processes the attributes for a propertyNode.
     *
     * @param propertyNode The node to add property attributes
     * @param property The {@link Property} object contains attributes
     */
    private static void processAttributes(Element propertyNode, Property property) {
        if (property.getAttributes() != null && property.getAttributes().size() > 0) {
            property.getAttributes().entrySet().stream()
                    .forEach(entry -> propertyNode.setAttribute(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Processes the graniteData for a propertyNode.
     *
     * @param document     ..
     * @param property     The {@link Property} object contains attributes
     * @param propertyNode The node to add property attributes
     */
    private static void processGraniteData(Document document, Element propertyNode, Property property) {
        if (property.getGraniteDate() != null && property.getGraniteDate().size() > 0) {
            try {
                Element graniteData = createUnStructuredNode(document, "granite:data");
                property.getGraniteDate().entrySet().stream()
                        .forEach(entry -> graniteData.setAttribute(entry.getKey(), entry.getValue()));
                propertyNode.appendChild(graniteData);
            } catch (DOMException e) {
                throw new GeneratorException("Exception while process granite:data for Dialog xml : " + property.getField(), e);
            }
        }
    }

    /**
     * Process the dialog node item by setting property attributes on it.
     *  @param document The {@link Document} object
     * @param generationConfig
     * @param itemsNode The {@link Node} object
     * @param property The {@link Property} object contains attributes
     */
    private static void processItems(Document document, GenerationConfig generationConfig, Node itemsNode,
            Property property) {
        if (property.getItems() == null) {
            LOG.debug("no property items available");
            return;
        }
        for (Property propertyItem : property.getItems()) {
            Element optionNode = document.createElement(propertyItem.getField());
            optionNode.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);

            addBasicProperties(optionNode, propertyItem);

            setSlingResourceType(optionNode, propertyItem);

            if (property.getTypeAsFieldType().equals(Property.FieldType.MULTIFIELD)) {
                String nameForField = getPropertyFieldName(generationConfig, property);
                optionNode.setAttribute(Constants.PROPERTY_NAME, nameForField);
            }

            processAttributes(optionNode, propertyItem);
            itemsNode.appendChild(optionNode);
        }
    }

    private static void setSlingResourceType(Element propertyNode, Property property) {
        String resourceType = getSlingResourceType(property);
        if (StringUtils.isNotEmpty(resourceType)) {
            propertyNode.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, resourceType);
        }
    }

    /**
     * Adds the field label and field description attributes to the node.
     *
     * @param propertyNode The node to add property attributes
     * @param property The {@link Property} object contains attributes
     */
    private static void addBasicProperties(Element propertyNode, Property property) {
        if (StringUtils.isNotEmpty(property.getLabel())) {
            propertyNode.setAttribute(Constants.PROPERTY_FIELDLABEL, property.getLabel());
        }
        if (StringUtils.isNotEmpty(property.getDescription())) {
            propertyNode.setAttribute(Constants.PROPERTY_FIELDDESC, property.getDescription());
        }
    }

    /**
     * Adds the properties specific to the image node. These could all have been
     * included as attributes in the configuration json file, but they never/rarely
     * change, so hardcoding them here seems safe to do.
     *  @param imageNode The {@link Node} object
     * @param generationConfig
     * @param property The {@link Property} object contains attributes
     */
    private static void addImagePropertyValues(Element imageNode, GenerationConfig generationConfig,
            Property property) {
        String nameForField = getPropertyFieldName(generationConfig, property);
        imageNode.setAttribute(Constants.PROPERTY_NAME, nameForField + "/file");
        imageNode.setAttribute(Constants.PROPERTY_CQ_MSM_LOCKABLE, nameForField + "/file");
        imageNode.setAttribute("allowUpload", "{Boolean}false");
        imageNode.setAttribute("autoStart", "{Boolean}false");
        imageNode.setAttribute("class", "cq-droptarget");
        imageNode.setAttribute("fileReferenceParameter", nameForField + "/fileReference");
        imageNode.setAttribute("mimeTypes", "[image/gif,image/jpeg,image/png,image/webp,image/tiff,image/svg+xml]");
        imageNode.setAttribute("multiple", "{Boolean}false");
        imageNode.setAttribute("title", "Drag to select image");
        imageNode.setAttribute("uploadUrl", "${suffix.path}");
        imageNode.setAttribute("useHTML5", "{Boolean}true");
    }

    /**
     * Adds the properties specific to the hidden image node that allows the image
     * dropzone to operate properly on dialogs.
     *
     * @param hiddenImageNode An {@link Element} object representing an image's hidden node
     * @param property The {@link Property} object contains attributes
     */
    private static void addImageHiddenProperyValues(Element hiddenImageNode, Property property) {
        hiddenImageNode.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        hiddenImageNode.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_HIDDEN);
        hiddenImageNode.setAttribute("name", "./" + property.getField() + "/" + Constants.PROPERTY_SLING_RESOURCETYPE);
        hiddenImageNode.setAttribute("value", Constants.RESOURCE_TYPE_IMAGE_HIDDEN_TYPE);
    }

    /**
     * Builds default node structure of dialog xml in the document passed in based on dialogType.
     *
     * @param document The {@link Document} object
     * @param root The root node to append children nodes to
     * @param content dialog content node
     * @return Node
     */
    private static Node updateDefaultNodeStructure(Document document, Element root, String content) {
        Element containerElement = document.createElement(content);
        containerElement.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        containerElement.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_CONTAINER);

        Element layoutElement1 = document.createElement("layout");
        layoutElement1.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        layoutElement1.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_FIXEDCOLUMNS);
        layoutElement1.setAttribute("margin", "{Boolean}false");

        Element columnElement = document.createElement("column");
        columnElement.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        columnElement.setAttribute(Constants.PROPERTY_SLING_RESOURCETYPE, Constants.RESOURCE_TYPE_CONTAINER);

        Node containerNode = root.appendChild(containerElement);

        containerNode.appendChild(layoutElement1);
        return containerNode.appendChild(createUnStructuredNode(document, "items")).appendChild(columnElement);
    }

    /**
     * Creates a node with the jcr:primaryType set to nt:unstructured.
     *
     * @param document The {@link Document} object
     * @param nodeName The name of the node being created
     * @return Node
     */
    protected static Element createUnStructuredNode(Document document, String nodeName) {
        Element element = document.createElement(nodeName);
        element.setAttribute(Constants.JCR_PRIMARY_TYPE, Constants.NT_UNSTRUCTURED);
        return element;
    }

    /**
     * Determine the proper sling:resourceType.
     *
     * @param property Property
     * @return String
     */
    private static String getSlingResourceType(Property property) {
        Property.FieldType type = property.getTypeAsFieldType();
        if (Property.FieldType.TEXTFIELD.equals(type)) {
            return Constants.RESOURCE_TYPE_TEXTFIELD;
        } else if (Property.FieldType.NUMBERFIELD.equals(type)) {
            return Constants.RESOURCE_TYPE_NUMBER;
        } else if (Property.FieldType.CHECKBOX.equals(type)) {
            return Constants.RESOURCE_TYPE_CHECKBOX;
        } else if (Property.FieldType.CONTAINER.equals(type)) {
            return Constants.RESOURCE_TYPE_CONTAINER;
        } else if (Property.FieldType.PATHFIELD.equals(type)) {
            return Constants.RESOURCE_TYPE_PATHFIELD;
        } else if (Property.FieldType.TEXTAREA.equals(type)) {
            return Constants.RESOURCE_TYPE_TEXTAREA;
        } else if (Property.FieldType.HIDDEN.equals(type)) {
            return Constants.RESOURCE_TYPE_HIDDEN;
        } else if (Property.FieldType.DATEPICKER.equals(type)) {
            return Constants.RESOURCE_TYPE_DATEPICKER;
        } else if (Property.FieldType.SELECT.equals(type)) {
            return Constants.RESOURCE_TYPE_SELECT;
        } else if (Property.FieldType.SWITCH.equals(type)) {
            return Constants.RESOURCE_TYPE_SWITCH;
        } else if (Property.FieldType.RADIOGROUP.equals(type)) {
            return Constants.RESOURCE_TYPE_RADIOGROUP;
        } else if (Property.FieldType.RADIO.equals(type)) {
            return Constants.RESOURCE_TYPE_RADIO;
        } else if (Property.FieldType.IMAGE.equals(type)) {
            return Constants.RESOURCE_TYPE_IMAGE;
        } else if (Property.FieldType.MULTIFIELD.equals(type)) {
            return Constants.RESOURCE_TYPE_MULTIFIELD;
        } else if (Property.FieldType.HIDDEN_MULTIFIELD.equals(type)) {
            return Constants.RESOURCE_TYPE_HIDDEN;
        } else if (Property.FieldType.UNKNOWN.equals(type)) {
            // Support for not defined types. Model name will be the sling model field type
            return property.getTypeOriginal();
        } else {
            return null;
        }
    }
}
