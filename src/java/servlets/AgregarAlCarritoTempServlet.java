package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import utils.CarritoDAO;
import utils.ProductoDAO;
import utils.ProductoTemp;

@WebServlet("/agregarAlCarritoTemp")
public class AgregarAlCarritoTempServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true); // Crea una nueva sesión si no existe

        // Obtener el ID del usuario desde la sesión
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        // Leer el cuerpo de la solicitud como JSON
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonBuffer.toString());
            Integer productoTempId = jsonObject.optInt("productoTempId", -1);
            Integer cantidad = jsonObject.optInt("cantidad", -1);

            // Validar los parámetros
            if (cantidad <= 0 || productoTempId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Datos inválidos para agregar al carrito.\"}");
                return;
            }

            // Si el usuario no está autenticado, usar carrito temporal
            if (usuarioId == null) {
                if (session.getAttribute("carritoTemporal") == null) {
                    session.setAttribute("carritoTemporal", new HashMap<Integer, Integer>());
                }

                Map<Integer, Integer> carritoTemporal =
                        (Map<Integer, Integer>) session.getAttribute("carritoTemporal");
                carritoTemporal.put(productoTempId, carritoTemporal.getOrDefault(productoTempId, 0) + cantidad);

                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Producto temporal agregado al carrito correctamente.\"}");
                return;
            }

            // Recuperar producto temporal desde la base de datos
            ProductoTemp productoTemp = ProductoDAO.obtenerProductoTemporadaPorId(productoTempId);
            if (productoTemp == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Producto temporal no encontrado.\"}");
                return;
            }

            // Verificar stock
            if (cantidad > productoTemp.getStock()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Stock insuficiente para el producto.\"}");
                return;
            }

            // Actualizar stock del producto temporal
            boolean stockActualizado = ProductoDAO.actualizarStockProductoTemp(productoTempId, -cantidad);
            if (!stockActualizado) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"message\": \"No se pudo actualizar el stock del producto.\"}");
                return;
            }

            // Agregar al carrito del usuario autenticado
            CarritoDAO carritoDAO = new CarritoDAO();
            boolean agregado = carritoDAO.agregarProductoAlCarrito(usuarioId, null, productoTempId, null, cantidad);

            if (agregado) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"Producto agregado al carrito correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"message\": \"Error al agregar el producto al carrito.\"}");
            }
        } catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"message\": \"JSON inválido.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"message\": \"Error procesando la solicitud.\"}");
            e.printStackTrace();
        }
    }
}
