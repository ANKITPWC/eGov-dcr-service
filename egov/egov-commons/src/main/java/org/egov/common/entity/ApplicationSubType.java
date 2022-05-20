package org.egov.common.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplicationSubType {

	REVISE("Revise");

    @JsonValue
    private final String applicationSubTypeVal;

    ApplicationSubType(String asTypeVal) {
        this.applicationSubTypeVal = asTypeVal;
    }

    public String getApplicationType() {
        return applicationSubTypeVal;
    }

    public String getApplicationTypeVal() {
        return applicationSubTypeVal;
    }

}
