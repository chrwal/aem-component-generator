
package com.adobe.aem.compgenerator.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cq:isContainer",
    "cq:actions",
    "cq:listeners"
})
public class CqEditConfig {

    @JsonProperty("cq:isContainer")
    private Boolean cqIsContainer;
    @JsonProperty("cq:actions")
    private List<CqAction> cqActions = new ArrayList<CqAction>();
    @JsonProperty("cq:listeners")
    private CqListeners cqListeners;

    @JsonProperty("cq:isContainer")
    public Boolean getCqIsContainer() {
        return cqIsContainer;
    }

    @JsonProperty("cq:isContainer")
    public void setCqIsContainer(Boolean cqIsContainer) {
        this.cqIsContainer = cqIsContainer;
    }

    @JsonProperty("cq:actions")
    public List<CqAction> getCqActions() {
        return cqActions;
    }

    @JsonProperty("cq:actions")
    public void setCqActions(List<CqAction> cqActions) {
        this.cqActions = cqActions;
    }

    @JsonProperty("cq:listeners")
    public CqListeners getCqListeners() {
        return cqListeners;
    }

    @JsonProperty("cq:listeners")
    public void setCqListeners(CqListeners cqListeners) {
        this.cqListeners = cqListeners;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CqEditConfig.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("cqIsContainer");
        sb.append('=');
        sb.append(((this.cqIsContainer == null)?"<null>":this.cqIsContainer));
        sb.append(',');
        sb.append("cqActions");
        sb.append('=');
        sb.append(((this.cqActions == null)?"<null>":this.cqActions));
        sb.append(',');
        sb.append("cqListeners");
        sb.append('=');
        sb.append(((this.cqListeners == null)?"<null>":this.cqListeners));
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
        result = ((result* 31)+((this.cqActions == null)? 0 :this.cqActions.hashCode()));
        result = ((result* 31)+((this.cqListeners == null)? 0 :this.cqListeners.hashCode()));
        result = ((result* 31)+((this.cqIsContainer == null)? 0 :this.cqIsContainer.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CqEditConfig) == false) {
            return false;
        }
        CqEditConfig rhs = ((CqEditConfig) other);
        return ((((this.cqActions == rhs.cqActions)||((this.cqActions!= null)&&this.cqActions.equals(rhs.cqActions)))&&((this.cqListeners == rhs.cqListeners)||((this.cqListeners!= null)&&this.cqListeners.equals(rhs.cqListeners))))&&((this.cqIsContainer == rhs.cqIsContainer)||((this.cqIsContainer!= null)&&this.cqIsContainer.equals(rhs.cqIsContainer))));
    }

}
