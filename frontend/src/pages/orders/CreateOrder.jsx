import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../services/api";

function CreateOrder() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        customerId: "",
        pickupStreet: "",
        pickupNumber: "",
        pickupNeighborhood: "",
        pickupCity: "",
        pickupState: "",
        pickupCountry: "Brasil",
        pickupCep: "",
        deliveryStreet: "",
        deliveryNumber: "",
        deliveryNeighborhood: "",
        deliveryCity: "",
        deliveryState: "",
        deliveryCountry: "Brasil",
        deliveryCep: "",
        distanceKm: ""
    });

    function handleChange(e) {
        const { name, value } = e.target;

        setForm({
            ...form,
            [name]: value
        });
    }

    async function handleSubmit(e) {
        e.preventDefault();

        try {
            await api.post("/api/v1/orders", {
                ...form,
                distanceKm: Number(form.distanceKm)
            });

            alert("Pedido criado com sucesso!");
            navigate("/dashboard");
        } catch (error) {
            console.log(error);
            alert(error.response?.data?.message || "Erro ao criar pedido");
        }
    }

    return (
        <div className="container mt-5">
            <div className="card p-4">
                <h1>Criar Pedido</h1>

                <form onSubmit={handleSubmit}>
                    <h4 className="mt-3">Cliente</h4>

                    <input
                        className="form-control mb-3"
                        name="customerId"
                        placeholder="ID do cliente"
                        value={form.customerId}
                        onChange={handleChange}
                        required
                    />

                    <h4 className="mt-3">Endereço de coleta</h4>

                    <input className="form-control mb-3" name="pickupStreet" placeholder="Rua" value={form.pickupStreet} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="pickupNumber" placeholder="Número" value={form.pickupNumber} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="pickupNeighborhood" placeholder="Bairro" value={form.pickupNeighborhood} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="pickupCity" placeholder="Cidade" value={form.pickupCity} onChange={handleChange}required />
                    <input className="form-control mb-3" name="pickupState" placeholder="Estado" value={form.pickupState} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="pickupCountry" placeholder="País" value={form.pickupCountry} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="pickupCep" placeholder="CEP" value={form.pickupCep} onChange={handleChange} required/>

                    <h4 className="mt-3">Endereço de entrega</h4>

                    <input className="form-control mb-3" name="deliveryStreet" placeholder="Rua" value={form.deliveryStreet} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="deliveryNumber" placeholder="Número" value={form.deliveryNumber} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="deliveryNeighborhood" placeholder="Bairro" value={form.deliveryNeighborhood} onChange={handleChange}required />
                    <input className="form-control mb-3" name="deliveryCity" placeholder="Cidade" value={form.deliveryCity} onChange={handleChange}required />
                    <input className="form-control mb-3" name="deliveryState" placeholder="Estado" value={form.deliveryState} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="deliveryCountry" placeholder="País" value={form.deliveryCountry} onChange={handleChange} required/>
                    <input className="form-control mb-3" name="deliveryCep" placeholder="CEP" value={form.deliveryCep} onChange={handleChange} required/>

                    <h4 className="mt-3">Entrega</h4>

                    <input
                        className="form-control mb-3"
                        name="distanceKm"
                        type="number"
                        step="0.1"
                        placeholder="Distância em KM"
                        value={form.distanceKm}
                        onChange={handleChange}
                        required
                    />

                    <button className="btn btn-success me-2" type="submit">
                        Criar pedido
                    </button>

                    <Link to="/dashboard" className="btn btn-secondary">
                        Voltar
                    </Link>
                </form>
            </div>
        </div>
    );
}

export default CreateOrder;