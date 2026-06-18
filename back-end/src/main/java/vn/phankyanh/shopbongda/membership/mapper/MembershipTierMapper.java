package vn.phankyanh.shopbongda.membership.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.membership.domain.MembershipTier;
import vn.phankyanh.shopbongda.membership.dto.request.MembershipTierRequest;
import vn.phankyanh.shopbongda.membership.dto.response.MembershipTierResponse;

@Mapper(componentModel = "spring")
public interface MembershipTierMapper {

    MembershipTier toEntity(
            MembershipTierRequest request
    );

    MembershipTierResponse toResponse(
            MembershipTier entity
    );

    void updateEntity(
            MembershipTierRequest request,
            @MappingTarget MembershipTier entity
    );
}
