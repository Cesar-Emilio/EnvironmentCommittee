const URL = 'http://localhost:8080';
let memberList = [];

// Obtener todos los usuarios
const findAllMembers = async () => {
    try {
        const response = await fetch(`${URL}/api/user`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        const data = await response.json();
        memberList = data.data || [];
    } catch (error) {
        console.error('Error fetching members:', error);
    }
};

// Cargar la tabla de miembros
const loadTable = async () => {
    await findAllMembers();
    const tbody = document.querySelector("tbody");
    tbody.innerHTML = memberList.length
        ? memberList.map((member, index) => `
            <tr>
                <th>${index + 1}</th>
                <td>${member.name}</td>
                <td>${member.lastname}</td>
                <td>${member.username}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="viewMember(${member.id})" data-bs-toggle="modal" data-bs-target="#viewUserModal">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-success btn-sm" onclick="loadMember(${member.id})" data-bs-toggle="modal" data-bs-target="#editUserModal">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteMember(${member.id})">
                        <i class="bi bi-person-dash"></i>
                    </button>
                </td>
            </tr>
        `).join("")
        : `<tr><td colspan="6" class="text-center">No hay registros</td></tr>`;
};


// Registrar un nuevo miembro
const saveMember = async () => {
    const name = document.getElementById("addUserName").value.trim();
    const lastname = document.getElementById("addUserLastname").value.trim();
    const phone = document.getElementById("addUserPhone").value.trim();
    const email = document.getElementById("addUserEmail").value.trim();
    const username = document.getElementById("addUserUsername").value.trim();
    const password = document.getElementById("addUserPassword").value.trim();
    const roleId = document.getElementById("addUserRoleId").value;

    if (!name || !lastname || !phone || !email || !username || !password) {
        return Swal.fire("Error", "Por favor, completa todos los campos.", "error");
    }

    const newMember = {
        name,
        lastname,
        phone,
        email,
        username,
        password,
        role: { id: parseInt(roleId) },
    };

    Swal.fire({
        title: "¿Confirmar registro?",
        text: "El miembro será registrado.",
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Sí, registrar!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
    try {
        const response = await fetch(`${URL}/api/user`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(newMember),
        });

        if (response.ok) {
            Swal.fire("Registrado", "El miembro fue registrado exitosamente.", "success");
            // Vaciar los campos del formulario
            document.getElementById("addUserName").value = "";
            document.getElementById("addUserLastname").value = "";
            document.getElementById("addUserPhone").value = "";
            document.getElementById("addUserEmail").value = "";
            document.getElementById("addUserUsername").value = "";
            document.getElementById("addUserPassword").value = "";
            await loadTable();
            bootstrap.Modal.getInstance(document.getElementById("addUserModal")).hide();
        } else if (response.status === 409) {
            const errorMessage = await response.text();
            Swal.fire("Error", errorMessage || "El correo ya está registrado.", "error");
        } else {
            Swal.fire("Error", "No se pudo registrar el miembro.", "error");
        }
    } catch (error) {
        Swal.fire("Error", "Ocurrió un problema al registrar el miembro.", "error");
            }
        }   
    });
};

// Cargar datos de un miembro en el modal de edición
const loadMember = async (id) => {
    try {
        const response = await fetch(`${URL}/api/user/${id}`);
        const data = await response.json();
        const member = data.data;

        if (member) {
            document.getElementById("editUserName").value = member.name;
            document.getElementById("editUserLastname").value = member.lastname;
            document.getElementById("editUserPhone").value = member.phone;
            document.getElementById("editUserEmail").value = member.email;
            document.getElementById("editUserUsername").value = member.username; // Mostrar el username
            document.getElementById("editUserPassword").value = ""; // Dejar vacío
            document.getElementById("editUserRole").innerHTML = `<option value="${member.role.id}" selected>${member.role.name}</option>`; // Mostrar el rol
            document.getElementById("editUserName").dataset.memberId = id; // Almacenar el ID del miembro
        }
    } catch (error) {
        console.error("Error loading member:", error);
        Swal.fire("Error", "No se pudieron cargar los datos del miembro.", "error");
    }
};

