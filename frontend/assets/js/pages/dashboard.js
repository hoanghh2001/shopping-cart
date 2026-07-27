import { fetchOverview } from "../api/admin.js";
import { checkLogin } from "../components/check-login.js";

const RANGE_LABELS = {
  TODAY: "今日",
  LAST_7_DAYS: "過去7日間",
  WTD: "今週",
  MTD: "今月",
  YTD: "今年",
};

const METRIC_CONFIG = {
  revenue: {
    label: "売上",
    responseKey: "revenue",
    currentKey: "currentRevenue",
    previousKey: "previousRevenue",
    valueId: "revenueValue",
    changeId: "revenueChange",
    format: formatYen,
  },
  orders: {
    label: "注文数",
    responseKey: "orderCount",
    currentKey: "current",
    previousKey: "previous",
    valueId: "ordersValue",
    changeId: "ordersChange",
    format: formatNumber,
  },
  visits: {
    label: "訪問数",
    responseKey: "visitorCount",
    currentKey: "current",
    previousKey: "previous",
    valueId: "visitsValue",
    changeId: "visitsChange",
    format: formatNumber,
  },
  signups: {
    label: "新規登録",
    responseKey: "newUserCount",
    currentKey: "current",
    previousKey: "previous",
    valueId: "signupsValue",
    changeId: "signupsChange",
    format: formatNumber,
  },
};

const elements = {
  range: document.getElementById("rangeFilter"),
  alert: document.getElementById("dashboardAlert"),
  retry: document.getElementById("retryDashboard"),
  sidebar: document.getElementById("adminSidebar"),
  menuButton: document.querySelector('[data-action="open-sidebar"]'),
  date: document.getElementById("dashboardDate"),
  tableBody: document.getElementById("metricsTableBody"),
  comparisonTitle: document.getElementById("comparisonTitle"),
  comparisonPeriod: document.getElementById("comparisonPeriod"),
  comparisonCurrent: document.getElementById("comparisonCurrent"),
  comparisonBadge: document.getElementById("comparisonBadge"),
  currentBar: document.getElementById("currentBar"),
  previousBar: document.getElementById("previousBar"),
  currentBarValue: document.getElementById("currentBarValue"),
  previousBarValue: document.getElementById("previousBarValue"),
  comparisonInsight: document.getElementById("comparisonInsight"),
  connectionStatus: document.getElementById("connectionStatus"),
  lastUpdated: document.getElementById("lastUpdated"),
  statusPeriod: document.getElementById("statusPeriod"),
  statusDot: document.getElementById("statusDot"),
};

let overviewData = null;
let activeMetric = "revenue";
let requestId = 0;

document.addEventListener("DOMContentLoaded", () => {
  checkLogin();
  if (!document.body.classList.contains("admin-dashboard")) return;

  renderToday();
  bindEvents();
  loadDashboard();
});

function bindEvents() {
  elements.range.addEventListener("change", () => {
    updatePeriodLabels();
    loadDashboard();
  });

  elements.retry.addEventListener("click", loadDashboard);

  document.addEventListener("click", (event) => {
    const actionElement = event.target.closest("[data-action]");
    if (actionElement) {
      if (actionElement.dataset.action === "open-sidebar") openSidebar();
      if (actionElement.dataset.action === "close-sidebar") closeSidebar();
    }

    const metricCard = event.target.closest("[data-metric]");
    if (metricCard) selectMetric(metricCard.dataset.metric);
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") closeSidebar();
  });

  window.addEventListener("resize", () => {
    if (window.innerWidth > 900) closeSidebar();
  });
}

async function loadDashboard() {
  const currentRequest = ++requestId;
  setLoadingState(true);
  hideError();

  try {
    const data = await fetchOverview(elements.range.value);
    if (currentRequest !== requestId) return;

    overviewData = data;
    renderDashboard(data);
    setConnectionState(true);
  } catch {
    if (currentRequest !== requestId) return;

    overviewData = null;
    renderUnavailableState();
    showError();
    setConnectionState(false);
  } finally {
    if (currentRequest === requestId) setLoadingState(false);
  }
}

