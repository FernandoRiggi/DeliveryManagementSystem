import { useState } from "react";
import { Link } from "react-router-dom";
import { dispatchOrder } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";

function DispatchOrder() {
    const [orderId, setOrderId] = useState("");
    const [deliverymanId, setDeliverymanId] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    async function handleDispatch(e) {
        e.preventDefault();
        setError("");
        setSuccess("");

        const trimmedOrderId = orderId.trim();
        const trimmedDeliverymanId = deliverymanId.trim();

        if (!trimmedOrderId || !trimmedDeliverymanId) {
            setError("Informe o ID do pedido e o ID do entregador.");
            return;
        }

        setLoading(true);

        try {
            await dispatchOrder(trimmedOrderId, trimmedDeliverymanId);
            setSuccess("Pedido despachado com sucesso!");
            setOrderId("");
            setDeliverymanId("");
        } catch (err) {
            setError(parseApiError(err, "Erro ao despachar pedido."));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="container page-wrapper">
            <div className="page-card">
                <div className="page-header">
                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                        ← Voltar
                    </Link>
                    <h1>Despachar Pedido</h1>
                </div>

                <p className="text-muted mb-4">
                    Informe o ID do pedido e o ID do entregador para despachar a entrega.
                </p>

                {error && <div className="alert alert-danger" role="alert">{error}</div>}
                {success && <div className="alert alert-success" role="alert">{success}</div>}

                <form onSubmit={handleDispatch}>
                    <div className="row g-3">
                        <div className="col-md-6">
                            <label className="form-label fw-semibold">ID do pedido</label>
                            <input
                                className="form-control"
                                placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
                                value={orderId}
                                onChange={(e) => setOrderId(e.target.value)}
                                required
                            />
                        </div>
                        <div className="col-md-6">
                            <label className="form-label fw-semibold">ID do entregador</label>
                            <input
                                className="form-control"
                                placeholder="ex: 550e8400-e29b-41d4-a716-446655440001"
                                value={deliverymanId}
                                onChange={(e) => setDeliverymanId(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="mt-4">
                        <button
                            className="btn btn-primary me-2"
                            type="submit"
                            disabled={loading}
                        >
                            {loading ? "Despachando..." : "Despachar pedido"}
                        </button>
                        <Link to="/dashboard" className="btn btn-outline-secondary">
                            Cancelar
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default DispatchOrder;
