package ro.kutaba.finance.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.Valid;

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
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {

        return transactionService.create(request);
    }

    @GetMapping
    public Page<TransactionResponse> getAll(@RequestParam(defaultValue = "0") int page, 
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "date") String sortBy,
                                            @RequestParam(defaultValue = "desc") String sortDir,
                                            TransactionFilter filter) {
     
        return transactionService.getAll(page, size, sortBy, sortDir, filter);
    }
    
    @PutMapping("/{id}")
    public TransactionResponse update(
            @PathVariable Long id,
            @Valid 
            @RequestBody CreateTransactionRequest request) {

        return transactionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        
        transactionService.delete(id);
    }

}
