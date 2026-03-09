import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Problem 6: Distributed Rate Limiter for API Gateway
 * Concepts: Hash table for client tracking, time-based operations, collision handling, concurrency
 */
public class Problem6_RateLimiter {

    private static class TokenBucket {
        int tokens;
        long lastRefillTime;
        final int maxTokens;
        final int refillRatePerHour;

        TokenBucket(int maxTokens) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
            this.refillRatePerHour = maxTokens;
        }

        synchronized boolean consume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        synchronized void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            long hourMs = 3600_000L;
            if (elapsed >= hourMs) {
                tokens = maxTokens;
                lastRefillTime = now;
            }
        }

        synchronized long getResetTimeSeconds() {
            long hourMs = 3600_000L;
            return (hourMs - (System.currentTimeMillis() - lastRefillTime)) / 1000;
        }
    }

    private ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private final int DEFAULT_LIMIT = 1000;

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clients.computeIfAbsent(clientId, k -> new TokenBucket(DEFAULT_LIMIT));

        if (bucket.consume()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            long retryAfter = bucket.getResetTimeSeconds();
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    public Map<String, Object> getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) return Map.of("error", "Client not found");

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("used", DEFAULT_LIMIT - bucket.tokens);
        status.put("limit", DEFAULT_LIMIT);
        status.put("remaining", bucket.tokens);
        status.put("reset_in_seconds", bucket.getResetTimeSeconds());
        return status;
    }

    public static void main(String[] args) {
        Problem6_RateLimiter limiter = new Problem6_RateLimiter();

        System.out.println("=== Problem 6: Distributed Rate Limiter ===");
        System.out.println(limiter.checkRateLimit("abc123")); // 999 remaining
        System.out.println(limiter.checkRateLimit("abc123")); // 998 remaining
        System.out.println(limiter.checkRateLimit("xyz789")); // different client

        // Exhaust the limit
        for (int i = 0; i < 998; i++) limiter.checkRateLimit("abc123");
        System.out.println(limiter.checkRateLimit("abc123")); // should be denied

        System.out.println("getRateLimitStatus(\"abc123\") → " + limiter.getRateLimitStatus("abc123"));
    }
}
