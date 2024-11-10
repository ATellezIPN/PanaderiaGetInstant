package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EnviarMensajeSoporteServlet")
public class EnviarMensajeSoporteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje = request.getParameter("mensaje");
        int remitenteId = Integer.parseInt(request.getSession().getAttribute("usuarioId").toString());

        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=panaderia", "User_AntonioO", "tellez08");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO MensajesSoporte (remitente_id, mensaje) VALUES (?, ?)")) {

            stmt.setInt(1, remitenteId);
            stmt.setString(2, mensaje);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new ServletException("Error al enviar el mensaje de soporte", e);
        }

        response.sendRedirect("soporteUsuario.html?status=mensaje-enviado");
    }
}
