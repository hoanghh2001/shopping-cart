import {
  changeAdminBrandStatus,
  changeAdminTagsStatus,
  createAdminBrand,
  createAdminTag,
  fetchAdminBrands,
  fetchAdminTags,
  updateAdminBrand,
  updateAdminTag,
} from "../api/admin.js";
import { API_BASE } from "../api/config.js";
import { checkLogin } from "../components/check-login.js";

const resource = document.body.dataset.resource === "tag" ? "tag" : "brand";
const isBrand = resource === "brand";
const labels = isBrand
  ? { singular: "ブランド", plural: "ブランド", create: "ブランドを追加" }
  : { singular: "タグ", plural: "タグ", create: "タグを追加" };

const elements = {
  sidebar: document.getElementById("adminSidebar"),
  menuButton: document.querySelector('[data-action="open-sidebar"]'),
  form: document.getElementById("managementFilters"),
  keyword: document.getElementById("managementKeyword"),
  status: document.getElementById("managementStatus"),
  reset: document.getElementById("resetManagementFilters"),
  retry: document.getElementById("retryManagement"),
  error: document.getElementById("managementError"),
  loading: document.getElementById("managementLoading"),
  empty: document.getElementById("managementEmpty"),
  tableBody: document.getElementById("managementTableBody"),
  selectAll: document.getElementById("selectAllManagement"),
  bulkBar: document.getElementById("managementBulkBar"),
  selectedCount: document.getElementById("selectedManagementCount"),
  total: document.getElementById("totalManagement"),
  visible: document.getElementById("visibleManagement"),
  active: document.getElementById("activeManagement"),
  deleted: document.getElementById("deletedManagement"),
  paginationInfo: document.getElementById("managementPaginationInfo"),
  pageIndicator: document.getElementById("managementPageIndicator"),
  previousPage: document.getElementById("previousManagementPage"),
  nextPage: document.getElementById("nextManagementPage"),
  editor: document.getElementById("managementEditor"),
  editorForm: document.getElementById("managementEditorForm"),
  editorTitle: document.getElementById("managementEditorTitle"),
  editingId: document.getElementById("editingManagementId"),
  name: document.getElementById("managementName"),
  slug: document.getElementById("managementSlug"),
  description: document.getElementById("managementDescription"),
  logoField: document.getElementById("managementLogoField"),
  logo: document.getElementById("managementLogo"),
  editorStatusField: document.getElementById("managementEditorStatusField"),
  editorStatus: document.getElementById("managementEditorStatus"),
  editorError: document.getElementById("managementEditorError"),
  save: document.getElementById("saveManagement"),
  confirm: document.getElementById("managementConfirm"),
  confirmForm: document.getElementById("managementConfirmForm"),
  confirmTitle: document.getElementById("managementConfirmTitle"),
  confirmMessage: document.getElementById("managementConfirmMessage"),
  confirmButton: document.getElementById("confirmManagementAction"),
  toast: document.getElementById("managementToast"),
};

const state = {
  items: [],
  selectedIds: new Set(),
  page: 0,
  size: 15,
  hasNext: false,
  requestId: 0,
  pendingAction: null,
};

document.addEventListener("DOMContentLoaded", () => {
  checkLogin();
  elements.logoField.hidden = !isBrand;
  bindEvents();
  loadItems();
});

