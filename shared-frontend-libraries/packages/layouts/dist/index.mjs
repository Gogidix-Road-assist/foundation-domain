import ee, { useState as mr } from "react";
import { Box as F, AppBar as br, Toolbar as yr, IconButton as re, Typography as De, Drawer as Er, List as Rr, ListItem as we, ListItemButton as Se, ListItemIcon as Oe, ListItemText as ke, Collapse as _r, Link as jr } from "@mui/material";
import { Menu as Fr, ExpandLess as Tr, ExpandMore as Cr, Twitter as wr, Facebook as Sr, LinkedIn as Or, GitHub as kr } from "@mui/icons-material";
import { MANAGEMENT_COLORS as W } from "@shared-frontend-libraries/design-system";
var Q = { exports: {} }, $ = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Pe;
function Pr() {
  if (Pe) return $;
  Pe = 1;
  var x = ee, u = Symbol.for("react.element"), E = Symbol.for("react.fragment"), c = Object.prototype.hasOwnProperty, g = x.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, m = { key: !0, ref: !0, __self: !0, __source: !0 };
  function w(T, h, f) {
    var v, R = {}, S = null, M = null;
    f !== void 0 && (S = "" + f), h.key !== void 0 && (S = "" + h.key), h.ref !== void 0 && (M = h.ref);
    for (v in h) c.call(h, v) && !m.hasOwnProperty(v) && (R[v] = h[v]);
    if (T && T.defaultProps) for (v in h = T.defaultProps, h) R[v] === void 0 && (R[v] = h[v]);
    return { $$typeof: u, type: T, key: S, ref: M, props: R, _owner: g.current };
  }
  return $.Fragment = E, $.jsx = w, $.jsxs = w, $;
}
var Y = {};
/**
 * @license React
 * react-jsx-runtime.development.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Ae;
function Ar() {
  return Ae || (Ae = 1, process.env.NODE_ENV !== "production" && function() {
    var x = ee, u = Symbol.for("react.element"), E = Symbol.for("react.portal"), c = Symbol.for("react.fragment"), g = Symbol.for("react.strict_mode"), m = Symbol.for("react.profiler"), w = Symbol.for("react.provider"), T = Symbol.for("react.context"), h = Symbol.for("react.forward_ref"), f = Symbol.for("react.suspense"), v = Symbol.for("react.suspense_list"), R = Symbol.for("react.memo"), S = Symbol.for("react.lazy"), M = Symbol.for("react.offscreen"), te = Symbol.iterator, We = "@@iterator";
    function Ie(e) {
      if (e === null || typeof e != "object")
        return null;
      var r = te && e[te] || e[We];
      return typeof r == "function" ? r : null;
    }
    var P = x.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function b(e) {
      {
        for (var r = arguments.length, t = new Array(r > 1 ? r - 1 : 0), n = 1; n < r; n++)
          t[n - 1] = arguments[n];
        Le("error", e, t);
      }
    }
    function Le(e, r, t) {
      {
        var n = P.ReactDebugCurrentFrame, s = n.getStackAddendum();
        s !== "" && (r += "%s", t = t.concat([s]));
        var l = t.map(function(i) {
          return String(i);
        });
        l.unshift("Warning: " + r), Function.prototype.apply.call(console[e], console, l);
      }
    }
    var $e = !1, Ye = !1, Me = !1, Be = !1, Ve = !1, ne;
    ne = Symbol.for("react.module.reference");
    function Ue(e) {
      return !!(typeof e == "string" || typeof e == "function" || e === c || e === m || Ve || e === g || e === f || e === v || Be || e === M || $e || Ye || Me || typeof e == "object" && e !== null && (e.$$typeof === S || e.$$typeof === R || e.$$typeof === w || e.$$typeof === T || e.$$typeof === h || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      e.$$typeof === ne || e.getModuleId !== void 0));
    }
    function Ne(e, r, t) {
      var n = e.displayName;
      if (n)
        return n;
      var s = r.displayName || r.name || "";
      return s !== "" ? t + "(" + s + ")" : t;
    }
    function ae(e) {
      return e.displayName || "Context";
    }
    function C(e) {
      if (e == null)
        return null;
      if (typeof e.tag == "number" && b("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof e == "function")
        return e.displayName || e.name || null;
      if (typeof e == "string")
        return e;
      switch (e) {
        case c:
          return "Fragment";
        case E:
          return "Portal";
        case m:
          return "Profiler";
        case g:
          return "StrictMode";
        case f:
          return "Suspense";
        case v:
          return "SuspenseList";
      }
      if (typeof e == "object")
        switch (e.$$typeof) {
          case T:
            var r = e;
            return ae(r) + ".Consumer";
          case w:
            var t = e;
            return ae(t._context) + ".Provider";
          case h:
            return Ne(e, e.render, "ForwardRef");
          case R:
            var n = e.displayName || null;
            return n !== null ? n : C(e.type) || "Memo";
          case S: {
            var s = e, l = s._payload, i = s._init;
            try {
              return C(i(l));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var O = Object.assign, I = 0, oe, ie, se, ue, le, ce, fe;
    function de() {
    }
    de.__reactDisabledLog = !0;
    function He() {
      {
        if (I === 0) {
          oe = console.log, ie = console.info, se = console.warn, ue = console.error, le = console.group, ce = console.groupCollapsed, fe = console.groupEnd;
          var e = {
            configurable: !0,
            enumerable: !0,
            value: de,
            writable: !0
          };
          Object.defineProperties(console, {
            info: e,
            log: e,
            warn: e,
            error: e,
            group: e,
            groupCollapsed: e,
            groupEnd: e
          });
        }
        I++;
      }
    }
    function Ge() {
      {
        if (I--, I === 0) {
          var e = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: O({}, e, {
              value: oe
            }),
            info: O({}, e, {
              value: ie
            }),
            warn: O({}, e, {
              value: se
            }),
            error: O({}, e, {
              value: ue
            }),
            group: O({}, e, {
              value: le
            }),
            groupCollapsed: O({}, e, {
              value: ce
            }),
            groupEnd: O({}, e, {
              value: fe
            })
          });
        }
        I < 0 && b("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var H = P.ReactCurrentDispatcher, G;
    function B(e, r, t) {
      {
        if (G === void 0)
          try {
            throw Error();
          } catch (s) {
            var n = s.stack.trim().match(/\n( *(at )?)/);
            G = n && n[1] || "";
          }
        return `
` + G + e;
      }
    }
    var z = !1, V;
    {
      var ze = typeof WeakMap == "function" ? WeakMap : Map;
      V = new ze();
    }
    function ve(e, r) {
      if (!e || z)
        return "";
      {
        var t = V.get(e);
        if (t !== void 0)
          return t;
      }
      var n;
      z = !0;
      var s = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var l;
      l = H.current, H.current = null, He();
      try {
        if (r) {
          var i = function() {
            throw Error();
          };
          if (Object.defineProperty(i.prototype, "props", {
            set: function() {
              throw Error();
            }
          }), typeof Reflect == "object" && Reflect.construct) {
            try {
              Reflect.construct(i, []);
            } catch (_) {
              n = _;
            }
            Reflect.construct(e, [], i);
          } else {
            try {
              i.call();
            } catch (_) {
              n = _;
            }
            e.call(i.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (_) {
            n = _;
          }
          e();
        }
      } catch (_) {
        if (_ && n && typeof _.stack == "string") {
          for (var o = _.stack.split(`
`), y = n.stack.split(`
`), d = o.length - 1, p = y.length - 1; d >= 1 && p >= 0 && o[d] !== y[p]; )
            p--;
          for (; d >= 1 && p >= 0; d--, p--)
            if (o[d] !== y[p]) {
              if (d !== 1 || p !== 1)
                do
                  if (d--, p--, p < 0 || o[d] !== y[p]) {
                    var j = `
` + o[d].replace(" at new ", " at ");
                    return e.displayName && j.includes("<anonymous>") && (j = j.replace("<anonymous>", e.displayName)), typeof e == "function" && V.set(e, j), j;
                  }
                while (d >= 1 && p >= 0);
              break;
            }
        }
      } finally {
        z = !1, H.current = l, Ge(), Error.prepareStackTrace = s;
      }
      var D = e ? e.displayName || e.name : "", k = D ? B(D) : "";
      return typeof e == "function" && V.set(e, k), k;
    }
    function Je(e, r, t) {
      return ve(e, !1);
    }
    function qe(e) {
      var r = e.prototype;
      return !!(r && r.isReactComponent);
    }
    function U(e, r, t) {
      if (e == null)
        return "";
      if (typeof e == "function")
        return ve(e, qe(e));
      if (typeof e == "string")
        return B(e);
      switch (e) {
        case f:
          return B("Suspense");
        case v:
          return B("SuspenseList");
      }
      if (typeof e == "object")
        switch (e.$$typeof) {
          case h:
            return Je(e.render);
          case R:
            return U(e.type, r, t);
          case S: {
            var n = e, s = n._payload, l = n._init;
            try {
              return U(l(s), r, t);
            } catch {
            }
          }
        }
      return "";
    }
    var L = Object.prototype.hasOwnProperty, pe = {}, he = P.ReactDebugCurrentFrame;
    function N(e) {
      if (e) {
        var r = e._owner, t = U(e.type, e._source, r ? r.type : null);
        he.setExtraStackFrame(t);
      } else
        he.setExtraStackFrame(null);
    }
    function Ke(e, r, t, n, s) {
      {
        var l = Function.call.bind(L);
        for (var i in e)
          if (l(e, i)) {
            var o = void 0;
            try {
              if (typeof e[i] != "function") {
                var y = Error((n || "React class") + ": " + t + " type `" + i + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof e[i] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw y.name = "Invariant Violation", y;
              }
              o = e[i](r, i, n, t, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (d) {
              o = d;
            }
            o && !(o instanceof Error) && (N(s), b("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", n || "React class", t, i, typeof o), N(null)), o instanceof Error && !(o.message in pe) && (pe[o.message] = !0, N(s), b("Failed %s type: %s", t, o.message), N(null));
          }
      }
    }
    var Xe = Array.isArray;
    function J(e) {
      return Xe(e);
    }
    function Ze(e) {
      {
        var r = typeof Symbol == "function" && Symbol.toStringTag, t = r && e[Symbol.toStringTag] || e.constructor.name || "Object";
        return t;
      }
    }
    function Qe(e) {
      try {
        return ge(e), !1;
      } catch {
        return !0;
      }
    }
    function ge(e) {
      return "" + e;
    }
    function xe(e) {
      if (Qe(e))
        return b("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", Ze(e)), ge(e);
    }
    var me = P.ReactCurrentOwner, er = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, be, ye;
    function rr(e) {
      if (L.call(e, "ref")) {
        var r = Object.getOwnPropertyDescriptor(e, "ref").get;
        if (r && r.isReactWarning)
          return !1;
      }
      return e.ref !== void 0;
    }
    function tr(e) {
      if (L.call(e, "key")) {
        var r = Object.getOwnPropertyDescriptor(e, "key").get;
        if (r && r.isReactWarning)
          return !1;
      }
      return e.key !== void 0;
    }
    function nr(e, r) {
      typeof e.ref == "string" && me.current;
    }
    function ar(e, r) {
      {
        var t = function() {
          be || (be = !0, b("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", r));
        };
        t.isReactWarning = !0, Object.defineProperty(e, "key", {
          get: t,
          configurable: !0
        });
      }
    }
    function or(e, r) {
      {
        var t = function() {
          ye || (ye = !0, b("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", r));
        };
        t.isReactWarning = !0, Object.defineProperty(e, "ref", {
          get: t,
          configurable: !0
        });
      }
    }
    var ir = function(e, r, t, n, s, l, i) {
      var o = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: u,
        // Built-in properties that belong on the element
        type: e,
        key: r,
        ref: t,
        props: i,
        // Record the component responsible for creating this element.
        _owner: l
      };
      return o._store = {}, Object.defineProperty(o._store, "validated", {
        configurable: !1,
        enumerable: !1,
        writable: !0,
        value: !1
      }), Object.defineProperty(o, "_self", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: n
      }), Object.defineProperty(o, "_source", {
        configurable: !1,
        enumerable: !1,
        writable: !1,
        value: s
      }), Object.freeze && (Object.freeze(o.props), Object.freeze(o)), o;
    };
    function sr(e, r, t, n, s) {
      {
        var l, i = {}, o = null, y = null;
        t !== void 0 && (xe(t), o = "" + t), tr(r) && (xe(r.key), o = "" + r.key), rr(r) && (y = r.ref, nr(r, s));
        for (l in r)
          L.call(r, l) && !er.hasOwnProperty(l) && (i[l] = r[l]);
        if (e && e.defaultProps) {
          var d = e.defaultProps;
          for (l in d)
            i[l] === void 0 && (i[l] = d[l]);
        }
        if (o || y) {
          var p = typeof e == "function" ? e.displayName || e.name || "Unknown" : e;
          o && ar(i, p), y && or(i, p);
        }
        return ir(e, o, y, s, n, me.current, i);
      }
    }
    var q = P.ReactCurrentOwner, Ee = P.ReactDebugCurrentFrame;
    function A(e) {
      if (e) {
        var r = e._owner, t = U(e.type, e._source, r ? r.type : null);
        Ee.setExtraStackFrame(t);
      } else
        Ee.setExtraStackFrame(null);
    }
    var K;
    K = !1;
    function X(e) {
      return typeof e == "object" && e !== null && e.$$typeof === u;
    }
    function Re() {
      {
        if (q.current) {
          var e = C(q.current.type);
          if (e)
            return `

Check the render method of \`` + e + "`.";
        }
        return "";
      }
    }
    function ur(e) {
      return "";
    }
    var _e = {};
    function lr(e) {
      {
        var r = Re();
        if (!r) {
          var t = typeof e == "string" ? e : e.displayName || e.name;
          t && (r = `

Check the top-level render call using <` + t + ">.");
        }
        return r;
      }
    }
    function je(e, r) {
      {
        if (!e._store || e._store.validated || e.key != null)
          return;
        e._store.validated = !0;
        var t = lr(r);
        if (_e[t])
          return;
        _e[t] = !0;
        var n = "";
        e && e._owner && e._owner !== q.current && (n = " It was passed a child from " + C(e._owner.type) + "."), A(e), b('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', t, n), A(null);
      }
    }
    function Fe(e, r) {
      {
        if (typeof e != "object")
          return;
        if (J(e))
          for (var t = 0; t < e.length; t++) {
            var n = e[t];
            X(n) && je(n, r);
          }
        else if (X(e))
          e._store && (e._store.validated = !0);
        else if (e) {
          var s = Ie(e);
          if (typeof s == "function" && s !== e.entries)
            for (var l = s.call(e), i; !(i = l.next()).done; )
              X(i.value) && je(i.value, r);
        }
      }
    }
    function cr(e) {
      {
        var r = e.type;
        if (r == null || typeof r == "string")
          return;
        var t;
        if (typeof r == "function")
          t = r.propTypes;
        else if (typeof r == "object" && (r.$$typeof === h || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        r.$$typeof === R))
          t = r.propTypes;
        else
          return;
        if (t) {
          var n = C(r);
          Ke(t, e.props, "prop", n, e);
        } else if (r.PropTypes !== void 0 && !K) {
          K = !0;
          var s = C(r);
          b("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", s || "Unknown");
        }
        typeof r.getDefaultProps == "function" && !r.getDefaultProps.isReactClassApproved && b("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function fr(e) {
      {
        for (var r = Object.keys(e.props), t = 0; t < r.length; t++) {
          var n = r[t];
          if (n !== "children" && n !== "key") {
            A(e), b("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", n), A(null);
            break;
          }
        }
        e.ref !== null && (A(e), b("Invalid attribute `ref` supplied to `React.Fragment`."), A(null));
      }
    }
    var Te = {};
    function Ce(e, r, t, n, s, l) {
      {
        var i = Ue(e);
        if (!i) {
          var o = "";
          (e === void 0 || typeof e == "object" && e !== null && Object.keys(e).length === 0) && (o += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var y = ur();
          y ? o += y : o += Re();
          var d;
          e === null ? d = "null" : J(e) ? d = "array" : e !== void 0 && e.$$typeof === u ? (d = "<" + (C(e.type) || "Unknown") + " />", o = " Did you accidentally export a JSX literal instead of a component?") : d = typeof e, b("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", d, o);
        }
        var p = sr(e, r, t, s, l);
        if (p == null)
          return p;
        if (i) {
          var j = r.children;
          if (j !== void 0)
            if (n)
              if (J(j)) {
                for (var D = 0; D < j.length; D++)
                  Fe(j[D], e);
                Object.freeze && Object.freeze(j);
              } else
                b("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              Fe(j, e);
        }
        if (L.call(r, "key")) {
          var k = C(e), _ = Object.keys(r).filter(function(xr) {
            return xr !== "key";
          }), Z = _.length > 0 ? "{key: someKey, " + _.join(": ..., ") + ": ...}" : "{key: someKey}";
          if (!Te[k + Z]) {
            var gr = _.length > 0 ? "{" + _.join(": ..., ") + ": ...}" : "{}";
            b(`A props object containing a "key" prop is being spread into JSX:
  let props = %s;
  <%s {...props} />
React keys must be passed directly to JSX without using spread:
  let props = %s;
  <%s key={someKey} {...props} />`, Z, k, gr, k), Te[k + Z] = !0;
          }
        }
        return e === c ? fr(p) : cr(p), p;
      }
    }
    function dr(e, r, t) {
      return Ce(e, r, t, !0);
    }
    function vr(e, r, t) {
      return Ce(e, r, t, !1);
    }
    var pr = vr, hr = dr;
    Y.Fragment = c, Y.jsx = pr, Y.jsxs = hr;
  }()), Y;
}
process.env.NODE_ENV === "production" ? Q.exports = Pr() : Q.exports = Ar();
var a = Q.exports;
const Dr = {
  small: { maxWidth: "640px", padding: "16px" },
  medium: { maxWidth: "1024px", padding: "24px" },
  large: { maxWidth: "1280px", padding: "32px" },
  full: { maxWidth: "100%", padding: "24px" }
}, Mr = ({
  size: x = "medium",
  centered: u = !1,
  noPadding: E = !1,
  children: c,
  ...g
}) => {
  const m = Dr[x];
  return /* @__PURE__ */ a.jsx(
    F,
    {
      sx: {
        maxWidth: m.maxWidth,
        margin: u ? "0 auto" : void 0,
        padding: E ? void 0 : m.padding,
        backgroundColor: "#FFFFFF",
        minHeight: "100vh"
      },
      ...g,
      children: c
    }
  );
}, Br = ({
  type: x = "default",
  headerHeight: u = 64,
  footerHeight: E = 48,
  sidebarWidth: c = 250,
  children: g,
  ...m
}) => /* @__PURE__ */ a.jsx(
  F,
  {
    sx: {
      display: "flex",
      flexDirection: "column",
      minHeight: "100vh",
      backgroundColor: "#F9FAFB"
    },
    ...m,
    children: g
  }
), Vr = ({
  title: x,
  onMenuClick: u,
  logo: E,
  actions: c,
  elevation: g = 1,
  ...m
}) => /* @__PURE__ */ a.jsx(
  br,
  {
    position: "sticky",
    elevation: g,
    sx: {
      backgroundColor: "#FFFFFF",
      color: "#111827",
      borderBottom: "1px solid #E5E7EB"
    },
    ...m,
    children: /* @__PURE__ */ a.jsxs(yr, { sx: { minHeight: 64 }, children: [
      u && /* @__PURE__ */ a.jsx(
        re,
        {
          edge: "start",
          onClick: u,
          sx: { mr: 2, display: { sm: "none", xs: "flex" } },
          children: /* @__PURE__ */ a.jsx(Fr, {})
        }
      ),
      E && /* @__PURE__ */ a.jsx(F, { sx: { mr: 2 }, children: E }),
      x && /* @__PURE__ */ a.jsx(
        De,
        {
          variant: "h6",
          component: "div",
          sx: {
            flexGrow: 1,
            fontWeight: 600,
            color: W.primary
          },
          children: x
        }
      ),
      c && /* @__PURE__ */ a.jsx(F, { sx: { ml: "auto" }, children: c })
    ] })
  }
), Ur = ({
  items: x,
  collapsed: u = !1,
  onToggle: E,
  onItemClick: c,
  width: g = 250
}) => {
  const [m, w] = mr(/* @__PURE__ */ new Set()), T = (f) => {
    w((v) => {
      const R = new Set(v);
      return R.has(f) ? R.delete(f) : R.add(f), R;
    });
  }, h = (f) => {
    c == null || c(f);
  };
  return /* @__PURE__ */ a.jsx(
    Er,
    {
      variant: "permanent",
      open: !u,
      sx: {
        width: u ? 64 : g,
        "& .MuiDrawer-paper": {
          backgroundColor: "#FFFFFF",
          borderRight: "1px solid #E5E7EB",
          borderLeft: "none",
          borderLeft: u ? "none" : "1px solid #E5E7EB"
        }
      },
      children: /* @__PURE__ */ a.jsx(F, { sx: { overflow: "auto", py: 2 }, children: /* @__PURE__ */ a.jsx(Rr, { component: "nav", disablePadding: !0, children: x.map((f) => /* @__PURE__ */ a.jsxs(ee.Fragment, { children: [
        /* @__PURE__ */ a.jsx(
          we,
          {
            disablePadding: !0,
            sx: { display: "block" },
            onClick: () => {
              f.children ? T(f.id) : h(f);
            },
            children: /* @__PURE__ */ a.jsxs(
              Se,
              {
                sx: {
                  minHeight: 48,
                  justifyContent: u ? "center" : "flex-start",
                  px: u ? 0 : 2,
                  borderRadius: u ? 0 : 1,
                  mx: u ? 0 : 1,
                  "&.Mui-selected": {
                    backgroundColor: `${W.primary}15`,
                    "&:hover": {
                      backgroundColor: `${W.primary}25`
                    }
                  },
                  "&:hover": {
                    backgroundColor: "#F3F4F6"
                  }
                },
                children: [
                  /* @__PURE__ */ a.jsx(
                    Oe,
                    {
                      sx: {
                        minWidth: u ? void 0 : 48,
                        color: "#6B7280"
                      },
                      children: f.icon
                    }
                  ),
                  !u && /* @__PURE__ */ a.jsx(
                    ke,
                    {
                      primary: f.label,
                      sx: {
                        color: "#374151",
                        fontWeight: 500
                      }
                    }
                  ),
                  f.children && !u && /* @__PURE__ */ a.jsx(
                    re,
                    {
                      edge: "end",
                      sx: {
                        transform: m.has(f.id) ? "rotate(180deg)" : "rotate(0deg)",
                        transition: "transform 0.2s"
                      },
                      children: m.has(f.id) ? /* @__PURE__ */ a.jsx(Tr, {}) : /* @__PURE__ */ a.jsx(Cr, {})
                    }
                  )
                ]
              }
            )
          }
        ),
        f.children && (u ? null : /* @__PURE__ */ a.jsx(_r, { in: m.has(f.id), timeout: "auto", unmountOnExit: !0, children: /* @__PURE__ */ a.jsx(F, { sx: { pl: 4 }, children: f.children.map((v) => /* @__PURE__ */ a.jsx(we, { disablePadding: !0, sx: { display: "block" }, children: /* @__PURE__ */ a.jsxs(
          Se,
          {
            onClick: () => h(v),
            sx: {
              minHeight: 40,
              px: 2,
              borderRadius: 1,
              "&.Mui-selected": {
                backgroundColor: `${W.primary}15`
              },
              "&:hover": {
                backgroundColor: "#F3F4F6"
              }
            },
            children: [
              /* @__PURE__ */ a.jsx(Oe, { sx: { minWidth: 36 }, children: v.icon }),
              /* @__PURE__ */ a.jsx(
                ke,
                {
                  primary: v.label,
                  sx: { color: "#374151", fontSize: "0.875rem" }
                }
              )
            ]
          }
        ) }, v.id)) }) }))
      ] }, f.id)) }) })
    }
  );
}, Wr = [
  { label: "Twitter", href: "https://twitter.com", icon: /* @__PURE__ */ a.jsx(wr, {}) },
  { label: "Facebook", href: "https://facebook.com", icon: /* @__PURE__ */ a.jsx(Sr, {}) },
  { label: "LinkedIn", href: "https://linkedin.com", icon: /* @__PURE__ */ a.jsx(Or, {}) },
  { label: "GitHub", href: "https://github.com", icon: /* @__PURE__ */ a.jsx(kr, {}) }
], Nr = ({
  links: x = [],
  copyright: u = `© ${(/* @__PURE__ */ new Date()).getFullYear()} RapidAssist. All rights reserved.`,
  socialLinks: E = Wr
}) => /* @__PURE__ */ a.jsxs(
  F,
  {
    component: "footer",
    sx: {
      backgroundColor: "#FFFFFF",
      borderTop: "1px solid #E5E7EB",
      padding: "24px 0",
      mt: "auto"
    },
    children: [
      /* @__PURE__ */ a.jsxs(
        F,
        {
          sx: {
            maxWidth: 1200,
            margin: "0 auto",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 3
          },
          children: [
            /* @__PURE__ */ a.jsx(F, { sx: { display: "flex", flexWrap: "wrap", gap: 3 }, children: x.map((c, g) => /* @__PURE__ */ a.jsxs(
              jr,
              {
                href: c.href,
                sx: {
                  color: "#6B7280",
                  textDecoration: "none",
                  fontSize: "0.875rem",
                  fontWeight: 500,
                  "&:hover": {
                    color: W.primary
                  }
                },
                children: [
                  c.icon && /* @__PURE__ */ a.jsx("span", { style: { marginRight: 6, verticalAlign: "middle" }, children: c.icon }),
                  c.label
                ]
              },
              g
            )) }),
            /* @__PURE__ */ a.jsx(F, { sx: { display: "flex", alignItems: "center", gap: 1 }, children: E.map((c, g) => /* @__PURE__ */ a.jsx(
              re,
              {
                href: c.href,
                size: "small",
                sx: {
                  color: "#9CA3AF",
                  "&:hover": {
                    color: W.primary,
                    backgroundColor: "#F3F4F6"
                  }
                },
                children: c.icon
              },
              g
            )) })
          ]
        }
      ),
      /* @__PURE__ */ a.jsx(
        F,
        {
          sx: {
            maxWidth: 1200,
            margin: "16px auto 0",
            textAlign: "center"
          },
          children: /* @__PURE__ */ a.jsx(
            De,
            {
              variant: "body2",
              sx: {
                color: "#9CA3AF",
                fontSize: "0.875rem"
              },
              children: u
            }
          )
        }
      )
    ]
  }
), Hr = ({
  scrollable: x = !0,
  overflow: u = "auto",
  children: E,
  ...c
}) => /* @__PURE__ */ a.jsx(
  F,
  {
    component: "main",
    sx: {
      flexGrow: 1,
      overflow: x ? u : "visible",
      backgroundColor: "#FFFFFF"
    },
    ...c,
    children: E
  }
);
export {
  Mr as Container,
  Hr as ContentArea,
  Nr as Footer,
  Vr as Header,
  Br as Layout,
  Ur as Sidebar
};
