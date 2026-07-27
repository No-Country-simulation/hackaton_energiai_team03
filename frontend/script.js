// Endpoints do backend
const API_URL = "http://localhost:8080/analise-energetica";
const HISTORICO_API = "http://localhost:8080/api/analises/historico";
const PATCH_API_URL = "http://localhost:8080/api/analises";

// Marcas disponíveis no select de freezer
const marcas = [
  "Metalfrio",
  "Gelopar",
  "Friginox",
  "Invar",
  "Electrolux",
  "Outro",
];

const snowflakes = ["🍦", "🍨", "🍧", "🧊", "❄️", "🥄", "⚡"];
let freezerCount = 0; // Contador para gerar IDs únicos dos freezers

/* ── Toast Notifications ── */
const toastContainer = document.createElement("div");
toastContainer.className = "toast-container";
document.body.appendChild(toastContainer);

const toastIcons = {
  success: "✅",
  error: "❌",
  warning: "⚠️",
  info: "ℹ️",
};

function showToast(message, type = "info", duration = 3500) {
  const toast = document.createElement("div");
  toast.className = "toast " + type;
  toast.innerHTML =
    '<span class="toast-icon">' +
    toastIcons[type] +
    "</span>" +
    '<span class="toast-text">' +
    message +
    "</span>";
  toastContainer.appendChild(toast);

  setTimeout(() => {
    toast.classList.add("removing");
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

const exemplos = {
  1: {
    consumo_kwh: 150,
    uso_horario_pico: false,
    horas_alto_consumo: "Baixo",
    epoca_ano: "Primavera",
    freezers: [
      {
        marca: "Metalfrio",
        tipo: "EXIBICAO",
        tecnologia: "Inverter",
        estado_borracha: "Integra",
        quantidade: 2,
      },
      {
        marca: "Gelopar",
        tipo: "EXIBICAO",
        tecnologia: "Inverter",
        estado_borracha: "Integra",
        quantidade: 2,
      },
    ],
  },
  2: {
    consumo_kwh: 300,
    uso_horario_pico: true,
    horas_alto_consumo: "Médio",
    epoca_ano: "Inverno",
    freezers: [
      {
        marca: "Metalfrio",
        tipo: "EXIBICAO",
        tecnologia: "Convencional",
        estado_borracha: "Integra",
        quantidade: 3,
      },
      {
        marca: "Gelopar",
        tipo: "EXIBICAO",
        tecnologia: "Inverter",
        estado_borracha: "Integra",
        quantidade: 2,
      },
      {
        marca: "Friginox",
        tipo: "EXIBICAO",
        tecnologia: "Convencional",
        estado_borracha: "Gasta",
        quantidade: 1,
      },
    ],
  },
  3: {
    consumo_kwh: 1200,
    uso_horario_pico: true,
    horas_alto_consumo: "Alto",
    epoca_ano: "Verão",
    freezers: [
      {
        marca: "Metalfrio",
        tipo: "EXIBICAO",
        tecnologia: "Convencional",
        estado_borracha: "Gasta",
        quantidade: 2,
      },
      {
        marca: "Metalfrio",
        tipo: "ARMAZENAMENTO",
        tecnologia: "Convencional",
        estado_borracha: "Gasta",
        quantidade: 1,
      },
      {
        marca: "Gelopar",
        tipo: "ARMAZENAMENTO",
        tecnologia: "Convencional",
        estado_borracha: "Gasta",
        quantidade: 2,
      },
      {
        marca: "Electrolux",
        tipo: "EXIBICAO",
        tecnologia: "Convencional",
        estado_borracha: "Integra",
        quantidade: 2,
      },
      {
        marca: "Invar",
        tipo: "ARMAZENAMENTO",
        tecnologia: "Inverter",
        estado_borracha: "Integra",
        quantidade: 1,
      },
    ],
  },
};

/* ── Premium Snowfall (3 layers + sway) ── */
// Cria animação de neve com 3 camadas de profundidade (fundo, meio, frente)
let snowflakeCount = 0;
const MAX_SNOWFLAKES = 35;

const layers = [
  {
    cls: "a",
    minSize: 0.5,
    maxSize: 0.8,
    minDur: 25,
    maxDur: 35,
    minOp: 0.15,
    maxOp: 0.25,
  },
  {
    cls: "b",
    minSize: 0.9,
    maxSize: 1.3,
    minDur: 17,
    maxDur: 25,
    minOp: 0.25,
    maxOp: 0.35,
  },
  {
    cls: "c",
    minSize: 1.4,
    maxSize: 2.0,
    minDur: 11,
    maxDur: 17,
    minOp: 0.35,
    maxOp: 0.5,
  },
];

function createSnowflake() {
  const container = document.getElementById("snowfall");

  if (container.children.length >= MAX_SNOWFLAKES) {
    container.removeChild(container.firstElementChild);
  }

  const el = document.createElement("div");
  el.classList.add("snowflake");
  el.textContent = snowflakes[snowflakeCount % snowflakes.length];
  snowflakeCount++;

  const layer = layers[snowflakeCount % 3];
  el.classList.add("snowflake-" + layer.cls);

  const zone = (snowflakeCount % 5) * 20;
  el.style.left = zone + Math.random() * 20 + "%";
  el.style.fontSize =
    layer.minSize + Math.random() * (layer.maxSize - layer.minSize) + "rem";
  el.style.animationDuration =
    layer.minDur + Math.random() * (layer.maxDur - layer.minDur) + "s";
  el.style.animationDelay = Math.random() * 4 + "s";
  el.style.opacity = layer.minOp + Math.random() * (layer.maxOp - layer.minOp);

  container.appendChild(el);
}

let snowflakeInterval = setInterval(createSnowflake, 1200);
for (let i = 0; i < 10; i++) setTimeout(createSnowflake, i * 300);

/* ── Theme Toggle (Dark Mode) ── */
(function initTheme() {
  const saved = localStorage.getItem("theme");
  if (saved === "dark") {
    document.documentElement.setAttribute("data-theme", "dark");
  }
})();

document.getElementById("themeToggle").addEventListener("click", () => {
  const html = document.documentElement;
  const isDark = html.getAttribute("data-theme") === "dark";

  if (isDark) {
    html.removeAttribute("data-theme");
    localStorage.setItem("theme", "light");
  } else {
    html.setAttribute("data-theme", "dark");
    localStorage.setItem("theme", "dark");
  }

  lucide.createIcons();
});

/* ── Custom Select (Época do Ano) ── */
// Inicializa os selects customizados com ícones quando a página carrega
document.addEventListener("DOMContentLoaded", () => {
  lucide.createIcons();

  const epocaSelect = document.getElementById("epocaAno");
  initCustomSelect(epocaSelect, {
    Verão: '<i data-lucide="sun" class="select-icon"></i>',
    Inverno: '<i data-lucide="snowflake" class="select-icon"></i>',
    Primavera: '<i data-lucide="flower-2" class="select-icon"></i>',
    Outono: '<i data-lucide="leaf" class="select-icon"></i>',
  });

  const usoSelect = document.getElementById("usoHorarioPico");
  initCustomSelect(usoSelect);

  const horasSelect = document.getElementById("horasAltoConsumo");
  initCustomSelect(horasSelect, {
    Baixo: '<i data-lucide="trending-down" class="select-icon"></i>',
    Médio: '<i data-lucide="minus" class="select-icon"></i>',
    Alto: '<i data-lucide="trending-up" class="select-icon"></i>',
  });
});

/* ── Custom Select Component ── */
// Transforma um <select> nativo em um dropdown customizado com ícones e animação
function initCustomSelect(selectEl, icons = {}) {
  if (selectEl.dataset.customInit) return;
  selectEl.dataset.customInit = "true";

  const wrapper = document.createElement("div");
  wrapper.className = "custom-select";

  const trigger = document.createElement("div");
  trigger.className = "custom-select-trigger";

  const selectedOpt = selectEl.options[selectEl.selectedIndex];
  const valueSpan = document.createElement("span");
  valueSpan.className = "custom-select-value";

  const iconKey = selectedOpt.value;
  if (icons[iconKey]) {
    valueSpan.innerHTML = icons[iconKey] + " " + selectedOpt.text;
  } else {
    valueSpan.textContent = selectedOpt.text;
  }

  const arrow = document.createElement("i");
  arrow.setAttribute("data-lucide", "chevron-down");
  arrow.className = "select-arrow";

  trigger.appendChild(valueSpan);
  trigger.appendChild(arrow);

  const optionsContainer = document.createElement("div");
  optionsContainer.className = "custom-select-options";

  Array.from(selectEl.options).forEach((opt) => {
    const div = document.createElement("div");
    div.className = "custom-option" + (opt.selected ? " selected" : "");
    div.dataset.value = opt.value;

    const iconKey = opt.value;
    if (icons[iconKey]) {
      div.innerHTML = icons[iconKey] + " " + opt.text;
    } else {
      div.textContent = opt.text;
    }

    div.addEventListener("click", () => {
      optionsContainer
        .querySelectorAll(".custom-option")
        .forEach((o) => o.classList.remove("selected"));
      div.classList.add("selected");
      selectEl.value = opt.value;
      selectEl.dispatchEvent(new Event("change"));

      const newIcon = icons[opt.value] || "";
      if (newIcon) {
        valueSpan.innerHTML = newIcon + " " + opt.text;
      } else {
        valueSpan.textContent = opt.text;
      }
      wrapper.classList.remove("open");
    });

    optionsContainer.appendChild(div);
  });

  trigger.addEventListener("click", (e) => {
    e.stopPropagation();
    document.querySelectorAll(".custom-select.open").forEach((s) => {
      if (s !== wrapper) s.classList.remove("open");
    });
    wrapper.classList.toggle("open");
  });

  wrapper.appendChild(trigger);
  wrapper.appendChild(optionsContainer);
  selectEl.parentNode.insertBefore(wrapper, selectEl);

  lucide.createIcons({ nodes: [wrapper] });
}

// Atualiza o select customizado quando o valor muda programaticamente (ex: ao carregar exemplo)
function refreshCustomSelect(selectEl) {
  const wrapper = selectEl.previousElementSibling;
  if (!wrapper || !wrapper.classList.contains("custom-select")) return;

  const selectedOpt = selectEl.options[selectEl.selectedIndex];
  const valueSpan = wrapper.querySelector(".custom-select-value");

  // Procura o ícone na opção selecionada (pode ser <i> ou <svg> após Lucide renderizar)
  const optionsContainer = wrapper.querySelector(".custom-select-options");
  const selectedOptionDiv = optionsContainer.querySelector(
    '.custom-option[data-value="' + selectedOpt.value + '"]',
  );
  const icon = selectedOptionDiv
    ? selectedOptionDiv.querySelector("svg, i")
    : null;

  valueSpan.innerHTML = "";
  if (icon) {
    valueSpan.appendChild(icon.cloneNode(true));
    valueSpan.appendChild(document.createTextNode(" " + selectedOpt.text));
  } else {
    valueSpan.textContent = selectedOpt.text;
  }

  // Atualiza qual opção está selected
  const options = optionsContainer.querySelectorAll(".custom-option");
  options.forEach((o) => {
    o.classList.toggle("selected", o.dataset.value === selectedOpt.value);
  });
}

document.addEventListener("click", () => {
  document
    .querySelectorAll(".custom-select.open")
    .forEach((s) => s.classList.remove("open"));
});

/* ── Freezer management ── */
// Adiciona um card de freezer no formulário com selects de marca, tipo, tecnologia, borracha e quantidade
function adicionarFreezer(
  marca = "",
  tipo = "EXIBICAO",
  tecnologia = "Convencional",
  estado = "Integra",
  qtd = 1,
) {
  freezerCount++;
  const id = freezerCount;

  const html = `
        <div class="freezer-item" id="freezer-${id}">
            <button type="button" class="btn-remove" onclick="removerFreezer(${id})">✕</button>
            <div class="row">
                <div>
                    <label>Marca</label>
                    <select class="marca-select" onchange="toggleMarcaCustom(${id})" data-id="${id}">
                        ${marcas.map((m) => `<option value="${m}" ${m === marca ? "selected" : ""}>${m}</option>`).join("")}
                    </select>
                    <div class="marca-custom ${marca === "Outro" ? "show" : ""}" id="marca-custom-${id}">
                        <input type="text" class="marca-input" placeholder="Digite a marca" value="">
                    </div>
                </div>
                <div>
                    <label>Tipo</label>
                    <select class="tipo-select">
                        <option value="EXIBICAO" ${tipo === "EXIBICAO" ? "selected" : ""}>Exibição</option>
                        <option value="ARMAZENAMENTO" ${tipo === "ARMAZENAMENTO" ? "selected" : ""}>Armazenamento</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div>
                    <label>Tecnologia</label>
                    <select class="tecnologia-select">
                        <option value="Convencional" ${tecnologia === "Convencional" ? "selected" : ""}>Convencional</option>
                        <option value="Inverter" ${tecnologia === "Inverter" ? "selected" : ""}>Inverter</option>
                    </select>
                </div>
                <div>
                    <label>Borracha</label>
                    <select class="borracha-select">
                        <option value="Integra" ${estado === "Integra" ? "selected" : ""}>Íntegra</option>
                        <option value="Gasta" ${estado === "Gasta" ? "selected" : ""}>Gasta</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div>
                    <label>Quantidade</label>
                    <input type="number" class="quantidade-input" min="1" value="${qtd}">
                </div>
            </div>
        </div>
    `;

  document
    .getElementById("freezersContainer")
    .insertAdjacentHTML("beforeend", html);

  const freezerEl = document.getElementById(`freezer-${id}`);
  freezerEl.querySelectorAll("select").forEach((sel) => initCustomSelect(sel));
}

// Remove um freezer com animação de fade out
function removerFreezer(id) {
  const el = document.getElementById(`freezer-${id}`);
  el.style.opacity = "0";
  el.style.transform = "translateX(20px)";
  el.style.transition = "all 0.2s ease";
  setTimeout(() => el.remove(), 200);
}

// Mostra/esconde o input de marca customizada quando seleciona "Outro"
function toggleMarcaCustom(id) {
  const select = document.querySelector(`#freezer-${id} .marca-select`);
  const custom = document.getElementById(`marca-custom-${id}`);
  custom.classList.toggle("show", select.value === "Outro");
}

// Lê todos os freezers do formulário e retorna como array de objetos para o JSON
function obterFreezers() {
  const items = document.querySelectorAll(".freezer-item");
  const freezers = [];

  items.forEach((item) => {
    const marcaSelect = item.querySelector(".marca-select");
    const marca =
      marcaSelect.value === "Outro"
        ? item.querySelector(".marca-input").value
        : marcaSelect.value;

    freezers.push({
      marca: marca || null,
      tipo: item.querySelector(".tipo-select").value,
      tecnologia: item.querySelector(".tecnologia-select").value,
      estado_borracha: item.querySelector(".borracha-select").value,
      quantidade: parseInt(item.querySelector(".quantidade-input").value) || 1,
    });
  });

  return freezers;
}

// Preenche o formulário com um dos 3 cenários pré-definidos (eficiente, moderado, ineficiente)
function carregarExemplo(numero) {
  document.getElementById("freezersContainer").innerHTML = "";
  freezerCount = 0;

  const ex = exemplos[numero];
  document.getElementById("consumoKwh").value = ex.consumo_kwh;
  document.getElementById("usoHorarioPico").value = ex.uso_horario_pico;
  document.getElementById("horasAltoConsumo").value = ex.horas_alto_consumo;
  document.getElementById("epocaAno").value = ex.epoca_ano;

  refreshCustomSelect(document.getElementById("usoHorarioPico"));
  refreshCustomSelect(document.getElementById("horasAltoConsumo"));
  refreshCustomSelect(document.getElementById("epocaAno"));

  ex.freezers.forEach((f) => {
    adicionarFreezer(
      f.marca,
      f.tipo,
      f.tecnologia,
      f.estado_borracha,
      f.quantidade,
    );
  });
}

/* ── Form submit ── */
// Botão "Adicionar Freezer" — adiciona um novo card vazio
document
  .getElementById("addFreezer")
  .addEventListener("click", () => adicionarFreezer());

// Submit do formulário — monta o JSON, envia POST para o backend e exibe o resultado
document.getElementById("analiseForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const freezers = obterFreezers();
  if (freezers.length === 0) {
    showToast("Adicione pelo menos um freezer!", "warning");
    return;
  }

  const cnpjFinal = cnpjAtual || chatState.dados?.cnpj || "";
  const nomeForm = document.getElementById("nomeResponsavel").value.trim();

  const dados = {
    consumo_kwh: parseFloat(document.getElementById("consumoKwh").value),
    uso_horario_pico:
      document.getElementById("usoHorarioPico").value === "true",
    horas_alto_consumo: document.getElementById("horasAltoConsumo").value,
    epoca_ano: document.getElementById("epocaAno").value.trim(),
    freezers: freezers,
    salvar: true,
    ...(cnpjFinal ? { cnpj: cnpjFinal } : {}),
    ...(nomeForm ? { nome: nomeForm } : {}),
    ...(inventarioAtualId ? { inventario_id: inventarioAtualId } : {}),
  };

  chatState.dados = { nome: nomeForm, cnpj: cnpjFinal };
  chatState.ultimoRequest = dados;

  const btn = e.target.querySelector('button[type="submit"]');
  btn.classList.add("loading");
  btn.disabled = true;

  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(dados),
    });

    if (!response.ok) {
      const erro = await response.text();
      throw new Error(`Servidor retornou erro ${response.status}: ${erro}`);
    }

    const resultado = await response.json();
    chatState.ultimaAnalise = resultado;
    chatState.ultimaAnaliseId = resultado.id;
    exibirResultado(resultado);
    setTimeout(abrirChatComPergunta, 1500);

    // Salva inventário se tem CNPJ
    if (cnpjFinal) {
      try {
        await fetch(INVENTARIO_API, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            cnpj: cnpjFinal,
            freezers_json: JSON.stringify(freezers),
          }),
        });
      } catch (e) {
        /* ignora erro de inventário */
      }
    }
  } catch (error) {
    console.error(error);
    document.getElementById("resultado").classList.add("hidden");
    showToast(
      "Não foi possível conectar ao servidor. Verifique se o backend está rodando em http://localhost:8080",
      "error",
      6000,
    );
  } finally {
    btn.classList.remove("loading");
    btn.disabled = false;
  }
});

