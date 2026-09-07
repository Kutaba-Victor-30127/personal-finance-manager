export type TransactionType = "INCOME" | "EXPENSE";

export interface Transaction {
    id: number;
    title: string;
    description: string;
    amount: number;
    date: string;
    type: TransactionType;
    categoryName: string;
}

export interface CreateTransactionRequest{
    title: string;
    description: string;
    amount: number;
    date: string;
    type: TransactionType;
    categoryId: number;
}