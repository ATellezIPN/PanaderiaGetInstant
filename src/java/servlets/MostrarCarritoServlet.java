package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.ConexionDB;
import utils.ProductoCarrito;

@WebServlet("/mostrarCarrito")
public class MostrarCarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            response.sendRedirect("login.html");
            return;
        }

        ArrayList<ProductoCarrito> carrito = new ArrayList<>();

        try (Connection connection = ConexionDB.getConnection()) {
            String sql = "SELECT p.id, p.nombre, p.precio, c.cantidad FROM productos p " +
                         "JOIN carrito c ON p.id = c.producto_id WHERE c.usuario_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    ProductoCarrito item = new ProductoCarrito(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                    );
                    carrito.add(item);
                }
            }

            request.setAttribute("carrito", carrito);
            request.getRequestDispatcher("carrito.html").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al acceder a la base de datos.");
        }
    }
}
