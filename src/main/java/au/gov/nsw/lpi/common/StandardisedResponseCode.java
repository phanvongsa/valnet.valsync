package au.gov.nsw.lpi.common;

public enum StandardisedResponseCode {
    ERROR(0),
    SUCCESS(1),
    PENDING(2);

    private final int numericValue;

    StandardisedResponseCode(int numericValue) {
        this.numericValue = numericValue;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
