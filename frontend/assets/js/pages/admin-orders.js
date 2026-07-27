import {
  fetchAdminOrder,
  fetchAdminOrderHistory,
  fetchAdminOrderItems,
  fetchAdminOrders,
  updateAdminOrderStatus,
} from "../api/admin.js";
import { API_BASE } from "../api/config.js";
import { checkLogin } from "../components/check-login.js";

const ORDER_STATUS = {
  CREATED: { label: "新規", tone: "new" },
  CONFIRMED: { label: "確認済み", tone: "confirmed" },
  PACKING: { label: "梱包中", tone: "packing" },
  SHIPPING: { label: "配送中", tone: "shipping" },
  DELIVERED: { label: "配達完了", tone: "delivered" },
  CANCELLED: { label: "キャンセル", tone: "cancelled" },
};

const PAYMENT_STATUS = {
  PENDING: { label: "支払い待ち", tone: "pending" },
  COMPLETED: { label: "支払い済み", tone: "completed" },
  FAILED: { label: "失敗", tone: "failed" },
  REFUNDED: { label: "返金済み", tone: "refunded" },
};

const PAYMENT_METHOD = {
  CASH: "代金引換",
  RAKUTEN_PAY: "楽天ペイ",
  PAYPAY: "PayPay",
  CREDIT_CARD: "クレジットカード",
  BANK_TRANSFER: "銀行振込",
};

const NEXT_ACTION = {
  CREATED: { action: "confirm", label: "注文を確認" },
  CONFIRMED: { action: "packing", label: "梱包を開始" },
  PACKING: { action: "shipping", label: "配送を開始" },
  SHIPPING: { action: "delivered", label: "配達完了にする" },
};

const elements = {
  sidebar: document.getElementById("adminSidebar"),
  menuButton: document.querySelector('[data-action="open-sidebar"]'),
  filters: document.getElementById("ordersFilters"),
  keyword: document.getElementById("orderKeyword"),
  orderStatus: document.getElementById("orderStatus"),
  paymentStatus: document.getElementById("paymentStatus"),
  fromDate: document.getElementById("fromDate"),
  toDate: document.getElementById("toDate"),
  resetFilters: document.getElementById("resetFilters"),
  refresh: document.getElementById("refreshOrders"),
  retry: document.getElementById("retryOrders"),
  error: document.getElementById("ordersError"),
  loading: document.getElementById("ordersLoading"),
  empty: document.getElementById("ordersEmpty"),
  tableBody: document.getElementById("ordersTableBody"),
  totalOrders: document.getElementById("totalOrders"),
  visibleOrders: document.getElementById("visibleOrders"),
  pendingOrders: document.getElementById("pendingOrders"),
  visibleRevenue: document.getElementById("visibleRevenue"),
  paginationInfo: document.getElementById("paginationInfo"),
  pageIndicator: document.getElementById("pageIndicator"),
  previousPage: document.getElementById("previousPage"),
  nextPage: document.getElementById("nextPage"),
  drawer: document.getElementById("orderDrawer"),
  drawerBackdrop: document.getElementById("orderDrawerBackdrop"),
  drawerTitle: document.getElementById("drawerTitle"),
  drawerBody: document.getElementById("orderDrawerBody"),
  drawerFooter: document.getElementById("orderDrawerFooter"),
  cancelDialog: document.getElementById("cancelDialog"),
  cancelForm: document.getElementById("cancelForm"),
  cancelReason: document.getElementById("cancelReason"),
  cancelError: document.getElementById("cancelError"),
  confirmCancel: document.getElementById("confirmCancel"),
  toast: document.getElementById("adminToast"),
};

const state = {
  orders: [],
  page: 0,
  size: 10,
  totalPages: 1,
  totalElements: 0,
  selectedOrder: null,
  selectedOrderId: null,
  requestId: 0,
};

document.addEventListener("DOMContentLoaded", () => {
  checkLogin();
  bindEvents();
  loadOrders();
});

