package vn.phankyanh.shopbongda.field.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.field.domain.FootballField;

@Repository
public interface FootballFieldRepository extends JpaRepository<FootballField, Long> {
    
}
