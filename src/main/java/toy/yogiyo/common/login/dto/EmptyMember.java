package toy.yogiyo.common.login.dto;

import toy.yogiyo.core.Member.domain.Member;

public class EmptyMember extends Member {
    public EmptyMember() {
        super(null, null, null, null, null);
    }
}