// Actualizar usuario
const updateMember = async () => {
    const id = document.getElementById("editUserName").dataset.memberId;
    const name = document.getElementById("editUserName").value.trim();
    const lastname = document.getElementById("editUserLastname").value.trim();
    const phone = document.getElementById("editUserPhone").value.trim();
    const email = document.getElementById("editUserEmail").value.trim();
    const username = document.getElementById("editUserUsername").value.trim();
    const password = document.getElementById("editUserPassword").value.trim();
    const roleId = document.getElementById("editUserRole").value;

    if (!name || !lastname || !phone || !email || !username || !roleId) {
        return Swal.fire("Error", "Por favor, completa todos los campos obligatorios.", "error");
    }

    const updatedMember = {
        id,
        name,
        lastname,
        phone,
        email,
        username,
        role: { id: parseInt(roleId) },
    };

    if (password) {
        updatedMember.password = password; // Solo añadir la contraseña si no está vacía
    }

    // Confirmación antes de enviar los datos
    Swal.fire({
        title: "¿Confirmar edición?",
        text: "Los cambios serán guardados.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, guardar cambios!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/user`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedMember),
                });

                if (response.ok) {
                    Swal.fire("Actualizado", "El miembro fue actualizado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("editUserModal")).hide();
                } else {
                    const errorMessage = await response.text();
                    Swal.fire("Error", errorMessage || "No se pudo actualizar el miembro.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "Ocurrió un problema al actualizar el miembro.", "error");
            }
        } else {
            // Opcional: Agregar lógica para manejar cuando se presiona "Cancelar"
            console.log("Edición cancelada por el usuario.");
        }
    });
};

// Eliminar un miembro
const deleteMember = async (id) => {
    Swal.fire({
        title: "¿Estás seguro?",
        text: "No podrás revertir esta acción.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/user/${id}`, {
                    method: "DELETE",
                });

                if (response.ok) {
                    Swal.fire("Eliminado", "El miembro fue eliminado exitosamente.", "success");
                    await loadTable();
                } else {
                    Swal.fire("Error", "No se pudo eliminar el miembro.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo eliminar el miembro.", "error");
            }
        }
    });
};

// Ver detalles del miembro
const viewMember = async (id) => {
    try {
        const response = await fetch(`${URL}/api/user/${id}`);
        const data = await response.json();
        const member = data.data;

        const modalBody = document.querySelector("#viewUserModal .modal-body");
        if (!modalBody) {
            console.error("El modal para visualizar usuarios no está configurado correctamente.");
            return;
        }

        if (member) {
            modalBody.innerHTML = `
                <p><strong>Nombre:</strong> ${member.name} ${member.lastname}</p>
                <p><strong>Correo electrónico:</strong> ${member.email}</p>
                <p><strong>Teléfono:</strong> ${member.phone}</p>
                <p><strong>Rol:</strong> ${member.role.name}</p>
            `;
        } else {
            modalBody.innerHTML = "<p>No se encontraron detalles para este usuario.</p>";
        }
    } catch (error) {
        console.error("Error viewing member:", error);
        Swal.fire("Error", "No se pudieron cargar los detalles del usuario.", "error");
    }
};

// Limpiar campos al cerrar los modales
document.getElementById("addUserModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("addUserName").value = "";
    document.getElementById("addUserLastname").value = "";
    document.getElementById("addUserPhone").value = "";
    document.getElementById("addUserEmail").value = "";
    document.getElementById("addUserUsername").value = "";
    document.getElementById("addUserPassword").value = "";
});

document.getElementById("editUserModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("editUserName").value = "";
    document.getElementById("editUserLastname").value = "";
    document.getElementById("editUserPhone").value = "";
    document.getElementById("editUserEmail").value = "";
    document.getElementById("editUserPassword").value = "";
    document.getElementById("editUserUsername").value = "";
    document.getElementById("editUserRole").innerHTML = ""; // Limpiar el select de rol
    document.getElementById("editUserName").dataset.memberId = ""; // Limpiar ID almacenado
});

// Inicializar eventos
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("addUserModal").querySelector("form").addEventListener("submit", (e) => {
        e.preventDefault();
        saveMember();
    });

    document.getElementById("editUserModal").querySelector("form").addEventListener("submit", (e) => {
        e.preventDefault();
        updateMember();
    });

    loadTable();
});
