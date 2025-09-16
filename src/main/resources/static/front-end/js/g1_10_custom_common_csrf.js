// console.log(document.cookie);
// "XSRF-TOKEN=abc123; theme=dark; sessionId=xyz789"

function getCookie(name) {
	const value = `; ${document.cookie}`; // "; XSRF-TOKEN=abc123; theme=dark; sessionId=xyz789"
	const parts = value.split(`; ${name}=`); // ["; ", "abc123; theme=dark; sessionId=xyz789"]
	if (parts.length === 2) return parts.pop().split(";").shift(); // 2代表有該 cookie(1則為沒有） -> 拿後面陣列 -> 以;分隔 -> 拿第一個值
	return null;
}

// 1. Cookie 可能不是立即可見
// Set-Cookie: XSRF-TOKEN=... 其實會在第一次 response 才下發。
// 你的 JS 如果「同一個流程裡立刻 fetch API」 → 取不到 cookie 值，因為瀏覽器還沒把 cookie 寫進 document.cookie。
// 所以常見解法就是「先暖身 GET 一次 /」，讓 cookie 確定種下，再開始呼叫 API。

// 2. 需要注意 authorizeHttpRequest 與會不會經過 csrffilter 是兩回事
// 如果 /exhibitions 沒走 Controller，而是直接 resources/static/exhibitions.html → Security 才會預設跳過 → 不會跑 CsrfFilter → 不會種 cookie。
// 這時就算你 GET 過，也永遠拿不到 token，除非你打 /（Controller 有處理）才會拿到。

// (1) 不處理任何 .then 的版本
window.csrfFetch = function (url, options) {
  options = options || {}; // method, headers, body

  function doFetch() {
    const token = getCookie("XSRF-TOKEN"); // from CookieCsrfTokenRepository
	console.log(url + " 取得token: " + token);
    const headers = {
      ...options.headers,
      "X-XSRF-TOKEN": token || ""
    };

    return fetch(url, {
      ...options,
      headers,
      credentials: "include" // 確保 cookie 帶上
    });
  }

  // 如果沒有 token，先暖身 GET 讓後端種 cookie
  // 當 request 中「有人用到」這個 token（例如 JSP/Thymeleaf 拿 @csrf，或 Controller 注入 CsrfToken 參數，或 REST 返回它） → repository 才會把 token 寫回 response → 你才會看到 Set-Cookie: XSRF-TOKEN=...。
  // 如果沒有人用 token，它認為「沒必要暴露」→ 就不會送 cookie。
  // 這條 有手動宣告 CsrfToken 所以有效！
  if (!getCookie("XSRF-TOKEN")) {
	console.log(url + ": 有看到代表初次取不到token");
    return fetch("/api/auth/csrf_token", { credentials: "include" }).then(() => doFetch());
  }

  // 已經有 token，直接打
  return doFetch();
};

// (2) ## 協助處理401 ## -> 存 redirect 進 sessionStorage 的版本 -> 轉導向 login
window.csrfFetchToRedirect = function (url, options) {
  options = options || {}; // method, headers, body

  function doFetch() {
    const token = getCookie("XSRF-TOKEN"); // from CookieCsrfTokenRepository
	console.log(url + " 取得token: " + token);
    const headers = {
      ...options.headers,
      "X-XSRF-TOKEN": token || ""
    };

    return fetch(url, {
      ...options,
      headers,
      credentials: "include" // 確保 cookie 帶上
    });
  }

  // 如果沒有 token，先暖身 GET 讓後端種 cookie
  // 當 request 中「有人用到」這個 token（例如 JSP/Thymeleaf 拿 @csrf，或 Controller 注入 CsrfToken 參數，或 REST 返回它） → repository 才會把 token 寫回 response → 你才會看到 Set-Cookie: XSRF-TOKEN=...。
  // 如果沒有人用 token，它認為「沒必要暴露」→ 就不會送 cookie。
  // 這條 有手動宣告 CsrfToken 所以有效！
  if (!getCookie("XSRF-TOKEN")) {
	console.log(url + ": 有看到代表初次取不到token");
    return fetch("/api/auth/csrf_token", { credentials: "include" }).then(() => doFetch());
  }

  // 已經有 token，直接打
  return doFetch().then(res => {
    if (res.status === 401) {
      sessionStorage.setItem("redirect", window.location.pathname);
       window.location.href = "/front-end/login";
      return new Promise(() => {}); // 停住，避免呼叫端繼續跑 then
    }
	return res;
  });
};