// Anima um contador de 0 até o valor alvo com easing (usado para custo, economia etc.)
function animateCounter(el, target, prefix, suffix, duration) {
  prefix = prefix || "";
  suffix = suffix || "";
  duration = duration || 1500;
  var start = performance.now();
  var hasDecimal = String(target).includes(".");
  function update(now) {
    var elapsed = now - start;
    var progress = Math.min(elapsed / duration, 1);
    var eased = 1 - Math.pow(1 - progress, 3);
    var current = target * eased;
    var formatted = current.toFixed(hasDecimal ? 2 : 0).replace(".", ",");
    el.textContent = prefix + formatted + suffix;
    if (progress < 1) requestAnimationFrame(update);
  }
  requestAnimationFrame(update);
}

// Exibe o resultado da análise: categoria, custo, economia e recomendações com animação
// Classifica uma recomendação em categoria, ícone e label
function classificarRecomendacao(texto) {
  const t = texto.toLowerCase();
  if (
    t.includes("perfil") ||
    t.includes("parabens") ||
    t.includes("ineficiente") ||
    t.includes("moderado") ||
    t.includes("eficiente")
  )
    return { tipo: "perfil", icon: "shield-check", label: "Perfil" };
  if (t.includes("pico") || t.includes("18h") || t.includes("tarif"))
    return { tipo: "pico", icon: "clock", label: "Horário de Pico" };
  if (
    t.includes("movimento") ||
    t.includes("abertura dos freezers") ||
    t.includes("portas fechadas")
  )
    return { tipo: "movimento", icon: "door-open", label: "Movimento" };
  if (t.includes("manutencao") || t.includes("borracha"))
    return { tipo: "manutencao", icon: "wrench", label: "Manutenção" };
  if (t.includes("inverter"))
    return { tipo: "inverter", icon: "zap", label: "Tecnologia" };
  if (
    t.includes("epoca") ||
    t.includes("inverno") ||
    t.includes("verao") ||
    t.includes("primavera") ||
    t.includes("outono")
  )
    return { tipo: "epoca", icon: "sun", label: "Sazonal" };
  if (t.includes("consolide") || t.includes("menos freezers"))
    return { tipo: "consolidar", icon: "package", label: "Consolidação" };
  if (t.includes("consumo real") || t.includes("acima do esperado"))
    return { tipo: "consumo", icon: "alert-triangle", label: "Consumo" };
  return { tipo: "dica", icon: "lightbulb", label: "Dica" };
}

