package utils;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private List<ProductoCarrito> productos;

    public Carrito() {
        this.productos = new ArrayList<>();
    }

    // Método para agregar un producto al carrito
    public void agregarProducto(Producto producto, int cantidad) {
        // Verificar si el producto ya está en el carrito
        for (ProductoCarrito productoCarrito : productos) {
            if (productoCarrito.getProducto().getId() == producto.getId()) {
                // Actualizar la cantidad si ya existe
                productoCarrito.setCantidad(productoCarrito.getCantidad() + cantidad);
                return;
            }
        }
        // Si no existe, agregarlo como un nuevo producto en el carrito
        productos.add(new ProductoCarrito(producto, cantidad));
    }

    // Método para eliminar un producto del carrito
    public void eliminarProducto(int productoId) {
        productos.removeIf(productoCarrito -> productoCarrito.getProducto().getId() == productoId);
    }

    // Método para obtener la lista de productos en el carrito
    public List<ProductoCarrito> getProductos() {
        return productos;
    }

    // Método para calcular el total del carrito
    public double calcularTotal() {
        return productos.stream()
                .mapToDouble(ProductoCarrito::getTotal)
                .sum();
    }

    // Método para vaciar el carrito
    public void vaciarCarrito() {
        productos.clear();
    }
}
