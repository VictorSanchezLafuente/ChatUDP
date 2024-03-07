package chatUDP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Room {
    private final String name;
    private final List<User> users;

    public Room(String name) {
        this.name = name;
        this.users = Collections.synchronizedList(new LinkedList<>());
    }

    public void addUser(User user) {
        synchronized (users) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        synchronized (users) {
            users.remove(user);
        }
    }

    public void sendMessage(String message, User sender) {
        synchronized (users) {
            for (User user : users) {
                if (!user.equals(sender)) {
                    user.sendMessage(message);
                }
            }
        }
    }

    public List<User> listUsers() {
        synchronized (users) {
            return new ArrayList<>(users);
        }
    }

    public String getName() {
        return name;
    }
}
