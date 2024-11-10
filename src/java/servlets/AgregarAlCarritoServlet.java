package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/agregarAlCarrito")
public class AgregarAlCarritoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));  // Obtienes la cantidad

        // Obtener el ID del usuario desde la sesión
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión para agregar productos al carrito.");
            return;
        }

        // Obtener el ID del producto
        String productoIdParam = request.getParameter("productoId");
        Integer productoId = (productoIdParam != null) ? Integer.parseInt(productoIdParam) : null;

        // Conectar a la base de datos y agregar el producto al carrito
        try (Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433/panaderia", "User_AntonioO", "tellez08")) {
            String insertar = "INSERT INTO Carrito (usuario_id, producto_id, cantidad) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertar);
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al agregar el producto al carrito.");
            return;
        }

        // Responder con éxito
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Producto agregado al carrito.\"}");
    }
}
