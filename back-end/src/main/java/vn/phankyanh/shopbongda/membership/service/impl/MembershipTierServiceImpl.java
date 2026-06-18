package vn.phankyanh.shopbongda.membership.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.membership.domain.MembershipTier;
import vn.phankyanh.shopbongda.membership.dto.request.MembershipTierRequest;
import vn.phankyanh.shopbongda.membership.dto.response.MembershipTierResponse;
import vn.phankyanh.shopbongda.membership.mapper.MembershipTierMapper;
import vn.phankyanh.shopbongda.membership.repository.MembershipTierRepository;
import vn.phankyanh.shopbongda.membership.service.MembershipTierService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipTierServiceImpl
        implements MembershipTierService {

    private final MembershipTierRepository membershipTierRepository;

    private final MembershipTierMapper membershipTierMapper;

    @Override
    public MembershipTierResponse create(
            MembershipTierRequest request
    ) {

        MembershipTier membershipTier =
                membershipTierMapper.toEntity(request);

        MembershipTier saved =
                membershipTierRepository.save(membershipTier);

        return membershipTierMapper.toResponse(saved);
    }

    @Override
    public List<MembershipTierResponse> getAll() {

        return membershipTierRepository.findAll()
                .stream()
                .map(membershipTierMapper::toResponse)
                .toList();
    }

    @Override
    public MembershipTierResponse update(
            Long id,
            MembershipTierRequest request
    ) {

        MembershipTier membershipTier =
                membershipTierRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Membership tier not found"));

        membershipTierMapper.updateEntity(
                request,
                membershipTier
        );

        MembershipTier updated =
                membershipTierRepository.save(membershipTier);

        return membershipTierMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        MembershipTier membershipTier =
                membershipTierRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Membership tier not found"));

        membershipTierRepository.delete(membershipTier);
    }
}