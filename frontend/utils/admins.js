// URL base para la API
const URL = 'http://localhost:8080';

// Función genérica para manejar solicitudes API
const apiRequest = async (endpoint, method = 'GET', data = null) => {
    try {
        const token = localStorage.getItem('token');
        const headers = {
            "Content-Type": "application/json",
            ...(token && { "Authorization": `Bearer ${token}` })
        };

        const response = await fetch(`${URL}${endpoint}`, {
            method,
            headers,
            body: data ? JSON.stringify(data) : null
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`API Error (${method} ${endpoint}):`, error.message);
        throw error;
    }
};

// Renderizar grupos
const renderGroups = async () => {
    try {
        const response = await apiRequest('/api/group');
        const groups = response.data || [];
        const container = document.getElementById('groupContainer');
        container.innerHTML = groups.length
            ? groups.map(group => `
                <div class="col-md-6 col-lg-4">
                    <div class="card shadow">
                        <div class="card-body">
                            <h5 class="card-title">${group.name}</h5>
                            <p><strong>Municipio:</strong> ${group.municipality}</p>
                            <p><strong>Colonia:</strong> ${group.neighborhood}</p>
                        </div>
                    </div>
                </div>`).join('')
            : '<p class="text-center">No hay grupos registrados.</p>';
    } catch (error) {
        console.error("Error al obtener grupos:", error);
    }
};

// Renderizar eventos
const renderEvents = async () => {
    try {
        const response = await apiRequest('/api/event');
        const events = response.data || [];
        const container = document.getElementById('eventContainer');
        container.innerHTML = events.length
            ? events.map(event => `
                <div class="col-md-6 col-lg-4">
                    <div class="card shadow">
                        <div class="card-body">
                            <h5 class="card-title">${event.title}</h5>
                            <p><strong>Fecha:</strong> ${event.date}</p>
                            <p><strong>Estado:</strong> ${event.status}</p>
                            <p><strong>Tipo:</strong> ${event.type.name}</p>
                        </div>
                    </div>
                </div>`).join('')
            : '<p class="text-center">No hay eventos registrados.</p>';
    } catch (error) {
        console.error("Error al obtener eventos:", error);
    }
};

// Renderizar usuarios
const renderUsers = async () => {
    try {
        const response = await apiRequest('/api/user');
        const users = response.data || [];
        const container = document.getElementById('userContainer');
        container.innerHTML = users.length
            ? users.map(user => `
                <div class="col-md-6 col-lg-4">
                    <div class="card shadow">
                        <div class="card-body">
                            <h5 class="card-title">${user.name} ${user.lastname}</h5>
                            <p><strong>Teléfono:</strong> ${user.phone}</p>
                            <p><strong>Correo:</strong> ${user.email}</p>
                            <p><strong>Rol:</strong> ${user.role.name}</p>
                        </div>
                    </div>
                </div>`).join('')
            : '<p class="text-center">No hay usuarios registrados.</p>';
    } catch (error) {
        console.error("Error al obtener usuarios:", error);
    }
};

// Inicializar páginas
const initializePage = () => {
    const path = window.location.pathname;
    if (path.includes('manage-groups.html')) {
        renderGroups();
    } else if (path.includes('manage-events.html')) {
        renderEvents();
    } else if (path.includes('manage-users.html')) {
        renderUsers();
    }
};

document.addEventListener('DOMContentLoaded', initializePage);

export { renderGroups, renderEvents, renderUsers, apiRequest };
