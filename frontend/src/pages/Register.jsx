import { useState } from "react";
import api from "../services/api";

function Register() {

    const [name, setName] = useState("");
    const [lastname, setLastname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    async function handleRegister(e) {

        e.preventDefault();

        try {

            const response = await api.post(
                "/api/v1/register",
                {
                    name,
                    lastname,
                    email,
                    password
                }
            );

            console.log(response.data);

            alert("Usuário criado!");

        } catch(error) {

            console.log(error);

            alert("Erro ao cadastrar");

        }
    }

    return (

        <div className="container mt-5">

            <div className="card p-4">

                <h1>Cadastro</h1>

                <form onSubmit={handleRegister}>

                    <input
                        className="form-control mb-3"
                        placeholder="Nome"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        placeholder="Sobrenome"
                        value={lastname}
                        onChange={(e) => setLastname(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        type="password"
                        placeholder="Senha"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <button
                        className="btn btn-success"
                        type="submit"
                    >
                        Cadastrar
                    </button>

                </form>

            </div>

        </div>
    );
}

export default Register;