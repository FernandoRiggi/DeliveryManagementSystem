import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import { parseApiError } from "../utils/parseApiError";

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    async function handleLogin(e) {
        e.preventDefault();
        setError("");

        try {
            const response = await api.post("/api/v1/authenticate", { email, password });
            localStorage.setItem("token", response.data.token);
            navigate("/dashboard");
        } catch (err) {
            setError(parseApiError(err, "Email ou senha inválidos."));
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h1>Entrar</h1>
                <p className="auth-subtitle">Acesse o sistema de gerenciamento de entregas</p>

                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}

                <form onSubmit={handleLogin}>
                    <div className="mb-3">
                        <label className="form-label fw-semibold">Email</label>
                        <input
                            className="form-control"
                            type="email"
                            placeholder="seu@email.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="form-label fw-semibold">Senha</label>
                        <input
                            className="form-control"
                            type="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button className="btn btn-primary w-100 py-2" type="submit">
                        Entrar
                    </button>
                </form>

                <div className="auth-footer">
                    Não tem conta? <Link to="/register">Cadastrar</Link>
                </div>
            </div>
        </div>
    );
}

export default Login;