function renderDashboard(data) {
  Object.entries(METRIC_CONFIG).forEach(([metricKey, config]) => {
    const metric = getMetric(data, config);
    const valueElement = document.getElementById(config.valueId);
    const changeElement = document.getElementById(config.changeId);

    valueElement.textContent = config.format(metric.current);
    renderChange(changeElement, metric.changeRate, "前期比", metric.previous, metric.current);
  });

  renderMetricsTable(data);
  renderComparison(activeMetric);
}

function getMetric(data, config) {
  const source = data?.[config.responseKey] ?? {};
  const current = toFiniteNumber(source[config.currentKey]);
  const changeRate = toFiniteNumber(source.changeRate);
  const previousValue = Number(source[config.previousKey]);

  return {
    current,
    changeRate,
    previous: Number.isFinite(previousValue) ? previousValue : calculatePrevious(current, changeRate),
  };
}

function calculatePrevious(current, changeRate) {
  if (changeRate <= -100) return current === 0 ? 0 : null;
  const previous = current / (1 + changeRate / 100);
  return Number.isFinite(previous) ? previous : null;
}

function renderMetricsTable(data) {
  elements.tableBody.replaceChildren(
    ...Object.entries(METRIC_CONFIG).map(([metricKey, config]) => {
      const metric = getMetric(data, config);
      const row = document.createElement("tr");
      if (metricKey === activeMetric) row.classList.add("is-active");

      const labelCell = createCell(config.label);
      labelCell.dataset.label = "指標";
      const currentCell = createCell(config.format(metric.current));
      currentCell.dataset.label = "現在";
      const previousCell = createCell(formatNullable(metric.previous, config.format));
      previousCell.dataset.label = "前期";
      const changeCell = document.createElement("td");
      changeCell.dataset.label = "変化";
      const change = document.createElement("span");
      change.className = "table-change";
      renderChange(change, metric.changeRate, "", metric.previous, metric.current);
      changeCell.append(change);

      row.append(labelCell, currentCell, previousCell, changeCell);
      row.addEventListener("click", () => selectMetric(metricKey));
      return row;
    }),
  );
}

function renderComparison(metricKey) {
  const config = METRIC_CONFIG[metricKey];
  elements.comparisonTitle.textContent = `${config.label}の比較`;
  if (!overviewData) return;

  const metric = getMetric(overviewData, config);
  const previous = metric.previous ?? 0;
  const maxValue = Math.max(metric.current, previous, 1);
  const currentWidth = Math.max((metric.current / maxValue) * 100, metric.current > 0 ? 4 : 0);
  const previousWidth = Math.max((previous / maxValue) * 100, previous > 0 ? 4 : 0);

  elements.comparisonCurrent.textContent = config.format(metric.current);
  elements.currentBarValue.textContent = config.format(metric.current);
  elements.previousBarValue.textContent = formatNullable(metric.previous, config.format);
  elements.currentBar.style.width = `${currentWidth}%`;
  elements.previousBar.style.width = `${previousWidth}%`;
  renderChange(elements.comparisonBadge, metric.changeRate, "", metric.previous, metric.current);

  const direction = metric.current > previous ? "増加" : metric.current < previous ? "減少" : "変化なし";
  const amount = Math.abs(metric.current - previous);
  elements.comparisonInsight.textContent =
    metric.previous === null
      ? "前期が0のため、変化率から前期値を算出できません。"
      : `${config.label}は前期と比べて${config.format(amount)}の${direction}です。`;
}

function selectMetric(metricKey) {
  if (!METRIC_CONFIG[metricKey]) return;
  activeMetric = metricKey;

  document.querySelectorAll("[data-metric]").forEach((card) => {
    const isActive = card.dataset.metric === metricKey;
    card.classList.toggle("is-active", isActive);
    card.setAttribute("aria-pressed", String(isActive));
  });

  document.querySelectorAll(".metrics-table tbody tr").forEach((row, index) => {
    row.classList.toggle("is-active", Object.keys(METRIC_CONFIG)[index] === metricKey);
  });

  renderComparison(metricKey);
}

