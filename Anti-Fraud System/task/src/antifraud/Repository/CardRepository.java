package antifraud.Repository;

import antifraud.Models.Entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Long> {
    List<Card> findAllByIsStolenTrue();
    Card findCardByNumberAndIsStolenTrue(String number);
}
