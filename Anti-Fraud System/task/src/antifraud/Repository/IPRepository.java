package antifraud.Repository;

import antifraud.Models.Entity.IP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPRepository extends JpaRepository<IP,Long> {
}
