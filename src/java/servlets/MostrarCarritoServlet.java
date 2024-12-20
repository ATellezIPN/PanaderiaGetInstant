package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.ConexionDB;

@WebServlet("/mostrarCarrito")
public class MostrarCarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener la sesión del usuario
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            // Usuario no autenticado
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Debes iniciar sesión para ver el carrito.\"}");
            return;
        }

        // Configurar el tipo de respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Conexión a la base de datos y lógica para obtener los datos del carrito
        try (Connection connection = ConexionDB.getConnection();
             PrintWriter out = response.getWriter()) {

            if (connection == null) {
                throw new SQLException("La conexión a la base de datos es nula.");
            }

            // Consulta SQL para obtener los productos del carrito
            String sql = "SELECT "+
    "c.id AS carrito_id, " +
    "COALESCE(p.nombre, pt.nombre, pr.nombre) AS nombre, " +
    "COALESCE(p.precio, pt.precio, pr.precio) AS precio, " +
    "c.cantidad, " +
    "(COALESCE(p.precio, pt.precio, pr.precio) * c.cantidad) AS subtotal, " +
    "CASE " +
        "WHEN p.id IS NOT NULL THEN 'producto' " +
        "WHEN pt.id IS NOT NULL THEN 'productoTemp' " +
        "WHEN pr.id IS NOT NULL THEN 'promocion' " +
    "END AS tipo " +
"FROM Carrito c " +
"LEFT JOIN Productos p ON c.producto_id = p.id " +
"LEFT JOIN ProdTemp pt ON c.productoTemp_id = pt.id " +
"LEFT JOIN Promociones pr ON c.promocion_id = pr.id " +
"WHERE c.usuario_id = ? ";


            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();

                JSONArray productos = new JSONArray();

                // Procesar los resultados de la consulta
                while (rs.next()) {
    JSONObject producto = new JSONObject();
    producto.put("id", rs.getInt("carrito_id")); // Incluye el id
    producto.put("nombre", rs.getString("nombre"));
    producto.put("precio", rs.getDouble("precio"));
    producto.put("cantidad", rs.getInt("cantidad"));
    producto.put("subtotal", rs.getDouble("subtotal"));
    producto.put("tipo", rs.getString("tipo")); 
    productos.put(producto);
}

                // Verificar si el carrito está vacío
                if (productos.length() == 0) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    response.getWriter().write("{\"message\":\"No hay productos en el carrito.\"}");
                    return;
                }

                JSONObject respuesta = new JSONObject();
                respuesta.put("productos", productos);

                // Escribir la respuesta JSON al cliente
                out.write(respuesta.toString());
            }
        } catch (SQLException e) {
            // Capturar y registrar errores de SQL
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Error al acceder a la base de datos: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Capturar cualquier otro error
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"Ocurrió un error inesperado: " + e.getMessage() + "\"}");
        }
    }
}