function exibirResultado(r) {
  const resultado = document.getElementById("resultado");
  resultado.classList.remove("hidden");

  const cards = resultado.querySelectorAll(".card");
  cards.forEach((card, i) => {
    card.style.opacity = "0";
    card.style.transform = "translateY(12px)";
    setTimeout(
      () => {
        card.style.transition = "opacity 0.4s ease, transform 0.4s ease";
        card.style.opacity = "1";
        card.style.transform = "translateY(0)";
      },
      100 + i * 120,
    );
  });

  const cat = document.getElementById("cardCategoria");
  cat.className = "card " + r.categoria;
  document.getElementById("categoria").textContent = r.categoria;

  // Animate gauge
  const probabilidade = r.probabilidade || 0;
  const gaugeFill = document.getElementById("gaugeFill");
  const gaugeValue = document.getElementById("gaugeValue");
  const arcLength = 141.37;
  const targetOffset = arcLength * (1 - probabilidade);

  gaugeFill.style.strokeDashoffset = arcLength;
  gaugeValue.textContent = "0%";

  setTimeout(() => {
    gaugeFill.style.strokeDashoffset = targetOffset;
    animateCounter(gaugeValue, Math.round(probabilidade * 100), "", "%", 1400);
  }, 400);

  setTimeout(function () {
    animateCounter(
      document.getElementById("custo"),
      r.custo_estimado_mensal,
      "R$ ",
      "",
      1500,
    );
    animateCounter(
      document.getElementById("economiaReais"),
      r.economia_potencial_reais,
      "R$ ",
      "",
      1500,
    );
    animateCounter(
      document.getElementById("economiaKwh"),
      r.economia_potencial_kwh,
      "",
      " kWh/mês de economia",
      1500,
    );
    const custoDiario = r.custo_estimado_mensal / 30;
    animateCounter(
      document.getElementById("custoDiario"),
      custoDiario,
      "~R$ ",
      "/dia",
      1500,
    );
  }, 350);

  const lista = document.getElementById("listaRecomendacoes");
  lista.innerHTML = "";
  if (r.recomendacoes && r.recomendacoes.length > 0) {
    r.recomendacoes.forEach((rec, i) => {
      const cat = classificarRecomendacao(rec);
      const li = document.createElement("li");
      li.className = "rec-" + cat.tipo;
      li.innerHTML =
        '<div class="rec-icon"><i data-lucide="' +
        cat.icon +
        '"></i></div>' +
        '<div class="rec-text">' +
        '<span class="rec-tag">' +
        cat.label +
        "</span>" +
        "<div>" +
        rec +
        "</div>" +
        "</div>";
      li.style.opacity = "0";
      li.style.transform = "translateY(8px)";
      setTimeout(
        () => {
          li.style.transition = "opacity 0.35s ease, transform 0.35s ease";
          li.style.opacity = "1";
          li.style.transform = "translateY(0)";
        },
        500 + i * 100,
      );
      lista.appendChild(li);
    });
    lucide.createIcons({ nodes: [lista] });
  } else {
    const li = document.createElement("li");
    li.className = "rec-dica";
    li.innerHTML =
      '<div class="rec-icon"><i data-lucide="check-circle"></i></div>' +
      '<div class="rec-text"><div>Nenhuma recomendação disponível. Seu perfil está otimizado!</div></div>';
    lista.appendChild(li);
    lucide.createIcons({ nodes: [lista] });
  }

  // Mostra botão de simulação após as recomendações
  const btnSimulacao = document.getElementById("btnSimulacao");
  if (r.freezers && r.freezers.length > 0) {
    btnSimulacao.classList.remove("hidden");
  } else {
    btnSimulacao.classList.add("hidden");
  }

  resultado.scrollIntoView({ behavior: "smooth", block: "start" });
}

