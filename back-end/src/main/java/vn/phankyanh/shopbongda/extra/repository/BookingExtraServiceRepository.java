package vn.phankyanh.shopbongda.extra.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.extra.domain.BookingExtraService;

@Repository
public interface BookingExtraServiceRepository
        extends JpaRepository<BookingExtraService, Long> {
}