function bindEvents() {
  elements.filters.addEventListener("submit", (event) => {
    event.preventDefault();
    state.page = 0;
    loadOrders();
  });

  elements.resetFilters.addEventListener("click", resetFilters);
  elements.refresh.addEventListener("click", loadOrders);
  elements.retry.addEventListener("click", loadOrders);

  elements.previousPage.addEventListener("click", () => {
    if (state.page <= 0) return;
    state.page -= 1;
    loadOrders();
  });

  elements.nextPage.addEventListener("click", () => {
    if (state.page >= state.totalPages - 1) return;
    state.page += 1;
    loadOrders();
  });

  elements.drawerBackdrop.addEventListener("click", closeDrawer);

  document.addEventListener("click", (event) => {
    const actionElement = event.target.closest("[data-action]");
    if (!actionElement) return;

    switch (actionElement.dataset.action) {
      case "open-sidebar":
        openSidebar();
        break;
      case "close-sidebar":
        closeSidebar();
        break;
      case "clear-filters":
        resetFilters();
        break;
      case "view-order":
        openOrderByIndex(Number(actionElement.dataset.index));
        break;
      case "close-drawer":
        closeDrawer();
        break;
      case "copy-order":
        copyOrderNumber(actionElement.dataset.orderNumber);
        break;
      case "advance-status":
        advanceSelectedOrder(actionElement.dataset.statusAction);
        break;
      case "open-cancel":
        openCancelDialog();
        break;
      case "close-cancel":
        closeCancelDialog();
        break;
    }
  });

  elements.cancelForm.addEventListener("submit", (event) => {
    event.preventDefault();
    cancelSelectedOrder();
  });

  document.addEventListener("keydown", (event) => {
    if (event.key !== "Escape") return;
    closeSidebar();
    if (!elements.cancelDialog.open) closeDrawer();
  });

  window.addEventListener("resize", () => {
    if (window.innerWidth > 900) closeSidebar();
  });
}

async function loadOrders() {
  const currentRequest = ++state.requestId;
  setLoading(true);
  hideError();

  try {
    const response = await fetchAdminOrders(buildFilters());
    if (currentRequest !== state.requestId) return;

    const page = response?.page ?? response;
    state.orders = Array.isArray(response) ? response : response?.content ?? [];
    state.page = Number(page?.number ?? state.page);
    state.size = Number(page?.size ?? state.size);
    state.totalPages = Math.max(Number(page?.totalPages ?? 1), 1);
    state.totalElements = Number(page?.totalElements ?? state.orders.length);

    renderOrders();
    renderSummary();
    renderPagination();
  } catch {
    if (currentRequest !== state.requestId) return;
    state.orders = [];
    renderOrders();
    renderSummary();
    renderPagination();
    showError();
  } finally {
    if (currentRequest === state.requestId) setLoading(false);
  }
}

function buildFilters() {
  return {
    page: state.page,
    size: state.size,
    keyword: elements.keyword.value.trim(),
    orderStatus: elements.orderStatus.value,
    paymentStatus: elements.paymentStatus.value,
    fromDate: toInstant(elements.fromDate.value, false),
    toDate: toInstant(elements.toDate.value, true),
  };
}

