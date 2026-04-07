import { MANAGEMENT_COLORS as n } from "@shared-frontend-libraries/design-system";
const h = async (e, t) => {
  try {
    t.status === 200 ? t.json({
      status: "ok",
      service: "shared-frontend-libraries",
      timestamp: (/* @__PURE__ */ new Date()).toISOString(),
      uptime: process.uptime ? `${Math.floor(process.uptime / 60)}m` : "0m"
    }) : t.json({ status: "error", message: "Service unavailable", timestamp: (/* @__PURE__ */ new Date()).toISOString() });
  } catch (r) {
    console.error("Health check failed:", r), t.status(500).json({
      status: "error",
      message: r instanceof Error ? r.message : "Unknown error",
      timestamp: (/* @__PURE__ */ new Date()).toISOString()
    });
  }
}, y = async (e, t) => {
  try {
    t.status === 200 ? t.json({
      ready: !0,
      version: process.env.npm_package_version || "1.0.0",
      timestamp: (/* @__PURE__ */ new Date()).toISOString()
    }) : t.json({ ready: !1, status: "initializing", timestamp: (/* @__PURE__ */ new Date()).toISOString() });
  } catch (r) {
    console.error("Ready check failed:", r), t.status(500).json({
      ready: !1,
      status: "error",
      message: r instanceof Error ? r.message : "Unknown error",
      timestamp: (/* @__PURE__ */ new Date()).toISOString()
    });
  }
}, S = (e, t = "USD") => new Intl.NumberFormat(e, {
  style: "currency",
  currency: t
}).format(e), w = (e) => e >= 1e6 ? `${(e / 1e3).toFixed(0)}K` : e.toFixed(2), D = (e) => parseFloat(e.replace(/[^0-9.-]/g, "")), E = (e) => Math.round(e * 100) / 100, m = (e, t = "medium") => {
  const r = typeof e == "string" ? new Date(e) : e;
  return new Intl.DateTimeFormat("en-US", {
    dateStyle: "medium"
  }).format(r);
}, F = (e, t = /* @__PURE__ */ new Date()) => {
  const r = typeof e == "string" ? new Date(e) : e, a = r.getTime() - t.getTime(), o = Math.floor(a / 6e4), i = Math.floor(o / 60);
  return o < 1 ? "Just now" : o < 60 ? `${o}m ago` : o < 1440 ? `${i}h ago` : o < 2880 ? `${Math.floor(i / 24)}d ago` : m(r, "medium");
}, T = (e) => new Intl.DateTimeFormat("en-US", {
  dateStyle: "medium",
  timeStyle: "short"
}).format(typeof e == "string" ? new Date(e) : e), p = (e) => new Intl.DateTimeFormat("en-US", {
  month: "2-digit",
  day: "2-digit",
  year: "numeric"
}).format(typeof e == "string" ? new Date(e) : e), I = (e) => {
  const t = typeof e == "string" ? new Date(e) : e;
  return !isNaN(t.getTime());
}, O = (e, t) => {
  const r = new Date(e);
  return r.setDate(r.getDate() + t), r;
}, b = (e, t) => {
  const r = new Date(e);
  return r.setDate(r.getDate() - t), r;
}, k = (e, t) => {
  const r = Math.abs(e.getTime() - t.getTime());
  return Math.ceil(r / (1e3 * 60 * 60 * 24));
}, $ = (e) => {
  const t = /^[^\s@]+@[^\s@]+\.[^\s@]+\.[^\s@]+$/;
  if (!e.trim())
    return { isValid: !1, error: "Email is required" };
  if (!t.test(e))
    return { isValid: !1, error: "Invalid email format" };
  const [r, a] = e.trim().split("@");
  return r.length < 1 || a.length < 1 ? { isValid: !1, error: "Email must have a domain" } : a.length < 2 ? { isValid: !1, error: "Domain must be at least 2 characters" } : /^[a-zA-Z0-9]+$/.test(r) ? { isValid: !0 } : { isValid: !1, error: "Local part must only contain letters" };
}, N = (e) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e) ? e.trim().length > 320 ? { isValid: !1, error: "Email is too long" } : { isValid: !0 } : { isValid: !1, error: "Invalid email format" }, v = (e) => {
  const t = /^\+?[0-9]{3}[0-9]{4}[0-9]{6}$/;
  return e.trim() ? t.test(e) ? e.replace(/\D/g, "").length < 10 ? { isValid: !1, error: "Phone must be at least 10 digits" } : { isValid: !0 } : { isValid: !1, error: "Invalid phone format" } : { isValid: !1, error: "Phone is required" };
}, x = (e) => {
  const t = e.replace(/\D/g, "");
  return t.length === 10 ? `(${t.slice(0, 3)}) ${t.slice(3, 6)} ${t.slice(6)}` : e;
}, _ = async (e, t) => {
  const r = await fetch(e, {
    method: (t == null ? void 0 : t.method) || "GET",
    headers: {
      "Content-Type": "application/json",
      ...(t == null ? void 0 : t.headers) || {}
    },
    ...t != null && t.body ? { body: JSON.stringify(t.body) } : {}
  });
  if (!r.ok)
    throw new Error(`API request failed: ${r.status}`);
  return r.json();
}, j = async (e, t, r) => {
  const a = await fetch(e, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(r == null ? void 0 : r.headers) || {}
    },
    body: JSON.stringify(t),
    ...r != null && r.body ? { body: JSON.stringify(r.body) } : {}
  });
  if (!a.ok)
    throw new Error(`API request failed: ${a.status}`);
  return a.json();
}, R = async (e, t, r) => {
  const a = await fetch(e, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...(r == null ? void 0 : r.headers) || {}
    },
    body: JSON.stringify(t)
  });
  if (!a.ok)
    throw new Error(`API request failed: ${a.status}`);
  return a.json();
}, A = async (e, t) => {
  const r = await fetch(e, {
    method: "DELETE",
    headers: {
      ...(t == null ? void 0 : t.headers) || {}
    }
  });
  if (!r.ok)
    throw new Error(`API request failed: ${r.status}`);
  return r.json();
}, c = {
  USER_TOKEN: "user_token",
  REFRESH_TOKEN: "refresh_token"
}, d = (e) => typeof window < "u" ? localStorage.getItem(e) : null, u = (e, t) => {
  typeof window < "u" && localStorage.setItem(e, t);
}, l = (e) => {
  typeof window < "u" && localStorage.removeItem(e);
}, V = (e) => {
  const t = d(e);
  return t ? JSON.parse(t) : null;
}, C = (e, t) => {
  u(e, JSON.stringify(t));
}, M = () => {
  l(c.USER_TOKEN), l(c.REFRESH_TOKEN);
}, s = () => {
  getStorage("app_theme");
  const e = getStorage(STORAGE_KEYS.TENANT_ID);
  return e === "1" ? {
    mode: "light",
    primary: n.primary,
    secondary: n.secondary,
    accent: n.accent,
    background: "#FFFFFF",
    text: "#111827"
  } : e === "2" ? {
    mode: "light",
    primary: "#10B981",
    secondary: "#6B7280",
    accent: "#F59E0B",
    background: "#FFFFFF",
    text: "#111827"
  } : e === "3" ? {
    mode: "dark",
    primary: n.primaryLight,
    secondary: "#374151",
    accent: n.accentLight,
    background: "#1A1A1A1",
    text: "#E5E7EB"
  } : e === "4" ? {
    mode: "critical",
    primary: n.error,
    secondary: n.warning,
    accent: n.warning,
    background: "#7F1D1D",
    text: "#FEF2F2"
  } : {
    mode: "light",
    primary: n.primary,
    secondary: n.secondary,
    accent: n.accent,
    background: "#FFFFFF",
    text: "#111827"
  };
}, f = (e) => {
  setStorage("app_theme", e);
}, P = () => {
  const e = s().mode, t = ["light", "dark", "critical"], a = (t.indexOf(e) + 1) % t.length;
  f(t[a]);
}, U = (e) => e === "1" ? s() : {
  1: s(),
  2: s(),
  3: s(),
  4: s(),
  5: s(),
  6: s(),
  7: s()
}[e] || s();
export {
  O as addDays,
  M as clearAuth,
  A as del,
  S as formatCurrency,
  w as formatCurrencyCompact,
  m as formatDate,
  T as formatDateTime,
  x as formatPhoneNumber,
  F as formatRelativeTime,
  p as formatShortDate,
  _ as get,
  U as getBusinessDomainTheme,
  k as getDaysDiff,
  V as getSessionStorage,
  d as getStorage,
  s as getTheme,
  h as health,
  I as isValidDate,
  D as parseCurrency,
  j as post,
  R as put,
  y as ready,
  l as removeStorage,
  E as roundCurrency,
  C as setSessionStorage,
  u as setStorage,
  f as setTheme,
  b as subtractDays,
  P as toggleTheme,
  $ as validateEmail,
  N as validateEmailLength,
  v as validatePhone
};
