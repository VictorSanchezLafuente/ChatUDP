# Aplicación de Chat UDP Simple
### Esta sencilla aplicación de chat demuestra el uso de los sockets de Java para implementar un sistema de chat básico. Consiste en un servidor que puede manejar múltiples clientes y permite a los usuarios unirse a salas y enviar mensajes entre ellos.

- Componentes
    - La aplicación se divide en los siguientes componentes:

      - ChatClient.java: Implementa el cliente de chat con el que los usuarios interactúan para conectarse al servidor, unirse a salas de chat y enviar/recibir mensajes.
      - ChatServer.java: Implementa el servidor de chat que gestiona las conexiones de los clientes, las salas y la difusión de mensajes.
      - Room.java: Representa una sala de chat que puede contener varios usuarios.
      - User.java: Representa un usuario en el sistema de chat.
