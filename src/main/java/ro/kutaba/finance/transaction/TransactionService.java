package ro.kutaba.finance.transaction;

import java.util.List;

public interface TransactionService {

    TransactionResponse create(CreateTransactionRequest request);

    List<TransactionResponse> getAll();

    TransactionResponse update(Long id, CreateTransactionRequest request);

    void delete(Long id);
}
