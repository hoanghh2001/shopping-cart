import {
  changeAdminProductArchive,
  createAdminProduct,
  deleteAdminColorImage,
  fetchAdminBrands,
  fetchAdminCategories,
  fetchAdminColorImages,
  fetchAdminColorVariants,
  fetchAdminProduct,
  fetchAdminProductColors,
  fetchAdminProducts,
  updateAdminVariant,
  updateAdminProduct,
  uploadAdminColorImages,
} from "../api/admin.js";
import { API_BASE } from "../api/config.js";
import { checkLogin } from "../components/check-login.js";

const STATUS_META = {
  ACTIVE: { label: "公開中", tone: "active" },
  INACTIVE: { label: "非公開", tone: "inactive" },
  DELETED: { label: "削除済み", tone: "deleted" },
};

const elements = {
  sidebar: document.getElementById("adminSidebar"),
  menuButton: document.querySelector('[data-action="open-sidebar"]'),
  filters: document.getElementById("productFilters"),
  keyword: document.getElementById("productKeyword"),
  status: document.getElementById("productStatus"),
  brandFilter: document.getElementById("brandFilter"),
  categoryFilter: document.getElementById("categoryFilter"),
  resetFilters: document.getElementById("resetProductFilters"),
  retry: document.getElementById("retryProducts"),
  error: document.getElementById("productsError"),
  loading: document.getElementById("productsLoading"),
  empty: document.getElementById("productsEmpty"),
  tableBody: document.getElementById("productsTableBody"),
  selectAll: document.getElementById("selectAllProducts"),
  bulkBar: document.getElementById("productBulkBar"),
  selectedCount: document.getElementById("selectedProductCount"),
  total: document.getElementById("totalProducts"),
  visible: document.getElementById("visibleProducts"),
  active: document.getElementById("activeProducts"),
  deleted: document.getElementById("deletedProducts"),
  paginationInfo: document.getElementById("productPaginationInfo"),
  pageIndicator: document.getElementById("productPageIndicator"),
  previousPage: document.getElementById("previousProductPage"),
  nextPage: document.getElementById("nextProductPage"),
  drawer: document.getElementById("productDrawer"),
  drawerBackdrop: document.getElementById("productDrawerBackdrop"),
  drawerTitle: document.getElementById("productDrawerTitle"),
  drawerBody: document.getElementById("productDrawerBody"),
  drawerFooter: document.getElementById("productDrawerFooter"),
  formDialog: document.getElementById("productFormDialog"),
  form: document.getElementById("productForm"),
  formTitle: document.getElementById("productFormTitle"),
  editingId: document.getElementById("editingProductId"),
  name: document.getElementById("productName"),
  slugField: document.getElementById("productSlugField"),
  slug: document.getElementById("productSlug"),
  category: document.getElementById("productCategory"),
  brand: document.getElementById("productBrand"),
  description: document.getElementById("productDescription"),
  formError: document.getElementById("productFormError"),
  saveButton: document.getElementById("saveProduct"),
  confirmDialog: document.getElementById("productConfirmDialog"),
  confirmForm: document.getElementById("productConfirmForm"),
  confirmTitle: document.getElementById("productConfirmTitle"),
  confirmMessage: document.getElementById("productConfirmMessage"),
  confirmButton: document.getElementById("confirmProductAction"),
  toast: document.getElementById("productToast"),
};

const state = {
  products: [],
  brands: [],
  categories: [],
  selectedIds: new Set(),
  selectedProduct: null,
  page: 0,
  size: 12,
  totalPages: 1,
  totalElements: 0,
  requestId: 0,
  pendingAction: null,
};

document.addEventListener("DOMContentLoaded", async () => {
  checkLogin();
  bindEvents();
  await loadReferenceData();
  loadProducts();
});

