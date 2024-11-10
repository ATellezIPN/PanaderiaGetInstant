package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/paginaProtegida")
public class PaginaProtegidaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener la sesión existente (sin crear una nueva)
        HttpSession session = request.getSession(false);
        
        // Verificar si la sesión existe
        if (session == null) {
            // Si no hay sesión, redirigir al usuario a la página de inicio de sesión
            response.sendRedirect("login.html");
            return;
        }

        // Obtener el ID del usuario de la sesión
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String username = (String) session.getAttribute("user");

        // Verificar si el ID del usuario o el nombre de usuario son nulos
        if (usuarioId == null || username == null) {
            // Si no hay ID de usuario, redirigir al login
            response.sendRedirect("login.html");
        } else {
            // Si el usuario tiene una sesión válida, continuar con la página protegida
            response.getWriter().println("Bienvenido, " + username + ". Tu ID de usuario es: " + usuarioId);
        }
    }
}