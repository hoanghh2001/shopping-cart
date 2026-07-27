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
  const responseText = await response.text();
  return responseText ? JSON.parse(responseText) : null;
}

function toQueryString(filters = {}) {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.set(key, String(value));
    }
  });

  return params.toString();
}

export function fetchAdminProducts(filters = {}) {
  return adminRequest(`/api/admin/products?${toQueryString(filters)}`);
}

export function fetchAdminProduct(productId) {
  return adminRequest(`/api/admin/products/${encodeURIComponent(productId)}`);
}

export function createAdminProduct(payload) {
  return adminRequest("/api/admin/products", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function updateAdminProduct(productId, payload) {
  return adminRequest(`/api/admin/products/${encodeURIComponent(productId)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function changeAdminProductArchive(ids, action) {
  return adminRequest(`/api/admin/products/${action}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ ids }),
  });
}

export function fetchAdminProductColors(productId) {
  return adminRequest(`/api/admin/products/${encodeURIComponent(productId)}/colors`);
}

export function fetchAdminColorVariants(colorId) {
  return adminRequest(`/api/admin/colors/${encodeURIComponent(colorId)}/variants`);
}

export function updateAdminVariant(variantId, payload) {
  return adminRequest(`/api/admin/variants/${encodeURIComponent(variantId)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function fetchAdminColorImages(colorId, filters = {}) {
  return adminRequest(
    `/api/admin/images?${toQueryString({
      id: colorId,
      status: "ACTIVE",
      page: 0,
      size: 100,
      ...filters,
    })}`,
  );
}

export function uploadAdminColorImages(colorId, files) {
  const body = new FormData();
  const metadata = files.map((file, index) => ({
    altText: file.name.replace(/\.[^.]+$/, ""),
    sortOrder: index,
  }));

  files.forEach((file) => body.append("files", file));
  body.append("metadata", new Blob([JSON.stringify(metadata)], { type: "application/json" }));

  return adminRequest(`/api/admin/colors/${encodeURIComponent(colorId)}/images`, {
    method: "POST",
    body,
  });
}

export function deleteAdminColorImage(imageId) {
  return adminRequest(`/api/admin/images/${encodeURIComponent(imageId)}/delete`, {
    method: "PATCH",
  });
}

export function fetchAdminBrands(filters = {}) {
  return adminRequest(`/api/admin/brands?${toQueryString(filters)}`);
}

export function fetchAdminCategories(filters = {}) {
  return adminRequest(`/api/admin/categories?${toQueryString(filters)}`);
}

export function fetchAdminBrand(brandId) {
  return adminRequest(`/api/admin/brands/${encodeURIComponent(brandId)}`);
}

export function createAdminBrand(payload) {
  return adminRequest("/api/admin/brands", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function updateAdminBrand(brandId, payload) {
  return adminRequest(`/api/admin/brands/${encodeURIComponent(brandId)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function changeAdminBrandStatus(brandId, action) {
  return adminRequest(`/api/admin/brands/${encodeURIComponent(brandId)}/${action}`, {
    method: "PATCH",
  });
}

export function fetchAdminTags(filters = {}) {
  return adminRequest(`/api/admin/tags?${toQueryString(filters)}`);
}

export function fetchAdminTag(tagId) {
  return adminRequest(`/api/admin/tags/${encodeURIComponent(tagId)}`);
}

export function createAdminTag(payload) {
  return adminRequest("/api/admin/tags", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function updateAdminTag(tagId, payload) {
  return adminRequest(`/api/admin/tags/${encodeURIComponent(tagId)}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
}

export function changeAdminTagsStatus(ids, action) {
  return adminRequest(`/api/admin/tags/${action}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(ids),
  });
}

export function searchAdminUsers(filters = {}) {
  return adminRequest(`/api/admin/users/search?${toQueryString(filters)}`);
}

export function fetchAdminUser(userId) {
  return adminRequest(`/api/admin/users/${encodeURIComponent(userId)}`);
}

export function changeAdminUserStatus(userId, action) {
  return adminRequest(`/api/admin/users/${encodeURIComponent(userId)}/${action}`, {
    method: "PATCH",
  });
}

export function fetchAdminOrders(filters = {}) {
  return adminRequest(`/api/admin/orders?${toQueryString(filters)}`);
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
