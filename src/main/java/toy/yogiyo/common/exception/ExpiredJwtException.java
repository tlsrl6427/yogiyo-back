package toy.yogiyo.common.exception;

import java.util.Base64;

public class ExpiredJwtException extends BaseException {
    public ExpiredJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
