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

// Función para cerrar sesión
const logoutUser = () => {
    Swal.fire({
        title: "¿Cerrar sesión?",
        text: "¿Estás seguro de que deseas cerrar sesión?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, cerrar sesión",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#6c757d"
    }).then((result) => {
        if (result.isConfirmed) {
            localStorage.removeItem("user");
            localStorage.removeItem("token");
            window.location.href = "../index.html";
        }
    });
};


// Redirigir al inicio del usuario según su rol
const redirectToHome = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.role && user.role.name) {
        const role = user.role.name;
        switch (role) {
            case "ADMIN":
                window.location.href = "../admin_principal/index.html";
                break;
            case "ADMINGROUP":
                window.location.href = "../admin_group/index.html";
                break;
            case "MEMBER":
                window.location.href = "../member/index.html";
                break;
            default:
                console.error("Rol desconocido. Redirigiendo a login.");
                window.location.href = "../login.html";
        }
    } else {
        console.error("Usuario no autenticado. Redirigiendo a login.");
        window.location.href = "../login.html";
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

document.addEventListener("DOMContentLoaded", () => {
    // Botón de cerrar sesión
    const logoutBtn = document.querySelector(".logout-btn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logoutUser();
        });
    } else {
        console.warn("Botón de cerrar sesión no encontrado.");
    }

    // Enlace de inicio (Home)
    const homeLink = document.querySelector(".nav-link[href='../index.html']");
    if (homeLink) {
        homeLink.addEventListener("click", (e) => {
            e.preventDefault();
            redirectToHome();
        });
    } else {
        console.warn("Enlace de Home no encontrado.");
    }
});

document.querySelector(".nav-link[href='../index.html']").addEventListener("click", (e) => {
    e.preventDefault();
    redirectToHome();
});

export { renderGroups, renderEvents, renderUsers };
