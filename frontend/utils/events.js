const URL = 'http://localhost:8080';
let eventList = [];

// Obtener todos los eventos
const findAllEvents = async () => {
    try {
        const response = await fetch(`${URL}/api/event`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });
        const data = await response.json();
        eventList = data.data || [];
    } catch (error) {
        console.error("Error fetching events:", error);
    }
};

// Cargar la tabla de eventos
const loadTable = async () => {
    await findAllEvents();
    const tbody = document.getElementById("tbodyLoad");
    tbody.innerHTML = eventList.length
        ? eventList.map((item, index) => `
            <tr>
                <th>${index + 1}</th>
                <td>${item.title}</td>
                <td>${item.type.name}</td>
                <td>${item.date}</td>
                <td><span class="badge ${getStatusBadgeClass(item.status)}">${item.status}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="loadEventStatus(${item.id})" data-bs-toggle="modal" data-bs-target="#statusGroupModal">
                        <i class="bi bi-calendar4-week"></i>
                    </button>
                    <button class="btn btn-success btn-sm" onclick="loadEvent(${item.id})" data-bs-toggle="modal" data-bs-target="#editEventModal">
                        <i class="bi bi-pencil"></i>
                    </button>
                </td>
            </tr>
        `).join("")
        : `<tr><td colspan="6" class="text-center">No hay registros</td></tr>`;
};

// Cargar tipos de eventos para creación y edición
const loadEventTypes = async (selectElementId) => {
    try {
        const response = await fetch(`${URL}/api/type`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });
        const data = await response.json();
        const selectElement = document.getElementById(selectElementId);
        selectElement.innerHTML = ""; // Limpia las opciones anteriores

        data.data.forEach((type) => {
            const option = document.createElement("option");
            option.value = type.id; // El ID del tipo
            option.textContent = type.name; // El nombre visible
            selectElement.appendChild(option);
        });
    } catch (error) {
        console.error("Error al cargar los tipos de eventos:", error);
    }
};

// Obtener la clase del estado del evento
const getStatusBadgeClass = (status) => {
    const statusClasses = {
        "Próximamente": "bg-warning",
        "En ejecución": "bg-success",
        "Finalizado": "bg-secondary"
    };
    return statusClasses[status] || "bg-secondary";
};

