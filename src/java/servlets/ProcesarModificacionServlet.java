package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.ConexionDB;
import utils.Usuario;

@WebServlet("/procesarModificacion")
public class ProcesarModificacionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener los datos del formulario
        String nombreCompleto = request.getParameter("nombreCompleto");
        String nombreUsuario = request.getParameter("nombreUsuario");
        String contrasena = request.getParameter("contrasena");
        String fotoPerfil = request.getParameter("fotoPerfil"); // Aquí podrías procesar la imagen si la estás guardando en base64 o en un archivo
        
        // Obtener el usuario en sesión
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("login.html"); // Redirige a la página de login si no hay usuario en sesión
            return;
        }
        
        // Actualizar los datos en la base de datos
        try (Connection conn = ConexionDB.conectar()) { // Usa tu método de conexión a la BD
            String sql = "UPDATE Usuarios SET nombre_completo = ?, nombre_usuario = ?, contraseña = ?, foto_perfil = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombreCompleto);
                stmt.setString(2, nombreUsuario);
                stmt.setString(3, contrasena);
                stmt.setString(4, fotoPerfil); // Convierte la imagen si es necesario
                stmt.setInt(5, usuario.getId()); // Suponiendo que el Usuario tiene un método getId()

                int filasActualizadas = stmt.executeUpdate();
                if (filasActualizadas > 0) {
                    // Actualización exitosa, también actualiza el objeto usuario en sesión
                    usuario.setNombre(nombreCompleto);
                    usuario.setUsuario(nombreUsuario);
                    usuario.setPassword(contrasena);
                    usuario.setFotoPerfil(fotoPerfil);
                    session.setAttribute("usuario", usuario);
                    
                    response.sendRedirect("miCuenta.html"); // Redirige de vuelta a la página de cuenta
                } else {
                    response.getWriter().println("Error al actualizar los datos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Ocurrió un error al intentar actualizar los datos.");
        }
    }
}
