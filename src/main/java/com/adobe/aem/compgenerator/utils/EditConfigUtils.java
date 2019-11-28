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
import com.adobe.aem.compgenerator.models.CqEditConfig;
import com.adobe.aem.compgenerator.models.GenerationConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Objects;

class EditConfigUtils {
    private static final Logger LOG = LogManager.getLogger(EditConfigUtils.class);

    static final String DIALOG_EDIT_CONFIG_NAME = "cq:editConfig";
    static final String DIALOG_EDIT_CONFIG_TYPE = "cq:EditConfig";
    static final String DIALOG_CHILD_EDIT_CONFIG_NAME = "cq:childEditConfig";
    static final String DIALOG_CHILD_EDIT_CONFIG_TYPE = "cq:EditConfig";
    static final String DIALOG_EDIT_CONFIG_ACTIONS_NAME = "cq:actions";
    static final String DIALOG_EDIT_CONFIG_ISCONTAINER_NAME = "cq:isContainer";
    static final String DIALOG_EDIT_LISTENERS_CONFIG_NAME = "cq:listeners";
    static final String DIALOG_EDIT_LISTENER_CONFIG_TYPE = "cq:EditListenersConfig";

    /**
     * Creates dialog xml by adding the properties in data-config json file.
     *
     * @param cqEditConfig     The type of dialog to create (editConfig,childEidtConfig)
     * @param generationConfig The {@link GenerationConfig} object with all the populated values
     */
    static Document createEditConfigXml(GenerationConfig generationConfig, CqEditConfig cqEditConfig,
            String editConfigName,
            String editConfigType) {
        Document doc = null;
        if (cqEditConfig != null) {
            String dialogPath =
                    generationConfig.getCompDir() + "/" + StringUtils.replace(editConfigName, "cq:", "_cq_");
            try {
                CommonUtils.createFolder(dialogPath);
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element rootElement = createCqEditorRoot(doc, generationConfig, cqEditConfig, editConfigType);

                doc.appendChild(rootElement);
                XMLUtils.transformDomToFile(doc, dialogPath + "/" + Constants.FILENAME_CONTENT_XML);
            } catch (Exception e) {
                throw new GeneratorException("Exception while creating Dialog xml : " + dialogPath, e);
            }
        }
        return doc;
    }

    /**
     * Generates the root elements of what will be the _cq_dialog/.content.xml.
     *
     * @param document         The {@link Document} object
     * @param generationConfig The {@link GenerationConfig} object with all the populated values
     * @param cqEditConfig     ..
     * @param editConfigType   The type of CqEditor to create (regular, shared or global)
     * @return Element
     */
    static Element createCqEditorRoot(Document document, GenerationConfig generationConfig, CqEditConfig cqEditConfig,
            String editConfigType) {
        Element rootElement = XMLUtils.createRootElement(document, generationConfig);
        setAttributeIfNotNull(rootElement, Constants.JCR_PRIMARY_TYPE, editConfigType);
        setAttributeIfNotNull(rootElement, DIALOG_EDIT_CONFIG_ISCONTAINER_NAME, cqEditConfig.getCqIsContainer());
        extractAction(rootElement, cqEditConfig);
        addListenerNodeStructure(document, rootElement, cqEditConfig);
        return rootElement;
    }

    private static void extractAction(Element rootElement, CqEditConfig cqEditorConfig) {
        if (cqEditorConfig.getCqActions() != null) {
            final String jcrXmlAction = createJcrXmlArrayString(cqEditorConfig.getCqActions());
            rootElement.setAttribute(DIALOG_EDIT_CONFIG_ACTIONS_NAME, jcrXmlAction);
            LOG.info("actions {}", jcrXmlAction);
        }
    }

    /**
     * Builds default node structure of listener xml in the document passed in based on cqEditConfig.
     *
     * @param doc          The {@link Document} object
     * @param rootElement  The root node to append children nodes to
     * @param cqEditConfig ..
     */
    private static void addListenerNodeStructure(Document doc, Element rootElement, CqEditConfig cqEditConfig) {
        if (cqEditConfig.getCqListeners() != null) {
            Element listenerElement = doc.createElement(DIALOG_EDIT_LISTENERS_CONFIG_NAME);
            listenerElement.setAttribute(Constants.JCR_PRIMARY_TYPE, DIALOG_EDIT_LISTENER_CONFIG_TYPE);
            setAttributeIfNotNull(listenerElement, "afterchildinsert",
                    cqEditConfig.getCqListeners().getAfterchildinsert());
            setAttributeIfNotNull(listenerElement, "aftercopy", cqEditConfig.getCqListeners().getAftercopy());
            setAttributeIfNotNull(listenerElement, "afterdelete", cqEditConfig.getCqListeners().getAfterdelete());
            setAttributeIfNotNull(listenerElement, "afteredit", cqEditConfig.getCqListeners().getAfteredit());
            setAttributeIfNotNull(listenerElement, "afterinsert", cqEditConfig.getCqListeners().getAfterinsert());
            setAttributeIfNotNull(listenerElement, "aftermove", cqEditConfig.getCqListeners().getAftermove());
            setAttributeIfNotNull(listenerElement, "beforechildinsert",
                    cqEditConfig.getCqListeners().getBeforechildinsert());
            setAttributeIfNotNull(listenerElement, "beforecopy", cqEditConfig.getCqListeners().getBeforecopy());
            setAttributeIfNotNull(listenerElement, "beforedelete", cqEditConfig.getCqListeners().getBeforedelete());
            setAttributeIfNotNull(listenerElement, "beforeedit", cqEditConfig.getCqListeners().getBeforeedit());
            setAttributeIfNotNull(listenerElement, "beforeinsert", cqEditConfig.getCqListeners().getBeforeinsert());
            setAttributeIfNotNull(listenerElement, "beforemove", cqEditConfig.getCqListeners().getBeforemove());

            rootElement.appendChild(listenerElement);
        }
    }

    private static void setAttributeIfNotNull(Element listenerElement, String attributeName, Object attributeValue) {
        if (attributeValue != null) {
            listenerElement.setAttribute(attributeName, Objects.toString(attributeValue));
        }
    }

    /**
     * @return "[a,b]" array format.
     */
    private static <T> String createJcrXmlArrayString(Iterable<T> iter) {
        StringBuilder sb = new StringBuilder();
        if (iter != null) {
            sb.append("[");
            boolean first = true;
            for (Object item : iter) {
                sb.append(first ? item.toString() : "," + item.toString());
                first = false;
            }
            sb.append("]");
        }
        return sb.toString();
    }

}
