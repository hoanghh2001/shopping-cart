import { changeAdminUserStatus, fetchAdminUser, searchAdminUsers } from "../api/admin.js";
import { API_BASE } from "../api/config.js";
import { checkLogin } from "../components/check-login.js";

const elements = {
  sidebar: document.getElementById("adminSidebar"),
  menuButton: document.querySelector('[data-action="open-sidebar"]'),
  filters: document.getElementById("userFilters"),
  keyword: document.getElementById("userKeyword"),
  reset: document.getElementById("resetUserFilters"),
  retry: document.getElementById("retryUsers"),
  error: document.getElementById("usersError"),
  loading: document.getElementById("usersLoading"),
  empty: document.getElementById("usersEmpty"),
  tableBody: document.getElementById("usersTableBody"),
  total: document.getElementById("totalUsers"),
  visible: document.getElementById("visibleUsers"),
  verified: document.getElementById("verifiedUsers"),
  restricted: document.getElementById("restrictedUsers"),
  paginationInfo: document.getElementById("userPaginationInfo"),
  pageIndicator: document.getElementById("userPageIndicator"),
  previousPage: document.getElementById("previousUserPage"),
  nextPage: document.getElementById("nextUserPage"),
  detailBackdrop: document.getElementById("userDetailBackdrop"),
  detail: document.getElementById("userDetail"),
  detailTitle: document.getElementById("userDetailTitle"),
  detailBody: document.getElementById("userDetailBody"),
  detailFooter: document.getElementById("userDetailFooter"),
  confirm: document.getElementById("userConfirm"),
  confirmForm: document.getElementById("userConfirmForm"),
  confirmTitle: document.getElementById("userConfirmTitle"),
  confirmMessage: document.getElementById("userConfirmMessage"),
  confirmButton: document.getElementById("confirmUserAction"),
  toast: document.getElementById("userToast"),
};

const state = {
  users: [],
  selectedUser: null,
  localStatuses: new Map(),
  page: 0,
  size: 15,
  totalElements: 0,
  totalPages: 1,
  requestId: 0,
  pendingAction: null,
};

document.addEventListener("DOMContentLoaded", () => {
  checkLogin();
  bindEvents();
  loadUsers();
});

function bindEvents() {
  elements.filters.addEventListener("submit", (event) => {
    event.preventDefault();
    state.page = 0;
    loadUsers();
  });
  elements.reset.addEventListener("click", () => {
    elements.filters.reset();
    state.page = 0;
    loadUsers();
  });
  elements.retry.addEventListener("click", loadUsers);
  elements.previousPage.addEventListener("click", () => {
    if (state.page === 0) return;
    state.page -= 1;
    loadUsers();
  });
  elements.nextPage.addEventListener("click", () => {
    if (state.page >= state.totalPages - 1) return;
    state.page += 1;
    loadUsers();
  });
  elements.detailBackdrop.addEventListener("click", closeDetail);
  elements.confirmForm.addEventListener("submit", executeUserAction);

  document.addEventListener("click", (event) => {
    const trigger = event.target.closest("[data-action]");
    if (!trigger) return;
    const action = trigger.dataset.action;
    if (action === "open-sidebar") openSidebar();
    if (action === "close-sidebar") closeSidebar();
    if (action === "open-user") openUser(Number(trigger.dataset.userId));
    if (action === "close-user-detail") closeDetail();
    if (action === "close-user-confirm") closeConfirm();
    if (["mark-banned", "mark-deleted", "mark-active"].includes(action)) {
      requestUserAction(Number(trigger.dataset.userId || state.selectedUser?.id), action);
    }
  });

  document.addEventListener("keydown", (event) => {
    if (event.key !== "Escape") return;
    closeSidebar();
    if (!elements.confirm.open) closeDetail();
  });
  window.addEventListener("resize", () => {
    if (window.innerWidth > 900) closeSidebar();
  });
}

async function loadUsers() {
  const requestId = ++state.requestId;
  setLoading(true);
  elements.error.hidden = true;

  try {
    const response = await searchAdminUsers({
      keyword: elements.keyword.value.trim(),
      page: state.page,
      size: state.size,
      sort: "createdAt,desc",
    });
    if (requestId !== state.requestId) return;

    const page = response?.page ?? response;
    state.users = response?.content ?? [];
    state.page = Number(page?.number ?? state.page);
    state.size = Number(page?.size ?? state.size);
    state.totalElements = Number(page?.totalElements ?? response?.totalElements ?? state.users.length);
    state.totalPages = Math.max(Number(page?.totalPages ?? response?.totalPages ?? 1), 1);
    renderUsers();
    renderSummary();
    renderPagination();
  } catch {
    if (requestId !== state.requestId) return;
    state.users = [];
    state.totalElements = 0;
    state.totalPages = 1;
    elements.error.hidden = false;
    renderUsers();
    renderSummary();
    renderPagination();
  } finally {
    if (requestId === state.requestId) setLoading(false);
  }
}

