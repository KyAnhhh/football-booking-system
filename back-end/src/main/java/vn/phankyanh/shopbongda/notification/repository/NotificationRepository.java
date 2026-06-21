package vn.phankyanh.shopbongda.notification.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.notification.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
    SELECT n FROM Notification n
    WHERE n.user.id = :userId
    ORDER BY n.createdAt DESC
    """)
    List<Notification> findLatestByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.isRead = true,
        n.readAt = CURRENT_TIMESTAMP
    WHERE n.user.id = :userId
    AND n.isRead = false
    """)
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
