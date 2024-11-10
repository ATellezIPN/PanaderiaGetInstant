package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import utils.Product;

@WebServlet("/BusquedaServlet")
public class BusquedaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sabor = request.getParameter("sabor");
        String temporada = request.getParameter("temporada");
        String precio = request.getParameter("precio");

        // Establece el tipo de respuesta y la codificación de caracteres
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Product> productos = buscarProductos(query, sabor, temporada, precio);
        
        // Convertir la lista de productos a JSON y enviar la respuesta
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(productos);
        out.print(json);
        out.flush();
    }

    private List<Product> buscarProductos(String query, String sabor, String temporada, String precio) {
        List<Product> productos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Conexión a la base de datos
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=panaderia;user=User_AntonioO;password=tellez08");

            // Construir la consulta SQL
            String sql = "SELECT id, nombre, precio, descripcion, categoria, 'productos' AS origen FROM Productos WHERE nombre LIKE ?";
            if (sabor != null && !sabor.isEmpty()) {
                sql += " AND sabor = ?";
            }
            if (temporada != null && !temporada.isEmpty()) {
                sql += " AND temporada = ?";
            }
            if (precio != null && !precio.isEmpty()) {
                if (precio.equals("1")) {
                    sql += " AND precio < 50";
                } else if (precio.equals("2")) {
                    sql += " AND precio BETWEEN 50 AND 100";
                } else if (precio.equals("3")) {
                    sql += " AND precio > 100";
                }
            }

            // Agregar la unión con ProdTemp
            sql += " UNION ALL ";
            sql += "SELECT id, nombre, precio, descripcion, categoria, 'temp' AS origen FROM ProdTemp WHERE nombre LIKE ?";
            if (sabor != null && !sabor.isEmpty()) {
                sql += " AND sabor = ?";
            }
            if (temporada != null && !temporada.isEmpty()) {
                sql += " AND temporada = ?";
            }
            if (precio != null && !precio.isEmpty()) {
                if (precio.equals("1")) {
                    sql += " AND precio < 50";
                } else if (precio.equals("2")) {
                    sql += " AND precio BETWEEN 50 AND 100";
                } else if (precio.equals("3")) {
                    sql += " AND precio > 100";
                }
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + query + "%");
            int parameterIndex = 2;

            // Establecer parámetros para Productos
            if (sabor != null && !sabor.isEmpty()) {
                ps.setString(parameterIndex++, sabor);
            }
            if (temporada != null && !temporada.isEmpty()) {
                ps.setString(parameterIndex++, temporada);
            }

            // Parámetro de búsqueda para ProdTemp
            ps.setString(parameterIndex++, "%" + query + "%");

            // Establecer parámetros para ProdTemp
            if (sabor != null && !sabor.isEmpty()) {
                ps.setString(parameterIndex++, sabor);
            }
            if (temporada != null && !temporada.isEmpty()) {
                ps.setString(parameterIndex++, temporada);
            }

            // Ejecutar la consulta
            rs = ps.executeQuery();

            // Procesar los resultados y agregar a la lista
            while (rs.next()) {
                Product producto = new Product();
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setOrigen(rs.getString("origen"));
                productos.add(producto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return productos;
    }
}
