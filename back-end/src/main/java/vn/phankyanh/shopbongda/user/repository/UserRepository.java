package vn.phankyanh.shopbongda.user.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.domain.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    Page<User> findByEmailContaining(String email, Pageable pageable);


    @Query("SELECT COUNT(u) FROM User u")
    Long countUsers();

}
