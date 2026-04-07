import { useRef as C, useState as P, useCallback as f, useEffect as W, createContext as x, useContext as E, useMemo as p } from "react";
import { jsx as S } from "react/jsx-runtime";
import { QueryClient as N, useQuery as O, QueryClientProvider as A } from "@tanstack/react-query";
const M = (t) => {
  const { config: e } = t, n = C(null), r = C(null), c = C(null), s = C(null), [a, i] = P({
    status: "disconnected",
    connectionCount: 0
  }), l = f((o, u) => {
    var y;
    i((g) => ({
      ...g,
      status: o,
      error: u
    })), (y = t.onStatusChange) == null || y.call(t, {
      status: o,
      error: u,
      connectionCount: a.connectionCount + 1
    });
  }, [a.connectionCount, t]), h = f(() => {
    try {
      l("connecting");
      const o = new WebSocket(e.url);
      n.current = o, o.onopen = () => {
        var u;
        l("connected"), (u = t.onConnect) == null || u.call(t);
      }, o.onmessage = (u) => {
        var g;
        const y = JSON.parse(u.data);
        (g = t.onMessage) == null || g.call(t, y), i((R) => ({
          ...R,
          lastMessage: y
        }));
      }, o.onerror = () => {
        l("error", "Connection error"), b();
      }, o.onclose = () => {
        var u;
        l("disconnected"), (u = t.onDisconnect) == null || u.call(t), b();
      }, v(o);
    } catch (o) {
      l("error", o instanceof Error ? o.message : "Failed to connect"), b();
    }
  }, [e.url, l, b, v, t]), d = f(() => {
    n.current && (n.current.close(), n.current = null, l("disconnected"));
  }, [l]), I = f((o) => {
    var u;
    ((u = n.current) == null ? void 0 : u.readyState) === WebSocket.OPEN && n.current.send(JSON.stringify(o));
  }, []), b = f(() => {
    r.current && clearTimeout(r.current);
    const o = () => {
      const u = e.maxReconnectAttempts || 5;
      a.connectionCount >= u || (l("reconnecting"), r.current = setTimeout(() => {
        h();
      }, e.reconnectInterval || 3e3));
    };
    r.current = setTimeout(o, 1e3);
  }, [e, a.connectionCount, l, h]), v = f((o) => {
    if (e.heartbeatInterval) {
      const u = () => {
        if (o.readyState === WebSocket.OPEN)
          try {
            o.send(JSON.stringify({ type: "ping" }));
          } catch {
          }
      };
      s.current = setInterval(u, e.heartbeatInterval);
    }
  }, [e.heartbeatInterval]), T = f(() => {
    c.current && clearInterval(c.current), s.current && clearInterval(s.current);
  }, []);
  return W(() => () => {
    d(), T(), r.current && clearTimeout(r.current);
  }, [d, T]), {
    connect: h,
    disconnect: d,
    send: I,
    status: a.status,
    error: a.error,
    lastMessage: a.lastMessage
  };
}, G = (t) => {
  const { config: e = t } = t, [n, r] = P({
    isPolling: !1,
    lastPollTime: 0
  }), c = f(async () => {
    var a, i, l;
    if (e.enabled)
      try {
        r((d) => ({ ...d, isPolling: !0 }));
        const h = await ((a = t.onPoll) == null ? void 0 : a.call(t));
        (i = t.onDataChange) == null || i.call(t, h);
      } catch (h) {
        (l = t.onError) == null || l.call(t, h instanceof Error ? h.message : "Polling failed");
      } finally {
        r((h) => ({ ...h, isPolling: !1, lastPollTime: Date.now() }));
      }
  }, [e.enabled, e.interval, t.onPoll, t.onDataChange, t.onError]), s = f(() => {
    e.interval && c();
  }, [c, e.interval]);
  return {
    isPolling: n.isPolling,
    lastPollTime: n.lastPollTime,
    error: n.error,
    start: s
  };
}, D = x({
  ws: null,
  onlineUsers: /* @__PURE__ */ new Map(),
  data: {
    notifications: [],
    alerts: [],
    users: /* @__PURE__ */ new Map()
  }
}), K = (t = {}) => {
  const { eventType: e } = t, n = E(D), r = f((a) => {
    var i;
    (i = n.publish) == null || i.call(n, { type: e || "custom", data: a });
  }, [n.publish, e]), c = f((a) => {
    var i;
    return (i = n.subscribe) == null ? void 0 : i.call(n, { type: e || "custom", handler: a });
  }, [n.subscribe, e]), s = f((a) => {
    var i;
    return (i = n.unsubscribe) == null ? void 0 : i.call(n, { type: e || "custom", handler: a });
  }, [n.unsubscribe, e]);
  return {
    publish: r,
    subscribe: c,
    unsubscribe: s
  };
}, V = (t = {}) => {
  const e = useContext(D), n = f(async () => {
    var r;
    try {
      const s = await (await fetch("/api/realtime/data")).json();
      (r = t.onDataUpdate) == null || r.call(t, s);
    } catch (c) {
      console.error("Failed to fetch real-time data:", c);
    }
  }, [t.refreshInterval, t.onDataUpdate]);
  return {
    data: e.data,
    refresh: n
  };
}, k = x(null), U = ({
  children: t,
  wsUrl: e,
  onMessage: n,
  onConnect: r,
  onDisconnect: c
}) => {
  const s = p(
    () => ({
      url: e || process.env.NEXT_PUBLIC_WS_URL || "ws://localhost:8080",
      reconnectInterval: 5e3,
      maxReconnectAttempts: 5
    }),
    [e]
  ), a = M({
    config: s,
    onMessage: n,
    onConnect: r,
    onDisconnect: c
  });
  return /* @__PURE__ */ S(k.Provider, { value: { ws: a, isConnected: a.status === "connected" }, children: t });
}, L = () => {
  const t = E(k);
  if (!t)
    throw new Error("useWebSocketContext must be used within WebSocketProvider");
  return t;
}, F = new N({
  defaultOptions: {
    refetchOnWindowFocus: !1,
    retry: 1,
    staleTime: 3e4
  }
}), X = ({ children: t }) => {
  const [e, n] = P({
    isConnected: !1,
    lastUpdate: Date.now(),
    onlineUsers: /* @__PURE__ */ new Map()
  }), r = f((s) => {
    if (s.type === "users:update") {
      const a = s.payload;
      n((i) => ({
        ...i,
        onlineUsers: new Map(
          Object.entries(a).map(([l, h]) => {
            var d;
            return [
              l,
              {
                ...h,
                status: h.status || ((d = i.onlineUsers.get(l)) == null ? void 0 : d.status) || "offline",
                lastSeen: Date.now()
              }
            ];
          })
        )
      }));
    }
  }, []), { data: c } = O({
    queryKey: ["notifications"],
    queryFn: async () => (await fetch("/api/notifications")).json(),
    enabled: e.isConnected,
    refetchInterval: 1e4
  });
  return /* @__PURE__ */ S(A, { client: F, children: /* @__PURE__ */ S(U, { onMessage: r, children: t }) });
}, $ = () => {
  const { isConnected: t } = L();
  return { isConnected: t };
}, m = class m {
  constructor() {
    this.connections = /* @__PURE__ */ new Map(), this.listeners = /* @__PURE__ */ new Set();
  }
  static getInstance() {
    return m.instance || (m.instance = new m()), m.instance;
  }
  connect(e, n) {
    this.connections.set(e, n), this.notifyListeners({ id: e, isConnected: !0, lastConnectedAt: /* @__PURE__ */ new Date() });
  }
  disconnect(e) {
    const n = this.connections.get(e);
    n && (n.close(), this.connections.delete(e), this.notifyListeners({ id: e, isConnected: !1 }));
  }
  subscribe(e) {
    return this.listeners.add(e), this.notifyListeners(this.getCurrentState()), () => this.unsubscribe(e);
  }
  unsubscribe(e) {
    this.listeners.delete(e);
  }
  getCurrentState() {
    const e = Array.from(this.connections.values());
    if (e.length === 0)
      return { isConnected: !1, isReconnecting: !1 };
    const n = e.some((s) => {
      try {
        return s.readyState === WebSocket.OPEN || s.readyState === "open";
      } catch {
        return !1;
      }
    }), r = e.some((s) => {
      try {
        return s.readyState === WebSocket.CONNECTING || s.readyState === "connecting";
      } catch {
        return !1;
      }
    }), c = e.find((s) => {
      try {
        return s.readyState === WebSocket.OPEN || s.readyState === "open";
      } catch {
        return !1;
      }
    });
    return {
      isConnected: n,
      isReconnecting: r,
      lastConnectedAt: c == null ? void 0 : c.lastConnectedAt,
      connectionId: c == null ? void 0 : c.url
    };
  }
  getConnectedCount() {
    return Array.from(this.connections.values()).filter((e) => {
      const n = e.readyState;
      return n === WebSocket.OPEN || n === "open";
    }).length;
  }
  notifyListeners(e) {
    this.listeners.forEach((n) => {
      try {
        n(e);
      } catch (r) {
        console.error("Error notifying listener:", r);
      }
    });
  }
  getConnectedIds() {
    return Array.from(this.connections.keys()).filter((e) => {
      const n = this.connections.get(e);
      return n && (n.readyState === WebSocket.OPEN || n.readyState === "open");
    });
  }
};
m.instance = null;
let w = m;
const j = () => w.getInstance();
class z {
  constructor() {
    this.manager = j();
  }
  publish(e, n = {}) {
    if (this.manager.getCurrentState().isConnected) {
      const c = {
        ...e,
        timestamp: Date.now()
      };
      this.manager.notifyListeners({ ...c });
    }
  }
  subscribe(e) {
    this.manager.subscribe((n) => {
      n.isConnected && e(data);
    });
  }
  unsubscribe(e) {
    this.manager.unsubscribe(e);
  }
}
class q {
  constructor() {
    this.attempt = 0, this.maxAttempts = 5, this.baseDelay = 1e3, this.maxDelay = 3e4;
  }
  async reconnect() {
    if (this.attempt >= this.maxAttempts)
      return !1;
    const e = Math.min(this.baseDelay * Math.pow(2, this.attempt), this.maxDelay);
    return await new Promise((n) => {
      setTimeout(() => {
        this.attempt++, n(!0);
      }, e);
    }), this.attempt < this.maxAttempts;
  }
  reset() {
    this.attempt = 0;
  }
}
class B {
  constructor(e = 3e3) {
    this.delay = e;
  }
  async reconnect() {
    return await new Promise((e) => {
      setTimeout(() => {
        e(!0);
      }, this.delay);
    }), !0;
  }
}
const Y = (t = "exponential") => t === "exponential" ? new q() : new B();
export {
  w as ConnectionManager,
  z as EventPublisher,
  q as ExponentialBackoffStrategy,
  B as FixedDelayStrategy,
  X as RealTimeProvider,
  U as WebSocketProvider,
  j as useConnection,
  K as useEventBus,
  G as usePolling,
  $ as useRealTime,
  V as useRealTimeData,
  Y as useReconnectionStrategy,
  M as useWebSocket,
  L as useWebSocketContext
};
