import { useState } from "react";
import api from "../services/api";

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    async function handleLogin(e) {

        e.preventDefault();

        try {

            const response = await api.post(
                "/api/v1/authenticate",
                {
                    username,
                    password
                }
            );

            localStorage.setItem(
                "token",
                response.data.token
            );

            alert("Login realizado!");

        } catch(error) {

            if (error.response?.status === 401) {
                alert("Email ou senha inválidos.");
                return;
            }

            if (!error.response) {
                alert("Não foi possível conectar ao backend. Verifique se ele está rodando na porta 8080.");
                return;
            }

            alert("Erro no login. Status: " + error.response.status);

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

            </div>

        </div>
    );
}

export default Login;