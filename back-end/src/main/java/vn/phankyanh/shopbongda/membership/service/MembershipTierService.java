package vn.phankyanh.shopbongda.membership.service;

import java.util.List;
import vn.phankyanh.shopbongda.membership.dto.request.MembershipTierRequest;
import vn.phankyanh.shopbongda.membership.dto.response.MembershipTierResponse;

public interface MembershipTierService {

    MembershipTierResponse create(
            MembershipTierRequest request
    );

    List<MembershipTierResponse> getAll();

    MembershipTierResponse update(
            Long id,
            MembershipTierRequest request
    );

    void delete(Long id);
}
