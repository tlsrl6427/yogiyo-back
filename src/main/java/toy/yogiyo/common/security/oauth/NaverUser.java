package toy.yogiyo.common.security.oauth;

import lombok.Data;

@Data
public class NaverUser {

    private String resultcode;
    private String message;
    private Response response;

    @Data
    public class Response{
        private String id;
        private String email;
        private String nickname;
    }
}
