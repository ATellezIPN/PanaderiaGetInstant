package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    // Método para verificar si un usuario ya existe en la base de datos
    public boolean existeUsuario(String usuario) {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean existe = false;

        try {
            // Obtener la conexión
            conexion = ConexionDB.conectar();

            // Consulta para verificar si el usuario ya existe
            String sql = "SELECT COUNT(*) FROM Usuarios WHERE nombre_usuario = ?";
            statement = conexion.prepareStatement(sql);
            statement.setString(1, usuario);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                existe = resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conexion != null) ConexionDB.cerrarConexion(conexion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return existe;
    }

    // Método para registrar un nuevo usuario en la base de datos
    public void registrarUsuario(Usuario usuario) {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            // Obtener la conexión
            conexion = ConexionDB.conectar();

            // Consulta para insertar un nuevo usuario
            String sql = "INSERT INTO Usuarios (nombre_completo, correo_electronico, nombre_usuario, contraseña, tipo_usuario, foto_perfil) VALUES (?, ?, ?, ?, ?, ?)";
            statement = conexion.prepareStatement(sql);
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getCorreo());
            statement.setString(3, usuario.getUsuario());
            statement.setString(4, usuario.getPassword()); // En producción, utiliza hashing para mayor seguridad
            statement.setInt(5, 1); // Por defecto, tipo_usuario es 1
            statement.setString(6, usuario.getFotoPerfil()); // Asigna la foto de perfil por defecto

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (conexion != null) ConexionDB.cerrarConexion(conexion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para validar las credenciales del usuario
    public Usuario validarUsuario(String emailOrUsername, String password) {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Usuario usuario = null;

        try {
            // Obtener la conexión
            conexion = ConexionDB.conectar();

            // Consulta para verificar las credenciales
            String sql = "SELECT * FROM Usuarios WHERE (correo_electronico = ? OR nombre_usuario = ?) AND contraseña = ?";
            statement = conexion.prepareStatement(sql);
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            statement.setString(3, password);

            resultSet = statement.executeQuery();

            // Si las credenciales son válidas, crear un objeto Usuario con los datos obtenidos
            if (resultSet.next()) {
                usuario = new Usuario();
                usuario.setId(resultSet.getInt("id")); // Asegúrate de que esta columna exista
                usuario.setNombre(resultSet.getString("nombre_completo"));
                usuario.setCorreo(resultSet.getString("correo_electronico"));
                usuario.setUsuario(resultSet.getString("nombre_usuario"));
                usuario.setPassword(resultSet.getString("contraseña"));
                usuario.setFotoPerfil(resultSet.getString("foto_perfil"));
                usuario.setTipoUsuario(resultSet.getInt("tipoUsuario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conexion != null) ConexionDB.cerrarConexion(conexion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return usuario; // Retorna el objeto Usuario si es válido, o null si no lo es
    }
}