function bindEvents() {
  elements.filters.addEventListener("submit", (event) => {
    event.preventDefault();
    state.page = 0;
    loadProducts();
  });

  elements.resetFilters.addEventListener("click", resetFilters);
  elements.retry.addEventListener("click", loadProducts);

  elements.previousPage.addEventListener("click", () => {
    if (state.page <= 0) return;
    state.page -= 1;
    loadProducts();
  });

  elements.nextPage.addEventListener("click", () => {
    if (state.page >= state.totalPages - 1) return;
    state.page += 1;
    loadProducts();
  });

  elements.selectAll.addEventListener("change", () => {
    state.products.forEach((product) => {
      if (elements.selectAll.checked) state.selectedIds.add(product.id);
      else state.selectedIds.delete(product.id);
    });
    renderSelection();
  });

  elements.tableBody.addEventListener("change", (event) => {
    const checkbox = event.target.closest("[data-product-select]");
    if (!checkbox) return;
    const productId = Number(checkbox.dataset.productSelect);
    if (checkbox.checked) state.selectedIds.add(productId);
    else state.selectedIds.delete(productId);
    renderSelection();
  });

  elements.drawerBody.addEventListener("change", (event) => {
    const imageInput = event.target.closest("[data-color-image-input]");
    if (!imageInput?.files?.length) return;
    uploadColorImages(Number(imageInput.dataset.colorImageInput), [...imageInput.files], imageInput);
  });

  elements.drawerBackdrop.addEventListener("click", closeDrawer);
  elements.form.addEventListener("submit", saveProduct);
  elements.confirmForm.addEventListener("submit", executePendingAction);

  document.addEventListener("click", (event) => {
    const actionElement = event.target.closest("[data-action]");
    if (!actionElement) return;

    const action = actionElement.dataset.action;
    if (action === "open-sidebar") openSidebar();
    if (action === "close-sidebar") closeSidebar();
    if (action === "clear-filters") resetFilters();
    if (action === "open-create") openCreateForm();
    if (action === "open-product") openProductByIndex(Number(actionElement.dataset.index));
    if (action === "close-drawer") closeDrawer();
    if (action === "close-form") closeProductForm();
    if (action === "edit-product") openEditForm();
    if (action === "delete-product") requestArchiveAction([state.selectedProduct?.id], "delete");
    if (action === "restore-product") requestArchiveAction([state.selectedProduct?.id], "restore");
    if (action === "bulk-delete") requestArchiveAction([...state.selectedIds], "delete");
    if (action === "bulk-restore") requestArchiveAction([...state.selectedIds], "restore");
    if (action === "save-variant") saveVariant(Number(actionElement.dataset.variantId));
    if (action === "delete-color-image") {
      requestImageDelete(Number(actionElement.dataset.imageId), Number(actionElement.dataset.colorId));
    }
    if (action === "close-confirm") closeConfirmDialog();
  });

  document.addEventListener("keydown", (event) => {
    if (event.key !== "Escape") return;
    closeSidebar();
    if (!elements.formDialog.open && !elements.confirmDialog.open) closeDrawer();
  });

  window.addEventListener("resize", () => {
    if (window.innerWidth > 900) closeSidebar();
  });
}

async function loadReferenceData() {
  try {
    const [brandsResponse, categoriesResponse] = await Promise.all([
      fetchAdminBrands({ status: "ACTIVE", page: 0, size: 100 }),
      fetchAdminCategories({ status: "ACTIVE", page: 0, size: 100 }),
    ]);

    state.brands = getContent(brandsResponse);
    state.categories = getContent(categoriesResponse);
    populateReferenceSelects();
  } catch {
    state.brands = [];
    state.categories = [];
  }
}

async function loadProducts() {
  const currentRequest = ++state.requestId;
  setLoading(true);
  hideError();

  try {
    const response = await fetchAdminProducts({
      page: state.page,
      size: state.size,
      keyword: elements.keyword.value.trim(),
      status: elements.status.value,
      brandId: elements.brandFilter.value,
      categoryId: elements.categoryFilter.value,
      sort: "createdAt,desc",
    });
    if (currentRequest !== state.requestId) return;

    const page = response?.page ?? response;
    state.products = getContent(response);
    state.page = Number(page?.number ?? state.page);
    state.size = Number(page?.size ?? state.size);
    state.totalPages = Math.max(Number(page?.totalPages ?? (response?.last === false ? state.page + 2 : state.page + 1)), 1);
    state.totalElements = Number(page?.totalElements ?? response?.totalElements ?? state.products.length);
    state.selectedIds.clear();

    renderProducts();
    renderSummary();
    renderPagination(response);
    renderSelection();
  } catch {
    if (currentRequest !== state.requestId) return;
    state.products = [];
    state.totalElements = 0;
    renderProducts();
    renderSummary();
    renderPagination(null);
    showError();
  } finally {
    if (currentRequest === state.requestId) setLoading(false);
  }
}