function renderOrders() {
  elements.tableBody.replaceChildren();
  elements.empty.hidden = state.orders.length !== 0 || !elements.error.hidden;

  state.orders.forEach((order, index) => {
    const row = document.createElement("tr");
    const orderNumber = order.orderNumber || "—";
    const status = ORDER_STATUS[order.orderStatus] ?? { label: order.orderStatus || "未設定", tone: "neutral" };
    const payment = PAYMENT_STATUS[order.paymentStatus] ?? { label: order.paymentStatus || "未設定", tone: "neutral" };

    row.innerHTML = `
      <td data-label="注文番号">
        <button class="order-number" type="button" data-action="view-order" data-index="${index}">
          <strong>${escapeHtml(orderNumber)}</strong>
          <span>${getItemCount(order)}点</span>
        </button>
      </td>
      <td data-label="受取人">
        <div class="customer-cell">
          <strong>${escapeHtml(order.name || "—")}</strong>
          <span>${escapeHtml(order.phone || `ユーザーID: ${order.userId ?? "—"}`)}</span>
        </div>
      </td>
      <td data-label="注文状況"><span class="status-badge status-badge--${status.tone}">${status.label}</span></td>
      <td data-label="支払い"><span class="payment-badge payment-badge--${payment.tone}">${payment.label}</span></td>
      <td data-label="合計"><strong>${formatYen(order.grandTotal)}</strong></td>
      <td data-label="注文日時"><time datetime="${escapeHtml(order.placedAt || "")}">${formatDateTime(order.placedAt)}</time></td>
      <td class="order-action-cell">
        <button class="row-action" type="button" data-action="view-order" data-index="${index}" aria-label="${escapeHtml(orderNumber)}の詳細を見る">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m9 18 6-6-6-6" /></svg>
        </button>
      </td>
    `;

    elements.tableBody.append(row);
  });
}

function renderSummary() {
  const pending = state.orders.filter((order) => ["CREATED", "CONFIRMED"].includes(order.orderStatus)).length;
  const revenue = state.orders
    .filter((order) => order.orderStatus !== "CANCELLED")
    .reduce((sum, order) => sum + toNumber(order.grandTotal), 0);

  elements.totalOrders.textContent = formatNumber(state.totalElements);
  elements.visibleOrders.textContent = formatNumber(state.orders.length);
  elements.pendingOrders.textContent = formatNumber(pending);
  elements.visibleRevenue.textContent = formatYen(revenue);
}

function renderPagination() {
  const start = state.totalElements === 0 ? 0 : state.page * state.size + 1;
  const end = Math.min((state.page + 1) * state.size, state.totalElements);

  elements.paginationInfo.textContent = `${formatNumber(state.totalElements)}件中 ${formatNumber(start)}–${formatNumber(end)}件`;
  elements.pageIndicator.textContent = `${state.page + 1} / ${state.totalPages}`;
  elements.previousPage.disabled = state.page <= 0;
  elements.nextPage.disabled = state.page >= state.totalPages - 1;
}

async function openOrderByIndex(index) {
  const order = state.orders[index];
  if (!order) return;

  state.selectedOrder = order;
  state.selectedOrderId = getOrderId(order);
  openDrawer();
  renderDrawerLoading(order);

  if (!state.selectedOrderId) {
    renderOrderDetail(order, order.items ?? [], []);
    return;
  }

  const [detailResult, itemsResult, historyResult] = await Promise.allSettled([
    fetchAdminOrder(state.selectedOrderId),
    fetchAdminOrderItems(state.selectedOrderId),
    fetchAdminOrderHistory(state.selectedOrderId),
  ]);

  const detail = detailResult.status === "fulfilled" ? detailResult.value : order;
  const fetchedItems = itemsResult.status === "fulfilled" && Array.isArray(itemsResult.value) ? itemsResult.value : [];
  const items = fetchedItems.length ? fetchedItems : detail.items?.length ? detail.items : order.items ?? [];
  const history = historyResult.status === "fulfilled" ? historyResult.value : [];

  state.selectedOrder = { ...order, ...detail };
  renderOrderDetail(state.selectedOrder, items, history);
}

function renderDrawerLoading(order) {
  elements.drawerTitle.textContent = order.orderNumber || "注文詳細";
  elements.drawerBody.innerHTML = `
    <div class="drawer-loading">
      <span class="orders-spinner" aria-hidden="true"></span>
      <span>詳細を読み込んでいます。</span>
    </div>
  `;
  elements.drawerFooter.replaceChildren();
}

