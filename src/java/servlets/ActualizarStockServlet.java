package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/actualizarStock")
public class ActualizarStockServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=panaderia";
    private static final String DB_USER = "User_AntonioO"; 
    private static final String DB_PASSWORD = "tellez08"; 

    // SQL para restar stock
    private static final String UPDATE_STOCK_SQL = 
        "UPDATE Productos SET stock = stock - ? WHERE id = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Obtener los parámetros del request
        String[] productoIds = request.getParameterValues("productoIds[]");
        String[] cantidades = request.getParameterValues("cantidades[]");

        if (productoIds == null || cantidades == null || productoIds.length != cantidades.length) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\": \"Datos inválidos en la solicitud.\"}");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Iniciar una transacción

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_STOCK_SQL)) {
                for (int i = 0; i < productoIds.length; i++) {
                    int productoId = Integer.parseInt(productoIds[i]);
                    int cantidad = Integer.parseInt(cantidades[i]);

                    ps.setInt(1, cantidad);
                    ps.setInt(2, productoId);
                    ps.addBatch(); // Añadir al batch para ejecución múltiple
                }

                ps.executeBatch(); // Ejecutar todas las actualizaciones
                conn.commit(); // Confirmar la transacción

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Stock actualizado correctamente.\"}");
            } catch (SQLException e) {
                conn.rollback(); // Revertir si ocurre un error
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\": \"Error al actualizar el stock.\"}");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"No se pudo conectar a la base de datos.\"}");
            e.printStackTrace();
        }
    }
}
