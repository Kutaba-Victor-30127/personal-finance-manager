import api from './api';

import type { Transaction, CreateTransactionRequest} from '../types/transaction';
import type { PageResponse } from '../types/page';

export type TransactionFilters = {
    type?: "INCOME" | "EXPENSE";
    categoryId?: number;
    startDate?: string;
    endDate?: string;
}

export const getTransactions = async (
    page = 0,
    size = 10,
    sortBy: "date" | "title" | "amount" = "date",
    sortDir: "asc" | "desc" = "desc",
    filters: TransactionFilters = {}
): Promise<PageResponse<Transaction>> => {

    const response = await api.get<PageResponse<Transaction>>(
        "/transactions",
        {
            params: {
                page,
                size,
                sortBy,
                sortDir,

                type: filters.type,
                categoryId: filters.categoryId,
                startDate: filters.startDate,
                endDate: filters.endDate,
            },
        }
    );

    return response.data;
};

export const createTransaction = async(
    request: CreateTransactionRequest
): Promise<Transaction> =>{

    const response = await api.post<Transaction>(
        "/transactions",
        request
    );

    return response.data;   

};

export const deleteTransaction = async(
    id: number
): Promise<void> =>{

    await api.delete(
        `/transactions/${id}`
    );
};

export const updateTransaction = async(
    id: number,
    request: CreateTransactionRequest
): Promise<Transaction> =>{

    const response = await api.put<Transaction>(
        `/transactions/${id}`,
        request
    );

    return response.data;
};
