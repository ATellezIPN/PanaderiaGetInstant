package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    // Obtiene un producto regular por su ID
    public static Producto obtenerProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getString("foto"),
                        rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el producto: " + e.getMessage());
        }
        return null;
    }

    // Obtiene un producto temporal por su ID
    public static ProductoTemp obtenerProductoTemporadaPorId(int id) {
        String sql = "SELECT id, nombre, precio, stock, descripcion, categoria, foto FROM ProdTemp WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductoTemp(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion"),
                        rs.getString("categoria"),
                        rs.getString("foto"),
                        rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el producto de temporada: " + e.getMessage());
        }
        return null;
    }

    // Actualiza el stock de un producto regular
    public static boolean actualizarStockProducto(int productoId, int cantidad) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock de producto: " + e.getMessage());
        }
        return false;
    }

    // Actualiza el stock de un producto temporal
    public static boolean actualizarStockProductoTemp(int productoTempId, int cantidad) {
        String sql = "UPDATE ProdTemp SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, productoTempId);
            stmt.setInt(3, cantidad);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock de producto temporal: " + e.getMessage());
        }
        return false;
    }

    // Verifica si un producto regular tiene suficiente stock
    public static boolean verificarStockProducto(int productoId, int cantidad) {
        String sql = "SELECT stock FROM productos WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    return stockActual >= cantidad;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar el stock del producto: " + e.getMessage());
        }
        return false;
    }

    // Verifica si un producto temporal tiene suficiente stock
    public static boolean verificarStockProductoTemp(int productoTempId, int cantidad) {
        String sql = "SELECT stock FROM ProdTemp WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productoTempId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    return stockActual >= cantidad;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar el stock del producto temporal: " + e.getMessage());
        }
        return false;
    }
}
