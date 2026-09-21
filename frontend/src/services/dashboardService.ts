import api from "./api";
import type { DashboardResponse } from "../types/dashboard";

export type DashboardFilters = {
    startDate?: string;
    endDate?: string;
    categoryId?: number;
};

export type CategorySummaryResponse = {
    category: string;
    total: number;
    transactionCount: number;
};

export type GroupBy = "DAY" | "WEEK" | "MONTH"

export type PeriodSummaryResponse = {
    periodStart: string;
    income: number;
    expenses: number;
};

export const getDashboard = async (
    filters: DashboardFilters = {}
): Promise<DashboardResponse> => {
  
    const response = await api.get<DashboardResponse>(
        "/dashboard",
        {
            params: filters,
        }
    );
  
    return response.data;
};

export const getCategorySummary = async (
    filters: DashboardFilters = {}
): Promise<CategorySummaryResponse[]> => {

    const response = await api.get<CategorySummaryResponse[]>(
        "/dashboard/category-summary",
        {
            params: filters,
        }
    );

    return response.data;
};

export const getPeriodSummary = async (
    filters: DashboardFilters = {},
    groupBy: GroupBy
): Promise<PeriodSummaryResponse[]> => {

    const response = await api.get<PeriodSummaryResponse[]>(
        "/dashboard/period-summary",
        {
            params: {
                ...filters,
                groupBy,
            },
        }
    );

    return response.data;
};