function bindEvents() {
  elements.form.addEventListener("submit", (event) => {
    event.preventDefault();
    state.page = 0;
    loadItems();
  });
  elements.reset.addEventListener("click", resetFilters);
  elements.retry.addEventListener("click", loadItems);
  elements.previousPage.addEventListener("click", () => {
    if (state.page === 0) return;
    state.page -= 1;
    loadItems();
  });
  elements.nextPage.addEventListener("click", () => {
    if (!state.hasNext) return;
    state.page += 1;
    loadItems();
  });
  elements.selectAll.addEventListener("change", () => {
    state.items.forEach((item) => {
      if (elements.selectAll.checked) state.selectedIds.add(item.id);
      else state.selectedIds.delete(item.id);
    });
    renderSelection();
  });
  elements.tableBody.addEventListener("change", (event) => {
    const checkbox = event.target.closest("[data-management-select]");
    if (!checkbox) return;
    const id = Number(checkbox.dataset.managementSelect);
    if (checkbox.checked) state.selectedIds.add(id);
    else state.selectedIds.delete(id);
    renderSelection();
  });
  elements.editorForm.addEventListener("submit", saveItem);
  elements.confirmForm.addEventListener("submit", executeStatusChange);

  document.addEventListener("click", (event) => {
    const trigger = event.target.closest("[data-action]");
    if (!trigger) return;
    const action = trigger.dataset.action;
    if (action === "open-sidebar") openSidebar();
    if (action === "close-sidebar") closeSidebar();
    if (action === "open-create") openEditor();
    if (action === "edit-management") openEditor(Number(trigger.dataset.itemId));
    if (action === "close-editor") closeEditor();
    if (action === "close-confirm") closeConfirm();
    if (action === "clear-filters") resetFilters();
    if (action === "bulk-delete") requestStatusChange([...state.selectedIds], "delete");
    if (action === "bulk-restore") requestStatusChange([...state.selectedIds], "restore");
    if (action === "delete-management") requestStatusChange([Number(trigger.dataset.itemId)], "delete");
    if (action === "restore-management") requestStatusChange([Number(trigger.dataset.itemId)], "restore");
  });

  document.addEventListener("keydown", (event) => {
    if (event.key !== "Escape") return;
    closeSidebar();
    closeEditor();
    closeConfirm();
  });
  window.addEventListener("resize", () => {
    if (window.innerWidth > 900) closeSidebar();
  });
}

async function loadItems() {
  const requestId = ++state.requestId;
  setLoading(true);
  elements.error.hidden = true;

  try {
    const brandKeyword = isBrand ? elements.keyword.value.trim() : "";
    const filters = {
      page: brandKeyword ? 0 : state.page,
      size: brandKeyword ? 200 : state.size,
      status: elements.status.value,
      sort: "createdAt,desc",
    };
    if (!isBrand) filters.keyword = elements.keyword.value.trim();

    const response = isBrand ? await fetchAdminBrands(filters) : await fetchAdminTags(filters);
    if (requestId !== state.requestId) return;

    let items = getContent(response);
    if (brandKeyword) {
      const keyword = brandKeyword.toLocaleLowerCase();
      items = items.filter((item) => `${item.name} ${item.slug}`.toLocaleLowerCase().includes(keyword));
    }

    state.items = items;
    state.page = Number(response?.number ?? response?.page?.number ?? (brandKeyword ? 0 : state.page));
    state.hasNext = response?.last === false;
    state.selectedIds.clear();
    renderItems();
    renderSummary();
    renderPagination();
    renderSelection();
  } catch {
    if (requestId !== state.requestId) return;
    state.items = [];
    state.hasNext = false;
    elements.error.hidden = false;
    renderItems();
    renderSummary();
    renderPagination();
  } finally {
    if (requestId === state.requestId) setLoading(false);
  }
}

function renderItems() {
  elements.tableBody.replaceChildren();
  elements.empty.hidden = state.items.length !== 0 || !elements.error.hidden;

  state.items.forEach((item) => {
    const row = document.createElement("tr");
    const status = getStatusMeta(item.status);
    const icon = isBrand
      ? `<img src="${escapeHtml(resolveImageUrl(item.logoUrl))}" alt="" onerror="this.parentElement.textContent='${escapeHtml(getInitials(item.name))}'" />`
      : `<span class="management-tag-icon">#</span>`;

    row.innerHTML = `
      <td class="management-select-cell"><input type="checkbox" data-management-select="${item.id}" aria-label="${escapeHtml(item.name)}を選択" /></td>
      <td class="management-name-cell">
        <button class="management-identity" type="button" data-action="edit-management" data-item-id="${item.id}">
          <span class="management-avatar">${icon}</span>
          <span><strong>${escapeHtml(item.name || "名称未設定")}</strong><small>#${item.id} · ${escapeHtml(item.slug || "—")}</small></span>
        </button>
      </td>
      <td data-label="説明">${escapeHtml(item.description || (isBrand ? "ブランド情報" : "説明なし"))}</td>
      <td data-label="ステータス"><span class="management-status management-status--${status.tone}">${status.label}</span></td>
      <td data-label="更新日">${formatDate(item.updatedAt || item.createdAt)}</td>
      <td class="management-row-actions">
        <button type="button" data-action="edit-management" data-item-id="${item.id}">編集</button>
        <button type="button" class="${item.status === "DELETED" ? "is-restore" : "is-delete"}" data-action="${item.status === "DELETED" ? "restore-management" : "delete-management"}" data-item-id="${item.id}">
          ${item.status === "DELETED" ? "復元" : "削除"}
        </button>
      </td>
    `;
    elements.tableBody.append(row);
  });
}

