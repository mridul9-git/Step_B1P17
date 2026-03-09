import java.util.*;

/**
 * Problem 7: Autocomplete System for Search Engine
 * Concepts: Hash table for query frequency, string hashing, prefix search, space optimization
 */
public class Problem7_AutocompleteSystem {

    // Trie Node with HashMap children
    private static class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        int frequency = 0;
        String fullWord = "";
    }

    private TrieNode root = new TrieNode();
    private HashMap<String, Integer> queryFrequency = new HashMap<>();

    public void insertQuery(String query, int frequency) {
        queryFrequency.put(query.toLowerCase(), frequency);
        TrieNode node = root;
        for (char c : query.toLowerCase().toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
        node.frequency = frequency;
        node.fullWord = query;
    }

    public void updateFrequency(String query) {
        int newFreq = queryFrequency.merge(query.toLowerCase(), 1, Integer::sum);
        insertQuery(query, newFreq);
        System.out.println("updateFrequency(\"" + query + "\") → Frequency: " + newFreq);
    }

    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        // Collect all words with this prefix using BFS
        List<Map.Entry<String, Integer>> results = new ArrayList<>();
        collectWords(node, prefix, results);

        // Sort by frequency descending (top 10)
        results.sort((a, b) -> b.getValue() - a.getValue());

        List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < Math.min(10, results.size()); i++) {
            suggestions.add(results.get(i).getKey() + " (" + String.format("%,d", results.get(i).getValue()) + " searches)");
        }
        return suggestions;
    }

    private void collectWords(TrieNode node, String currentPrefix, List<Map.Entry<String, Integer>> results) {
        if (node.isEndOfWord) {
            results.add(new AbstractMap.SimpleEntry<>(currentPrefix, node.frequency));
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectWords(entry.getValue(), currentPrefix + entry.getKey(), results);
        }
    }

    public static void main(String[] args) {
        Problem7_AutocompleteSystem ac = new Problem7_AutocompleteSystem();

        System.out.println("=== Problem 7: Autocomplete System ===");

        // Seed with popular queries
        ac.insertQuery("java tutorial", 1_234_567);
        ac.insertQuery("javascript", 987_654);
        ac.insertQuery("java download", 456_789);
        ac.insertQuery("java 21 features", 1);
        ac.insertQuery("java interview questions", 345_000);
        ac.insertQuery("javascript vs python", 210_000);

        System.out.println("search(\"jav\") →");
        List<String> results = ac.search("jav");
        for (int i = 0; i < results.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + results.get(i));
        }

        ac.updateFrequency("java 21 features");
        ac.updateFrequency("java 21 features");
    }
}
