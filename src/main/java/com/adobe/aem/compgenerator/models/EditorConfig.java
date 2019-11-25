
package com.adobe.aem.compgenerator.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cq:editConfig",
    "cq:childEditConfig"
})
public class EditorConfig {

    @JsonProperty("cq:editConfig")
    private CqEditConfig cqEditConfig;
    @JsonProperty("cq:childEditConfig")
    private CqEditConfig cqChildEditConfig;

    @JsonProperty("cq:editConfig")
    public CqEditConfig getCqEditConfig() {
        return cqEditConfig;
    }

    @JsonProperty("cq:editConfig")
    public void setCqEditConfig(CqEditConfig cqEditConfig) {
        this.cqEditConfig = cqEditConfig;
    }

    @JsonProperty("cq:childEditConfig")
    public CqEditConfig getCqChildEditConfig() {
        return cqChildEditConfig;
    }

    @JsonProperty("cq:childEditConfig")
    public void setCqChildEditConfig(CqEditConfig cqChildEditConfig) {
        this.cqChildEditConfig = cqChildEditConfig;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(EditorConfig.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("cqEditConfig");
        sb.append('=');
        sb.append(((this.cqEditConfig == null)?"<null>":this.cqEditConfig));
        sb.append(',');
        sb.append("cqChildEditConfig");
        sb.append('=');
        sb.append(((this.cqChildEditConfig == null)?"<null>":this.cqChildEditConfig));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.cqEditConfig == null)? 0 :this.cqEditConfig.hashCode()));
        result = ((result* 31)+((this.cqChildEditConfig == null)? 0 :this.cqChildEditConfig.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof EditorConfig) == false) {
            return false;
        }
        EditorConfig rhs = ((EditorConfig) other);
        return (((this.cqEditConfig == rhs.cqEditConfig)||((this.cqEditConfig!= null)&&this.cqEditConfig.equals(rhs.cqEditConfig)))&&((this.cqChildEditConfig == rhs.cqChildEditConfig)||((this.cqChildEditConfig!= null)&&this.cqChildEditConfig.equals(rhs.cqChildEditConfig))));
    }

}
