import { useState } from "react";
import { Link } from "react-router-dom";
import {
    cancelOrder,
    cancelRoute,
    concludeOrder,
    getOrderById,
    startRoute,
} from "../../services/orderService";
import { parseApiError } from "../../utils/parseApiError";
import OrderCard from "../../components/OrderCard";

const ACTIONS_BY_STATUS = {
    CREATED: [
        {
            key: "cancel",
            label: "Cancelar pedido",
            className: "btn btn-outline-danger",
            successMessage: "Pedido cancelado com sucesso.",
            handler: cancelOrder,
        },
    ],
    DISPATCHED: [
        {
            key: "start-route",
            label: "Iniciar rota",
            className: "btn btn-primary",
            successMessage: "Rota iniciada com sucesso.",
            handler: startRoute,
        },
        {
            key: "cancel-route",
            label: "Cancelar rota",
            className: "btn btn-outline-warning",
            successMessage: "Rota cancelada com sucesso.",
            handler: cancelRoute,
        },
        {
            key: "cancel",
            label: "Cancelar pedido",
            className: "btn btn-outline-danger",
            successMessage: "Pedido cancelado com sucesso.",
            handler: cancelOrder,
        },
    ],
    EN_ROUTE: [
        {
            key: "conclude",
            label: "Concluir pedido",
            className: "btn btn-success",
            successMessage: "Pedido concluído com sucesso.",
            handler: concludeOrder,
        },
        {
            key: "cancel-route",
            label: "Cancelar rota",
            className: "btn btn-outline-warning",
            successMessage: "Rota cancelada com sucesso.",
            handler: cancelRoute,
        },
        {
            key: "cancel",
            label: "Cancelar pedido",
            className: "btn btn-outline-danger",
            successMessage: "Pedido cancelado com sucesso.",
            handler: cancelOrder,
        },
    ],
};

function ManageOrder() {
    const [orderId, setOrderId] = useState("");
    const [order, setOrder] = useState(null);
    const [loadingSearch, setLoadingSearch] = useState(false);
    const [loadingAction, setLoadingAction] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    async function fetchOrder(id) {
        const response = await getOrderById(id);
        setOrder(response.data);
    }

    async function handleSearch(e) {
        e.preventDefault();
        setError("");
        setSuccess("");
        setOrder(null);

        const trimmedOrderId = orderId.trim();

        if (!trimmedOrderId) {
            setError("Informe o ID do pedido.");
            return;
        }

        setLoadingSearch(true);

        try {
            await fetchOrder(trimmedOrderId);
        } catch (err) {
            setError(parseApiError(err, "Erro ao buscar pedido."));
        } finally {
            setLoadingSearch(false);
        }
    }

    async function handleAction(action) {
        if (!order?.id) {
            setError("Busque um pedido antes de executar uma ação.");
            return;
        }

        const shouldContinue = window.confirm(
            `Tem certeza que deseja ${action.label.toLowerCase()}?`
        );

        if (!shouldContinue) return;

        setError("");
        setSuccess("");
        setLoadingAction(action.key);

        try {
            await action.handler(order.id);
            await fetchOrder(order.id);
            setSuccess(action.successMessage);
        } catch (err) {
            setError(parseApiError(err, "Erro ao executar ação no pedido."));
        } finally {
            setLoadingAction("");
        }
    }

    const availableActions = order ? ACTIONS_BY_STATUS[order.status] ?? [] : [];
    const hasFinalStatus = order?.status === "CONCLUDED" || order?.status === "CANCELED";

    return (
        <div className="container page-wrapper">
            <div className="page-card">
                <div className="page-header">
                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                        ← Voltar
                    </Link>
                    <h1>Alterar Status do Pedido</h1>
                </div>

                <p className="text-muted mb-4">
                    Busque um pedido pelo ID para visualizar o status atual e executar as alterações disponíveis.
                </p>

                <form onSubmit={handleSearch}>
                    <div className="row g-3 align-items-end">
                        <div className="col">
                            <label className="form-label">ID do pedido</label>
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
                                disabled={loadingSearch}
                            >
                                {loadingSearch ? "Buscando..." : "Buscar"}
                            </button>
                        </div>
                    </div>
                </form>

                {error && (
                    <div className="alert alert-danger mt-4" role="alert">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="alert alert-success mt-4" role="alert">
                        {success}
                    </div>
                )}

                {order && (
                    <div className="mt-4">
                        <p className="section-label" style={{ marginTop: 0 }}>
                            Pedido encontrado
                        </p>

                        <OrderCard order={order} />

                        <div className="mt-3">
                            <p className="section-label" style={{ marginTop: 0 }}>
                                Ações disponíveis
                            </p>

                            {availableActions.length > 0 ? (
                                <div className="d-flex flex-wrap gap-2">
                                    {availableActions.map((action) => (
                                        <button
                                            key={action.key}
                                            type="button"
                                            className={action.className}
                                            disabled={Boolean(loadingAction)}
                                            onClick={() => handleAction(action)}
                                        >
                                            {loadingAction === action.key
                                                ? "Processando..."
                                                : action.label}
                                        </button>
                                    ))}
                                </div>
                            ) : (
                                <div className="alert alert-info mb-0">
                                    {hasFinalStatus
                                        ? "Este pedido já está finalizado e não possui novas ações."
                                        : "Não há ações disponíveis para o status atual deste pedido."}
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ManageOrder;