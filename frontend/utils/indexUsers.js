const URL = 'http://localhost:8080';
let eventList = [];

// Obtener todos los eventos desde el servidor
const findAllEvents = async () => {
    try {
        const response = await fetch(`${URL}/api/event`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        const data = await response.json();
        eventList = data.data || [];
    } catch (error) {
        console.error("Error fetching events:", error);
    }
};

// Obtener la clase del estado del evento
const getStatusBadgeClass = (status) => {
    const statusClasses = {
        "Próximamente": "bg-warning text-white",
        "En ejecución": "bg-success text-white",
        "Finalizado": "bg-secondary text-white",
        "Aceptado": "bg-primary text-white",
        "Declinado": "bg-danger text-white"
    };
    return statusClasses[status] || "bg-secondary text-white";
};

// Renderizar eventos como tarjetas
const renderEventCards = async () => {
    await findAllEvents(); // Obtener los eventos
    const container = document.getElementById("eventCardsContainer");
    container.innerHTML = eventList.length
        ? eventList.map(event => `
            <div class="col-lg-4 col-md-6 col-sm-12 mb-4">
                <div class="card shadow border-success">
                    <div class="card-body text-center">
                        <div class="icon-card text-success">
                            <i class="bi bi-leaf-fill"></i>
                        </div>
                        <h5 class="card-title mt-3">${event.title}</h5>
                        <span class="badge ${getStatusBadgeClass(event.status)}">${event.status}</span>
                        <p class="mt-3 mb-1"><strong>Fecha:</strong> ${event.date}</p>
                        <p><strong>Tipo de Evento:</strong> ${event.type.name}</p>
                        <div class="d-flex justify-content-center">
                            <button class="btn btn-success btn-sm" onclick="acceptEvent(${event.id})">Aceptar</button>
                            <button class="btn btn-danger btn-sm" onclick="declineEvent(${event.id})">Declinar</button>
                        </div>
                    </div>
                </div>
            </div>
        `).join("")
        : `<div class="text-center mt-4"><p>No hay eventos registrados</p></div>`;
};

// Función para aceptar un evento
const acceptEvent = async (id) => {
    Swal.fire({
        title: "¿Aceptar evento?",
        text: "¿Estás seguro de aceptar este evento?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, aceptar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#28a745",
        cancelButtonColor: "#d33"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/event/${id}`, {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ status: "Aceptado" })
                });

                if (response.ok) {
                    Swal.fire("Aceptado", "Has aceptado el evento.", "success");
                    renderEventCards();
                } else {
                    Swal.fire("Error", "No se pudo aceptar el evento.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "Ocurrió un problema al aceptar el evento.", "error");
            }
        }
    });
};

// Función para declinar un evento
const declineEvent = async (id) => {
    Swal.fire({
        title: "¿Declinar evento?",
        text: "¿Estás seguro de declinar este evento?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, declinar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#3085d6"
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`${URL}/api/event/${id}`, {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ status: "Declinado" })
                });

                if (response.ok) {
                    Swal.fire("Declinado", "Has declinado el evento.", "success");
                    renderEventCards();
                } else {
                    Swal.fire("Error", "No se pudo declinar el evento.", "error");
                }
            } catch (error) {
                Swal.fire("Error", "Ocurrió un problema al declinar el evento.", "error");
            }
        }
    });
};

// Inicializar eventos
document.addEventListener("DOMContentLoaded", () => {
    renderEventCards();
});
