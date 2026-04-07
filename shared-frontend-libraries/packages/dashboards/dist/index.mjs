import * as xe from "react";
import Wr, { forwardRef as qo, useContext as Ho, useState as on, useEffect as Ko, useCallback as Xo } from "react";
import { Box as le, Typography as be, Grid as Nr, Paper as Gn, CardContent as zr, useTheme as qn, IconButton as Jo, alpha as yr, Card as kt, Chip as xt, LinearProgress as Qo } from "@mui/material";
import { TrendingUp as an, TrendingDown as Zo, CheckCircle as sn, BarChart as ei } from "@mui/icons-material";
import { MANAGEMENT_COLORS as Dr } from "@shared-frontend-libraries/design-system";
import { motion as St, AnimatePresence as ri } from "framer-motion";
function ti(e) {
  return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e;
}
var Et = { exports: {} }, fr = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var cn;
function ni() {
  if (cn) return fr;
  cn = 1;
  var e = Wr, r = Symbol.for("react.element"), t = Symbol.for("react.fragment"), n = Object.prototype.hasOwnProperty, o = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, i = { key: !0, ref: !0, __self: !0, __source: !0 };
  function c(l, f, d) {
    var p, h = {}, m = null, C = null;
    d !== void 0 && (m = "" + d), f.key !== void 0 && (m = "" + f.key), f.ref !== void 0 && (C = f.ref);
    for (p in f) n.call(f, p) && !i.hasOwnProperty(p) && (h[p] = f[p]);
    if (l && l.defaultProps) for (p in f = l.defaultProps, f) h[p] === void 0 && (h[p] = f[p]);
    return { $$typeof: r, type: l, key: m, ref: C, props: h, _owner: o.current };
  }
  return fr.Fragment = t, fr.jsx = c, fr.jsxs = c, fr;
}
var dr = {};
/**
 * @license React
 * react-jsx-runtime.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var ln;
function oi() {
  return ln || (ln = 1, process.env.NODE_ENV !== "production" && function() {
    var e = Wr, r = Symbol.for("react.element"), t = Symbol.for("react.portal"), n = Symbol.for("react.fragment"), o = Symbol.for("react.strict_mode"), i = Symbol.for("react.profiler"), c = Symbol.for("react.provider"), l = Symbol.for("react.context"), f = Symbol.for("react.forward_ref"), d = Symbol.for("react.suspense"), p = Symbol.for("react.suspense_list"), h = Symbol.for("react.memo"), m = Symbol.for("react.lazy"), C = Symbol.for("react.offscreen"), y = Symbol.iterator, s = "@@iterator";
    function v(u) {
      if (u === null || typeof u != "object")
        return null;
      var S = y && u[y] || u[s];
      return typeof S == "function" ? S : null;
    }
    var w = e.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function P(u) {
      {
        for (var S = arguments.length, O = new Array(S > 1 ? S - 1 : 0), j = 1; j < S; j++)
          O[j - 1] = arguments[j];
        x("error", u, O);
      }
    }
    function x(u, S, O) {
      {
        var j = w.ReactDebugCurrentFrame, J = j.getStackAddendum();
        J !== "" && (S += "%s", O = O.concat([J]));
        var oe = O.map(function(G) {
          return String(G);
        });
        oe.unshift("Warning: " + S), Function.prototype.apply.call(console[u], console, oe);
      }
    }
    var _ = !1, b = !1, I = !1, k = !1, ue = !1, ee;
    ee = Symbol.for("react.module.reference");
    function a(u) {
      return !!(typeof u == "string" || typeof u == "function" || u === n || u === i || ue || u === o || u === d || u === p || k || u === C || _ || b || I || typeof u == "object" && u !== null && (u.$$typeof === m || u.$$typeof === h || u.$$typeof === c || u.$$typeof === l || u.$$typeof === f || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      u.$$typeof === ee || u.getModuleId !== void 0));
    }
    function $(u, S, O) {
      var j = u.displayName;
      if (j)
        return j;
      var J = S.displayName || S.name || "";
      return J !== "" ? O + "(" + J + ")" : O;
    }
    function B(u) {
      return u.displayName || "Context";
    }
    function M(u) {
      if (u == null)
        return null;
      if (typeof u.tag == "number" && P("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof u == "function")
        return u.displayName || u.name || null;
      if (typeof u == "string")
        return u;
      switch (u) {
        case n:
          return "Fragment";
        case t:
          return "Portal";
        case i:
          return "Profiler";
        case o:
          return "StrictMode";
        case d:
          return "Suspense";
        case p:
          return "SuspenseList";
      }
      if (typeof u == "object")
        switch (u.$$typeof) {
          case l:
            var S = u;
            return B(S) + ".Consumer";
          case c:
            var O = u;
            return B(O._context) + ".Provider";
          case f:
            return $(u, u.render, "ForwardRef");
          case h:
            var j = u.displayName || null;
            return j !== null ? j : M(u.type) || "Memo";
          case m: {
            var J = u, oe = J._payload, G = J._init;
            try {
              return M(G(oe));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var ne = Object.assign, se = 0, ye, Se, je, Be, E, A, V;
    function L() {
    }
    L.__reactDisabledLog = !0;
    function N() {
      {
        if (se === 0) {
          ye = console.log, Se = console.info, je = console.warn, Be = console.error, E = console.group, A = console.groupCollapsed, V = console.groupEnd;
          var u = {
            configurable: !0,
            enumerable: !0,
            value: L,
            writable: !0
          };
          Object.defineProperties(console, {
            info: u,
            log: u,
            warn: u,
            error: u,
            group: u,
            groupCollapsed: u,
            groupEnd: u
          });
        }
        se++;
      }
    }
    function Y() {
      {
        if (se--, se === 0) {
          var u = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: ne({}, u, {
              value: ye
            }),
            info: ne({}, u, {
              value: Se
            }),
            warn: ne({}, u, {
              value: je
            }),
            error: ne({}, u, {
              value: Be
            }),
            group: ne({}, u, {
              value: E
            }),
            groupCollapsed: ne({}, u, {
              value: A
            }),
            groupEnd: ne({}, u, {
              value: V
            })
          });
        }
        se < 0 && P("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var D = w.ReactCurrentDispatcher, F;
    function W(u, S, O) {
      {
        if (F === void 0)
          try {
            throw Error();
          } catch (J) {
            var j = J.stack.trim().match(/\n( *(at )?)/);
            F = j && j[1] || "";
          }
        return `
` + F + u;
      }
    }
    var q = !1, z;
    {
      var Ee = typeof WeakMap == "function" ? WeakMap : Map;
      z = new Ee();
    }
    function R(u, S) {
      if (!u || q)
        return "";
      {
        var O = z.get(u);
        if (O !== void 0)
          return O;
      }
      var j;
      q = !0;
      var J = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var oe;
      oe = D.current, D.current = null, N();
      try {
        if (S) {
          var G = function() {
            throw Error();
          };
          if (Object.defineProperty(G.prototype, "props", {
            set: function() {
              throw Error();
            }
          }), typeof Reflect == "object" && Reflect.construct) {
            try {
              Reflect.construct(G, []);
            } catch (we) {
              j = we;
            }
            Reflect.construct(u, [], G);
          } else {
            try {
              G.call();
            } catch (we) {
              j = we;
            }
            u.call(G.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (we) {
            j = we;
          }
          u();
        }
      } catch (we) {
        if (we && j && typeof we.stack == "string") {
          for (var U = we.stack.split(`
`), Te = j.stack.split(`
`), pe = U.length - 1, me = Te.length - 1; pe >= 1 && me >= 0 && U[pe] !== Te[me]; )
            me--;
          for (; pe >= 1 && me >= 0; pe--, me--)
            if (U[pe] !== Te[me]) {
              if (pe !== 1 || me !== 1)
                do
                  if (pe--, me--, me < 0 || U[pe] !== Te[me]) {
                    var Pe = `
` + U[pe].replace(" at new ", " at ");
                    return u.displayName && Pe.includes("<anonymous>") && (Pe = Pe.replace("<anonymous>", u.displayName)), typeof u == "function" && z.set(u, Pe), Pe;
                  }
                while (pe >= 1 && me >= 0);
              break;
            }
        }
      } finally {
        q = !1, D.current = oe, Y(), Error.prepareStackTrace = J;
      }
      var Ze = u ? u.displayName || u.name : "", qe = Ze ? W(Ze) : "";
      return typeof u == "function" && z.set(u, qe), qe;
    }
    function Ae(u, S, O) {
      return R(u, !1);
    }
    function Je(u) {
      var S = u.prototype;
      return !!(S && S.isReactComponent);
    }
    function Ge(u, S, O) {
      if (u == null)
        return "";
      if (typeof u == "function")
        return R(u, Je(u));
      if (typeof u == "string")
        return W(u);
      switch (u) {
        case d:
          return W("Suspense");
        case p:
          return W("SuspenseList");
      }
      if (typeof u == "object")
        switch (u.$$typeof) {
          case f:
            return Ae(u.render);
          case h:
            return Ge(u.type, S, O);
          case m: {
            var j = u, J = j._payload, oe = j._init;
            try {
              return Ge(oe(J), S, O);
            } catch {
            }
          }
        }
      return "";
    }
    var ur = Object.prototype.hasOwnProperty, Ut = {}, Yt = w.ReactDebugCurrentFrame;
    function Rr(u) {
      if (u) {
        var S = u._owner, O = Ge(u.type, u._source, S ? S.type : null);
        Yt.setExtraStackFrame(O);
      } else
        Yt.setExtraStackFrame(null);
    }
    function wo(u, S, O, j, J) {
      {
        var oe = Function.call.bind(ur);
        for (var G in u)
          if (oe(u, G)) {
            var U = void 0;
            try {
              if (typeof u[G] != "function") {
                var Te = Error((j || "React class") + ": " + O + " type `" + G + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof u[G] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw Te.name = "Invariant Violation", Te;
              }
              U = u[G](S, G, j, O, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (pe) {
              U = pe;
            }
            U && !(U instanceof Error) && (Rr(J), P("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", j || "React class", O, G, typeof U), Rr(null)), U instanceof Error && !(U.message in Ut) && (Ut[U.message] = !0, Rr(J), P("Failed %s type: %s", O, U.message), Rr(null));
          }
      }
    }
    var _o = Array.isArray;
    function ot(u) {
      return _o(u);
    }
    function Ro(u) {
      {
        var S = typeof Symbol == "function" && Symbol.toStringTag, O = S && u[Symbol.toStringTag] || u.constructor.name || "Object";
        return O;
      }
    }
    function Oo(u) {
      try {
        return Gt(u), !1;
      } catch {
        return !0;
      }
    }
    function Gt(u) {
      return "" + u;
    }
    function qt(u) {
      if (Oo(u))
        return P("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", Ro(u)), Gt(u);
    }
    var Ht = w.ReactCurrentOwner, $o = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, Kt, Xt;
    function Ao(u) {
      if (ur.call(u, "ref")) {
        var S = Object.getOwnPropertyDescriptor(u, "ref").get;
        if (S && S.isReactWarning)
          return !1;
      }
      return u.ref !== void 0;
    }
    function Po(u) {
      if (ur.call(u, "key")) {
        var S = Object.getOwnPropertyDescriptor(u, "key").get;
        if (S && S.isReactWarning)
          return !1;
      }
      return u.key !== void 0;
    }
    function ko(u, S) {
      typeof u.ref == "string" && Ht.current;
    }
    function Io(u, S) {
      {
        var O = function() {
          Kt || (Kt = !0, P("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", S));
        };
        O.isReactWarning = !0, Object.defineProperty(u, "key", {
          get: O,
          configurable: !0
        });
      }
    }
    function jo(u, S) {
      {
        var O = function() {
          Xt || (Xt = !0, P("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", S));
        };
        O.isReactWarning = !0, Object.defineProperty(u, "ref", {
          get: O,
          configurable: !0
        });
      }
    }
    var Mo = function(u, S, O, j, J, oe, G) {
      var U = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: r,
        // Built-in properties that belong on the element
        type: u,
        key: S,
        ref: O,
        props: G,
        // Record the component responsible for creating this element.
        _owner: oe
      };
      return U._store = {}, Object.defineProperty(U._store, "validated", {
        configurable: !1,
        enumerable: !1,
        writable: !0,
        value: !1
      }), Object.defineProperty(U, "_self", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: j
      }), Object.defineProperty(U, "_source", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: J
      }), Object.freeze && (Object.freeze(U.props), Object.freeze(U)), U;
    };
    function No(u, S, O, j, J) {
      {
        var oe, G = {}, U = null, Te = null;
        O !== void 0 && (qt(O), U = "" + O), Po(S) && (qt(S.key), U = "" + S.key), Ao(S) && (Te = S.ref, ko(S, J));
        for (oe in S)
          ur.call(S, oe) && !$o.hasOwnProperty(oe) && (G[oe] = S[oe]);
        if (u && u.defaultProps) {
          var pe = u.defaultProps;
          for (oe in pe)
            G[oe] === void 0 && (G[oe] = pe[oe]);
        }
        if (U || Te) {
          var me = typeof u == "function" ? u.displayName || u.name || "Unknown" : u;
          U && Io(G, me), Te && jo(G, me);
        }
        return Mo(u, U, Te, J, j, Ht.current, G);
      }
    }
    var it = w.ReactCurrentOwner, Jt = w.ReactDebugCurrentFrame;
    function Qe(u) {
      if (u) {
        var S = u._owner, O = Ge(u.type, u._source, S ? S.type : null);
        Jt.setExtraStackFrame(O);
      } else
        Jt.setExtraStackFrame(null);
    }
    var at;
    at = !1;
    function st(u) {
      return typeof u == "object" && u !== null && u.$$typeof === r;
    }
    function Qt() {
      {
        if (it.current) {
          var u = M(it.current.type);
          if (u)
            return `

Check the render method of \`` + u + "`.";
        }
        return "";
      }
    }
    function Do(u) {
      return "";
    }
    var Zt = {};
    function Fo(u) {
      {
        var S = Qt();
        if (!S) {
          var O = typeof u == "string" ? u : u.displayName || u.name;
          O && (S = `

Check the top-level render call using <` + O + ">.");
        }
        return S;
      }
    }
    function en(u, S) {
      {
        if (!u._store || u._store.validated || u.key != null)
          return;
        u._store.validated = !0;
        var O = Fo(S);
        if (Zt[O])
          return;
        Zt[O] = !0;
        var j = "";
        u && u._owner && u._owner !== it.current && (j = " It was passed a child from " + M(u._owner.type) + "."), Qe(u), P('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', O, j), Qe(null);
      }
    }
    function rn(u, S) {
      {
        if (typeof u != "object")
          return;
        if (ot(u))
          for (var O = 0; O < u.length; O++) {
            var j = u[O];
            st(j) && en(j, S);
          }
        else if (st(u))
          u._store && (u._store.validated = !0);
        else if (u) {
          var J = v(u);
          if (typeof J == "function" && J !== u.entries)
            for (var oe = J.call(u), G; !(G = oe.next()).done; )
              st(G.value) && en(G.value, S);
        }
      }
    }
    function Bo(u) {
      {
        var S = u.type;
        if (S == null || typeof S == "string")
          return;
        var O;
        if (typeof S == "function")
          O = S.propTypes;
        else if (typeof S == "object" && (S.$$typeof === f || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        S.$$typeof === h))
          O = S.propTypes;
        else
          return;
        if (O) {
          var j = M(S);
          wo(O, u.props, "prop", j, u);
        } else if (S.PropTypes !== void 0 && !at) {
          at = !0;
          var J = M(S);
          P("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", J || "Unknown");
        }
        typeof S.getDefaultProps == "function" && !S.getDefaultProps.isReactClassApproved && P("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function Lo(u) {
      {
        for (var S = Object.keys(u.props), O = 0; O < S.length; O++) {
          var j = S[O];
          if (j !== "children" && j !== "key") {
            Qe(u), P("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", j), Qe(null);
            break;
          }
        }
        u.ref !== null && (Qe(u), P("Invalid attribute `ref` supplied to `React.Fragment`."), Qe(null));
      }
    }
    var tn = {};
    function nn(u, S, O, j, J, oe) {
      {
        var G = a(u);
        if (!G) {
          var U = "";
          (u === void 0 || typeof u == "object" && u !== null && Object.keys(u).length === 0) && (U += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var Te = Do();
          Te ? U += Te : U += Qt();
          var pe;
          u === null ? pe = "null" : ot(u) ? pe = "array" : u !== void 0 && u.$$typeof === r ? (pe = "<" + (M(u.type) || "Unknown") + " />", U = " Did you accidentally export a JSX literal instead of a component?") : pe = typeof u, P("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", pe, U);
        }
        var me = No(u, S, O, J, oe);
        if (me == null)
          return me;
        if (G) {
          var Pe = S.children;
          if (Pe !== void 0)
            if (j)
              if (ot(Pe)) {
                for (var Ze = 0; Ze < Pe.length; Ze++)
                  rn(Pe[Ze], u);
                Object.freeze && Object.freeze(Pe);
              } else
                P("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              rn(Pe, u);
        }
        if (ur.call(S, "key")) {
          var qe = M(u), we = Object.keys(S).filter(function(Go) {
            return Go !== "key";
          }), ct = we.length > 0 ? "{key: someKey, " + we.join(": ..., ") + ": ...}" : "{key: someKey}";
          if (!tn[qe + ct]) {
            var Yo = we.length > 0 ? "{" + we.join(": ..., ") + ": ...}" : "{}";
            P(`A props object containing a "key" prop is being spread into JSX:
  let props = %s;
  <%s {...props} />
React keys must be passed directly to JSX without using spread:
  let props = %s;
  <%s key={someKey} {...props} />`, ct, qe, Yo, qe), tn[qe + ct] = !0;
          }
        }
        return u === n ? Lo(me) : Bo(me), me;
      }
    }
    function Vo(u, S, O) {
      return nn(u, S, O, !0);
    }
    function Wo(u, S, O) {
      return nn(u, S, O, !1);
    }
    var zo = Wo, Uo = Vo;
    dr.Fragment = n, dr.jsx = zo, dr.jsxs = Uo;
  }()), dr;
}
process.env.NODE_ENV === "production" ? Et.exports = ni() : Et.exports = oi();
var T = Et.exports;
const Dc = ({ refreshInterval: e = 3e4 }) => {
  const r = [
    { label: "Total Requests", value: "24,532", trend: "up", icon: /* @__PURE__ */ T.jsx(an, {}) },
    { label: "Active Users", value: "1,847", trend: "up", icon: /* @__PURE__ */ T.jsx(sn, {}) },
    { label: "Avg Response Time", value: "45s", trend: "down", icon: /* @__PURE__ */ T.jsx(ei, {}) },
    { label: "Service Health", value: "98.5%", trend: "up", icon: /* @__PURE__ */ T.jsx(sn, {}) }
  ];
  return /* @__PURE__ */ T.jsxs(le, { sx: { p: 3 }, children: [
    /* @__PURE__ */ T.jsx(be, { variant: "h4", gutterBottom: !0, sx: { fontWeight: 600, color: "#111827" }, children: "Global Headquarters Dashboard" }),
    /* @__PURE__ */ T.jsx(Nr, { container: !0, spacing: 3, children: r.map((t, n) => /* @__PURE__ */ T.jsx(Nr, { item: !0, xs: 12, sm: 6, children: /* @__PURE__ */ T.jsx(Gn, { sx: { height: "100%", p: 2, borderRadius: 2 }, children: /* @__PURE__ */ T.jsxs(zr, { children: [
      /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }, children: [
        /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", alignItems: "center", gap: 2 }, children: [
          t.icon,
          /* @__PURE__ */ T.jsx(be, { variant: "h6", sx: { color: "#111827", fontWeight: 600 }, children: t.value })
        ] }),
        /* @__PURE__ */ T.jsx(le, { sx: { flex: 1, textAlign: "right" }, children: /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", alignItems: "center", gap: 1 }, children: [
          t.trend === "up" && /* @__PURE__ */ T.jsx(an, { sx: { fontSize: "1rem", color: Dr.success } }),
          t.trend === "down" && /* @__PURE__ */ T.jsx(Zo, { sx: { fontSize: "1rem", color: Dr.error } }),
          /* @__PURE__ */ T.jsx(be, { variant: "caption", color: "text.secondary", children: typeof t.change == "number" ? `${t.change > 0 ? "+" : ""}${Math.abs(t.change)}%` : t.change })
        ] }) })
      ] }),
      /* @__PURE__ */ T.jsx(be, { variant: "h6", sx: { color: "#374151", fontWeight: 500 }, children: t.label })
    ] }) }) }, n)) })
  ] });
};
var Ct = { exports: {} }, re = {};
/**
 * @license React
 * react-is.production.js
 *
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var un;
function ii() {
  if (un) return re;
  un = 1;
  var e = Symbol.for("react.transitional.element"), r = Symbol.for("react.portal"), t = Symbol.for("react.fragment"), n = Symbol.for("react.strict_mode"), o = Symbol.for("react.profiler"), i = Symbol.for("react.consumer"), c = Symbol.for("react.context"), l = Symbol.for("react.forward_ref"), f = Symbol.for("react.suspense"), d = Symbol.for("react.suspense_list"), p = Symbol.for("react.memo"), h = Symbol.for("react.lazy"), m = Symbol.for("react.view_transition"), C = Symbol.for("react.client.reference");
  function y(s) {
    if (typeof s == "object" && s !== null) {
      var v = s.$$typeof;
      switch (v) {
        case e:
          switch (s = s.type, s) {
            case t:
            case o:
            case n:
            case f:
            case d:
            case m:
              return s;
            default:
              switch (s = s && s.$$typeof, s) {
                case c:
                case l:
                case h:
                case p:
                  return s;
                case i:
                  return s;
                default:
                  return v;
              }
          }
        case r:
          return v;
      }
    }
  }
  return re.ContextConsumer = i, re.ContextProvider = c, re.Element = e, re.ForwardRef = l, re.Fragment = t, re.Lazy = h, re.Memo = p, re.Portal = r, re.Profiler = o, re.StrictMode = n, re.Suspense = f, re.SuspenseList = d, re.isContextConsumer = function(s) {
    return y(s) === i;
  }, re.isContextProvider = function(s) {
    return y(s) === c;
  }, re.isElement = function(s) {
    return typeof s == "object" && s !== null && s.$$typeof === e;
  }, re.isForwardRef = function(s) {
    return y(s) === l;
  }, re.isFragment = function(s) {
    return y(s) === t;
  }, re.isLazy = function(s) {
    return y(s) === h;
  }, re.isMemo = function(s) {
    return y(s) === p;
  }, re.isPortal = function(s) {
    return y(s) === r;
  }, re.isProfiler = function(s) {
    return y(s) === o;
  }, re.isStrictMode = function(s) {
    return y(s) === n;
  }, re.isSuspense = function(s) {
    return y(s) === f;
  }, re.isSuspenseList = function(s) {
    return y(s) === d;
  }, re.isValidElementType = function(s) {
    return typeof s == "string" || typeof s == "function" || s === t || s === o || s === n || s === f || s === d || typeof s == "object" && s !== null && (s.$$typeof === h || s.$$typeof === p || s.$$typeof === c || s.$$typeof === i || s.$$typeof === l || s.$$typeof === C || s.getModuleId !== void 0);
  }, re.typeOf = y, re;
}
var te = {};
/**
 * @license React
 * react-is.development.js
 *
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var fn;
function ai() {
  return fn || (fn = 1, process.env.NODE_ENV !== "production" && function() {
    function e(s) {
      if (typeof s == "object" && s !== null) {
        var v = s.$$typeof;
        switch (v) {
          case r:
            switch (s = s.type, s) {
              case n:
              case i:
              case o:
              case d:
              case p:
              case C:
                return s;
              default:
                switch (s = s && s.$$typeof, s) {
                  case l:
                  case f:
                  case m:
                  case h:
                    return s;
                  case c:
                    return s;
                  default:
                    return v;
                }
            }
          case t:
            return v;
        }
      }
    }
    var r = Symbol.for("react.transitional.element"), t = Symbol.for("react.portal"), n = Symbol.for("react.fragment"), o = Symbol.for("react.strict_mode"), i = Symbol.for("react.profiler"), c = Symbol.for("react.consumer"), l = Symbol.for("react.context"), f = Symbol.for("react.forward_ref"), d = Symbol.for("react.suspense"), p = Symbol.for("react.suspense_list"), h = Symbol.for("react.memo"), m = Symbol.for("react.lazy"), C = Symbol.for("react.view_transition"), y = Symbol.for("react.client.reference");
    te.ContextConsumer = c, te.ContextProvider = l, te.Element = r, te.ForwardRef = f, te.Fragment = n, te.Lazy = m, te.Memo = h, te.Portal = t, te.Profiler = i, te.StrictMode = o, te.Suspense = d, te.SuspenseList = p, te.isContextConsumer = function(s) {
      return e(s) === c;
    }, te.isContextProvider = function(s) {
      return e(s) === l;
    }, te.isElement = function(s) {
      return typeof s == "object" && s !== null && s.$$typeof === r;
    }, te.isForwardRef = function(s) {
      return e(s) === f;
    }, te.isFragment = function(s) {
      return e(s) === n;
    }, te.isLazy = function(s) {
      return e(s) === m;
    }, te.isMemo = function(s) {
      return e(s) === h;
    }, te.isPortal = function(s) {
      return e(s) === t;
    }, te.isProfiler = function(s) {
      return e(s) === i;
    }, te.isStrictMode = function(s) {
      return e(s) === o;
    }, te.isSuspense = function(s) {
      return e(s) === d;
    }, te.isSuspenseList = function(s) {
      return e(s) === p;
    }, te.isValidElementType = function(s) {
      return typeof s == "string" || typeof s == "function" || s === n || s === i || s === o || s === d || s === p || typeof s == "object" && s !== null && (s.$$typeof === m || s.$$typeof === h || s.$$typeof === l || s.$$typeof === c || s.$$typeof === f || s.$$typeof === y || s.getModuleId !== void 0);
    }, te.typeOf = e;
  }()), te;
}
process.env.NODE_ENV === "production" ? Ct.exports = ii() : Ct.exports = ai();
var Fr = Ct.exports;
function Ve(e) {
  if (typeof e != "object" || e === null)
    return !1;
  const r = Object.getPrototypeOf(e);
  return (r === null || r === Object.prototype || Object.getPrototypeOf(r) === null) && !(Symbol.toStringTag in e) && !(Symbol.iterator in e);
}
function Hn(e) {
  if (/* @__PURE__ */ xe.isValidElement(e) || Fr.isValidElementType(e) || !Ve(e))
    return e;
  const r = {};
  return Object.keys(e).forEach((t) => {
    r[t] = Hn(e[t]);
  }), r;
}
function Oe(e, r, t = {
  clone: !0
}) {
  const n = t.clone ? {
    ...e
  } : e;
  return Ve(e) && Ve(r) && Object.keys(r).forEach((o) => {
    /* @__PURE__ */ xe.isValidElement(r[o]) || Fr.isValidElementType(r[o]) ? n[o] = r[o] : Ve(r[o]) && // Avoid prototype pollution
    Object.prototype.hasOwnProperty.call(e, o) && Ve(e[o]) ? n[o] = Oe(e[o], r[o], t) : t.clone ? n[o] = Ve(r[o]) ? Hn(r[o]) : r[o] : n[o] = r[o];
  }), n;
}
var Tt = { exports: {} }, Or = { exports: {} }, Q = {};
/** @license React v16.13.1
 * react-is.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var dn;
function si() {
  if (dn) return Q;
  dn = 1;
  var e = typeof Symbol == "function" && Symbol.for, r = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, n = e ? Symbol.for("react.fragment") : 60107, o = e ? Symbol.for("react.strict_mode") : 60108, i = e ? Symbol.for("react.profiler") : 60114, c = e ? Symbol.for("react.provider") : 60109, l = e ? Symbol.for("react.context") : 60110, f = e ? Symbol.for("react.async_mode") : 60111, d = e ? Symbol.for("react.concurrent_mode") : 60111, p = e ? Symbol.for("react.forward_ref") : 60112, h = e ? Symbol.for("react.suspense") : 60113, m = e ? Symbol.for("react.suspense_list") : 60120, C = e ? Symbol.for("react.memo") : 60115, y = e ? Symbol.for("react.lazy") : 60116, s = e ? Symbol.for("react.block") : 60121, v = e ? Symbol.for("react.fundamental") : 60117, w = e ? Symbol.for("react.responder") : 60118, P = e ? Symbol.for("react.scope") : 60119;
  function x(b) {
    if (typeof b == "object" && b !== null) {
      var I = b.$$typeof;
      switch (I) {
        case r:
          switch (b = b.type, b) {
            case f:
            case d:
            case n:
            case i:
            case o:
            case h:
              return b;
            default:
              switch (b = b && b.$$typeof, b) {
                case l:
                case p:
                case y:
                case C:
                case c:
                  return b;
                default:
                  return I;
              }
          }
        case t:
          return I;
      }
    }
  }
  function _(b) {
    return x(b) === d;
  }
  return Q.AsyncMode = f, Q.ConcurrentMode = d, Q.ContextConsumer = l, Q.ContextProvider = c, Q.Element = r, Q.ForwardRef = p, Q.Fragment = n, Q.Lazy = y, Q.Memo = C, Q.Portal = t, Q.Profiler = i, Q.StrictMode = o, Q.Suspense = h, Q.isAsyncMode = function(b) {
    return _(b) || x(b) === f;
  }, Q.isConcurrentMode = _, Q.isContextConsumer = function(b) {
    return x(b) === l;
  }, Q.isContextProvider = function(b) {
    return x(b) === c;
  }, Q.isElement = function(b) {
    return typeof b == "object" && b !== null && b.$$typeof === r;
  }, Q.isForwardRef = function(b) {
    return x(b) === p;
  }, Q.isFragment = function(b) {
    return x(b) === n;
  }, Q.isLazy = function(b) {
    return x(b) === y;
  }, Q.isMemo = function(b) {
    return x(b) === C;
  }, Q.isPortal = function(b) {
    return x(b) === t;
  }, Q.isProfiler = function(b) {
    return x(b) === i;
  }, Q.isStrictMode = function(b) {
    return x(b) === o;
  }, Q.isSuspense = function(b) {
    return x(b) === h;
  }, Q.isValidElementType = function(b) {
    return typeof b == "string" || typeof b == "function" || b === n || b === d || b === i || b === o || b === h || b === m || typeof b == "object" && b !== null && (b.$$typeof === y || b.$$typeof === C || b.$$typeof === c || b.$$typeof === l || b.$$typeof === p || b.$$typeof === v || b.$$typeof === w || b.$$typeof === P || b.$$typeof === s);
  }, Q.typeOf = x, Q;
}
var Z = {};
/** @license React v16.13.1
 * react-is.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var pn;
function ci() {
  return pn || (pn = 1, process.env.NODE_ENV !== "production" && function() {
    var e = typeof Symbol == "function" && Symbol.for, r = e ? Symbol.for("react.element") : 60103, t = e ? Symbol.for("react.portal") : 60106, n = e ? Symbol.for("react.fragment") : 60107, o = e ? Symbol.for("react.strict_mode") : 60108, i = e ? Symbol.for("react.profiler") : 60114, c = e ? Symbol.for("react.provider") : 60109, l = e ? Symbol.for("react.context") : 60110, f = e ? Symbol.for("react.async_mode") : 60111, d = e ? Symbol.for("react.concurrent_mode") : 60111, p = e ? Symbol.for("react.forward_ref") : 60112, h = e ? Symbol.for("react.suspense") : 60113, m = e ? Symbol.for("react.suspense_list") : 60120, C = e ? Symbol.for("react.memo") : 60115, y = e ? Symbol.for("react.lazy") : 60116, s = e ? Symbol.for("react.block") : 60121, v = e ? Symbol.for("react.fundamental") : 60117, w = e ? Symbol.for("react.responder") : 60118, P = e ? Symbol.for("react.scope") : 60119;
    function x(R) {
      return typeof R == "string" || typeof R == "function" || // Note: its typeof might be other than 'symbol' or 'number' if it's a polyfill.
      R === n || R === d || R === i || R === o || R === h || R === m || typeof R == "object" && R !== null && (R.$$typeof === y || R.$$typeof === C || R.$$typeof === c || R.$$typeof === l || R.$$typeof === p || R.$$typeof === v || R.$$typeof === w || R.$$typeof === P || R.$$typeof === s);
    }
    function _(R) {
      if (typeof R == "object" && R !== null) {
        var Ae = R.$$typeof;
        switch (Ae) {
          case r:
            var Je = R.type;
            switch (Je) {
              case f:
              case d:
              case n:
              case i:
              case o:
              case h:
                return Je;
              default:
                var Ge = Je && Je.$$typeof;
                switch (Ge) {
                  case l:
                  case p:
                  case y:
                  case C:
                  case c:
                    return Ge;
                  default:
                    return Ae;
                }
            }
          case t:
            return Ae;
        }
      }
    }
    var b = f, I = d, k = l, ue = c, ee = r, a = p, $ = n, B = y, M = C, ne = t, se = i, ye = o, Se = h, je = !1;
    function Be(R) {
      return je || (je = !0, console.warn("The ReactIs.isAsyncMode() alias has been deprecated, and will be removed in React 17+. Update your code to use ReactIs.isConcurrentMode() instead. It has the exact same API.")), E(R) || _(R) === f;
    }
    function E(R) {
      return _(R) === d;
    }
    function A(R) {
      return _(R) === l;
    }
    function V(R) {
      return _(R) === c;
    }
    function L(R) {
      return typeof R == "object" && R !== null && R.$$typeof === r;
    }
    function N(R) {
      return _(R) === p;
    }
    function Y(R) {
      return _(R) === n;
    }
    function D(R) {
      return _(R) === y;
    }
    function F(R) {
      return _(R) === C;
    }
    function W(R) {
      return _(R) === t;
    }
    function q(R) {
      return _(R) === i;
    }
    function z(R) {
      return _(R) === o;
    }
    function Ee(R) {
      return _(R) === h;
    }
    Z.AsyncMode = b, Z.ConcurrentMode = I, Z.ContextConsumer = k, Z.ContextProvider = ue, Z.Element = ee, Z.ForwardRef = a, Z.Fragment = $, Z.Lazy = B, Z.Memo = M, Z.Portal = ne, Z.Profiler = se, Z.StrictMode = ye, Z.Suspense = Se, Z.isAsyncMode = Be, Z.isConcurrentMode = E, Z.isContextConsumer = A, Z.isContextProvider = V, Z.isElement = L, Z.isForwardRef = N, Z.isFragment = Y, Z.isLazy = D, Z.isMemo = F, Z.isPortal = W, Z.isProfiler = q, Z.isStrictMode = z, Z.isSuspense = Ee, Z.isValidElementType = x, Z.typeOf = _;
  }()), Z;
}
var hn;
function Kn() {
  return hn || (hn = 1, process.env.NODE_ENV === "production" ? Or.exports = si() : Or.exports = ci()), Or.exports;
}
/*
object-assign
(c) Sindre Sorhus
@license MIT
*/
var lt, mn;
function li() {
  if (mn) return lt;
  mn = 1;
  var e = Object.getOwnPropertySymbols, r = Object.prototype.hasOwnProperty, t = Object.prototype.propertyIsEnumerable;
  function n(i) {
    if (i == null)
      throw new TypeError("Object.assign cannot be called with null or undefined");
    return Object(i);
  }
  function o() {
    try {
      if (!Object.assign)
        return !1;
      var i = new String("abc");
      if (i[5] = "de", Object.getOwnPropertyNames(i)[0] === "5")
        return !1;
      for (var c = {}, l = 0; l < 10; l++)
        c["_" + String.fromCharCode(l)] = l;
      var f = Object.getOwnPropertyNames(c).map(function(p) {
        return c[p];
      });
      if (f.join("") !== "0123456789")
        return !1;
      var d = {};
      return "abcdefghijklmnopqrst".split("").forEach(function(p) {
        d[p] = p;
      }), Object.keys(Object.assign({}, d)).join("") === "abcdefghijklmnopqrst";
    } catch {
      return !1;
    }
  }
  return lt = o() ? Object.assign : function(i, c) {
    for (var l, f = n(i), d, p = 1; p < arguments.length; p++) {
      l = Object(arguments[p]);
      for (var h in l)
        r.call(l, h) && (f[h] = l[h]);
      if (e) {
        d = e(l);
        for (var m = 0; m < d.length; m++)
          t.call(l, d[m]) && (f[d[m]] = l[d[m]]);
      }
    }
    return f;
  }, lt;
}
var ut, gn;
function It() {
  if (gn) return ut;
  gn = 1;
  var e = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED";
  return ut = e, ut;
}
var ft, yn;
function Xn() {
  return yn || (yn = 1, ft = Function.call.bind(Object.prototype.hasOwnProperty)), ft;
}
var dt, bn;
function ui() {
  if (bn) return dt;
  bn = 1;
  var e = function() {
  };
  if (process.env.NODE_ENV !== "production") {
    var r = It(), t = {}, n = Xn();
    e = function(i) {
      var c = "Warning: " + i;
      typeof console < "u" && console.error(c);
      try {
        throw new Error(c);
      } catch {
      }
    };
  }
  function o(i, c, l, f, d) {
    if (process.env.NODE_ENV !== "production") {
      for (var p in i)
        if (n(i, p)) {
          var h;
          try {
            if (typeof i[p] != "function") {
              var m = Error(
                (f || "React class") + ": " + l + " type `" + p + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof i[p] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`."
              );
              throw m.name = "Invariant Violation", m;
            }
            h = i[p](c, p, f, l, null, r);
          } catch (y) {
            h = y;
          }
          if (h && !(h instanceof Error) && e(
            (f || "React class") + ": type specification of " + l + " `" + p + "` is invalid; the type checker function must return `null` or an `Error` but returned a " + typeof h + ". You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument)."
          ), h instanceof Error && !(h.message in t)) {
            t[h.message] = !0;
            var C = d ? d() : "";
            e(
              "Failed " + l + " type: " + h.message + (C ?? "")
            );
          }
        }
    }
  }
  return o.resetWarningCache = function() {
    process.env.NODE_ENV !== "production" && (t = {});
  }, dt = o, dt;
}
var pt, vn;
function fi() {
  if (vn) return pt;
  vn = 1;
  var e = Kn(), r = li(), t = It(), n = Xn(), o = ui(), i = function() {
  };
  process.env.NODE_ENV !== "production" && (i = function(l) {
    var f = "Warning: " + l;
    typeof console < "u" && console.error(f);
    try {
      throw new Error(f);
    } catch {
    }
  });
  function c() {
    return null;
  }
  return pt = function(l, f) {
    var d = typeof Symbol == "function" && Symbol.iterator, p = "@@iterator";
    function h(E) {
      var A = E && (d && E[d] || E[p]);
      if (typeof A == "function")
        return A;
    }
    var m = "<<anonymous>>", C = {
      array: w("array"),
      bigint: w("bigint"),
      bool: w("boolean"),
      func: w("function"),
      number: w("number"),
      object: w("object"),
      string: w("string"),
      symbol: w("symbol"),
      any: P(),
      arrayOf: x,
      element: _(),
      elementType: b(),
      instanceOf: I,
      node: a(),
      objectOf: ue,
      oneOf: k,
      oneOfType: ee,
      shape: B,
      exact: M
    };
    function y(E, A) {
      return E === A ? E !== 0 || 1 / E === 1 / A : E !== E && A !== A;
    }
    function s(E, A) {
      this.message = E, this.data = A && typeof A == "object" ? A : {}, this.stack = "";
    }
    s.prototype = Error.prototype;
    function v(E) {
      if (process.env.NODE_ENV !== "production")
        var A = {}, V = 0;
      function L(Y, D, F, W, q, z, Ee) {
        if (W = W || m, z = z || F, Ee !== t) {
          if (f) {
            var R = new Error(
              "Calling PropTypes validators directly is not supported by the `prop-types` package. Use `PropTypes.checkPropTypes()` to call them. Read more at http://fb.me/use-check-prop-types"
            );
            throw R.name = "Invariant Violation", R;
          } else if (process.env.NODE_ENV !== "production" && typeof console < "u") {
            var Ae = W + ":" + F;
            !A[Ae] && // Avoid spamming the console because they are often not actionable except for lib authors
            V < 3 && (i(
              "You are manually calling a React.PropTypes validation function for the `" + z + "` prop on `" + W + "`. This is deprecated and will throw in the standalone `prop-types` package. You may be seeing this warning due to a third-party PropTypes library. See https://fb.me/react-warning-dont-call-proptypes for details."
            ), A[Ae] = !0, V++);
          }
        }
        return D[F] == null ? Y ? D[F] === null ? new s("The " + q + " `" + z + "` is marked as required " + ("in `" + W + "`, but its value is `null`.")) : new s("The " + q + " `" + z + "` is marked as required in " + ("`" + W + "`, but its value is `undefined`.")) : null : E(D, F, W, q, z);
      }
      var N = L.bind(null, !1);
      return N.isRequired = L.bind(null, !0), N;
    }
    function w(E) {
      function A(V, L, N, Y, D, F) {
        var W = V[L], q = ye(W);
        if (q !== E) {
          var z = Se(W);
          return new s(
            "Invalid " + Y + " `" + D + "` of type " + ("`" + z + "` supplied to `" + N + "`, expected ") + ("`" + E + "`."),
            { expectedType: E }
          );
        }
        return null;
      }
      return v(A);
    }
    function P() {
      return v(c);
    }
    function x(E) {
      function A(V, L, N, Y, D) {
        if (typeof E != "function")
          return new s("Property `" + D + "` of component `" + N + "` has invalid PropType notation inside arrayOf.");
        var F = V[L];
        if (!Array.isArray(F)) {
          var W = ye(F);
          return new s("Invalid " + Y + " `" + D + "` of type " + ("`" + W + "` supplied to `" + N + "`, expected an array."));
        }
        for (var q = 0; q < F.length; q++) {
          var z = E(F, q, N, Y, D + "[" + q + "]", t);
          if (z instanceof Error)
            return z;
        }
        return null;
      }
      return v(A);
    }
    function _() {
      function E(A, V, L, N, Y) {
        var D = A[V];
        if (!l(D)) {
          var F = ye(D);
          return new s("Invalid " + N + " `" + Y + "` of type " + ("`" + F + "` supplied to `" + L + "`, expected a single ReactElement."));
        }
        return null;
      }
      return v(E);
    }
    function b() {
      function E(A, V, L, N, Y) {
        var D = A[V];
        if (!e.isValidElementType(D)) {
          var F = ye(D);
          return new s("Invalid " + N + " `" + Y + "` of type " + ("`" + F + "` supplied to `" + L + "`, expected a single ReactElement type."));
        }
        return null;
      }
      return v(E);
    }
    function I(E) {
      function A(V, L, N, Y, D) {
        if (!(V[L] instanceof E)) {
          var F = E.name || m, W = Be(V[L]);
          return new s("Invalid " + Y + " `" + D + "` of type " + ("`" + W + "` supplied to `" + N + "`, expected ") + ("instance of `" + F + "`."));
        }
        return null;
      }
      return v(A);
    }
    function k(E) {
      if (!Array.isArray(E))
        return process.env.NODE_ENV !== "production" && (arguments.length > 1 ? i(
          "Invalid arguments supplied to oneOf, expected an array, got " + arguments.length + " arguments. A common mistake is to write oneOf(x, y, z) instead of oneOf([x, y, z])."
        ) : i("Invalid argument supplied to oneOf, expected an array.")), c;
      function A(V, L, N, Y, D) {
        for (var F = V[L], W = 0; W < E.length; W++)
          if (y(F, E[W]))
            return null;
        var q = JSON.stringify(E, function(Ee, R) {
          var Ae = Se(R);
          return Ae === "symbol" ? String(R) : R;
        });
        return new s("Invalid " + Y + " `" + D + "` of value `" + String(F) + "` " + ("supplied to `" + N + "`, expected one of " + q + "."));
      }
      return v(A);
    }
    function ue(E) {
      function A(V, L, N, Y, D) {
        if (typeof E != "function")
          return new s("Property `" + D + "` of component `" + N + "` has invalid PropType notation inside objectOf.");
        var F = V[L], W = ye(F);
        if (W !== "object")
          return new s("Invalid " + Y + " `" + D + "` of type " + ("`" + W + "` supplied to `" + N + "`, expected an object."));
        for (var q in F)
          if (n(F, q)) {
            var z = E(F, q, N, Y, D + "." + q, t);
            if (z instanceof Error)
              return z;
          }
        return null;
      }
      return v(A);
    }
    function ee(E) {
      if (!Array.isArray(E))
        return process.env.NODE_ENV !== "production" && i("Invalid argument supplied to oneOfType, expected an instance of array."), c;
      for (var A = 0; A < E.length; A++) {
        var V = E[A];
        if (typeof V != "function")
          return i(
            "Invalid argument supplied to oneOfType. Expected an array of check functions, but received " + je(V) + " at index " + A + "."
          ), c;
      }
      function L(N, Y, D, F, W) {
        for (var q = [], z = 0; z < E.length; z++) {
          var Ee = E[z], R = Ee(N, Y, D, F, W, t);
          if (R == null)
            return null;
          R.data && n(R.data, "expectedType") && q.push(R.data.expectedType);
        }
        var Ae = q.length > 0 ? ", expected one of type [" + q.join(", ") + "]" : "";
        return new s("Invalid " + F + " `" + W + "` supplied to " + ("`" + D + "`" + Ae + "."));
      }
      return v(L);
    }
    function a() {
      function E(A, V, L, N, Y) {
        return ne(A[V]) ? null : new s("Invalid " + N + " `" + Y + "` supplied to " + ("`" + L + "`, expected a ReactNode."));
      }
      return v(E);
    }
    function $(E, A, V, L, N) {
      return new s(
        (E || "React class") + ": " + A + " type `" + V + "." + L + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + N + "`."
      );
    }
    function B(E) {
      function A(V, L, N, Y, D) {
        var F = V[L], W = ye(F);
        if (W !== "object")
          return new s("Invalid " + Y + " `" + D + "` of type `" + W + "` " + ("supplied to `" + N + "`, expected `object`."));
        for (var q in E) {
          var z = E[q];
          if (typeof z != "function")
            return $(N, Y, D, q, Se(z));
          var Ee = z(F, q, N, Y, D + "." + q, t);
          if (Ee)
            return Ee;
        }
        return null;
      }
      return v(A);
    }
    function M(E) {
      function A(V, L, N, Y, D) {
        var F = V[L], W = ye(F);
        if (W !== "object")
          return new s("Invalid " + Y + " `" + D + "` of type `" + W + "` " + ("supplied to `" + N + "`, expected `object`."));
        var q = r({}, V[L], E);
        for (var z in q) {
          var Ee = E[z];
          if (n(E, z) && typeof Ee != "function")
            return $(N, Y, D, z, Se(Ee));
          if (!Ee)
            return new s(
              "Invalid " + Y + " `" + D + "` key `" + z + "` supplied to `" + N + "`.\nBad object: " + JSON.stringify(V[L], null, "  ") + `
Valid keys: ` + JSON.stringify(Object.keys(E), null, "  ")
            );
          var R = Ee(F, z, N, Y, D + "." + z, t);
          if (R)
            return R;
        }
        return null;
      }
      return v(A);
    }
    function ne(E) {
      switch (typeof E) {
        case "number":
        case "string":
        case "undefined":
          return !0;
        case "boolean":
          return !E;
        case "object":
          if (Array.isArray(E))
            return E.every(ne);
          if (E === null || l(E))
            return !0;
          var A = h(E);
          if (A) {
            var V = A.call(E), L;
            if (A !== E.entries) {
              for (; !(L = V.next()).done; )
                if (!ne(L.value))
                  return !1;
            } else
              for (; !(L = V.next()).done; ) {
                var N = L.value;
                if (N && !ne(N[1]))
                  return !1;
              }
          } else
            return !1;
          return !0;
        default:
          return !1;
      }
    }
    function se(E, A) {
      return E === "symbol" ? !0 : A ? A["@@toStringTag"] === "Symbol" || typeof Symbol == "function" && A instanceof Symbol : !1;
    }
    function ye(E) {
      var A = typeof E;
      return Array.isArray(E) ? "array" : E instanceof RegExp ? "object" : se(A, E) ? "symbol" : A;
    }
    function Se(E) {
      if (typeof E > "u" || E === null)
        return "" + E;
      var A = ye(E);
      if (A === "object") {
        if (E instanceof Date)
          return "date";
        if (E instanceof RegExp)
          return "regexp";
      }
      return A;
    }
    function je(E) {
      var A = Se(E);
      switch (A) {
        case "array":
        case "object":
          return "an " + A;
        case "boolean":
        case "date":
        case "regexp":
          return "a " + A;
        default:
          return A;
      }
    }
    function Be(E) {
      return !E.constructor || !E.constructor.name ? m : E.constructor.name;
    }
    return C.checkPropTypes = o, C.resetWarningCache = o.resetWarningCache, C.PropTypes = C, C;
  }, pt;
}
var ht, xn;
function di() {
  if (xn) return ht;
  xn = 1;
  var e = It();
  function r() {
  }
  function t() {
  }
  return t.resetWarningCache = r, ht = function() {
    function n(c, l, f, d, p, h) {
      if (h !== e) {
        var m = new Error(
          "Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types"
        );
        throw m.name = "Invariant Violation", m;
      }
    }
    n.isRequired = n;
    function o() {
      return n;
    }
    var i = {
      array: n,
      bigint: n,
      bool: n,
      func: n,
      number: n,
      object: n,
      string: n,
      symbol: n,
      any: n,
      arrayOf: o,
      element: n,
      elementType: n,
      instanceOf: o,
      node: n,
      objectOf: o,
      oneOf: o,
      oneOfType: o,
      shape: o,
      exact: o,
      checkPropTypes: t,
      resetWarningCache: r
    };
    return i.PropTypes = i, i;
  }, ht;
}
if (process.env.NODE_ENV !== "production") {
  var pi = Kn(), hi = !0;
  Tt.exports = fi()(pi.isElement, hi);
} else
  Tt.exports = di()();
var mi = Tt.exports;
const H = /* @__PURE__ */ ti(mi);
function ze(e, ...r) {
  const t = new URL(`https://mui.com/production-error/?code=${e}`);
  return r.forEach((n) => t.searchParams.append("args[]", n)), `Minified MUI error #${e}; visit ${t} for the full message.`;
}
function Jn(e, r = "") {
  return e.displayName || e.name || r;
}
function Sn(e, r, t) {
  const n = Jn(r);
  return e.displayName || (n !== "" ? `${t}(${n})` : t);
}
function gi(e) {
  if (e != null) {
    if (typeof e == "string")
      return e;
    if (typeof e == "function")
      return Jn(e, "Component");
    if (typeof e == "object")
      switch (e.$$typeof) {
        case Fr.ForwardRef:
          return Sn(e, e.render, "ForwardRef");
        case Fr.Memo:
          return Sn(e, e.type, "memo");
        default:
          return;
      }
  }
}
function Xe(e) {
  if (typeof e != "string")
    throw new Error(process.env.NODE_ENV !== "production" ? "MUI: `capitalize(string)` expects a string argument." : ze(7));
  return e.charAt(0).toUpperCase() + e.slice(1);
}
function wt(e, r) {
  const t = {
    ...r
  };
  for (const n in e)
    if (Object.prototype.hasOwnProperty.call(e, n)) {
      const o = n;
      if (o === "components" || o === "slots")
        t[o] = {
          ...e[o],
          ...t[o]
        };
      else if (o === "componentsProps" || o === "slotProps") {
        const i = e[o], c = r[o];
        if (!c)
          t[o] = i || {};
        else if (!i)
          t[o] = c;
        else {
          t[o] = {
            ...c
          };
          for (const l in i)
            if (Object.prototype.hasOwnProperty.call(i, l)) {
              const f = l;
              t[o][f] = wt(i[f], c[f]);
            }
        }
      } else t[o] === void 0 && (t[o] = e[o]);
    }
  return t;
}
function yi(e, r, t = void 0) {
  const n = {};
  for (const o in e) {
    const i = e[o];
    let c = "", l = !0;
    for (let f = 0; f < i.length; f += 1) {
      const d = i[f];
      d && (c += (l === !0 ? "" : " ") + r(d), l = !1, t && t[d] && (c += " " + t[d]));
    }
    n[o] = c;
  }
  return n;
}
const En = (e) => e, bi = () => {
  let e = En;
  return {
    configure(r) {
      e = r;
    },
    generate(r) {
      return e(r);
    },
    reset() {
      e = En;
    }
  };
}, vi = bi(), xi = {
  active: "active",
  checked: "checked",
  completed: "completed",
  disabled: "disabled",
  error: "error",
  expanded: "expanded",
  focused: "focused",
  focusVisible: "focusVisible",
  open: "open",
  readOnly: "readOnly",
  required: "required",
  selected: "selected"
};
function jt(e, r, t = "Mui") {
  const n = xi[r];
  return n ? `${t}-${n}` : `${vi.generate(e)}-${r}`;
}
function Si(e, r, t = "Mui") {
  const n = {};
  return r.forEach((o) => {
    n[o] = jt(e, o, t);
  }), n;
}
function Ei(e, r = Number.MIN_SAFE_INTEGER, t = Number.MAX_SAFE_INTEGER) {
  return Math.max(r, Math.min(e, t));
}
function Qn(e) {
  var r, t, n = "";
  if (typeof e == "string" || typeof e == "number") n += e;
  else if (typeof e == "object") if (Array.isArray(e)) {
    var o = e.length;
    for (r = 0; r < o; r++) e[r] && (t = Qn(e[r])) && (n && (n += " "), n += t);
  } else for (t in e) e[t] && (n && (n += " "), n += t);
  return n;
}
function Ci() {
  for (var e, r, t = 0, n = "", o = arguments.length; t < o; t++) (e = arguments[t]) && (r = Qn(e)) && (n && (n += " "), n += r);
  return n;
}
function br(e, r) {
  return r ? Oe(e, r, {
    clone: !1
    // No need to clone deep, it's way faster.
  }) : e;
}
const Ye = process.env.NODE_ENV !== "production" ? H.oneOfType([H.number, H.string, H.object, H.array]) : {};
function Cn(e, r) {
  if (!e.containerQueries)
    return r;
  const t = Object.keys(r).filter((n) => n.startsWith("@container")).sort((n, o) => {
    var c, l;
    const i = /min-width:\s*([0-9.]+)/;
    return +(((c = n.match(i)) == null ? void 0 : c[1]) || 0) - +(((l = o.match(i)) == null ? void 0 : l[1]) || 0);
  });
  return t.length ? t.reduce((n, o) => {
    const i = r[o];
    return delete n[o], n[o] = i, n;
  }, {
    ...r
  }) : r;
}
function Ti(e, r) {
  return r === "@" || r.startsWith("@") && (e.some((t) => r.startsWith(`@${t}`)) || !!r.match(/^@\d/));
}
function wi(e, r) {
  const t = r.match(/^@([^/]+)?\/?(.+)?$/);
  if (!t) {
    if (process.env.NODE_ENV !== "production")
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The provided shorthand ${`(${r})`} is invalid. The format should be \`@<breakpoint | number>\` or \`@<breakpoint | number>/<container>\`.
For example, \`@sm\` or \`@600\` or \`@40rem/sidebar\`.` : ze(18, `(${r})`));
    return null;
  }
  const [, n, o] = t, i = Number.isNaN(+n) ? n || 0 : +n;
  return e.containerQueries(o).up(i);
}
function _i(e) {
  const r = (i, c) => i.replace("@media", c ? `@container ${c}` : "@container");
  function t(i, c) {
    i.up = (...l) => r(e.breakpoints.up(...l), c), i.down = (...l) => r(e.breakpoints.down(...l), c), i.between = (...l) => r(e.breakpoints.between(...l), c), i.only = (...l) => r(e.breakpoints.only(...l), c), i.not = (...l) => {
      const f = r(e.breakpoints.not(...l), c);
      return f.includes("not all and") ? f.replace("not all and ", "").replace("min-width:", "width<").replace("max-width:", "width>").replace("and", "or") : f;
    };
  }
  const n = {}, o = (i) => (t(n, i), n);
  return t(o), {
    ...e,
    containerQueries: o
  };
}
const Ur = {
  xs: 0,
  // phone
  sm: 600,
  // tablet
  md: 900,
  // small laptop
  lg: 1200,
  // desktop
  xl: 1536
  // large screen
}, Tn = {
  // Sorted ASC by size. That's important.
  // It can't be configured as it's used statically for propTypes.
  keys: ["xs", "sm", "md", "lg", "xl"],
  up: (e) => `@media (min-width:${Ur[e]}px)`
}, Ri = {
  containerQueries: (e) => ({
    up: (r) => {
      let t = typeof r == "number" ? r : Ur[r] || r;
      return typeof t == "number" && (t = `${t}px`), e ? `@container ${e} (min-width:${t})` : `@container (min-width:${t})`;
    }
  })
};
function We(e, r, t) {
  const n = e.theme || {};
  if (Array.isArray(r)) {
    const i = n.breakpoints || Tn;
    return r.reduce((c, l, f) => (c[i.up(i.keys[f])] = t(r[f]), c), {});
  }
  if (typeof r == "object") {
    const i = n.breakpoints || Tn;
    return Object.keys(r).reduce((c, l) => {
      if (Ti(i.keys, l)) {
        const f = wi(n.containerQueries ? n : Ri, l);
        f && (c[f] = t(r[l], l));
      } else if (Object.keys(i.values || Ur).includes(l)) {
        const f = i.up(l);
        c[f] = t(r[l], l);
      } else {
        const f = l;
        c[f] = r[f];
      }
      return c;
    }, {});
  }
  return t(r);
}
function Oi(e = {}) {
  var t;
  return ((t = e.keys) == null ? void 0 : t.reduce((n, o) => {
    const i = e.up(o);
    return n[i] = {}, n;
  }, {})) || {};
}
function wn(e, r) {
  return e.reduce((t, n) => {
    const o = t[n];
    return (!o || Object.keys(o).length === 0) && delete t[n], t;
  }, r);
}
function Yr(e, r, t = !0) {
  if (!r || typeof r != "string")
    return null;
  if (e && e.vars && t) {
    const n = `vars.${r}`.split(".").reduce((o, i) => o && o[i] ? o[i] : null, e);
    if (n != null)
      return n;
  }
  return r.split(".").reduce((n, o) => n && n[o] != null ? n[o] : null, e);
}
function Br(e, r, t, n = t) {
  let o;
  return typeof e == "function" ? o = e(t) : Array.isArray(e) ? o = e[t] || n : o = Yr(e, t) || n, r && (o = r(o, n, e)), o;
}
function he(e) {
  const {
    prop: r,
    cssProperty: t = e.prop,
    themeKey: n,
    transform: o
  } = e, i = (c) => {
    if (c[r] == null)
      return null;
    const l = c[r], f = c.theme, d = Yr(f, n) || {};
    return We(c, l, (h) => {
      let m = Br(d, o, h);
      return h === m && typeof h == "string" && (m = Br(d, o, `${r}${h === "default" ? "" : Xe(h)}`, h)), t === !1 ? m : {
        [t]: m
      };
    });
  };
  return i.propTypes = process.env.NODE_ENV !== "production" ? {
    [r]: Ye
  } : {}, i.filterProps = [r], i;
}
function $i(e) {
  const r = {};
  return (t) => (r[t] === void 0 && (r[t] = e(t)), r[t]);
}
const Ai = {
  m: "margin",
  p: "padding"
}, Pi = {
  t: "Top",
  r: "Right",
  b: "Bottom",
  l: "Left",
  x: ["Left", "Right"],
  y: ["Top", "Bottom"]
}, _n = {
  marginX: "mx",
  marginY: "my",
  paddingX: "px",
  paddingY: "py"
}, ki = $i((e) => {
  if (e.length > 2)
    if (_n[e])
      e = _n[e];
    else
      return [e];
  const [r, t] = e.split(""), n = Ai[r], o = Pi[t] || "";
  return Array.isArray(o) ? o.map((i) => n + i) : [n + o];
}), Gr = ["m", "mt", "mr", "mb", "ml", "mx", "my", "margin", "marginTop", "marginRight", "marginBottom", "marginLeft", "marginX", "marginY", "marginInline", "marginInlineStart", "marginInlineEnd", "marginBlock", "marginBlockStart", "marginBlockEnd"], qr = ["p", "pt", "pr", "pb", "pl", "px", "py", "padding", "paddingTop", "paddingRight", "paddingBottom", "paddingLeft", "paddingX", "paddingY", "paddingInline", "paddingInlineStart", "paddingInlineEnd", "paddingBlock", "paddingBlockStart", "paddingBlockEnd"], Ii = [...Gr, ...qr];
function Cr(e, r, t, n) {
  const o = Yr(e, r, !0) ?? t;
  return typeof o == "number" || typeof o == "string" ? (i) => typeof i == "string" ? i : (process.env.NODE_ENV !== "production" && typeof i != "number" && console.error(`MUI: Expected ${n} argument to be a number or a string, got ${i}.`), typeof o == "string" ? `calc(${i} * ${o})` : o * i) : Array.isArray(o) ? (i) => {
    if (typeof i == "string")
      return i;
    const c = Math.abs(i);
    process.env.NODE_ENV !== "production" && (Number.isInteger(c) ? c > o.length - 1 && console.error([`MUI: The value provided (${c}) overflows.`, `The supported values are: ${JSON.stringify(o)}.`, `${c} > ${o.length - 1}, you need to add the missing values.`].join(`
`)) : console.error([`MUI: The \`theme.${r}\` array type cannot be combined with non integer values.You should either use an integer value that can be used as index, or define the \`theme.${r}\` as a number.`].join(`
`)));
    const l = o[c];
    return i >= 0 ? l : typeof l == "number" ? -l : `-${l}`;
  } : typeof o == "function" ? o : (process.env.NODE_ENV !== "production" && console.error([`MUI: The \`theme.${r}\` value (${o}) is invalid.`, "It should be a number, an array or a function."].join(`
`)), () => {
  });
}
function Mt(e) {
  return Cr(e, "spacing", 8, "spacing");
}
function Tr(e, r) {
  return typeof r == "string" || r == null ? r : e(r);
}
function ji(e, r) {
  return (t) => e.reduce((n, o) => (n[o] = Tr(r, t), n), {});
}
function Mi(e, r, t, n) {
  if (!r.includes(t))
    return null;
  const o = ki(t), i = ji(o, n), c = e[t];
  return We(e, c, i);
}
function Zn(e, r) {
  const t = Mt(e.theme);
  return Object.keys(e).map((n) => Mi(e, r, n, t)).reduce(br, {});
}
function fe(e) {
  return Zn(e, Gr);
}
fe.propTypes = process.env.NODE_ENV !== "production" ? Gr.reduce((e, r) => (e[r] = Ye, e), {}) : {};
fe.filterProps = Gr;
function de(e) {
  return Zn(e, qr);
}
de.propTypes = process.env.NODE_ENV !== "production" ? qr.reduce((e, r) => (e[r] = Ye, e), {}) : {};
de.filterProps = qr;
process.env.NODE_ENV !== "production" && Ii.reduce((e, r) => (e[r] = Ye, e), {});
function Hr(...e) {
  const r = e.reduce((n, o) => (o.filterProps.forEach((i) => {
    n[i] = o;
  }), n), {}), t = (n) => Object.keys(n).reduce((o, i) => r[i] ? br(o, r[i](n)) : o, {});
  return t.propTypes = process.env.NODE_ENV !== "production" ? e.reduce((n, o) => Object.assign(n, o.propTypes), {}) : {}, t.filterProps = e.reduce((n, o) => n.concat(o.filterProps), []), t;
}
function ke(e) {
  return typeof e != "number" ? e : `${e}px solid`;
}
function Ie(e, r) {
  return he({
    prop: e,
    themeKey: "borders",
    transform: r
  });
}
const Ni = Ie("border", ke), Di = Ie("borderTop", ke), Fi = Ie("borderRight", ke), Bi = Ie("borderBottom", ke), Li = Ie("borderLeft", ke), Vi = Ie("borderColor"), Wi = Ie("borderTopColor"), zi = Ie("borderRightColor"), Ui = Ie("borderBottomColor"), Yi = Ie("borderLeftColor"), Gi = Ie("outline", ke), qi = Ie("outlineColor"), Kr = (e) => {
  if (e.borderRadius !== void 0 && e.borderRadius !== null) {
    const r = Cr(e.theme, "shape.borderRadius", 4, "borderRadius"), t = (n) => ({
      borderRadius: Tr(r, n)
    });
    return We(e, e.borderRadius, t);
  }
  return null;
};
Kr.propTypes = process.env.NODE_ENV !== "production" ? {
  borderRadius: Ye
} : {};
Kr.filterProps = ["borderRadius"];
Hr(Ni, Di, Fi, Bi, Li, Vi, Wi, zi, Ui, Yi, Kr, Gi, qi);
const Xr = (e) => {
  if (e.gap !== void 0 && e.gap !== null) {
    const r = Cr(e.theme, "spacing", 8, "gap"), t = (n) => ({
      gap: Tr(r, n)
    });
    return We(e, e.gap, t);
  }
  return null;
};
Xr.propTypes = process.env.NODE_ENV !== "production" ? {
  gap: Ye
} : {};
Xr.filterProps = ["gap"];
const Jr = (e) => {
  if (e.columnGap !== void 0 && e.columnGap !== null) {
    const r = Cr(e.theme, "spacing", 8, "columnGap"), t = (n) => ({
      columnGap: Tr(r, n)
    });
    return We(e, e.columnGap, t);
  }
  return null;
};
Jr.propTypes = process.env.NODE_ENV !== "production" ? {
  columnGap: Ye
} : {};
Jr.filterProps = ["columnGap"];
const Qr = (e) => {
  if (e.rowGap !== void 0 && e.rowGap !== null) {
    const r = Cr(e.theme, "spacing", 8, "rowGap"), t = (n) => ({
      rowGap: Tr(r, n)
    });
    return We(e, e.rowGap, t);
  }
  return null;
};
Qr.propTypes = process.env.NODE_ENV !== "production" ? {
  rowGap: Ye
} : {};
Qr.filterProps = ["rowGap"];
const Hi = he({
  prop: "gridColumn"
}), Ki = he({
  prop: "gridRow"
}), Xi = he({
  prop: "gridAutoFlow"
}), Ji = he({
  prop: "gridAutoColumns"
}), Qi = he({
  prop: "gridAutoRows"
}), Zi = he({
  prop: "gridTemplateColumns"
}), ea = he({
  prop: "gridTemplateRows"
}), ra = he({
  prop: "gridTemplateAreas"
}), ta = he({
  prop: "gridArea"
});
Hr(Xr, Jr, Qr, Hi, Ki, Xi, Ji, Qi, Zi, ea, ra, ta);
function ir(e, r) {
  return r === "grey" ? r : e;
}
const na = he({
  prop: "color",
  themeKey: "palette",
  transform: ir
}), oa = he({
  prop: "bgcolor",
  cssProperty: "backgroundColor",
  themeKey: "palette",
  transform: ir
}), ia = he({
  prop: "backgroundColor",
  themeKey: "palette",
  transform: ir
});
Hr(na, oa, ia);
function Re(e) {
  return e <= 1 && e !== 0 ? `${e * 100}%` : e;
}
const aa = he({
  prop: "width",
  transform: Re
}), Nt = (e) => {
  if (e.maxWidth !== void 0 && e.maxWidth !== null) {
    const r = (t) => {
      var o, i, c, l, f;
      const n = ((c = (i = (o = e.theme) == null ? void 0 : o.breakpoints) == null ? void 0 : i.values) == null ? void 0 : c[t]) || Ur[t];
      return n ? ((f = (l = e.theme) == null ? void 0 : l.breakpoints) == null ? void 0 : f.unit) !== "px" ? {
        maxWidth: `${n}${e.theme.breakpoints.unit}`
      } : {
        maxWidth: n
      } : {
        maxWidth: Re(t)
      };
    };
    return We(e, e.maxWidth, r);
  }
  return null;
};
Nt.filterProps = ["maxWidth"];
const sa = he({
  prop: "minWidth",
  transform: Re
}), ca = he({
  prop: "height",
  transform: Re
}), la = he({
  prop: "maxHeight",
  transform: Re
}), ua = he({
  prop: "minHeight",
  transform: Re
});
he({
  prop: "size",
  cssProperty: "width",
  transform: Re
});
he({
  prop: "size",
  cssProperty: "height",
  transform: Re
});
const fa = he({
  prop: "boxSizing"
});
Hr(aa, Nt, sa, ca, la, ua, fa);
const Zr = {
  // borders
  border: {
    themeKey: "borders",
    transform: ke
  },
  borderTop: {
    themeKey: "borders",
    transform: ke
  },
  borderRight: {
    themeKey: "borders",
    transform: ke
  },
  borderBottom: {
    themeKey: "borders",
    transform: ke
  },
  borderLeft: {
    themeKey: "borders",
    transform: ke
  },
  borderColor: {
    themeKey: "palette"
  },
  borderTopColor: {
    themeKey: "palette"
  },
  borderRightColor: {
    themeKey: "palette"
  },
  borderBottomColor: {
    themeKey: "palette"
  },
  borderLeftColor: {
    themeKey: "palette"
  },
  outline: {
    themeKey: "borders",
    transform: ke
  },
  outlineColor: {
    themeKey: "palette"
  },
  borderRadius: {
    themeKey: "shape.borderRadius",
    style: Kr
  },
  // palette
  color: {
    themeKey: "palette",
    transform: ir
  },
  bgcolor: {
    themeKey: "palette",
    cssProperty: "backgroundColor",
    transform: ir
  },
  backgroundColor: {
    themeKey: "palette",
    transform: ir
  },
  // spacing
  p: {
    style: de
  },
  pt: {
    style: de
  },
  pr: {
    style: de
  },
  pb: {
    style: de
  },
  pl: {
    style: de
  },
  px: {
    style: de
  },
  py: {
    style: de
  },
  padding: {
    style: de
  },
  paddingTop: {
    style: de
  },
  paddingRight: {
    style: de
  },
  paddingBottom: {
    style: de
  },
  paddingLeft: {
    style: de
  },
  paddingX: {
    style: de
  },
  paddingY: {
    style: de
  },
  paddingInline: {
    style: de
  },
  paddingInlineStart: {
    style: de
  },
  paddingInlineEnd: {
    style: de
  },
  paddingBlock: {
    style: de
  },
  paddingBlockStart: {
    style: de
  },
  paddingBlockEnd: {
    style: de
  },
  m: {
    style: fe
  },
  mt: {
    style: fe
  },
  mr: {
    style: fe
  },
  mb: {
    style: fe
  },
  ml: {
    style: fe
  },
  mx: {
    style: fe
  },
  my: {
    style: fe
  },
  margin: {
    style: fe
  },
  marginTop: {
    style: fe
  },
  marginRight: {
    style: fe
  },
  marginBottom: {
    style: fe
  },
  marginLeft: {
    style: fe
  },
  marginX: {
    style: fe
  },
  marginY: {
    style: fe
  },
  marginInline: {
    style: fe
  },
  marginInlineStart: {
    style: fe
  },
  marginInlineEnd: {
    style: fe
  },
  marginBlock: {
    style: fe
  },
  marginBlockStart: {
    style: fe
  },
  marginBlockEnd: {
    style: fe
  },
  // display
  displayPrint: {
    cssProperty: !1,
    transform: (e) => ({
      "@media print": {
        display: e
      }
    })
  },
  display: {},
  overflow: {},
  textOverflow: {},
  visibility: {},
  whiteSpace: {},
  // flexbox
  flexBasis: {},
  flexDirection: {},
  flexWrap: {},
  justifyContent: {},
  alignItems: {},
  alignContent: {},
  order: {},
  flex: {},
  flexGrow: {},
  flexShrink: {},
  alignSelf: {},
  justifyItems: {},
  justifySelf: {},
  // grid
  gap: {
    style: Xr
  },
  rowGap: {
    style: Qr
  },
  columnGap: {
    style: Jr
  },
  gridColumn: {},
  gridRow: {},
  gridAutoFlow: {},
  gridAutoColumns: {},
  gridAutoRows: {},
  gridTemplateColumns: {},
  gridTemplateRows: {},
  gridTemplateAreas: {},
  gridArea: {},
  // positions
  position: {},
  zIndex: {
    themeKey: "zIndex"
  },
  top: {},
  right: {},
  bottom: {},
  left: {},
  // shadows
  boxShadow: {
    themeKey: "shadows"
  },
  // sizing
  width: {
    transform: Re
  },
  maxWidth: {
    style: Nt
  },
  minWidth: {
    transform: Re
  },
  height: {
    transform: Re
  },
  maxHeight: {
    transform: Re
  },
  minHeight: {
    transform: Re
  },
  boxSizing: {},
  // typography
  font: {
    themeKey: "font"
  },
  fontFamily: {
    themeKey: "typography"
  },
  fontSize: {
    themeKey: "typography"
  },
  fontStyle: {
    themeKey: "typography"
  },
  fontWeight: {
    themeKey: "typography"
  },
  letterSpacing: {},
  textTransform: {},
  lineHeight: {},
  textAlign: {},
  typography: {
    cssProperty: !1,
    themeKey: "typography"
  }
};
function da(...e) {
  const r = e.reduce((n, o) => n.concat(Object.keys(o)), []), t = new Set(r);
  return e.every((n) => t.size === Object.keys(n).length);
}
function pa(e, r) {
  return typeof e == "function" ? e(r) : e;
}
function ha() {
  function e(t, n, o, i) {
    const c = {
      [t]: n,
      theme: o
    }, l = i[t];
    if (!l)
      return {
        [t]: n
      };
    const {
      cssProperty: f = t,
      themeKey: d,
      transform: p,
      style: h
    } = l;
    if (n == null)
      return null;
    if (d === "typography" && n === "inherit")
      return {
        [t]: n
      };
    const m = Yr(o, d) || {};
    return h ? h(c) : We(c, n, (y) => {
      let s = Br(m, p, y);
      return y === s && typeof y == "string" && (s = Br(m, p, `${t}${y === "default" ? "" : Xe(y)}`, y)), f === !1 ? s : {
        [f]: s
      };
    });
  }
  function r(t) {
    const {
      sx: n,
      theme: o = {},
      nested: i
    } = t || {};
    if (!n)
      return null;
    const c = o.unstable_sxConfig ?? Zr;
    function l(f) {
      let d = f;
      if (typeof f == "function")
        d = f(o);
      else if (typeof f != "object")
        return f;
      if (!d)
        return null;
      const p = Oi(o.breakpoints), h = Object.keys(p);
      let m = p;
      return Object.keys(d).forEach((C) => {
        const y = pa(d[C], o);
        if (y != null)
          if (typeof y == "object")
            if (c[C])
              m = br(m, e(C, y, o, c));
            else {
              const s = We({
                theme: o
              }, y, (v) => ({
                [C]: v
              }));
              da(s, y) ? m[C] = r({
                sx: y,
                theme: o,
                nested: !0
              }) : m = br(m, s);
            }
          else
            m = br(m, e(C, y, o, c));
      }), !i && o.modularCssLayers ? {
        "@layer sx": Cn(o, wn(h, m))
      } : Cn(o, wn(h, m));
    }
    return Array.isArray(n) ? n.map(l) : l(n);
  }
  return r;
}
const sr = ha();
sr.filterProps = ["sx"];
function _t() {
  return _t = Object.assign ? Object.assign.bind() : function(e) {
    for (var r = 1; r < arguments.length; r++) {
      var t = arguments[r];
      for (var n in t) ({}).hasOwnProperty.call(t, n) && (e[n] = t[n]);
    }
    return e;
  }, _t.apply(null, arguments);
}
function ma(e) {
  if (e.sheet)
    return e.sheet;
  for (var r = 0; r < document.styleSheets.length; r++)
    if (document.styleSheets[r].ownerNode === e)
      return document.styleSheets[r];
}
function ga(e) {
  var r = document.createElement("style");
  return r.setAttribute("data-emotion", e.key), e.nonce !== void 0 && r.setAttribute("nonce", e.nonce), r.appendChild(document.createTextNode("")), r.setAttribute("data-s", ""), r;
}
var ya = /* @__PURE__ */ function() {
  function e(t) {
    var n = this;
    this._insertTag = function(o) {
      var i;
      n.tags.length === 0 ? n.insertionPoint ? i = n.insertionPoint.nextSibling : n.prepend ? i = n.container.firstChild : i = n.before : i = n.tags[n.tags.length - 1].nextSibling, n.container.insertBefore(o, i), n.tags.push(o);
    }, this.isSpeedy = t.speedy === void 0 ? !0 : t.speedy, this.tags = [], this.ctr = 0, this.nonce = t.nonce, this.key = t.key, this.container = t.container, this.prepend = t.prepend, this.insertionPoint = t.insertionPoint, this.before = null;
  }
  var r = e.prototype;
  return r.hydrate = function(n) {
    n.forEach(this._insertTag);
  }, r.insert = function(n) {
    this.ctr % (this.isSpeedy ? 65e3 : 1) === 0 && this._insertTag(ga(this));
    var o = this.tags[this.tags.length - 1];
    if (this.isSpeedy) {
      var i = ma(o);
      try {
        i.insertRule(n, i.cssRules.length);
      } catch {
      }
    } else
      o.appendChild(document.createTextNode(n));
    this.ctr++;
  }, r.flush = function() {
    this.tags.forEach(function(n) {
      var o;
      return (o = n.parentNode) == null ? void 0 : o.removeChild(n);
    }), this.tags = [], this.ctr = 0;
  }, e;
}(), Ce = "-ms-", Lr = "-moz-", K = "-webkit-", eo = "comm", Dt = "rule", Ft = "decl", ba = "@import", ro = "@keyframes", va = "@layer", xa = Math.abs, et = String.fromCharCode, Sa = Object.assign;
function Ea(e, r) {
  return ve(e, 0) ^ 45 ? (((r << 2 ^ ve(e, 0)) << 2 ^ ve(e, 1)) << 2 ^ ve(e, 2)) << 2 ^ ve(e, 3) : 0;
}
function to(e) {
  return e.trim();
}
function Ca(e, r) {
  return (e = r.exec(e)) ? e[0] : e;
}
function X(e, r, t) {
  return e.replace(r, t);
}
function Rt(e, r) {
  return e.indexOf(r);
}
function ve(e, r) {
  return e.charCodeAt(r) | 0;
}
function vr(e, r, t) {
  return e.slice(r, t);
}
function Ne(e) {
  return e.length;
}
function Bt(e) {
  return e.length;
}
function $r(e, r) {
  return r.push(e), e;
}
function Ta(e, r) {
  return e.map(r).join("");
}
var rt = 1, cr = 1, no = 0, _e = 0, ge = 0, lr = "";
function tt(e, r, t, n, o, i, c) {
  return { value: e, root: r, parent: t, type: n, props: o, children: i, line: rt, column: cr, length: c, return: "" };
}
function pr(e, r) {
  return Sa(tt("", null, null, "", null, null, 0), e, { length: -e.length }, r);
}
function wa() {
  return ge;
}
function _a() {
  return ge = _e > 0 ? ve(lr, --_e) : 0, cr--, ge === 10 && (cr = 1, rt--), ge;
}
function $e() {
  return ge = _e < no ? ve(lr, _e++) : 0, cr++, ge === 10 && (cr = 1, rt++), ge;
}
function Fe() {
  return ve(lr, _e);
}
function kr() {
  return _e;
}
function wr(e, r) {
  return vr(lr, e, r);
}
function xr(e) {
  switch (e) {
    case 0:
    case 9:
    case 10:
    case 13:
    case 32:
      return 5;
    case 33:
    case 43:
    case 44:
    case 47:
    case 62:
    case 64:
    case 126:
    case 59:
    case 123:
    case 125:
      return 4;
    case 58:
      return 3;
    case 34:
    case 39:
    case 40:
    case 91:
      return 2;
    case 41:
    case 93:
      return 1;
  }
  return 0;
}
function oo(e) {
  return rt = cr = 1, no = Ne(lr = e), _e = 0, [];
}
function io(e) {
  return lr = "", e;
}
function Ir(e) {
  return to(wr(_e - 1, Ot(e === 91 ? e + 2 : e === 40 ? e + 1 : e)));
}
function Ra(e) {
  for (; (ge = Fe()) && ge < 33; )
    $e();
  return xr(e) > 2 || xr(ge) > 3 ? "" : " ";
}
function Oa(e, r) {
  for (; --r && $e() && !(ge < 48 || ge > 102 || ge > 57 && ge < 65 || ge > 70 && ge < 97); )
    ;
  return wr(e, kr() + (r < 6 && Fe() == 32 && $e() == 32));
}
function Ot(e) {
  for (; $e(); )
    switch (ge) {
      case e:
        return _e;
      case 34:
      case 39:
        e !== 34 && e !== 39 && Ot(ge);
        break;
      case 40:
        e === 41 && Ot(e);
        break;
      case 92:
        $e();
        break;
    }
  return _e;
}
function $a(e, r) {
  for (; $e() && e + ge !== 57; )
    if (e + ge === 84 && Fe() === 47)
      break;
  return "/*" + wr(r, _e - 1) + "*" + et(e === 47 ? e : $e());
}
function Aa(e) {
  for (; !xr(Fe()); )
    $e();
  return wr(e, _e);
}
function Pa(e) {
  return io(jr("", null, null, null, [""], e = oo(e), 0, [0], e));
}
function jr(e, r, t, n, o, i, c, l, f) {
  for (var d = 0, p = 0, h = c, m = 0, C = 0, y = 0, s = 1, v = 1, w = 1, P = 0, x = "", _ = o, b = i, I = n, k = x; v; )
    switch (y = P, P = $e()) {
      case 40:
        if (y != 108 && ve(k, h - 1) == 58) {
          Rt(k += X(Ir(P), "&", "&\f"), "&\f") != -1 && (w = -1);
          break;
        }
      case 34:
      case 39:
      case 91:
        k += Ir(P);
        break;
      case 9:
      case 10:
      case 13:
      case 32:
        k += Ra(y);
        break;
      case 92:
        k += Oa(kr() - 1, 7);
        continue;
      case 47:
        switch (Fe()) {
          case 42:
          case 47:
            $r(ka($a($e(), kr()), r, t), f);
            break;
          default:
            k += "/";
        }
        break;
      case 123 * s:
        l[d++] = Ne(k) * w;
      case 125 * s:
      case 59:
      case 0:
        switch (P) {
          case 0:
          case 125:
            v = 0;
          case 59 + p:
            w == -1 && (k = X(k, /\f/g, "")), C > 0 && Ne(k) - h && $r(C > 32 ? On(k + ";", n, t, h - 1) : On(X(k, " ", "") + ";", n, t, h - 2), f);
            break;
          case 59:
            k += ";";
          default:
            if ($r(I = Rn(k, r, t, d, p, o, l, x, _ = [], b = [], h), i), P === 123)
              if (p === 0)
                jr(k, r, I, I, _, i, h, l, b);
              else
                switch (m === 99 && ve(k, 3) === 110 ? 100 : m) {
                  case 100:
                  case 108:
                  case 109:
                  case 115:
                    jr(e, I, I, n && $r(Rn(e, I, I, 0, 0, o, l, x, o, _ = [], h), b), o, b, h, l, n ? _ : b);
                    break;
                  default:
                    jr(k, I, I, I, [""], b, 0, l, b);
                }
        }
        d = p = C = 0, s = w = 1, x = k = "", h = c;
        break;
      case 58:
        h = 1 + Ne(k), C = y;
      default:
        if (s < 1) {
          if (P == 123)
            --s;
          else if (P == 125 && s++ == 0 && _a() == 125)
            continue;
        }
        switch (k += et(P), P * s) {
          case 38:
            w = p > 0 ? 1 : (k += "\f", -1);
            break;
          case 44:
            l[d++] = (Ne(k) - 1) * w, w = 1;
            break;
          case 64:
            Fe() === 45 && (k += Ir($e())), m = Fe(), p = h = Ne(x = k += Aa(kr())), P++;
            break;
          case 45:
            y === 45 && Ne(k) == 2 && (s = 0);
        }
    }
  return i;
}
function Rn(e, r, t, n, o, i, c, l, f, d, p) {
  for (var h = o - 1, m = o === 0 ? i : [""], C = Bt(m), y = 0, s = 0, v = 0; y < n; ++y)
    for (var w = 0, P = vr(e, h + 1, h = xa(s = c[y])), x = e; w < C; ++w)
      (x = to(s > 0 ? m[w] + " " + P : X(P, /&\f/g, m[w]))) && (f[v++] = x);
  return tt(e, r, t, o === 0 ? Dt : l, f, d, p);
}
function ka(e, r, t) {
  return tt(e, r, t, eo, et(wa()), vr(e, 2, -2), 0);
}
function On(e, r, t, n) {
  return tt(e, r, t, Ft, vr(e, 0, n), vr(e, n + 1, -1), n);
}
function ar(e, r) {
  for (var t = "", n = Bt(e), o = 0; o < n; o++)
    t += r(e[o], o, e, r) || "";
  return t;
}
function Ia(e, r, t, n) {
  switch (e.type) {
    case va:
      if (e.children.length) break;
    case ba:
    case Ft:
      return e.return = e.return || e.value;
    case eo:
      return "";
    case ro:
      return e.return = e.value + "{" + ar(e.children, n) + "}";
    case Dt:
      e.value = e.props.join(",");
  }
  return Ne(t = ar(e.children, n)) ? e.return = e.value + "{" + t + "}" : "";
}
function ja(e) {
  var r = Bt(e);
  return function(t, n, o, i) {
    for (var c = "", l = 0; l < r; l++)
      c += e[l](t, n, o, i) || "";
    return c;
  };
}
function Ma(e) {
  return function(r) {
    r.root || (r = r.return) && e(r);
  };
}
function ao(e) {
  var r = /* @__PURE__ */ Object.create(null);
  return function(t) {
    return r[t] === void 0 && (r[t] = e(t)), r[t];
  };
}
var Na = function(r, t, n) {
  for (var o = 0, i = 0; o = i, i = Fe(), o === 38 && i === 12 && (t[n] = 1), !xr(i); )
    $e();
  return wr(r, _e);
}, Da = function(r, t) {
  var n = -1, o = 44;
  do
    switch (xr(o)) {
      case 0:
        o === 38 && Fe() === 12 && (t[n] = 1), r[n] += Na(_e - 1, t, n);
        break;
      case 2:
        r[n] += Ir(o);
        break;
      case 4:
        if (o === 44) {
          r[++n] = Fe() === 58 ? "&\f" : "", t[n] = r[n].length;
          break;
        }
      default:
        r[n] += et(o);
    }
  while (o = $e());
  return r;
}, Fa = function(r, t) {
  return io(Da(oo(r), t));
}, $n = /* @__PURE__ */ new WeakMap(), Ba = function(r) {
  if (!(r.type !== "rule" || !r.parent || // positive .length indicates that this rule contains pseudo
  // negative .length indicates that this rule has been already prefixed
  r.length < 1)) {
    for (var t = r.value, n = r.parent, o = r.column === n.column && r.line === n.line; n.type !== "rule"; )
      if (n = n.parent, !n) return;
    if (!(r.props.length === 1 && t.charCodeAt(0) !== 58 && !$n.get(n)) && !o) {
      $n.set(r, !0);
      for (var i = [], c = Fa(t, i), l = n.props, f = 0, d = 0; f < c.length; f++)
        for (var p = 0; p < l.length; p++, d++)
          r.props[d] = i[f] ? c[f].replace(/&\f/g, l[p]) : l[p] + " " + c[f];
    }
  }
}, La = function(r) {
  if (r.type === "decl") {
    var t = r.value;
    // charcode for l
    t.charCodeAt(0) === 108 && // charcode for b
    t.charCodeAt(2) === 98 && (r.return = "", r.value = "");
  }
};
function so(e, r) {
  switch (Ea(e, r)) {
    case 5103:
      return K + "print-" + e + e;
    case 5737:
    case 4201:
    case 3177:
    case 3433:
    case 1641:
    case 4457:
    case 2921:
    case 5572:
    case 6356:
    case 5844:
    case 3191:
    case 6645:
    case 3005:
    case 6391:
    case 5879:
    case 5623:
    case 6135:
    case 4599:
    case 4855:
    case 4215:
    case 6389:
    case 5109:
    case 5365:
    case 5621:
    case 3829:
      return K + e + e;
    case 5349:
    case 4246:
    case 4810:
    case 6968:
    case 2756:
      return K + e + Lr + e + Ce + e + e;
    case 6828:
    case 4268:
      return K + e + Ce + e + e;
    case 6165:
      return K + e + Ce + "flex-" + e + e;
    case 5187:
      return K + e + X(e, /(\w+).+(:[^]+)/, K + "box-$1$2" + Ce + "flex-$1$2") + e;
    case 5443:
      return K + e + Ce + "flex-item-" + X(e, /flex-|-self/, "") + e;
    case 4675:
      return K + e + Ce + "flex-line-pack" + X(e, /align-content|flex-|-self/, "") + e;
    case 5548:
      return K + e + Ce + X(e, "shrink", "negative") + e;
    case 5292:
      return K + e + Ce + X(e, "basis", "preferred-size") + e;
    case 6060:
      return K + "box-" + X(e, "-grow", "") + K + e + Ce + X(e, "grow", "positive") + e;
    case 4554:
      return K + X(e, /([^-])(transform)/g, "$1" + K + "$2") + e;
    case 6187:
      return X(X(X(e, /(zoom-|grab)/, K + "$1"), /(image-set)/, K + "$1"), e, "") + e;
    case 5495:
    case 3959:
      return X(e, /(image-set\([^]*)/, K + "$1$`$1");
    case 4968:
      return X(X(e, /(.+:)(flex-)?(.*)/, K + "box-pack:$3" + Ce + "flex-pack:$3"), /s.+-b[^;]+/, "justify") + K + e + e;
    case 4095:
    case 3583:
    case 4068:
    case 2532:
      return X(e, /(.+)-inline(.+)/, K + "$1$2") + e;
    case 8116:
    case 7059:
    case 5753:
    case 5535:
    case 5445:
    case 5701:
    case 4933:
    case 4677:
    case 5533:
    case 5789:
    case 5021:
    case 4765:
      if (Ne(e) - 1 - r > 6) switch (ve(e, r + 1)) {
        case 109:
          if (ve(e, r + 4) !== 45) break;
        case 102:
          return X(e, /(.+:)(.+)-([^]+)/, "$1" + K + "$2-$3$1" + Lr + (ve(e, r + 3) == 108 ? "$3" : "$2-$3")) + e;
        case 115:
          return ~Rt(e, "stretch") ? so(X(e, "stretch", "fill-available"), r) + e : e;
      }
      break;
    case 4949:
      if (ve(e, r + 1) !== 115) break;
    case 6444:
      switch (ve(e, Ne(e) - 3 - (~Rt(e, "!important") && 10))) {
        case 107:
          return X(e, ":", ":" + K) + e;
        case 101:
          return X(e, /(.+:)([^;!]+)(;|!.+)?/, "$1" + K + (ve(e, 14) === 45 ? "inline-" : "") + "box$3$1" + K + "$2$3$1" + Ce + "$2box$3") + e;
      }
      break;
    case 5936:
      switch (ve(e, r + 11)) {
        case 114:
          return K + e + Ce + X(e, /[svh]\w+-[tblr]{2}/, "tb") + e;
        case 108:
          return K + e + Ce + X(e, /[svh]\w+-[tblr]{2}/, "tb-rl") + e;
        case 45:
          return K + e + Ce + X(e, /[svh]\w+-[tblr]{2}/, "lr") + e;
      }
      return K + e + Ce + e + e;
  }
  return e;
}
var Va = function(r, t, n, o) {
  if (r.length > -1 && !r.return) switch (r.type) {
    case Ft:
      r.return = so(r.value, r.length);
      break;
    case ro:
      return ar([pr(r, {
        value: X(r.value, "@", "@" + K)
      })], o);
    case Dt:
      if (r.length) return Ta(r.props, function(i) {
        switch (Ca(i, /(::plac\w+|:read-\w+)/)) {
          case ":read-only":
          case ":read-write":
            return ar([pr(r, {
              props: [X(i, /:(read-\w+)/, ":" + Lr + "$1")]
            })], o);
          case "::placeholder":
            return ar([pr(r, {
              props: [X(i, /:(plac\w+)/, ":" + K + "input-$1")]
            }), pr(r, {
              props: [X(i, /:(plac\w+)/, ":" + Lr + "$1")]
            }), pr(r, {
              props: [X(i, /:(plac\w+)/, Ce + "input-$1")]
            })], o);
        }
        return "";
      });
  }
}, Wa = [Va], za = function(r) {
  var t = r.key;
  if (t === "css") {
    var n = document.querySelectorAll("style[data-emotion]:not([data-s])");
    Array.prototype.forEach.call(n, function(s) {
      var v = s.getAttribute("data-emotion");
      v.indexOf(" ") !== -1 && (document.head.appendChild(s), s.setAttribute("data-s", ""));
    });
  }
  var o = r.stylisPlugins || Wa, i = {}, c, l = [];
  c = r.container || document.head, Array.prototype.forEach.call(
    // this means we will ignore elements which don't have a space in them which
    // means that the style elements we're looking at are only Emotion 11 server-rendered style elements
    document.querySelectorAll('style[data-emotion^="' + t + ' "]'),
    function(s) {
      for (var v = s.getAttribute("data-emotion").split(" "), w = 1; w < v.length; w++)
        i[v[w]] = !0;
      l.push(s);
    }
  );
  var f, d = [Ba, La];
  {
    var p, h = [Ia, Ma(function(s) {
      p.insert(s);
    })], m = ja(d.concat(o, h)), C = function(v) {
      return ar(Pa(v), m);
    };
    f = function(v, w, P, x) {
      p = P, C(v ? v + "{" + w.styles + "}" : w.styles), x && (y.inserted[w.name] = !0);
    };
  }
  var y = {
    key: t,
    sheet: new ya({
      key: t,
      container: c,
      nonce: r.nonce,
      speedy: r.speedy,
      prepend: r.prepend,
      insertionPoint: r.insertionPoint
    }),
    nonce: r.nonce,
    inserted: i,
    registered: {},
    insert: f
  };
  return y.sheet.hydrate(l), y;
}, Ua = !0;
function Ya(e, r, t) {
  var n = "";
  return t.split(" ").forEach(function(o) {
    e[o] !== void 0 ? r.push(e[o] + ";") : o && (n += o + " ");
  }), n;
}
var co = function(r, t, n) {
  var o = r.key + "-" + t.name;
  // we only need to add the styles to the registered cache if the
  // class name could be used further down
  // the tree but if it's a string tag, we know it won't
  // so we don't have to add it to registered cache.
  // this improves memory usage since we can avoid storing the whole style string
  (n === !1 || // we need to always store it if we're in compat mode and
  // in node since emotion-server relies on whether a style is in
  // the registered cache to know whether a style is global or not
  // also, note that this check will be dead code eliminated in the browser
  Ua === !1) && r.registered[o] === void 0 && (r.registered[o] = t.styles);
}, Ga = function(r, t, n) {
  co(r, t, n);
  var o = r.key + "-" + t.name;
  if (r.inserted[t.name] === void 0) {
    var i = t;
    do
      r.insert(t === i ? "." + o : "", i, r.sheet, !0), i = i.next;
    while (i !== void 0);
  }
};
function qa(e) {
  for (var r = 0, t, n = 0, o = e.length; o >= 4; ++n, o -= 4)
    t = e.charCodeAt(n) & 255 | (e.charCodeAt(++n) & 255) << 8 | (e.charCodeAt(++n) & 255) << 16 | (e.charCodeAt(++n) & 255) << 24, t = /* Math.imul(k, m): */
    (t & 65535) * 1540483477 + ((t >>> 16) * 59797 << 16), t ^= /* k >>> r: */
    t >>> 24, r = /* Math.imul(k, m): */
    (t & 65535) * 1540483477 + ((t >>> 16) * 59797 << 16) ^ /* Math.imul(h, m): */
    (r & 65535) * 1540483477 + ((r >>> 16) * 59797 << 16);
  switch (o) {
    case 3:
      r ^= (e.charCodeAt(n + 2) & 255) << 16;
    case 2:
      r ^= (e.charCodeAt(n + 1) & 255) << 8;
    case 1:
      r ^= e.charCodeAt(n) & 255, r = /* Math.imul(h, m): */
      (r & 65535) * 1540483477 + ((r >>> 16) * 59797 << 16);
  }
  return r ^= r >>> 13, r = /* Math.imul(h, m): */
  (r & 65535) * 1540483477 + ((r >>> 16) * 59797 << 16), ((r ^ r >>> 15) >>> 0).toString(36);
}
var Ha = {
  animationIterationCount: 1,
  aspectRatio: 1,
  borderImageOutset: 1,
  borderImageSlice: 1,
  borderImageWidth: 1,
  boxFlex: 1,
  boxFlexGroup: 1,
  boxOrdinalGroup: 1,
  columnCount: 1,
  columns: 1,
  flex: 1,
  flexGrow: 1,
  flexPositive: 1,
  flexShrink: 1,
  flexNegative: 1,
  flexOrder: 1,
  gridRow: 1,
  gridRowEnd: 1,
  gridRowSpan: 1,
  gridRowStart: 1,
  gridColumn: 1,
  gridColumnEnd: 1,
  gridColumnSpan: 1,
  gridColumnStart: 1,
  msGridRow: 1,
  msGridRowSpan: 1,
  msGridColumn: 1,
  msGridColumnSpan: 1,
  fontWeight: 1,
  lineHeight: 1,
  opacity: 1,
  order: 1,
  orphans: 1,
  scale: 1,
  tabSize: 1,
  widows: 1,
  zIndex: 1,
  zoom: 1,
  WebkitLineClamp: 1,
  // SVG-related properties
  fillOpacity: 1,
  floodOpacity: 1,
  stopOpacity: 1,
  strokeDasharray: 1,
  strokeDashoffset: 1,
  strokeMiterlimit: 1,
  strokeOpacity: 1,
  strokeWidth: 1
}, Ka = /[A-Z]|^ms/g, Xa = /_EMO_([^_]+?)_([^]*?)_EMO_/g, lo = function(r) {
  return r.charCodeAt(1) === 45;
}, An = function(r) {
  return r != null && typeof r != "boolean";
}, mt = /* @__PURE__ */ ao(function(e) {
  return lo(e) ? e : e.replace(Ka, "-$&").toLowerCase();
}), Pn = function(r, t) {
  switch (r) {
    case "animation":
    case "animationName":
      if (typeof t == "string")
        return t.replace(Xa, function(n, o, i) {
          return De = {
            name: o,
            styles: i,
            next: De
          }, o;
        });
  }
  return Ha[r] !== 1 && !lo(r) && typeof t == "number" && t !== 0 ? t + "px" : t;
};
function Sr(e, r, t) {
  if (t == null)
    return "";
  var n = t;
  if (n.__emotion_styles !== void 0)
    return n;
  switch (typeof t) {
    case "boolean":
      return "";
    case "object": {
      var o = t;
      if (o.anim === 1)
        return De = {
          name: o.name,
          styles: o.styles,
          next: De
        }, o.name;
      var i = t;
      if (i.styles !== void 0) {
        var c = i.next;
        if (c !== void 0)
          for (; c !== void 0; )
            De = {
              name: c.name,
              styles: c.styles,
              next: De
            }, c = c.next;
        var l = i.styles + ";";
        return l;
      }
      return Ja(e, r, t);
    }
    case "function": {
      if (e !== void 0) {
        var f = De, d = t(e);
        return De = f, Sr(e, r, d);
      }
      break;
    }
  }
  var p = t;
  if (r == null)
    return p;
  var h = r[p];
  return h !== void 0 ? h : p;
}
function Ja(e, r, t) {
  var n = "";
  if (Array.isArray(t))
    for (var o = 0; o < t.length; o++)
      n += Sr(e, r, t[o]) + ";";
  else
    for (var i in t) {
      var c = t[i];
      if (typeof c != "object") {
        var l = c;
        r != null && r[l] !== void 0 ? n += i + "{" + r[l] + "}" : An(l) && (n += mt(i) + ":" + Pn(i, l) + ";");
      } else if (Array.isArray(c) && typeof c[0] == "string" && (r == null || r[c[0]] === void 0))
        for (var f = 0; f < c.length; f++)
          An(c[f]) && (n += mt(i) + ":" + Pn(i, c[f]) + ";");
      else {
        var d = Sr(e, r, c);
        switch (i) {
          case "animation":
          case "animationName": {
            n += mt(i) + ":" + d + ";";
            break;
          }
          default:
            n += i + "{" + d + "}";
        }
      }
    }
  return n;
}
var kn = /label:\s*([^\s;{]+)\s*(;|$)/g, De;
function uo(e, r, t) {
  if (e.length === 1 && typeof e[0] == "object" && e[0] !== null && e[0].styles !== void 0)
    return e[0];
  var n = !0, o = "";
  De = void 0;
  var i = e[0];
  if (i == null || i.raw === void 0)
    n = !1, o += Sr(t, r, i);
  else {
    var c = i;
    o += c[0];
  }
  for (var l = 1; l < e.length; l++)
    if (o += Sr(t, r, e[l]), n) {
      var f = i;
      o += f[l];
    }
  kn.lastIndex = 0;
  for (var d = "", p; (p = kn.exec(o)) !== null; )
    d += "-" + p[1];
  var h = qa(o) + d;
  return {
    name: h,
    styles: o,
    next: De
  };
}
var Qa = function(r) {
  return r();
}, Za = xe.useInsertionEffect ? xe.useInsertionEffect : !1, es = Za || Qa, fo = /* @__PURE__ */ xe.createContext(
  // we're doing this to avoid preconstruct's dead code elimination in this one case
  // because this module is primarily intended for the browser and node
  // but it's also required in react native and similar environments sometimes
  // and we could have a special build just for that
  // but this is much easier and the native packages
  // might use a different theme context in the future anyway
  typeof HTMLElement < "u" ? /* @__PURE__ */ za({
    key: "css"
  }) : null
);
fo.Provider;
var rs = function(r) {
  return /* @__PURE__ */ qo(function(t, n) {
    var o = Ho(fo);
    return r(t, o, n);
  });
}, ts = /* @__PURE__ */ xe.createContext({}), ns = /^((children|dangerouslySetInnerHTML|key|ref|autoFocus|defaultValue|defaultChecked|innerHTML|suppressContentEditableWarning|suppressHydrationWarning|valueLink|abbr|accept|acceptCharset|accessKey|action|allow|allowUserMedia|allowPaymentRequest|allowFullScreen|allowTransparency|alt|async|autoComplete|autoPlay|capture|cellPadding|cellSpacing|challenge|charSet|checked|cite|classID|className|cols|colSpan|content|contentEditable|contextMenu|controls|controlsList|coords|crossOrigin|data|dateTime|decoding|default|defer|dir|disabled|disablePictureInPicture|disableRemotePlayback|download|draggable|encType|enterKeyHint|fetchpriority|fetchPriority|form|formAction|formEncType|formMethod|formNoValidate|formTarget|frameBorder|headers|height|hidden|high|href|hrefLang|htmlFor|httpEquiv|id|inputMode|integrity|is|keyParams|keyType|kind|label|lang|list|loading|loop|low|marginHeight|marginWidth|max|maxLength|media|mediaGroup|method|min|minLength|multiple|muted|name|nonce|noValidate|open|optimum|pattern|placeholder|playsInline|popover|popoverTarget|popoverTargetAction|poster|preload|profile|radioGroup|readOnly|referrerPolicy|rel|required|reversed|role|rows|rowSpan|sandbox|scope|scoped|scrolling|seamless|selected|shape|size|sizes|slot|span|spellCheck|src|srcDoc|srcLang|srcSet|start|step|style|summary|tabIndex|target|title|translate|type|useMap|value|width|wmode|wrap|about|datatype|inlist|prefix|property|resource|typeof|vocab|autoCapitalize|autoCorrect|autoSave|color|incremental|fallback|inert|itemProp|itemScope|itemType|itemID|itemRef|on|option|results|security|unselectable|accentHeight|accumulate|additive|alignmentBaseline|allowReorder|alphabetic|amplitude|arabicForm|ascent|attributeName|attributeType|autoReverse|azimuth|baseFrequency|baselineShift|baseProfile|bbox|begin|bias|by|calcMode|capHeight|clip|clipPathUnits|clipPath|clipRule|colorInterpolation|colorInterpolationFilters|colorProfile|colorRendering|contentScriptType|contentStyleType|cursor|cx|cy|d|decelerate|descent|diffuseConstant|direction|display|divisor|dominantBaseline|dur|dx|dy|edgeMode|elevation|enableBackground|end|exponent|externalResourcesRequired|fill|fillOpacity|fillRule|filter|filterRes|filterUnits|floodColor|floodOpacity|focusable|fontFamily|fontSize|fontSizeAdjust|fontStretch|fontStyle|fontVariant|fontWeight|format|from|fr|fx|fy|g1|g2|glyphName|glyphOrientationHorizontal|glyphOrientationVertical|glyphRef|gradientTransform|gradientUnits|hanging|horizAdvX|horizOriginX|ideographic|imageRendering|in|in2|intercept|k|k1|k2|k3|k4|kernelMatrix|kernelUnitLength|kerning|keyPoints|keySplines|keyTimes|lengthAdjust|letterSpacing|lightingColor|limitingConeAngle|local|markerEnd|markerMid|markerStart|markerHeight|markerUnits|markerWidth|mask|maskContentUnits|maskUnits|mathematical|mode|numOctaves|offset|opacity|operator|order|orient|orientation|origin|overflow|overlinePosition|overlineThickness|panose1|paintOrder|pathLength|patternContentUnits|patternTransform|patternUnits|pointerEvents|points|pointsAtX|pointsAtY|pointsAtZ|preserveAlpha|preserveAspectRatio|primitiveUnits|r|radius|refX|refY|renderingIntent|repeatCount|repeatDur|requiredExtensions|requiredFeatures|restart|result|rotate|rx|ry|scale|seed|shapeRendering|slope|spacing|specularConstant|specularExponent|speed|spreadMethod|startOffset|stdDeviation|stemh|stemv|stitchTiles|stopColor|stopOpacity|strikethroughPosition|strikethroughThickness|string|stroke|strokeDasharray|strokeDashoffset|strokeLinecap|strokeLinejoin|strokeMiterlimit|strokeOpacity|strokeWidth|surfaceScale|systemLanguage|tableValues|targetX|targetY|textAnchor|textDecoration|textRendering|textLength|to|transform|u1|u2|underlinePosition|underlineThickness|unicode|unicodeBidi|unicodeRange|unitsPerEm|vAlphabetic|vHanging|vIdeographic|vMathematical|values|vectorEffect|version|vertAdvY|vertOriginX|vertOriginY|viewBox|viewTarget|visibility|widths|wordSpacing|writingMode|x|xHeight|x1|x2|xChannelSelector|xlinkActuate|xlinkArcrole|xlinkHref|xlinkRole|xlinkShow|xlinkTitle|xlinkType|xmlBase|xmlns|xmlnsXlink|xmlLang|xmlSpace|y|y1|y2|yChannelSelector|z|zoomAndPan|for|class|autofocus)|(([Dd][Aa][Tt][Aa]|[Aa][Rr][Ii][Aa]|x)-.*))$/, os = /* @__PURE__ */ ao(
  function(e) {
    return ns.test(e) || e.charCodeAt(0) === 111 && e.charCodeAt(1) === 110 && e.charCodeAt(2) < 91;
  }
  /* Z+1 */
), is = os, as = function(r) {
  return r !== "theme";
}, In = function(r) {
  return typeof r == "string" && // 96 is one less than the char code
  // for "a" so this is checking that
  // it's a lowercase character
  r.charCodeAt(0) > 96 ? is : as;
}, jn = function(r, t, n) {
  var o;
  if (t) {
    var i = t.shouldForwardProp;
    o = r.__emotion_forwardProp && i ? function(c) {
      return r.__emotion_forwardProp(c) && i(c);
    } : i;
  }
  return typeof o != "function" && n && (o = r.__emotion_forwardProp), o;
}, ss = function(r) {
  var t = r.cache, n = r.serialized, o = r.isStringTag;
  return co(t, n, o), es(function() {
    return Ga(t, n, o);
  }), null;
}, cs = function e(r, t) {
  var n = r.__emotion_real === r, o = n && r.__emotion_base || r, i, c;
  t !== void 0 && (i = t.label, c = t.target);
  var l = jn(r, t, n), f = l || In(o), d = !f("as");
  return function() {
    var p = arguments, h = n && r.__emotion_styles !== void 0 ? r.__emotion_styles.slice(0) : [];
    if (i !== void 0 && h.push("label:" + i + ";"), p[0] == null || p[0].raw === void 0)
      h.push.apply(h, p);
    else {
      var m = p[0];
      h.push(m[0]);
      for (var C = p.length, y = 1; y < C; y++)
        h.push(p[y], m[y]);
    }
    var s = rs(function(v, w, P) {
      var x = d && v.as || o, _ = "", b = [], I = v;
      if (v.theme == null) {
        I = {};
        for (var k in v)
          I[k] = v[k];
        I.theme = xe.useContext(ts);
      }
      typeof v.className == "string" ? _ = Ya(w.registered, b, v.className) : v.className != null && (_ = v.className + " ");
      var ue = uo(h.concat(b), w.registered, I);
      _ += w.key + "-" + ue.name, c !== void 0 && (_ += " " + c);
      var ee = d && l === void 0 ? In(x) : f, a = {};
      for (var $ in v)
        d && $ === "as" || ee($) && (a[$] = v[$]);
      return a.className = _, P && (a.ref = P), /* @__PURE__ */ xe.createElement(xe.Fragment, null, /* @__PURE__ */ xe.createElement(ss, {
        cache: w,
        serialized: ue,
        isStringTag: typeof x == "string"
      }), /* @__PURE__ */ xe.createElement(x, a));
    });
    return s.displayName = i !== void 0 ? i : "Styled(" + (typeof o == "string" ? o : o.displayName || o.name || "Component") + ")", s.defaultProps = r.defaultProps, s.__emotion_real = s, s.__emotion_base = o, s.__emotion_styles = h, s.__emotion_forwardProp = l, Object.defineProperty(s, "toString", {
      value: function() {
        return "." + c;
      }
    }), s.withComponent = function(v, w) {
      var P = e(v, _t({}, t, w, {
        shouldForwardProp: jn(s, w, !0)
      }));
      return P.apply(void 0, h);
    }, s;
  };
}, ls = [
  "a",
  "abbr",
  "address",
  "area",
  "article",
  "aside",
  "audio",
  "b",
  "base",
  "bdi",
  "bdo",
  "big",
  "blockquote",
  "body",
  "br",
  "button",
  "canvas",
  "caption",
  "cite",
  "code",
  "col",
  "colgroup",
  "data",
  "datalist",
  "dd",
  "del",
  "details",
  "dfn",
  "dialog",
  "div",
  "dl",
  "dt",
  "em",
  "embed",
  "fieldset",
  "figcaption",
  "figure",
  "footer",
  "form",
  "h1",
  "h2",
  "h3",
  "h4",
  "h5",
  "h6",
  "head",
  "header",
  "hgroup",
  "hr",
  "html",
  "i",
  "iframe",
  "img",
  "input",
  "ins",
  "kbd",
  "keygen",
  "label",
  "legend",
  "li",
  "link",
  "main",
  "map",
  "mark",
  "marquee",
  "menu",
  "menuitem",
  "meta",
  "meter",
  "nav",
  "noscript",
  "object",
  "ol",
  "optgroup",
  "option",
  "output",
  "p",
  "param",
  "picture",
  "pre",
  "progress",
  "q",
  "rp",
  "rt",
  "ruby",
  "s",
  "samp",
  "script",
  "section",
  "select",
  "small",
  "source",
  "span",
  "strong",
  "style",
  "sub",
  "summary",
  "sup",
  "table",
  "tbody",
  "td",
  "textarea",
  "tfoot",
  "th",
  "thead",
  "time",
  "title",
  "tr",
  "track",
  "u",
  "ul",
  "var",
  "video",
  "wbr",
  // SVG
  "circle",
  "clipPath",
  "defs",
  "ellipse",
  "foreignObject",
  "g",
  "image",
  "line",
  "linearGradient",
  "mask",
  "path",
  "pattern",
  "polygon",
  "polyline",
  "radialGradient",
  "rect",
  "stop",
  "svg",
  "text",
  "tspan"
], $t = cs.bind(null);
ls.forEach(function(e) {
  $t[e] = $t(e);
});
/**
 * @mui/styled-engine v6.5.0
 *
 * @license MIT
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
function us(e, r) {
  const t = $t(e, r);
  return process.env.NODE_ENV !== "production" ? (...n) => {
    const o = typeof e == "string" ? `"${e}"` : "component";
    return n.length === 0 ? console.error([`MUI: Seems like you called \`styled(${o})()\` without a \`style\` argument.`, 'You must provide a `styles` argument: `styled("div")(styleYouForgotToPass)`.'].join(`
`)) : n.some((i) => i === void 0) && console.error(`MUI: the styled(${o})(...args) API requires all its args to be defined.`), t(...n);
  } : t;
}
function fs(e, r) {
  Array.isArray(e.__emotion_styles) && (e.__emotion_styles = r(e.__emotion_styles));
}
const Mn = [];
function Ke(e) {
  return Mn[0] = e, uo(Mn);
}
const ds = (e) => {
  const r = Object.keys(e).map((t) => ({
    key: t,
    val: e[t]
  })) || [];
  return r.sort((t, n) => t.val - n.val), r.reduce((t, n) => ({
    ...t,
    [n.key]: n.val
  }), {});
};
function ps(e) {
  const {
    // The breakpoint **start** at this value.
    // For instance with the first breakpoint xs: [xs, sm).
    values: r = {
      xs: 0,
      // phone
      sm: 600,
      // tablet
      md: 900,
      // small laptop
      lg: 1200,
      // desktop
      xl: 1536
      // large screen
    },
    unit: t = "px",
    step: n = 5,
    ...o
  } = e, i = ds(r), c = Object.keys(i);
  function l(m) {
    return `@media (min-width:${typeof r[m] == "number" ? r[m] : m}${t})`;
  }
  function f(m) {
    return `@media (max-width:${(typeof r[m] == "number" ? r[m] : m) - n / 100}${t})`;
  }
  function d(m, C) {
    const y = c.indexOf(C);
    return `@media (min-width:${typeof r[m] == "number" ? r[m] : m}${t}) and (max-width:${(y !== -1 && typeof r[c[y]] == "number" ? r[c[y]] : C) - n / 100}${t})`;
  }
  function p(m) {
    return c.indexOf(m) + 1 < c.length ? d(m, c[c.indexOf(m) + 1]) : l(m);
  }
  function h(m) {
    const C = c.indexOf(m);
    return C === 0 ? l(c[1]) : C === c.length - 1 ? f(c[C]) : d(m, c[c.indexOf(m) + 1]).replace("@media", "@media not all and");
  }
  return {
    keys: c,
    values: i,
    up: l,
    down: f,
    between: d,
    only: p,
    not: h,
    unit: t,
    ...o
  };
}
const hs = {
  borderRadius: 4
};
function po(e = 8, r = Mt({
  spacing: e
})) {
  if (e.mui)
    return e;
  const t = (...n) => (process.env.NODE_ENV !== "production" && (n.length <= 4 || console.error(`MUI: Too many arguments provided, expected between 0 and 4, got ${n.length}`)), (n.length === 0 ? [1] : n).map((i) => {
    const c = r(i);
    return typeof c == "number" ? `${c}px` : c;
  }).join(" "));
  return t.mui = !0, t;
}
function ms(e, r) {
  var n;
  const t = this;
  if (t.vars) {
    if (!((n = t.colorSchemes) != null && n[e]) || typeof t.getColorSchemeSelector != "function")
      return {};
    let o = t.getColorSchemeSelector(e);
    return o === "&" ? r : ((o.includes("data-") || o.includes(".")) && (o = `*:where(${o.replace(/\s*&$/, "")}) &`), {
      [o]: r
    });
  }
  return t.palette.mode === e ? r : {};
}
function ho(e = {}, ...r) {
  const {
    breakpoints: t = {},
    palette: n = {},
    spacing: o,
    shape: i = {},
    ...c
  } = e, l = ps(t), f = po(o);
  let d = Oe({
    breakpoints: l,
    direction: "ltr",
    components: {},
    // Inject component definitions.
    palette: {
      mode: "light",
      ...n
    },
    spacing: f,
    shape: {
      ...hs,
      ...i
    }
  }, c);
  return d = _i(d), d.applyStyles = ms, d = r.reduce((p, h) => Oe(p, h), d), d.unstable_sxConfig = {
    ...Zr,
    ...c == null ? void 0 : c.unstable_sxConfig
  }, d.unstable_sx = function(h) {
    return sr({
      sx: h,
      theme: this
    });
  }, d;
}
function mo(e) {
  const {
    variants: r,
    ...t
  } = e, n = {
    variants: r,
    style: Ke(t),
    isProcessed: !0
  };
  return n.style === t || r && r.forEach((o) => {
    typeof o.style != "function" && (o.style = Ke(o.style));
  }), n;
}
const gs = ho();
function gt(e) {
  return e !== "ownerState" && e !== "theme" && e !== "sx" && e !== "as";
}
function He(e, r) {
  return r && e && typeof e == "object" && e.styles && !e.styles.startsWith("@layer") && (e.styles = `@layer ${r}{${String(e.styles)}}`), e;
}
function ys(e) {
  return e ? (r, t) => t[e] : null;
}
function bs(e, r, t) {
  e.theme = Es(e.theme) ? t : e.theme[r] || e.theme;
}
function Mr(e, r, t) {
  const n = typeof r == "function" ? r(e) : r;
  if (Array.isArray(n))
    return n.flatMap((o) => Mr(e, o, t));
  if (Array.isArray(n == null ? void 0 : n.variants)) {
    let o;
    if (n.isProcessed)
      o = t ? He(n.style, t) : n.style;
    else {
      const {
        variants: i,
        ...c
      } = n;
      o = t ? He(Ke(c), t) : c;
    }
    return go(e, n.variants, [o], t);
  }
  return n != null && n.isProcessed ? t ? He(Ke(n.style), t) : n.style : t ? He(Ke(n), t) : n;
}
function go(e, r, t = [], n = void 0) {
  var i;
  let o;
  e: for (let c = 0; c < r.length; c += 1) {
    const l = r[c];
    if (typeof l.props == "function") {
      if (o ?? (o = {
        ...e,
        ...e.ownerState,
        ownerState: e.ownerState
      }), !l.props(o))
        continue;
    } else
      for (const f in l.props)
        if (e[f] !== l.props[f] && ((i = e.ownerState) == null ? void 0 : i[f]) !== l.props[f])
          continue e;
    typeof l.style == "function" ? (o ?? (o = {
      ...e,
      ...e.ownerState,
      ownerState: e.ownerState
    }), t.push(n ? He(Ke(l.style(o)), n) : l.style(o))) : t.push(n ? He(Ke(l.style), n) : l.style);
  }
  return t;
}
function vs(e = {}) {
  const {
    themeId: r,
    defaultTheme: t = gs,
    rootShouldForwardProp: n = gt,
    slotShouldForwardProp: o = gt
  } = e;
  function i(l) {
    bs(l, r, t);
  }
  return (l, f = {}) => {
    fs(l, (I) => I.filter((k) => k !== sr));
    const {
      name: d,
      slot: p,
      skipVariantsResolver: h,
      skipSx: m,
      // TODO v6: remove `lowercaseFirstLetter()` in the next major release
      // For more details: https://github.com/mui/material-ui/pull/37908
      overridesResolver: C = ys(yo(p)),
      ...y
    } = f, s = d && d.startsWith("Mui") || p ? "components" : "custom", v = h !== void 0 ? h : (
      // TODO v6: remove `Root` in the next major release
      // For more details: https://github.com/mui/material-ui/pull/37908
      p && p !== "Root" && p !== "root" || !1
    ), w = m || !1;
    let P = gt;
    p === "Root" || p === "root" ? P = n : p ? P = o : Cs(l) && (P = void 0);
    const x = us(l, {
      shouldForwardProp: P,
      label: Ss(d, p),
      ...y
    }), _ = (I) => {
      if (I.__emotion_real === I)
        return I;
      if (typeof I == "function")
        return function(ue) {
          return Mr(ue, I, ue.theme.modularCssLayers ? s : void 0);
        };
      if (Ve(I)) {
        const k = mo(I);
        return function(ee) {
          return k.variants ? Mr(ee, k, ee.theme.modularCssLayers ? s : void 0) : ee.theme.modularCssLayers ? He(k.style, s) : k.style;
        };
      }
      return I;
    }, b = (...I) => {
      const k = [], ue = I.map(_), ee = [];
      if (k.push(i), d && C && ee.push(function(M) {
        var Se, je;
        const se = (je = (Se = M.theme.components) == null ? void 0 : Se[d]) == null ? void 0 : je.styleOverrides;
        if (!se)
          return null;
        const ye = {};
        for (const Be in se)
          ye[Be] = Mr(M, se[Be], M.theme.modularCssLayers ? "theme" : void 0);
        return C(M, ye);
      }), d && !v && ee.push(function(M) {
        var ye, Se;
        const ne = M.theme, se = (Se = (ye = ne == null ? void 0 : ne.components) == null ? void 0 : ye[d]) == null ? void 0 : Se.variants;
        return se ? go(M, se, [], M.theme.modularCssLayers ? "theme" : void 0) : null;
      }), w || ee.push(sr), Array.isArray(ue[0])) {
        const B = ue.shift(), M = new Array(k.length).fill(""), ne = new Array(ee.length).fill("");
        let se;
        se = [...M, ...B, ...ne], se.raw = [...M, ...B.raw, ...ne], k.unshift(se);
      }
      const a = [...k, ...ue, ...ee], $ = x(...a);
      return l.muiName && ($.muiName = l.muiName), process.env.NODE_ENV !== "production" && ($.displayName = xs(d, p, l)), $;
    };
    return x.withConfig && (b.withConfig = x.withConfig), b;
  };
}
function xs(e, r, t) {
  return e ? `${e}${Xe(r || "")}` : `Styled(${gi(t)})`;
}
function Ss(e, r) {
  let t;
  return process.env.NODE_ENV !== "production" && e && (t = `${e}-${yo(r || "Root")}`), t;
}
function Es(e) {
  for (const r in e)
    return !1;
  return !0;
}
function Cs(e) {
  return typeof e == "string" && // 96 is one less than the char code
  // for "a" so this is checking that
  // it's a lowercase character
  e.charCodeAt(0) > 96;
}
function yo(e) {
  return e && e.charAt(0).toLowerCase() + e.slice(1);
}
function Lt(e, r = 0, t = 1) {
  return process.env.NODE_ENV !== "production" && (e < r || e > t) && console.error(`MUI: The value provided ${e} is out of range [${r}, ${t}].`), Ei(e, r, t);
}
function Ts(e) {
  e = e.slice(1);
  const r = new RegExp(`.{1,${e.length >= 6 ? 2 : 1}}`, "g");
  let t = e.match(r);
  return t && t[0].length === 1 && (t = t.map((n) => n + n)), process.env.NODE_ENV !== "production" && e.length !== e.trim().length && console.error(`MUI: The color: "${e}" is invalid. Make sure the color input doesn't contain leading/trailing space.`), t ? `rgb${t.length === 4 ? "a" : ""}(${t.map((n, o) => o < 3 ? parseInt(n, 16) : Math.round(parseInt(n, 16) / 255 * 1e3) / 1e3).join(", ")})` : "";
}
function Ue(e) {
  if (e.type)
    return e;
  if (e.charAt(0) === "#")
    return Ue(Ts(e));
  const r = e.indexOf("("), t = e.substring(0, r);
  if (!["rgb", "rgba", "hsl", "hsla", "color"].includes(t))
    throw new Error(process.env.NODE_ENV !== "production" ? `MUI: Unsupported \`${e}\` color.
The following formats are supported: #nnn, #nnnnnn, rgb(), rgba(), hsl(), hsla(), color().` : ze(9, e));
  let n = e.substring(r + 1, e.length - 1), o;
  if (t === "color") {
    if (n = n.split(" "), o = n.shift(), n.length === 4 && n[3].charAt(0) === "/" && (n[3] = n[3].slice(1)), !["srgb", "display-p3", "a98-rgb", "prophoto-rgb", "rec-2020"].includes(o))
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: unsupported \`${o}\` color space.
The following color spaces are supported: srgb, display-p3, a98-rgb, prophoto-rgb, rec-2020.` : ze(10, o));
  } else
    n = n.split(",");
  return n = n.map((i) => parseFloat(i)), {
    type: t,
    values: n,
    colorSpace: o
  };
}
const ws = (e) => {
  const r = Ue(e);
  return r.values.slice(0, 3).map((t, n) => r.type.includes("hsl") && n !== 0 ? `${t}%` : t).join(" ");
}, mr = (e, r) => {
  try {
    return ws(e);
  } catch {
    return r && process.env.NODE_ENV !== "production" && console.warn(r), e;
  }
};
function nt(e) {
  const {
    type: r,
    colorSpace: t
  } = e;
  let {
    values: n
  } = e;
  return r.includes("rgb") ? n = n.map((o, i) => i < 3 ? parseInt(o, 10) : o) : r.includes("hsl") && (n[1] = `${n[1]}%`, n[2] = `${n[2]}%`), r.includes("color") ? n = `${t} ${n.join(" ")}` : n = `${n.join(", ")}`, `${r}(${n})`;
}
function bo(e) {
  e = Ue(e);
  const {
    values: r
  } = e, t = r[0], n = r[1] / 100, o = r[2] / 100, i = n * Math.min(o, 1 - o), c = (d, p = (d + t / 30) % 12) => o - i * Math.max(Math.min(p - 3, 9 - p, 1), -1);
  let l = "rgb";
  const f = [Math.round(c(0) * 255), Math.round(c(8) * 255), Math.round(c(4) * 255)];
  return e.type === "hsla" && (l += "a", f.push(r[3])), nt({
    type: l,
    values: f
  });
}
function At(e) {
  e = Ue(e);
  let r = e.type === "hsl" || e.type === "hsla" ? Ue(bo(e)).values : e.values;
  return r = r.map((t) => (e.type !== "color" && (t /= 255), t <= 0.03928 ? t / 12.92 : ((t + 0.055) / 1.055) ** 2.4)), Number((0.2126 * r[0] + 0.7152 * r[1] + 0.0722 * r[2]).toFixed(3));
}
function Nn(e, r) {
  const t = At(e), n = At(r);
  return (Math.max(t, n) + 0.05) / (Math.min(t, n) + 0.05);
}
function _s(e, r) {
  return e = Ue(e), r = Lt(r), (e.type === "rgb" || e.type === "hsl") && (e.type += "a"), e.type === "color" ? e.values[3] = `/${r}` : e.values[3] = r, nt(e);
}
function Ar(e, r, t) {
  try {
    return _s(e, r);
  } catch {
    return e;
  }
}
function Vt(e, r) {
  if (e = Ue(e), r = Lt(r), e.type.includes("hsl"))
    e.values[2] *= 1 - r;
  else if (e.type.includes("rgb") || e.type.includes("color"))
    for (let t = 0; t < 3; t += 1)
      e.values[t] *= 1 - r;
  return nt(e);
}
function ie(e, r, t) {
  try {
    return Vt(e, r);
  } catch {
    return e;
  }
}
function Wt(e, r) {
  if (e = Ue(e), r = Lt(r), e.type.includes("hsl"))
    e.values[2] += (100 - e.values[2]) * r;
  else if (e.type.includes("rgb"))
    for (let t = 0; t < 3; t += 1)
      e.values[t] += (255 - e.values[t]) * r;
  else if (e.type.includes("color"))
    for (let t = 0; t < 3; t += 1)
      e.values[t] += (1 - e.values[t]) * r;
  return nt(e);
}
function ae(e, r, t) {
  try {
    return Wt(e, r);
  } catch {
    return e;
  }
}
function Rs(e, r = 0.15) {
  return At(e) > 0.5 ? Vt(e, r) : Wt(e, r);
}
function Pr(e, r, t) {
  try {
    return Rs(e, r);
  } catch {
    return e;
  }
}
const Os = /* @__PURE__ */ xe.createContext(void 0);
process.env.NODE_ENV !== "production" && (H.node, H.object);
function $s(e) {
  const {
    theme: r,
    name: t,
    props: n
  } = e;
  if (!r || !r.components || !r.components[t])
    return n;
  const o = r.components[t];
  return o.defaultProps ? wt(o.defaultProps, n) : !o.styleOverrides && !o.variants ? wt(o, n) : n;
}
function As({
  props: e,
  name: r
}) {
  const t = xe.useContext(Os);
  return $s({
    props: e,
    name: r,
    theme: {
      components: t
    }
  });
}
const Dn = {
  theme: void 0
};
function Ps(e) {
  let r, t;
  return function(o) {
    let i = r;
    return (i === void 0 || o.theme !== t) && (Dn.theme = o.theme, i = mo(e(Dn)), r = i, t = o.theme), i;
  };
}
function ks(e = "") {
  function r(...n) {
    if (!n.length)
      return "";
    const o = n[0];
    return typeof o == "string" && !o.match(/(#|\(|\)|(-?(\d*\.)?\d+)(px|em|%|ex|ch|rem|vw|vh|vmin|vmax|cm|mm|in|pt|pc))|^(-?(\d*\.)?\d+)$|(\d+ \d+ \d+)/) ? `, var(--${e ? `${e}-` : ""}${o}${r(...n.slice(1))})` : `, ${o}`;
  }
  return (n, ...o) => `var(--${e ? `${e}-` : ""}${n}${r(...o)})`;
}
const Fn = (e, r, t, n = []) => {
  let o = e;
  r.forEach((i, c) => {
    c === r.length - 1 ? Array.isArray(o) ? o[Number(i)] = t : o && typeof o == "object" && (o[i] = t) : o && typeof o == "object" && (o[i] || (o[i] = n.includes(i) ? [] : {}), o = o[i]);
  });
}, Is = (e, r, t) => {
  function n(o, i = [], c = []) {
    Object.entries(o).forEach(([l, f]) => {
      (!t || t && !t([...i, l])) && f != null && (typeof f == "object" && Object.keys(f).length > 0 ? n(f, [...i, l], Array.isArray(f) ? [...c, l] : c) : r([...i, l], f, c));
    });
  }
  n(e);
}, js = (e, r) => typeof r == "number" ? ["lineHeight", "fontWeight", "opacity", "zIndex"].some((n) => e.includes(n)) || e[e.length - 1].toLowerCase().includes("opacity") ? r : `${r}px` : r;
function yt(e, r) {
  const {
    prefix: t,
    shouldSkipGeneratingVar: n
  } = r || {}, o = {}, i = {}, c = {};
  return Is(
    e,
    (l, f, d) => {
      if ((typeof f == "string" || typeof f == "number") && (!n || !n(l, f))) {
        const p = `--${t ? `${t}-` : ""}${l.join("-")}`, h = js(l, f);
        Object.assign(o, {
          [p]: h
        }), Fn(i, l, `var(${p})`, d), Fn(c, l, `var(${p}, ${h})`, d);
      }
    },
    (l) => l[0] === "vars"
    // skip 'vars/*' paths
  ), {
    css: o,
    vars: i,
    varsWithDefaults: c
  };
}
function Ms(e, r = {}) {
  const {
    getSelector: t = v,
    disableCssColorScheme: n,
    colorSchemeSelector: o
  } = r, {
    colorSchemes: i = {},
    components: c,
    defaultColorScheme: l = "light",
    ...f
  } = e, {
    vars: d,
    css: p,
    varsWithDefaults: h
  } = yt(f, r);
  let m = h;
  const C = {}, {
    [l]: y,
    ...s
  } = i;
  if (Object.entries(s || {}).forEach(([x, _]) => {
    const {
      vars: b,
      css: I,
      varsWithDefaults: k
    } = yt(_, r);
    m = Oe(m, k), C[x] = {
      css: I,
      vars: b
    };
  }), y) {
    const {
      css: x,
      vars: _,
      varsWithDefaults: b
    } = yt(y, r);
    m = Oe(m, b), C[l] = {
      css: x,
      vars: _
    };
  }
  function v(x, _) {
    var I, k;
    let b = o;
    if (o === "class" && (b = ".%s"), o === "data" && (b = "[data-%s]"), o != null && o.startsWith("data-") && !o.includes("%s") && (b = `[${o}="%s"]`), x) {
      if (b === "media")
        return e.defaultColorScheme === x ? ":root" : {
          [`@media (prefers-color-scheme: ${((k = (I = i[x]) == null ? void 0 : I.palette) == null ? void 0 : k.mode) || x})`]: {
            ":root": _
          }
        };
      if (b)
        return e.defaultColorScheme === x ? `:root, ${b.replace("%s", String(x))}` : b.replace("%s", String(x));
    }
    return ":root";
  }
  return {
    vars: m,
    generateThemeVars: () => {
      let x = {
        ...d
      };
      return Object.entries(C).forEach(([, {
        vars: _
      }]) => {
        x = Oe(x, _);
      }), x;
    },
    generateStyleSheets: () => {
      var ue, ee;
      const x = [], _ = e.defaultColorScheme || "light";
      function b(a, $) {
        Object.keys($).length && x.push(typeof a == "string" ? {
          [a]: {
            ...$
          }
        } : a);
      }
      b(t(void 0, {
        ...p
      }), p);
      const {
        [_]: I,
        ...k
      } = C;
      if (I) {
        const {
          css: a
        } = I, $ = (ee = (ue = i[_]) == null ? void 0 : ue.palette) == null ? void 0 : ee.mode, B = !n && $ ? {
          colorScheme: $,
          ...a
        } : {
          ...a
        };
        b(t(_, {
          ...B
        }), B);
      }
      return Object.entries(k).forEach(([a, {
        css: $
      }]) => {
        var ne, se;
        const B = (se = (ne = i[a]) == null ? void 0 : ne.palette) == null ? void 0 : se.mode, M = !n && B ? {
          colorScheme: B,
          ...$
        } : {
          ...$
        };
        b(t(a, {
          ...M
        }), M);
      }), x;
    }
  };
}
function Ns(e) {
  return function(t) {
    return e === "media" ? (process.env.NODE_ENV !== "production" && t !== "light" && t !== "dark" && console.error(`MUI: @media (prefers-color-scheme) supports only 'light' or 'dark', but receive '${t}'.`), `@media (prefers-color-scheme: ${t})`) : e ? e.startsWith("data-") && !e.includes("%s") ? `[${e}="${t}"] &` : e === "class" ? `.${t} &` : e === "data" ? `[data-${t}] &` : `${e.replace("%s", t)} &` : "&";
  };
}
const Er = {
  black: "#000",
  white: "#fff"
}, Ds = {
  50: "#fafafa",
  100: "#f5f5f5",
  200: "#eeeeee",
  300: "#e0e0e0",
  400: "#bdbdbd",
  500: "#9e9e9e",
  600: "#757575",
  700: "#616161",
  800: "#424242",
  900: "#212121",
  A100: "#f5f5f5",
  A200: "#eeeeee",
  A400: "#bdbdbd",
  A700: "#616161"
}, er = {
  50: "#f3e5f5",
  200: "#ce93d8",
  300: "#ba68c8",
  400: "#ab47bc",
  500: "#9c27b0",
  700: "#7b1fa2"
}, rr = {
  300: "#e57373",
  400: "#ef5350",
  500: "#f44336",
  700: "#d32f2f",
  800: "#c62828"
}, hr = {
  300: "#ffb74d",
  400: "#ffa726",
  500: "#ff9800",
  700: "#f57c00",
  900: "#e65100"
}, tr = {
  50: "#e3f2fd",
  200: "#90caf9",
  400: "#42a5f5",
  700: "#1976d2",
  800: "#1565c0"
}, nr = {
  300: "#4fc3f7",
  400: "#29b6f6",
  500: "#03a9f4",
  700: "#0288d1",
  900: "#01579b"
}, or = {
  300: "#81c784",
  400: "#66bb6a",
  500: "#4caf50",
  700: "#388e3c",
  800: "#2e7d32",
  900: "#1b5e20"
};
function vo() {
  return {
    // The colors used to style the text.
    text: {
      // The most important text.
      primary: "rgba(0, 0, 0, 0.87)",
      // Secondary text.
      secondary: "rgba(0, 0, 0, 0.6)",
      // Disabled text have even lower visual prominence.
      disabled: "rgba(0, 0, 0, 0.38)"
    },
    // The color used to divide different elements.
    divider: "rgba(0, 0, 0, 0.12)",
    // The background colors used to style the surfaces.
    // Consistency between these values is important.
    background: {
      paper: Er.white,
      default: Er.white
    },
    // The colors used to style the action elements.
    action: {
      // The color of an active action like an icon button.
      active: "rgba(0, 0, 0, 0.54)",
      // The color of an hovered action.
      hover: "rgba(0, 0, 0, 0.04)",
      hoverOpacity: 0.04,
      // The color of a selected action.
      selected: "rgba(0, 0, 0, 0.08)",
      selectedOpacity: 0.08,
      // The color of a disabled action.
      disabled: "rgba(0, 0, 0, 0.26)",
      // The background color of a disabled action.
      disabledBackground: "rgba(0, 0, 0, 0.12)",
      disabledOpacity: 0.38,
      focus: "rgba(0, 0, 0, 0.12)",
      focusOpacity: 0.12,
      activatedOpacity: 0.12
    }
  };
}
const Fs = vo();
function xo() {
  return {
    text: {
      primary: Er.white,
      secondary: "rgba(255, 255, 255, 0.7)",
      disabled: "rgba(255, 255, 255, 0.5)",
      icon: "rgba(255, 255, 255, 0.5)"
    },
    divider: "rgba(255, 255, 255, 0.12)",
    background: {
      paper: "#121212",
      default: "#121212"
    },
    action: {
      active: Er.white,
      hover: "rgba(255, 255, 255, 0.08)",
      hoverOpacity: 0.08,
      selected: "rgba(255, 255, 255, 0.16)",
      selectedOpacity: 0.16,
      disabled: "rgba(255, 255, 255, 0.3)",
      disabledBackground: "rgba(255, 255, 255, 0.12)",
      disabledOpacity: 0.38,
      focus: "rgba(255, 255, 255, 0.12)",
      focusOpacity: 0.12,
      activatedOpacity: 0.24
    }
  };
}
const Bn = xo();
function Ln(e, r, t, n) {
  const o = n.light || n, i = n.dark || n * 1.5;
  e[r] || (e.hasOwnProperty(t) ? e[r] = e[t] : r === "light" ? e.light = Wt(e.main, o) : r === "dark" && (e.dark = Vt(e.main, i)));
}
function Bs(e = "light") {
  return e === "dark" ? {
    main: tr[200],
    light: tr[50],
    dark: tr[400]
  } : {
    main: tr[700],
    light: tr[400],
    dark: tr[800]
  };
}
function Ls(e = "light") {
  return e === "dark" ? {
    main: er[200],
    light: er[50],
    dark: er[400]
  } : {
    main: er[500],
    light: er[300],
    dark: er[700]
  };
}
function Vs(e = "light") {
  return e === "dark" ? {
    main: rr[500],
    light: rr[300],
    dark: rr[700]
  } : {
    main: rr[700],
    light: rr[400],
    dark: rr[800]
  };
}
function Ws(e = "light") {
  return e === "dark" ? {
    main: nr[400],
    light: nr[300],
    dark: nr[700]
  } : {
    main: nr[700],
    light: nr[500],
    dark: nr[900]
  };
}
function zs(e = "light") {
  return e === "dark" ? {
    main: or[400],
    light: or[300],
    dark: or[700]
  } : {
    main: or[800],
    light: or[500],
    dark: or[900]
  };
}
function Us(e = "light") {
  return e === "dark" ? {
    main: hr[400],
    light: hr[300],
    dark: hr[700]
  } : {
    main: "#ed6c02",
    // closest to orange[800] that pass 3:1.
    light: hr[500],
    dark: hr[900]
  };
}
function zt(e) {
  const {
    mode: r = "light",
    contrastThreshold: t = 3,
    tonalOffset: n = 0.2,
    ...o
  } = e, i = e.primary || Bs(r), c = e.secondary || Ls(r), l = e.error || Vs(r), f = e.info || Ws(r), d = e.success || zs(r), p = e.warning || Us(r);
  function h(s) {
    const v = Nn(s, Bn.text.primary) >= t ? Bn.text.primary : Fs.text.primary;
    if (process.env.NODE_ENV !== "production") {
      const w = Nn(s, v);
      w < 3 && console.error([`MUI: The contrast ratio of ${w}:1 for ${v} on ${s}`, "falls below the WCAG recommended absolute minimum contrast ratio of 3:1.", "https://www.w3.org/TR/2008/REC-WCAG20-20081211/#visual-audio-contrast-contrast"].join(`
`));
    }
    return v;
  }
  const m = ({
    color: s,
    name: v,
    mainShade: w = 500,
    lightShade: P = 300,
    darkShade: x = 700
  }) => {
    if (s = {
      ...s
    }, !s.main && s[w] && (s.main = s[w]), !s.hasOwnProperty("main"))
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The color${v ? ` (${v})` : ""} provided to augmentColor(color) is invalid.
The color object needs to have a \`main\` property or a \`${w}\` property.` : ze(11, v ? ` (${v})` : "", w));
    if (typeof s.main != "string")
      throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The color${v ? ` (${v})` : ""} provided to augmentColor(color) is invalid.
\`color.main\` should be a string, but \`${JSON.stringify(s.main)}\` was provided instead.

Did you intend to use one of the following approaches?

import { green } from "@mui/material/colors";

const theme1 = createTheme({ palette: {
  primary: green,
} });

const theme2 = createTheme({ palette: {
  primary: { main: green[500] },
} });` : ze(12, v ? ` (${v})` : "", JSON.stringify(s.main)));
    return Ln(s, "light", P, n), Ln(s, "dark", x, n), s.contrastText || (s.contrastText = h(s.main)), s;
  };
  let C;
  return r === "light" ? C = vo() : r === "dark" && (C = xo()), process.env.NODE_ENV !== "production" && (C || console.error(`MUI: The palette mode \`${r}\` is not supported.`)), Oe({
    // A collection of common colors.
    common: {
      ...Er
    },
    // prevent mutable object.
    // The palette mode, can be light or dark.
    mode: r,
    // The colors used to represent primary interface elements for a user.
    primary: m({
      color: i,
      name: "primary"
    }),
    // The colors used to represent secondary interface elements for a user.
    secondary: m({
      color: c,
      name: "secondary",
      mainShade: "A400",
      lightShade: "A200",
      darkShade: "A700"
    }),
    // The colors used to represent interface elements that the user should be made aware of.
    error: m({
      color: l,
      name: "error"
    }),
    // The colors used to represent potentially dangerous actions or important messages.
    warning: m({
      color: p,
      name: "warning"
    }),
    // The colors used to present information to the user that is neutral and not necessarily important.
    info: m({
      color: f,
      name: "info"
    }),
    // The colors used to indicate the successful completion of an action that user triggered.
    success: m({
      color: d,
      name: "success"
    }),
    // The grey colors.
    grey: Ds,
    // Used by `getContrastText()` to maximize the contrast between
    // the background and the text.
    contrastThreshold: t,
    // Takes a background color and returns the text color that maximizes the contrast.
    getContrastText: h,
    // Generate a rich color object.
    augmentColor: m,
    // Used by the functions below to shift a color's luminance by approximately
    // two indexes within its tonal palette.
    // E.g., shift from Red 500 to Red 300 or Red 700.
    tonalOffset: n,
    // The light and dark mode object.
    ...C
  }, o);
}
function Ys(e) {
  const r = {};
  return Object.entries(e).forEach((n) => {
    const [o, i] = n;
    typeof i == "object" && (r[o] = `${i.fontStyle ? `${i.fontStyle} ` : ""}${i.fontVariant ? `${i.fontVariant} ` : ""}${i.fontWeight ? `${i.fontWeight} ` : ""}${i.fontStretch ? `${i.fontStretch} ` : ""}${i.fontSize || ""}${i.lineHeight ? `/${i.lineHeight} ` : ""}${i.fontFamily || ""}`);
  }), r;
}
function Gs(e, r) {
  return {
    toolbar: {
      minHeight: 56,
      [e.up("xs")]: {
        "@media (orientation: landscape)": {
          minHeight: 48
        }
      },
      [e.up("sm")]: {
        minHeight: 64
      }
    },
    ...r
  };
}
function qs(e) {
  return Math.round(e * 1e5) / 1e5;
}
const Vn = {
  textTransform: "uppercase"
}, Wn = '"Roboto", "Helvetica", "Arial", sans-serif';
function Hs(e, r) {
  const {
    fontFamily: t = Wn,
    // The default font size of the Material Specification.
    fontSize: n = 14,
    // px
    fontWeightLight: o = 300,
    fontWeightRegular: i = 400,
    fontWeightMedium: c = 500,
    fontWeightBold: l = 700,
    // Tell MUI what's the font-size on the html element.
    // 16px is the default font-size used by browsers.
    htmlFontSize: f = 16,
    // Apply the CSS properties to all the variants.
    allVariants: d,
    pxToRem: p,
    ...h
  } = typeof r == "function" ? r(e) : r;
  process.env.NODE_ENV !== "production" && (typeof n != "number" && console.error("MUI: `fontSize` is required to be a number."), typeof f != "number" && console.error("MUI: `htmlFontSize` is required to be a number."));
  const m = n / 14, C = p || ((v) => `${v / f * m}rem`), y = (v, w, P, x, _) => ({
    fontFamily: t,
    fontWeight: v,
    fontSize: C(w),
    // Unitless following https://meyerweb.com/eric/thoughts/2006/02/08/unitless-line-heights/
    lineHeight: P,
    // The letter spacing was designed for the Roboto font-family. Using the same letter-spacing
    // across font-families can cause issues with the kerning.
    ...t === Wn ? {
      letterSpacing: `${qs(x / w)}em`
    } : {},
    ..._,
    ...d
  }), s = {
    h1: y(o, 96, 1.167, -1.5),
    h2: y(o, 60, 1.2, -0.5),
    h3: y(i, 48, 1.167, 0),
    h4: y(i, 34, 1.235, 0.25),
    h5: y(i, 24, 1.334, 0),
    h6: y(c, 20, 1.6, 0.15),
    subtitle1: y(i, 16, 1.75, 0.15),
    subtitle2: y(c, 14, 1.57, 0.1),
    body1: y(i, 16, 1.5, 0.15),
    body2: y(i, 14, 1.43, 0.15),
    button: y(c, 14, 1.75, 0.4, Vn),
    caption: y(i, 12, 1.66, 0.4),
    overline: y(i, 12, 2.66, 1, Vn),
    // TODO v6: Remove handling of 'inherit' variant from the theme as it is already handled in Material UI's Typography component. Also, remember to remove the associated types.
    inherit: {
      fontFamily: "inherit",
      fontWeight: "inherit",
      fontSize: "inherit",
      lineHeight: "inherit",
      letterSpacing: "inherit"
    }
  };
  return Oe({
    htmlFontSize: f,
    pxToRem: C,
    fontFamily: t,
    fontSize: n,
    fontWeightLight: o,
    fontWeightRegular: i,
    fontWeightMedium: c,
    fontWeightBold: l,
    ...s
  }, h, {
    clone: !1
    // No need to clone deep
  });
}
const Ks = 0.2, Xs = 0.14, Js = 0.12;
function ce(...e) {
  return [`${e[0]}px ${e[1]}px ${e[2]}px ${e[3]}px rgba(0,0,0,${Ks})`, `${e[4]}px ${e[5]}px ${e[6]}px ${e[7]}px rgba(0,0,0,${Xs})`, `${e[8]}px ${e[9]}px ${e[10]}px ${e[11]}px rgba(0,0,0,${Js})`].join(",");
}
const Qs = ["none", ce(0, 2, 1, -1, 0, 1, 1, 0, 0, 1, 3, 0), ce(0, 3, 1, -2, 0, 2, 2, 0, 0, 1, 5, 0), ce(0, 3, 3, -2, 0, 3, 4, 0, 0, 1, 8, 0), ce(0, 2, 4, -1, 0, 4, 5, 0, 0, 1, 10, 0), ce(0, 3, 5, -1, 0, 5, 8, 0, 0, 1, 14, 0), ce(0, 3, 5, -1, 0, 6, 10, 0, 0, 1, 18, 0), ce(0, 4, 5, -2, 0, 7, 10, 1, 0, 2, 16, 1), ce(0, 5, 5, -3, 0, 8, 10, 1, 0, 3, 14, 2), ce(0, 5, 6, -3, 0, 9, 12, 1, 0, 3, 16, 2), ce(0, 6, 6, -3, 0, 10, 14, 1, 0, 4, 18, 3), ce(0, 6, 7, -4, 0, 11, 15, 1, 0, 4, 20, 3), ce(0, 7, 8, -4, 0, 12, 17, 2, 0, 5, 22, 4), ce(0, 7, 8, -4, 0, 13, 19, 2, 0, 5, 24, 4), ce(0, 7, 9, -4, 0, 14, 21, 2, 0, 5, 26, 4), ce(0, 8, 9, -5, 0, 15, 22, 2, 0, 6, 28, 5), ce(0, 8, 10, -5, 0, 16, 24, 2, 0, 6, 30, 5), ce(0, 8, 11, -5, 0, 17, 26, 2, 0, 6, 32, 5), ce(0, 9, 11, -5, 0, 18, 28, 2, 0, 7, 34, 6), ce(0, 9, 12, -6, 0, 19, 29, 2, 0, 7, 36, 6), ce(0, 10, 13, -6, 0, 20, 31, 3, 0, 8, 38, 7), ce(0, 10, 13, -6, 0, 21, 33, 3, 0, 8, 40, 7), ce(0, 10, 14, -6, 0, 22, 35, 3, 0, 8, 42, 7), ce(0, 11, 14, -7, 0, 23, 36, 3, 0, 9, 44, 8), ce(0, 11, 15, -7, 0, 24, 38, 3, 0, 9, 46, 8)], Zs = {
  // This is the most common easing curve.
  easeInOut: "cubic-bezier(0.4, 0, 0.2, 1)",
  // Objects enter the screen at full velocity from off-screen and
  // slowly decelerate to a resting point.
  easeOut: "cubic-bezier(0.0, 0, 0.2, 1)",
  // Objects leave the screen at full velocity. They do not decelerate when off-screen.
  easeIn: "cubic-bezier(0.4, 0, 1, 1)",
  // The sharp curve is used by objects that may return to the screen at any time.
  sharp: "cubic-bezier(0.4, 0, 0.6, 1)"
}, ec = {
  shortest: 150,
  shorter: 200,
  short: 250,
  // most basic recommended timing
  standard: 300,
  // this is to be used in complex animations
  complex: 375,
  // recommended when something is entering screen
  enteringScreen: 225,
  // recommended when something is leaving screen
  leavingScreen: 195
};
function zn(e) {
  return `${Math.round(e)}ms`;
}
function rc(e) {
  if (!e)
    return 0;
  const r = e / 36;
  return Math.min(Math.round((4 + 15 * r ** 0.25 + r / 5) * 10), 3e3);
}
function tc(e) {
  const r = {
    ...Zs,
    ...e.easing
  }, t = {
    ...ec,
    ...e.duration
  };
  return {
    getAutoHeightDuration: rc,
    create: (o = ["all"], i = {}) => {
      const {
        duration: c = t.standard,
        easing: l = r.easeInOut,
        delay: f = 0,
        ...d
      } = i;
      if (process.env.NODE_ENV !== "production") {
        const p = (m) => typeof m == "string", h = (m) => !Number.isNaN(parseFloat(m));
        !p(o) && !Array.isArray(o) && console.error('MUI: Argument "props" must be a string or Array.'), !h(c) && !p(c) && console.error(`MUI: Argument "duration" must be a number or a string but found ${c}.`), p(l) || console.error('MUI: Argument "easing" must be a string.'), !h(f) && !p(f) && console.error('MUI: Argument "delay" must be a number or a string.'), typeof i != "object" && console.error(["MUI: Secong argument of transition.create must be an object.", "Arguments should be either `create('prop1', options)` or `create(['prop1', 'prop2'], options)`"].join(`
`)), Object.keys(d).length !== 0 && console.error(`MUI: Unrecognized argument(s) [${Object.keys(d).join(",")}].`);
      }
      return (Array.isArray(o) ? o : [o]).map((p) => `${p} ${typeof c == "string" ? c : zn(c)} ${l} ${typeof f == "string" ? f : zn(f)}`).join(",");
    },
    ...e,
    easing: r,
    duration: t
  };
}
const nc = {
  mobileStepper: 1e3,
  fab: 1050,
  speedDial: 1050,
  appBar: 1100,
  drawer: 1200,
  modal: 1300,
  snackbar: 1400,
  tooltip: 1500
};
function oc(e) {
  return Ve(e) || typeof e > "u" || typeof e == "string" || typeof e == "boolean" || typeof e == "number" || Array.isArray(e);
}
function So(e = {}) {
  const r = {
    ...e
  };
  function t(n) {
    const o = Object.entries(n);
    for (let i = 0; i < o.length; i++) {
      const [c, l] = o[i];
      !oc(l) || c.startsWith("unstable_") ? delete n[c] : Ve(l) && (n[c] = {
        ...l
      }, t(n[c]));
    }
  }
  return t(r), `import { unstable_createBreakpoints as createBreakpoints, createTransitions } from '@mui/material/styles';

const theme = ${JSON.stringify(r, null, 2)};

theme.breakpoints = createBreakpoints(theme.breakpoints || {});
theme.transitions = createTransitions(theme.transitions || {});

export default theme;`;
}
function Pt(e = {}, ...r) {
  const {
    breakpoints: t,
    mixins: n = {},
    spacing: o,
    palette: i = {},
    transitions: c = {},
    typography: l = {},
    shape: f,
    ...d
  } = e;
  if (e.vars && // The error should throw only for the root theme creation because user is not allowed to use a custom node `vars`.
  // `generateThemeVars` is the closest identifier for checking that the `options` is a result of `createTheme` with CSS variables so that user can create new theme for nested ThemeProvider.
  e.generateThemeVars === void 0)
    throw new Error(process.env.NODE_ENV !== "production" ? "MUI: `vars` is a private field used for CSS variables support.\nPlease use another name or follow the [docs](https://mui.com/material-ui/customization/css-theme-variables/usage/) to enable the feature." : ze(20));
  const p = zt(i), h = ho(e);
  let m = Oe(h, {
    mixins: Gs(h.breakpoints, n),
    palette: p,
    // Don't use [...shadows] until you've verified its transpiled code is not invoking the iterator protocol.
    shadows: Qs.slice(),
    typography: Hs(p, l),
    transitions: tc(c),
    zIndex: {
      ...nc
    }
  });
  if (m = Oe(m, d), m = r.reduce((C, y) => Oe(C, y), m), process.env.NODE_ENV !== "production") {
    const C = ["active", "checked", "completed", "disabled", "error", "expanded", "focused", "focusVisible", "required", "selected"], y = (s, v) => {
      let w;
      for (w in s) {
        const P = s[w];
        if (C.includes(w) && Object.keys(P).length > 0) {
          if (process.env.NODE_ENV !== "production") {
            const x = jt("", w);
            console.error([`MUI: The \`${v}\` component increases the CSS specificity of the \`${w}\` internal state.`, "You can not override it like this: ", JSON.stringify(s, null, 2), "", `Instead, you need to use the '&.${x}' syntax:`, JSON.stringify({
              root: {
                [`&.${x}`]: P
              }
            }, null, 2), "", "https://mui.com/r/state-classes-guide"].join(`
`));
          }
          s[w] = {};
        }
      }
    };
    Object.keys(m.components).forEach((s) => {
      const v = m.components[s].styleOverrides;
      v && s.startsWith("Mui") && y(v, s);
    });
  }
  return m.unstable_sxConfig = {
    ...Zr,
    ...d == null ? void 0 : d.unstable_sxConfig
  }, m.unstable_sx = function(y) {
    return sr({
      sx: y,
      theme: this
    });
  }, m.toRuntimeSource = So, m;
}
function ic(e) {
  let r;
  return e < 1 ? r = 5.11916 * e ** 2 : r = 4.5 * Math.log(e + 1) + 2, Math.round(r * 10) / 1e3;
}
const ac = [...Array(25)].map((e, r) => {
  if (r === 0)
    return "none";
  const t = ic(r);
  return `linear-gradient(rgba(255 255 255 / ${t}), rgba(255 255 255 / ${t}))`;
});
function Eo(e) {
  return {
    inputPlaceholder: e === "dark" ? 0.5 : 0.42,
    inputUnderline: e === "dark" ? 0.7 : 0.42,
    switchTrackDisabled: e === "dark" ? 0.2 : 0.12,
    switchTrack: e === "dark" ? 0.3 : 0.38
  };
}
function Co(e) {
  return e === "dark" ? ac : [];
}
function sc(e) {
  const {
    palette: r = {
      mode: "light"
    },
    // need to cast to avoid module augmentation test
    opacity: t,
    overlays: n,
    ...o
  } = e, i = zt(r);
  return {
    palette: i,
    opacity: {
      ...Eo(i.mode),
      ...t
    },
    overlays: n || Co(i.mode),
    ...o
  };
}
function cc(e) {
  var r;
  return !!e[0].match(/(cssVarPrefix|colorSchemeSelector|modularCssLayers|rootSelector|typography|mixins|breakpoints|direction|transitions)/) || !!e[0].match(/sxConfig$/) || // ends with sxConfig
  e[0] === "palette" && !!((r = e[1]) != null && r.match(/(mode|contrastThreshold|tonalOffset)/));
}
const lc = (e) => [...[...Array(25)].map((r, t) => `--${e ? `${e}-` : ""}overlays-${t}`), `--${e ? `${e}-` : ""}palette-AppBar-darkBg`, `--${e ? `${e}-` : ""}palette-AppBar-darkColor`], uc = (e) => (r, t) => {
  const n = e.rootSelector || ":root", o = e.colorSchemeSelector;
  let i = o;
  if (o === "class" && (i = ".%s"), o === "data" && (i = "[data-%s]"), o != null && o.startsWith("data-") && !o.includes("%s") && (i = `[${o}="%s"]`), e.defaultColorScheme === r) {
    if (r === "dark") {
      const c = {};
      return lc(e.cssVarPrefix).forEach((l) => {
        c[l] = t[l], delete t[l];
      }), i === "media" ? {
        [n]: t,
        "@media (prefers-color-scheme: dark)": {
          [n]: c
        }
      } : i ? {
        [i.replace("%s", r)]: c,
        [`${n}, ${i.replace("%s", r)}`]: t
      } : {
        [n]: {
          ...t,
          ...c
        }
      };
    }
    if (i && i !== "media")
      return `${n}, ${i.replace("%s", String(r))}`;
  } else if (r) {
    if (i === "media")
      return {
        [`@media (prefers-color-scheme: ${String(r)})`]: {
          [n]: t
        }
      };
    if (i)
      return i.replace("%s", String(r));
  }
  return n;
};
function fc(e, r) {
  r.forEach((t) => {
    e[t] || (e[t] = {});
  });
}
function g(e, r, t) {
  !e[r] && t && (e[r] = t);
}
function gr(e) {
  return typeof e != "string" || !e.startsWith("hsl") ? e : bo(e);
}
function Le(e, r) {
  `${r}Channel` in e || (e[`${r}Channel`] = mr(gr(e[r]), `MUI: Can't create \`palette.${r}Channel\` because \`palette.${r}\` is not one of these formats: #nnn, #nnnnnn, rgb(), rgba(), hsl(), hsla(), color().
To suppress this warning, you need to explicitly provide the \`palette.${r}Channel\` as a string (in rgb format, for example "12 12 12") or undefined if you want to remove the channel token.`));
}
function dc(e) {
  return typeof e == "number" ? `${e}px` : typeof e == "string" || typeof e == "function" || Array.isArray(e) ? e : "8px";
}
const Me = (e) => {
  try {
    return e();
  } catch {
  }
}, pc = (e = "mui") => ks(e);
function bt(e, r, t, n) {
  if (!r)
    return;
  r = r === !0 ? {} : r;
  const o = n === "dark" ? "dark" : "light";
  if (!t) {
    e[n] = sc({
      ...r,
      palette: {
        mode: o,
        ...r == null ? void 0 : r.palette
      }
    });
    return;
  }
  const {
    palette: i,
    ...c
  } = Pt({
    ...t,
    palette: {
      mode: o,
      ...r == null ? void 0 : r.palette
    }
  });
  return e[n] = {
    ...r,
    palette: i,
    opacity: {
      ...Eo(o),
      ...r == null ? void 0 : r.opacity
    },
    overlays: (r == null ? void 0 : r.overlays) || Co(o)
  }, c;
}
function hc(e = {}, ...r) {
  const {
    colorSchemes: t = {
      light: !0
    },
    defaultColorScheme: n,
    disableCssColorScheme: o = !1,
    cssVarPrefix: i = "mui",
    shouldSkipGeneratingVar: c = cc,
    colorSchemeSelector: l = t.light && t.dark ? "media" : void 0,
    rootSelector: f = ":root",
    ...d
  } = e, p = Object.keys(t)[0], h = n || (t.light && p !== "light" ? "light" : p), m = pc(i), {
    [h]: C,
    light: y,
    dark: s,
    ...v
  } = t, w = {
    ...v
  };
  let P = C;
  if ((h === "dark" && !("dark" in t) || h === "light" && !("light" in t)) && (P = !0), !P)
    throw new Error(process.env.NODE_ENV !== "production" ? `MUI: The \`colorSchemes.${h}\` option is either missing or invalid.` : ze(21, h));
  const x = bt(w, P, d, h);
  y && !w.light && bt(w, y, void 0, "light"), s && !w.dark && bt(w, s, void 0, "dark");
  let _ = {
    defaultColorScheme: h,
    ...x,
    cssVarPrefix: i,
    colorSchemeSelector: l,
    rootSelector: f,
    getCssVar: m,
    colorSchemes: w,
    font: {
      ...Ys(x.typography),
      ...x.font
    },
    spacing: dc(d.spacing)
  };
  Object.keys(_.colorSchemes).forEach((ee) => {
    const a = _.colorSchemes[ee].palette, $ = (B) => {
      const M = B.split("-"), ne = M[1], se = M[2];
      return m(B, a[ne][se]);
    };
    if (a.mode === "light" && (g(a.common, "background", "#fff"), g(a.common, "onBackground", "#000")), a.mode === "dark" && (g(a.common, "background", "#000"), g(a.common, "onBackground", "#fff")), fc(a, ["Alert", "AppBar", "Avatar", "Button", "Chip", "FilledInput", "LinearProgress", "Skeleton", "Slider", "SnackbarContent", "SpeedDialAction", "StepConnector", "StepContent", "Switch", "TableCell", "Tooltip"]), a.mode === "light") {
      g(a.Alert, "errorColor", ie(a.error.light, 0.6)), g(a.Alert, "infoColor", ie(a.info.light, 0.6)), g(a.Alert, "successColor", ie(a.success.light, 0.6)), g(a.Alert, "warningColor", ie(a.warning.light, 0.6)), g(a.Alert, "errorFilledBg", $("palette-error-main")), g(a.Alert, "infoFilledBg", $("palette-info-main")), g(a.Alert, "successFilledBg", $("palette-success-main")), g(a.Alert, "warningFilledBg", $("palette-warning-main")), g(a.Alert, "errorFilledColor", Me(() => a.getContrastText(a.error.main))), g(a.Alert, "infoFilledColor", Me(() => a.getContrastText(a.info.main))), g(a.Alert, "successFilledColor", Me(() => a.getContrastText(a.success.main))), g(a.Alert, "warningFilledColor", Me(() => a.getContrastText(a.warning.main))), g(a.Alert, "errorStandardBg", ae(a.error.light, 0.9)), g(a.Alert, "infoStandardBg", ae(a.info.light, 0.9)), g(a.Alert, "successStandardBg", ae(a.success.light, 0.9)), g(a.Alert, "warningStandardBg", ae(a.warning.light, 0.9)), g(a.Alert, "errorIconColor", $("palette-error-main")), g(a.Alert, "infoIconColor", $("palette-info-main")), g(a.Alert, "successIconColor", $("palette-success-main")), g(a.Alert, "warningIconColor", $("palette-warning-main")), g(a.AppBar, "defaultBg", $("palette-grey-100")), g(a.Avatar, "defaultBg", $("palette-grey-400")), g(a.Button, "inheritContainedBg", $("palette-grey-300")), g(a.Button, "inheritContainedHoverBg", $("palette-grey-A100")), g(a.Chip, "defaultBorder", $("palette-grey-400")), g(a.Chip, "defaultAvatarColor", $("palette-grey-700")), g(a.Chip, "defaultIconColor", $("palette-grey-700")), g(a.FilledInput, "bg", "rgba(0, 0, 0, 0.06)"), g(a.FilledInput, "hoverBg", "rgba(0, 0, 0, 0.09)"), g(a.FilledInput, "disabledBg", "rgba(0, 0, 0, 0.12)"), g(a.LinearProgress, "primaryBg", ae(a.primary.main, 0.62)), g(a.LinearProgress, "secondaryBg", ae(a.secondary.main, 0.62)), g(a.LinearProgress, "errorBg", ae(a.error.main, 0.62)), g(a.LinearProgress, "infoBg", ae(a.info.main, 0.62)), g(a.LinearProgress, "successBg", ae(a.success.main, 0.62)), g(a.LinearProgress, "warningBg", ae(a.warning.main, 0.62)), g(a.Skeleton, "bg", `rgba(${$("palette-text-primaryChannel")} / 0.11)`), g(a.Slider, "primaryTrack", ae(a.primary.main, 0.62)), g(a.Slider, "secondaryTrack", ae(a.secondary.main, 0.62)), g(a.Slider, "errorTrack", ae(a.error.main, 0.62)), g(a.Slider, "infoTrack", ae(a.info.main, 0.62)), g(a.Slider, "successTrack", ae(a.success.main, 0.62)), g(a.Slider, "warningTrack", ae(a.warning.main, 0.62));
      const B = Pr(a.background.default, 0.8);
      g(a.SnackbarContent, "bg", B), g(a.SnackbarContent, "color", Me(() => a.getContrastText(B))), g(a.SpeedDialAction, "fabHoverBg", Pr(a.background.paper, 0.15)), g(a.StepConnector, "border", $("palette-grey-400")), g(a.StepContent, "border", $("palette-grey-400")), g(a.Switch, "defaultColor", $("palette-common-white")), g(a.Switch, "defaultDisabledColor", $("palette-grey-100")), g(a.Switch, "primaryDisabledColor", ae(a.primary.main, 0.62)), g(a.Switch, "secondaryDisabledColor", ae(a.secondary.main, 0.62)), g(a.Switch, "errorDisabledColor", ae(a.error.main, 0.62)), g(a.Switch, "infoDisabledColor", ae(a.info.main, 0.62)), g(a.Switch, "successDisabledColor", ae(a.success.main, 0.62)), g(a.Switch, "warningDisabledColor", ae(a.warning.main, 0.62)), g(a.TableCell, "border", ae(Ar(a.divider, 1), 0.88)), g(a.Tooltip, "bg", Ar(a.grey[700], 0.92));
    }
    if (a.mode === "dark") {
      g(a.Alert, "errorColor", ae(a.error.light, 0.6)), g(a.Alert, "infoColor", ae(a.info.light, 0.6)), g(a.Alert, "successColor", ae(a.success.light, 0.6)), g(a.Alert, "warningColor", ae(a.warning.light, 0.6)), g(a.Alert, "errorFilledBg", $("palette-error-dark")), g(a.Alert, "infoFilledBg", $("palette-info-dark")), g(a.Alert, "successFilledBg", $("palette-success-dark")), g(a.Alert, "warningFilledBg", $("palette-warning-dark")), g(a.Alert, "errorFilledColor", Me(() => a.getContrastText(a.error.dark))), g(a.Alert, "infoFilledColor", Me(() => a.getContrastText(a.info.dark))), g(a.Alert, "successFilledColor", Me(() => a.getContrastText(a.success.dark))), g(a.Alert, "warningFilledColor", Me(() => a.getContrastText(a.warning.dark))), g(a.Alert, "errorStandardBg", ie(a.error.light, 0.9)), g(a.Alert, "infoStandardBg", ie(a.info.light, 0.9)), g(a.Alert, "successStandardBg", ie(a.success.light, 0.9)), g(a.Alert, "warningStandardBg", ie(a.warning.light, 0.9)), g(a.Alert, "errorIconColor", $("palette-error-main")), g(a.Alert, "infoIconColor", $("palette-info-main")), g(a.Alert, "successIconColor", $("palette-success-main")), g(a.Alert, "warningIconColor", $("palette-warning-main")), g(a.AppBar, "defaultBg", $("palette-grey-900")), g(a.AppBar, "darkBg", $("palette-background-paper")), g(a.AppBar, "darkColor", $("palette-text-primary")), g(a.Avatar, "defaultBg", $("palette-grey-600")), g(a.Button, "inheritContainedBg", $("palette-grey-800")), g(a.Button, "inheritContainedHoverBg", $("palette-grey-700")), g(a.Chip, "defaultBorder", $("palette-grey-700")), g(a.Chip, "defaultAvatarColor", $("palette-grey-300")), g(a.Chip, "defaultIconColor", $("palette-grey-300")), g(a.FilledInput, "bg", "rgba(255, 255, 255, 0.09)"), g(a.FilledInput, "hoverBg", "rgba(255, 255, 255, 0.13)"), g(a.FilledInput, "disabledBg", "rgba(255, 255, 255, 0.12)"), g(a.LinearProgress, "primaryBg", ie(a.primary.main, 0.5)), g(a.LinearProgress, "secondaryBg", ie(a.secondary.main, 0.5)), g(a.LinearProgress, "errorBg", ie(a.error.main, 0.5)), g(a.LinearProgress, "infoBg", ie(a.info.main, 0.5)), g(a.LinearProgress, "successBg", ie(a.success.main, 0.5)), g(a.LinearProgress, "warningBg", ie(a.warning.main, 0.5)), g(a.Skeleton, "bg", `rgba(${$("palette-text-primaryChannel")} / 0.13)`), g(a.Slider, "primaryTrack", ie(a.primary.main, 0.5)), g(a.Slider, "secondaryTrack", ie(a.secondary.main, 0.5)), g(a.Slider, "errorTrack", ie(a.error.main, 0.5)), g(a.Slider, "infoTrack", ie(a.info.main, 0.5)), g(a.Slider, "successTrack", ie(a.success.main, 0.5)), g(a.Slider, "warningTrack", ie(a.warning.main, 0.5));
      const B = Pr(a.background.default, 0.98);
      g(a.SnackbarContent, "bg", B), g(a.SnackbarContent, "color", Me(() => a.getContrastText(B))), g(a.SpeedDialAction, "fabHoverBg", Pr(a.background.paper, 0.15)), g(a.StepConnector, "border", $("palette-grey-600")), g(a.StepContent, "border", $("palette-grey-600")), g(a.Switch, "defaultColor", $("palette-grey-300")), g(a.Switch, "defaultDisabledColor", $("palette-grey-600")), g(a.Switch, "primaryDisabledColor", ie(a.primary.main, 0.55)), g(a.Switch, "secondaryDisabledColor", ie(a.secondary.main, 0.55)), g(a.Switch, "errorDisabledColor", ie(a.error.main, 0.55)), g(a.Switch, "infoDisabledColor", ie(a.info.main, 0.55)), g(a.Switch, "successDisabledColor", ie(a.success.main, 0.55)), g(a.Switch, "warningDisabledColor", ie(a.warning.main, 0.55)), g(a.TableCell, "border", ie(Ar(a.divider, 1), 0.68)), g(a.Tooltip, "bg", Ar(a.grey[700], 0.92));
    }
    Le(a.background, "default"), Le(a.background, "paper"), Le(a.common, "background"), Le(a.common, "onBackground"), Le(a, "divider"), Object.keys(a).forEach((B) => {
      const M = a[B];
      B !== "tonalOffset" && M && typeof M == "object" && (M.main && g(a[B], "mainChannel", mr(gr(M.main))), M.light && g(a[B], "lightChannel", mr(gr(M.light))), M.dark && g(a[B], "darkChannel", mr(gr(M.dark))), M.contrastText && g(a[B], "contrastTextChannel", mr(gr(M.contrastText))), B === "text" && (Le(a[B], "primary"), Le(a[B], "secondary")), B === "action" && (M.active && Le(a[B], "active"), M.selected && Le(a[B], "selected")));
    });
  }), _ = r.reduce((ee, a) => Oe(ee, a), _);
  const b = {
    prefix: i,
    disableCssColorScheme: o,
    shouldSkipGeneratingVar: c,
    getSelector: uc(_)
  }, {
    vars: I,
    generateThemeVars: k,
    generateStyleSheets: ue
  } = Ms(_, b);
  return _.vars = I, Object.entries(_.colorSchemes[_.defaultColorScheme]).forEach(([ee, a]) => {
    _[ee] = a;
  }), _.generateThemeVars = k, _.generateStyleSheets = ue, _.generateSpacing = function() {
    return po(d.spacing, Mt(this));
  }, _.getColorSchemeSelector = Ns(l), _.spacing = _.generateSpacing(), _.shouldSkipGeneratingVar = c, _.unstable_sxConfig = {
    ...Zr,
    ...d == null ? void 0 : d.unstable_sxConfig
  }, _.unstable_sx = function(a) {
    return sr({
      sx: a,
      theme: this
    });
  }, _.toRuntimeSource = So, _;
}
function Un(e, r, t) {
  e.colorSchemes && t && (e.colorSchemes[r] = {
    ...t !== !0 && t,
    palette: zt({
      ...t === !0 ? {} : t.palette,
      mode: r
    })
    // cast type to skip module augmentation test
  });
}
function mc(e = {}, ...r) {
  const {
    palette: t,
    cssVariables: n = !1,
    colorSchemes: o = t ? void 0 : {
      light: !0
    },
    defaultColorScheme: i = t == null ? void 0 : t.mode,
    ...c
  } = e, l = i || "light", f = o == null ? void 0 : o[l], d = {
    ...o,
    ...t ? {
      [l]: {
        ...typeof f != "boolean" && f,
        palette: t
      }
    } : void 0
  };
  if (n === !1) {
    if (!("colorSchemes" in e))
      return Pt(e, ...r);
    let p = t;
    "palette" in e || d[l] && (d[l] !== !0 ? p = d[l].palette : l === "dark" && (p = {
      mode: "dark"
    }));
    const h = Pt({
      ...e,
      palette: p
    }, ...r);
    return h.defaultColorScheme = l, h.colorSchemes = d, h.palette.mode === "light" && (h.colorSchemes.light = {
      ...d.light !== !0 && d.light,
      palette: h.palette
    }, Un(h, "dark", d.dark)), h.palette.mode === "dark" && (h.colorSchemes.dark = {
      ...d.dark !== !0 && d.dark,
      palette: h.palette
    }, Un(h, "light", d.light)), h;
  }
  return !t && !("light" in d) && l === "light" && (d.light = !0), hc({
    ...c,
    colorSchemes: d,
    defaultColorScheme: l,
    ...typeof n != "boolean" && n
  }, ...r);
}
const gc = mc(), yc = "$$material";
function bc(e) {
  return e !== "ownerState" && e !== "theme" && e !== "sx" && e !== "as";
}
const vc = (e) => bc(e) && e !== "classes", xc = vs({
  themeId: yc,
  defaultTheme: gc,
  rootShouldForwardProp: vc
}), Sc = Ps;
process.env.NODE_ENV !== "production" && (H.node, H.object.isRequired);
function Ec(e) {
  return As(e);
}
function Cc(e) {
  return jt("MuiSvgIcon", e);
}
Si("MuiSvgIcon", ["root", "colorPrimary", "colorSecondary", "colorAction", "colorError", "colorDisabled", "fontSizeInherit", "fontSizeSmall", "fontSizeMedium", "fontSizeLarge"]);
const Tc = (e) => {
  const {
    color: r,
    fontSize: t,
    classes: n
  } = e, o = {
    root: ["root", r !== "inherit" && `color${Xe(r)}`, `fontSize${Xe(t)}`]
  };
  return yi(o, Cc, n);
}, wc = xc("svg", {
  name: "MuiSvgIcon",
  slot: "Root",
  overridesResolver: (e, r) => {
    const {
      ownerState: t
    } = e;
    return [r.root, t.color !== "inherit" && r[`color${Xe(t.color)}`], r[`fontSize${Xe(t.fontSize)}`]];
  }
})(Sc(({
  theme: e
}) => {
  var r, t, n, o, i, c, l, f, d, p, h, m, C, y;
  return {
    userSelect: "none",
    width: "1em",
    height: "1em",
    display: "inline-block",
    flexShrink: 0,
    transition: (o = (r = e.transitions) == null ? void 0 : r.create) == null ? void 0 : o.call(r, "fill", {
      duration: (n = (t = (e.vars ?? e).transitions) == null ? void 0 : t.duration) == null ? void 0 : n.shorter
    }),
    variants: [
      {
        props: (s) => !s.hasSvgAsChild,
        style: {
          // the <svg> will define the property that has `currentColor`
          // for example heroicons uses fill="none" and stroke="currentColor"
          fill: "currentColor"
        }
      },
      {
        props: {
          fontSize: "inherit"
        },
        style: {
          fontSize: "inherit"
        }
      },
      {
        props: {
          fontSize: "small"
        },
        style: {
          fontSize: ((c = (i = e.typography) == null ? void 0 : i.pxToRem) == null ? void 0 : c.call(i, 20)) || "1.25rem"
        }
      },
      {
        props: {
          fontSize: "medium"
        },
        style: {
          fontSize: ((f = (l = e.typography) == null ? void 0 : l.pxToRem) == null ? void 0 : f.call(l, 24)) || "1.5rem"
        }
      },
      {
        props: {
          fontSize: "large"
        },
        style: {
          fontSize: ((p = (d = e.typography) == null ? void 0 : d.pxToRem) == null ? void 0 : p.call(d, 35)) || "2.1875rem"
        }
      },
      // TODO v5 deprecate color prop, v6 remove for sx
      ...Object.entries((e.vars ?? e).palette).filter(([, s]) => s && s.main).map(([s]) => {
        var v, w;
        return {
          props: {
            color: s
          },
          style: {
            color: (w = (v = (e.vars ?? e).palette) == null ? void 0 : v[s]) == null ? void 0 : w.main
          }
        };
      }),
      {
        props: {
          color: "action"
        },
        style: {
          color: (m = (h = (e.vars ?? e).palette) == null ? void 0 : h.action) == null ? void 0 : m.active
        }
      },
      {
        props: {
          color: "disabled"
        },
        style: {
          color: (y = (C = (e.vars ?? e).palette) == null ? void 0 : C.action) == null ? void 0 : y.disabled
        }
      },
      {
        props: {
          color: "inherit"
        },
        style: {
          color: void 0
        }
      }
    ]
  };
})), Vr = /* @__PURE__ */ xe.forwardRef(function(r, t) {
  const n = Ec({
    props: r,
    name: "MuiSvgIcon"
  }), {
    children: o,
    className: i,
    color: c = "inherit",
    component: l = "svg",
    fontSize: f = "medium",
    htmlColor: d,
    inheritViewBox: p = !1,
    titleAccess: h,
    viewBox: m = "0 0 24 24",
    ...C
  } = n, y = /* @__PURE__ */ xe.isValidElement(o) && o.type === "svg", s = {
    ...n,
    color: c,
    component: l,
    fontSize: f,
    instanceFontSize: r.fontSize,
    inheritViewBox: p,
    viewBox: m,
    hasSvgAsChild: y
  }, v = {};
  p || (v.viewBox = m);
  const w = Tc(s);
  return /* @__PURE__ */ T.jsxs(wc, {
    as: l,
    className: Ci(w.root, i),
    focusable: "false",
    color: d,
    "aria-hidden": h ? void 0 : !0,
    role: h ? "img" : void 0,
    ref: t,
    ...v,
    ...C,
    ...y && o.props,
    ownerState: s,
    children: [y ? o.props.children : o, h ? /* @__PURE__ */ T.jsx("title", {
      children: h
    }) : null]
  });
});
process.env.NODE_ENV !== "production" && (Vr.propTypes = {
  // ┌────────────────────────────── Warning ──────────────────────────────┐
  // │ These PropTypes are generated from the TypeScript type definitions. │
  // │    To update them, edit the d.ts file and run `pnpm proptypes`.     │
  // └─────────────────────────────────────────────────────────────────────┘
  /**
   * Node passed into the SVG element.
   */
  children: H.node,
  /**
   * Override or extend the styles applied to the component.
   */
  classes: H.object,
  /**
   * @ignore
   */
  className: H.string,
  /**
   * The color of the component.
   * It supports both default and custom theme colors, which can be added as shown in the
   * [palette customization guide](https://mui.com/material-ui/customization/palette/#custom-colors).
   * You can use the `htmlColor` prop to apply a color attribute to the SVG element.
   * @default 'inherit'
   */
  color: H.oneOfType([H.oneOf(["inherit", "action", "disabled", "primary", "secondary", "error", "info", "success", "warning"]), H.string]),
  /**
   * The component used for the root node.
   * Either a string to use a HTML element or a component.
   */
  component: H.elementType,
  /**
   * The fontSize applied to the icon. Defaults to 24px, but can be configure to inherit font size.
   * @default 'medium'
   */
  fontSize: H.oneOfType([H.oneOf(["inherit", "large", "medium", "small"]), H.string]),
  /**
   * Applies a color attribute to the SVG element.
   */
  htmlColor: H.string,
  /**
   * If `true`, the root node will inherit the custom `component`'s viewBox and the `viewBox`
   * prop will be ignored.
   * Useful when you want to reference a custom `component` and have `SvgIcon` pass that
   * `component`'s viewBox to the root node.
   * @default false
   */
  inheritViewBox: H.bool,
  /**
   * The shape-rendering attribute. The behavior of the different options is described on the
   * [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-rendering).
   * If you are having issues with blurry icons you should investigate this prop.
   */
  shapeRendering: H.string,
  /**
   * The system prop that allows defining system overrides as well as additional CSS styles.
   */
  sx: H.oneOfType([H.arrayOf(H.oneOfType([H.func, H.object, H.bool])), H.func, H.object]),
  /**
   * Provides a human-readable title for the element that contains it.
   * https://www.w3.org/TR/SVG-access/#Equivalent
   */
  titleAccess: H.string,
  /**
   * Allows you to redefine what the coordinates without units mean inside an SVG element.
   * For example, if the SVG element is 500 (width) by 200 (height),
   * and you pass viewBox="0 0 50 20",
   * this means that the coordinates inside the SVG will go from the top left corner (0,0)
   * to bottom right (50,20) and each unit will be worth 10px.
   * @default '0 0 24 24'
   */
  viewBox: H.string
});
Vr.muiName = "SvgIcon";
function _r(e, r) {
  function t(n, o) {
    return /* @__PURE__ */ T.jsx(Vr, {
      "data-testid": `${r}Icon`,
      ref: o,
      ...n,
      children: e
    });
  }
  return process.env.NODE_ENV !== "production" && (t.displayName = `${r}Icon`), t.muiName = Vr.muiName, /* @__PURE__ */ xe.memo(/* @__PURE__ */ xe.forwardRef(t));
}
const _c = _r(/* @__PURE__ */ T.jsx("path", {
  d: "M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4z"
}), "Refresh"), Rc = _r(/* @__PURE__ */ T.jsx("path", {
  d: "M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7m0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5"
}), "LocationOn"), Yn = _r(/* @__PURE__ */ T.jsx("path", {
  d: "M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16m11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5M5 11l1.5-4.5h11L19 11z"
}), "DirectionsCar"), Oc = _r(/* @__PURE__ */ T.jsx("path", {
  d: "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4m0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4"
}), "Person"), vt = _r(/* @__PURE__ */ T.jsx("path", {
  d: "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5m13.5-9 1.96 2.5H17V9.5zm-1.5 9c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5"
}), "LocalShipping"), $c = {
  active: { bg: "#d1fae5", text: "#065f46", icon: "success" },
  idle: { bg: "#fef3c7", text: "#92400e", icon: "warning" },
  offline: { bg: "#f3f4f6", text: "#374151", icon: "default" },
  error: { bg: "#fee2e2", text: "#991b1b", icon: "error" }
}, To = {
  car: /* @__PURE__ */ T.jsx(Yn, {}),
  truck: /* @__PURE__ */ T.jsx(vt, {}),
  ambulance: /* @__PURE__ */ T.jsx(vt, {}),
  police: /* @__PURE__ */ T.jsx(Yn, {}),
  mechanic: /* @__PURE__ */ T.jsx(Oc, {}),
  tow: /* @__PURE__ */ T.jsx(vt, {})
};
function Ac({
  vehicles: e,
  onRefresh: r,
  onSelectVehicle: t,
  selectedVehicleId: n,
  autoRefresh: o = !0,
  refreshInterval: i = 3e4,
  showMap: c = !0,
  height: l = 600,
  className: f
}) {
  const d = qn(), [p, h] = on(!1), [m, C] = on(/* @__PURE__ */ new Date());
  Ko(() => {
    if (!o || !r) return;
    const x = setInterval(() => {
      y();
    }, i);
    return () => clearInterval(x);
  }, [o, i, r]);
  const y = Xo(async () => {
    h(!0);
    try {
      await (r == null ? void 0 : r()), C(/* @__PURE__ */ new Date());
    } finally {
      h(!1);
    }
  }, [r]), s = (x) => $c[x], v = [...e].sort((x, _) => {
    const b = { error: 0, active: 1, idle: 2, offline: 3 };
    return b[x.status] - b[_.status];
  }), w = v.filter((x) => x.status === "active"), P = v.filter((x) => x.status !== "active");
  return /* @__PURE__ */ T.jsxs(le, { className: f, sx: { height: l }, children: [
    /* @__PURE__ */ T.jsxs(
      le,
      {
        sx: {
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 2
        },
        children: [
          /* @__PURE__ */ T.jsxs(le, { children: [
            /* @__PURE__ */ T.jsx(be, { variant: "h6", gutterBottom: !0, children: "Real-Time Tracker" }),
            /* @__PURE__ */ T.jsxs(be, { variant: "caption", color: "text.secondary", children: [
              "Last updated: ",
              m.toLocaleTimeString(),
              ` • ${w.length} active, ${P.length} inactive`
            ] })
          ] }),
          /* @__PURE__ */ T.jsx(Jo, { onClick: y, disabled: p, children: /* @__PURE__ */ T.jsx(
            St.div,
            {
              animate: p ? { rotate: 360 } : { rotate: 0 },
              transition: { duration: 1, repeat: p ? 1 / 0 : 0, ease: "linear" },
              children: /* @__PURE__ */ T.jsx(_c, {})
            }
          ) })
        ]
      }
    ),
    /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", flexDirection: "column", gap: 2, height: "calc(100% - 60px)" }, children: [
      c && /* @__PURE__ */ T.jsx(
        Gn,
        {
          sx: {
            flex: 1,
            minHeight: 300,
            bgcolor: yr(d.palette.primary.main, 0.05),
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            borderRadius: 2,
            border: `2px dashed ${d.palette.divider}`
          },
          children: /* @__PURE__ */ T.jsxs(le, { sx: { textAlign: "center", color: "text.secondary" }, children: [
            /* @__PURE__ */ T.jsx(Rc, { sx: { fontSize: 48, mb: 1 } }),
            /* @__PURE__ */ T.jsx(be, { variant: "body2", children: "Interactive Map Component" }),
            /* @__PURE__ */ T.jsx(be, { variant: "caption", children: "Requires Map integration (Leaflet/Google Maps)" })
          ] })
        }
      ),
      /* @__PURE__ */ T.jsx(
        le,
        {
          sx: {
            maxHeight: c ? 250 : "100%",
            overflowY: "auto",
            display: "flex",
            flexDirection: "column",
            gap: 1.5
          },
          children: /* @__PURE__ */ T.jsx(ri, { children: v.map((x, _) => {
            const b = x.id === n, I = s(x.status), k = To[x.type];
            return /* @__PURE__ */ T.jsx(
              St.div,
              {
                initial: { opacity: 0, x: -20 },
                animate: { opacity: 1, x: 0 },
                exit: { opacity: 0, x: 20 },
                transition: { delay: _ * 0.05 },
                children: /* @__PURE__ */ T.jsx(
                  kt,
                  {
                    onClick: () => t == null ? void 0 : t(x),
                    sx: {
                      cursor: "pointer",
                      transition: "all 0.2s",
                      border: b ? `2px solid ${d.palette.primary.main}` : "1px solid transparent",
                      "&:hover": {
                        bgcolor: yr(d.palette.primary.main, 0.05),
                        transform: "translateX(4px)"
                      }
                    },
                    children: /* @__PURE__ */ T.jsx(zr, { sx: { py: 1.5, "&:last-child": { pb: 1.5 } }, children: /* @__PURE__ */ T.jsxs(
                      le,
                      {
                        sx: {
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "flex-start"
                        },
                        children: [
                          /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", gap: 1.5, flex: 1 }, children: [
                            /* @__PURE__ */ T.jsx(
                              le,
                              {
                                sx: {
                                  p: 1,
                                  borderRadius: 1,
                                  bgcolor: yr(d.palette.primary.main, 0.1),
                                  color: d.palette.primary.main
                                },
                                children: Wr.cloneElement(k, {
                                  fontSize: 20
                                })
                              }
                            ),
                            /* @__PURE__ */ T.jsxs(le, { sx: { flex: 1, minWidth: 0 }, children: [
                              /* @__PURE__ */ T.jsxs(
                                le,
                                {
                                  sx: {
                                    display: "flex",
                                    alignItems: "center",
                                    gap: 0.75,
                                    mb: 0.5
                                  },
                                  children: [
                                    /* @__PURE__ */ T.jsxs(
                                      be,
                                      {
                                        variant: "subtitle2",
                                        sx: { fontWeight: 600 },
                                        children: [
                                          x.type.toUpperCase(),
                                          " #",
                                          x.id
                                        ]
                                      }
                                    ),
                                    /* @__PURE__ */ T.jsx(
                                      xt,
                                      {
                                        label: x.status,
                                        size: "small",
                                        sx: {
                                          bgcolor: I.bg,
                                          color: I.text,
                                          fontSize: "11px",
                                          height: 22,
                                          fontWeight: 500
                                        }
                                      }
                                    )
                                  ]
                                }
                              ),
                              x.driver && /* @__PURE__ */ T.jsx(be, { variant: "caption", color: "text.secondary", children: x.driver.name }),
                              x.destination && /* @__PURE__ */ T.jsxs(be, { variant: "caption", color: "text.secondary", children: [
                                "→ ",
                                x.destination
                              ] }),
                              x.eta && /* @__PURE__ */ T.jsx(
                                xt,
                                {
                                  label: `ETA: ${x.eta}`,
                                  size: "small",
                                  variant: "outlined",
                                  sx: { fontSize: "11px", height: 22 }
                                }
                              )
                            ] })
                          ] }),
                          /* @__PURE__ */ T.jsxs(le, { sx: { textAlign: "right", minWidth: 80 }, children: [
                            x.speed && /* @__PURE__ */ T.jsxs(be, { variant: "caption", color: "text.secondary", children: [
                              x.speed,
                              " km/h"
                            ] }),
                            /* @__PURE__ */ T.jsx(be, { variant: "caption", color: "text.secondary", children: x.lastUpdate })
                          ] })
                        ]
                      }
                    ) })
                  }
                )
              },
              x.id
            );
          }) })
        }
      )
    ] })
  ] });
}
Ac.displayName = "RealTimeTracker";
function Pc({ vehicle: e, selected: r, onClick: t }) {
  const n = qn(), o = getStatusColor(e.status), i = To[e.type];
  return /* @__PURE__ */ T.jsx(
    St.div,
    {
      initial: { opacity: 0, y: 10 },
      animate: { opacity: 1, y: 0 },
      transition: { duration: 0.2 },
      children: /* @__PURE__ */ T.jsx(
        kt,
        {
          onClick: () => t == null ? void 0 : t(e),
          sx: {
            cursor: "pointer",
            border: r ? `2px solid ${n.palette.primary.main}` : "1px solid transparent",
            "&:hover": {
              bgcolor: yr(n.palette.primary.main, 0.05),
              boxShadow: n.shadows[2]
            }
          },
          children: /* @__PURE__ */ T.jsx(zr, { sx: { py: 1 }, children: /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", alignItems: "center", gap: 1.5 }, children: [
            /* @__PURE__ */ T.jsx(
              le,
              {
                sx: {
                  p: 0.75,
                  borderRadius: 1,
                  bgcolor: yr(n.palette.primary.main, 0.1),
                  color: n.palette.primary.main
                },
                children: Wr.cloneElement(i, {
                  fontSize: 18
                })
              }
            ),
            /* @__PURE__ */ T.jsx(le, { sx: { flex: 1 }, children: /* @__PURE__ */ T.jsxs(
              le,
              {
                sx: {
                  display: "flex",
                  alignItems: "center",
                  gap: 0.75
                },
                children: [
                  /* @__PURE__ */ T.jsxs(be, { variant: "body2", sx: { fontWeight: 600 }, children: [
                    e.type.toUpperCase(),
                    " #",
                    e.id
                  ] }),
                  /* @__PURE__ */ T.jsx(
                    xt,
                    {
                      label: e.status,
                      size: "small",
                      sx: {
                        bgcolor: o.bg,
                        color: o.text,
                        fontSize: "10px",
                        height: 20,
                        fontWeight: 500
                      }
                    }
                  )
                ]
              }
            ) })
          ] }) })
        }
      )
    }
  );
}
Pc.displayName = "VehicleCard";
const Fc = ({ title: e, metrics: r, ...t }) => {
  const n = (i) => i === void 0 ? "#9CA3AF" : i > 0 ? Dr.success : Dr.error, o = (i, c) => Math.min(100, Math.round(i / c * 100));
  return /* @__PURE__ */ T.jsx(kt, { ...t, children: /* @__PURE__ */ T.jsxs(zr, { children: [
    /* @__PURE__ */ T.jsx(be, { variant: "h6", gutterBottom: !0, sx: { fontWeight: 600, color: "#111827" }, children: e }),
    /* @__PURE__ */ T.jsx(Nr, { container: !0, spacing: 2, children: r.map((i, c) => /* @__PURE__ */ T.jsx(Nr, { item: !0, xs: 6, children: /* @__PURE__ */ T.jsxs(
      le,
      {
        sx: {
          border: "1px solid #E5E7EB",
          borderRadius: 2,
          p: 2,
          backgroundColor: "#F9FAFB"
        },
        children: [
          /* @__PURE__ */ T.jsxs(le, { sx: { display: "flex", justifyContent: "space-between", mb: 1 }, children: [
            /* @__PURE__ */ T.jsx(be, { variant: "body2", sx: { fontWeight: 500, color: "#374151" }, children: i.label }),
            /* @__PURE__ */ T.jsx(le, { sx: { textAlign: "right" }, children: /* @__PURE__ */ T.jsx(be, { variant: "h5", sx: { fontWeight: 600, color: "#111827" }, children: i.value }) })
          ] }),
          /* @__PURE__ */ T.jsx(
            Qo,
            {
              variant: "determinate",
              value: o(i.value, i.target || 100),
              sx: {
                height: 8,
                borderRadius: 4,
                backgroundColor: n(i.change)
              }
            }
          )
        ]
      }
    ) }, c)) })
  ] }) });
};
export {
  Dc as GlobalHQDashboard,
  Fc as KPICard,
  Ac as RealTimeTracker,
  Pc as VehicleCard
};
