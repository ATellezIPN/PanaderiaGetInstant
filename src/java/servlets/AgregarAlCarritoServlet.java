package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import utils.CarritoDAO;
import utils.ProductoDAO;
import utils.Producto;
import utils.ProductoTemp;

@WebServlet("/agregarAlCarrito")
public class AgregarAlCarritoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"Debes iniciar sesión para agregar productos al carrito.\"}");
            return;
        }

        try {
            // Leer el cuerpo de la solicitud como JSON
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                jsonBuffer.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonBuffer.toString());

            Integer productoId = jsonObject.optInt("productoId", -1);
            Integer productoTempId = jsonObject.optInt("productoTempId", -1);
            int cantidad = jsonObject.optInt("cantidad", 0);
            int usuarioId = (int) session.getAttribute("usuarioId");

            // Validar que al menos uno de los IDs sea válido
            if ((productoId <= 0 && productoTempId <= 0) || cantidad <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Datos inválidos para agregar al carrito.\"}");
                return;
            }

            // Validar stock para productos normales y temporales
            if (productoId > 0) {
                Producto producto = ProductoDAO.obtenerProductoPorId(productoId);
                if (producto == null || cantidad > producto.getStock()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"message\": \"Producto no disponible o sin stock suficiente.\"}");
                    return;
                }
            } else if (productoTempId > 0) {
                ProductoTemp productoTemp = ProductoDAO.obtenerProductoTemporadaPorId(productoTempId);
                if (productoTemp == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"message\": \"Producto de temporada no encontrado.\"}");
                    return;
                }
                if (cantidad > productoTemp.getStock()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"message\": \"Stock insuficiente para el producto de temporada.\"}");
                    return;
                }
            }

            // Agregar al carrito
            CarritoDAO carritoDAO = new CarritoDAO();
            boolean agregado = carritoDAO.agregarProductoAlCarrito(
                    usuarioId,
                    productoId > 0 ? productoId : null,
                    productoTempId > 0 ? productoTempId : null,
                    null, // Sin manejo de promociones
                    cantidad
            );

            if (agregado) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Producto agregado al carrito correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"message\": \"Error al agregar el producto al carrito.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"message\": \"Error procesando la solicitud.\"}");
        }
    }
}