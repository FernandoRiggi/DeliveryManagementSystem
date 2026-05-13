import { useState } from "react";
import { Link } from "react-router-dom";
import { dispatchOrder } from "../../services/orderService";

function DispatchOrder() {
    const [orderId, setOrderId] = useState("");
    const [deliverymanId, setDeliverymanId] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleDispatch(e) {
        e.preventDefault();

        const trimmedOrderId = orderId.trim();
        const trimmedDeliverymanId = deliverymanId.trim();

        if (!trimmedOrderId || !trimmedDeliverymanId) {
            alert("Informe o ID do pedido e o ID do entregador.");
            return;
        }

        setLoading(true);

        try {
            await dispatchOrder(trimmedOrderId, trimmedDeliverymanId);

            alert("Pedido despachado com sucesso!");

            setOrderId("");
            setDeliverymanId("");
        } catch (error) {
            console.log(error);
            alert(error.response?.data?.message || "Erro ao despachar pedido");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="container mt-5">
            <div className="card p-4">
                <h1>Despachar Pedido</h1>

                <p className="text-muted">
                    Informe o ID do pedido e o ID do entregador para despachar a entrega.
                </p>

                <form onSubmit={handleDispatch}>
                    <div className="mb-3">
                        <label className="form-label">
                            ID do pedido
                        </label>

                        <input
                            className="form-control"
                            placeholder="Ex: 550e8400-e29b-41d4-a716-446655440000"
                            value={orderId}
                            onChange={(e) => setOrderId(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">
                            ID do entregador
                        </label>

                        <input
                            className="form-control"
                            placeholder="Ex: 550e8400-e29b-41d4-a716-446655440001"
                            value={deliverymanId}
                            onChange={(e) => setDeliverymanId(e.target.value)}
                            required
                        />
                    </div>

                    <button
                        className="btn btn-info me-2"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? "Despachando..." : "Despachar pedido"}
                    </button>

                    <Link to="/dashboard" className="btn btn-secondary">
                        Voltar
                    </Link>
                </form>
            </div>
        </div>
    );
}

export default DispatchOrder;