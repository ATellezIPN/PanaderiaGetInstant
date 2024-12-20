package utils;

public class Usuario {
    private int id; // Cambiar a int para que coincida con el tipo en la BD
    private String nombre;
    private String correo;
    private String usuario;
    private String password;
    private String fotoPerfil;
    private int tipoUsuario;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int id, String nombre, String correo, String usuario, String password, int tipoUsuario, String fotoPerfil) {
        this.id = id; // Asigna el id correctamente
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
        this.fotoPerfil = fotoPerfil;
    }

    // Constructor con parámetros sin id
    public Usuario(String nombre, String correo, String usuario, String password) {
        this.nombre = nombre;
        this.correo = correo;
        this.usuario = usuario;
        this.password = password;
    }

    // Getter y setter para id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id; // Corrige el setter
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
