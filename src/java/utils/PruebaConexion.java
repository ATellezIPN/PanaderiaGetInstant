package utils;

import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        Connection conexion = ConexionDB.conectar();
        
        // Verificar si la conexión es exitosa
        if (conexion != null) {
            System.out.println("La conexión se realizó correctamente.");
            ConexionDB.cerrarConexion(conexion);
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
}
