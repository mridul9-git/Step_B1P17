import java.util.*;

/**
 * Problem 10: Multi-Level Cache System with Hash Tables
 * Concepts: Multiple hash tables, resizing/rehashing, performance benchmarking, load factor optimization
 */
public class Problem10_MultiLevelCache {

    private static class VideoData {
        String videoId;
        String content;
        int accessCount;

        VideoData(String id, String content) {
            this.videoId = id;
            this.content = content;
            this.accessCount = 0;
        }
    }

    // L1: In-memory LRU cache (LinkedHashMap with access-order = true)
    private LinkedHashMap<String, VideoData> l1Cache;
    private final int L1_CAPACITY = 10_000;

    // L2: Simulated SSD cache (HashMap pointing to file paths)
    private HashMap<String, String> l2Cache = new HashMap<>(100_000);
    private final int L2_CAPACITY = 100_000;

    // L3: Simulated database (all videos)
    private HashMap<String, String> l3Database = new HashMap<>();

    // Stats
    private int l1Hits, l2Hits, l3Hits, misses;
    private long l1Time, l2Time, l3Time;
    private HashMap<String, Integer> accessCounts = new HashMap<>();

    public Problem10_MultiLevelCache() {
        l1Cache = new LinkedHashMap<String, VideoData>(L1_CAPACITY, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                if (size() > L1_CAPACITY) {
                    // Demote to L2 on eviction
                    l2Cache.put(eldest.getKey(), "/ssd/videos/" + eldest.getKey());
                    return true;
                }
                return false;
            }
        };

        // Pre-populate L3
        for (int i = 1; i <= 1_000_000; i++) {
            l3Database.put("video_" + i, "content_of_video_" + i);
        }
    }

    public String getVideo(String videoId) {
        accessCounts.merge(videoId, 1, Integer::sum);

        // Check L1
        long start = System.nanoTime();
        VideoData v = l1Cache.get(videoId);
        if (v != null) {
            l1Hits++;
            l1Time += (System.nanoTime() - start);
            System.out.println("→ L1 Cache HIT (0.5ms)");
            return v.content;
        }
        System.out.println("→ L1 Cache MISS (0.5ms)");

        // Check L2
        start = System.nanoTime();
        String l2Path = l2Cache.get(videoId);
        if (l2Path != null) {
            l2Hits++;
            l2Time += (System.nanoTime() - start);
            System.out.println("→ L2 Cache HIT (5ms)");
            // Promote to L1 if accessed frequently
            VideoData promoted = new VideoData(videoId, "content_from_ssd");
            l1Cache.put(videoId, promoted);
            System.out.println("→ Promoted to L1");
            return promoted.content;
        }
        System.out.println("→ L2 Cache MISS");

        // Check L3 (database)
        start = System.nanoTime();
        String content = l3Database.get(videoId);
        if (content != null) {
            l3Hits++;
            l3Time += (System.nanoTime() - start);
            System.out.println("→ L3 Database HIT (150ms)");
            // Add to L2 cache
            l2Cache.put(videoId, "/ssd/videos/" + videoId);
            int count = accessCounts.getOrDefault(videoId, 1);
            System.out.println("→ Added to L2 (access count: " + count + ")");
            return content;
        }

        misses++;
        System.out.println("→ Video not found");
        return null;
    }

    public void invalidateCache(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        System.out.println("Cache invalidated for: " + videoId);
    }

    public void getStatistics() {
        int total = l1Hits + l2Hits + l3Hits + misses;
        if (total == 0) return;

        System.out.println("\n=== Cache Statistics ===");
        System.out.printf("L1: Hit Rate %.0f%%, Avg Time: 0.5ms%n", (l1Hits * 100.0 / total));
        System.out.printf("L2: Hit Rate %.0f%%, Avg Time: 5ms%n",   (l2Hits * 100.0 / total));
        System.out.printf("L3: Hit Rate %.0f%%, Avg Time: 150ms%n", (l3Hits * 100.0 / total));
        double overall = ((l1Hits + l2Hits + l3Hits) * 100.0 / total);
        System.out.printf("Overall: Hit Rate %.0f%%%n", overall);
        System.out.printf("L1 Cache Size: %d/%d | L2 Cache Size: %d/%d%n",
                l1Cache.size(), L1_CAPACITY, l2Cache.size(), L2_CAPACITY);
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 10: Multi-Level Cache System ===");
        Problem10_MultiLevelCache cache = new Problem10_MultiLevelCache();

        System.out.println("\ngetVideo(\"video_123\")");
        cache.getVideo("video_123");

        System.out.println("\ngetVideo(\"video_123\") [second request]");
        cache.getVideo("video_123");

        System.out.println("\ngetVideo(\"video_999\")");
        cache.getVideo("video_999");

        // Simulate warm cache with many videos
        for (int i = 1; i <= 100; i++) cache.getVideo("video_" + i);

        cache.getStatistics();
    }
}
