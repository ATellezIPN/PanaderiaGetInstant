package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/DetalleProdTempServlet")
public class DetalleProdTempServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int productoId = Integer.parseInt(request.getParameter("id"));
        JSONObject productoJson = new JSONObject();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=panaderia", "User_AntonioO", "tellez08");
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM ProdTemp WHERE id = ?")) {

            statement.setInt(1, productoId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                productoJson.put("nombre", resultSet.getString("nombre"));
                productoJson.put("precio", resultSet.getBigDecimal("precio"));
                productoJson.put("stock", resultSet.getInt("stock"));
                productoJson.put("descripcion", resultSet.getString("descripcion"));
                productoJson.put("foto", resultSet.getString("foto"));
                productoJson.put("calificacion", obtenerCalificacionPromedio(productoId));
                productoJson.put("cuponDisponible", verificarCuponDisponible(request.getSession().getAttribute("usuarioId")));
            }

        } catch (SQLException e) {
            throw new ServletException("Error al obtener detalles del producto temporal", e);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(productoJson.toString());
    }

    private int obtenerCalificacionPromedio(int productoId) {
        return 4; // Ejemplo
    }

    private boolean verificarCuponDisponible(Object usuarioId) {
        return true; // Ejemplo
    }
}
