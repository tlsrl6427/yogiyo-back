package toy.yogiyo.common.exception;

public enum ErrorCode {

    MEMBER_NOT_FOUND(400, "U_001", "고객을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXIST(400, "U_002", "이미 존재하는 이메일입니다");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
