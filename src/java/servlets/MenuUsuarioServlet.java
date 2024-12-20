package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

@WebServlet("/MenuUsuarioServlet")
public class MenuUsuarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configura el tipo de respuesta
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        Integer tipoUsuario = (Integer) session.getAttribute("tipoUsuario");

        // Inicia el HTML
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"es\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Menú de Usuario</title>");
        out.println("    <link rel=\"stylesheet\" href=\"styles.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class=\"menu-desplegable\" id=\"menu-usuario\">");
        out.println("        <a href=\"miCuenta.html\">Mi cuenta</a>");
        out.println("        <a href=\"#\">Mis pedidos</a>");
        out.println("        <a href=\"soporteUsuario.html\">Soporte al cliente</a>");
        out.println("        <a href=\"logout\">Cerrar sesion</a>");

        // Verifica el permiso y añade las opciones de administrador si aplica
        if (tipoUsuario != null && tipoUsuario == 5) {
            out.println("        <a href=\"agregarProducto.html\" class=\"admin-option\">Agregar producto</a>");
            out.println("        <a href=\"modificarProducto.html\" class=\"admin-option\">Modificar producto</a>");
            out.println("        <a href=\"mensajes.html\" class=\"admin-option\">Mensaje de soporte</a>");
            out.println("        <a href=\"pedidos.html\" class=\"admin-option\">Pedidos de clientes</a>");
        }

        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }
}

