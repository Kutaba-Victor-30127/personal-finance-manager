package ro.kutaba.finance.transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getCategory().getName()
        );
    }
    
}
