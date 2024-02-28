package au.gov.nsw.lpi.common;

public class StandardisedResponse {
    public ResponseCode code;
    public String message;
    public Object data;

    public StandardisedResponse(ResponseCode code, String message, Object data) {
        init(code, message, data);
    }

//    public StandardisedResponse(ResponseCode code, String message) {
//        init(code, message, null);
//    }

    private void init(ResponseCode code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
