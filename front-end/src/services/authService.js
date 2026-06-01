const API_URL = "http://localhost:8080/api/auth";

    export const login = async(credentials) => {
        return await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(credentials),
        });
    };

    export const logout = async () => {
        const accessToken = sessionStorage.getItem("accessToken");

        if (!accessToken) {
            throw new Error("No access token found");
        }

        return await fetch(`${API_URL}/logout`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });
    };

    export const register = async(credentials) => {
        return await fetch(`${API_URL}/register`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(credentials),
        });    
    }

    export const forgotPassword = async(email) => {
        return await fetch(`${API_URL}/forgot-password`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ email }),
        });
    }

    // token và newPassword thì sẽ gửi bằng @RequestParam
    export const resetPassword = async(request) => {
        return await fetch(`${API_URL}/reset-password`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(request),
        });
    }

    export const refresh = async () => {
        const refreshToken = sessionStorage.getItem("refreshToken");
        console.log("Refresh token:", refreshToken);
        console.log("refresh status", refresh.status);
        
        if (!refreshToken) {
            throw new Error("No refresh token found");
        }
        return await fetch(`${API_URL}/refresh`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
        });
    }