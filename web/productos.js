document.addEventListener("DOMContentLoaded", function() {
    // Llama a la función para cargar los productos
    cargarProductos();
});

function cargarProductos() {
    fetch('http://localhost:8080/Panaderia/obtenerProductos')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la red');
            }
            return response.json();
        })
        .then(data => {
            mostrarProductos(data);
        })
        .catch(error => {
            console.error('Hubo un problema con la solicitud Fetch:', error);
            document.getElementById('productos-grid').innerHTML = "<p>Error al cargar los productos.</p>";
        });
}

function mostrarProductos(productos) {
    const productosGrid = document.getElementById('productos-grid');
    productosGrid.innerHTML = ''; // Limpia el contenedor

    productos.forEach(producto => {
        const productoCard = document.createElement('div');
        productoCard.classList.add('producto-card');

        productoCard.innerHTML = `
            <img src="${producto.foto}" alt="${producto.nombre}" class="producto-imagen">
            <h3>${producto.nombre}</h3>
            <p>${producto.descripcion}</p>
            <p>Precio: $${producto.precio}</p>
            <button onclick="verDetalles(${producto.id})">Ver Detalles</button>
        `;

        productosGrid.appendChild(productoCard);
    });
}

function verDetalles(productoId) {
    // Redirige a la página de detalles con el ID del producto en la URL
    window.location.href = `detalleProducto.html?id=${productoId}`;
}
