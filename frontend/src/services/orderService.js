import api from "./api";

const ORDER_BASE_URL = "/api/v1/orders";

export function createOrder(orderData) {
    return api.post(ORDER_BASE_URL, orderData);
}

export function getOrderById(orderId) {
    return api.get(`${ORDER_BASE_URL}/${orderId}`);
}

export function listOrdersByCustomer(customerId) {
    return api.get(`${ORDER_BASE_URL}/customer/${customerId}`);
}

export function cancelOrder(orderId) {
    return api.patch(`${ORDER_BASE_URL}/${orderId}/cancel`);
}

export function dispatchOrder(orderId, deliverymanId) {
    return api.patch(`${ORDER_BASE_URL}/${orderId}/dispatch/${deliverymanId}`);
}

export function cancelRoute(orderId) {
    return api.patch(`${ORDER_BASE_URL}/${orderId}/cancel-route`);
}

export function startRoute(orderId) {
    return api.patch(`${ORDER_BASE_URL}/${orderId}/start-route`);
}

export function concludeOrder(orderId) {
    return api.patch(`${ORDER_BASE_URL}/${orderId}/conclude`);
}

export function getOrderQueue() {
    return api.get(`${ORDER_BASE_URL}/queue`);
}