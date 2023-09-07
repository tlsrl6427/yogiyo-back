package toy.yogiyo.common.exception;

public class EntityNotFoundException extends BaseException{
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
