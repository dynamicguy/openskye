package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The different types of attributes allowed.
 */
public enum AttributeType {
    /**
     * A text/string attribute
     */
    TEXT("TEXT"),
    /**
     * A numeric attribute.
     */
    NUMERIC("NUMERIC"),
    /**
     * A date attribute
      */
    DATE("DATE"),
    /**
     * An enumerated attribute
     */
    ENUMERATED("ENUMERATED");

    private AttributeType(String value) {
        this.value = value;
    }

    private String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