function renderUsers() {
  elements.tableBody.replaceChildren();
  elements.empty.hidden = state.users.length !== 0 || !elements.error.hidden;

  state.users.forEach((user) => {
    const status = getUserStatus(user);
    const row = document.createElement("tr");
    row.innerHTML = `
      <td class="user-name-cell">
        <button class="user-identity" type="button" data-action="open-user" data-user-id="${user.id}">
          <span class="user-avatar">${renderAvatar(user)}</span>
          <span><strong>${escapeHtml(getUserName(user))}</strong><small>#${user.id} · ${escapeHtml(user.email || "—")}</small></span>
        </button>
      </td>
      <td data-label="電話番号">${escapeHtml(user.phone || "—")}</td>
      <td data-label="認証"><span class="verification-status ${user.emailVerified ? "is-verified" : ""}">${user.emailVerified ? "認証済み" : "未認証"}</span></td>
      <td data-label="登録日">${formatDate(user.createdAt)}</td>
      <td data-label="ステータス"><span class="user-status user-status--${status.toLowerCase()}">${statusLabel(status)}</span></td>
      <td class="user-row-action"><button type="button" data-action="open-user" data-user-id="${user.id}">詳細</button></td>
    `;
    elements.tableBody.append(row);
  });
}

function renderSummary() {
  const verified = state.users.filter((user) => user.emailVerified).length;
  const restricted = state.users.filter((user) => getUserStatus(user) !== "ACTIVE").length;
  elements.total.textContent = formatNumber(state.totalElements);
  elements.visible.textContent = formatNumber(state.users.length);
  elements.verified.textContent = formatNumber(verified);
  elements.restricted.textContent = formatNumber(restricted);
}

function renderPagination() {
  const start = state.totalElements ? state.page * state.size + 1 : 0;
  const end = Math.min((state.page + 1) * state.size, state.totalElements);
  elements.paginationInfo.textContent = `${formatNumber(state.totalElements)}件中 ${formatNumber(start)}–${formatNumber(end)}件`;
  elements.pageIndicator.textContent = `${state.page + 1} / ${state.totalPages}`;
  elements.previousPage.disabled = state.page === 0;
  elements.nextPage.disabled = state.page >= state.totalPages - 1;
}

async function openUser(userId) {
  const fallback = state.users.find((user) => user.id === userId);
  if (!fallback) return;
  state.selectedUser = fallback;
  openDetail();
  renderDetailLoading(fallback);

  try {
    const detail = await fetchAdminUser(userId);
    state.selectedUser = { ...fallback, ...detail };
  } catch {
    state.selectedUser = fallback;
  }
  renderUserDetail(state.selectedUser);
}

function renderDetailLoading(user) {
  elements.detailTitle.textContent = getUserName(user);
  elements.detailBody.innerHTML = '<div class="management-loading"><span class="management-spinner"></span><span>ユーザー情報を読み込んでいます。</span></div>';
  elements.detailFooter.replaceChildren();
}

