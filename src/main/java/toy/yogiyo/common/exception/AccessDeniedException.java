package toy.yogiyo.common.exception;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
