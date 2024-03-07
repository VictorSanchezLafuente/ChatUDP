package chatUDP;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<User> users;
    private List<Room> rooms;

    public ChatServer() {
        users = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor de chat iniciado en el puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress().getHostAddress());
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private User user;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                initializeStreams();
                welcomeMessage();

                String username = getUserInput("Ingrese su nombre de usuario:");
                user = new User(username, clientSocket);
                users.add(user);

                greetUser(username);

                handleChatInput();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void initializeStreams() throws IOException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        private void welcomeMessage() {
            out.println("Conexión establecida. Bienvenido al chat!");
        }

        private String getUserInput(String prompt) throws IOException {
            String userInput = null;
            while (userInput == null || userInput.isEmpty()) {
                out.println(prompt);
                userInput = in.readLine();
            }
            return userInput;
        }

        private void greetUser(String username) {
            out.println("Bienvenido, " + username + "! Para ver la lista de comandos, escriba /help");
        }

        private void handleChatInput() throws IOException {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("/")) {
                    handleCommand(inputLine);
                } else {
                    broadcastMessage(user.getUsername() + ": " + inputLine, user.getCurrentRoom());
                }
            }
        }

        private void handleCommand(String command) {
            String[] tokens = command.split("\\s+");
            switch (tokens[0]) {
                case "/help":
                    out.println("Lista de comandos:");
                    out.println("/list -> Lista todas las salas disponibles");
                    out.println("/create + nombre_sala -> Crea una nueva sala");
                    out.println("/join + nombre_sala -> Únete a una sala existente");
                    out.println("/exit -> Salir del chat");
                    break;
                case "/list":
                    if (rooms.isEmpty()) {
                        out.println("No hay salas Creadas.");
                    } else {
                        out.println("Salas disponibles:");
                        for (Room room : rooms) {
                            out.println("- " + room.getName());
                        }
                    }
                    break;
                case "/create":
                    if (tokens.length < 2) {
                        out.println("Uso: /create [nombre_sala]");
                    } else {
                        String roomName = tokens[1];
                        Room room = createRoom(roomName);
                        if (room != null) {
                            user.joinRoom(room);
                            out.println("Sala '" + roomName + "' creada y unido exitosamente.");
                            out.println("Escriba el mensaje:");
                        } else {
                            out.println("La sala '" + roomName + "' ya existe.");
                        }
                    }
                    break;
                case "/join":
                    if (tokens.length < 2) {
                        out.println("Uso: /join [nombre_sala]");
                    } else {
                        String roomName = tokens[1];
                        Room room = findRoom(roomName);
                        if (room != null) {
                            user.joinRoom(room);
                            out.println("Unido exitosamente a la sala '" + roomName + "'.");
                        } else {
                            out.println("La sala '" + roomName + "' no existe.");
                        }
                    }
                    break;
                case "/exit":
                    out.println("Desconectado del chat.");
                    break;
                default:
                    out.println("Comando no reconocido. Escriba /help para ver la lista de comandos.");
            }
        }
        
        private void closeConnection() {
            try {
                if (user != null) {
                    users.remove(user);
                    user.leaveRoom();
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void broadcastMessage(String message, Room room) {
        if (room != null) {
            List<User> roomUsers = room.listUsers();
            for (User user : roomUsers) {
                user.sendMessage(message);
            }
        } else {
            System.out.println("La sala es nula. No se puede enviar el mensaje.");
        }
    }

    public Room findRoom(String roomName) {
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(roomName)) {
                return room;
            }
        }
        return null; // Retorna null si no se encuentra la sala
    }

    public synchronized Room createRoom(String roomName) {
        for (Room room : rooms) {
            if (room.getName().equalsIgnoreCase(roomName)) {
                return null; // La sala ya existe
            }
        }
        // Si la sala no existe, crea una nueva
        Room newRoom = new Room(roomName);
        rooms.add(newRoom);
        return newRoom;
    }

	public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.startServer(8080);
    }
}