function renderOrderDetail(order, items, history) {
  const status = ORDER_STATUS[order.orderStatus] ?? { label: order.orderStatus || "未設定", tone: "neutral" };
  const payment = PAYMENT_STATUS[order.paymentStatus] ?? { label: order.paymentStatus || "未設定", tone: "neutral" };
  const orderNumber = order.orderNumber || "—";

  elements.drawerTitle.textContent = orderNumber;
  elements.drawerBody.innerHTML = `
    <section class="detail-hero">
      <div>
        <span class="status-badge status-badge--${status.tone}">${status.label}</span>
        <span class="payment-badge payment-badge--${payment.tone}">${payment.label}</span>
      </div>
      <button type="button" data-action="copy-order" data-order-number="${escapeHtml(orderNumber)}">注文番号をコピー</button>
    </section>

    <section class="detail-section">
      <h3>お客様・配送先</h3>
      <dl class="detail-list">
        ${detailRow("受取人", order.name)}
        ${detailRow("電話番号", order.phone)}
        ${detailRow("郵便番号", order.postalCode)}
        ${detailRow("配送先", order.fullAddress)}
        ${detailRow("注文日時", formatDateTime(order.placedAt))}
      </dl>
    </section>

    <section class="detail-section">
      <h3>注文商品 <span>${formatNumber(items.length)}点</span></h3>
      <div class="detail-items">
        ${items.length ? items.map(renderOrderItem).join("") : '<p class="detail-empty">商品情報がありません。</p>'}
      </div>
    </section>

    <section class="detail-section">
      <h3>お支払い</h3>
      <dl class="detail-list detail-totals">
        ${detailRow("商品小計", formatYen(order.subtotalAmount))}
        ${detailRow("割引", `−${formatYen(order.discountAmount)}`)}
        ${detailRow("送料", formatYen(order.shippingFee))}
        ${detailRow("税金", formatYen(order.taxAmount))}
        ${detailRow("合計", formatYen(order.grandTotal), true)}
        ${detailRow("支払い方法", PAYMENT_METHOD[order.paymentMethod] || order.paymentMethod || "—")}
      </dl>
    </section>

    <section class="detail-section">
      <h3>ステータス履歴</h3>
      <ol class="order-timeline">
        ${history.length ? history.map(renderHistoryItem).join("") : renderFallbackTimeline(order)}
      </ol>
    </section>

    ${
      order.note
        ? `<section class="detail-section"><h3>注文メモ</h3><p class="order-note">${escapeHtml(order.note)}</p></section>`
        : ""
    }
  `;

  renderDrawerActions(order);
}

function renderDrawerActions(order) {
  elements.drawerFooter.replaceChildren();
  const nextAction = NEXT_ACTION[order.orderStatus];
  const canMutate = Boolean(state.selectedOrderId);

  if (!canMutate && !["DELIVERED", "CANCELLED"].includes(order.orderStatus)) {
    const notice = document.createElement("p");
    notice.className = "order-contract-notice";
    notice.textContent = "更新に必要な注文IDがAPIレスポンスに含まれていません。";
    elements.drawerFooter.append(notice);
  }

  if (order.orderStatus !== "DELIVERED" && order.orderStatus !== "CANCELLED") {
    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.className = "drawer-action is-secondary is-danger";
    cancelButton.dataset.action = "open-cancel";
    cancelButton.textContent = "キャンセル";
    cancelButton.disabled = !canMutate;
    elements.drawerFooter.append(cancelButton);
  }

  if (nextAction) {
    const primaryButton = document.createElement("button");
    primaryButton.type = "button";
    primaryButton.className = "drawer-action is-primary";
    primaryButton.dataset.action = "advance-status";
    primaryButton.dataset.statusAction = nextAction.action;
    primaryButton.textContent = nextAction.label;
    primaryButton.disabled = !canMutate;
    elements.drawerFooter.append(primaryButton);
  }
}

