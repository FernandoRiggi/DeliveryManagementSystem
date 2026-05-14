import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createOrder } from "../../services/orderService.js";
import { parseApiError } from "../../utils/parseApiError";

function CopyId({ id }) {
    const [copied, setCopied] = useState(false);
    function handleCopy() {
        navigator.clipboard.writeText(id);
        setCopied(true);
        setTimeout(() => setCopied(false), 1500);
    }
    return (
        <div className="created-order-id">
            <span className="created-order-label">ID do pedido</span>
            <div className="created-order-value">
                <code>{id}</code>
                <button className={`btn-copy ${copied ? "btn-copy--ok" : ""}`} onClick={handleCopy}>
                    {copied ? "✓ copiado" : "⧉ copiar"}
                </button>
            </div>
        </div>
    );
}

const ADDRESS_FIELDS = [
    { name: "Street",       label: "Rua",    col: "col-md-8" },
    { name: "Number",       label: "Número", col: "col-md-4" },
    { name: "Neighborhood", label: "Bairro", col: "col-md-6" },
    { name: "City",         label: "Cidade", col: "col-md-6" },
    { name: "State",        label: "Estado", col: "col-md-4" },
    { name: "Country",      label: "País",   col: "col-md-4" },
    { name: "Cep",          label: "CEP",    col: "col-md-4" },
];

function AddressFields({ prefix, form, onChange }) {
    return (
        <div className="row g-3">
            {ADDRESS_FIELDS.map(({ name, label, col }) => {
                const fieldName = prefix + name;
                return (
                    <div className={col} key={fieldName}>
                        <label className="form-label">{label}</label>
                        <input
                            className="form-control"
                            name={fieldName}
                            placeholder={label}
                            value={form[fieldName]}
                            onChange={onChange}
                            required
                        />
                    </div>
                );
            })}
        </div>
    );
}

function CreateOrder() {
    const navigate = useNavigate();
    const [error, setError] = useState("");
    const [createdId, setCreatedId] = useState(null);

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
        distanceKm: "",
    });

    function handleChange(e) {
        const { name, value } = e.target;
        setForm({ ...form, [name]: value });
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setCreatedId(null);

        try {
            const response = await createOrder({ ...form, distanceKm: Number(form.distanceKm) });
            setCreatedId(response.data.orderId);
        } catch (err) {
            setError(parseApiError(err, "Erro ao criar pedido. Verifique os dados e tente novamente."));
        }
    }

    return (
        <div className="container page-wrapper">
            <div className="page-card">
                <div className="page-header">
                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                        ← Voltar
                    </Link>
                    <h1>Criar Pedido</h1>
                </div>

                {error && <div className="alert alert-danger" role="alert">{error}</div>}

                {createdId && (
                    <div className="created-order-box">
                        <div className="created-order-header">✅ Pedido criado com sucesso!</div>
                        <CopyId id={createdId} />
                        <div className="created-order-actions">
                            <button className="btn btn-primary btn-sm" onClick={() => { setCreatedId(null); setError(""); setForm({ customerId: "", pickupStreet: "", pickupNumber: "", pickupNeighborhood: "", pickupCity: "", pickupState: "", pickupCountry: "Brasil", pickupCep: "", deliveryStreet: "", deliveryNumber: "", deliveryNeighborhood: "", deliveryCity: "", deliveryState: "", deliveryCountry: "Brasil", deliveryCep: "", distanceKm: "" }); }}>
                                Criar outro pedido
                            </button>
                            <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                                Voltar ao Dashboard
                            </Link>
                        </div>
                    </div>
                )}

                <form onSubmit={handleSubmit} style={{ display: createdId ? "none" : undefined }}>
                    <p className="section-label">Cliente</p>
                    <div className="row g-3">
                        <div className="col-12">
                            <label className="form-label">ID do cliente</label>
                            <input
                                className="form-control"
                                name="customerId"
                                placeholder="ex: 550e8400-e29b-41d4-a716-446655440000"
                                value={form.customerId}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <p className="section-label">Endereço de Coleta</p>
                    <AddressFields prefix="pickup" form={form} onChange={handleChange} />

                    <p className="section-label">Endereço de Entrega</p>
                    <AddressFields prefix="delivery" form={form} onChange={handleChange} />

                    <p className="section-label">Entrega</p>
                    <div className="row g-3">
                        <div className="col-md-4">
                            <label className="form-label">Distância (km)</label>
                            <input
                                className="form-control"
                                name="distanceKm"
                                type="number"
                                step="0.1"
                                min="0.1"
                                placeholder="0.0"
                                value={form.distanceKm}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mt-4">
                        <button className="btn btn-primary me-2" type="submit">
                            Criar pedido
                        </button>
                        <Link to="/dashboard" className="btn btn-outline-secondary">
                            Cancelar
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateOrder;
