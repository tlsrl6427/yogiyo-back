package toy.yogiyo.common.exception;

public class IllegalArgumentException extends BaseException{
    public IllegalArgumentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