function renderSummary() {
  const active = state.items.filter((item) => item.status === "ACTIVE").length;
  const deleted = state.items.filter((item) => item.status === "DELETED").length;
  const viewed = state.page * state.size + state.items.length;
  elements.total.textContent = `${formatNumber(viewed)}${state.hasNext ? "+" : ""}`;
  elements.visible.textContent = formatNumber(state.items.length);
  elements.active.textContent = formatNumber(active);
  elements.deleted.textContent = formatNumber(deleted);
}

function renderPagination() {
  const start = state.items.length ? state.page * state.size + 1 : 0;
  const end = state.page * state.size + state.items.length;
  elements.paginationInfo.textContent = `${formatNumber(start)}–${formatNumber(end)}件を表示`;
  elements.pageIndicator.textContent = `${state.page + 1}ページ`;
  elements.previousPage.disabled = state.page === 0;
  elements.nextPage.disabled = !state.hasNext;
}

function renderSelection() {
  const visibleIds = state.items.map((item) => item.id);
  const selectedVisible = visibleIds.filter((id) => state.selectedIds.has(id));
  elements.tableBody.querySelectorAll("[data-management-select]").forEach((checkbox) => {
    checkbox.checked = state.selectedIds.has(Number(checkbox.dataset.managementSelect));
  });
  elements.selectAll.checked = visibleIds.length > 0 && selectedVisible.length === visibleIds.length;
  elements.selectAll.indeterminate = selectedVisible.length > 0 && selectedVisible.length < visibleIds.length;
  elements.selectedCount.textContent = formatNumber(state.selectedIds.size);
  elements.bulkBar.hidden = state.selectedIds.size === 0;
}

async function openEditor(itemId = null) {
  elements.editorForm.reset();
  elements.editorError.hidden = true;
  elements.editingId.value = itemId || "";
  elements.editorTitle.textContent = itemId ? `${labels.singular}を編集` : labels.create;
  elements.editorStatusField.hidden = !itemId;

  if (itemId) {
    const item = state.items.find((entry) => entry.id === itemId);
    if (!item) return;
    elements.name.value = item.name || "";
    elements.slug.value = item.slug || "";
    elements.description.value = item.description || "";
    elements.logo.value = item.logoUrl || "";
    elements.editorStatus.value = item.status === "INACTIVE" ? "INACTIVE" : "ACTIVE";
  }

  elements.editor.showModal();
  elements.name.focus();
}

async function saveItem(event) {
  event.preventDefault();
  const itemId = Number(elements.editingId.value) || null;
  const current = state.items.find((item) => item.id === itemId);
  const payload = {
    name: elements.name.value.trim(),
    slug: elements.slug.value.trim(),
    description: elements.description.value.trim() || null,
  };
  if (isBrand) payload.logoUrl = elements.logo.value.trim() || null;
  if (itemId) payload.status = elements.editorStatus.value || current?.status || "ACTIVE";

  elements.save.disabled = true;
  elements.editorError.hidden = true;
  try {
    if (isBrand) {
      if (itemId) await updateAdminBrand(itemId, payload);
      else await createAdminBrand(payload);
    } else if (itemId) {
      await updateAdminTag(itemId, payload);
    } else {
      await createAdminTag(payload);
    }
    closeEditor();
    await loadItems();
    showToast(`${labels.singular}を${itemId ? "更新" : "追加"}しました。`);
  } catch (error) {
    elements.editorError.textContent = getErrorMessage(error, `${labels.singular}を保存できませんでした。`);
    elements.editorError.hidden = false;
  } finally {
    elements.save.disabled = false;
  }
}

