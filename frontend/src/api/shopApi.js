import { apiClient } from "./client";

export async function fetchProducts(params = {}) {
    const response = await apiClient.get("/products", { params });
    return response.data;
}