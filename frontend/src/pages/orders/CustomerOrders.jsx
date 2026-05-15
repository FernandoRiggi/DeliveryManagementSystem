import { useState } from "react";
import { Link } from "react-router-dom";
import { listOrdersByCustomer } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

function CustomerOrders() {

    const [customerId, setCustomerId] = useState("");
    const [orders, setOrders] = useState([]);
    const [searched, setSearched] = useState(false);
    const [error, setError] = useState("");

    async function handleSearch(e) {

        e.preventDefault();

        setError("");
        setSearched(false);

        try {

            const response = await listOrdersByCustomer(customerId);

            setOrders(response.data);

            setSearched(true);

        } catch (err) {

            setOrders([]);
            setSearched(true);

            setError(
                parseApiError(
                    err,
                    "Erro ao buscar pedidos do cliente."
                )
            );
        }
    }

    return (

        <div className="container page-wrapper">

            <div className="page-card">

                <div className="page-header">

                    <Link
                        to="/dashboard"
                        className="btn btn-outline-secondary btn-sm"
                    >
                        ← Voltar
                    </Link>

                    <h1>Pedidos por Cliente</h1>

                </div>

                <form onSubmit={handleSearch}>

                    <div className="row g-3 align-items-end">

                        <div className="col">

                            <label className="form-label">
                                ID do cliente
                            </label>

                            <input
                                className="form-control"
                                placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
                                value={customerId}
                                onChange={(e) =>
                                    setCustomerId(e.target.value)
                                }
                                required
                            />

                        </div>

                        <div className="col-auto">

                            <button
                                className="btn btn-primary"
                                type="submit"
                            >
                                Buscar
                            </button>

                        </div>

                    </div>

                </form>

                {error && (

                    <div
                        className="alert alert-danger mt-4"
                        role="alert"
                    >
                        {error}
                    </div>
                )}

                {searched && !error && orders.length === 0 && (

                    <div className="alert alert-info mt-4">

                        Nenhum pedido encontrado para este cliente.

                    </div>
                )}

                {orders.length > 0 && (

                    <div className="mt-4">

                        <p
                            className="section-label"
                            style={{ marginTop: 0 }}
                        >
                            {orders.length}{" "}
                            {orders.length === 1
                                ? "pedido encontrado"
                                : "pedidos encontrados"}
                        </p>

                        {orders.map((order, index) => (

                            <div key={order.id || index}>

                                <OrderCard order={order} />

                                {order.status !== "CANCELLED" && (
                                    <div className="priority-badge mt-2 mb-3">

                                        <strong>Prioridade:</strong>{" "}
                                        {order.priorityLevel || "NORMAL"}

                                    </div>
                                )}

                            </div>

                        ))}

                    </div>
                )}

            </div>

        </div>
    );
}

export default CustomerOrders;