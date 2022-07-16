package antifraud.Service.RepoService;

import antifraud.Models.Entity.Card;
import antifraud.Repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
public class CardService {
    @Autowired
    CardRepository cardRepository;
    public Card findCardByNumber(String number) {
        List<Card> list_card = cardRepository.findAll();
        for(Card card : list_card){
            if(card.getNumber().equals(number)){
                return card;
            }
        }
        return null;
    }
    public List<Card> findAllByIsStolenTrue(){
        return cardRepository.findAllByIsStolenTrue();
    }
    public  Card findCardByNumberAndIsStolenTrue(String number){
        return cardRepository.findCardByNumberAndIsStolenTrue(number);
    }
    public void Save(Card card) {
        cardRepository.saveAndFlush(card);
    }
    public void Delete(String number){
        List<Card> cards = cardRepository.findAll();
        for(Card card : cards){
            if(Objects.equals(card.getNumber(), number)){
                cardRepository.deleteById(card.getId());
            }
        }
    }
    public void Delete(long id) {
        cardRepository.deleteById(id);
    }

    public List<Card> getAll() {
        return cardRepository.findAll();
    }
}
