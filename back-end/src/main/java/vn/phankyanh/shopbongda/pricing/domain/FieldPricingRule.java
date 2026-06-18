package vn.phankyanh.shopbongda.pricing.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import vn.phankyanh.shopbongda.field.domain.FootballField;

@Entity
@Table(name = "field_pricing_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldPricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private FootballField field;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    private Boolean isActive;
}
