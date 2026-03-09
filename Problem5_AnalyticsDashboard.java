import java.util.*;

/**
 * Problem 5: Real-Time Analytics Dashboard for Website Traffic
 * Concepts: Frequency counting, multiple hash tables, load factor, time/space optimization
 */
public class Problem5_AnalyticsDashboard {

    private HashMap<String, Integer> pageViews = new HashMap<>();
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    private HashMap<String, Integer> trafficSources = new HashMap<>();
    private long lastUpdateTime = System.currentTimeMillis();

    public void processEvent(String url, String userId, String source) {
        // Track page views - O(1)
        pageViews.merge(url, 1, Integer::sum);

        // Track unique visitors - O(1)
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);

        // Track traffic sources - O(1)
        trafficSources.merge(source, 1, Integer::sum);
    }

    public void getDashboard() {
        System.out.println("\n=== Dashboard (updated every 5s) ===");
        System.out.println("\nTop Pages:");

        // Sort by view count (top 10) using PriorityQueue
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue()
        );
        pq.addAll(pageViews.entrySet());

        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            int unique = uniqueVisitors.getOrDefault(entry.getKey(), new HashSet<>()).size();
            System.out.printf("  %d. %s - %,d views (%,d unique)%n",
                    rank++, entry.getKey(), entry.getValue(), unique);
        }

        // Traffic sources breakdown
        int total = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\nTraffic Sources:");
        trafficSources.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.printf("  %s: %d%%%n",
                        e.getKey(), (e.getValue() * 100 / Math.max(total, 1))));
    }

    public static void main(String[] args) {
        Problem5_AnalyticsDashboard dashboard = new Problem5_AnalyticsDashboard();

        System.out.println("=== Problem 5: Real-Time Analytics Dashboard ===");

        String[] pages = {"/article/breaking-news", "/sports/championship", "/tech/ai-news", "/home"};
        String[] sources = {"google", "facebook", "direct", "other"};
        Random rand = new Random(42);

        // Simulate 1000 page view events
        for (int i = 0; i < 1000; i++) {
            String page = pages[rand.nextInt(pages.length)];
            String user = "user_" + rand.nextInt(200);
            String source = sources[rand.nextInt(sources.length)];
            dashboard.processEvent(page, user, source);
        }

        dashboard.getDashboard();
    }
}
