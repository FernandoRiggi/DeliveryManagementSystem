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
];

function Dashboard() {
    const navigate = useNavigate();

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
        </div>
    );
}

export default Dashboard;