adicionarFreezer();

/* ══════════════════════════════════════
   ── Modal CNPJ (Pre-loader) ──
   ══════════════════════════════════════ */
const INVENTARIO_API = "http://localhost:8080/api/inventario";
let cnpjAtual = null;
let inventarioAtualId = null;

// Formata CNPJ enquanto digita: XX.XXX.XXX/XXXX-XX
document.addEventListener("DOMContentLoaded", () => {
  const input = document.getElementById("cnpjInput");
  if (input) {
    input.addEventListener("input", (e) => {
      let v = e.target.value.replace(/\D/g, "").substring(0, 14);
      if (v.length > 12)
        v = v.replace(
          /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{0,2})/,
          "$1.$2.$3/$4-$5",
        );
      else if (v.length > 8)
        v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{0,4})/, "$1.$2.$3/$4");
      else if (v.length > 5)
        v = v.replace(/^(\d{2})(\d{3})(\d{0,3})/, "$1.$2.$3");
      else if (v.length > 2) v = v.replace(/^(\d{2})(\d{0,3})/, "$1.$2");
      e.target.value = v;
    });

    input.addEventListener("keydown", (e) => {
      if (e.key === "Enter") confirmarCnpj();
    });
  }
});

// Abre o modal CNPJ (esconde a LP com transição suave)
function abrirModalCnpj() {
  const lp = document.getElementById("landingPage");
  lp.classList.add("exiting");

  setTimeout(() => {
    lp.classList.add("hidden");
    lp.classList.remove("exiting");
    document.getElementById("snowfall").classList.add("hidden");
    clearInterval(snowflakeInterval);
    document.getElementById("cnpjModal").classList.remove("hidden");
    lucide.createIcons();
  }, 450);
}

// Valida CNPJ (algoritmo oficial com dígitos verificadores)
function validarCnpj(cnpj) {
  if (!/^\d{14}$/.test(cnpj)) return false;
  if (/^(\d)\1{13}$/.test(cnpj)) return false;

  const pesos1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const pesos2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

  let soma = 0;
  for (let i = 0; i < 12; i++) soma += parseInt(cnpj[i]) * pesos1[i];
  let resto = soma % 11;
  const dig1 = resto < 2 ? 0 : 11 - resto;
  if (parseInt(cnpj[12]) !== dig1) return false;

  soma = 0;
  for (let i = 0; i < 13; i++) soma += parseInt(cnpj[i]) * pesos2[i];
  resto = soma % 11;
  const dig2 = resto < 2 ? 0 : 11 - resto;
  return parseInt(cnpj[13]) === dig2;
}

// Confirma CNPJ: busca inventário e fecha modal
async function confirmarCnpj() {
  const input = document.getElementById("cnpjInput");
  const msg = document.getElementById("cnpjMsg");
  const cnpj = input.value.replace(/\D/g, "");

  if (cnpj.length !== 14 || !validarCnpj(cnpj)) {
    msg.className = "cnpj-msg error";
    msg.textContent = "CNPJ inválido. Verifique e tente novamente.";
    msg.classList.remove("hidden");
    input.style.borderColor = "#e74c3c";
    setTimeout(() => {
      input.style.borderColor = "";
      msg.classList.add("hidden");
    }, 2000);
    return;
  }

  cnpjAtual = cnpj;
  const btn = document.getElementById("cnpjBtn");
  btn.innerHTML = '<span class="cnpj-spinner"></span>';
  btn.disabled = true;

  try {
    const response = await fetch(`${INVENTARIO_API}?cnpj=${cnpj}`);
    if (response.ok) {
      const data = await response.json();
      inventarioAtualId = data.id;
      const freezers = JSON.parse(data.freezers_json);
      preencherFreezers(freezers);

      msg.className = "cnpj-msg success";
      msg.textContent = "Setup carregado com sucesso!";
      msg.classList.remove("hidden");

      setTimeout(() => fecharModalCnpj(), 1000);
      return;
    }

    // 404 — CNPJ não encontrado → mostra tela de criação de setup
    msg.className = "cnpj-msg info";
    msg.innerHTML =
      "<strong>CNPJ não encontrado.</strong> Crie seu setup de freezers abaixo.";
    msg.classList.remove("hidden");

    setTimeout(() => {
      document.getElementById("cnpjTela1").classList.add("hidden");
      document.getElementById("cnpjTela2").classList.remove("hidden");
      setupFreezerCount = 0;
      adicionarSetupFreezer();
      lucide.createIcons();
    }, 1200);
  } catch (e) {
    fecharModalCnpj();
  }
}

// Pula o CNPJ — fecha modal sem buscar inventário
function pularCnpj() {
  cnpjAtual = null;
  inventarioAtualId = null;
  fecharModalCnpj();
}

// Fecha o modal CNPJ e mostra o formulário de análise com transição suave
function fecharModalCnpj() {
  const btn = document.getElementById("cnpjBtn");
  btn.innerHTML = '<i data-lucide="arrow-right"></i>';
  btn.disabled = false;
  document.getElementById("cnpjModal").classList.add("hidden");
  // Reseta para tela 1
  document.getElementById("cnpjTela1").classList.remove("hidden");
  document.getElementById("cnpjTela2").classList.add("hidden");
  document.getElementById("cnpjMsg").classList.add("hidden");
  // Mostra o formulário de análise com animação de entrada
  const container = document.getElementById("mainContainer");
  container.classList.remove("hidden");
  container.classList.add("entering");
  setTimeout(() => container.classList.remove("entering"), 500);
  document.getElementById("chatbotToggle").classList.remove("hidden");
  // Mostra botão "Meu Inventário" se tem CNPJ
  if (cnpjAtual) {
    document.getElementById("btnMeuInventario").classList.remove("hidden");
  }
  lucide.createIcons();
}

/* ══════════════════════════════════════
   ── Tela 2: Criação de Setup ──
   ══════════════════════════════════════ */
let setupFreezerCount = 0;

function adicionarSetupFreezer() {
  const container = document.getElementById("setupFreezerContainer");
  const idx = setupFreezerCount++;
  const div = document.createElement("div");
  div.className = "setup-freezer-item";
  div.dataset.index = idx;
  div.innerHTML = `
    <div class="setup-freezer-header">
      <span class="setup-freezer-num">Freezer ${idx + 1}</span>
      <button type="button" class="setup-freezer-remove" onclick="removerSetupFreezer(${idx})">
        <i data-lucide="trash-2" style="width:14px;height:14px"></i>
      </button>
    </div>
    <div class="setup-freezer-fields">
      <div class="setup-field">
        <label>Marca</label>
        <select class="setup-marca">
          ${marcas.map((m) => `<option value="${m}">${m}</option>`).join("")}
        </select>
      </div>
      <div class="setup-field">
        <label>Tipo</label>
        <select class="setup-tipo">
          <option value="EXIBICAO">Exibição</option>
          <option value="ARMAZENAMENTO">Armazenamento</option>
        </select>
      </div>
      <div class="setup-field">
        <label>Tecnologia</label>
        <select class="setup-tecnologia">
          <option value="CONVENCIONAL">Convencional</option>
          <option value="INVERTER">Inverter</option>
        </select>
      </div>
      <div class="setup-field">
        <label>Borracha</label>
        <select class="setup-borracha">
          <option value="INTEGRA">Íntegra</option>
          <option value="GASTA">Gasta</option>
        </select>
      </div>
      <div class="setup-field">
        <label>Quantidade</label>
        <input type="number" class="setup-quantidade" value="1" min="1" max="20">
      </div>
    </div>
  `;
  container.appendChild(div);
  lucide.createIcons({ nodes: [div] });
  div.querySelectorAll("select").forEach((s) => initCustomSelect(s));
}

