package antifraud.Config.Controller;

import antifraud.Models.*;
import antifraud.Models.Entity.Card;
import antifraud.Models.Entity.Transaction;
import antifraud.Service.RepoService.CardService;
import antifraud.Service.RepoService.IPService;
import antifraud.Service.Utils.Support;
import antifraud.Service.RepoService.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/antifraud/")
public class TransactionController {
    @Autowired
    IPService ipService;
    Support support = new Support();
    @Autowired
    CardService cardService;
    @Autowired
    TransactionService transactionService;


    @PostMapping("transaction")
    public ResponseEntity<?> transaction(@RequestBody Transaction transaction) {
        List<String> info = new ArrayList<>();
        if (transaction.getAmount() == null || transaction.getAmount() <= 0 || !support.check_card(transaction.getNumber()) || !support.check_valid(transaction.getIp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            ResultValid resultValid = new ResultValid(null);
            Card card = cardService.findCardByNumber(transaction.getNumber());
            if(card == null){
                    card = new Card();
                    card.setNumber(transaction.getNumber());
                    card.setStolen(false);
                    cardService.Save(card);
            }
            if (transaction.getAmount() > card.getMaxLimit()) {
                resultValid.setResult("PROHIBITED");
                info.add("amount");
            }
            if (transaction.getAmount() <= card.getMinLimit()) {
                resultValid.setResult("ALLOWED");
                info.add("none");
            } else if (transaction.getAmount() > card.getMinLimit() && transaction.getAmount() <= card.getMaxLimit()) {
                if (!Objects.equals(resultValid.getResult(), "PROHIBITED")) {
                    resultValid.setResult("MANUAL_PROCESSING");
                }
                info.add("amount");
            }
            if (cardService.findCardByNumber(transaction.getNumber()).isStolen()) {
                if(!resultValid.getResult().equals("PROHIBITED")){
                    info.clear();
                    resultValid.setResult("PROHIBITED");
                }
                info.add("card-number");
            }
            if (ipService.findByIP(transaction.getIp()) != null) {
                if(!resultValid.getResult().equals("PROHIBITED")){
                    info.clear();
                    resultValid.setResult("PROHIBITED");
                }
                info.add("ip");
            }
            Long region = transactionService.getTransactionsWithDistinctRegionCount(transaction.getRegion(),
                    transaction.getNumber(),
                    transaction.getDate().minusHours(1),
                    transaction.getDate());
            if (region > 1) {
                info.add("region-correlation");
                info.remove("none");
                if (region == 2) {
                    resultValid.setResult("MANUAL_PROCESSING");
                } else {
                    resultValid.setResult("PROHIBITED");
                }
            }
            Long ip = transactionService.getTransactionsWithDistinctIpCount(transaction.getIp(),
                    transaction.getNumber(),
                    transaction.getDate().minusHours(1),
                    transaction.getDate());
            if (ip > 1) {
                info.add("ip-correlation");
                info.remove("none");
                if (ip == 2) {
                    resultValid.setResult("MANUAL_PROCESSING");
                } else {
                    resultValid.setResult("PROHIBITED");
                }
            }
            resultValid.setInfo(info.stream().sorted().collect(Collectors.joining(", ")));
            transaction.setResult(resultValid.getResult());
            transaction.setFeedback("");
            transactionService.Save(transaction);
            return ResponseEntity.status(HttpStatus.OK).body(resultValid);
        }
    }
    @PutMapping("/transaction")
    public ResponseEntity<?> Feedback(@Valid @RequestBody Feedback feedback) {
        return addFeedback(feedback);
    }
    private void changeLimit(Transaction transaction, String feedback) {
        String trStatus = transaction.getResult();
        Card card = cardService.findCardByNumber(transaction.getNumber());

        int increasedAllowed = (int) Math.ceil(0.8 * card.getMinLimit() + 0.2 * transaction.getAmount());
        int decreasedAllowed = (int) Math.ceil(0.8 * card.getMinLimit() - 0.2 * transaction.getAmount());
        int increasedManual = (int) Math.ceil(0.8 * card.getMaxLimit() + 0.2 * transaction.getAmount());
        int decreasedManual = (int) Math.ceil(0.8 * card.getMaxLimit() - 0.2 * transaction.getAmount());

        if (feedback.equals("MANUAL_PROCESSING") && trStatus.equals("ALLOWED")) {
            card.setMinLimit(decreasedAllowed);
        } else if (feedback.equals("PROHIBITED") && trStatus.equals("ALLOWED")) {
            card.setMinLimit(decreasedAllowed);
            card.setMaxLimit(decreasedManual);
        } else if (feedback.equals("ALLOWED") && trStatus.equals("MANUAL_PROCESSING")) {
            card.setMinLimit(increasedAllowed);
        } else if (feedback.equals("PROHIBITED") && trStatus.equals("MANUAL_PROCESSING")) {
            card.setMaxLimit(decreasedManual);
        } else if (feedback.equals("ALLOWED") && trStatus.equals("PROHIBITED")) {
            card.setMinLimit(increasedAllowed);
            card.setMaxLimit(increasedManual);
        } else if (feedback.equals("MANUAL_PROCESSING") && trStatus.equals("PROHIBITED")) {
            card.setMaxLimit(increasedManual);
        }

        cardService.Save(card);
    }

    public ResponseEntity<?> addFeedback(Feedback feedback) {
        Transaction transaction = transactionService.findTransactionById(feedback.getTransactionId());
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var isWrongFormat = Stream.of("ALLOWED",
                "MANUAL_PROCESSING",
                "PROHIBITED").anyMatch(el -> el.equals(feedback.getFeedback()));

        if (!isWrongFormat) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (transaction.getResult().equals(feedback.getFeedback())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!transaction.getFeedback().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }


        transaction.setFeedback(feedback.getFeedback());
        transactionService.Save(transaction);
        changeLimit(transaction, feedback.getFeedback());
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
