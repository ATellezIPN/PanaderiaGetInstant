document.addEventListener("DOMContentLoaded", function() {
    // Llama a la función para cargar los productos temporales
    cargarProductosTemp();
});

function cargarProductosTemp() {
    fetch('http://localhost:8080/Panaderia/obtenerProductosTemp')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la red');
            }
            return response.json();
        })
        .then(data => {
            mostrarProductosTemp(data);
        })
        .catch(error => {
            console.error('Hubo un problema con la solicitud Fetch:', error);
            document.getElementById('productos-temp-grid').innerHTML = "<p>Error al cargar los productos temporales.</p>";
        });
}

function mostrarProductosTemp(productosTemp) {
    const productosTempGrid = document.getElementById('productos-temp-grid');
    productosTempGrid.innerHTML = ''; // Limpia el contenedor

    productosTemp.forEach(productoTemp => {
        const productoTempCard = document.createElement('div');
        productoTempCard.classList.add('producto-card');

        productoTempCard.innerHTML = `
            <img src="${productoTemp.foto}" alt="${productoTemp.nombre}" class="producto-imagen">
            <h3>${productoTemp.nombre}</h3>
            <p>${productoTemp.descripcion}</p>
            <p>Precio: $${productoTemp.precio}</p>
            <button onclick="verDetalles(${productoTemp.id})">Ver Detalles</button>
        `;

        productosTempGrid.appendChild(productoTempCard);
    });
}

function verDetalles(id) {
    // Redirige a la página de detalles pasando el ID del producto como parámetro
    window.location.href = `detalleProductoTemp.html?id=${id}`;
}