async function advanceSelectedOrder(action) {
  if (!state.selectedOrderId || !action) return;
  setDrawerBusy(true);

  try {
    const updatedOrder = await updateAdminOrderStatus(state.selectedOrderId, action);
    state.selectedOrder = { ...state.selectedOrder, ...updatedOrder };
    updateOrderInList(state.selectedOrder);
    renderOrderDetail(state.selectedOrder, state.selectedOrder.items ?? [], []);
    renderOrders();
    renderSummary();
    showToast("注文ステータスを更新しました。");
  } catch {
    showToast("ステータスを更新できませんでした。", true);
  } finally {
    setDrawerBusy(false);
  }
}

function openCancelDialog() {
  if (!state.selectedOrderId) return;
  elements.cancelReason.value = "";
  elements.cancelError.hidden = true;
  elements.cancelDialog.showModal();
  elements.cancelReason.focus();
}

async function cancelSelectedOrder() {
  const reason = elements.cancelReason.value.trim();
  if (!reason) {
    elements.cancelError.textContent = "キャンセル理由を入力してください。";
    elements.cancelError.hidden = false;
    return;
  }

  elements.confirmCancel.disabled = true;
  elements.cancelError.hidden = true;

  try {
    const updatedOrder = await updateAdminOrderStatus(state.selectedOrderId, "cancel", reason);
    state.selectedOrder = { ...state.selectedOrder, ...updatedOrder };
    updateOrderInList(state.selectedOrder);
    closeCancelDialog();
    renderOrderDetail(state.selectedOrder, state.selectedOrder.items ?? [], []);
    renderOrders();
    renderSummary();
    showToast("注文をキャンセルしました。");
  } catch {
    elements.cancelError.textContent = "注文をキャンセルできませんでした。";
    elements.cancelError.hidden = false;
  } finally {
    elements.confirmCancel.disabled = false;
  }
}

function updateOrderInList(updatedOrder) {
  const updatedId = getOrderId(updatedOrder);
  const index = state.orders.findIndex((order) => {
    const orderId = getOrderId(order);
    return updatedId ? orderId === updatedId : order.orderNumber === updatedOrder.orderNumber;
  });

  if (index >= 0) state.orders[index] = { ...state.orders[index], ...updatedOrder };
}

function resetFilters() {
  elements.filters.reset();
  state.page = 0;
  loadOrders();
}

function openDrawer() {
  elements.drawer.classList.add("is-open");
  elements.drawer.setAttribute("aria-hidden", "false");
  elements.drawer.inert = false;
  elements.drawerBackdrop.hidden = false;
  document.body.classList.add("drawer-open");
}

function closeDrawer() {
  elements.drawer.classList.remove("is-open");
  elements.drawer.setAttribute("aria-hidden", "true");
  elements.drawer.inert = true;
  elements.drawerBackdrop.hidden = true;
  document.body.classList.remove("drawer-open");
  state.selectedOrder = null;
  state.selectedOrderId = null;
}

function closeCancelDialog() {
  if (elements.cancelDialog.open) elements.cancelDialog.close();
}

function openSidebar() {
  elements.sidebar.classList.add("is-open");
  document.body.classList.add("sidebar-open");
  elements.menuButton.setAttribute("aria-expanded", "true");
}

function closeSidebar() {
  elements.sidebar.classList.remove("is-open");
  document.body.classList.remove("sidebar-open");
  elements.menuButton.setAttribute("aria-expanded", "false");
}

function setLoading(isLoading) {
  elements.loading.hidden = !isLoading;
  elements.refresh.disabled = isLoading;
  elements.previousPage.disabled = isLoading || state.page <= 0;
  elements.nextPage.disabled = isLoading || state.page >= state.totalPages - 1;
}

function setDrawerBusy(isBusy) {
  elements.drawerFooter.querySelectorAll("button").forEach((button) => {
    button.disabled = isBusy;
  });
}

