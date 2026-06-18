package vn.phankyanh.shopbongda.membership.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import vn.phankyanh.shopbongda.membership.domain.MembershipTier;
import vn.phankyanh.shopbongda.membership.repository.MembershipTierRepository;
import vn.phankyanh.shopbongda.membership.service.MembershipService;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final UserRepository userRepository;

    private final MembershipTierRepository membershipTierRepository;

    @Override
    public void addPoints(
            Long userId,
            BigDecimal bookingAmount
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        int earnedPoints = bookingAmount
                .divide(
                        BigDecimal.valueOf(10000),
                        0,
                        RoundingMode.DOWN
                )
                .intValue();

        Integer currentPoints =
                user.getLoyaltyPoints() == null
                        ? 0
                        : user.getLoyaltyPoints();

        user.setLoyaltyPoints(
                currentPoints + earnedPoints
        );

        MembershipTier tier =
                membershipTierRepository
                        .findTopByRequiredPointsLessThanEqualAndIsActiveTrueOrderByRequiredPointsDesc(
                                user.getLoyaltyPoints()
                        );

        if (tier != null) {
            user.setMembershipTier(tier);
        }

        userRepository.save(user);
    }

    @Override
    public void updateMembershipTier(
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        MembershipTier tier =
                membershipTierRepository
                        .findTopByRequiredPointsLessThanEqualAndIsActiveTrueOrderByRequiredPointsDesc(
                                user.getLoyaltyPoints()
                        );

        if (tier != null) {
            user.setMembershipTier(tier);
            userRepository.save(user);
        }
    }

    @Override
    public BigDecimal applyMembershipDiscount(
            Long userId,
            BigDecimal amount
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        MembershipTier tier =
                user.getMembershipTier();

        if (tier == null) {
            return amount;
        }

        BigDecimal discount =
                amount.multiply(
                        BigDecimal.valueOf(
                                tier.getDiscountPercent()
                        )
                ).divide(
                        BigDecimal.valueOf(100)
                );

        return amount.subtract(discount);
    }
}