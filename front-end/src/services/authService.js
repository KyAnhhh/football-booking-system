const API_URL = "http://localhost:8080/auth";

export const login = async(credentials) => {
    return await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
    });
}