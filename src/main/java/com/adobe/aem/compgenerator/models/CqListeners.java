package com.adobe.aem.compgenerator.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"afterchildinsert", "aftercopy", "afterdelete", "afteredit", "afterinsert", "aftermove",
        "beforechildinsert", "beforecopy", "beforedelete", "beforeedit", "beforeinsert", "beforemove"})
public class CqListeners {

    @JsonProperty("afterchildinsert")
    private CqListeners.CqListenersBasicEvents afterchildinsert;
    @JsonProperty("aftercopy")
    private CqListeners.CqListenersBasicEvents aftercopy;
    @JsonProperty("afterdelete")
    private CqListeners.CqListenersBasicEvents afterdelete;
    @JsonProperty("afteredit")
    private CqListeners.CqListenersBasicEvents afteredit;
    @JsonProperty("afterinsert")
    private CqListeners.CqListenersBasicEvents afterinsert;
    @JsonProperty("aftermove")
    private CqListeners.CqListenersBasicEvents aftermove;
    @JsonProperty("beforechildinsert")
    private CqListeners.CqListenersBasicEvents beforechildinsert;
    @JsonProperty("beforecopy")
    private CqListeners.CqListenersBasicEvents beforecopy;
    @JsonProperty("beforedelete")
    private CqListeners.CqListenersBasicEvents beforedelete;
    @JsonProperty("beforeedit")
    private CqListeners.CqListenersBasicEvents beforeedit;
    @JsonProperty("beforeinsert")
    private CqListeners.CqListenersBasicEvents beforeinsert;
    @JsonProperty("beforemove")
    private CqListeners.CqListenersBasicEvents beforemove;

    @JsonProperty("afterchildinsert")
    public CqListeners.CqListenersBasicEvents getAfterchildinsert() {
        return afterchildinsert;
    }

    @JsonProperty("afterchildinsert")
    public void setAfterchildinsert(CqListeners.CqListenersBasicEvents afterchildinsert) {
        this.afterchildinsert = afterchildinsert;
    }

    @JsonProperty("aftercopy")
    public CqListeners.CqListenersBasicEvents getAftercopy() {
        return aftercopy;
    }

    @JsonProperty("aftercopy")
    public void setAftercopy(CqListeners.CqListenersBasicEvents aftercopy) {
        this.aftercopy = aftercopy;
    }

    @JsonProperty("afterdelete")
    public CqListeners.CqListenersBasicEvents getAfterdelete() {
        return afterdelete;
    }

    @JsonProperty("afterdelete")
    public void setAfterdelete(CqListeners.CqListenersBasicEvents afterdelete) {
        this.afterdelete = afterdelete;
    }

    @JsonProperty("afteredit")
    public CqListeners.CqListenersBasicEvents getAfteredit() {
        return afteredit;
    }

    @JsonProperty("afteredit")
    public void setAfteredit(CqListeners.CqListenersBasicEvents afteredit) {
        this.afteredit = afteredit;
    }

    @JsonProperty("afterinsert")
    public CqListeners.CqListenersBasicEvents getAfterinsert() {
        return afterinsert;
    }

    @JsonProperty("afterinsert")
    public void setAfterinsert(CqListeners.CqListenersBasicEvents afterinsert) {
        this.afterinsert = afterinsert;
    }

    @JsonProperty("aftermove")
    public CqListeners.CqListenersBasicEvents getAftermove() {
        return aftermove;
    }

    @JsonProperty("aftermove")
    public void setAftermove(CqListeners.CqListenersBasicEvents aftermove) {
        this.aftermove = aftermove;
    }

    @JsonProperty("beforechildinsert")
    public CqListeners.CqListenersBasicEvents getBeforechildinsert() {
        return beforechildinsert;
    }

    @JsonProperty("beforechildinsert")
    public void setBeforechildinsert(CqListeners.CqListenersBasicEvents beforechildinsert) {
        this.beforechildinsert = beforechildinsert;
    }

    @JsonProperty("beforecopy")
    public CqListeners.CqListenersBasicEvents getBeforecopy() {
        return beforecopy;
    }

    @JsonProperty("beforecopy")
    public void setBeforecopy(CqListeners.CqListenersBasicEvents beforecopy) {
        this.beforecopy = beforecopy;
    }

    @JsonProperty("beforedelete")
    public CqListeners.CqListenersBasicEvents getBeforedelete() {
        return beforedelete;
    }

    @JsonProperty("beforedelete")
    public void setBeforedelete(CqListeners.CqListenersBasicEvents beforedelete) {
        this.beforedelete = beforedelete;
    }

    @JsonProperty("beforeedit")
    public CqListeners.CqListenersBasicEvents getBeforeedit() {
        return beforeedit;
    }

    @JsonProperty("beforeedit")
    public void setBeforeedit(CqListeners.CqListenersBasicEvents beforeedit) {
        this.beforeedit = beforeedit;
    }

    @JsonProperty("beforeinsert")
    public CqListeners.CqListenersBasicEvents getBeforeinsert() {
        return beforeinsert;
    }

    @JsonProperty("beforeinsert")
    public void setBeforeinsert(CqListeners.CqListenersBasicEvents beforeinsert) {
        this.beforeinsert = beforeinsert;
    }

    @JsonProperty("beforemove")
    public CqListeners.CqListenersBasicEvents getBeforemove() {
        return beforemove;
    }