function removerSetupFreezer(idx) {
  const container = document.getElementById("setupFreezerContainer");
  const item = container.querySelector(`[data-index="${idx}"]`);
  if (item) item.remove();
}

function obterSetupFreezers() {
  const items = document.querySelectorAll(
    "#setupFreezerContainer .setup-freezer-item",
  );
  const freezers = [];
  items.forEach((item) => {
    freezers.push({
      marca: item.querySelector(".setup-marca").value,
      tipo: item.querySelector(".setup-tipo").value,
      tecnologia: item.querySelector(".setup-tecnologia").value,
      estado_borracha: item.querySelector(".setup-borracha").value,
      quantidade: parseInt(item.querySelector(".setup-quantidade").value) || 1,
    });
  });
  return freezers;
}

// Salva setup no inventário e preenche formulário de análise
async function salvarSetup() {
  const freezers = obterSetupFreezers();
  if (freezers.length === 0) {
    showToast("Adicione pelo menos um freezer!", "warning");
    return;
  }

  // Salva inventário
  if (cnpjAtual) {
    try {
      const res = await fetch(INVENTARIO_API, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          cnpj: cnpjAtual,
          freezers_json: JSON.stringify(freezers),
        }),
      });
      if (res.ok) {
        const data = await res.json();
        inventarioAtualId = data.id;
      }
    } catch (e) {
      /* ignora */
    }
  }

  preencherFreezers(freezers);
  fecharModalCnpj();
}

// Pula criação de setup — fecha modal sem preencher
function pularSetup() {
  fecharModalCnpj();
}

/* ══════════════════════════════════════
   ── Sidebar: Meu Inventário ──
   ══════════════════════════════════════ */
let inventarioFreezerCount = 0;

// Abre a sidebar do inventário e carrega os dados
async function abrirInventario() {
  if (!cnpjAtual) return;

  const container = document.getElementById("inventarioFreezerContainer");
  container.innerHTML = "";
  inventarioFreezerCount = 0;

  // Busca inventário do backend
  try {
    const response = await fetch(`${INVENTARIO_API}?cnpj=${cnpjAtual}`);
    if (response.ok) {
      const data = await response.json();
      inventarioAtualId = data.id;
      const freezers = JSON.parse(data.freezers_json);
      freezers.forEach((f) => {
        adicionarInventarioFreezer(
          f.marca,
          f.tipo,
          f.tecnologia,
          f.estado_borracha,
          f.quantidade,
        );
      });
    }
  } catch (e) {
    console.error("Erro ao carregar inventário:", e);
  }

  // Abre sidebar e overlay
  document.getElementById("inventarioOverlay").classList.remove("hidden");
  const sidebar = document.getElementById("inventarioSidebar");
  sidebar.classList.remove("hidden");
  requestAnimationFrame(() => {
    sidebar.classList.add("open");
  });
  lucide.createIcons();
}

// Fecha a sidebar do inventário
function fecharInventario() {
  const sidebar = document.getElementById("inventarioSidebar");
  sidebar.classList.remove("open");
  setTimeout(() => {
    sidebar.classList.add("hidden");
    document.getElementById("inventarioOverlay").classList.add("hidden");
  }, 300);
}

// Adiciona um card de freezer na sidebar do inventário
function adicionarInventarioFreezer(
  marca = "",
  tipo = "EXIBICAO",
  tecnologia = "Convencional",
  estado = "Integra",
  qtd = 1,
) {
  const container = document.getElementById("inventarioFreezerContainer");
  const idx = inventarioFreezerCount++;
  const div = document.createElement("div");
  div.className = "inventario-freezer-item";
  div.dataset.index = idx;
  div.innerHTML = `
    <div class="inventario-freezer-header">
      <span class="inventario-freezer-num">Freezer ${idx + 1}</span>
      <button type="button" class="inventario-freezer-remove" onclick="removerInventarioFreezer(${idx})">
        <i data-lucide="trash-2" style="width:14px;height:14px"></i>
      </button>
    </div>
    <div class="inventario-freezer-fields">
      <div class="inventario-field">
        <label>Marca</label>
        <select class="inventario-marca">
          ${marcas.map((m) => `<option value="${m}" ${m === marca ? "selected" : ""}>${m}</option>`).join("")}
        </select>
      </div>
      <div class="inventario-field">
        <label>Tipo</label>
        <select class="inventario-tipo">
          <option value="EXIBICAO" ${tipo === "EXIBICAO" ? "selected" : ""}>Exibição</option>
          <option value="ARMAZENAMENTO" ${tipo === "ARMAZENAMENTO" ? "selected" : ""}>Armazenamento</option>
        </select>
      </div>
      <div class="inventario-field">
        <label>Tecnologia</label>
        <select class="inventario-tecnologia">
          <option value="Convencional" ${tecnologia === "Convencional" || tecnologia === "CONVENCIONAL" ? "selected" : ""}>Convencional</option>
          <option value="Inverter" ${tecnologia === "Inverter" || tecnologia === "INVERTER" ? "selected" : ""}>Inverter</option>
        </select>
      </div>
      <div class="inventario-field">
        <label>Borracha</label>
        <select class="inventario-borracha">
          <option value="Integra" ${estado === "Integra" || estado === "INTEGRA" ? "selected" : ""}>Íntegra</option>
          <option value="Gasta" ${estado === "Gasta" || estado === "GASTA" ? "selected" : ""}>Gasta</option>
        </select>
      </div>
      <div class="inventario-field">
        <label>Quantidade</label>
        <input type="number" class="inventario-quantidade" value="${qtd}" min="1" max="20">
      </div>
    </div>
  `;
  container.appendChild(div);
  lucide.createIcons({ nodes: [div] });
  div.querySelectorAll("select").forEach((s) => initCustomSelect(s));
}

// Remove um freezer da sidebar do inventário
function removerInventarioFreezer(idx) {
  const container = document.getElementById("inventarioFreezerContainer");
  const item = container.querySelector(`[data-index="${idx}"]`);
  if (item) {
    item.style.opacity = "0";
    item.style.transform = "translateX(-20px)";
    item.style.transition = "all 0.2s ease";
    setTimeout(() => item.remove(), 200);
  }
}

// Lê todos os freezers da sidebar do inventário
function obterInventarioFreezers() {
  const items = document.querySelectorAll(
    "#inventarioFreezerContainer .inventario-freezer-item",
  );
  const freezers = [];
  items.forEach((item) => {
    freezers.push({
      marca: item.querySelector(".inventario-marca").value,
      tipo: item.querySelector(".inventario-tipo").value,
      tecnologia: item.querySelector(".inventario-tecnologia").value,
      estado_borracha: item.querySelector(".inventario-borracha").value,
      quantidade:
        parseInt(item.querySelector(".inventario-quantidade").value) || 1,
    });
  });
  return freezers;
}

// Salva inventário no backend e atualiza o formulário principal
async function salvarInventario() {
  const freezers = obterInventarioFreezers();
  if (freezers.length === 0) {
    showToast("Adicione pelo menos um freezer!", "warning");
    return;
  }

  if (!cnpjAtual) {
    showToast("Nenhum CNPJ configurado.", "warning");
    return;
  }

  try {
    const res = await fetch(INVENTARIO_API, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        cnpj: cnpjAtual,
        freezers_json: JSON.stringify(freezers),
      }),
    });
    if (res.ok) {
      const data = await res.json();
      inventarioAtualId = data.id;
      // Atualiza o formulário principal com os freezers salvos
      preencherFreezers(freezers);
      fecharInventario();
      showToast("Inventário salvo com sucesso!", "success");
    } else {
      showToast("Erro ao salvar inventário. Tente novamente.", "error");
    }
  } catch (e) {
    console.error("Erro ao salvar inventário:", e);
    showToast(
      "Erro ao conectar ao servidor. Verifique se o backend está rodando.",
      "error",
    );
  }
}

