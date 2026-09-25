package controller;

import dto.UserDTO;
import dto.UserRole;

public class Session {

    private static String token;
    private static String email;
    private static UserRole role;

    private Session() { }

    public static void set(UserDTO user) {
        Session.token = user.getToken();
        Session.email = user.getEmail();
        Session.role = user.getRole();
    }

    public static void clear() {
        token = null; email = null; role = null;
    }

    public static String getToken() { return token; }
    public static String getEmail() { return email; }

    public static String getName() { return email; }

    public static UserRole getRole() { return role; }
    public static boolean isAdmin() { return role == UserRole.ADMIN; }
    public static boolean isDev() { return role == UserRole.DEV; }
    public static boolean isLoggedIn() { return token != null; }
}
