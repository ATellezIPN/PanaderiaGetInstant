package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.UsuarioDAO;
import utils.Usuario;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String email = request.getParameter("email");
    String password = request.getParameter("password");

    // Validar usuario en base de datos
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = usuarioDAO.validarUsuario(email, password);

    if (usuario != null) {
        // Si las credenciales son correctas, almacenar el usuario en la sesión
        HttpSession session = request.getSession();
        session.setAttribute("usuarioId", usuario.getId()); // Almacena el ID del usuario
        session.setAttribute("usuario", usuario); // Almacena el objeto Usuario

        // Crear una cookie para la sesión
        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
        sessionCookie.setMaxAge(60 * 60 * 24 * 7); // La cookie durará 7 días
        response.addCookie(sessionCookie);

        // Redirigir al header.html
        response.sendRedirect("header.html"); // Cambia a header.jsp si es necesario
    } else {
        // Redirigir de vuelta a login.html si falla la autenticación
        response.sendRedirect("Login.html?error=true");
    }
}
}
