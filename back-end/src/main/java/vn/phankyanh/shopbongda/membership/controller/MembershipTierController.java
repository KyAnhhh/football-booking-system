package vn.phankyanh.shopbongda.membership.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import vn.phankyanh.shopbongda.membership.dto.request.MembershipTierRequest;
import vn.phankyanh.shopbongda.membership.dto.response.MembershipTierResponse;
import vn.phankyanh.shopbongda.membership.service.MembershipTierService;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/membership-tiers")
public class MembershipTierController {

    private final MembershipTierService membershipTierService;

    @PostMapping
    public MembershipTierResponse create(
            @Valid @RequestBody MembershipTierRequest request
    ) {

        return membershipTierService.create(request);
    }

    @GetMapping
    public List<MembershipTierResponse> getAll() {

        return membershipTierService.getAll();
    }

    @PutMapping("/{id}")
    public MembershipTierResponse update(
            @PathVariable Long id,
            @Valid @RequestBody MembershipTierRequest request
    ) {

        return membershipTierService.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        membershipTierService.delete(id);
    }
}
