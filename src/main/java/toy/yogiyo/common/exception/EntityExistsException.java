package toy.yogiyo.common.exception;

import com.fasterxml.jackson.databind.ser.Serializers;

public class EntityExistsException extends BaseException {
    public EntityExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
