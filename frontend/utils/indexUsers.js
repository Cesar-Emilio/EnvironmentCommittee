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

// Función para guardar un nuevo usuario
const saveUser = async (email, password) => {
    try {
        const data = { email, password };
        const response = await apiRequest('/api/user', 'POST', data);

        if (response) {
            Swal.fire("Éxito", "Usuario guardado correctamente.", "success");
            return response;
        } else {
            throw new Error('No se pudo guardar el usuario.');
        }
    } catch (error) {
        Swal.fire("Error", "Ocurrió un error al guardar el usuario.", "error");
        console.error("Error en saveUser:", error);
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
    if (user) {
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
                console.error("Rol desconocido: No se puede redirigir");
        }
    } else {
        console.error("Usuario no autenticado");
    }
};

// Función para iniciar sesión
const loginUser = async (credentials) => {
    try {
        const response = await apiRequest('/api/auth/login', 'POST', credentials);
        if (response.code === 200) {
            const { user, token } = response.data;
            localStorage.setItem('user', JSON.stringify(user));
            localStorage.setItem('token', token);
            return { user, token };
        } else {
            throw new Error(response.message || 'Error desconocido al iniciar sesión');
        }
    } catch (error) {
        console.error("Error en loginUser:", error);
        throw error;
    }
};

const updateUserProfile = async (updatedUser) => {
    try {
        const user = JSON.parse(localStorage.getItem("user"));
        const response = await apiRequest(`/api/user/${user.id}`, 'PUT', updatedUser);
        if (response) {
            localStorage.setItem("user", JSON.stringify(response.data));
            Swal.fire("Éxito", "Perfil actualizado correctamente.", "success");
        }
    } catch (error) {
        Swal.fire("Error", "No se pudo actualizar el perfil.", "error");
        console.error("Error al actualizar perfil:", error);
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
            ? users.map((user, index) => `
                <tr>
                    <td>${index + 1}</td>
                    <td>${user.name} ${user.lastname}</td>
                    <td>${user.email}</td>
                    <td>${user.phone}</td>
                    <td>${user.role.name}</td>
                    <td>
                        <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Eliminar</button>
                    </td>
                </tr>
            `).join('')
            : '<tr><td colspan="6" class="text-center">No hay usuarios registrados.</td></tr>';
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

document.addEventListener('DOMContentLoaded', () => {
    initializePage(); // Inicializa la página según su contenido

    const logoutButton = document.querySelector(".logout-btn");
    if (logoutButton) {
        logoutButton.addEventListener("click", (e) => {
            e.preventDefault();
            logoutUser();
        });
    }

    const homeLink = document.querySelector(".nav-link[href='../index.html']");
    if (homeLink) {
        homeLink.addEventListener("click", (e) => {
            e.preventDefault();
            redirectToHome();
        });
    }
});

export { apiRequest, logoutUser, updateUserProfile, renderGroups, renderEvents, renderUsers, loginUser };
