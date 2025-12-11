const API_URL = "http://localhost:8080/api/auth";

    const handleResponse = async (response) => {
    if (!response.ok) {
        const errText = await response.text();
        throw new Error(errText || "Lỗi không xác định từ server");
    }
    const result = await response.json();

    return result.data;
    };

    const getToken = () => {
        return localStorage.getItem("token");
    };

    export const login = async(credentials) => {
    return await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
    });
}
export const register = async(credentials) => {
    return await fetch(`${API_URL}/register`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(credentials),
    })
}

    export const getProfile = async () => {
        const token = getToken();
        const response = await fetch(`${API_URL}/profile/me`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token ? `Bearer ${token}` : "",
            },
        });

        return await handleResponse(response);
    };


    export const updateProfile = async (credentials) => {
        const token = getToken();
        const response = await fetch(`${API_URL}/update/me`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": token ? `Bearer ${token}` : "",
            },
            body: JSON.stringify(credentials),
        });

        return await handleResponse(response);
    };
