import { useState } from "react";
import { Link } from "react-router-dom";
import {getOrderById} from "../../services/orderService";

function SearchOrder() {
    const [orderId, setOrderId] = useState("");
    const [order, setOrder] = useState(null);

    async function fetchOrder() {
        const response = await getOrderById(orderId);
        setOrder(response.data);
    }

    async function handleSearch(e) {
        e.preventDefault();

        try {
            await fetchOrder();
        } catch (error) {
            console.log(error);
            setOrder(null);
            alert(error.response?.data?.message || "Pedido não encontrado");
        }
    }

    return (
        <div className="container mt-5">
            <div className="card p-4">
                <h1>Buscar Pedido</h1>

                <form onSubmit={handleSearch}>
                    <input
                        className="form-control mb-3"
                        placeholder="ID do pedido"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        required
                    />

                    <button className="btn btn-primary me-2" type="submit">
                        Buscar
                    </button>

                    <Link to="/dashboard" className="btn btn-secondary">
                        Voltar
                    </Link>
                </form>

                {order && (
                    <div className="mt-4">
                        <h3>Resultado</h3>

                        <pre className="bg-light p-3 border rounded">
                            {JSON.stringify(order, null, 2)}
                        </pre>
                    </div>
                )}
            </div>
        </div>
    );
}

export default SearchOrder;