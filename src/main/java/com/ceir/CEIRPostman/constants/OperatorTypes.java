package com.ceir.CEIRPostman.constants;

public enum OperatorTypes {
    SMART("smart"),
    METFONE("metfone"),
    CELLCARD("cellcard"),
    SEATEL("seatel"),
    DEFAULT("default");

    private final String value;

    OperatorTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
