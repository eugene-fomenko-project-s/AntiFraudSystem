package antifraud.Config.Controller;

import antifraud.Models.Entity.Transaction;
import antifraud.Service.RepoService.IPService;
import antifraud.Service.RepoService.TransactionService;
import antifraud.Service.Utils.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("/api/antifraud/")
public class HistoryController {
    @Autowired
    IPService ipService;
    Support support = new Support();
    @Autowired
    TransactionService transactionService;
    @GetMapping("/history")
    public ResponseEntity<List<?>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<?> getTransaction(@PathVariable String number) {
        if (!support.check_card(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Transaction> transaction = transactionService.findTransactionByNumber(number);
        if (transaction.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
