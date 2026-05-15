import { useState } from "react";
import { Link } from "react-router-dom";
import { getOrderById, dispatchOrder } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

const DELIVERYMEN = [
    { id: "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", name: "Carlos Mendes", capacity: 3 },
    { id: "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", name: "Marina Souza", capacity: 2 },
    { id: "cccccccc-cccc-cccc-cccc-cccccccccccc", name: "Felipe Ramos", capacity: 4 },
    { id: "dddddddd-dddd-dddd-dddd-dddddddddddd", name: "Ana Paula", capacity: 2 },
    { id: "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee", name: "João Victor", capacity: 5 },
];

function CapacityDots({ value, max = 5 }) {

    return (

        <span className="capacity-dots">

            {Array.from({ length: max }, (_, i) => (

                <span
                    key={i}
                    className={`dot ${i < value ? "dot--on" : ""}`}
                />

            ))}

            <span className="capacity-number">
                {value}
            </span>

        </span>
    );
}

function DispatchOrder() {

    const [orderId, setOrderId] = useState("");
    const [order, setOrder] = useState(null);
    const [searching, setSearching] = useState(false);
    const [selected, setSelected] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    async function handleSearchOrder(e) {

        e.preventDefault();

        setError("");
        setOrder(null);
        setSelected(null);
        setSuccess(false);
        setSearching(true);

        try {

            const response = await getOrderById(orderId.trim());

            setOrder(response.data);

        } catch (err) {

            setError(
                parseApiError(err, "Pedido não encontrado.")
            );

        } finally {

            setSearching(false);
        }
    }

    async function handleDispatch() {

        if (!selected) return;

        setError("");
        setLoading(true);

        try {

            await dispatchOrder(order.id, selected);

            setSuccess(true);

        } catch (err) {

            setError(
                parseApiError(err, "Erro ao despachar pedido.")
            );

        } finally {

            setLoading(false);
        }
    }

    function handleReset() {

        setOrderId("");
        setOrder(null);
        setSelected(null);
        setError("");
        setSuccess(false);
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

                    <h1>Despachar Pedido</h1>

                </div>

                {error && (

                    <div
                        className="alert alert-danger"
                        role="alert"
                    >
                        {error}
                    </div>
                )}

                {success ? (

                    <div className="created-order-box">

                        <div className="created-order-header">
                            ✅ Pedido despachado com sucesso!
                        </div>

                        <div className="created-order-actions">

                            <button
                                className="btn btn-primary btn-sm"
                                onClick={handleReset}
                            >
                                Despachar outro pedido
                            </button>

                            <Link
                                to="/dashboard"
                                className="btn btn-outline-secondary btn-sm"
                            >
                                Voltar ao Dashboard
                            </Link>

                        </div>

                    </div>

                ) : (

                    <>

                        <p className="section-label">
                            1. Buscar pedido pelo ID
                        </p>

                        <form onSubmit={handleSearchOrder}>

                            <div className="row g-3 align-items-end">

                                <div className="col">

                                    <input
                                        className="form-control"
                                        placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
                                        value={orderId}
                                        onChange={(e) => {
                                            setOrderId(e.target.value);
                                            setOrder(null);
                                            setSelected(null);
                                        }}
                                        required
                                    />

                                </div>

                                <div className="col-auto">

                                    <button
                                        className="btn btn-outline-primary"
                                        type="submit"
                                        disabled={searching}
                                    >
                                        {searching ? "Buscando..." : "Buscar"}
                                    </button>

                                </div>

                            </div>

                        </form>

                        {order && (

                            <>

                                <div className="mt-4">

                                    <OrderCard order={order} />

                                    <div className="priority-highlight mt-3">

                                        <div className="priority-highlight__header">
                                            Prioridade da Entrega
                                        </div>

                                        <div className="priority-highlight__content">

                                            <div className="priority-highlight__score">
                                                {order.priorityLevel}
                                            </div>

                                            <div
                                                className={`priority-badge priority-badge--${(order.priorityLevel || "").toLowerCase()}`}
                                            >
                                                {order.priorityLevel || "NÃO CALCULADA"}
                                            </div>

                                        </div>

                                    </div>

                                </div>

                                <p className="section-label mt-4">
                                    2. Escolher entregador
                                </p>

                                <div className="deliveryman-grid">

                                    {DELIVERYMEN.map((d) => (

                                        <button
                                            key={d.id}
                                            className={`deliveryman-card ${selected === d.id ? "deliveryman-card--selected" : ""}`}
                                            onClick={() => setSelected(d.id)}
                                            type="button"
                                        >

                                            <span className="deliveryman-name">
                                                {d.name}
                                            </span>

                                            <CapacityDots value={d.capacity} />

                                        </button>
                                    ))}

                                </div>

                                <div className="mt-4">

                                    <button
                                        className="btn btn-primary me-2"
                                        onClick={handleDispatch}
                                        disabled={!selected || loading}
                                    >
                                        {loading ? "Despachando..." : "Confirmar despacho"}
                                    </button>

                                    <Link
                                        to="/dashboard"
                                        className="btn btn-outline-secondary"
                                    >
                                        Cancelar
                                    </Link>

                                </div>

                            </>
                        )}

                    </>
                )}

            </div>

        </div>
    );
}

export default DispatchOrder;