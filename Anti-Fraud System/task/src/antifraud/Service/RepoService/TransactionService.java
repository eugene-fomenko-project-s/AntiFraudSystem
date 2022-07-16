package antifraud.Service.RepoService;

import antifraud.Models.Entity.Transaction;
import antifraud.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public List<Transaction> findTransactionByNumber(String number) {
        List<Transaction> list_transaction = transactionRepository.findAll();
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : list_transaction) {
            if (Objects.equals(transaction.getNumber(), number)) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    public Transaction findTransactionById(Long id) {
        List<Transaction> list_transaction = transactionRepository.findAll();
        for (Transaction transaction : list_transaction) {
            if (Objects.equals(transaction.getTransactionId(), id)) {
                return transaction;
            }
        }
        return null;
    }

    public void Save(Transaction transaction) {
        transactionRepository.saveAndFlush(transaction);
    }

    public void Delete(String number) {
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            if (Objects.equals(transaction.getNumber(), number)) {
                transactionRepository.deleteById(transaction.getTransactionId());
            }
        }
    }

    public void Delete(long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public Long getTransactionsWithDistinctRegionCount(String region,
                                                       String number,
                                                       LocalDateTime start,
                                                       LocalDateTime end) {
        return transactionRepository.getTransactionsWithDistinctRegionCount(region,
                number,
                start,
                end);
    }

    public Long getTransactionsWithDistinctIpCount(String ip,
                                                   String number,
                                                   LocalDateTime start,
                                                   LocalDateTime end){
        return transactionRepository.getTransactionsWithDistinctIpCount(ip,
                number,
                start,
                end);
    }
}
