package vn.phankyanh.shopbongda.field.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;
import vn.phankyanh.shopbongda.field.domain.FieldType;
import vn.phankyanh.shopbongda.field.domain.FootballField;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Long> {
    List<FootballField> findByStatus(FieldStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT f FROM FootballField f
        WHERE f.id = :fieldId
    """)
    Optional<FootballField> findByIdForUpdate(
        @Param("fieldId") Long fieldId
    );

    @Query("""
        SELECT f FROM FootballField f
        WHERE (
            :keyword IS NULL
            OR LOWER(f.name)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
        )

        AND (:status IS NULL OR f.status = :status)

        AND (:type IS NULL OR f.type = :type)

        AND (:minPrice IS NULL OR f.pricePerHour >= :minPrice)

        AND (:maxPrice IS NULL OR f.pricePerHour <= :maxPrice)
    """)
    Page<FootballField> searchFootballFields(
        @Param("keyword") String keyword,
        @Param("status") FieldStatus status,
        @Param("type") FieldType type,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
}