function renderChange(element, rate, prefix = "", previous = null, current = null) {
  const numericRate = toFiniteNumber(rate);
  const isNew = previous === 0 && current > 0;
  const sign = numericRate > 0 ? "+" : "";
  element.classList.remove("is-positive", "is-negative", "is-neutral");
  element.classList.add(isNew || numericRate > 0 ? "is-positive" : numericRate < 0 ? "is-negative" : "is-neutral");
  element.textContent = `${prefix ? `${prefix} ` : ""}${isNew ? "新規" : `${sign}${numericRate.toFixed(1)}%`}`;
}

function setLoadingState(isLoading) {
  elements.range.disabled = isLoading;
  elements.retry.disabled = isLoading;
  document.querySelectorAll(".metric-card_value").forEach((element) => {
    element.classList.toggle("is-loading", isLoading);
  });
}

function renderUnavailableState() {
  Object.values(METRIC_CONFIG).forEach((config) => {
    document.getElementById(config.valueId).textContent = "—";
    const changeElement = document.getElementById(config.changeId);
    changeElement.textContent = "前期比 —";
    changeElement.classList.remove("is-positive", "is-negative", "is-neutral");
  });

  elements.tableBody.innerHTML = '<tr><td colspan="4" class="table-loading">表示できるデータがありません。</td></tr>';
  elements.comparisonCurrent.textContent = "—";
  elements.comparisonBadge.textContent = "—";
  elements.currentBar.style.width = "0";
  elements.previousBar.style.width = "0";
  elements.currentBarValue.textContent = "—";
  elements.previousBarValue.textContent = "—";
  elements.comparisonInsight.textContent = "データ取得後に比較結果が表示されます。";
}

function updatePeriodLabels() {
  const label = RANGE_LABELS[elements.range.value] ?? elements.range.value;
  elements.comparisonPeriod.textContent = label;
  elements.statusPeriod.textContent = label;
}

function setConnectionState(isConnected) {
  elements.connectionStatus.textContent = isConnected ? "接続済み" : "接続エラー";
  elements.connectionStatus.classList.toggle("is-error", !isConnected);
  elements.statusDot.classList.toggle("is-error", !isConnected);
  elements.lastUpdated.textContent = isConnected
    ? new Intl.DateTimeFormat("ja-JP", { hour: "2-digit", minute: "2-digit", second: "2-digit" }).format(new Date())
    : "—";
}

function renderToday() {
  elements.date.textContent = new Intl.DateTimeFormat("ja-JP", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  }).format(new Date());
  updatePeriodLabels();
}

function openSidebar() {
  elements.sidebar.classList.add("is-open");
  document.body.classList.add("sidebar-open");
  elements.menuButton.setAttribute("aria-expanded", "true");
  elements.sidebar.querySelector("a")?.focus();
}

function closeSidebar() {
  elements.sidebar.classList.remove("is-open");
  document.body.classList.remove("sidebar-open");
  elements.menuButton.setAttribute("aria-expanded", "false");
}

function showError() {
  elements.alert.hidden = false;
}

function hideError() {
  elements.alert.hidden = true;
}

function createCell(text) {
  const cell = document.createElement("td");
  cell.textContent = text;
  return cell;
}

function formatYen(value) {
  return new Intl.NumberFormat("ja-JP", {
    style: "currency",
    currency: "JPY",
    maximumFractionDigits: 0,
  }).format(toFiniteNumber(value));
}

function formatNumber(value) {
  return new Intl.NumberFormat("ja-JP").format(toFiniteNumber(value));
}

function formatNullable(value, formatter) {
  return value === null ? "—" : formatter(value);
}

function toFiniteNumber(value) {
  const number = Number(value ?? 0);
  return Number.isFinite(number) ? number : 0;
}
