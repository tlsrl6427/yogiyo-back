package toy.yogiyo.core.owner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.yogiyo.common.exception.AuthenticationException;
import toy.yogiyo.common.exception.EntityExistsException;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.core.owner.dto.OwnerJoinRequest;
import toy.yogiyo.core.owner.dto.OwnerJoinResponse;
import toy.yogiyo.core.owner.dto.OwnerMypageResponse;
import toy.yogiyo.core.owner.dto.OwnerUpdateRequest;
import toy.yogiyo.core.owner.domain.Owner;
import toy.yogiyo.core.owner.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final BCryptPasswordEncoder encoder;


    public OwnerJoinResponse join(OwnerJoinRequest ownerJoinRequest) {
        ownerRepository.findByEmailAndProvider(ownerJoinRequest.getEmail(), ownerJoinRequest.getProviderType())
                .ifPresent(owner -> {throw new EntityExistsException(ErrorCode.OWNER_ALREADY_EXIST);});

        Owner savedOwner = ownerRepository.save(ownerJoinRequest.toOwner());
        savedOwner.setEncodedPassword(encoder.encode(ownerJoinRequest.getPassword()));
        return OwnerJoinResponse.of(savedOwner);
    }

    @Transactional(readOnly = true)
    public OwnerMypageResponse getMypage(Owner owner){
        if(owner.getId() == null) throw new AuthenticationException(ErrorCode.OWNER_UNAUTHORIZATION);
        return OwnerMypageResponse.of(owner);
    }

    public void update(Owner owner, OwnerUpdateRequest ownerUpdateRequest) {
        if(owner.getId() == null) throw new AuthenticationException(ErrorCode.OWNER_UNAUTHORIZATION);
        owner.update(ownerUpdateRequest.toOwner());
    }

    public void delete(Owner owner) {
        if(owner.getId() == null) throw new AuthenticationException(ErrorCode.OWNER_UNAUTHORIZATION);
        ownerRepository.delete(owner);
    }
}
