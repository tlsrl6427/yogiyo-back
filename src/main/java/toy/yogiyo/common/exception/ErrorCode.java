package toy.yogiyo.common.exception;

public enum ErrorCode {

    MEMBER_NOT_FOUND(400, "M_001", "고객을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXIST(400, "M_002", "이미 존재하는 이메일입니다"),
    MEMBER_UNAUTHORIZATION(403, "M_003", "권한이 없습니다"),

    MEMBERADDRESS_NOT_FOUND(400, "MA_001", "주소를 찾을 수 없습니다."),

    OWNER_NOT_FOUND(400, "OWN_001", "점주를 찾을 수 없습니다."),
    OWNER_ALREADY_EXIST(400, "OWN_002", "이미 존재하는 이메일입니다"),
    OWNER_UNAUTHORIZATION(403, "OWN_003", "권한이 없습니다"),

    ORDER_NOT_FOUND(400, "ORD_001", "주문내역을 찾을 수 없습니다."),

    USERTYPE_ILLEGAL(400, "U_001", "유효하지 않는 유저 타입입니다"),

    JWT_EXPIRED(401, "J_001", "토큰 인증기간이 만료되었습니다"),
    JWT_UNSUPPORTED(401, "J_002", "토큰의 포맷이 맞지 않습니다"),
    JWT_MALFORMED(401, "J_003", "잘못된 형식의 토큰입니다"),
    JWT_SIGNATURE_FAILED(401, "J_004", "서명 검증에 실패하였습니다"),
    JWT_ILLEGAL_ARGUMENT(401, "J_005", "잘못된 인자입니다"),

    SHOP_NOT_FOUND(400, "SH_001", "가게를 찾을 수 없습니다."),
    SHOP_ACCESS_DENIED(400, "SH_002", "가게에 대한 권한이 없습니다."),
    SHOP_ALREADY_EXIST(400, "SH_003", "이미 존재하는 가게명 입니다."),

    CATEGORY_NOT_FOUND(400, "C_001", "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXIST(400, "C_002", "이미 존재하는 카테고리명 입니다."),

    MENU_NOT_FOUND(400, "M_001", "메뉴를 찾을 수 없습니다."),

    MENUGROUP_NOT_FOUND(400, "MG_001", "메뉴 그룹을 찾을 수 없습니다."),

    MENUGROUPITEM_NOT_FOUND(400, "MGI_001", "메뉴를 찾을 수 없습니다."),

    SIGNATUREMENU_NOT_FOUND(400, "SM_001", "대표 메뉴를 찾을 수 없습니다."),

    MENUOPTION_NOT_FOUND(400, "MO_001", "옵션을 찾을 수 없습니다."),

    MENUOPTIONGROUP_NOT_FOUND(400, "MOG_001", "옵션 그룹을 찾을 수 없습니다."),

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
