package servlets;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.ProductoTemp;

@WebServlet("/obtenerProductosTemp")
public class ProductosTempServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=panaderia", "User_AntonioO", "tellez08");
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("No se pudo conectar a la base de datos", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Producto> productos = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM ProdTemp")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Producto producto = new Producto();
                    producto.setId(resultSet.getInt("id"));
                    producto.setNombre(resultSet.getString("nombre"));
                    producto.setPrecio(resultSet.getBigDecimal("precio"));
                    producto.setDescripcion(resultSet.getString("descripcion"));
                    producto.setCategoria(resultSet.getString("categoria"));
                    producto.setFoto(resultSet.getString("foto"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error al obtener productos", e);
        }

        JSONArray productosJson = new JSONArray();
        for (Producto producto : productos) {
            JSONObject productoJson = new JSONObject();
            productoJson.put("id", producto.getId());
            productoJson.put("nombre", producto.getNombre());
            productoJson.put("precio", producto.getPrecio());
            productoJson.put("descripcion", producto.getDescripcion());
            productoJson.put("categoria", producto.getCategoria());
            productoJson.put("foto", producto.getFoto());
            productosJson.put(productoJson);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(productosJson.toString());
    }

    @Override
    public void destroy() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            // Loggear el error
        }
    }

    private static class Producto {
        private int id;
        private String nombre;
        private BigDecimal precio;
        private String descripcion;
        private String categoria;
        private String foto;

        // Getters y Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getFoto() {
            return foto;
        }

        public void setFoto(String foto) {
            this.foto = foto;
        }
    }
}