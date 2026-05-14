const STATUS_CONFIG = {
    CREATED:    { label: "Criado",     color: "secondary" },
    DISPATCHED: { label: "Despachado", color: "primary"   },
    EN_ROUTE:   { label: "Em rota",    color: "warning"   },
    CONCLUDED:  { label: "Concluído",  color: "success"   },
    CANCELED:   { label: "Cancelado",  color: "danger"    },
};

const EVENT_LABELS = {
    CREATED:        "Pedido criado",
    DISPATCHED:     "Despachado",
    EN_ROUTE:       "Em rota",
    CONCLUDED:      "Concluído",
    CANCELLATION:   "Cancelado",
    ROUTE_CANCELED: "Rota cancelada",
};

const CUSTOMER_TYPE_LABELS = {
    PERSON:   "Pessoa Física",
    BUSINESS: "Pessoa Jurídica",
};

function formatAddress(addr) {
    if (!addr) return "—";
    return (
        <>
            <span className="field-value">{addr.street}, {addr.number}</span>
            <br />
            <span className="field-sub">{addr.neighborhood} — {addr.city}/{addr.state}</span>
            <br />
            <span className="field-sub">CEP: {addr.cep}</span>
        </>
    );
}

function formatDateTime(raw) {
    if (!raw) return "";
    return new Date(raw).toLocaleString("pt-BR", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit",
    });
}

function OrderCard({ order }) {
    const status = STATUS_CONFIG[order.status] || { label: order.status, color: "secondary" };
    const shortId = order.id ? `${order.id.slice(0, 8)}…` : "—";

    return (
        <div className="order-card">
            <div className="order-card-head">
                <span className="order-id" title={order.id}>ID: {shortId}</span>
                <span className={`badge bg-${status.color}`}>{status.label}</span>
            </div>

            <div className="order-card-body">
                <div className="row g-4">
                    <div className="col-sm-6">
                        <p className="field-label">Cliente</p>
                        <p className="field-value mb-0">{order.customer?.name ?? "—"}</p>
                        <p className="field-sub mb-0">
                            {CUSTOMER_TYPE_LABELS[order.customer?.type] ?? order.customer?.type}
                        </p>
                    </div>

                    <div className="col-sm-6">
                        <p className="field-label">Distância</p>
                        <p className="field-value mb-0">{order.distanceKm} km</p>
                    </div>

                    {order.deliveryman && (
                        <div className="col-sm-6">
                            <p className="field-label">Entregador</p>
                            <p className="field-value mb-0">{order.deliveryman.name}</p>
                        </div>
                    )}

                    <div className="col-sm-6">
                        <p className="field-label">Endereço de Coleta</p>
                        {formatAddress(order.pickupAddress)}
                    </div>

                    <div className="col-sm-6">
                        <p className="field-label">Endereço de Entrega</p>
                        {formatAddress(order.deliveryAddress)}
                    </div>
                </div>

                {order.events?.length > 0 && (
                    <div className="events-section">
                        <p className="field-label mb-3">Histórico</p>
                        {order.events.map((evt, i) => (
                            <div className="event-row" key={i}>
                                <span className={`event-dot ${i === 0 ? "first" : ""}`} />
                                <span>
                                    <span className="event-label">
                                        {EVENT_LABELS[evt.type] ?? evt.type}
                                    </span>
                                    {evt.dateTime && (
                                        <span className="event-time">
                                            {formatDateTime(evt.dateTime)}
                                        </span>
                                    )}
                                </span>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default OrderCard;
