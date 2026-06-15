package vn.phankyanh.shopbongda.maintenance.component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import vn.phankyanh.shopbongda.field.domain.FootballField;

import java.time.LocalDateTime;

@Component
@Order(1)
public class MaintenanceDataMigration implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        migrateMaintenanceFields();
    }

    private void migrateMaintenanceFields() {
        // Check if there are any fields with MAINTENANCE status
        Long count = entityManager.createQuery(
                        "SELECT COUNT(f) FROM FootballField f WHERE f.status = 'MAINTENANCE'", Long.class)
                .getSingleResult();

        if (count > 0) {
            // Create maintenance records for each field with MAINTENANCE status
            // Set maintenance from now until far future (admin will update later)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime farFuture = now.plusYears(10);

            entityManager.createNativeQuery("""
                    INSERT INTO maintenances (field_id, start_time, end_time, reason, description, created_at, updated_at)
                    SELECT id, :startTime, :endTime, 'Migrated from MAINTENANCE status', 'Auto-migrated from FieldStatus.MAINTENANCE', :now, :now
                    FROM football_fields
                    WHERE status = 'MAINTENANCE'
                    """)
                    .setParameter("startTime", now)
                    .setParameter("endTime", farFuture)
                    .setParameter("now", now)
                    .executeUpdate();

            // Update fields to AVAILABLE status (they'll be blocked by maintenance schedule)
            entityManager.createNativeQuery("""
                    UPDATE football_fields SET status = 'AVAILABLE' WHERE status = 'MAINTENANCE'
                    """)
                    .executeUpdate();

            System.out.println("Migrated " + count + " field(s) from MAINTENANCE status to maintenance schedule");
        }
    }
}