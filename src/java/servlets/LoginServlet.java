package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.UsuarioDAO;
import utils.Usuario;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configuración del tipo de contenido
        response.setContentType("text/html;charset=UTF-8");

        // Obtener parámetros del formulario
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validación básica de campos vacíos
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Por favor completa todos los campos.");
            return;
        }

        // Validar credenciales contra la base de datos
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.validarUsuario(email, password);

        if (usuario != null) {
            // Credenciales válidas: configurar sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuario", usuario);

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Credenciales inválidas: enviar código de error
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Correo o contraseña incorrectos.");
        }
    }
}
