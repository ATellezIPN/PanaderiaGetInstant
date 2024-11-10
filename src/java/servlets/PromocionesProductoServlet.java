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
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/PromocionesProductoServlet")
public class PromocionesProductoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int productoId = Integer.parseInt(request.getParameter("id"));
        JSONArray promocionesArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=panaderia", "User_AntonioO", "tellez08");
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM Promociones WHERE producto_id = ?")) {

            statement.setInt(1, productoId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                JSONObject promocionJson = new JSONObject();
                promocionJson.put("nombre", resultSet.getString("nombre"));
                promocionJson.put("descripcion", resultSet.getString("descripcion"));
                promocionJson.put("fecha_inicio", resultSet.getDate("fecha_inicio").toString());
                promocionJson.put("fecha_fin", resultSet.getDate("fecha_fin").toString());
                promocionJson.put("foto", resultSet.getString("foto"));
                promocionJson.put("precio", resultSet.getDouble("precio")); // Agregado
                promocionJson.put("stock", resultSet.getInt("stock")); // Agregado
                promocionJson.put("calificacion", obtenerCalificacionPromedio(productoId));
                promocionJson.put("cuponDisponible", verificarCuponDisponible(request.getSession().getAttribute("usuarioId")));

                promocionesArray.put(promocionJson);
            }

        } catch (SQLException e) {
            throw new ServletException("Error al obtener promociones del producto", e);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(promocionesArray.toString());
    }

    private int obtenerCalificacionPromedio(int productoId) {
        // Lógica para calcular el promedio de calificaciones
        return 4; // Ejemplo de calificación promedio
    }

    private boolean verificarCuponDisponible(Object usuarioId) {
        // Lógica para verificar si el usuario tiene un cupón disponible
        return true; // Ejemplo de cupón disponible
    }
}
