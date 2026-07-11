package ro.kutaba.finance.transaction;

import org.springframework.data.domain.Page;

public interface TransactionService {

    TransactionResponse create(CreateTransactionRequest request);

    Page<TransactionResponse> getAll(int page, int size, String sortBy, String sortDir, TransactionFilter filter);

    TransactionResponse update(Long id, CreateTransactionRequest request);

    void delete(Long id);
}