function requestStatusChange(ids, action) {
  const validIds = ids.filter((id) => {
    const item = state.items.find((entry) => entry.id === id);
    return action === "delete" ? item?.status === "ACTIVE" : item?.status === "DELETED";
  });
  if (!validIds.length) {
    showToast(action === "delete" ? "公開中の項目を選択してください。" : "削除済みの項目を選択してください。", true);
    return;
  }
  state.pendingAction = { ids: validIds, action };
  elements.confirmTitle.textContent = `${labels.singular}を${action === "delete" ? "削除" : "復元"}`;
  elements.confirmMessage.textContent = `${formatNumber(validIds.length)}件を${action === "delete" ? "削除" : "復元"}します。よろしいですか？`;
  elements.confirmButton.textContent = action === "delete" ? "削除する" : "復元する";
  elements.confirmButton.classList.toggle("is-danger", action === "delete");
  elements.confirm.showModal();
}

async function executeStatusChange(event) {
  event.preventDefault();
  if (!state.pendingAction) return;
  const { ids, action } = state.pendingAction;
  elements.confirmButton.disabled = true;
  try {
    if (isBrand && action === "restore") {
      await Promise.all(
        ids.map((id) => {
          const item = state.items.find((entry) => entry.id === id);
          return updateAdminBrand(id, {
            name: item.name,
            slug: item.slug,
            description: null,
            logoUrl: item.logoUrl || null,
            status: "ACTIVE",
          });
        }),
      );
    } else if (isBrand) {
      await Promise.all(ids.map((id) => changeAdminBrandStatus(id, action)));
    } else {
      await changeAdminTagsStatus(ids, action);
    }
    closeConfirm();
    await loadItems();
    showToast(`${formatNumber(ids.length)}件を${action === "delete" ? "削除" : "復元"}しました。`);
  } catch (error) {
    closeConfirm();
    showToast(getErrorMessage(error, "操作を完了できませんでした。"), true);
  } finally {
    elements.confirmButton.disabled = false;
  }
}

function resetFilters() {
  elements.form.reset();
  state.page = 0;
  loadItems();
}

function setLoading(loading) {
  elements.loading.hidden = !loading;
}

function closeEditor() {
  if (elements.editor.open) elements.editor.close();
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

function showToast(message, error = false) {
  elements.toast.textContent = message;
  elements.toast.classList.toggle("is-error", error);
  elements.toast.classList.add("is-visible");
  window.setTimeout(() => elements.toast.classList.remove("is-visible"), 2800);
}

function getContent(response) {
  return Array.isArray(response) ? response : response?.content ?? [];
}

function resolveImageUrl(path) {
  if (!path) return "/assets/images/default/no-image.png";
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE}${path.startsWith("/") ? "" : "/"}${path}`;
}

function getInitials(name) {
  return String(name || "?").trim().slice(0, 2).toUpperCase();
}

function getStatusMeta(status) {
  if (status === "DELETED") return { label: "削除済み", tone: "deleted" };
  if (status === "INACTIVE") return { label: "非公開", tone: "inactive" };
  return { label: "公開中", tone: "active" };
}

function formatDate(value) {
  const date = value ? new Date(value) : null;
  if (!date || Number.isNaN(date.getTime())) return "—";
  return new Intl.DateTimeFormat("ja-JP", { year: "numeric", month: "2-digit", day: "2-digit" }).format(date);
}

function formatNumber(value) {
  return new Intl.NumberFormat("ja-JP").format(Number(value) || 0);
}

function getErrorMessage(error, fallback) {
  if (error?.status === 400) return "入力内容を確認してください。";
  if (error?.status === 403) return "この操作を実行する権限がありません。";
  if (error?.status === 409 || String(error?.message).includes("exists")) return "同じ名前またはスラッグが既に存在します。";
  return fallback;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
