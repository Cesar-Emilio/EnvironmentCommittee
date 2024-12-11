const URL = 'http://localhost:8080';
let typeEventList = [];

// Obtener todos los tipos de eventos
const findAllTypeEvents = async () => {
    try {
        const response = await fetch(`${URL}/api/type`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });
        const data = await response.json();
        typeEventList = data.data || [];
    } catch (error) {
        console.error('Error fetching types:', error);
    }
};

// Cargar la tabla de tipos de eventos
const loadTable = async () => {
    await findAllTypeEvents();
    const tbody = document.getElementById("tbodyLoad");
    tbody.innerHTML = typeEventList.length
        ? typeEventList.map((item, index) => `
            <tr>
                <th>${index + 1}</th>
                <td>${item.name}</td>
                <td>
                    <button class="btn btn-success" onclick="loadType(${item.id})" data-bs-toggle="modal" data-bs-target="#editEventModal"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-danger" onclick="confirmDeleteType(${item.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join("")
        : `<tr><td colspan="3" class="text-center">No hay registros</td></tr>`;
};

// Registrar un nuevo tipo de evento con confirmación
const saveType = async () => {
    const nameInput = document.getElementById("addEventTitle");
    const newType = { name: nameInput.value.trim() };

    if (!newType.name) {
        return Swal.fire("Error", "El nombre no puede estar vacío.", "error");
    }

    Swal.fire({
        title: "¿Confirmar registro?",
        text: "El tipo de evento será registrado.",
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Sí, registrar!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/type`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(newType)
                });
                if (response.ok) {
                    Swal.fire("Registrado!", "El tipo de evento fue registrado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("addEventModal")).hide();
                    document.getElementById("saveTypeForm").reset(); // Limpieza
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo registrar el tipo de evento.", "error");
            }
        }
    });
};

// Cargar los datos en el modal de edición
const loadType = async (id) => {
    try {
        const response = await fetch(`${URL}/api/type/${id}`);
        const data = await response.json();
        document.getElementById("editEventTitle").value = data.data.name;
        document.getElementById("editEventTitle").dataset.typeId = id;
    } catch (error) {
        console.error("Error loading type:", error);
    }
};

// Actualizar tipo de evento con confirmación
const updateType = async () => {
    const nameInput = document.getElementById("editEventTitle");
    const id = nameInput.dataset.typeId;
    const updatedType = { id, name: nameInput.value.trim() };

    if (!updatedType.name) {
        return Swal.fire("Error", "El nombre no puede estar vacío.", "error");
    }

    Swal.fire({
        title: "¿Confirmar edición?",
        text: "Los cambios serán guardados.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, guardar cambios!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/type`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedType)
                });
                if (response.ok) {
                    Swal.fire("Actualizado!", "El tipo de evento fue actualizado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("editEventModal")).hide();
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo actualizar el tipo de evento.", "error");
            }
        }
    });
};

// Confirmar y eliminar un tipo de evento
const confirmDeleteType = (id) => {
    Swal.fire({
        title: "¿Estás seguro?",
        text: "No podrás revertir esta acción.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/type/${id}`, { method: "DELETE" });
                if (response.ok) {
                    Swal.fire("Eliminado!", "El tipo de evento fue eliminado exitosamente.", "success");
                    await loadTable();
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo eliminar el tipo de evento.", "error");
            }
        }
    });
};

// Limpiar campos al cerrar los modales
document.getElementById("addEventModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("saveTypeForm").reset(); // Limpiar formulario de agregar
});

document.getElementById("editEventModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("editEventTitle").value = ""; // Limpiar formulario de edición
    document.getElementById("editEventTitle").dataset.typeId = ""; // Limpiar ID
});

// Inicializar eventos y cargar tabla
document.getElementById("registerTypeButton").addEventListener("click", saveType);
document.getElementById("updateTypeButton").addEventListener("click", updateType);
loadTable();
