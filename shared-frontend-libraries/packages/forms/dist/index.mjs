import B, { useState as z } from "react";
import { Box as w, TextField as oe, FormLabel as Er, FormHelperText as ie, FormControl as br, InputLabel as Rr, Select as _r, MenuItem as jr, Checkbox as Tr, FormControlLabel as ne, RadioGroup as Sr, IconButton as Ie, ClickAwayListener as We, Popper as $e, Paper as De } from "@mui/material";
import { MANAGEMENT_COLORS as I } from "@shared-frontend-libraries/design-system";
import { CalendarToday as Or, AccessTime as wr } from "@mui/icons-material";
var ae = { exports: {} }, M = {};
/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
var Fe;
function Cr() {
  if (Fe) return M;
  Fe = 1;
  var p = B, s = Symbol.for("react.element"), c = Symbol.for("react.fragment"), u = Object.prototype.hasOwnProperty, x = p.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner, d = { key: !0, ref: !0, __self: !0, __source: !0 };
  function y(h, v, S) {
    var b, T = {}, m = null, P = null;
    S !== void 0 && (m = "" + S), v.key !== void 0 && (m = "" + v.key), v.ref !== void 0 && (P = v.ref);
    for (b in v) u.call(v, b) && !d.hasOwnProperty(b) && (T[b] = v[b]);
    if (h && h.defaultProps) for (b in v = h.defaultProps, v) T[b] === void 0 && (T[b] = v[b]);
    return { $$typeof: s, type: h, key: m, ref: P, props: T, _owner: x.current };
  }
  return M.Fragment = c, M.jsx = y, M.jsxs = y, M;
}
var N = {};
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
function Pr() {
  return Ae || (Ae = 1, process.env.NODE_ENV !== "production" && function() {
    var p = B, s = Symbol.for("react.element"), c = Symbol.for("react.portal"), u = Symbol.for("react.fragment"), x = Symbol.for("react.strict_mode"), d = Symbol.for("react.profiler"), y = Symbol.for("react.provider"), h = Symbol.for("react.context"), v = Symbol.for("react.forward_ref"), S = Symbol.for("react.suspense"), b = Symbol.for("react.suspense_list"), T = Symbol.for("react.memo"), m = Symbol.for("react.lazy"), P = Symbol.for("react.offscreen"), C = Symbol.iterator, K = "@@iterator";
    function Le(e) {
      if (e === null || typeof e != "object")
        return null;
      var r = C && e[C] || e[K];
      return typeof r == "function" ? r : null;
    }
    var W = p.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED;
    function R(e) {
      {
        for (var r = arguments.length, t = new Array(r > 1 ? r - 1 : 0), n = 1; n < r; n++)
          t[n - 1] = arguments[n];
        Ye("error", e, t);
      }
    }
    function Ye(e, r, t) {
      {
        var n = W.ReactDebugCurrentFrame, l = n.getStackAddendum();
        l !== "" && (r += "%s", t = t.concat([l]));
        var f = t.map(function(i) {
          return String(i);
        });
        f.unshift("Warning: " + r), Function.prototype.apply.call(console[e], console, f);
      }
    }
    var Me = !1, Ne = !1, Be = !1, Ue = !1, Ve = !1, se;
    se = Symbol.for("react.module.reference");
    function Ge(e) {
      return !!(typeof e == "string" || typeof e == "function" || e === u || e === d || Ve || e === x || e === S || e === b || Ue || e === P || Me || Ne || Be || typeof e == "object" && e !== null && (e.$$typeof === m || e.$$typeof === T || e.$$typeof === y || e.$$typeof === h || e.$$typeof === v || // This needs to include all possible module reference object
      // types supported by any Flight configuration anywhere since
      // we don't know which Flight build this will end up being used
      // with.
      e.$$typeof === se || e.getModuleId !== void 0));
    }
    function Je(e, r, t) {
      var n = e.displayName;
      if (n)
        return n;
      var l = r.displayName || r.name || "";
      return l !== "" ? t + "(" + l + ")" : t;
    }
    function le(e) {
      return e.displayName || "Context";
    }
    function k(e) {
      if (e == null)
        return null;
      if (typeof e.tag == "number" && R("Received an unexpected object in getComponentNameFromType(). This is likely a bug in React. Please file an issue."), typeof e == "function")
        return e.displayName || e.name || null;
      if (typeof e == "string")
        return e;
      switch (e) {
        case u:
          return "Fragment";
        case c:
          return "Portal";
        case d:
          return "Profiler";
        case x:
          return "StrictMode";
        case S:
          return "Suspense";
        case b:
          return "SuspenseList";
      }
      if (typeof e == "object")
        switch (e.$$typeof) {
          case h:
            var r = e;
            return le(r) + ".Consumer";
          case y:
            var t = e;
            return le(t._context) + ".Provider";
          case v:
            return Je(e, e.render, "ForwardRef");
          case T:
            var n = e.displayName || null;
            return n !== null ? n : k(e.type) || "Memo";
          case m: {
            var l = e, f = l._payload, i = l._init;
            try {
              return k(i(f));
            } catch {
              return null;
            }
          }
        }
      return null;
    }
    var F = Object.assign, L = 0, ue, ce, fe, de, pe, ve, me;
    function he() {
    }
    he.__reactDisabledLog = !0;
    function ze() {
      {
        if (L === 0) {
          ue = console.log, ce = console.info, fe = console.warn, de = console.error, pe = console.group, ve = console.groupCollapsed, me = console.groupEnd;
          var e = {
            configurable: !0,
            enumerable: !0,
            value: he,
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
        L++;
      }
    }
    function Ke() {
      {
        if (L--, L === 0) {
          var e = {
            configurable: !0,
            enumerable: !0,
            writable: !0
          };
          Object.defineProperties(console, {
            log: F({}, e, {
              value: ue
            }),
            info: F({}, e, {
              value: ce
            }),
            warn: F({}, e, {
              value: fe
            }),
            error: F({}, e, {
              value: de
            }),
            group: F({}, e, {
              value: pe
            }),
            groupCollapsed: F({}, e, {
              value: ve
            }),
            groupEnd: F({}, e, {
              value: me
            })
          });
        }
        L < 0 && R("disabledDepth fell below zero. This is a bug in React. Please file an issue.");
      }
    }
    var q = W.ReactCurrentDispatcher, H;
    function U(e, r, t) {
      {
        if (H === void 0)
          try {
            throw Error();
          } catch (l) {
            var n = l.stack.trim().match(/\n( *(at )?)/);
            H = n && n[1] || "";
          }
        return `
` + H + e;
      }
    }
    var X = !1, V;
    {
      var qe = typeof WeakMap == "function" ? WeakMap : Map;
      V = new qe();
    }
    function ge(e, r) {
      if (!e || X)
        return "";
      {
        var t = V.get(e);
        if (t !== void 0)
          return t;
      }
      var n;
      X = !0;
      var l = Error.prepareStackTrace;
      Error.prepareStackTrace = void 0;
      var f;
      f = q.current, q.current = null, ze();
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
            } catch (j) {
              n = j;
            }
            Reflect.construct(e, [], i);
          } else {
            try {
              i.call();
            } catch (j) {
              n = j;
            }
            e.call(i.prototype);
          }
        } else {
          try {
            throw Error();
          } catch (j) {
            n = j;
          }
          e();
        }
      } catch (j) {
        if (j && n && typeof j.stack == "string") {
          for (var o = j.stack.split(`
`), _ = n.stack.split(`
`), g = o.length - 1, E = _.length - 1; g >= 1 && E >= 0 && o[g] !== _[E]; )
            E--;
          for (; g >= 1 && E >= 0; g--, E--)
            if (o[g] !== _[E]) {
              if (g !== 1 || E !== 1)
                do
                  if (g--, E--, E < 0 || o[g] !== _[E]) {
                    var O = `
` + o[g].replace(" at new ", " at ");
                    return e.displayName && O.includes("<anonymous>") && (O = O.replace("<anonymous>", e.displayName)), typeof e == "function" && V.set(e, O), O;
                  }
                while (g >= 1 && E >= 0);
              break;
            }
        }
      } finally {
        X = !1, q.current = f, Ke(), Error.prepareStackTrace = l;
      }
      var D = e ? e.displayName || e.name : "", A = D ? U(D) : "";
      return typeof e == "function" && V.set(e, A), A;
    }
    function He(e, r, t) {
      return ge(e, !1);
    }
    function Xe(e) {
      var r = e.prototype;
      return !!(r && r.isReactComponent);
    }
    function G(e, r, t) {
      if (e == null)
        return "";
      if (typeof e == "function")
        return ge(e, Xe(e));
      if (typeof e == "string")
        return U(e);
      switch (e) {
        case S:
          return U("Suspense");
        case b:
          return U("SuspenseList");
      }
      if (typeof e == "object")
        switch (e.$$typeof) {
          case v:
            return He(e.render);
          case T:
            return G(e.type, r, t);
          case m: {
            var n = e, l = n._payload, f = n._init;
            try {
              return G(f(l), r, t);
            } catch {
            }
          }
        }
      return "";
    }
    var Y = Object.prototype.hasOwnProperty, xe = {}, ye = W.ReactDebugCurrentFrame;
    function J(e) {
      if (e) {
        var r = e._owner, t = G(e.type, e._source, r ? r.type : null);
        ye.setExtraStackFrame(t);
      } else
        ye.setExtraStackFrame(null);
    }
    function Ze(e, r, t, n, l) {
      {
        var f = Function.call.bind(Y);
        for (var i in e)
          if (f(e, i)) {
            var o = void 0;
            try {
              if (typeof e[i] != "function") {
                var _ = Error((n || "React class") + ": " + t + " type `" + i + "` is invalid; it must be a function, usually from the `prop-types` package, but received `" + typeof e[i] + "`.This often happens because of typos such as `PropTypes.function` instead of `PropTypes.func`.");
                throw _.name = "Invariant Violation", _;
              }
              o = e[i](r, i, n, t, null, "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED");
            } catch (g) {
              o = g;
            }
            o && !(o instanceof Error) && (J(l), R("%s: type specification of %s `%s` is invalid; the type checker function must return `null` or an `Error` but returned a %s. You may have forgotten to pass an argument to the type checker creator (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an argument).", n || "React class", t, i, typeof o), J(null)), o instanceof Error && !(o.message in xe) && (xe[o.message] = !0, J(l), R("Failed %s type: %s", t, o.message), J(null));
          }
      }
    }
    var Qe = Array.isArray;
    function Z(e) {
      return Qe(e);
    }
    function er(e) {
      {
        var r = typeof Symbol == "function" && Symbol.toStringTag, t = r && e[Symbol.toStringTag] || e.constructor.name || "Object";
        return t;
      }
    }
    function rr(e) {
      try {
        return Ee(e), !1;
      } catch {
        return !0;
      }
    }
    function Ee(e) {
      return "" + e;
    }
    function be(e) {
      if (rr(e))
        return R("The provided key is an unsupported type %s. This value must be coerced to a string before before using it here.", er(e)), Ee(e);
    }
    var Re = W.ReactCurrentOwner, tr = {
      key: !0,
      ref: !0,
      __self: !0,
      __source: !0
    }, _e, je;
    function nr(e) {
      if (Y.call(e, "ref")) {
        var r = Object.getOwnPropertyDescriptor(e, "ref").get;
        if (r && r.isReactWarning)
          return !1;
      }
      return e.ref !== void 0;
    }
    function ar(e) {
      if (Y.call(e, "key")) {
        var r = Object.getOwnPropertyDescriptor(e, "key").get;
        if (r && r.isReactWarning)
          return !1;
      }
      return e.key !== void 0;
    }
    function or(e, r) {
      typeof e.ref == "string" && Re.current;
    }
    function ir(e, r) {
      {
        var t = function() {
          _e || (_e = !0, R("%s: `key` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", r));
        };
        t.isReactWarning = !0, Object.defineProperty(e, "key", {
          get: t,
          configurable: !0
        });
      }
    }
    function sr(e, r) {
      {
        var t = function() {
          je || (je = !0, R("%s: `ref` is not a prop. Trying to access it will result in `undefined` being returned. If you need to access the same value within the child component, you should pass it as a different prop. (https://reactjs.org/link/special-props)", r));
        };
        t.isReactWarning = !0, Object.defineProperty(e, "ref", {
          get: t,
          configurable: !0
        });
      }
    }
    var lr = function(e, r, t, n, l, f, i) {
      var o = {
        // This tag allows us to uniquely identify this as a React Element
        $$typeof: s,
        // Built-in properties that belong on the element
        type: e,
        key: r,
        ref: t,
        props: i,
        // Record the component responsible for creating this element.
        _owner: f
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
        value: l
      }), Object.freeze && (Object.freeze(o.props), Object.freeze(o)), o;
    };
    function ur(e, r, t, n, l) {
      {
        var f, i = {}, o = null, _ = null;
        t !== void 0 && (be(t), o = "" + t), ar(r) && (be(r.key), o = "" + r.key), nr(r) && (_ = r.ref, or(r, l));
        for (f in r)
          Y.call(r, f) && !tr.hasOwnProperty(f) && (i[f] = r[f]);
        if (e && e.defaultProps) {
          var g = e.defaultProps;
          for (f in g)
            i[f] === void 0 && (i[f] = g[f]);
        }
        if (o || _) {
          var E = typeof e == "function" ? e.displayName || e.name || "Unknown" : e;
          o && ir(i, E), _ && sr(i, E);
        }
        return lr(e, o, _, l, n, Re.current, i);
      }
    }
    var Q = W.ReactCurrentOwner, Te = W.ReactDebugCurrentFrame;
    function $(e) {
      if (e) {
        var r = e._owner, t = G(e.type, e._source, r ? r.type : null);
        Te.setExtraStackFrame(t);
      } else
        Te.setExtraStackFrame(null);
    }
    var ee;
    ee = !1;
    function re(e) {
      return typeof e == "object" && e !== null && e.$$typeof === s;
    }
    function Se() {
      {
        if (Q.current) {
          var e = k(Q.current.type);
          if (e)
            return `

Check the render method of \`` + e + "`.";
        }
        return "";
      }
    }
    function cr(e) {
      return "";
    }
    var Oe = {};
    function fr(e) {
      {
        var r = Se();
        if (!r) {
          var t = typeof e == "string" ? e : e.displayName || e.name;
          t && (r = `

Check the top-level render call using <` + t + ">.");
        }
        return r;
      }
    }
    function we(e, r) {
      {
        if (!e._store || e._store.validated || e.key != null)
          return;
        e._store.validated = !0;
        var t = fr(r);
        if (Oe[t])
          return;
        Oe[t] = !0;
        var n = "";
        e && e._owner && e._owner !== Q.current && (n = " It was passed a child from " + k(e._owner.type) + "."), $(e), R('Each child in a list should have a unique "key" prop.%s%s See https://reactjs.org/link/warning-keys for more information.', t, n), $(null);
      }
    }
    function Ce(e, r) {
      {
        if (typeof e != "object")
          return;
        if (Z(e))
          for (var t = 0; t < e.length; t++) {
            var n = e[t];
            re(n) && we(n, r);
          }
        else if (re(e))
          e._store && (e._store.validated = !0);
        else if (e) {
          var l = Le(e);
          if (typeof l == "function" && l !== e.entries)
            for (var f = l.call(e), i; !(i = f.next()).done; )
              re(i.value) && we(i.value, r);
        }
      }
    }
    function dr(e) {
      {
        var r = e.type;
        if (r == null || typeof r == "string")
          return;
        var t;
        if (typeof r == "function")
          t = r.propTypes;
        else if (typeof r == "object" && (r.$$typeof === v || // Note: Memo only checks outer props here.
        // Inner props are checked in the reconciler.
        r.$$typeof === T))
          t = r.propTypes;
        else
          return;
        if (t) {
          var n = k(r);
          Ze(t, e.props, "prop", n, e);
        } else if (r.PropTypes !== void 0 && !ee) {
          ee = !0;
          var l = k(r);
          R("Component %s declared `PropTypes` instead of `propTypes`. Did you misspell the property assignment?", l || "Unknown");
        }
        typeof r.getDefaultProps == "function" && !r.getDefaultProps.isReactClassApproved && R("getDefaultProps is only used on classic React.createClass definitions. Use a static property named `defaultProps` instead.");
      }
    }
    function pr(e) {
      {
        for (var r = Object.keys(e.props), t = 0; t < r.length; t++) {
          var n = r[t];
          if (n !== "children" && n !== "key") {
            $(e), R("Invalid prop `%s` supplied to `React.Fragment`. React.Fragment can only have `key` and `children` props.", n), $(null);
            break;
          }
        }
        e.ref !== null && ($(e), R("Invalid attribute `ref` supplied to `React.Fragment`."), $(null));
      }
    }
    var Pe = {};
    function ke(e, r, t, n, l, f) {
      {
        var i = Ge(e);
        if (!i) {
          var o = "";
          (e === void 0 || typeof e == "object" && e !== null && Object.keys(e).length === 0) && (o += " You likely forgot to export your component from the file it's defined in, or you might have mixed up default and named imports.");
          var _ = cr();
          _ ? o += _ : o += Se();
          var g;
          e === null ? g = "null" : Z(e) ? g = "array" : e !== void 0 && e.$$typeof === s ? (g = "<" + (k(e.type) || "Unknown") + " />", o = " Did you accidentally export a JSX literal instead of a component?") : g = typeof e, R("React.jsx: type is invalid -- expected a string (for built-in components) or a class/function (for composite components) but got: %s.%s", g, o);
        }
        var E = ur(e, r, t, l, f);
        if (E == null)
          return E;
        if (i) {
          var O = r.children;
          if (O !== void 0)
            if (n)
              if (Z(O)) {
                for (var D = 0; D < O.length; D++)
                  Ce(O[D], e);
                Object.freeze && Object.freeze(O);
              } else
                R("React.jsx: Static children should always be an array. You are likely explicitly calling React.jsxs or React.jsxDEV. Use the Babel transform instead.");
            else
              Ce(O, e);
        }
        if (Y.call(r, "key")) {
          var A = k(e), j = Object.keys(r).filter(function(yr) {
            return yr !== "key";
          }), te = j.length > 0 ? "{key: someKey, " + j.join(": ..., ") + ": ...}" : "{key: someKey}";
          if (!Pe[A + te]) {
            var xr = j.length > 0 ? "{" + j.join(": ..., ") + ": ...}" : "{}";
            R(`A props object containing a "key" prop is being spread into JSX:
  let props = %s;
  <%s {...props} />
React keys must be passed directly to JSX without using spread:
  let props = %s;
  <%s key={someKey} {...props} />`, te, A, xr, A), Pe[A + te] = !0;
          }
        }
        return e === u ? pr(E) : dr(E), E;
      }
    }
    function vr(e, r, t) {
      return ke(e, r, t, !0);
    }
    function mr(e, r, t) {
      return ke(e, r, t, !1);
    }
    var hr = mr, gr = vr;
    N.Fragment = u, N.jsx = hr, N.jsxs = gr;
  }()), N;
}
process.env.NODE_ENV === "production" ? ae.exports = Cr() : ae.exports = Pr();
var a = ae.exports;
const Yr = ({ children: p, onSubmit: s, noValidate: c = !1, ...u }) => /* @__PURE__ */ a.jsx(
  w,
  {
    component: "form",
    onSubmit: s,
    noValidate: c,
    sx: {
      width: "100%",
      display: "flex",
      flexDirection: "column",
      gap: 2
    },
    ...u,
    children: p
  }
), kr = B.forwardRef(
  ({ label: p, helperText: s, error: c, size: u = "medium", ...x }, d) => /* @__PURE__ */ a.jsx(
    oe,
    {
      ref: d,
      label: p,
      helperText: s,
      error: c,
      size: u,
      variant: "outlined",
      fullWidth: !0,
      ...x
    }
  )
);
kr.displayName = "TextField";
const Fr = ({ label: p, error: s, required: c = !1, children: u, ...x }) => /* @__PURE__ */ a.jsxs(w, { ...x, children: [
  p && /* @__PURE__ */ a.jsxs(
    Er,
    {
      component: "legend",
      sx: {
        color: "#374151",
        fontWeight: 500,
        marginBottom: "8px"
      },
      children: [
        p,
        " ",
        c && " *"
      ]
    }
  ),
  /* @__PURE__ */ a.jsx(Fr, { children: u }),
  s && /* @__PURE__ */ a.jsx(
    ie,
    {
      error: !0,
      sx: {
        marginTop: "4px",
        ml: "14px"
      },
      children: s
    }
  )
] }), Ar = B.forwardRef(
  ({ label: p, helperText: s, error: c, required: u = !1, options: x, size: d = "medium", ...y }, h) => /* @__PURE__ */ a.jsxs(br, { fullWidth: !0, error: c, ref: h, size: d, children: [
    p && /* @__PURE__ */ a.jsxs(
      Rr,
      {
        sx: {
          color: c ? I.error : "#374151",
          fontWeight: 500
        },
        children: [
          p,
          " ",
          u && " *"
        ]
      }
    ),
    /* @__PURE__ */ a.jsx(
      _r,
      {
        label: p,
        error: c,
        size: d,
        ...y,
        children: x.map((v) => /* @__PURE__ */ a.jsx(jr, { value: v.value, children: v.label }, v.value))
      }
    ),
    s && /* @__PURE__ */ a.jsx(ie, { error: c, children: s })
  ] })
);
Ar.displayName = "FormSelect";
const Ir = B.forwardRef(
  ({ label: p, error: s, helperText: c, size: u = "medium", ...x }, d) => /* @__PURE__ */ a.jsxs(w, { sx: { display: "flex", alignItems: u === "small" ? "flex-start" : "center" }, children: [
    /* @__PURE__ */ a.jsx(
      Tr,
      {
        ref: d,
        size: u,
        sx: {
          color: s ? I.error : "#374151"
        },
        ...x
      }
    ),
    (p || c) && /* @__PURE__ */ a.jsxs(w, { sx: { ml: 2, flex: 1 }, children: [
      p && /* @__PURE__ */ a.jsx(
        ne,
        {
          sx: {
            color: s ? I.error : "#374151",
            fontSize: u === "small" ? "0.875rem" : "1rem"
          },
          children: p
        }
      ),
      c && /* @__PURE__ */ a.jsx(
        w,
        {
          component: "span",
          sx: {
            color: s ? I.error : "#6B7280",
            fontSize: "0.75rem",
            mt: 0.25
          },
          children: c
        }
      )
    ] })
  ] })
);
Ir.displayName = "FormCheckbox";
const Mr = ({
  label: p,
  error: s,
  helperText: c,
  options: u,
  required: x = !1,
  size: d = "medium",
  ...y
}) => /* @__PURE__ */ a.jsxs(w, { children: [
  p && /* @__PURE__ */ a.jsxs(
    ne,
    {
      component: "legend",
      sx: {
        color: s ? I.error : "#374151",
        fontWeight: 500,
        marginBottom: "8px"
      },
      children: [
        p,
        " ",
        x && " *"
      ]
    }
  ),
  /* @__PURE__ */ a.jsx(
    Sr,
    {
      ...y,
      sx: {
        "& .MuiRadio-root": {
          padding: d === "small" ? "4px" : d === "medium" ? "6px" : "8px"
        }
      },
      children: u.map((h) => /* @__PURE__ */ a.jsxs(w, { sx: { display: "flex", alignItems: d === "small" ? "flex-start" : "center", mb: 1 }, children: [
        /* @__PURE__ */ a.jsx("input", { type: "radio", name: "radiogroup", value: h.value, id: h.value }),
        /* @__PURE__ */ a.jsx(
          ne,
          {
            htmlFor: h.value,
            sx: {
              color: s ? I.error : "#374151",
              fontSize: d === "small" ? "0.875rem" : "1rem",
              ml: 1,
              cursor: "pointer"
            },
            children: h.label
          }
        )
      ] }, h.value))
    }
  ),
  s && /* @__PURE__ */ a.jsx(
    ie,
    {
      error: !0,
      sx: {
        ml: "14px",
        mt: 1
      },
      children: s
    }
  )
] }), Nr = ({
  label: p,
  value: s,
  onChange: c,
  error: u,
  helperText: x,
  minDate: d,
  maxDate: y
}) => {
  const [h, v] = z(!1), [S, b] = z(null), T = () => v((C) => !C), m = (C) => {
    const K = C.target.value ? new Date(C.target.value) : null;
    c == null || c(K), v(!1);
  }, P = (C) => C.toLocaleDateString("en-US", { year: "numeric", month: "long", day: "numeric" });
  return /* @__PURE__ */ a.jsxs(w, { sx: { width: "100%" }, children: [
    /* @__PURE__ */ a.jsx(
      oe,
      {
        label: p,
        value: s ? P(s) : "",
        onChange: m,
        error: !!u,
        helperText: u,
        onFocus: (C) => b(C.currentTarget),
        inputProps: {
          readOnly: !0,
          min: d == null ? void 0 : d.toISOString().split("T")[0],
          max: y == null ? void 0 : y.toISOString().split("T")[0]
        },
        sx: {
          "& .MuiOutlinedInput-root": {
            paddingRight: "40px"
          }
        }
      }
    ),
    /* @__PURE__ */ a.jsx(
      Ie,
      {
        size: "small",
        sx: { position: "absolute", right: 8, top: 18, p: 0.5 },
        onClick: T,
        children: /* @__PURE__ */ a.jsx(Or, {})
      }
    ),
    h && S && /* @__PURE__ */ a.jsx(We, { onClickAway: () => v(!1), children: /* @__PURE__ */ a.jsx(
      $e,
      {
        open: h,
        anchorEl: S,
        placement: "bottom-start",
        children: /* @__PURE__ */ a.jsx(
          De,
          {
            elevation: 3,
            sx: {
              p: 2,
              maxHeight: 300,
              overflow: "auto",
              zIndex: 9999
            },
            children: /* @__PURE__ */ a.jsx(
              "input",
              {
                type: "date",
                value: s ? s.toISOString().split("T")[0] : "",
                onChange: m,
                min: d == null ? void 0 : d.toISOString().split("T")[0],
                max: y == null ? void 0 : y.toISOString().split("T")[0],
                style: {
                  width: "100%",
                  padding: "8px",
                  border: `1px solid ${u ? I.error : "#E5E7EB"}`,
                  borderRadius: "6px"
                }
              }
            )
          }
        )
      }
    ) }),
    x && !u && /* @__PURE__ */ a.jsx(w, { component: "span", sx: { ml: "14px", mt: 0.5, color: "#6B7280", fontSize: "0.75rem" }, children: x })
  ] });
}, Br = ({
  label: p,
  value: s,
  onChange: c,
  error: u,
  helperText: x
}) => {
  const [d, y] = z(!1), [h, v] = z(null), S = [];
  for (let m = 0; m < 24; m++)
    for (let P = 0; P < 60; P += 15)
      S.push(`${m.toString().padStart(2, "0")}:${P.toString().padStart(2, "0")}`);
  const b = () => y((m) => !m), T = (m) => {
    c == null || c(m), y(!1);
  };
  return /* @__PURE__ */ a.jsxs(w, { sx: { width: "100%" }, children: [
    /* @__PURE__ */ a.jsx(
      oe,
      {
        label: p,
        value: s || "",
        error: !!u,
        helperText: u,
        onFocus: (m) => v(m.currentTarget),
        inputProps: {
          readOnly: !0
        },
        sx: {
          "& .MuiOutlinedInput-root": {
            paddingRight: "40px"
          }
        }
      }
    ),
    /* @__PURE__ */ a.jsx(
      Ie,
      {
        size: "small",
        sx: { position: "absolute", right: 8, top: 18, p: 0.5 },
        onClick: b,
        children: /* @__PURE__ */ a.jsx(wr, {})
      }
    ),
    d && h && /* @__PURE__ */ a.jsx(We, { onClickAway: () => y(!1), children: /* @__PURE__ */ a.jsx(
      $e,
      {
        open: d,
        anchorEl: h,
        placement: "bottom-start",
        children: /* @__PURE__ */ a.jsx(
          De,
          {
            elevation: 3,
            sx: {
              p: 1,
              maxHeight: 250,
              overflow: "auto",
              zIndex: 9999
            },
            children: S.map((m) => /* @__PURE__ */ a.jsx(
              w,
              {
                onClick: () => T(m),
                sx: {
                  px: 2,
                  py: 1,
                  cursor: "pointer",
                  "&:hover": {
                    backgroundColor: "#F3F4F6"
                  }
                },
                children: m
              },
              m
            ))
          }
        )
      }
    ) }),
    x && !u && /* @__PURE__ */ a.jsx(w, { component: "span", sx: { ml: "14px", mt: 0.5, color: "#6B7280", fontSize: "0.75rem" }, children: x })
  ] });
};
export {
  Nr as DatePicker,
  Yr as Form,
  Ir as FormCheckbox,
  Fr as FormGroup,
  Mr as FormRadioGroup,
  Ar as FormSelect,
  kr as TextField,
  Br as TimePicker
};
