package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarritoDAO {

    public boolean agregarProductoAlCarrito(int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId, int cantidad) {
        if (!validarEntrada(usuarioId, productoId, productoTempId, promocionId, cantidad)) {
            return false;
        }

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);

            boolean stockSuficiente;
            if (productoId != null) {
                stockSuficiente = ProductoDAO.verificarStockProducto(productoId, cantidad);
            } else if (productoTempId != null) {
                stockSuficiente = ProductoDAO.verificarStockProductoTemp(productoTempId, cantidad);
            } else {
                stockSuficiente = true;
            }

            if (!stockSuficiente) {
                System.err.println("Error: Stock insuficiente para el producto.");
                conn.rollback();
                return false;
            }

            if (productoEnCarrito(conn, usuarioId, productoId, productoTempId, promocionId)) {
                int cantidadActual = obtenerCantidadExistente(conn, usuarioId, productoId, productoTempId, promocionId);
                System.out.println("Actualizando cantidad existente: " + cantidadActual);
                actualizarCantidad(conn, usuarioId, productoId, productoTempId, promocionId, cantidadActual + cantidad);
            } else {
                System.out.println("Insertando producto en carrito.");
                insertarProducto(conn, usuarioId, productoId, productoTempId, promocionId, cantidad);
            }

            boolean actualizado = false;
            if (productoId != null) {
                actualizado = ProductoDAO.actualizarStockProducto(productoId, cantidad);
            } else if (productoTempId != null) {
                actualizado = ProductoDAO.actualizarStockProductoTemp(productoTempId, cantidad);
            }

            if (!actualizado) {
                System.err.println("Error: No se pudo actualizar el stock.");
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al procesar el carrito: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProductoDelCarrito(int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId) {
        String query = "DELETE FROM Carrito WHERE usuario_id = ? AND producto_id = ? AND productoTemp_id = ? AND promocion_id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);
            stmt.setObject(2, productoId);
            stmt.setObject(3, productoTempId);
            stmt.setObject(4, promocionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto del carrito: " + e.getMessage());
            return false;
        }
    }

    public boolean vaciarCarrito(int usuarioId) {
        String query = "DELETE FROM Carrito WHERE usuario_id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al vaciar el carrito: " + e.getMessage());
            return false;
        }
    }

    public ResultSet obtenerCarritoPorUsuario(int usuarioId) {
        String query = "SELECT * FROM Carrito WHERE usuario_id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);

            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error al obtener el carrito del usuario: " + e.getMessage());
            return null;
        }
    }

    private boolean validarEntrada(int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId, int cantidad) {
        return usuarioId > 0 && cantidad > 0 && (productoId != null || productoTempId != null || promocionId != null);
    }

    private boolean productoEnCarrito(Connection conn, int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Carrito WHERE usuario_id = ? AND producto_id = ? AND productoTemp_id = ? AND promocion_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);
            stmt.setObject(2, productoId);
            stmt.setObject(3, productoTempId);
            stmt.setObject(4, promocionId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private int obtenerCantidadExistente(Connection conn, int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId) throws SQLException {
        String query = "SELECT cantidad FROM Carrito WHERE usuario_id = ? AND producto_id = ? AND productoTemp_id = ? AND promocion_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);
            stmt.setObject(2, productoId);
            stmt.setObject(3, productoTempId);
            stmt.setObject(4, promocionId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("cantidad") : 0;
            }
        }
    }

    private boolean insertarProducto(Connection conn, int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId, int cantidad) throws SQLException {
        String query = "INSERT INTO Carrito (usuario_id, producto_id, productoTemp_id, promocion_id, cantidad) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);
            stmt.setObject(2, productoId);
            stmt.setObject(3, productoTempId);
            stmt.setObject(4, promocionId);
            stmt.setInt(5, cantidad);

            return stmt.executeUpdate() > 0;
        }
    }

    private boolean actualizarCantidad(Connection conn, int usuarioId, Integer productoId, Integer productoTempId, Integer promocionId, int nuevaCantidad) throws SQLException {
        String query = "UPDATE Carrito SET cantidad = ? WHERE usuario_id = ? AND producto_id = ? AND productoTemp_id = ? AND promocion_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, usuarioId);
            stmt.setObject(3, productoId);
            stmt.setObject(4, productoTempId);
            stmt.setObject(5, promocionId);

            return stmt.executeUpdate() > 0;
        }
    }
}
