import java.util.*;

/**
 * Problem 4: Plagiarism Detection System
 * Concepts: String hashing, frequency counting, hash function properties, performance benchmarking
 */
public class Problem4_PlagiarismDetector {

    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();
    private int N_GRAM_SIZE = 5;

    public void indexDocument(String docId, String content) {
        List<String> ngrams = extractNGrams(content);
        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
        System.out.println("Indexed \"" + docId + "\": extracted " + ngrams.size() + " n-grams");
    }

    private List<String> extractNGrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
        for (int i = 0; i <= words.length - N_GRAM_SIZE; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < i + N_GRAM_SIZE; j++) {
                if (j > i) sb.append(" ");
                sb.append(words[j]);
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    public Map<String, Double> analyzeDocument(String docId, String content) {
        List<String> ngrams = extractNGrams(content);
        System.out.println("\nanalyzeDocument(\"" + docId + "\")");
        System.out.println("→ Extracted " + ngrams.size() + " n-grams");

        // Count matches per document
        Map<String, Integer> matchCounts = new HashMap<>();
        for (String ngram : ngrams) {
            Set<String> docs = ngramIndex.get(ngram);
            if (docs != null) {
                for (String matchDoc : docs) {
                    if (!matchDoc.equals(docId)) {
                        matchCounts.merge(matchDoc, 1, Integer::sum);
                    }
                }
            }
        }

        // Calculate similarity percentages
        Map<String, Double> similarities = new HashMap<>();
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / ngrams.size();
            similarities.put(entry.getKey(), similarity);
            String verdict = similarity > 50 ? "PLAGIARISM DETECTED" :
                           similarity > 15 ? "suspicious" : "ok";
            System.out.printf("→ Found %d matching n-grams with \"%s\"%n", entry.getValue(), entry.getKey());
            System.out.printf("→ Similarity: %.1f%% (%s)%n", similarity, verdict);
        }
        return similarities;
    }

    public static void main(String[] args) {
        Problem4_PlagiarismDetector detector = new Problem4_PlagiarismDetector();

        System.out.println("=== Problem 4: Plagiarism Detection System ===");

        String essay089 = "The quick brown fox jumps over the lazy dog in the forest near the river bank";
        String essay092 = "Hash tables are data structures that provide O(1) average time complexity for insertion deletion and lookup operations using a hash function to compute indices";
        String essay123 = "Hash tables are data structures that provide O(1) average time complexity for insertion deletion and lookup operations using a custom hash function to compute bucket indices";

        detector.indexDocument("essay_089.txt", essay089);
        detector.indexDocument("essay_092.txt", essay092);
        detector.analyzeDocument("essay_123.txt", essay123);
    }
}
