package au.gov.nsw.lpi.common;

public enum ResponseCode {
    ERROR(0),
    SUCCESS(1),
    PENDING(2);

    private final int numericValue;

    ResponseCode(int numericValue) {
        this.numericValue = numericValue;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
