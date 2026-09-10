const state = {
    accounts: [
        { id: "CM-A82F21B3D0C4", name: "Compte courant", type: "COURANT", balance: 8240.50, dark: false },
        { id: "CM-9D4A77E1B208", name: "Épargne projet", type: "EPARGNE", balance: 4240.00, dark: true }
    ],
    transactions: [
        { type: "DEPOT", title: "Salaire - Septembre", date: "Aujourd'hui, 09:12", amount: 2850.00, account: "CM-A82F21B3D0C4" },
        { type: "VIREMENT", title: "Vers CM-9D4A77E1B208", date: "Hier, 18:40", amount: -500.00, account: "CM-A82F21B3D0C4" },
        { type: "RETRAIT", title: "Retrait distributeur", date: "08 sept. 2026", amount: -80.00, account: "CM-A82F21B3D0C4" },
        { type: "DEPOT", title: "Versement épargne", date: "05 sept. 2026", amount: 1000.00, account: "CM-9D4A77E1B208" }
    ]
};

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
    source.balance -= amount;
    destination.balance += amount;
    state.transactions.unshift({ type: "VIREMENT", title: `Vers ${destination.id}`, date: "À l'instant", amount: -amount, account: source.id });
    renderAccounts(); renderAccounts("all-accounts", false); renderBalance(); renderTransactions(); closeModal("transfer-modal");
});

document.getElementById("account-form").addEventListener("submit", event => {
    event.preventDefault();
    const id = `CM-${crypto.randomUUID().replaceAll("-", "").slice(0, 12).toUpperCase()}`;
    const type = document.getElementById("account-type").value;
    state.accounts.push({ id, name: document.getElementById("account-name").value.trim(), type: type.includes("épargne") ? "EPARGNE" : "COURANT", balance: 0, dark: state.accounts.length % 2 === 1 });
    renderAccounts(); renderAccounts("all-accounts", false); renderBalance(); renderTransactions(); closeModal("account-modal"); event.target.reset();
});

renderAccounts();
renderAccounts("all-accounts", false);
renderBalance();
renderTransactions();
