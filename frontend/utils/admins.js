const URL = 'http://localhost:8080';
let adminList = [];
let groupList = []; // Variable para almacenar los grupos

// Obtener todos los administradores con rol 1
const findAllAdmins = async () => {
    try {
        const response = await fetch(`${URL}/api/user`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        const data = await response.json();

        // Filtrar solo administradores (rol 1)
        adminList = (data.data || []).filter(user => user.role && user.role.id === 1);
    } catch (error) {
        console.error('Error al obtener administradores:', error);
    }
};

// Obtener todos los grupos
const findAllGroups = async () => {
    try {
        const response = await fetch(`${URL}/api/group`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        const data = await response.json();
        groupList = data.data || [];
    } catch (error) {
        console.error('Error al obtener grupos:', error);
    }
};

// Cargar la tabla de administradores
const loadAdminTable = async () => {
    await findAllAdmins();
    const tbody = document.getElementById('tbodyLoad');
    tbody.innerHTML = adminList.length
        ? adminList.map((admin, index) => `
            <tr>
                <td>${index + 1}</td>
                <td>${admin.name}</td>
                <td>${admin.lastname}</td>
                <td>${admin.username}</td>
                <td class="text-center">
                    <button class="btn btn-secondary btn-sm me-2" onclick="viewAdmin(${admin.id})" data-bs-toggle="modal" data-bs-target="#viewAdminModal">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-success btn-sm me-2" onclick="loadAdmin(${admin.id})" data-bs-toggle="modal" data-bs-target="#editAdminModal">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteAdmin(${admin.id})">
                        <i class="bi bi-person-dash"></i>
                    </button>
                </td>
            </tr>
        `).join("")
        : `<tr><td colspan="5" class="text-center">No hay registros</td></tr>`;
};

// Registrar un nuevo administrador
const saveAdmin = async () => {
    const name = document.getElementById('addAdminName').value.trim();
    const lastname = document.getElementById('addAdminLastname').value.trim();
    const phone = document.getElementById('addAdminPhone').value.trim();
    const email = document.getElementById('addAdminEmail').value.trim();
    const username = document.getElementById('addAdminUsername').value.trim();
    const password = document.getElementById('addAdminPassword').value.trim();

    if (!name || !lastname || !phone || !email || !username || !password) {
        return Swal.fire('Error', 'Por favor, completa todos los campos.', 'error');
    }

    const newAdmin = {
        name,
        lastname,
        phone,
        email,
        username,
        password,
        role: { id: 1 }, // Rol predeterminado de administrador
    };

    try {
        const response = await fetch(`${URL}/api/user`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newAdmin),
        });

        if (response.ok) {
            Swal.fire('Registrado', 'El administrador fue registrado exitosamente.', 'success');
            document.getElementById('addAdminForm').reset(); // Limpia el formulario
            await loadAdminTable();
            bootstrap.Modal.getInstance(document.getElementById('addAdminModal')).hide();
        } else {
            const errorData = await response.json();
            Swal.fire('Error', errorData.message || 'No se pudo registrar el administrador.', 'error');
        }
    } catch (error) {
        Swal.fire('Error', 'Ocurrió un problema al registrar el administrador.', 'error');
    }
};

// Limpia los campos al cerrar el modal de agregar
document.getElementById('addAdminModal').addEventListener('hidden.bs.modal', () => {
    document.getElementById('addAdminForm').reset();
});

// Cargar datos de un administrador en el modal de edición
const loadAdmin = async (id) => {
    try {
        const response = await fetch(`${URL}/api/user/${id}`);
        const data = await response.json();
        const admin = data.data;

        if (admin) {
            document.getElementById('editAdminName').value = admin.name;
            document.getElementById('editAdminLastname').value = admin.lastname;
            document.getElementById('editAdminPhone').value = admin.phone;
            document.getElementById('editAdminEmail').value = admin.email;
            document.getElementById('editAdminUsername').value = admin.username; // Llenar campo de usuario
            document.getElementById('editAdminPassword').value = ''; // Mantener vacío el campo de contraseña
            document.getElementById('editAdminName').dataset.adminId = id;

            // Cargar los grupos disponibles en el select
            await findAllGroups();
            const groupSelect = document.getElementById('editAdminGroup');
            groupSelect.innerHTML = groupList.map(group => `
                <option value="${group.id}" ${admin.group?.id === group.id ? 'selected' : ''}>
                    ${group.name}
                </option>
            `).join("");
        }
    } catch (error) {
        console.error('Error al cargar administrador:', error);
    }
};


