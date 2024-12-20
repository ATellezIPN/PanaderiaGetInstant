package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONObject;

public class AgregarProductoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String tabla = request.getParameter("tabla");
        String nombre = request.getParameter("nombre");
        String precio = request.getParameter("precio");
        String stock = request.getParameter("stock");
        String categoria = request.getParameter("categoria");
        String descripcion = request.getParameter("descripcion");
        String foto = request.getParameter("foto");

        // Campos adicionales para promociones
        String fechaInicio = request.getParameter("fecha_inicio");
        String fechaFin = request.getParameter("fecha_fin");
        String productoId = request.getParameter("producto_id");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=TuDB", "user", "password")) {
            String query = "";

            if ("productos".equals(tabla) || "prodTemp".equals(tabla)) {
                query = "INSERT INTO " + tabla + " (nombre, precio, stock, descripcion, categoria, foto) VALUES (?, ?, ?, ?, ?, ?)";
            } else if ("promociones".equals(tabla)) {
                query = "INSERT INTO Promociones (nombre, descripcion, fecha_inicio, fecha_fin, producto_id, precio, stock, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nombre);
            stmt.setDouble(2, Double.parseDouble(precio));
            stmt.setInt(3, Integer.parseInt(stock));

            if ("productos".equals(tabla) || "prodTemp".equals(tabla)) {
                stmt.setString(4, descripcion);
                stmt.setString(5, categoria);
                stmt.setString(6, foto);
            } else if ("promociones".equals(tabla)) {
                stmt.setString(4, fechaInicio);
                stmt.setString(5, fechaFin);
                stmt.setInt(6, Integer.parseInt(productoId));
                stmt.setDouble(7, Double.parseDouble(precio));
                stmt.setInt(8, Integer.parseInt(stock));
                stmt.setString(9, foto);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                jsonResponse.put("success", true);
                jsonResponse.put("mensaje", "Producto agregado exitosamente");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("mensaje", "Error al agregar el producto");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
            jsonResponse.put("mensaje", "Error del servidor: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}