// Registrar un nuevo evento
const saveEvent = async () => {
    const title = document.getElementById("addEventTitle").value.trim();
    const typeId = document.getElementById("addEventType").value.trim();
    const date = document.getElementById("addEventDate").value.trim();

    // Validar que los campos no estén vacíos
    if (!title || !typeId || !date) {
        return Swal.fire("Error", "Por favor, completa todos los campos.", "error");
    }

    // Crear el objeto del evento con el estado predeterminado
    const newEvent = {
        title: title,
        status: "Próximamente", // Estado predeterminado
        date: date,
        type: {
            id: parseInt(typeId), // Convertir el ID a número si es necesario
        }
    };

    Swal.fire({
        title: "¿Confirmar registro?",
        text: "El evento será registrado.",
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Sí, registrar!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/event`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify(newEvent)
                });

                if (response.ok) {
                    Swal.fire("Registrado!", "El evento fue registrado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("addEventModal")).hide();
                    document.getElementById("addEventTitle").value = "";
                    document.getElementById("addEventType").value = "";
                    document.getElementById("addEventDate").value = "";
                } else {
                    const errorData = await response.json();
                    Swal.fire("Error", errorData.message || "No se pudo registrar el evento.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo registrar el evento.", "error");
            }
        }
    });
};


// Cargar un evento en el modal de edición
const loadEvent = async (id) => {
    await loadEventTypes("editEventType"); // Carga los tipos antes de rellenar

    try {
        const response = await fetch(`${URL}/api/event/${id}`);
        const data = await response.json();
        const event = data.data;

        if (event) {
            document.getElementById("editEventTitle").value = event.title;
            document.getElementById("editEventType").value = event.type.id; // Seleccionar el tipo correspondiente
            document.getElementById("editEventDate").value = event.date;
            document.getElementById("editEventTitle").dataset.eventId = id;
        }
    } catch (error) {
        console.error("Error loading event:", error);
    }
};

// Actualizar un evento
const updateEvent = async () => {
    const title = document.getElementById("editEventTitle").value.trim();
    const typeId = document.getElementById("editEventType").value.trim();
    const date = document.getElementById("editEventDate").value.trim();
    const id = document.getElementById("editEventTitle").dataset.eventId;

    if (!title || !typeId || !date) {
        return Swal.fire("Error", "Por favor, completa todos los campos.", "error");
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
                // Obtén el estado actual del evento antes de actualizar
                const response = await fetch(`${URL}/api/event/${id}`);
                const data = await response.json();
                const currentStatus = data.data.status; // Estado actual del evento

                // Construye el objeto actualizado
                const updatedEvent = {
                    id: id,
                    title: title,
                    date: date,
                    status: currentStatus, // Mantén el estado actual
                    type: {
                        id: parseInt(typeId)
                    }
                };

                const updateResponse = await fetch(`${URL}/api/event`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(updatedEvent)
                });

                if (updateResponse.ok) {
                    Swal.fire("Actualizado!", "El evento fue actualizado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("editEventModal")).hide();
                } else {
                    Swal.fire("Error", "No se pudo actualizar el evento.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo actualizar el evento.", "error");
            }
        }
    });
};


// Función para cargar el estado actual del evento en el modal
const loadEventStatus = async (id) => {
    try {
        const response = await fetch(`${URL}/api/event/${id}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        const data = await response.json();

        if (data.data) {
            // Establecer el ID del evento en el modal para actualizarlo después
            document.getElementById("statusGroupModal").dataset.eventId = id;

            // Seleccionar el estado actual en los radio buttons
            const currentStatus = data.data.status;
            document.querySelectorAll('input[name="status"]').forEach((radio) => {
                if (radio.value === currentStatus) {
                    radio.checked = true;
                }
            });
        }
    } catch (error) {
        console.error("Error al cargar el estado del evento:", error);
        Swal.fire("Error", "No se pudo cargar el estado del evento.", "error");
    }
};

// Función para actualizar el estado del evento
const updateEventStatus = async () => {
    const id = document.getElementById("statusGroupModal").dataset.eventId;
    const newStatus = document.querySelector('input[name="status"]:checked').value;

    Swal.fire({
        title: "¿Confirmar cambio de estado?",
        text: "El estado del evento será actualizado.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, actualizar!",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/event/${id}`, {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({ status: newStatus })
                });

                if (response.ok) {
                    Swal.fire("Actualizado!", "El estado del evento fue actualizado exitosamente.", "success");
                    await loadTable();
                    bootstrap.Modal.getInstance(document.getElementById("statusGroupModal")).hide();
                } else {
                    const errorData = await response.json();
                    Swal.fire("Error", errorData.message || "No se pudo actualizar el estado del evento.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "No se pudo actualizar el estado del evento.", "error");
            }
        }
    });
};

// Función para establecer la fecha mínima en los campos de fecha
const setMinDate = (inputId) => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0"); // Mes en formato "MM"
    const day = String(today.getDate()).padStart(2, "0"); // Día en formato "DD"

    // Formato de fecha "YYYY-MM-DD" requerido por los inputs de tipo date
    const minDate = `${year}-${month}-${day}`;
    document.getElementById(inputId).setAttribute("min", minDate);
};

// Llamar a esta función para los campos de fecha cuando el documento esté cargado
document.addEventListener("DOMContentLoaded", () => {
    setMinDate("addEventDate"); // Para el modal de agregar evento
    setMinDate("editEventDate"); // Para el modal de editar evento
});

// Limpiar campos al cerrar los modales
document.getElementById("addEventModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("addEventTitle").value = ""; // Limpiar el título del evento
    document.getElementById("addEventType").value = ""; // Limpiar el tipo del evento
    document.getElementById("addEventDate").value = ""; // Limpiar la fecha
});

document.getElementById("editEventModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("editEventTitle").value = ""; // Limpiar el título del evento
    document.getElementById("editEventType").value = ""; // Limpiar el tipo del evento
    document.getElementById("editEventDate").value = ""; // Limpiar la fecha
    document.getElementById("editEventTitle").dataset.eventId = ""; // Limpiar el ID
});

document.getElementById("statusGroupModal").addEventListener("hidden.bs.modal", () => {
    document.getElementById("statusGroupModal").dataset.eventId = ""; // Limpiar el ID del evento
    document.querySelectorAll('input[name="status"]').forEach((radio) => {
        radio.checked = false; // Desmarcar todos los radio buttons
    });
});

// Inicializar eventos
document.getElementById("addEventModal").querySelector("form").addEventListener("submit", (e) => {
    e.preventDefault();
    saveEvent();
});
document.getElementById("editEventModal").querySelector("form").addEventListener("submit", (e) => {
    e.preventDefault();
    updateEvent();
});
loadTable();
loadEventTypes("addEventType");
