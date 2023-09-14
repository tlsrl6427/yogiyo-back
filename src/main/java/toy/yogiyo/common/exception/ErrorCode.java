package toy.yogiyo.common.exception;

public enum ErrorCode {

    MEMBER_NOT_FOUND(400, "U_001", "고객을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXIST(400, "U_002", "이미 존재하는 이메일입니다"),

    SHOP_NOT_FOUND(400, "SH_001", "가게를 찾을 수 없습니다."),
    SHOP_ACCESS_DENIED(400, "SH_002", "가게에 대한 권한이 없습니다."),
    SHOP_ALREADY_EXIST(400, "SH_003", "이미 존재하는 가게명 입니다."),

    CATEGORY_NOT_FOUND(400, "C_001", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXIST(400, "C_002", "이미 존재하는 카테고리명 입니다."),

    FILE_NOT_REMOVED(400, "F_001", "관련 파일을 삭제하지 못했습니다."),
    FILE_EMPTY(400, "F_002", "파일이 비어있습니다.");

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
