import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import { getOrderQueue, getOrderById, dispatchOrder } from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

const PRIORITY_CONFIG = {
    NORMAL:   { label: "Normal",  color: "secondary" },
    URGENT:   { label: "Urgente", color: "warning"   },
    CRITICAL: { label: "Crítico", color: "danger"    },
};

const CUSTOMER_TYPE_LABELS = {
    REGULAR:  "Regular",
    BUSINESS: "Empresarial",
    PREMIUM:  "Premium",
};

const DELIVERYMEN = [
    { id: "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", name: "Carlos Mendes", capacity: 3 },
    { id: "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", name: "Marina Souza",  capacity: 2 },
    { id: "cccccccc-cccc-cccc-cccc-cccccccccccc", name: "Felipe Ramos",  capacity: 4 },
    { id: "dddddddd-dddd-dddd-dddd-dddddddddddd", name: "Ana Paula",     capacity: 2 },
    { id: "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee", name: "João Victor",   capacity: 5 },
];

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

function DispatchOrder() {
    const [queue, setQueue] = useState([]);
    const [loadingQueue, setLoadingQueue] = useState(true);
    const [queueError, setQueueError] = useState("");

    const [selectedItem, setSelectedItem] = useState(null);
    const [order, setOrder] = useState(null);
    const [loadingOrder, setLoadingOrder] = useState(false);
    const [selectedDeliveryman, setSelectedDeliveryman] = useState(null);

    const [dispatching, setDispatching] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    const fetchQueue = useCallback(async () => {
        setLoadingQueue(true);
        setQueueError("");
        try {
            const res = await getOrderQueue();
            setQueue(res.data);
        } catch {
            setQueueError("Não foi possível carregar a fila de pedidos.");
        } finally {
            setLoadingQueue(false);
        }
    }, []);

    useEffect(() => { fetchQueue(); }, [fetchQueue]);

    async function handleSelectOrder(item) {
        setSelectedItem(item);
        setOrder(null);
        setSelectedDeliveryman(null);
        setError("");
        setLoadingOrder(true);
        try {
            const res = await getOrderById(item.orderId);
            setOrder(res.data);
        } catch (err) {
            setError(parseApiError(err, "Erro ao carregar pedido."));
        } finally {
            setLoadingOrder(false);
        }
    }

    async function handleDispatch() {
        if (!selectedDeliveryman) return;
        setError("");
        setDispatching(true);
        try {
            await dispatchOrder(order.id, selectedDeliveryman);
            setSuccess(true);
        } catch (err) {
            setError(parseApiError(err, "Erro ao despachar pedido."));
        } finally {
            setDispatching(false);
        }
    }

    function handleReset() {
        setSelectedItem(null);
        setOrder(null);
        setSelectedDeliveryman(null);
        setError("");
        setSuccess(false);
        fetchQueue();
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
                    Pedidos aguardando despacho, ordenados por prioridade logística.
                </p>

                {error && (
                    <div className="alert alert-danger" role="alert">{error}</div>
                )}

                {success ? (
                    <div className="created-order-box">
                        <div className="created-order-header">✅ Pedido despachado com sucesso!</div>
                        <div className="created-order-actions">
                            <button className="btn btn-primary btn-sm" onClick={handleReset}>
                                Despachar outro pedido
                            </button>
                            <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                                Voltar ao Dashboard
                            </Link>
                        </div>
                    </div>
                ) : (
                    <>
                        <p className="section-label" style={{ marginTop: 0 }}>
                            1. Selecionar pedido da fila
                        </p>

                        {loadingQueue ? (
                            <p className="text-muted">Carregando fila...</p>
                        ) : queueError ? (
                            <div className="alert alert-danger">{queueError}</div>
                        ) : queue.length === 0 ? (
                            <div className="alert alert-info mb-0">
                                Nenhum pedido aguardando despacho.
                            </div>
                        ) : (
                            <div className="queue-list">
                                {queue.map((item, index) => {
                                    const p = PRIORITY_CONFIG[item.priorityLevel] ?? PRIORITY_CONFIG.NORMAL;
                                    const isSelected = selectedItem?.orderId === item.orderId;
                                    return (
                                        <button
                                            key={item.orderId}
                                            type="button"
                                            className={`queue-item ${isSelected ? "queue-item--selected" : ""}`}
                                            onClick={() => handleSelectOrder(item)}
                                        >
                                            <span className="queue-rank">#{index + 1}</span>

                                            <div className="queue-info">
                                                <span className="queue-customer">
                                                    {item.customerName}
                                                    <span className="queue-type">
                                                        {CUSTOMER_TYPE_LABELS[item.customerType] ?? item.customerType}
                                                    </span>
                                                </span>
                                                <span className="queue-meta">
                                                    {item.distanceKm} km
                                                    {item.waitMinutes > 0 && (
                                                        <> · aguardando {item.waitMinutes} min</>
                                                    )}
                                                </span>
                                            </div>

                                            <div className="queue-priority">
                                                <span className={`badge bg-${p.color}`}>{p.label}</span>
                                                <span className="queue-score">score {item.score}</span>
                                            </div>
                                        </button>
                                    );
                                })}
                            </div>
                        )}

                        {loadingOrder && (
                            <p className="text-muted mt-3">Carregando detalhes do pedido...</p>
                        )}

                        {order && (
                            <>
                                <p className="section-label">Pedido selecionado</p>
                                <OrderCard order={order} />

                                <p className="section-label">2. Escolher entregador</p>
                                <div className="deliveryman-grid">
                                    {DELIVERYMEN.map((d) => (
                                        <button
                                            key={d.id}
                                            className={`deliveryman-card ${selectedDeliveryman === d.id ? "deliveryman-card--selected" : ""}`}
                                            onClick={() => setSelectedDeliveryman(d.id)}
                                            type="button"
                                        >
                                            <span className="deliveryman-name">{d.name}</span>
                                            <CapacityDots value={d.capacity} />
                                        </button>
                                    ))}
                                </div>

                                <div className="mt-4">
                                    <button
                                        className="btn btn-primary me-2"
                                        onClick={handleDispatch}
                                        disabled={!selectedDeliveryman || dispatching}
                                    >
                                        {dispatching ? "Despachando..." : "Confirmar despacho"}
                                    </button>
                                    <button
                                        className="btn btn-outline-secondary"
                                        type="button"
                                        onClick={() => { setSelectedItem(null); setOrder(null); setSelectedDeliveryman(null); setError(""); }}
                                    >
                                        Cancelar seleção
                                    </button>
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