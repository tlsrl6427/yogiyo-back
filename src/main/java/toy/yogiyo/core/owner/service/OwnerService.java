package toy.yogiyo.core.owner.service;

import org.springframework.stereotype.Service;
import toy.yogiyo.core.owner.domain.Owner;

@Service
public class OwnerService {

    public Owner findOneTemp(Long id) {
        return new Owner(id);
    }
}
