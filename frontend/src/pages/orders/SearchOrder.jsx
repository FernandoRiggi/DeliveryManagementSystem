import { useState } from "react";
import { Link } from "react-router-dom";
import { getOrderById } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

import { useState } from "react";
import { Link } from "react-router-dom";
import { getOrderById } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

function SearchOrder() {

    const [orderId, setOrderId] = useState("");
    const [order, setOrder] = useState(null);
    const [error, setError] = useState("");

    async function handleSearch(e) {

        e.preventDefault();

        setError("");
        setOrder(null);

        try {

            const response = await getOrderById(orderId);

            if (response.data.status === "CANCELLED") {
                setError("Pedidos cancelados não podem ser visualizados.");
                return;
            }

            setOrder(response.data);

        } catch (err) {

            setError(parseApiError(err, "Pedido não encontrado."));
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

                    <h1>Buscar Pedido</h1>

                </div>

                <form onSubmit={handleSearch}>

                    <div className="row g-3 align-items-end">

                        <div className="col">

                            <label className="form-label">
                                ID do pedido
                            </label>

                            <input
                                className="form-control"
                                placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
                                value={orderId}
                                onChange={(e) => setOrderId(e.target.value)}
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

                {order && (

                    <div className="mt-4">

                        <p
                            className="section-label"
                            style={{ marginTop: 0 }}
                        >
                            Resultado
                        </p>

                        <OrderCard order={order} />

                    </div>
                )}

            </div>

        </div>
    );
}

export default SearchOrder;