package vn.phankyanh.shopbongda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