function renderProducts() {
  elements.tableBody.replaceChildren();
  elements.empty.hidden = state.products.length !== 0 || !elements.error.hidden;

  state.products.forEach((product, index) => {
    const row = document.createElement("tr");
    const status = STATUS_META[product.status] ?? { label: product.status || "未設定", tone: "inactive" };
    const imageUrl = resolveImageUrl(product.imageUrl);

    row.innerHTML = `
      <td class="product-check-cell">
        <input
          type="checkbox"
          data-product-select="${product.id}"
          aria-label="${escapeHtml(product.name || `商品 ${product.id}`)}を選択"
          ${state.selectedIds.has(product.id) ? "checked" : ""}
        />
      </td>
      <td class="product-name-cell" data-label="商品">
        <button class="product-main-cell" type="button" data-action="open-product" data-index="${index}">
          <img src="${escapeHtml(imageUrl)}" alt="" onerror="this.src='/assets/images/default/no-image.png'" />
          <span>
            <strong>${escapeHtml(product.name || "名称未設定")}</strong>
            <small>#${product.id} · ${escapeHtml(product.slug || "—")}</small>
          </span>
        </button>
      </td>
      <td data-label="ブランド">${escapeHtml(product.brandName || "—")}</td>
      <td data-label="カテゴリー">${escapeHtml(product.categoryName || "—")}</td>
      <td data-label="ステータス"><span class="product-status product-status--${status.tone}">${status.label}</span></td>
      <td data-label="更新日"><time datetime="${escapeHtml(product.updatedAt || product.createdAt || "")}">${formatDate(product.updatedAt || product.createdAt)}</time></td>
      <td class="product-row-action-cell">
        <button class="product-row-action" type="button" data-action="open-product" data-index="${index}" aria-label="${escapeHtml(product.name || "商品")}の詳細を見る">
          <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m9 18 6-6-6-6" /></svg>
        </button>
      </td>
    `;

    elements.tableBody.append(row);
  });
}

function renderSummary() {
  const active = state.products.filter((product) => product.status === "ACTIVE").length;
  const deleted = state.products.filter((product) => product.status === "DELETED").length;

  elements.total.textContent = formatNumber(state.totalElements);
  elements.visible.textContent = formatNumber(state.products.length);
  elements.active.textContent = formatNumber(active);
  elements.deleted.textContent = formatNumber(deleted);
}

function renderPagination(response) {
  const start = state.totalElements === 0 ? 0 : state.page * state.size + 1;
  const end = Math.min((state.page + 1) * state.size, state.totalElements);
  const hasNext = response ? response.last === false || state.page < state.totalPages - 1 : false;

  elements.paginationInfo.textContent = `${formatNumber(state.totalElements)}件中 ${formatNumber(start)}–${formatNumber(end)}件`;
  elements.pageIndicator.textContent = `${state.page + 1} / ${state.totalPages}`;
  elements.previousPage.disabled = state.page <= 0;
  elements.nextPage.disabled = !hasNext;
}

function renderSelection() {
  const visibleIds = state.products.map((product) => product.id);
  const selectedVisible = visibleIds.filter((id) => state.selectedIds.has(id));

  elements.tableBody.querySelectorAll("[data-product-select]").forEach((checkbox) => {
    checkbox.checked = state.selectedIds.has(Number(checkbox.dataset.productSelect));
  });

  elements.selectAll.checked = visibleIds.length > 0 && selectedVisible.length === visibleIds.length;
  elements.selectAll.indeterminate = selectedVisible.length > 0 && selectedVisible.length < visibleIds.length;
  elements.selectedCount.textContent = formatNumber(state.selectedIds.size);
  elements.bulkBar.hidden = state.selectedIds.size === 0;
}