// Preenche o formulário com os freezers do inventário
function preencherFreezers(freezers) {
  const container = document.getElementById("freezersContainer");
  container.innerHTML = "";
  freezerCount = 0;

  const tecnologiaMap = { CONVENCIONAL: "Convencional", INVERTER: "Inverter" };
  const borrachaMap = { INTEGRA: "Integra", GASTA: "Gasta" };

  freezers.forEach((f) => {
    adicionarFreezer();
    const idx = freezerCount - 1;
    const card = container.children[idx];
    if (!card) return;

    const setVal = (sel, val) => {
      const el = card.querySelector(sel);
      if (el) {
        el.value = val;
        if (el.tagName === "SELECT") refreshCustomSelect(el);
      }
    };

    setVal(".marca-select", f.marca || "");
    setVal(".tipo-select", f.tipo || "EXIBICAO");
    setVal(
      ".tecnologia-select",
      tecnologiaMap[f.tecnologia] || f.tecnologia || "Convencional",
    );
    setVal(
      ".borracha-select",
      borrachaMap[f.estado_borracha] || f.estado_borracha || "Integra",
    );
    setVal(".quantidade-input", f.quantidade || 1);
  });
}

/* ══════════════════════════════════════
   ── Modal de Simulação ──
   ══════════════════════════════════════ */
let simulacaoAnaliseAnterior = null;
let simulacaoRequestAnterior = null;
let simulacaoMelhorias = [];

// Abre o modal com o novo design
function abrirModalSimulacao(analise, request) {
  simulacaoAnaliseAnterior = analise;
  simulacaoRequestAnterior = request;
  // Melhorias vêm do backend
  simulacaoMelhorias = (analise.melhorias || []).map((m) => ({
    id: m.id,
    tipo: m.tipo,
    nome: m.nome,
    desc: m.desc,
    icon: m.icon,
    impacto: m.impacto,
    impacto_por_freezer: m.impacto_por_freezer || null,
    impacto_max: m.impacto_max || null,
    qtd_afetada: m.qtd_afetada || null,
    reducao: m.impacto,
  }));
  const modal = document.getElementById("simulacaoModal");

  // Esconde comparação
  document.getElementById("simulacaoComparacao").classList.add("hidden");
  document.getElementById("comparacaoEconomia").classList.add("hidden");

  // Preenche resumo
  document.getElementById("simCustoAtual").textContent =
    "R$ " + analise.custo_estimado_mensal.toFixed(2);
  document.getElementById("simPerfilAtual").textContent = analise.categoria;
  document.getElementById("simPerfilAtual").className =
    "sim-resumo-valor comparacao-categoria " + analise.categoria;
  document.getElementById("simQtdFreezers").textContent = (
    analise.freezers || []
  ).length;

  // Renderiza cards de melhorias
  const container = document.getElementById("simMelhorias");
  container.innerHTML = "";

  if (simulacaoMelhorias.length === 0) {
    container.innerHTML =
      '<p style="color:#888;font-size:0.85rem;text-align:center;padding:16px 0">Nenhuma melhoria disponível. Seu perfil já está otimizado!</p>';
  } else {
    simulacaoMelhorias.forEach((m) => {
      const card = document.createElement("div");
      card.className = "sim-melhoria-card";
      card.dataset.id = m.id;

      let controleHtml;
      if (m.qtd_afetada && m.qtd_afetada > 0) {
        controleHtml = `
          <div class="sim-stepper">
            <button class="sim-stepper-btn" onclick="stepperChange('${m.id}', -1)">−</button>
            <span class="sim-stepper-val" id="stepper-${m.id}">0</span>
            <button class="sim-stepper-btn" onclick="stepperChange('${m.id}', 1)">+</button>
            <span class="sim-stepper-max">/ ${m.qtd_afetada}</span>
          </div>`;
      } else {
        controleHtml = `
          <label class="sim-toggle">
            <input type="checkbox" data-melhoria-id="${m.id}" onchange="onMelhoriaToggle(this)">
            <span class="sim-toggle-slider"></span>
          </label>`;
      }

      const custoKwh = simulacaoAnaliseAnterior
        ? simulacaoAnaliseAnterior.custo_estimado_mensal / 0.75
        : 0;
      const economiaKwh = m.impacto_por_freezer
        ? (m.impacto_por_freezer * custoKwh).toFixed(1)
        : null;
      const percLabel = m.impacto_por_freezer
        ? (m.impacto_por_freezer * 100).toFixed(1).replace(".", ",") + "%"
        : null;

      card.innerHTML = `
        <div class="sim-melhoria-icon ${m.tipo}">${m.icon}</div>
        <div class="sim-melhoria-texto">
          <div class="sim-melhoria-nome">${m.nome}</div>
          <div class="sim-melhoria-desc">${m.desc}</div>
          ${percLabel ? `<div class="sim-melhoria-nota">Economia por freezer: ~${percLabel} (${economiaKwh} kWh/mês)</div>` : ""}
        </div>
        ${controleHtml}
      `;
      container.appendChild(card);
    });
  }

  // Renderiza freezers na seção avançada
  renderizarFreezersAvancados(analise.freezers || []);

  modal.classList.remove("hidden");
  lucide.createIcons({ nodes: [modal] });
}

// Renderiza freezers editáveis (seção avançada)
function renderizarFreezersAvancados(freezers) {
  const container = document.getElementById("simFreezersAvancado");
  container.innerHTML = "";
  freezers.forEach((f, i) => {
    const tipoLabel = f.tipo === "EXIBICAO" ? "Exibição" : "Armazenamento";
    const techLabel = f.tecnologia === "Inverter" ? "Inverter" : "Convencional";
    const borrachaLabel = f.estado_borracha === "Integra" ? "Íntegra" : "Gasta";
    container.innerHTML += `
      <div class="sim-freezer-item" data-index="${i}">
        <div class="sim-freezer-header">
          <span class="sim-freezer-num">Freezer ${i + 1}</span>
          <span class="sim-freezer-qtd">${f.quantidade || 1}x</span>
        </div>
        <div class="sim-freezer-tags">
          <span class="sim-tag">${tipoLabel}</span>
          <span class="sim-tag">${techLabel}</span>
          <span class="sim-tag">Borracha ${borrachaLabel}</span>
        </div>
      </div>
    `;
  });
}

// Toggle da seção avançada de freezers
function toggleFreezersAvancado() {
  const section = document.getElementById("simFreezersAvancado");
  const icon = document.getElementById("iconFreezersAvancado");
  section.classList.toggle("hidden");
  icon.style.transform = section.classList.contains("hidden")
    ? ""
    : "rotate(180deg)";
}

// Calcula o custo simulado aplicando os percentuais das melhorias ativas
function calcularCustoSimulado() {
  if (!simulacaoAnaliseAnterior || !simulacaoRequestAnterior) return;

  const custoOriginal = simulacaoAnaliseAnterior.custo_estimado_mensal;
  let fatorReducao = 1.0;

  // Percorre melhorias e aplica o impacto correto
  simulacaoMelhorias.forEach((m) => {
    if (m.qtd_afetada && m.qtd_afetada > 0) {
      // Stepper: lê valor selecionado e calcula impacto dinâmico
      const el = document.getElementById("stepper-" + m.id);
      const val = el ? parseInt(el.textContent) : 0;
      if (val > 0 && m.impacto_por_freezer) {
        const impacto = Math.min(
          m.impacto_por_freezer * val,
          m.impacto_max || 1.0,
        );
        fatorReducao *= 1 - impacto;
      }
    } else {
      // Toggle: usa impacto fixo
      const checkbox = document.querySelector(
        `.sim-toggle input[data-melhoria-id="${m.id}"]`,
      );
      if (checkbox && checkbox.checked) {
        fatorReducao *= 1 - (m.impacto || 0);
      }
    }
  });

  return custoOriginal * fatorReducao;
}

