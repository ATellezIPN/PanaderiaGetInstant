// Simulación de compras
let comprasRealizadas = 0;

// Función para actualizar la barra de progreso
function actualizarBarraProgreso() {
    const progreso = document.getElementById('progreso');
    const porcentaje = (comprasRealizadas / 100) * 100; // Cambia 100 por el total de compras esperadas
    progreso.style.width = porcentaje + '%';
}

// Simulación de compras al hacer clic en los cupones
document.querySelectorAll('.cupon').forEach((cupon, index) => {
    cupon.addEventListener('click', () => {
        comprasRealizadas += 25; // Incrementa según el valor que desees
        actualizarBarraProgreso();
        alert(`Has adquirido: ${cupon.innerText}`);
    });
});