package vn.phankyanh.shopbongda.membership.service;
import java.math.BigDecimal;

public interface MembershipService {

    void addPoints(
            Long userId,
            BigDecimal bookingAmount
    );

    void updateMembershipTier(
            Long userId
    );

    BigDecimal applyMembershipDiscount(
            Long userId,
            BigDecimal amount
    );
}