function renderUserDetail(user) {
  const status = getUserStatus(user);
  elements.detailTitle.textContent = getUserName(user);
  elements.detailBody.innerHTML = `
    <section class="user-detail-hero">
      <span class="user-detail-avatar">${renderAvatar(user)}</span>
      <div>
        <span class="user-status user-status--${status.toLowerCase()}">${statusLabel(status)}</span>
        <h3>${escapeHtml(getUserName(user))}</h3>
        <p>${escapeHtml(user.email || "—")}</p>
      </div>
    </section>
    <section class="user-detail-section">
      <h3>アカウント情報</h3>
      <dl class="user-detail-list">
        ${detailRow("ユーザーID", `#${user.id}`)}
        ${detailRow("メール認証", user.emailVerified ? "認証済み" : "未認証")}
        ${detailRow("電話番号", user.phone)}
        ${detailRow("性別", genderLabel(user.gender))}
        ${detailRow("生年月日", user.birthday)}
        ${detailRow("登録日", formatDateTime(user.createdAt))}
        ${detailRow("更新日", formatDateTime(user.updatedAt))}
        ${detailRow("削除日", formatDateTime(user.deletedAt))}
      </dl>
    </section>
  `;
  renderDetailActions(user, status);
}

function renderDetailActions(user, status) {
  elements.detailFooter.replaceChildren();
  if (status !== "ACTIVE") {
    elements.detailFooter.append(createActionButton("アカウントを有効化", "mark-active", user.id, "is-primary"));
    return;
  }
  elements.detailFooter.append(createActionButton("利用停止", "mark-banned", user.id, "is-warning"));
  elements.detailFooter.append(createActionButton("削除", "mark-deleted", user.id, "is-danger"));
}

function createActionButton(label, action, userId, className) {
  const button = document.createElement("button");
  button.type = "button";
  button.textContent = label;
  button.dataset.action = action;
  button.dataset.userId = userId;
  button.className = className;
  return button;
}

function requestUserAction(userId, action) {
  if (!userId) return;
  const actionMeta = {
    "mark-banned": { title: "アカウントを利用停止", message: "このユーザーを利用停止にします。", button: "利用停止する" },
    "mark-deleted": { title: "アカウントを削除", message: "このユーザーを削除状態にします。", button: "削除する" },
    "mark-active": { title: "アカウントを有効化", message: "このユーザーを有効な状態に戻します。", button: "有効化する" },
  }[action];
  state.pendingAction = { userId, action };
  elements.confirmTitle.textContent = actionMeta.title;
  elements.confirmMessage.textContent = actionMeta.message;
  elements.confirmButton.textContent = actionMeta.button;
  elements.confirmButton.classList.toggle("is-danger", action === "mark-deleted");
  elements.confirm.showModal();
}

async function executeUserAction(event) {
  event.preventDefault();
  if (!state.pendingAction) return;
  const { userId, action } = state.pendingAction;
  elements.confirmButton.disabled = true;
  try {
    await changeAdminUserStatus(userId, action);
    const status = action === "mark-active" ? "ACTIVE" : action === "mark-banned" ? "BANNED" : "DELETED";
    state.localStatuses.set(userId, status);
    closeConfirm();
    const user = state.users.find((item) => item.id === userId);
    if (user) renderUsers();
    if (state.selectedUser?.id === userId) renderUserDetail(state.selectedUser);
    renderSummary();
    showToast("アカウントの状態を更新しました。");
  } catch (error) {
    closeConfirm();
    showToast(error?.status === 403 ? "この操作を実行する権限がありません。" : "アカウントの状態を更新できませんでした。", true);
  } finally {
    elements.confirmButton.disabled = false;
  }
}

function getUserStatus(user) {
  return state.localStatuses.get(user.id) || (user.deletedAt || user.deleted ? "DELETED" : "ACTIVE");
}

function getUserName(user) {
  return user.fullName || [user.firstName, user.lastName].filter(Boolean).join(" ") || "名称未設定";
}

function renderAvatar(user) {
  if (user.avatarUrl) {
    return `<img src="${escapeHtml(resolveImageUrl(user.avatarUrl))}" alt="" onerror="this.parentElement.textContent='${escapeHtml(getInitials(user))}'" />`;
  }
  return escapeHtml(getInitials(user));
}

function getInitials(user) {
  return `${user.firstName?.[0] || ""}${user.lastName?.[0] || ""}`.toUpperCase() || "?";
}

function openDetail() {
  elements.detail.classList.add("is-open");
  elements.detail.inert = false;
  elements.detail.setAttribute("aria-hidden", "false");
  elements.detailBackdrop.hidden = false;
  document.body.classList.add("drawer-open");
}

function closeDetail() {
  elements.detail.classList.remove("is-open");
  elements.detail.inert = true;
  elements.detail.setAttribute("aria-hidden", "true");
  elements.detailBackdrop.hidden = true;
  document.body.classList.remove("drawer-open");
  state.selectedUser = null;
}

function closeConfirm() {
  if (elements.confirm.open) elements.confirm.close();
  state.pendingAction = null;
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

function setLoading(loading) {
  elements.loading.hidden = !loading;
}

function showToast(message, error = false) {
  elements.toast.textContent = message;
  elements.toast.classList.toggle("is-error", error);
  elements.toast.classList.add("is-visible");
  window.setTimeout(() => elements.toast.classList.remove("is-visible"), 2800);
}

function detailRow(label, value) {
  return `<div><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value || "—")}</dd></div>`;
}

function statusLabel(status) {
  return { ACTIVE: "有効", BANNED: "利用停止", DELETED: "削除済み" }[status] || status;
}

function genderLabel(gender) {
  return { MALE: "男性", FEMALE: "女性", OTHER: "その他" }[gender] || "未設定";
}

function formatDate(value) {
  const date = value ? new Date(value) : null;
  if (!date || Number.isNaN(date.getTime())) return "—";
  return new Intl.DateTimeFormat("ja-JP", { year: "numeric", month: "2-digit", day: "2-digit" }).format(date);
}

function formatDateTime(value) {
  const date = value ? new Date(value) : null;
  if (!date || Number.isNaN(date.getTime())) return "—";
  return new Intl.DateTimeFormat("ja-JP", {
    timeZone: "Asia/Tokyo",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

function formatNumber(value) {
  return new Intl.NumberFormat("ja-JP").format(Number(value) || 0);
}

function resolveImageUrl(path) {
  if (/^https?:\/\//i.test(path || "")) return path;
  return `${API_BASE}${String(path).startsWith("/") ? "" : "/"}${path}`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
