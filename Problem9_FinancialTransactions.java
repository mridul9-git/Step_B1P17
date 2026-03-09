import java.util.*;

/**
 * Problem 9: Two-Sum Problem Variants for Financial Transactions
 * Concepts: Hash table complement lookup, O(1) performance, multiple hash tables, time complexity
 */
public class Problem9_FinancialTransactions {

    private static class Transaction {
        int id;
        double amount;
        String merchant;
        String account;
        String time;

        Transaction(int id, double amount, String merchant, String account, String time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }

        @Override
        public String toString() {
            return String.format("{id:%d, amount:%.0f, merchant:\"%s\"}", id, amount, merchant);
        }
    }

    private List<Transaction> transactions;

    public Problem9_FinancialTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Classic Two-Sum using hash table - O(n)
    public List<int[]> findTwoSum(double target) {
        List<int[]> result = new ArrayList<>();
        HashMap<Double, Integer> seen = new HashMap<>(); // amount -> transaction id

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (seen.containsKey(complement)) {
                result.add(new int[]{seen.get(complement), t.id});
            }
            seen.put(t.amount, t.id);
        }
        return result;
    }

    // Two-Sum with time window (1 hour = 60 minutes)
    public List<int[]> findTwoSumWithTimeWindow(double target, int windowMinutes) {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                Transaction a = transactions.get(i);
                Transaction b = transactions.get(j);
                int timeDiff = Math.abs(parseMinutes(a.time) - parseMinutes(b.time));
                if (timeDiff <= windowMinutes && a.amount + b.amount == target) {
                    result.add(new int[]{a.id, b.id});
                }
            }
        }
        return result;
    }

    private int parseMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // K-Sum using recursion + memoization
    public List<List<Integer>> findKSum(int k, double target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (Transaction t : transactions) {
            amounts.add(t.amount);
            ids.add(t.id);
        }
        kSumHelper(amounts, ids, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(List<Double> amounts, List<Integer> ids, int k, double target,
                            int start, List<Integer> current, List<List<Integer>> result) {
        if (k == 1) {
            for (int i = start; i < amounts.size(); i++) {
                if (Math.abs(amounts.get(i) - target) < 0.001) {
                    List<Integer> combo = new ArrayList<>(current);
                    combo.add(ids.get(i));
                    result.add(combo);
                }
            }
            return;
        }
        for (int i = start; i < amounts.size() - k + 1; i++) {
            current.add(ids.get(i));
            kSumHelper(amounts, ids, k - 1, target - amounts.get(i), i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Detect duplicate transactions: same amount + merchant, different accounts
    public List<String> detectDuplicates() {
        HashMap<String, List<Transaction>> groupedByAmountMerchant = new HashMap<>();
        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant;
            groupedByAmountMerchant.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        List<String> duplicates = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : groupedByAmountMerchant.entrySet()) {
            Set<String> accounts = new HashSet<>();
            for (Transaction t : entry.getValue()) accounts.add(t.account);
            if (accounts.size() > 1) {
                duplicates.add(entry.getKey() + " → accounts: " + accounts);
            }
        }
        return duplicates;
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 9: Two-Sum Variants for Financial Transactions ===");

        List<Transaction> txns = Arrays.asList(
                new Problem9_FinancialTransactions.Transaction(1, 500, "Store A", "acc1", "10:00"),
                new Problem9_FinancialTransactions.Transaction(2, 300, "Store B", "acc2", "10:15"),
                new Problem9_FinancialTransactions.Transaction(3, 200, "Store C", "acc3", "10:30"),
                new Problem9_FinancialTransactions.Transaction(4, 500, "Store A", "acc2", "10:45")
        );

        Problem9_FinancialTransactions detector = new Problem9_FinancialTransactions(txns);

        System.out.println("findTwoSum(target=500):");
        for (int[] pair : detector.findTwoSum(500)) {
            System.out.println("  → (id:" + pair[0] + ", id:" + pair[1] + ")");
        }

        System.out.println("detectDuplicates():");
        for (String dup : detector.detectDuplicates()) {
            System.out.println("  → " + dup);
        }

        System.out.println("findKSum(k=3, target=1000):");
        for (List<Integer> combo : detector.findKSum(3, 1000)) {
            System.out.println("  → " + combo);
        }
    }
}
