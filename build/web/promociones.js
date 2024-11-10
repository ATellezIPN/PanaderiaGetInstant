document.addEventListener("DOMContentLoaded", function() {
    // Llama a la función para cargar las promociones
    cargarPromociones();
});

function cargarPromociones() {
    fetch('http://localhost:8080/Panaderia/obtenerPromociones')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la red');
            }
            return response.json();
        })
        .then(data => {
            mostrarPromociones(data);
        })
        .catch(error => {
            console.error('Hubo un problema con la solicitud Fetch:', error);
            document.getElementById('promociones-grid').innerHTML = "<p>Error al cargar las promociones.</p>";
        });
}

function mostrarPromociones(promociones) {
    const promocionesGrid = document.getElementById('promociones-grid');
    promocionesGrid.innerHTML = ''; // Limpia el contenedor

    promociones.forEach(promocion => {
        const promocionCard = document.createElement('div');
        promocionCard.classList.add('promocion-card');

        promocionCard.innerHTML = `
            <img src="${promocion.foto}" alt="${promocion.nombre}" class="producto-imagen">
            <h3>${promocion.nombre}</h3>
            <p>${promocion.descripcion}</p>
            <p>Fecha de inicio: ${promocion.fecha_inicio}</p>
            <p>Fecha de fin: ${promocion.fecha_fin}</p>
            <button onclick="verDetalles(${promocion.id})">Ver Detalles</button>
        `;

        promocionesGrid.appendChild(promocionCard);
    });
}

function verDetalles(id) {
    // Redirige a la página de detalles pasando el ID de la promoción como parámetro
    window.location.href = `detalleProductoPromocion.html?id=${id}`;
}
