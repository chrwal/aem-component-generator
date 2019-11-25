package com.adobe.aem.compgenerator.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum CqAction {

    EDIT("edit"), DELETE("delete");
    private final String value;
    private final static Map<String, CqAction> CONSTANTS = new HashMap<String, CqAction>();

    static {
        for (CqAction c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private CqAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static CqAction fromValue(String value) {
        CqAction constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
