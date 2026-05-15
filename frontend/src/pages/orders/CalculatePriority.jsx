import { useState } from "react";
import { useParams, Link } from "react-router-dom";
import api from "../../services/api";

function CalculatePriority() {

    const { id } = useParams();

    const [waitMinutes, setWaitMinutes] = useState("");
    const [priority, setPriority] = useState(null);
    const [level, setLevel] = useState("");
    const [error, setError] = useState("");

    async function handleCalculate(e) {

        e.preventDefault();

        setError("");

        try {

            const response = await api.post(
                `/api/v1/orders/${id}/priority`,
                {
                    waitMinutes: Number(waitMinutes)
                }
            );

            setPriority(response.data.priority);
            setLevel(response.data.level);

        } catch (err) {

            console.log(err);

            setError("Erro ao calcular prioridade");
        }
    }

    return (

        <div className="container page-wrapper">

            <div className="page-card">

                <div className="page-header">

                    <Link
                        className="btn btn-outline-secondary btn-sm"
                        to="/dashboard"
                    >
                        ← Voltar
                    </Link>

                    <h1>Calcular Prioridade</h1>

                </div>

                <div className="mb-4">

                    <p className="section-label mb-2">
                        Pedido
                    </p>

                    <div className="created-order-id">

                    <span className="created-order-label">
                        ID do pedido
                    </span>

                        <div className="created-order-value">

                            <code>{id}</code>

                        </div>

                    </div>

                </div>

                <form onSubmit={handleCalculate}>

                    <p className="section-label">
                        Dados da Prioridade
                    </p>

                    <div className="row g-3">

                        <div className="col-md-4">

                            <label className="form-label">
                                Tempo de espera (minutos)
                            </label>

                            <input
                                className="form-control"
                                type="number"
                                min="0"
                                value={waitMinutes}
                                onChange={(e) =>
                                    setWaitMinutes(e.target.value)
                                }
                                required
                            />

                        </div>

                    </div>

                    <div className="mt-4">

                        <button
                            className="btn btn-primary me-2"
                            type="submit"
                        >
                            Calcular prioridade
                        </button>

                        <Link
                            className="btn btn-outline-secondary"
                            to="/dashboard"
                        >
                            Cancelar
                        </Link>

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

                {priority !== null && (

                    <div className="alert alert-success mt-4">

                        <h4 className="mb-3">
                            Prioridade calculada
                        </h4>

                        <div className="d-flex flex-column gap-2">

                            <div>

                                <strong>Priority:</strong> {priority}

                            </div>

                            <div>

                                <strong>Level:</strong> {level}

                            </div>

                        </div>

                    </div>
                )}

            </div>

        </div>
    );
}

export default CalculatePriority;