package servlets;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.ConexionDB;

@WebServlet("/modificarCarrito")
public class ModificarCantidadServlet extends HttpServlet {
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Access-Control-Allow-Origin", "*");

    JSONObject jsonResponse = new JSONObject();

    try (Connection conn = ConexionDB.getConnection()) {
        int id = Integer.parseInt(request.getParameter("id"));
        int cambio = Integer.parseInt(request.getParameter("cambio"));

        if (id <= 0 || cambio == 0) {
            jsonResponse.put("exito", false);
            jsonResponse.put("mensaje", "Parámetros inválidos.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        String queryModificar = "UPDATE Carrito SET cantidad = cantidad + ? WHERE id = ?";
        try (PreparedStatement stmtModificar = conn.prepareStatement(queryModificar)) {
            stmtModificar.setInt(1, cambio);
            stmtModificar.setInt(2, id);

            int rows = stmtModificar.executeUpdate();
            if (rows > 0) {
                jsonResponse.put("exito", true);
                jsonResponse.put("mensaje", "Cantidad modificada.");
            } else {
                jsonResponse.put("exito", false);
                jsonResponse.put("mensaje", "Producto no encontrado en el carrito.");
            }
        }
    } catch (Exception e) {
        jsonResponse.put("exito", false);
        jsonResponse.put("mensaje", "Error interno del servidor: " + e.getMessage());
        e.printStackTrace();
    }
    response.getWriter().write(jsonResponse.toString());
}

    private String getColumnaPorTipo(String tipo) {
        switch (tipo) {
            case "producto": return "producto_id";
            case "productoTemp": return "productoTemp_id";
            case "promocion": return "promocion_id";
            default: return null;
        }
    }
}