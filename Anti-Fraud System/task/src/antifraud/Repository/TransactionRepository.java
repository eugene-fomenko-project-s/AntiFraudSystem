package antifraud.Repository;

import antifraud.Models.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("SELECT COUNT(DISTINCT t.region) FROM Transaction t WHERE t.region <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctRegionCount(
            String region,
            String number,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT COUNT(DISTINCT t.ip) FROM Transaction t WHERE t.ip <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctIpCount(String ip,
                                            String number,
                                            LocalDateTime start,
                                            LocalDateTime end);
}
