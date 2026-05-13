import { Link, useNavigate } from "react-router-dom";

function Dashboard() {
    const navigate = useNavigate();

    function handleLogout() {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <div className="container mt-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h1>Dashboard</h1>
                    <p className="text-muted mb-0">
                        Sistema de gerenciamento de entregas
                    </p>
                </div>

                <button className="btn btn-outline-danger" onClick={handleLogout}>
                    Sair
                </button>
            </div>

            <div className="row g-4">
                <div className="col-md-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body">
                            <h5 className="card-title">Criar Pedido</h5>
                            <p className="card-text">
                                Cadastre um novo pedido de entrega.
                            </p>
                            <Link to="/orders/new" className="btn btn-primary">
                                Criar pedido
                            </Link>
                        </div>
                    </div>
                </div>

                <div className="col-md-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body">
                            <h5 className="card-title">Buscar Pedido</h5>
                            <p className="card-text">
                                Consulte um pedido pelo ID.
                            </p>
                            <Link to="/orders/search" className="btn btn-primary">
                                Buscar pedido
                            </Link>
                        </div>
                    </div>
                </div>

                <div className="col-md-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body">
                            <h5 className="card-title">Pedidos por Cliente</h5>
                            <p className="card-text">
                                Liste os pedidos de um cliente específico.
                            </p>
                            <Link to="/orders/customer" className="btn btn-primary">
                                Listar pedidos
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;