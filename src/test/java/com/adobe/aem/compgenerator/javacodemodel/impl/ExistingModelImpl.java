/*
 * ***********************************************************************
 * NewCo Incorporated CONFIDENTIAL
 * ___________________
 *
 * Copyright 2019 NewCo Incorporated.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property
 * of NewCo Incorporated and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to NewCo Incorporated
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from NewCo Incorporated.
 * ***********************************************************************
 */

package com.adobe.aem.compgenerator.javacodemodel.impl;

import com.adobe.aem.compgenerator.javacodemodel.ExistingModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Collections;
import java.util.List;

@Model(adaptables = {
        SlingHttpServletRequest.class
}, adapters = ExistingModel.class)
public class ExistingModelImpl
        implements ExistingModel {

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "./jcrContainerColors1Item")
    private List<String> jcrContainerColors1Item;

    @Override
    public List<String> getJcrContainerColors1Item() {
        return jcrContainerColors1Item != null ? Collections.unmodifiableList(jcrContainerColors1Item) : null;
    }

}