function showError() {
  elements.error.hidden = false;
  elements.empty.hidden = true;
}

function hideError() {
  elements.error.hidden = true;
}

function showToast(message, isError = false) {
  elements.toast.textContent = message;
  elements.toast.classList.toggle("is-error", isError);
  elements.toast.classList.add("is-visible");
  window.setTimeout(() => elements.toast.classList.remove("is-visible"), 2800);
}

async function copyOrderNumber(orderNumber) {
  if (!orderNumber || orderNumber === "—") return;

  try {
    await navigator.clipboard.writeText(orderNumber);
    showToast("注文番号をコピーしました。");
  } catch {
    showToast("コピーできませんでした。", true);
  }
}

function renderOrderItem(item) {
  const imageUrl = resolveImageUrl(item.imageUrl);
  return `
    <article class="detail-item">
      <img src="${escapeHtml(imageUrl)}" alt="" onerror="this.src='/assets/images/default/no-image.png'" />
      <div>
        <strong>${escapeHtml(item.productName || item.sku || "商品")}</strong>
        <span>${escapeHtml([item.colorName, item.sizeName].filter(Boolean).join(" / ") || "バリエーションなし")}</span>
        <small>${formatYen(item.unitPriceAtOrder)} × ${formatNumber(item.quantity)}</small>
      </div>
      <strong>${formatYen(item.lineTotal)}</strong>
    </article>
  `;
}

function renderHistoryItem(history) {
  const status = ORDER_STATUS[history.newStatus]?.label || history.newStatus || "更新";
  return `
    <li>
      <span aria-hidden="true"></span>
      <div>
        <strong>${escapeHtml(status)}</strong>
        <small>${formatDateTime(history.changedAt)}${history.note ? ` · ${escapeHtml(history.note)}` : ""}</small>
      </div>
    </li>
  `;
}

function renderFallbackTimeline(order) {
  const statusOrder = ["CREATED", "CONFIRMED", "PACKING", "SHIPPING", "DELIVERED"];
  const currentIndex = statusOrder.indexOf(order.orderStatus);

  if (order.orderStatus === "CANCELLED") {
    return `<li class="is-complete"><span aria-hidden="true"></span><div><strong>キャンセル</strong><small>${formatDateTime(order.cancelledAt)}</small></div></li>`;
  }

  return statusOrder
    .map((status, index) => {
      const className = index <= currentIndex ? "is-complete" : "";
      return `<li class="${className}"><span aria-hidden="true"></span><div><strong>${ORDER_STATUS[status].label}</strong></div></li>`;
    })
    .join("");
}

function detailRow(label, value, isTotal = false) {
  return `<div class="${isTotal ? "is-total" : ""}"><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value ?? "—")}</dd></div>`;
}

function getOrderId(order) {
  return order?.id ?? order?.orderId ?? null;
}

function getItemCount(order) {
  if (Array.isArray(order.items)) {
    return order.items.reduce((total, item) => total + toNumber(item.quantity), 0);
  }
  return 0;
}

function toInstant(date, endOfDay) {
  if (!date) return "";
  const time = endOfDay ? "23:59:59.999" : "00:00:00.000";
  return new Date(`${date}T${time}+09:00`).toISOString();
}

function resolveImageUrl(path) {
  if (!path) return "/assets/images/default/no-image.png";
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE}${path.startsWith("/") ? "" : "/"}${path}`;
}

function formatYen(value) {
  return new Intl.NumberFormat("ja-JP", {
    style: "currency",
    currency: "JPY",
    maximumFractionDigits: 0,
  }).format(toNumber(value));
}

function formatNumber(value) {
  return new Intl.NumberFormat("ja-JP").format(toNumber(value));
}

function formatDateTime(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";

  return new Intl.DateTimeFormat("ja-JP", {
    timeZone: "Asia/Tokyo",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function toNumber(value) {
  const number = Number(value ?? 0);
  return Number.isFinite(number) ? number : 0;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
