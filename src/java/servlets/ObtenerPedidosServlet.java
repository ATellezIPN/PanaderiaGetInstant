package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/obtenerPedidos")
public class ObtenerPedidosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=panaderia";
    private static final String DB_USER = "User_AntonioO"; 
    private static final String DB_PASSWORD = "tellez08"; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pedidosSQL = "SELECT v.id, v.fecha_venta, v.total, dv.producto_id, p.nombre, dv.cantidad, dv.precio_unitario "
                          + "FROM Ventas v "
                          + "JOIN DetallesVentas dv ON v.id = dv.venta_id "
                          + "JOIN Productos p ON dv.producto_id = p.id";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(pedidosSQL);
             ResultSet rs = ps.executeQuery()) {

            Map<Integer, Map<String, Object>> pedidos = new HashMap<>();

            while (rs.next()) {
                int ventaId = rs.getInt("id");
                pedidos.putIfAbsent(ventaId, new HashMap<>());
                Map<String, Object> pedido = pedidos.get(ventaId);

                // Información del pedido principal
                if (!pedido.containsKey("id")) {
                    pedido.put("id", ventaId);
                    pedido.put("fecha_venta", rs.getDate("fecha_venta"));
                    pedido.put("total", rs.getBigDecimal("total"));
                    pedido.put("detalles", new ArrayList<Map<String, Object>>());
                }

                // Detalle del pedido
                List<Map<String, Object>> detalles = (List<Map<String, Object>>) pedido.get("detalles");
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("producto_id", rs.getInt("producto_id"));
                detalle.put("nombre_producto", rs.getString("nombre"));
                detalle.put("cantidad", rs.getInt("cantidad"));
                detalle.put("precio_unitario", rs.getBigDecimal("precio_unitario"));

                detalles.add(detalle);
            }

            out.write(new com.google.gson.Gson().toJson(pedidos.values()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Error al obtener los pedidos.\"}");
            e.printStackTrace();
        }
    }
}