// Actualizar un administrador
const updateAdmin = async () => {
    const id = document.getElementById('editAdminName').dataset.adminId;
    const name = document.getElementById('editAdminName').value.trim();
    const lastname = document.getElementById('editAdminLastname').value.trim();
    const phone = document.getElementById('editAdminPhone').value.trim();
    const email = document.getElementById('editAdminEmail').value.trim();
    const username = document.getElementById('editAdminUsername').value.trim();
    const password = document.getElementById('editAdminPassword').value.trim();
    const groupId = document.getElementById('editAdminGroup').value;

    if (!name || !lastname || !phone || !email || !username || !groupId) {
        return Swal.fire("Error", "Por favor, completa todos los campos obligatorios.", "error");
    }

    const updatedAdmin = {
        id: parseInt(id),
        name,
        lastname,
        phone,
        email,
        username,
        password: password ? password : undefined, // Enviar solo si es necesario
        role: { id: 1 }, // Rol fijo de administrador
        group: { id: parseInt(groupId) }, // Grupo seleccionado
    };

    Swal.fire({
        title: "¿Confirmar edición?",
        text: "Los cambios serán guardados.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, guardar cambios",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/user`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedAdmin),
                });

                if (response.ok) {
                    Swal.fire("Actualizado", "El administrador fue actualizado exitosamente.", "success");
                    await loadAdminTable(); // Llama a la función correcta
                    bootstrap.Modal.getInstance(document.getElementById("editAdminModal")).hide(); // Cerrar modal
                } else {
                    const errorMessage = await response.json();
                    Swal.fire("Error", errorMessage.message || "No se pudo actualizar el administrador.", "error");
                }
            } catch (error) {
                console.error("Error:", error);
                Swal.fire("Error", "Ocurrió un problema al actualizar el administrador.", "error");
            }
        } else {
            console.log("Edición cancelada por el usuario.");
        }
    });
};


// Eliminar un administrador
const deleteAdmin = async (id) => {
    Swal.fire({
        title: '¿Estás seguro?',
        text: 'No podrás revertir esta acción.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/user/${id}`, {
                    method: 'DELETE',
                });

                if (response.ok) {
                    Swal.fire('Eliminado', 'El administrador fue eliminado exitosamente.', 'success');
                    await loadAdminTable();
                } else {
                    Swal.fire('Error', 'No se pudo eliminar el administrador.', 'error');
                }
            } catch (error) {
                Swal.fire('Error', 'No se pudo eliminar el administrador.', 'error');
            }
        }
    });
};

// Ver detalles del administrador
const viewAdmin = async (id) => {
    try {
        // Obtener información del administrador
        const response = await fetch(`${URL}/api/user/${id}`);
        const data = await response.json();
        const admin = data.data;

        if (admin) {
            // Actualizar datos del administrador en el modal
            document.getElementById('viewAdminPhone').textContent = admin.phone || "Sin información";
            document.getElementById('viewAdminEmail').textContent = admin.email || "Sin información";

            // Verificar si tiene un grupo asignado
            const groupName = admin.group && admin.group.name ? admin.group.name : "No asignado"; // Verifica si existe un grupo
            document.getElementById('viewAdminGroup').textContent = groupName; // Mostrar el nombre del grupo
        } else {
            Swal.fire("Error", "No se pudo cargar la información del administrador.", "error");
        }
    } catch (error) {
        console.error('Error al cargar la información del administrador:', error);
        Swal.fire("Error", "Ocurrió un problema al cargar la información del administrador.", "error");
    }
};

// Inicializar eventos
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('addAdminModal').querySelector('.btn-success').addEventListener('click', saveAdmin);
    loadAdminTable();
});
