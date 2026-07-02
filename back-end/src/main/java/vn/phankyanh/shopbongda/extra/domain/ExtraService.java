package vn.phankyanh.shopbongda.extra.domain;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.*;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;

@Entity
@Table(name = "extra_services")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PublicationStatus publicationStatus = PublicationStatus.DRAFT;

    private Boolean trackStock;

    private Integer stockQuantity;

    private Integer remainingQuantity;

    private String unit;

    private Long inventoryItemId;

    @PrePersist
    void onCreate() {
        if (publicationStatus == null) {
            publicationStatus = PublicationStatus.DRAFT;
        }
    }
}
