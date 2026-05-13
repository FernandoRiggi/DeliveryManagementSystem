import { useEffect } from "react";
import api from "../services/api";

function Login() {

    useEffect(() => {

        api.get("/test")
            .then(response => {
                console.log(response.data);
            })
            .catch(error => {
                console.log(error);
            });

    }, []);
    return (

        <div className="container mt-5">

            <div className="card p-4">

                <h1>Login</h1>

                <input
                    className="form-control mb-3"
                    placeholder="Email"
                />

                <input
                    className="form-control mb-3"
                    type="password"
                    placeholder="Senha"
                />

                <button className="btn btn-primary">
                    Entrar
                </button>

            </div>

        </div>
    );
}

export default Login;