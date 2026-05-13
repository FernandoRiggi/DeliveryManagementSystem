import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    async function handleLogin(e) {

        e.preventDefault();

        console.log("clicou");

        try {

            const response = await api.post(
                "/api/v1/authenticate",
                {
                    username,
                    password
                }
            );

            console.log(response.data);

            localStorage.setItem(
                "token",
                response.data.token
            );

            navigate("/dashboard");

        } catch(error) {

            console.log(error);

            console.log(error.response);

            alert("Erro no login");

        }
    }

    return (

        <div className="container mt-5">

            <div className="card p-4">

                <h1>Login</h1>

                <form onSubmit={handleLogin}>

                    <input
                        className="form-control mb-3"
                        placeholder="Email"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        type="password"
                        placeholder="Senha"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <button
                        className="btn btn-primary"
                        type="submit"
                    >
                        Entrar
                    </button>

                </form>

                <div className="mt-3">
                    <span>Não tem conta? </span>
                    <Link to="/register">Cadastrar</Link>
                </div>

            </div>

        </div>
    );
}

export default Login;