import java.util.*;

/**
 * Problem 1: Social Media Username Availability Checker
 * Concepts: Hash table basics, O(1) lookup, collision handling, frequency counting
 */
public class Problem1_UsernameChecker {

    private HashMap<String, Integer> registeredUsers = new HashMap<>();
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();

    // Register some initial users
    public Problem1_UsernameChecker() {
        registeredUsers.put("john_doe", 1001);
        registeredUsers.put("admin", 1);
        registeredUsers.put("user123", 1002);
    }

    // O(1) lookup
    public boolean checkAvailability(String username) {
        attemptFrequency.merge(username, 1, Integer::sum);
        return !registeredUsers.containsKey(username);
    }

    public boolean registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            registeredUsers.put(username, userId);
            return true;
        }
        return false;
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String numbered = username + i;
            if (!registeredUsers.containsKey(numbered)) {
                suggestions.add(numbered);
            }
        }
        String dotVersion = username.replace("_", ".");
        if (!registeredUsers.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }
        String underscoreVersion = username + "_";
        if (!registeredUsers.containsKey(underscoreVersion)) {
            suggestions.add(underscoreVersion);
        }
        return suggestions;
    }

    public String getMostAttempted() {
        return Collections.max(attemptFrequency.entrySet(),
                Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) {
        Problem1_UsernameChecker checker = new Problem1_UsernameChecker();

        System.out.println("=== Problem 1: Username Availability Checker ===");
        System.out.println("checkAvailability(\"john_doe\") → " + checker.checkAvailability("john_doe"));
        System.out.println("checkAvailability(\"jane_smith\") → " + checker.checkAvailability("jane_smith"));
        System.out.println("suggestAlternatives(\"john_doe\") → " + checker.suggestAlternatives("john_doe"));

        // Simulate multiple attempts
        for (int i = 0; i < 100; i++) checker.checkAvailability("admin");
        System.out.println("getMostAttempted() → " + checker.getMostAttempted());
    }
}
