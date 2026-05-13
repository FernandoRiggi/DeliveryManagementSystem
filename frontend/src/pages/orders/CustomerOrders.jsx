import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../../services/api";

function CustomerOrders() {
    const [customerId, setCustomerId] = useState("");
    const [orders, setOrders] = useState([]);

    async function handleSearch(e) {
        e.preventDefault();

        try {
            const response = await api.get(`/api/v1/orders/customer/${customerId}`);
            setOrders(response.data);
        } catch (error) {
            console.log(error);
            setOrders([]);
            alert(error.response?.data?.message || "Erro ao buscar pedidos do cliente");
        }
    }

    return (
        <div className="container mt-5">
            <div className="card p-4">
                <h1>Pedidos por Cliente</h1>

                <form onSubmit={handleSearch}>
                    <input
                        className="form-control mb-3"
                        placeholder="ID do cliente"
                        value={customerId}
                        onChange={(e) => setCustomerId(e.target.value)}
                    />

                    <button className="btn btn-primary me-2" type="submit">
                        Buscar
                    </button>

                    <Link to="/dashboard" className="btn btn-secondary">
                        Voltar
                    </Link>
                </form>

                {orders.length > 0 && (
                    <div className="mt-4">
                        <h3>Pedidos encontrados</h3>

                        {orders.map((order, index) => (
                            <div className="card mb-3" key={order.orderDeliveryId || index}>
                                <div className="card-body">
                                    <pre className="mb-0">
                                        {JSON.stringify(order, null, 2)}
                                    </pre>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default CustomerOrders;