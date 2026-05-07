import { apiClient } from "./client";

export async function fetchProducts(params = {}) {
    const response = await apiClient.get("/products", { params });
    return response.data;
}

export async function fetchCategories() {
    const response = await apiClient.get("/categories");
    return response.data;
}

export async function fetchOrderByNumber(orderNumber) {
    const response = await apiClient.get(`/orders/${orderNumber}`);
    return response.data;
}

export async function createOrder(payload) {
    const response = await apiClient.post("/orders", payload);
    return response.data;
}