// Atualiza a comparação na tela
function atualizarComparacao(custoNovo) {
  const custoOriginal = simulacaoAnaliseAnterior.custo_estimado_mensal;
  const perfilOriginal = simulacaoAnaliseAnterior.categoria;

  const comp = document.getElementById("simulacaoComparacao");
  comp.classList.remove("hidden");

  document.getElementById("antesCusto").textContent =
    "R$ " + custoOriginal.toFixed(2);

  document.getElementById("depoisCusto").textContent =
    "R$ " + custoNovo.toFixed(2);

  const diff = custoOriginal - custoNovo;
  const economiaEl = document.getElementById("comparacaoEconomia");
  economiaEl.style.background = "";
  economiaEl.style.borderColor = "";
  economiaEl.style.color = "";

  if (diff > 0.01) {
    economiaEl.textContent = "Economia de R$ " + diff.toFixed(2) + "/mês";
    economiaEl.classList.remove("hidden");
  } else if (diff < -0.01) {
    economiaEl.textContent =
      "Aumento de R$ " + Math.abs(diff).toFixed(2) + "/mês";
    economiaEl.style.background = "rgba(231, 76, 60, 0.1)";
    economiaEl.style.borderColor = "rgba(231, 76, 60, 0.2)";
    economiaEl.style.color = "#e74c3c";
    economiaEl.classList.remove("hidden");
  } else {
    economiaEl.textContent = "Sem alteração no custo.";
    economiaEl.classList.remove("hidden");
  }
}

// Callback quando um toggle de melhoria é ativado/desativado
function onMelhoriaToggle(checkbox) {
  const melhoriaId = checkbox.dataset.melhoriaId;
  const card = checkbox.closest(".sim-melhoria-card");

  if (checkbox.checked) {
    card.classList.add("ativa");
  } else {
    card.classList.remove("ativa");
  }

  const custoSimulado = calcularCustoSimulado();
  atualizarComparacao(custoSimulado);
}

// Callback do stepper de quantidade por freezer
function stepperChange(melhoriaId, delta) {
  const el = document.getElementById("stepper-" + melhoriaId);
  if (!el) return;
  const melhoria = simulacaoMelhorias.find((m) => m.id === melhoriaId);
  if (!melhoria) return;

  let val = parseInt(el.textContent) + delta;
  val = Math.max(0, Math.min(val, melhoria.qtd_afetada || 0));
  el.textContent = val;

  // Atualiza estilo do card
  const card = el.closest(".sim-melhoria-card");
  if (card) {
    card.classList.toggle("ativa", val > 0);
  }

  const custoSimulado = calcularCustoSimulado();
  atualizarComparacao(custoSimulado);
}

// Fecha o modal
function fecharModal() {
  document.getElementById("simulacaoModal").classList.add("hidden");
  simulacaoAnaliseAnterior = null;
  simulacaoRequestAnterior = null;
  simulacaoMelhorias = [];
}

// Botão "Simular" — aplica e calcula
/* ══════════════════════════════════════
   ── Chatbot ──
   ══════════════════════════════════════ */
// Estado do chatbot: controla o fluxo de conversa (idle → pedindo_nome → pedindo_cnpj → pronto)
let chatState = {
  step: "idle",
  dados: null,
  ultimoRequest: null,
  ultimaAnaliseId: null,
};

const chatbotToggle = document.getElementById("chatbotToggle");
const chatbotWindow = document.getElementById("chatbotWindow");
const chatbotClose = document.getElementById("chatbotClose");
const chatbotMessages = document.getElementById("chatbotMessages");
const chatbotInput = document.getElementById("chatbotInput");
const chatbotSend = document.getElementById("chatbotSend");

chatbotToggle.addEventListener("click", () => {
  chatbotWindow.classList.toggle("hidden");
  if (!chatbotWindow.classList.contains("hidden")) {
    chatbotInput.focus();
    if (chatbotMessages.children.length === 0) {
      const cnpjDisponivel = chatState.dados?.cnpj || cnpjAtual;
      const nomeDisponivel =
        chatState.dados?.nome ||
        document.getElementById("nomeResponsavel").value.trim();

      if (cnpjDisponivel) {
        chatState.dados = { nome: nomeDisponivel, cnpj: cnpjDisponivel };
        chatState.step = "idle";
        const nome = nomeDisponivel || "parceiro";
        adicionarMsgChat(
          `Olá, <b>${nome}</b>! 🍦<br><br>Gostaria de consultar o seu <b>histórico de análises</b>? (sim/não)`,
        );
      } else {
        chatState.step = "idle";
        adicionarMsgChat(
          "Olá! 👋<br><br>Bem-vindo ao <b>ChillyWatts</b>. Para consultar seu histórico, vou precisar do <b>CNPJ</b> da sorveteria.<br><br>Caso ainda não tenha feito uma análise, preencha o formulário e seus dados serão salvos automaticamente.",
        );
      }
    }
  }
});

chatbotClose.addEventListener("click", () => {
  chatbotWindow.classList.add("hidden");
});

chatbotSend.addEventListener("click", enviarMensagemChat);
chatbotInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter") enviarMensagemChat();
});

// Adiciona uma mensagem no chat (bot ou usuário)
function adicionarMsgChat(texto, tipo = "bot") {
  const div = document.createElement("div");
  div.className = `chat-msg ${tipo}`;
  div.innerHTML = texto;
  chatbotMessages.appendChild(div);
  chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
}

// Mostra animação de "digitando..." enquanto o bot processa
function mostrarDigitando() {
  const div = document.createElement("div");
  div.className = "chat-msg bot digitando";
  div.innerHTML =
    '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
  chatbotMessages.appendChild(div);
  chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
  return div;
}

function removerDigitando(el) {
  if (el && el.parentNode) el.remove();
}

// Renderiza um card de análise no chat com data, responsável, perfil, consumo e custo
function adicionarHistoricoChat(item) {
  const data = new Date(item.dataAnalise).toLocaleDateString("pt-BR");
  const div = document.createElement("div");
  div.className = "chat-msg hist-item";

  // Prepara os freezers para o modal de simulação
  let freezersList = [];
  try {
    freezersList = item.freezersJson ? JSON.parse(item.freezersJson) : [];
  } catch (e) {
    freezersList = [];
  }

  // Monta resumo dos freezers
  let freezerSummary = "";
  if (freezersList.length > 0) {
    freezerSummary = freezersList
      .map(
        (f, i) =>
          `${i + 1}. ${f.quantidade || 1}x ${f.tipo === "EXIBICAO" ? "Exibição" : "Armazenamento"} ${f.tecnologia} (${f.estado_borracha})`,
      )
      .join("<br>");
  } else {
    freezerSummary = "Sem dados de freezers";
  }

  div.innerHTML = `
        <div class="hist-header">${data}</div>
        <div class="hist-row">
            <span>Responsável:</span>
            <span>${item.nome || "—"}</span>
        </div>
        <div class="hist-row">
            <span>Perfil:</span>
            <span class="hist-categoria ${item.perfilEnergetico}">${item.perfilEnergetico}</span>
        </div>
        <div class="hist-row">
            <span>Consumo:</span>
            <span>${item.consumoRealKwh} kWh</span>
        </div>
        <div class="hist-row">
            <span>Custo:</span>
            <span>R$ ${item.custoMensalAtual.toFixed(2)}</span>
        </div>
        <div class="hist-freezers">
            <span>Freezers:</span>
            <div class="hist-freezer-list">${freezerSummary}</div>
        </div>
    `;
  chatbotMessages.appendChild(div);
  chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
}

// Busca o histórico de análises no backend por CNPJ
async function buscarHistoricoBackend(cnpj) {
  try {
    const url = `${HISTORICO_API}/por-cnpj?cnpj=${encodeURIComponent(cnpj)}`;
    console.log("Buscando histórico:", url);
    const response = await fetch(url);
    if (!response.ok) {
      console.error(
        "Erro ao buscar histórico:",
        response.status,
        await response.text(),
      );
      return [];
    }
    const data = await response.json();
    console.log("Histórico encontrado:", data.length, "registros");
    return data;
  } catch (e) {
    console.error("Falha ao buscar histórico:", e);
    return [];
  }
}

function enviarMensagemChat() {
  const texto = chatbotInput.value.trim();
  if (!texto) return;

  adicionarMsgChat(texto, "user");
  chatbotInput.value = "";

  setTimeout(() => processarRespostaChat(texto), 400);
}

