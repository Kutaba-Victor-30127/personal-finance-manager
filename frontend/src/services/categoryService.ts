import api from "./api";
import type { Category } from "../types/category";

export const getCategories = async (): Promise<Category[]> => {

    const response = await api.get<Category[]>("/categories");

    return response.data;
}

export const createCategory = async (
    name: string
): Promise<Category> => {

    const response = await api.post<Category>(
        "/categories",
        {
            name: name,
        }
    );

    return response.data;
}

export const deleteCategory = async (
    id: number
): Promise<void> => {

    await api.delete(
        `/categories/${id}`
    );
};