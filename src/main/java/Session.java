
public class Session {

    private static String token;
    private static String email;
    private static String name;
    private static String role;

    private Session() { }

    public static void set(String token, String email, String name, String role) {
        Session.token = token;
        Session.email = email;
        Session.name = name;
        Session.role = role;
    }

    public static void clear() {
        token = null; email = null; name = null; role = null;
    }

    public static String getToken() { return token; }
    public static String getEmail() { return email; }
    public static String getName() { return name; }
    public static String getRole() { return role; }
    public static boolean isAdmin() { return "ADMIN".equals(role); }
    public static boolean isLoggedIn() { return token != null; }
}
