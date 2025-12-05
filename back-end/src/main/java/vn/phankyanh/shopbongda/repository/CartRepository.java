package vn.phankyanh.shopbongda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT DISTINCT c FROM Cart c " +
	    "LEFT JOIN FETCH c.items i " +
	    "LEFT JOIN FETCH c.user u " +
	    "WHERE c.id = :id")
    Optional<Cart> findByIdWithItemsAndUser(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Cart c " +
	    "LEFT JOIN FETCH c.items i " +
	    "LEFT JOIN FETCH c.user u")
    List<Cart> findAllWithItemsAndUser();

	@Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId")
	Optional<Cart> findByUserIdWithItems(Long userId);

}
