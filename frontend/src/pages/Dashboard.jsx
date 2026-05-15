import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const CARDS = [
    {
        to: "/orders/new",
        icon: "📦",
        iconClass: "icon-blue",
        title: "Criar Pedido",
        description: "Cadastre um novo pedido de entrega.",
    },
    {
        to: "/orders/search",
        icon: "🔍",
        iconClass: "icon-violet",
        title: "Buscar Pedido",
        description: "Consulte um pedido pelo ID.",
    },
    {
        to: "/orders/customer",
        icon: "👤",
        iconClass: "icon-green",
        title: "Pedidos por Cliente",
        description: "Liste os pedidos de um cliente específico.",
    },
    {
        to: "/orders/dispatch",
        icon: "🚚",
        iconClass: "icon-amber",
        title: "Despachar Pedido",
        description: "Associe um entregador a um pedido existente.",
    },
    {
        to: "/orders/manage",
        icon: "🔄",
        iconClass: "icon-violet",
        title: "Alterar Status",
        description: "Atualize o status de um pedido existente.",
    },
];

const CUSTOMERS = [
    { id: "11111111-1111-1111-1111-111111111111", name: "Acme Corp",    type: "Business" },
    { id: "22222222-2222-2222-2222-222222222222", name: "Beatriz Lima", type: "Regular"  },
    { id: "33333333-3333-3333-3333-333333333333", name: "Globex SA",    type: "Business" },
    { id: "44444444-4444-4444-4444-444444444444", name: "Studio Nova",  type: "Premium"  },
    { id: "55555555-5555-5555-5555-555555555555", name: "Initech",      type: "Premium"  },
];

const DELIVERYMEN = [
    { id: "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", name: "Carlos Mendes", capacity: 3 },
    { id: "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", name: "Marina Souza",  capacity: 2 },
    { id: "cccccccc-cccc-cccc-cccc-cccccccccccc", name: "Felipe Ramos",  capacity: 4 },
    { id: "dddddddd-dddd-dddd-dddd-dddddddddddd", name: "Ana Paula",     capacity: 2 },
    { id: "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee", name: "João Victor",   capacity: 5 },
];

const TYPE_STYLE = {
    Business: { bg: "#eff6ff", color: "#2563eb" },
    Regular:  { bg: "#f0fdf4", color: "#16a34a" },
    Premium:  { bg: "#fefce8", color: "#ca8a04" },
};

function CopyButton({ text }) {
    const [copied, setCopied] = useState(false);

    function handleCopy() {
        navigator.clipboard.writeText(text);
        setCopied(true);
        setTimeout(() => setCopied(false), 1500);
    }

    return (
        <button className={`btn-copy ${copied ? "btn-copy--ok" : ""}`} onClick={handleCopy} title={text}>
            {copied ? "✓ copiado" : "⧉ copiar"}
        </button>
    );
}

function CapacityDots({ value, max = 5 }) {
    return (
        <span className="capacity-dots">
            {Array.from({ length: max }, (_, i) => (
                <span key={i} className={`dot ${i < value ? "dot--on" : ""}`} />
            ))}
            <span className="capacity-number">{value}</span>
        </span>
    );
}

function Dashboard() {
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    function handleLogout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <div className="container dashboard-page">
            <div className="dashboard-banner">
                <div>
                    <h1>Dashboard</h1>
                    <p>Sistema de gerenciamento de entregas</p>
                </div>
                <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                    Sair
                </button>
            </div>

            <div className="row g-4">
                {CARDS.map((card) => (
                    <div className="col-sm-6 col-lg-3" key={card.to}>
                        <Link to={card.to} className="nav-card">
                            <div className={`nav-card-icon ${card.iconClass}`}>
                                {card.icon}
                            </div>
                            <h5>{card.title}</h5>
                            <p>{card.description}</p>
                        </Link>
                    </div>
                ))}
            </div>

            <div className="ref-section">
                <button className="ref-toggle" onClick={() => setOpen((o) => !o)}>
                    <div className="ref-toggle-left">
                        <span className="ref-toggle-icon">🗂</span>
                        <div>
                            <span className="ref-toggle-title">IDs para teste</span>
                            <span className="ref-toggle-sub">Dados inseridos automaticamente ao iniciar a aplicação</span>
                        </div>
                    </div>
                    <span className="ref-chevron">{open ? "▲" : "▼"}</span>
                </button>

                {open && (
                    <div className="ref-body">
                        <div className="row g-4">
                            <div className="col-md-6">
                                <p className="section-label">👥 Clientes</p>
                                <table className="ref-table">
                                    <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>Tipo</th>
                                            <th>ID</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {CUSTOMERS.map((c) => (
                                            <tr key={c.id}>
                                                <td className="ref-name">{c.name}</td>
                                                <td>
                                                    <span className="ref-badge" style={TYPE_STYLE[c.type]}>
                                                        {c.type}
                                                    </span>
                                                </td>
                                                <td className="ref-id-cell">
                                                    <span className="ref-id-short" title={c.id}>
                                                        {c.id.slice(0, 8)}···
                                                    </span>
                                                    <CopyButton text={c.id} />
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>

                            <div className="col-md-6">
                                <p className="section-label">🚴 Entregadores</p>
                                <table className="ref-table">
                                    <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>Capacidade</th>
                                            <th>ID</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {DELIVERYMEN.map((d) => (
                                            <tr key={d.id}>
                                                <td className="ref-name">{d.name}</td>
                                                <td><CapacityDots value={d.capacity} /></td>
                                                <td className="ref-id-cell">
                                                    <span className="ref-id-short" title={d.id}>
                                                        {d.id.slice(0, 8)}···
                                                    </span>
                                                    <CopyButton text={d.id} />
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;