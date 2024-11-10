package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/MostrarMensajesServlet")
public class MostrarMensajesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int tipoUsuario = Integer.parseInt(request.getSession().getAttribute("tipoUsuario").toString());

        // Verificar si el usuario es un administrador (tipo 5)
        if (tipoUsuario != 5) {
            response.sendRedirect("accesoDenegado.html");
            return;
        }

        JSONArray mensajesArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=panaderia", "User_AntonioO", "tellez08");
             PreparedStatement stmt = conn.prepareStatement("SELECT m.mensaje, m.fecha_envio, u.nombre_usuario FROM MensajesSoporte m JOIN Usuarios u ON m.remitente_id = u.id ORDER BY m.fecha_envio DESC")) {

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                JSONObject mensajeJson = new JSONObject();
                mensajeJson.put("mensaje", resultSet.getString("mensaje"));
                mensajeJson.put("fecha_envio", resultSet.getTimestamp("fecha_envio").toString());
                mensajeJson.put("nombre_usuario", resultSet.getString("nombre_usuario"));
                mensajesArray.put(mensajeJson);
            }
        } catch (Exception e) {
            throw new ServletException("Error al obtener mensajes de soporte", e);
        }

        response.setContentType("application/json");
        response.getWriter().write(mensajesArray.toString());
    }
}
