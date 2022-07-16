package antifraud.Config.Controller;

import antifraud.Models.Entity.Card;
import antifraud.Models.Status;
import antifraud.Service.RepoService.CardService;
import antifraud.Service.Utils.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api/antifraud/")
public class CardController {
    @Autowired
    CardService cardService;
    Support support = new Support();

    @PostMapping("stolencard")
    public ResponseEntity<?> stolencard(@RequestBody Card number) {
        if (!support.check_card(number.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Card searchedCard;

        if (cardService.findCardByNumber(number.getNumber()) != null) {
            if (cardService.findCardByNumberAndIsStolenTrue(number.getNumber()) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            } else {
                searchedCard = cardService.findCardByNumber(number.getNumber());
                searchedCard.setStolen(true);
            }
        } else {
            Card card = new Card();
            card.setNumber(number.getNumber());
            card.setStolen(true);
            searchedCard = card;
        }

        cardService.Save(searchedCard);
        return new ResponseEntity<>(searchedCard, HttpStatus.OK);
    }
    @DeleteMapping("stolencard/{number}")
    public ResponseEntity<?> delete_stolencard(@PathVariable String number) {
        if(support.check_card(number)){
            if(cardService.findCardByNumber(number) != null){
                cardService.Delete(number);
                return ResponseEntity.ok().body(new Status("Card "+number+" successfully removed!"));
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("stolencard")
    public ResponseEntity<?> list_stolencard() {
        return ResponseEntity.ok().body(cardService.getAll().stream().filter(Card::isStolen));
    }
}
