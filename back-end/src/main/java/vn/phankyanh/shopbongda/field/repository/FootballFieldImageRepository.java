package vn.phankyanh.shopbongda.field.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.field.domain.FootballFieldImage;

@Repository
public interface FootballFieldImageRepository extends JpaRepository<FootballFieldImage, Long> {

    List<FootballFieldImage> findByFieldIdOrderBySortOrderAscIdAsc(Long fieldId);

    boolean existsByFieldIdAndImageUrl(Long fieldId, String imageUrl);

    @Modifying
    @Query("UPDATE FootballFieldImage i SET i.isPrimary = false WHERE i.field.id = :fieldId")
    void resetPrimary(Long fieldId);
}
