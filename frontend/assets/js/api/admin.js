import { API_BASE } from "../api/config.js";
export async function fetchOverview(range) {
  const res = await fetch(`${API_BASE}/api/admin/dashboard/overview?range=${encodeURIComponent(range)}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

  if (!res.ok) {
    const errorBody = await res.json().catch(() => null);
    throw new Error(errorBody?.message || `DASHBOARD_API_ERROR_${res.status}`);
  }

  return res.json();
}
export async function fetchAdminProducts() {
  try {
    const res = await fetch(`${API_BASE}/api/admin/products`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    if (!res.ok) throw new Error(data?.message || "API_ERROR");
    return await res.json();
  } catch (error) {
    console.log(error);
  }
}

async function adminRequest(path, options = {}) {
  const headers = {
    Accept: "application/json",
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    const error = new Error(errorBody?.message || `ADMIN_API_ERROR_${response.status}`);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) return null;
  return response.json();
}

export function fetchAdminOrders(filters = {}) {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.set(key, String(value));
    }
  });

  return adminRequest(`/api/admin/orders?${params.toString()}`);
}

export function fetchAdminOrder(orderId) {
  return adminRequest(`/api/admin/orders/${encodeURIComponent(orderId)}`);
}

export function fetchAdminOrderItems(orderId) {
  return adminRequest(`/api/admin/orders/${encodeURIComponent(orderId)}/order-items`);
}

export function fetchAdminOrderHistory(orderId) {
  return adminRequest(`/api/admin/orders/${encodeURIComponent(orderId)}/order-status-histories`);
}

export function updateAdminOrderStatus(orderId, action, reason = "") {
  const options = {
    method: "PATCH",
  };

  if (action === "cancel") {
    options.headers = { "Content-Type": "text/plain;charset=UTF-8" };
    options.body = reason;
  }

  return adminRequest(`/api/admin/orders/${encodeURIComponent(orderId)}/${action}`, options);
}