    @JsonProperty("beforemove")
    public void setBeforemove(CqListeners.CqListenersBasicEvents beforemove) {
        this.beforemove = beforemove;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CqListeners.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("afterchildinsert");
        sb.append('=');
        sb.append(((this.afterchildinsert == null) ? "<null>" : this.afterchildinsert));
        sb.append(',');
        sb.append("aftercopy");
        sb.append('=');
        sb.append(((this.aftercopy == null) ? "<null>" : this.aftercopy));
        sb.append(',');
        sb.append("afterdelete");
        sb.append('=');
        sb.append(((this.afterdelete == null) ? "<null>" : this.afterdelete));
        sb.append(',');
        sb.append("afteredit");
        sb.append('=');
        sb.append(((this.afteredit == null) ? "<null>" : this.afteredit));
        sb.append(',');
        sb.append("afterinsert");
        sb.append('=');
        sb.append(((this.afterinsert == null) ? "<null>" : this.afterinsert));
        sb.append(',');
        sb.append("aftermove");
        sb.append('=');
        sb.append(((this.aftermove == null) ? "<null>" : this.aftermove));
        sb.append(',');
        sb.append("beforechildinsert");
        sb.append('=');
        sb.append(((this.beforechildinsert == null) ? "<null>" : this.beforechildinsert));
        sb.append(',');
        sb.append("beforecopy");
        sb.append('=');
        sb.append(((this.beforecopy == null) ? "<null>" : this.beforecopy));
        sb.append(',');
        sb.append("beforedelete");
        sb.append('=');
        sb.append(((this.beforedelete == null) ? "<null>" : this.beforedelete));
        sb.append(',');
        sb.append("beforeedit");
        sb.append('=');
        sb.append(((this.beforeedit == null) ? "<null>" : this.beforeedit));
        sb.append(',');
        sb.append("beforeinsert");
        sb.append('=');
        sb.append(((this.beforeinsert == null) ? "<null>" : this.beforeinsert));
        sb.append(',');
        sb.append("beforemove");
        sb.append('=');
        sb.append(((this.beforemove == null) ? "<null>" : this.beforemove));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.beforemove == null) ? 0 : this.beforemove.hashCode()));
        result = ((result * 31) + ((this.aftermove == null) ? 0 : this.aftermove.hashCode()));
        result = ((result * 31) + ((this.beforechildinsert == null) ? 0 : this.beforechildinsert.hashCode()));
        result = ((result * 31) + ((this.beforedelete == null) ? 0 : this.beforedelete.hashCode()));
        result = ((result * 31) + ((this.beforeedit == null) ? 0 : this.beforeedit.hashCode()));
        result = ((result * 31) + ((this.afterchildinsert == null) ? 0 : this.afterchildinsert.hashCode()));
        result = ((result * 31) + ((this.afteredit == null) ? 0 : this.afteredit.hashCode()));
        result = ((result * 31) + ((this.afterinsert == null) ? 0 : this.afterinsert.hashCode()));
        result = ((result * 31) + ((this.beforeinsert == null) ? 0 : this.beforeinsert.hashCode()));
        result = ((result * 31) + ((this.aftercopy == null) ? 0 : this.aftercopy.hashCode()));
        result = ((result * 31) + ((this.afterdelete == null) ? 0 : this.afterdelete.hashCode()));
        result = ((result * 31) + ((this.beforecopy == null) ? 0 : this.beforecopy.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CqListeners) == false) {
            return false;
        }
        CqListeners rhs = ((CqListeners) other);
        return (((((((((((((this.beforemove == rhs.beforemove) ||
                ((this.beforemove != null) && this.beforemove.equals(rhs.beforemove))) &&
                ((this.aftermove == rhs.aftermove) ||
                        ((this.aftermove != null) && this.aftermove.equals(rhs.aftermove)))) &&
                ((this.beforechildinsert == rhs.beforechildinsert) ||
                        ((this.beforechildinsert != null) && this.beforechildinsert.equals(rhs.beforechildinsert)))) &&
                ((this.beforedelete == rhs.beforedelete) ||
                        ((this.beforedelete != null) && this.beforedelete.equals(rhs.beforedelete)))) &&
                ((this.beforeedit == rhs.beforeedit) ||
                        ((this.beforeedit != null) && this.beforeedit.equals(rhs.beforeedit)))) &&
                ((this.afterchildinsert == rhs.afterchildinsert) ||
                        ((this.afterchildinsert != null) && this.afterchildinsert.equals(rhs.afterchildinsert)))) &&
                ((this.afteredit == rhs.afteredit) ||
                        ((this.afteredit != null) && this.afteredit.equals(rhs.afteredit)))) &&
                ((this.afterinsert == rhs.afterinsert) ||
                        ((this.afterinsert != null) && this.afterinsert.equals(rhs.afterinsert)))) &&
                ((this.beforeinsert == rhs.beforeinsert) ||
                        ((this.beforeinsert != null) && this.beforeinsert.equals(rhs.beforeinsert)))) &&
                ((this.aftercopy == rhs.aftercopy) ||
                        ((this.aftercopy != null) && this.aftercopy.equals(rhs.aftercopy)))) &&
                ((this.afterdelete == rhs.afterdelete) ||
                        ((this.afterdelete != null) && this.afterdelete.equals(rhs.afterdelete)))) &&
                ((this.beforecopy == rhs.beforecopy) ||
                        ((this.beforecopy != null) && this.beforecopy.equals(rhs.beforecopy))));
    }

    public enum CqListenersBasicEvents {

        REFRESH_SELF("REFRESH_SELF"), REFRESH_PAGE("REFRESH_PAGE");
        private final String value;
        private final static Map<String, CqListeners.CqListenersBasicEvents> CONSTANTS =
                new HashMap<String, CqListeners.CqListenersBasicEvents>();

        static {
            for (CqListeners.CqListenersBasicEvents c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private CqListenersBasicEvents(String value) {
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
        public static CqListeners.CqListenersBasicEvents fromValue(String value) {
            CqListeners.CqListenersBasicEvents constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
