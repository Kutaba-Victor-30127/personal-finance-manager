package ro.kutaba.finance.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionResponse create(@RequestBody CreateTransactionRequest request) {

        return transactionService.create(request);
    }

    @GetMapping
    public List<TransactionResponse> getAll() {
     
        return transactionService.getAll();
    }
    
    @PutMapping("/{id}")
    public TransactionResponse update(
            @PathVariable Long id, 
            @RequestBody CreateTransactionRequest request) {

        return transactionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        
        transactionService.delete(id);
    }

}
