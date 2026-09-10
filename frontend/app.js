const state = {
    accounts: [],
    transactions: []
};

const API_URL = "http://localhost:8080/api";
const euro = amount => new Intl.NumberFormat("fr-FR", { style: "currency", currency: "EUR" }).format(amount);
const shortNumber = id => id.replace("CM-", "•••• ") + "";
const accountIcon = type => type === "EPARGNE" ? "◒" : "◉";

function renderAccounts(targetId = "account-grid", includeAdd = true) {
    const target = document.getElementById(targetId);
    target.innerHTML = state.accounts.map(account => `
        <article class="account-card ${account.dark ? "savings" : ""}" data-account="${account.id}">
            <div class="card-top"><span class="card-type">${account.name}</span><span class="card-symbol">${accountIcon(account.type)}</span></div>
            <div class="card-bottom"><div><div class="card-balance">${euro(account.balance)}</div><div class="card-number">${shortNumber(account.id)}</div></div><span>→</span></div>
        </article>`).join("") + (includeAdd ? `<button class="account-card add-card" id="add-account-card"><span><span class="plus">＋</span><br>Ouvrir un compte</span></button>` : "");
    target.querySelectorAll("[data-account]").forEach(card => card.addEventListener("click", () => openTransfer(card.dataset.account)));
    const add = document.getElementById("add-account-card");
    if (add) add.addEventListener("click", () => openModal("account-modal"));
}

function transactionMarkup(transaction) {
    const positive = transaction.amount >= 0;
    const icon = transaction.type === "VIREMENT" ? "↗" : positive ? "↓" : "↑";
    return `<div class="transaction-row"><span class="transaction-icon ${positive ? "" : "out"}">${icon}</span><div class="transaction-info"><strong>${transaction.title}</strong><small>${transaction.date}</small></div><span class="transaction-amount ${positive ? "positive" : "negative"}">${positive ? "+" : "−"}${euro(Math.abs(transaction.amount))}</span></div>`;
}

async function loadData() {
    const response = await fetch(`${API_URL}/accounts`);
    if (!response.ok) throw new Error("Impossible de charger les comptes.");
    const accounts = await response.json();
    state.accounts = accounts.map((account, index) => ({
        ...account,
        name: account.type === "EPARGNE" ? "Compte épargne" : "Compte courant",
        dark: index % 2 === 1
    }));
    const histories = await Promise.all(state.accounts.map(async account => {
        const historyResponse = await fetch(`${API_URL}/accounts/${account.id}/transactions`);
        return historyResponse.ok ? historyResponse.json() : [];
    }));
    state.transactions = histories.flatMap((items, index) => items.map(item => ({
        type: item.type,
        title: item.description,
        date: item.date.replace("T", " ").slice(0, 16),
        amount: item.type === "RETRAIT" || (item.type === "VIREMENT" && item.description.includes("vers")) ? -item.amount : item.amount,
        account: state.accounts[index].id
    }))).reverse();
    renderAccounts();
    renderAccounts("all-accounts", false);
    renderBalance();
    renderTransactions();
}

function renderTransactions() {
    document.getElementById("recent-transactions").innerHTML = state.transactions.slice(0, 3).map(transactionMarkup).join("");
    const filter = document.getElementById("transaction-filter");
    filter.innerHTML = `<option value="all">Tous les comptes</option>` + state.accounts.map(account => `<option value="${account.id}">${account.name}</option>`).join("");
    const renderAll = () => {
        const selected = filter.value;
        document.getElementById("all-transactions").innerHTML = state.transactions.filter(item => selected === "all" || item.account === selected).map(transactionMarkup).join("") || `<p class="empty-state">Aucune transaction pour ce compte.</p>`;
    };
    filter.onchange = renderAll;
    renderAll();
}

function renderBalance() {
    document.getElementById("total-balance").textContent = euro(state.accounts.reduce((total, account) => total + account.balance, 0));
}

function openModal(id) { document.getElementById(id).hidden = false; }
function closeModal(id) { document.getElementById(id).hidden = true; }
function openTransfer(sourceId = state.accounts[0].id) {
    const source = document.getElementById("source-account");
    source.innerHTML = state.accounts.map(account => `<option value="${account.id}" ${account.id === sourceId ? "selected" : ""}>${account.name} - ${euro(account.balance)}</option>`).join("");
    document.getElementById("transfer-message").textContent = "";
    openModal("transfer-modal");
}

function changeView(view) {
    document.querySelectorAll(".view").forEach(section => { section.hidden = !section.classList.contains(`${view}-view`); });
    document.querySelectorAll(".nav-item").forEach(item => item.classList.toggle("active", item.dataset.view === view));
    const titles = { overview: "Bonjour, Alex ✦", accounts: "Vos comptes", transactions: "Vos transactions" };
    document.getElementById("page-title").textContent = titles[view];
}

document.querySelectorAll(".nav-item").forEach(item => item.addEventListener("click", () => changeView(item.dataset.view)));
document.querySelectorAll("[data-view-link]").forEach(item => item.addEventListener("click", () => changeView(item.dataset.viewLink)));
document.querySelectorAll("[data-close]").forEach(button => button.addEventListener("click", () => closeModal(button.dataset.close)));
document.getElementById("new-account-button").addEventListener("click", () => openModal("account-modal"));
document.getElementById("new-account-button-alt").addEventListener("click", () => openModal("account-modal"));

document.getElementById("transfer-form").addEventListener("submit", event => {
    event.preventDefault();
    const sourceId = document.getElementById("source-account").value;
    const destinationId = document.getElementById("destination-account").value.trim().toUpperCase();
    const amount = Number(document.getElementById("transfer-amount").value);
    const source = state.accounts.find(account => account.id === sourceId);
    const destination = state.accounts.find(account => account.id === destinationId);
    const message = document.getElementById("transfer-message");
    if (!destination) { message.textContent = "Compte destinataire introuvable."; return; }
    if (destination.id === source.id) { message.textContent = "Le compte source et le destinataire doivent être différents."; return; }
    if (!amount || amount <= 0) { message.textContent = "Le montant doit être supérieur à zéro."; return; }
    if (source.balance < amount) { message.textContent = "Solde insuffisant pour effectuer ce transfert."; return; }
    const response = await fetch(`${API_URL}/transfers`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ source: source.id, destination: destination.id, montant: amount })
    });
    const result = await response.json();
    if (!response.ok) { message.textContent = result.error || "Le transfert a échoué."; return; }
    await loadData();
    closeModal("transfer-modal");
});

document.getElementById("account-form").addEventListener("submit", async event => {
    event.preventDefault();
    const response = await fetch(`${API_URL}/accounts`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nom: document.getElementById("account-last-name").value,
            prenom: document.getElementById("account-first-name").value,
            telephone: document.getElementById("account-phone").value,
            email: document.getElementById("account-email").value,
            motDePasse: document.getElementById("account-password").value,
            type: document.getElementById("account-type").value
        })
    });
    if (!response.ok) return;
    await loadData();
    closeModal("account-modal");
    event.target.reset();
});

loadData().catch(error => {
    document.getElementById("total-balance").textContent = "API indisponible";
    document.getElementById("recent-transactions").innerHTML = `<p class="empty-state">${error.message}</p>`;
});
