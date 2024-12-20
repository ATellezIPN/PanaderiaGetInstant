package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/terminarPedido")
public class TerminarPedidoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=panaderia";
    private static final String DB_USER = "User_AntonioO"; 
    private static final String DB_PASSWORD = "tellez08"; 

    // Consultas SQL
    private static final String UPDATE_STOCK_SQL = 
        "UPDATE Productos SET stock = stock - ? WHERE id = ?";
    private static final String INSERT_VENTA_SQL = 
        "INSERT INTO Ventas (usuario_id, fecha_venta, total) OUTPUT INSERTED.id VALUES (?, ?, ?)";
    private static final String INSERT_DETALLE_SQL = 
        "INSERT INTO DetallesVentas (venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ADMIN_EMAILS_SQL = 
        "SELECT correo_electronico FROM Usuarios WHERE tipo_usuario = 5";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Obtener los datos del pedido del request
        String usuarioId = request.getParameter("usuarioId");
        String[] productoIds = request.getParameterValues("productoIds[]");
        String[] cantidades = request.getParameterValues("cantidades[]");
        String[] precios = request.getParameterValues("precios[]");
        String total = request.getParameter("total");

        if (productoIds == null || cantidades == null || precios == null 
            || productoIds.length != cantidades.length || cantidades.length != precios.length) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Datos inválidos en la solicitud.\"}");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Iniciar una transacción

            try {
                // Actualizar el stock de los productos
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_STOCK_SQL)) {
                    for (int i = 0; i < productoIds.length; i++) {
                        ps.setInt(1, Integer.parseInt(cantidades[i]));
                        ps.setInt(2, Integer.parseInt(productoIds[i]));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // Registrar la venta
                int ventaId;
                try (PreparedStatement ps = conn.prepareStatement(INSERT_VENTA_SQL)) {
                    ps.setInt(1, Integer.parseInt(usuarioId));
                    ps.setDate(2, new Date(System.currentTimeMillis()));
                    ps.setBigDecimal(3, new java.math.BigDecimal(total));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ventaId = rs.getInt(1);
                        } else {
                            throw new SQLException("No se pudo registrar la venta.");
                        }
                    }
                }

                // Registrar los detalles de la venta
                try (PreparedStatement ps = conn.prepareStatement(INSERT_DETALLE_SQL)) {
                    for (int i = 0; i < productoIds.length; i++) {
                        ps.setInt(1, ventaId);
                        ps.setInt(2, Integer.parseInt(productoIds[i]));
                        ps.setInt(3, Integer.parseInt(cantidades[i]));
                        ps.setBigDecimal(4, new java.math.BigDecimal(precios[i]));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // Notificar a los administradores
                try (PreparedStatement ps = conn.prepareStatement(SELECT_ADMIN_EMAILS_SQL)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String adminEmail = rs.getString("correo_electronico");
                            // Aquí puedes implementar el envío de correos electrónicos si lo deseas
                            System.out.println("Notificación enviada a: " + adminEmail);
                        }
                    }
                }

                conn.commit(); // Confirmar la transacción
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Pedido completado correctamente.\"}");
            } catch (SQLException e) {
                conn.rollback(); // Revertir si ocurre un error
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\": \"Error al completar el pedido.\"}");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"No se pudo conectar a la base de datos.\"}");
            e.printStackTrace();
        }
    }
}