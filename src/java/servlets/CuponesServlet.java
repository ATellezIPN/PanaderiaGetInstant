package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/obtenerCupones")
public class CuponesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Conexión a la base de datos (ajusta la URL, usuario y contraseña según tu configuración)
            Connection conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433 ;databaseName=PanaderiaGetInstant", "User_AntonioO", "tellez08");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Cupones");

            // Procesa los resultados y devuelve un JSON con los cupones
            out.println("[");
            while (rs.next()) {
                out.println("{\"id\": " + rs.getInt("id") + ", \"descripcion\": \"" + rs.getString("descripcion") + "\", \"tipo_cupon\": " + rs.getInt("tipo_cupon") + ", \"valor\": " + rs.getDouble("valor") + "},");
            }
            out.println("]");

            // Cierra la conexión
            conn.close();
        } catch (Exception e) {
            out.println("Error al obtener cupones: " + e.getMessage());
        }
    }
}