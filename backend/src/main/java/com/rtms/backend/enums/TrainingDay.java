package com.rtms.backend.enums;

public enum TrainingDay {
    MONDAY("MONDAY"),
    TUESDAY("TUESDAY"),
    WEDNESDAY("WEDNESDAY"),
    THURSDAY("THURSDAY"),
    FRIDAY("FRIDAY"),
    SATURDAY("SATURDAY"),
    SUNDAY("SUNDAY");

    private final String value;

    TrainingDay(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
