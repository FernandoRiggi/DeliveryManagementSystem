const UUID_EXAMPLE = "ex: 550e8400-e29b-41d4-a716-446655440000";

const MESSAGE_MAP = [
    { match: /invalid uuid/i, text: `ID inválido. Informe um UUID válido (${UUID_EXAMPLE}).` },
    { match: /order not found/i, text: "Pedido não encontrado." },
    { match: /customer not found/i, text: "Cliente não encontrado." },
    { match: /delivery.?man not found/i, text: "Entregador não encontrado." },
    { match: /already exists/i, text: "Este registro já existe." },
    { match: /email.*already/i, text: "Este e-mail já está cadastrado." },
    { match: /bad credentials/i, text: "Email ou senha incorretos." },
    { match: /invalid.*status/i, text: "Operação não permitida para o status atual do pedido." },
];

export function parseApiError(error, fallback = "Ocorreu um erro inesperado. Tente novamente.") {
    const status = error.response?.status;
    const serverMessage = error.response?.data?.message || error.response?.data?.error || "";

    for (const rule of MESSAGE_MAP) {
        if (rule.match.test(serverMessage)) {
            return rule.text;
        }
    }

    if (status === 404) return "Registro não encontrado.";
    if (status === 409) return "Conflito: este registro já existe.";
    if (status === 403) return "Você não tem permissão para realizar esta ação.";
    if (status === 500) return "Erro interno do servidor. Tente novamente mais tarde.";

    if (serverMessage) return serverMessage;

    return fallback;
}