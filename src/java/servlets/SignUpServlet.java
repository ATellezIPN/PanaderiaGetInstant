package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.Usuario;
import utils.UsuarioDAO;

public class SignUpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        // Verificar si el nombre de usuario ya existe
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean existeUsuario = usuarioDAO.existeUsuario(usuario);

        if (!existeUsuario) {
            // Si no existe, registrar el nuevo usuario
            Usuario nuevoUsuario = new Usuario(nombre, correo, usuario, password);
            usuarioDAO.registrarUsuario(nuevoUsuario);

            // Crear una sesión para el usuario registrado
            HttpSession session = request.getSession();
            session.setAttribute("usuario", nuevoUsuario);

            // Redirigir a la página principal
            response.sendRedirect("header.html"); // Cambia a header.jsp
        } else {
            // Redirigir a signup.html si el usuario ya existe
            response.sendRedirect("SignUp.html?error=usuarioExiste");
        }
    }
}