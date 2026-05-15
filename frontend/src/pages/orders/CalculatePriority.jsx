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

        <div className="container mt-5">

            <div className="card p-4">

                <h1>Calcular Prioridade</h1>

                <p>ID do pedido:</p>

                <code>{id}</code>

                <form
                    className="mt-4"
                    onSubmit={handleCalculate}
                >

                    <label className="form-label">
                        Tempo de espera (minutos)
                    </label>

                    <input
                        className="form-control mb-3"
                        type="number"
                        min="0"
                        value={waitMinutes}
                        onChange={(e) =>
                            setWaitMinutes(e.target.value)
                        }
                        required
                    />

                    <button
                        className="btn btn-primary"
                        type="submit"
                    >
                        Calcular prioridade
                    </button>

                </form>

                {error && (

                    <div className="alert alert-danger mt-3">

                        {error}

                    </div>
                )}

                {priority !== null && (

                    <div className="alert alert-success mt-4">

                        <h4>Prioridade calculada</h4>

                        <p>
                            <strong>Priority:</strong> {priority}
                        </p>

                        <p>
                            <strong>Level:</strong> {level}
                        </p>

                    </div>
                )}

                <Link
                    className="btn btn-outline-secondary mt-3"
                    to="/dashboard"
                >
                    Voltar
                </Link>

            </div>

        </div>
    );
}

export default CalculatePriority;