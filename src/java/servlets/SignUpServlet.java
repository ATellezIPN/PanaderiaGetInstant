package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SignUpServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=panaderia";
    private static final String DB_USER = "User_AntonioO"; 
    private static final String DB_PASSWORD = "tellez08"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombreCompleto = request.getParameter("nombre");
        String correoElectronico = request.getParameter("correo");
        String nombreUsuario = request.getParameter("usuario");
        String contrasena = request.getParameter("password");

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;

        try {
            // Conexión a la base de datos
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Verificar si el nombre de usuario o correo ya existe
            String checkQuery = "SELECT COUNT(*) FROM Usuarios WHERE nombre_usuario = ? OR correo_electronico = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, nombreUsuario);
            checkStmt.setString(2, correoElectronico);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Si el usuario o correo ya existe, redirigir a la página de registro con error
                response.sendRedirect("SignUp.html?error=usuarioExiste");
            } else {
                // Insertar el nuevo usuario
                String insertQuery = "INSERT INTO Usuarios (nombre_completo, correo_electronico, nombre_usuario, contraseña, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, nombreCompleto);
                insertStmt.setString(2, correoElectronico);
                insertStmt.setString(3, nombreUsuario);
                insertStmt.setString(4, contrasena); // Asegúrate de usar un hash para almacenar contraseñas
                insertStmt.setInt(5, 1); // Tipo de usuario por defecto

                insertStmt.executeUpdate();

                // Crear sesión para el nuevo usuario
                HttpSession session = request.getSession();
                session.setAttribute("usuario", nombreUsuario);

                // Redirigir al header o página principal
                response.sendRedirect("header.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("SignUp.html?error=errorServidor");
        } finally {
            try {
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
