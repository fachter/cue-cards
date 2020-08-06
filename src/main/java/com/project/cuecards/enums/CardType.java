package com.project.cuecards.enums;

public enum CardType {

    MC("mc"),
    SC("sc"),
    FT("ft");

    private final String stringValue;

    CardType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
