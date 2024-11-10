<%@ page import="utils.Usuario" %>
<link rel="stylesheet" href="styles.css">
<div class="mensaje-bienvenida">
    <%
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
    %>
        <p>Bienvenido, <%= usuario.getNombre() %>!!!</p>
    <%
        }
    %>
</div>
