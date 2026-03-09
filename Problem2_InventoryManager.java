import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Problem 2: E-commerce Flash Sale Inventory Manager
 * Concepts: Hash table for stock lookup, collision resolution, load factor, thread safety
 */
public class Problem2_InventoryManager {

    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private LinkedHashMap<String, Queue<Integer>> waitingList = new LinkedHashMap<>();

    public Problem2_InventoryManager() {
        inventory.put("IPHONE15_256GB", new AtomicInteger(100));
        inventory.put("SAMSUNG_S24", new AtomicInteger(50));
    }

    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "Product not found";

        // Atomic decrement - thread safe
        int remaining = stock.decrementAndGet();
        if (remaining >= 0) {
            return "Success, " + remaining + " units remaining";
        } else {
            stock.incrementAndGet(); // rollback
            waitingList.computeIfAbsent(productId, k -> new LinkedList<>()).add(userId);
            int position = waitingList.get(productId).size();
            return "Added to waiting list, position #" + position;
        }
    }

    public void addStock(String productId, int quantity) {
        inventory.put(productId, new AtomicInteger(quantity));
    }

    public static void main(String[] args) {
        Problem2_InventoryManager manager = new Problem2_InventoryManager();

        System.out.println("=== Problem 2: Flash Sale Inventory Manager ===");
        System.out.println("checkStock(\"IPHONE15_256GB\") → " + manager.checkStock("IPHONE15_256GB") + " units");
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Drain all stock
        for (int i = 0; i < 98; i++) manager.purchaseItem("IPHONE15_256GB", i + 1000);
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}
