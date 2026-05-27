import React, { createContext, useContext, useState, useEffect } from "react";

export type Lang = "uk" | "en";

const TRANSLATIONS: Record<Lang, Record<string, string>> = {
  uk: {
    "app.title": "☢️ RadiationMonitor",
    "menu.dashboard": "📊 Панель",
    "menu.devices": "📱 Пристрої",
    "menu.readings": "📈 Показання",
    "menu.alerts": "🚨 Сигналізації",
    "menu.subscriptions": "🔔 Підписки",
    "menu.admin": "⚙️ Адмін панель",
    "menu.health": "❤️ Здоров'я пристроїв",
    "admin.title": "Адміністрація",
    "role.admin": "Адміністратор",
    "role.user": "Користувач",
    "button.logout": "Вийти",

    "stat.totalDevices": "Всього пристроїв",
    "stat.activeDevices": "Активних",
    "stat.unacknowledgedAlerts": "Нерозглянутих",
    "stat.criticalAlerts": "Критичних",

    "chart.readingsTitle": "📈 Показання (останні 50)",
    "chart.noReadings": "Немає показань",
    "chart.avg": "Середнє",

    "alerts.title": "🚨 Останні сигналізації",
    "alerts.level.warning": "⚠️ Попередження",
    "alerts.level.info": "ℹ️ Інформація",
    "alerts.level.danger": "🔴 Небезпека",
    "alerts.level.critical": "🚨 Критично",
    "alerts.emptyTitle": "Сигналізацій не знайдено",
    "alerts.emptyText": "За вибраними фільтрами сигналізацій немає",
    "alerts.refresh": "Оновити",
    "filters.all": "Всі",
    "status.unacknowledged": "Нерозглянуті",
    "status.acknowledged": "Розглянуті",

    "devices.myDevices": "📱 Мої пристрої",
    "devices.newDevice": "Новий пристрій",
    "devices.edit": "Редагувати",
    "devices.createDevice": "Створити пристрій",
    "devices.token": "DEVICE TOKEN",
    "devices.copyToken": "Скопіювати токен",
    "devices.created": "Створено",
    "devices.activate": "Увімкнути",
    "devices.deactivate": "Вимкнути",
    "devices.active": "Активний",
    "devices.inactive": "Неактивний",
    "devices.confirmDelete": "Видалити",
    "devices.createDevice": "Створити пристрій",
    "devices.table.name": "Назва",
    "devices.table.status": "Статус",
    "devices.table.location": "Локація",
    "devices.table.registered": "Реєстрація",
    "device.unknown": "Невідомий пристрій",

    "readings.filters.device": "Пристрій",
    "readings.filters.since": "Від дати",
    "readings.filters.limit": "Ліміт",
    "readings.update": "Оновити",
    "readings.stats.total": "Всього показань",
    "readings.stats.avg": "Середнє",
    "readings.stats.min": "Мінімум",
    "readings.stats.max": "Максимум",
    "readings.chartTitleAll": "📈 Графік (всі пристрої)",
    "readings.noDataPeriod": "Немає показань за обраний період",
    "readings.table.title": "📋 Таблиця показань",

    "subscriptions.title": "🔔 Підписки на сповіщення",
    "subscriptions.new": "Нова підписка",
    "subscriptions.channel": "Канал сповіщення",
    "subscriptions.selectChannel": "Оберіть канал",
    "subscriptions.levels": "Рівні сигналізацій",
    "subscriptions.threshold": "Поріг значення (µSv/h, необов'язково)",
    "subscriptions.create": "Створити",
    "subscriptions.cancel": "Скасувати",
    "subscriptions.emptyTitle": "Підписок немає",
    "subscriptions.emptyText": "Додайте підписку, щоб отримувати сповіщення",
    "subscriptions.enable": "Увімкнути",
    "subscriptions.disable": "Вимкнена",
    "subscriptions.removeConfirm": "Видалити підписку?",
    "subscriptions.created": "Створити підписку",

    "admin.title": "⚙️ Адмін-панель обчислень",
    "admin.tabs.cumulative": "☢️ Кумулятивна доза",
    "admin.tabs.ewma": "📈 EWMA згладжування",
    "admin.tabs.peaks": "🔺 Піки",
    "admin.tabs.history": "📜 Історія обчислень",
    "admin.parameters": "Параметри розрахунку",
    "admin.device": "Пристрій",
    "admin.fromDate": "Дата початку",
    "admin.toDate": "Дата кінця",
    "admin.compute": "Розрахувати",
    "admin.result": "Результат",
    "admin.cumulative_label": "µSv — кумулятивна доза",
    "admin.ewma.title": "EWMA результати",
    "admin.ewma.original": "Оригінал",
    "admin.ewma.ewma": "EWMA",
    "admin.peaks.find": "Знайти піки",
    "admin.peaks.notFound": "Піків не знайдено",
    "admin.history.title": "Журнал обчислень",
    "admin.history.empty": "Ще не було жодних обчислень",
    "admin.ewma.parameters": "EWMA параметри",
    "admin.peaks.title": "Виявлення піків",
    "admin.peaks.found": "Знайдені піки ({count})",
    "admin.peaks.window": "Вікно кластеру (хв)",
    "select.choose": "Оберіть...",
    "devices.emptyTitle": "Пристроїв немає",
    "devices.emptyText": "Створіть перший пристрій для моніторингу",

    "health.title": "💗 Стан пристроїв",
    "health.devicesCount": "{count} пристроїв",
    "health.refresh": "Оновити",
    "health.recompute": "Перерахувати",
    "health.selectDevice": "Оберіть пристрій",
    "health.noData": "Немає даних про здоров'я",
    "health.details": "Деталі",
    "health.lastReading": "Останнє показання:",
    "health.checked": "Перевірено:",
    "health.battery": "Батарея:",
    "health.uptime": "Час активності",
    "health.missing": "Пропущених даних",
    "health.errors": "Помилок",

    "login.title": "RadiationMonitor",
    "login.registerCreate": "Створіть новий акаунт",
    "login.loginPrompt": "Увійдіть до системи моніторингу",
    "login.name": "Ім'я",
    "login.email": "Email",
    "login.password": "Пароль",
    "login.submit.loading": "Завантаження...",
    "login.submit.register": "Зареєструватися",
    "login.submit.login": "Увійти",
    "login.haveAccount": "Вже маєте акаунт?",
    "login.noAccount": "Немає акаунту?",
    "login.switchToLogin": "Увійти",
    "login.switchToRegister": "Зареєструватися",

    "buttons.cancel": "Скасувати",
    "buttons.save": "Збереження...",
    "buttons.close": "Закрити",
    "buttons.update": "Оновити",
    "buttons.refresh": "Оновити",
    "buttons.confirm": "Підтвердити",
    "buttons.create": "Створити",
    "buttons.yes": "Так",
    "buttons.no": "Ні",
  },
  en: {
    "app.title": "☢️ RadiationMonitor",
    "menu.dashboard": "📊 Dashboard",
    "menu.devices": "📱 Devices",
    "menu.readings": "📈 Readings",
    "menu.alerts": "🚨 Alerts",
    "menu.subscriptions": "🔔 Subscriptions",
    "menu.admin": "⚙️ Admin",
    "menu.health": "❤️ Device Health",
    "admin.title": "Administration",
    "role.admin": "Administrator",
    "role.user": "User",
    "button.logout": "Logout",

    "stat.totalDevices": "Total devices",
    "stat.activeDevices": "Active",
    "stat.unacknowledgedAlerts": "Unacknowledged",
    "stat.criticalAlerts": "Critical",

    "chart.readingsTitle": "📈 Readings (last 50)",
    "chart.noReadings": "No readings",
    "chart.avg": "Average",

    "alerts.title": "🚨 Recent alerts",
    "alerts.level.warning": "⚠️ Warning",
    "alerts.level.info": "ℹ️ Info",
    "alerts.level.danger": "🔴 Danger",
    "alerts.level.critical": "🚨 Critical",
    "alerts.emptyTitle": "No alerts found",
    "alerts.emptyText": "No alerts for the selected filters",
    "alerts.refresh": "Refresh",
    "filters.all": "All",
    "status.unacknowledged": "Unacknowledged",
    "status.acknowledged": "Acknowledged",

    "devices.myDevices": "📱 My devices",
    "devices.newDevice": "New device",
    "devices.edit": "Edit",
    "devices.createDevice": "Create device",
    "devices.token": "DEVICE TOKEN",
    "devices.copyToken": "Copy token",
    "devices.created": "Created",
    "devices.activate": "Activate",
    "devices.deactivate": "Deactivate",
    "devices.active": "Active",
    "devices.inactive": "Inactive",
    "devices.confirmDelete": "Delete",
    "devices.createDevice": "Create device",
    "devices.table.name": "Name",
    "devices.table.status": "Status",
    "devices.table.location": "Location",
    "devices.table.registered": "Registered",
    "device.unknown": "Unknown device",

    "readings.filters.device": "Device",
    "readings.filters.since": "Since",
    "readings.filters.limit": "Limit",
    "readings.update": "Refresh",
    "readings.stats.total": "Total readings",
    "readings.stats.avg": "Average",
    "readings.stats.min": "Minimum",
    "readings.stats.max": "Maximum",
    "readings.chartTitleAll": "📈 Chart (all devices)",
    "readings.noDataPeriod": "No readings for the selected period",
    "readings.table.title": "📋 Readings table",

    "subscriptions.title": "🔔 Notification subscriptions",
    "subscriptions.new": "New subscription",
    "subscriptions.channel": "Channel",
    "subscriptions.selectChannel": "Select channel",
    "subscriptions.levels": "Alert levels",
    "subscriptions.threshold": "Threshold (µSv/h, optional)",
    "subscriptions.create": "Create",
    "subscriptions.cancel": "Cancel",
    "subscriptions.emptyTitle": "No subscriptions",
    "subscriptions.emptyText": "Add a subscription to receive alerts",
    "subscriptions.enable": "Enable",
    "subscriptions.disable": "Disabled",
    "subscriptions.removeConfirm": "Delete subscription?",
    "subscriptions.created": "Create subscription",

    "admin.title": "⚙️ Admin computations",
    "admin.tabs.cumulative": "☢️ Cumulative dose",
    "admin.tabs.ewma": "📈 EWMA smoothing",
    "admin.tabs.peaks": "🔺 Peaks",
    "admin.tabs.history": "📜 Computation history",
    "admin.parameters": "Computation parameters",
    "admin.device": "Device",
    "admin.fromDate": "From date",
    "admin.toDate": "To date",
    "admin.compute": "Compute",
    "admin.result": "Result",
    "admin.cumulative_label": "µSv — cumulative dose",
    "admin.ewma.title": "EWMA results",
    "admin.ewma.original": "Original",
    "admin.ewma.ewma": "EWMA",
    "admin.peaks.find": "Find peaks",
    "admin.peaks.notFound": "No peaks found",
    "admin.history.title": "Computation history",
    "admin.history.empty": "No computations yet",
    "admin.ewma.parameters": "EWMA parameters",
    "admin.peaks.title": "Detect peaks",
    "admin.peaks.found": "Found peaks ({count})",
    "admin.peaks.window": "Cluster window (min)",
    "select.choose": "Select...",
    "devices.emptyTitle": "No devices",
    "devices.emptyText": "Create your first device to start monitoring",

    "health.title": "💗 Device health",
    "health.devicesCount": "{count} devices",
    "health.refresh": "Refresh",
    "health.recompute": "Recompute",
    "health.selectDevice": "Select device",
    "health.noData": "No health data",
    "health.details": "Details",
    "health.lastReading": "Last reading:",
    "health.checked": "Checked:",
    "health.battery": "Battery:",
    "health.uptime": "Uptime",
    "health.missing": "Missing data",
    "health.errors": "Errors",

    "login.title": "RadiationMonitor",
    "login.registerCreate": "Create a new account",
    "login.loginPrompt": "Sign in to the monitoring system",
    "login.name": "Name",
    "login.email": "Email",
    "login.password": "Password",
    "login.submit.loading": "Loading...",
    "login.submit.register": "Register",
    "login.submit.login": "Sign in",
    "login.haveAccount": "Already have an account?",
    "login.noAccount": "Don't have an account?",
    "login.switchToLogin": "Sign in",
    "login.switchToRegister": "Register",

    "buttons.cancel": "Cancel",
    "buttons.save": "Saving...",
    "buttons.close": "Close",
    "buttons.update": "Update",
    "buttons.refresh": "Refresh",
    "buttons.confirm": "Confirm",
    "buttons.create": "Create",
    "buttons.yes": "Yes",
    "buttons.no": "No",
  },
};

interface I18nContextValue {
  lang: Lang;
  setLang: (l: Lang) => void;
  t: (key: string) => string;
}

const I18nContext = createContext<I18nContextValue | undefined>(undefined);

export function I18nProvider({ children }: { children: React.ReactNode }) {
  const [lang, setLangState] = useState<Lang>("uk");

  useEffect(() => {
    const saved = localStorage.getItem("lang") as Lang | null;
    if (saved && (saved === "uk" || saved === "en")) setLangState(saved);
  }, []);

  const setLang = (l: Lang) => {
    setLangState(l);
    try {
      localStorage.setItem("lang", l);
    } catch {}
  };

  const t = (key: string) => {
    return TRANSLATIONS[lang][key] ?? key;
  };

  return (
    <I18nContext.Provider value={{ lang, setLang, t }}>
      {children}
    </I18nContext.Provider>
  );
}

export function useI18n() {
  const ctx = useContext(I18nContext);
  if (!ctx) throw new Error("useI18n must be used within I18nProvider");
  return ctx;
}

export default I18nContext;