// Máquina de estados do chatbot: processa a resposta do usuário e direciona o fluxo
// Estados: idle → pedindo_nome → pedindo_cnpj → pronto
function processarRespostaChat(texto) {
  const lower = texto.toLowerCase();

  function responder(msg, delay = 800) {
    const digitando = mostrarDigitando();
    setTimeout(() => {
      removerDigitando(digitando);
      adicionarMsgChat(msg);
    }, delay);
  }

  switch (chatState.step) {
    case "idle":
      if (
        lower.includes("sim") ||
        lower.includes("histórico") ||
        lower.includes("historico")
      ) {
        if (chatState.dados && chatState.dados.cnpj) {
          chatState.step = "pronto";
          const nome = chatState.dados.nome || "parceiro";
          adicionarMsgChat("Buscando seu histórico... ⏳");
          buscarHistoricoBackend(chatState.dados.cnpj).then((h) => {
            if (h.length > 0) {
              adicionarMsgChat(`Encontrei <b>${h.length}</b> análise(s) suas:`);
              h.slice(-5)
                .reverse()
                .forEach((item) => adicionarHistoricoChat(item));
            } else {
              adicionarMsgChat(`Nenhuma análise encontrada para este CNPJ.`);
            }
          });
        } else {
          chatState.step = "pedindo_cnpj";
          responder(
            "Informe o <b>CNPJ</b> da sorveteria para buscar o histórico.",
          );
        }
      } else if (lower.includes("não") || lower.includes("nao")) {
        responder(
          "Tudo bem! Se precisar depois, é só clicar no botão 💬 novamente.",
        );
        setTimeout(() => {
          chatbotWindow.classList.add("hidden");
          chatbotMessages.innerHTML = "";
        }, 4500);
      } else {
        // Tenta interpretar como CNPJ
        const cnpjLimpo = texto.replace(/\D/g, "");
        if (cnpjLimpo.length === 14 && validarCnpj(cnpjLimpo)) {
          chatState.dados = {
            nome: chatState.dados?.nome || "",
            cnpj: cnpjLimpo,
          };
          chatState.step = "pronto";
          cnpjAtual = cnpjLimpo;
          if (
            !document
              .getElementById("mainContainer")
              .classList.contains("hidden")
          ) {
            document
              .getElementById("btnMeuInventario")
              .classList.remove("hidden");
          }

          async function idleAtualizarEbuscar() {
            if (chatState.ultimaAnaliseId) {
              try {
                await fetch(`${PATCH_API_URL}/${chatState.ultimaAnaliseId}`, {
                  method: "PATCH",
                  headers: { "Content-Type": "application/json" },
                  body: JSON.stringify({ cnpj: cnpjLimpo }),
                });
              } catch (e) {
                console.error("Erro ao atualizar análise:", e);
              }
            }
            return buscarHistoricoBackend(cnpjLimpo);
          }

          responder(
            `CNPJ registrado! 🍦<br><br>Agora você pode criar seu inventário clicando em <b>"Meu Inventário"</b> no canto superior esquerdo!`,
            600,
          );
          setTimeout(() => {
            idleAtualizarEbuscar().then((h) => {
              if (h.length > 0) {
                adicionarMsgChat(
                  `Encontrei <b>${h.length}</b> análise(s). Veja abaixo:`,
                );
                h.slice(-5)
                  .reverse()
                  .forEach((item) => adicionarHistoricoChat(item));
              } else {
                adicionarMsgChat(`Nenhuma análise encontrada para este CNPJ.`);
              }
            });
          }, 1400);
        } else {
          responder(
            "Para consultar o histórico, informe o <b>CNPJ</b> da sorveteria ou digite <b>sim</b>.",
          );
        }
      }
      break;

    case "pedindo_cnpj": {
      const cnpjLimpo = texto.replace(/\D/g, "");
      if (cnpjLimpo.length !== 14 || !validarCnpj(cnpjLimpo)) {
        responder(
          "CNPJ inválido. Por favor, digite os <b>14 números</b> do CNPJ da sorveteria.",
        );
        return;
      }
      chatState.dados = { nome: chatState.dados?.nome || "", cnpj: cnpjLimpo };
      chatState.step = "pronto";
      cnpjAtual = cnpjLimpo;
      // Mostra botão "Meu Inventário" se estiver no formulário principal
      if (
        !document.getElementById("mainContainer").classList.contains("hidden")
      ) {
        document.getElementById("btnMeuInventario").classList.remove("hidden");
      }

      async function atualizarEbuscar() {
        if (chatState.ultimaAnaliseId) {
          try {
            await fetch(`${PATCH_API_URL}/${chatState.ultimaAnaliseId}`, {
              method: "PATCH",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ cnpj: chatState.dados.cnpj }),
            });
          } catch (e) {
            console.error("Erro ao atualizar análise:", e);
          }
        }
        return buscarHistoricoBackend(chatState.dados.cnpj);
      }

      responder(`CNPJ registrado! 🍦<br><br>Buscando suas análises... ⏳`, 600);
      setTimeout(() => {
        atualizarEbuscar().then((historico) => {
          if (historico.length > 0) {
            adicionarMsgChat(
              `Encontrei <b>${historico.length}</b> análise(s). Veja abaixo:`,
            );
            historico
              .slice(-5)
              .reverse()
              .forEach((h) => adicionarHistoricoChat(h));
          } else {
            adicionarMsgChat(`Nenhuma análise encontrada para este CNPJ.`);
          }
          adicionarMsgChat(
            `Dica: clique em <b>"Meu Inventário"</b> no canto superior esquerdo para cadastrar os freezers da sua sorveteria! 🍦`,
          );
        });
      }, 1400);
      break;
    }

    case "pronto":
      if (
        lower.includes("histórico") ||
        lower.includes("historico") ||
        lower.includes("ver") ||
        lower.includes("consultar")
      ) {
        const digitando = mostrarDigitando();
        setTimeout(() => {
          removerDigitando(digitando);
          adicionarMsgChat("Buscando seu histórico... ⏳");
          buscarHistoricoBackend(chatState.dados.cnpj).then((h2) => {
            if (h2.length > 0) {
              adicionarMsgChat(`Você tem <b>${h2.length}</b> análise(s):`);
              h2.slice(-5)
                .reverse()
                .forEach((h) => adicionarHistoricoChat(h));
            } else {
              adicionarMsgChat("Nenhuma análise encontrada ainda.");
            }
          });
        }, 800);
      } else if (lower.includes("sair") || lower.includes("obrigad")) {
        chatState.step = "idle";
        chatState.dados = null;
        responder("Até logo! Se precisar, é só clicar no botão 💬. 👋");
        setTimeout(() => {
          chatbotWindow.classList.add("hidden");
          chatbotMessages.innerHTML = "";
        }, 2500);
      } else {
        responder(
          'Digite <b>"histórico"</b> para ver suas análises ou <b>"sair"</b> para encerrar.',
        );
      }
      break;
  }
}

// Abre o chat automaticamente após uma análise
function abrirChatComPergunta() {
  chatbotWindow.classList.remove("hidden");
  chatbotInput.focus();
  chatbotMessages.innerHTML = "";

  const cnpjDisponivel = chatState.dados?.cnpj || cnpjAtual;
  const nomeDisponivel =
    chatState.dados?.nome ||
    document.getElementById("nomeResponsavel").value.trim();

  if (cnpjDisponivel) {
    chatState.dados = { nome: nomeDisponivel, cnpj: cnpjDisponivel };
    chatState.step = "idle";
    const nome = nomeDisponivel || "parceiro";
    adicionarMsgChat(
      `Olá, <b>${nome}</b>! Sua análise foi salva. 🍦<br><br>Gostaria de consultar o seu <b>histórico de análises</b>? (sim/não)`,
    );
  } else {
    chatState.step = "idle";
    adicionarMsgChat(
      "Olá! Vi que você acabou de fazer uma análise. 🍦<br><br>Para salvar no histórico, informe o <b>CNPJ</b> da sorveteria.",
    );
  }
}
