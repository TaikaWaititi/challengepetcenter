/* Aplica ao Swagger o JWT obtido pelo login, sem expor credenciais no codigo. */
(function () {
  "use strict";
  const original = window.SwaggerUIBundle;
  const storageKey = "petcenter.swagger.token";
  let ui;
  let status;

  function remember(token) {
    ui.preauthorizeApiKey("bearerAuth", token);
    try { sessionStorage.setItem(storageKey, token); } catch (_) { /* sessao em memoria */ }
    if (status) status.textContent = "Autenticado. O token sera enviado automaticamente nas chamadas protegidas.";
  }

  function forget() {
    try { sessionStorage.removeItem(storageKey); } catch (_) { /* armazenamento indisponivel */ }
    ui.authActions.logout(["bearerAuth"]);
  }

  async function post(path, body) {
    const response = await fetch(new URL(path, window.location.origin), {
      method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body)
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Nao foi possivel autenticar. Confira email, senha e perfil TUTOR.");
    return data;
  }

  function panel() {
    if (document.getElementById("petcenter-auth")) return;
    const section = document.createElement("section");
    section.id = "petcenter-auth";
    section.style.cssText = "max-width:1400px;margin:20px auto;padding:20px;box-sizing:border-box;border:1px solid #aaa;border-radius:6px;font:16px Arial,sans-serif";
    section.innerHTML = '<h2 style="margin-top:0">Acesso ao CRUD de pets</h2>' +
      '<p>Entre com um usuario TUTOR ou crie um tutor para demonstracao. A autorizacao sera aplicada automaticamente.</p>' +
      '<form id="petcenter-login"><label>Email <input name="email" type="email" autocomplete="username" required></label> ' +
      '<label>Senha <input name="senha" type="password" autocomplete="current-password" required></label> ' +
      '<button type="submit">Entrar</button></form>' +
      '<p><button type="button" id="petcenter-demo">Criar tutor de demonstracao e entrar</button> ' +
      '<button type="button" id="petcenter-logout">Sair</button></p>' +
      '<p id="petcenter-auth-status" role="status" aria-live="polite">Faca login antes de executar o CRUD.</p>';
    const root = document.getElementById("swagger-ui");
    root.parentNode.insertBefore(section, root);
    status = document.getElementById("petcenter-auth-status");

    async function authenticate(button, action) {
      button.disabled = true;
      status.textContent = "Autenticando...";
      try {
        const data = await action();
        if (!data.token) throw new Error("O login nao retornou token.");
        remember(data.token);
      } catch (error) {
        forget();
        status.textContent = error.message;
      } finally { button.disabled = false; }
    }

    const form = document.getElementById("petcenter-login");
    form.addEventListener("submit", function (event) {
      event.preventDefault();
      const body = { email: form.elements.email.value.trim(), senha: form.elements.senha.value };
      authenticate(form.querySelector("button"), () => post("/api/auth/login", body));
      form.elements.senha.value = "";
    });
    const demo = document.getElementById("petcenter-demo");
    demo.addEventListener("click", () => authenticate(demo, async () => {
      const body = { email: "video." + crypto.randomUUID() + "@example.test", senha: crypto.randomUUID().slice(0, 8) };
      await post("/api/users", { ...body, nome: "Tutor Demonstracao", telefone: "11900000000", tipoUsuario: "TUTOR" });
      return post("/api/auth/login", body);
    }));
    document.getElementById("petcenter-logout").addEventListener("click", () => {
      forget();
      status.textContent = "Sessao encerrada. Faca login antes de executar o CRUD.";
    });
    try {
      const token = sessionStorage.getItem(storageKey);
      if (token) remember(token);
    } catch (_) { /* sem armazenamento, o login continua disponivel */ }
  }

  function configured(options) {
    const previousResponse = options.responseInterceptor;
    const previousComplete = options.onComplete;
    options.responseInterceptor = async function (response) {
      if (previousResponse) response = await previousResponse(response);
      if (response.status === 200 && new URL(response.url, window.location.origin).pathname === "/api/auth/login") {
        const data = response.obj || JSON.parse(response.text || "{}");
        if (data.token) remember(data.token);
      }
      if (response.status === 401) {
        forget();
        if (status) status.textContent = "Sessao ausente ou expirada. Entre novamente para executar o CRUD.";
      }
      return response;
    };
    options.onComplete = function () {
      ui = window.ui;
      panel();
      if (previousComplete) previousComplete();
    };
    return original(options);
  }
  Object.assign(configured, original);
  window.SwaggerUIBundle = configured;
})();
