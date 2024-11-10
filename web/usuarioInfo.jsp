<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="utils.Usuario" %> 
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        usuario = new Usuario();
        usuario.setFotoPerfil("ruta/por/defecto.jpg");
        usuario.setTipoUsuario("***");
        usuario.setNombre("Nombre");
        usuario.setUsuario("Nombre de usuario");
        usuario.setCorreo("correo@example.com");
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Aclonica&display=swap" rel="stylesheet">
    <style>

        .info-cliente {
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 20px;
            background-color: #c47647;
            padding: 20px;
            border-radius: 10px;
        }

        .foto-perfil {
            flex-shrink: 0;
            flex: 2;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #cfac89;
            padding: 20px;
            border-radius: 20px;
            margin-right: 20px;
        }

        .foto-perfil img {
            width: 200px;
            height: 200px;
            border-radius: 50%;
        }

        .datos-cliente {
            flex: 4;
            background-color: #cfac89;
            padding: 20px;
            border-radius: 10px;
        }

        .datos-cliente p {
            font-size: 1.2em;
            margin: 18px 0;
            color: #3c2f2f;
        }

        .datos-cliente #tipoUsuario {
            color: #3c2f2f;
            font-weight: bold;
        }

        .boton-modificar {
            display: block;
            width: 100%;
            text-align: center;
            padding: 10px;
            background-color: #3c2f2f;
            color: #fff;
            font-size: 1em;
            font-weight: bold;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 20px;
        }

        .boton-modificar:hover {
            background-color: #b5835a;
        }
    </style>
</head>
<body>

<section class="info-cliente">
    <div class="foto-perfil">
        <img id="fotoPerfil" src="<%= usuario.getFotoPerfil() %>" alt="Foto de perfil">
    </div>
    <div class="datos-cliente">
        <p>Tipo de usuario: <span id="tipoUsuario"><%= usuario.getTipoUsuario() %></span> ★</p>
        <p>Nombre: <span id="nombreCompleto"><%= usuario.getNombre() %></span></p>
        <p>Usuario: <span id="nombreUsuario"><%= usuario.getUsuario() %></span></p>
        <p>Correo electrónico: <span id="correoElectronico"><%= usuario.getCorreo() %></span></p>
        <button class="boton-modificar" onclick="window.location.href='modificarDatos.html'">Modificar datos</button>
    </div>
</section>
</body>
</html>
