package controller;

import dto.UserDTO;
import dto.UserRole;

public class Session {

    private static String token;
    private static String email;
    private static String name;
    private static UserRole role;

    private Session() { }

    public static void set(UserDTO user) {
        Session.token = user.getToken();
        Session.email = user.getEmail();
        Session.name = user.getName();
        Session.role = user.getRole();
    }

    public static void clear() {
        token = null; email = null; name = null; role = null;
    }

    public static String getToken() { return token; }
    public static String getEmail() { return email; }
    public static String getName() { return name; }
    public static UserRole getRole() { return role; }
    public static boolean isAdmin() { return role == UserRole.ADMIN; }
    public static boolean isDev() { return role == UserRole.DEV; }
    public static boolean isLoggedIn() { return token != null; }
}
