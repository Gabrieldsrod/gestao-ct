
const apiUrl = import.meta.env.BASE_URL; 

export const api = {
    get: async (endpoint: string) => {
        const response = await fetch(`${apiUrl}${endpoint}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    },

    post: async (endpoint: string, data: any) => {
        const response = await fetch(`${apiUrl}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    },

    put: async (endpoint: string, data: any) => {
        const response = await fetch(`${apiUrl}${endpoint}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });     
    if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    },

    patch: async (endpoint: string, data: any) => {
        const response = await fetch(`${apiUrl}${endpoint}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }); 
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
    }
}
