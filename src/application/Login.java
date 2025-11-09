package application;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Login {
	// saving the user data from path
    public static void saveUsers(ArrayList<Player> users, Path path) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(users);
            System.out.println("Users saved successfully to " + path);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    // loading the user data from path
    public static ArrayList<Player> loadUsers(Path path) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            ArrayList<Player> users = (ArrayList<Player>) in.readObject();
            System.out.println("Users loaded successfully from " + path);
            return users;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No saved data found, or file is corrupted: " + e.getMessage());
            return new ArrayList<>(); // return empty list if none found
        }
    }
}