async function openProductByIndex(index) {
  const product = state.products[index];
  if (!product) return;

  state.selectedProduct = product;
  openDrawer();
  renderDrawerLoading(product);

  const [detailResult, colorsResult] = await Promise.allSettled([
    fetchAdminProduct(product.id),
    fetchAdminProductColors(product.id),
  ]);

  const detail = detailResult.status === "fulfilled" ? detailResult.value : product;
  const colors = colorsResult.status === "fulfilled" && Array.isArray(colorsResult.value) ? colorsResult.value : [];

  const colorsWithVariants = await Promise.all(
    colors.map(async (color) => {
      const [variantsResult, imagesResult] = await Promise.allSettled([
        fetchAdminColorVariants(color.id),
        fetchAdminColorImages(color.id),
      ]);

      return {
        ...color,
        variants: variantsResult.status === "fulfilled" ? getContent(variantsResult.value) : [],
        images: imagesResult.status === "fulfilled" ? getContent(imagesResult.value) : [],
      };
    }),
  );

  state.selectedProduct = { ...product, ...detail, colors: colorsWithVariants };
  renderProductDetail(state.selectedProduct);
}

function renderDrawerLoading(product) {
  elements.drawerTitle.textContent = product.name || "商品詳細";
  elements.drawerBody.innerHTML = `
    <div class="product-drawer-loading">
      <span class="products-spinner" aria-hidden="true"></span>
      <span>商品詳細を読み込んでいます。</span>
    </div>
  `;
  elements.drawerFooter.replaceChildren();
}

function renderProductDetail(product) {
  const status = STATUS_META[product.status] ?? { label: product.status || "未設定", tone: "inactive" };
  const colors = product.colors ?? [];
  const allVariants = colors.flatMap((color) => color.variants ?? []);
  const stock = allVariants.reduce((total, variant) => total + toNumber(variant.stock), 0);
  const outOfStock = allVariants.filter((variant) => toNumber(variant.stock) === 0).length;

  elements.drawerTitle.textContent = product.name || "商品詳細";
  elements.drawerBody.innerHTML = `
    <section class="product-detail-hero">
      <img src="${escapeHtml(resolveImageUrl(product.imageUrl))}" alt="" onerror="this.src='/assets/images/default/no-image.png'" />
      <div>
        <span class="product-status product-status--${status.tone}">${status.label}</span>
        <h3>${escapeHtml(product.name || "名称未設定")}</h3>
        <p>#${product.id} · ${escapeHtml(product.slug || "—")}</p>
      </div>
    </section>

    <section class="product-detail-section">
      <h3>基本情報</h3>
      <dl class="product-detail-list">
        ${detailRow("ブランド", product.brandName)}
        ${detailRow("カテゴリー", product.categoryName)}
        ${detailRow("作成日", formatDateTime(product.createdAt))}
        ${detailRow("更新日", formatDateTime(product.updatedAt))}
        ${detailRow("カラー数", `${formatNumber(colors.length)}色`)}
        ${detailRow("総在庫", `${formatNumber(stock)}点`, true)}
        ${detailRow("サイズ数", `${formatNumber(allVariants.length)}件`)}
        ${detailRow("在庫切れ", `${formatNumber(outOfStock)}件`, outOfStock > 0)}
      </dl>
    </section>

    <section class="product-detail-section">
      <h3>商品説明</h3>
      <p class="product-description">${escapeHtml(product.description || "説明は登録されていません。")}</p>
    </section>

    <section class="product-detail-section">
      <h3>カラー・在庫 <span>${formatNumber(colors.length)}色</span></h3>
      <div class="product-color-list">
        ${colors.length ? colors.map(renderColor).join("") : '<p class="product-detail-empty">カラー情報がありません。</p>'}
      </div>
    </section>
  `;

  renderDrawerActions(product);
}

function renderColor(color) {
  const variants = color.variants ?? [];
  const images = color.images ?? [];
  const stock = variants.reduce((total, variant) => total + toNumber(variant.stock), 0);

  return `
    <article class="product-color-card">
      <header>
        <span class="color-swatch" style="background:${safeColor(color.hex)}"></span>
        <div><strong>${escapeHtml(color.name || "名称未設定")}</strong><small>${formatNumber(stock)}点</small></div>
        <span class="product-color-status">${color.status === "ACTIVE" ? "公開中" : escapeHtml(color.status || "—")}</span>
      </header>

      <section class="color-image-manager">
        <div class="color-manager-heading">
          <div><strong>画像</strong><span>${formatNumber(images.length)}枚</span></div>
          <label class="color-image-upload">
            ＋ 画像を追加
            <input
              type="file"
              accept="image/png,image/jpeg,image/webp"
              multiple
              data-color-image-input="${color.id}"
            />
          </label>
        </div>
        <div class="color-image-grid">
          ${
            images.length
              ? images.map((image) => renderColorImage(color, image)).join("")
              : '<p class="product-detail-empty">このカラーの画像はまだありません。</p>'
          }
        </div>
      </section>

      <section class="color-variant-manager">
        <div class="color-manager-heading">
          <div><strong>サイズ別の価格・在庫</strong><span>${formatNumber(variants.length)}サイズ</span></div>
        </div>
        <div class="variant-editor-list">
        ${
          variants.length
            ? variants
                .map((variant) => renderVariantEditor(variant))
                .join("")
            : '<p class="product-detail-empty">サイズ情報なし</p>'
        }
        </div>
      </section>
    </article>
  `;
}

