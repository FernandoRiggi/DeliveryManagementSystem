import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";
import { parseApiError } from "../utils/parseApiError";

function Register() {
    const [name, setName] = useState("");
    const [lastname, setLastname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const navigate = useNavigate();

    async function handleRegister(e) {
        e.preventDefault();
        setError("");
        setSuccess("");

        try {
            await api.post("/api/v1/register", { name, lastname, email, password });
            setSuccess("Usuário criado com sucesso! Redirecionando...");
            setTimeout(() => navigate("/login"), 1500);
        } catch (err) {
            setError(parseApiError(err, "Erro ao cadastrar. Tente novamente."));
        }
    }

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h1>Criar conta</h1>
                <p className="auth-subtitle">Preencha os dados abaixo para se cadastrar</p>

                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="alert alert-success" role="alert">
                        {success}
                    </div>
                )}

                <form onSubmit={handleRegister}>
                    <div className="row g-3 mb-3">
                        <div className="col-6">
                            <label className="form-label fw-semibold">Nome</label>
                            <input
                                className="form-control"
                                placeholder="João"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                            />
                        </div>
                        <div className="col-6">
                            <label className="form-label fw-semibold">Sobrenome</label>
                            <input
                                className="form-control"
                                placeholder="Silva"
                                value={lastname}
                                onChange={(e) => setLastname(e.target.value)}
                                required
                            />
                        </div>
                    </div>

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
                        Criar conta
                    </button>
                </form>

                <div className="auth-footer">
                    Já tem conta? <Link to="/login">Entrar</Link>
                </div>
            </div>
        </div>
    );
}

export default Register;
