import java.util.*;

/**
 * Problem 3: DNS Cache with TTL (Time To Live)
 * Concepts: Custom Entry class, chaining for collision resolution, time-based operations, LRU eviction
 */
public class Problem3_DNSCache {

    private static class DNSEntry {
        String domain;
        String ipAddress;
        long timestamp;
        long expiryTime; // in milliseconds

        DNSEntry(String domain, String ip, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ip;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = timestamp + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private LinkedHashMap<String, DNSEntry> cache;
    private final int MAX_CACHE_SIZE;
    private int hits = 0, misses = 0;
    private long totalLookupTime = 0;
    private int lookupCount = 0;

    public Problem3_DNSCache(int maxSize) {
        this.MAX_CACHE_SIZE = maxSize;
        // LRU eviction using LinkedHashMap (access-order)
        this.cache = new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public String resolve(String domain) {
        long start = System.nanoTime();
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            totalLookupTime += elapsed;
            lookupCount++;
            System.out.println("Cache HIT → " + entry.ipAddress + " (retrieved in " + elapsed + "ms)");
            return entry.ipAddress;
        }

        if (entry != null) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED → querying upstream...");
        } else {
            System.out.println("Cache MISS → querying upstream...");
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, 300));

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        totalLookupTime += elapsed;
        lookupCount++;
        System.out.println("Resolved: " + domain + " → " + ip + " (TTL: 300s)");
        return ip;
    }

    private String queryUpstreamDNS(String domain) {
        // Simulated upstream DNS lookup
        Map<String, String> dnsDb = new HashMap<>();
        dnsDb.put("google.com", "172.217.14.206");
        dnsDb.put("github.com", "140.82.114.4");
        dnsDb.put("stackoverflow.com", "151.101.1.69");
        return dnsDb.getOrDefault(domain, "8.8.8.8");
    }

    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total > 0 ? (hits * 100.0 / total) : 0;
        double avgTime = lookupCount > 0 ? (totalLookupTime * 1.0 / lookupCount) : 0;
        System.out.printf("Hit Rate: %.1f%%, Avg Lookup Time: %.1fms, Cache Size: %d%n",
                hitRate, avgTime, cache.size());
    }

    public static void main(String[] args) throws InterruptedException {
        Problem3_DNSCache dns = new Problem3_DNSCache(1000);

        System.out.println("=== Problem 3: DNS Cache with TTL ===");
        dns.resolve("google.com");
        dns.resolve("google.com"); // should be cache hit
        dns.resolve("github.com");
        dns.resolve("google.com"); // cache hit
        System.out.print("getCacheStats() → ");
        dns.getCacheStats();
    }
}
