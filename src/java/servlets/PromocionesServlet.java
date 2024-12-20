package servlets;

import java.io.IOException;
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
import utils.Promocion;

@WebServlet("/obtenerPromociones")
public class PromocionesServlet extends HttpServlet {
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
        List<Promocion> promociones = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM Promociones")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Promocion promocion = new Promocion(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("descuento"),
                        resultSet.getDate("fecha_inicio"),
                        resultSet.getDate("fecha_fin")
                    );
                    promocion.setId(resultSet.getInt("id"));
                    promocion.setNombre(resultSet.getString("nombre"));
                    promocion.setDescripcion(resultSet.getString("descripcion"));
                    promocion.setFechaInicio(resultSet.getDate("fecha_inicio"));
                    promocion.setFechaFin(resultSet.getDate("fecha_fin"));
                    promocion.setProductoId(resultSet.getInt("producto_id"));
                    promocion.setFoto(resultSet.getString("foto"));
                    promociones.add(promocion);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Error al obtener promociones", e);
        }

        JSONArray promocionesJson = new JSONArray();
        for (Promocion promocion : promociones) {
            JSONObject promocionJson = new JSONObject();
            promocionJson.put("id", promocion.getId());
            promocionJson.put("nombre", promocion.getNombre());
            promocionJson.put("descripcion", promocion.getDescripcion());
            promocionJson.put("fecha_inicio", promocion.getFechaInicio());
            promocionJson.put("fecha_fin", promocion.getFechaFin());
            promocionJson.put("producto_id", promocion.getProductoId());
            promocionJson.put("foto", promocion.getFoto());
            promocionesJson.put(promocionJson);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(promocionesJson.toString());
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
}
