package vn.phankyanh.shopbongda.membership.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membership_tiers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer requiredPoints;

    private Integer discountPercent;

    private Boolean isActive;
}