function renderColorImage(color, image) {
  return `
    <figure class="color-image-item">
      <img
        src="${escapeHtml(resolveImageUrl(image.imageUrl))}"
        alt="${escapeHtml(image.altText || color.name || "")}"
        onerror="this.src='/assets/images/default/no-image.png'"
      />
      ${image.main ? '<span class="image-main-badge">メイン</span>' : ""}
      <button
        type="button"
        data-action="delete-color-image"
        data-image-id="${image.id}"
        data-color-id="${color.id}"
        aria-label="${escapeHtml(image.altText || "画像")}を削除"
      >×</button>
    </figure>
  `;
}

function renderVariantEditor(variant) {
  const stock = toNumber(variant.stock);
  const tone = stock === 0 ? "is-zero" : stock <= 3 ? "is-low" : "";

  return `
    <div class="variant-editor-row ${tone}" data-variant-id="${variant.id}">
      <div class="variant-size">
        <strong>${escapeHtml(variant.size || "—")}</strong>
        <small>${escapeHtml(variant.sku || `#${variant.id}`)}</small>
      </div>
      <label>
        <span>通常価格</span>
        <span class="price-input"><b>¥</b><input data-variant-field="regularPrice" type="number" min="0" step="1" value="${toNumber(variant.regularPrice)}" /></span>
      </label>
      <label>
        <span>販売価格</span>
        <span class="price-input"><b>¥</b><input data-variant-field="salePrice" type="number" min="0" step="1" value="${toNumber(variant.salePrice)}" /></span>
      </label>
      <label>
        <span>在庫</span>
        <span class="stock-display"><strong>${formatNumber(stock)}</strong><b>点</b></span>
      </label>
      <button type="button" data-action="save-variant" data-variant-id="${variant.id}">保存</button>
    </div>
  `;
}

function renderDrawerActions(product) {
  elements.drawerFooter.replaceChildren();

  const editButton = document.createElement("button");
  editButton.type = "button";
  editButton.className = "product-drawer-action is-secondary";
  editButton.dataset.action = "edit-product";
  editButton.textContent = "編集";
  editButton.disabled = product.status === "DELETED";
  elements.drawerFooter.append(editButton);

  const archiveButton = document.createElement("button");
  archiveButton.type = "button";
  archiveButton.className = `product-drawer-action ${product.status === "DELETED" ? "is-primary" : "is-danger"}`;
  archiveButton.dataset.action = product.status === "DELETED" ? "restore-product" : "delete-product";
  archiveButton.textContent = product.status === "DELETED" ? "商品を復元" : "商品を削除";
  archiveButton.disabled = product.status !== "ACTIVE" && product.status !== "DELETED";
  elements.drawerFooter.append(archiveButton);
}

function openCreateForm() {
  elements.form.reset();
  elements.editingId.value = "";
  elements.formTitle.textContent = "商品を追加";
  elements.slugField.hidden = true;
  elements.slug.required = false;
  elements.formError.hidden = true;
  elements.formDialog.showModal();
  elements.name.focus();
}

function openEditForm() {
  const product = state.selectedProduct;
  if (!product || product.status === "DELETED") return;

  elements.form.reset();
  elements.editingId.value = product.id;
  elements.formTitle.textContent = "商品を編集";
  elements.name.value = product.name || "";
  elements.slug.value = product.slug || "";
  elements.slugField.hidden = false;
  elements.slug.required = true;
  elements.category.value = product.categoryId ?? "";
  elements.brand.value = product.brandId ?? "";
  elements.description.value = product.description || "";
  elements.formError.hidden = true;
  elements.formDialog.showModal();
  elements.name.focus();
}

async function saveProduct(event) {
  event.preventDefault();
  const editingId = elements.editingId.value;
  const payload = {
    name: elements.name.value.trim(),
    categoryId: Number(elements.category.value),
    brandId: elements.brand.value ? Number(elements.brand.value) : null,
    description: elements.description.value.trim() || null,
  };

  if (editingId) payload.slug = elements.slug.value.trim();
  elements.saveButton.disabled = true;
  elements.formError.hidden = true;

  try {
    const savedProduct = editingId
      ? await updateAdminProduct(editingId, payload)
      : await createAdminProduct(payload);
    closeProductForm();
    closeDrawer();
    state.page = 0;
    await loadProducts();
    showToast(editingId ? "商品を更新しました。" : "商品を追加しました。");

    if (savedProduct?.id) {
      const index = state.products.findIndex((product) => product.id === savedProduct.id);
      if (index >= 0) openProductByIndex(index);
    }
  } catch (error) {
    elements.formError.textContent = getErrorMessage(error, "商品を保存できませんでした。");
    elements.formError.hidden = false;
  } finally {
    elements.saveButton.disabled = false;
  }
}

function requestArchiveAction(ids, action) {
  const validIds = ids.filter(Boolean).filter((id) => {
    const product = state.products.find((item) => item.id === id) ?? state.selectedProduct;
    return action === "delete" ? product?.status === "ACTIVE" : product?.status === "DELETED";
  });

  if (!validIds.length) {
    showToast(action === "delete" ? "公開中の商品を選択してください。" : "削除済みの商品を選択してください。", true);
    return;
  }

  state.pendingAction = { type: "product-archive", ids: validIds, action };
  const isDelete = action === "delete";
  elements.confirmTitle.textContent = isDelete ? "商品を削除" : "商品を復元";
  elements.confirmMessage.textContent = `${formatNumber(validIds.length)}件の商品を${isDelete ? "削除" : "復元"}します。よろしいですか？`;
  elements.confirmButton.textContent = isDelete ? "削除する" : "復元する";
  elements.confirmButton.classList.toggle("is-danger", isDelete);
  elements.confirmDialog.showModal();
}

async function executePendingAction(event) {
  event.preventDefault();
  if (!state.pendingAction) return;

  elements.confirmButton.disabled = true;
  const pendingAction = state.pendingAction;

  try {
    if (pendingAction.type === "image-delete") {
      await deleteAdminColorImage(pendingAction.imageId);
      removeImageFromSelectedProduct(pendingAction.colorId, pendingAction.imageId);
      closeConfirmDialog();
      renderProductDetail(state.selectedProduct);
      showToast("画像を削除しました。");
      return;
    }

    const { ids, action } = pendingAction;
    await changeAdminProductArchive(ids, action);
    closeConfirmDialog();
    closeDrawer();
    await loadProducts();
    showToast(action === "delete" ? "商品を削除しました。" : "商品を復元しました。");
  } catch (error) {
    closeConfirmDialog();
    showToast(getErrorMessage(error, "操作を完了できませんでした。"), true);
  } finally {
    elements.confirmButton.disabled = false;
  }
}

function requestImageDelete(imageId, colorId) {
  if (!imageId || !colorId) return;

  state.pendingAction = { type: "image-delete", imageId, colorId };
  elements.confirmTitle.textContent = "画像を削除";
  elements.confirmMessage.textContent = "このカラーから画像を削除します。よろしいですか？";
  elements.confirmButton.textContent = "削除する";
  elements.confirmButton.classList.add("is-danger");
  elements.confirmDialog.showModal();
}

async function uploadColorImages(colorId, files, input) {
  if (!colorId || !files.length) return;
  const invalidFile = files.find((file) => !["image/png", "image/jpeg", "image/webp"].includes(file.type));
  if (invalidFile) {
    showToast("PNG、JPEG、WebP形式の画像を選択してください。", true);
    input.value = "";
    return;
  }

  const uploadLabel = input.closest(".color-image-upload");
  uploadLabel?.classList.add("is-loading");
  input.disabled = true;

  try {
    const uploadedImages = await uploadAdminColorImages(colorId, files);
    const color = state.selectedProduct?.colors?.find((item) => item.id === colorId);
    if (color) color.images = [...(color.images ?? []), ...getContent(uploadedImages)];
    renderProductDetail(state.selectedProduct);
    showToast(`${formatNumber(files.length)}枚の画像を追加しました。`);
  } catch (error) {
    showToast(getErrorMessage(error, "画像を追加できませんでした。"), true);
  } finally {
    input.disabled = false;
    input.value = "";
    uploadLabel?.classList.remove("is-loading");
  }
}

async function saveVariant(variantId) {
  const row = elements.drawerBody.querySelector(`[data-variant-id="${variantId}"]`);
  if (!row) return;

  const regularPrice = toNumber(row.querySelector('[data-variant-field="regularPrice"]')?.value);
  const salePrice = toNumber(row.querySelector('[data-variant-field="salePrice"]')?.value);
  const variant = findSelectedVariant(variantId);
  const button = row.querySelector('[data-action="save-variant"]');

  if (!variant || regularPrice < 0 || salePrice < 0) {
    showToast("価格を正しく入力してください。", true);
    return;
  }

  button.disabled = true;
  button.textContent = "保存中";

  try {
    const updated = await updateAdminVariant(variantId, {
      size: variant.size,
      regularPrice,
      salePrice,
    });
    Object.assign(variant, updated);
    renderProductDetail(state.selectedProduct);
    showToast(`${variant.size || "サイズ"}の価格を更新しました。`);
  } catch (error) {
    button.disabled = false;
    button.textContent = "保存";
    showToast(getErrorMessage(error, "価格を更新できませんでした。"), true);
  }
}

function findSelectedVariant(variantId) {
  for (const color of state.selectedProduct?.colors ?? []) {
    const variant = (color.variants ?? []).find((item) => item.id === variantId);
    if (variant) return variant;
  }
  return null;
}

function removeImageFromSelectedProduct(colorId, imageId) {
  const color = state.selectedProduct?.colors?.find((item) => item.id === colorId);
  if (color) color.images = (color.images ?? []).filter((image) => image.id !== imageId);
}

function populateReferenceSelects() {
  const brandOptions = state.brands.map((brand) => `<option value="${brand.id}">${escapeHtml(brand.name)}</option>`).join("");
  const categoryOptions = state.categories
    .map((category) => `<option value="${category.id}">${escapeHtml(category.name)}</option>`)
    .join("");

  elements.brandFilter.insertAdjacentHTML("beforeend", brandOptions);
  elements.brand.insertAdjacentHTML("beforeend", brandOptions);
  elements.categoryFilter.insertAdjacentHTML("beforeend", categoryOptions);
  elements.category.insertAdjacentHTML("beforeend", categoryOptions);
}

function resetFilters() {
  elements.filters.reset();
  state.page = 0;
  loadProducts();
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
  state.selectedProduct = null;
}

function closeProductForm() {
  if (elements.formDialog.open) elements.formDialog.close();
}

function closeConfirmDialog() {
  if (elements.confirmDialog.open) elements.confirmDialog.close();
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

function setLoading(isLoading) {
  elements.loading.hidden = !isLoading;
  elements.previousPage.disabled = isLoading || state.page <= 0;
  elements.nextPage.disabled = isLoading || state.page >= state.totalPages - 1;
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

function getContent(response) {
  if (Array.isArray(response)) return response;
  return response?.content ?? [];
}

function detailRow(label, value, emphasize = false) {
  return `<div class="${emphasize ? "is-emphasized" : ""}"><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value ?? "—")}</dd></div>`;
}

function resolveImageUrl(path) {
  if (!path) return "/assets/images/default/no-image.png";
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE}${path.startsWith("/") ? "" : "/"}${path}`;
}

function formatNumber(value) {
  return new Intl.NumberFormat("ja-JP").format(toNumber(value));
}

function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";
  return new Intl.DateTimeFormat("ja-JP", { year: "numeric", month: "2-digit", day: "2-digit" }).format(date);
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

function safeColor(value) {
  return /^#[0-9a-f]{3,8}$/i.test(value || "") ? value : "#d9ded6";
}

function getErrorMessage(error, fallback) {
  const message = String(error?.message || "");
  if (message.includes("duplicate")) return "同じ商品名またはスラッグが既に存在します。";
  if (error?.status === 400) return "入力内容を確認してください。";
  if (error?.status === 403) return "この操作を実行する権限がありません。";
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
