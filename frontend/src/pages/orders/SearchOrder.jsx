import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../../services/api";

function SearchOrder() {
    const [orderId, setOrderId] = useState("");
    const [order, setOrder] = useState(null);

    async function handleSearch(e) {
        e.preventDefault();

        try {
            const response = await api.get(`/api/v1/orders/${orderId}`);
            setOrder(response.data);
        } catch (error) {
            console.log(error);
            setOrder(null);
            alert(error.response?.data?.message || "Pedido não encontrado");
        }
    }

    async function handleCancel() {
        try {
            await api.patch(`/api/v1/orders/${orderId}/cancel`);
            alert("Pedido cancelado com sucesso!");
            await handleSearch({ preventDefault: () => {} });
        } catch (error) {
            console.log(error);
            alert(error.response?.data?.message || "Erro ao cancelar pedido");
        }
    }

    async function handleStartRoute() {
        try {
            await api.patch(`/api/v1/orders/${orderId}/start-route`);
            alert("Rota iniciada com sucesso!");
            await handleSearch({ preventDefault: () => {} });
        } catch (error) {
            console.log(error);
            alert(error.response?.data?.message || "Erro ao iniciar rota");
        }
    }

    async function handleConclude() {
        try {
            await api.patch(`/api/v1/orders/${orderId}/conclude`);
            alert("Pedido concluído com sucesso!");
            await handleSearch({ preventDefault: () => {} });
        } catch (error) {
            console.log(error);
            alert(error.response?.data?.message || "Erro ao concluir pedido");
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

                        <button className="btn btn-warning me-2" onClick={handleStartRoute}>
                            Iniciar rota
                        </button>

                        <button className="btn btn-success me-2" onClick={handleConclude}>
                            Concluir
                        </button>

                        <button className="btn btn-danger" onClick={handleCancel}>
                            Cancelar pedido
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default SearchOrder;