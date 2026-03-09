# Hash Table Fundamentals and Implementation - Practice Problems

This repository contains Java solutions for 10 real-world hash table problems covering core concepts like O(1) lookup, collision handling, LRU eviction, frequency counting, and more.

## Problems Overview

| # | Problem | Key Concepts |
|---|---------|-------------|
| 1 | Social Media Username Availability Checker | HashMap basics, O(1) lookup, frequency counting |
| 2 | E-commerce Flash Sale Inventory Manager | Thread safety, ConcurrentHashMap, atomic ops |
| 3 | DNS Cache with TTL | Custom Entry class, LRU eviction, TTL expiry |
| 4 | Plagiarism Detection System | String hashing, n-gram indexing, similarity scoring |
| 5 | Real-Time Analytics Dashboard | Multiple HashMaps, frequency counting, top-K |
| 6 | Distributed Rate Limiter for API Gateway | Token bucket, time-based operations, concurrency |
| 7 | Autocomplete System for Search Engine | Trie + HashMap hybrid, prefix matching, top-K |
| 8 | Parking Lot with Open Addressing | Linear probing, custom hash function, load factor |
| 9 | Two-Sum Variants for Financial Transactions | Complement lookup, K-sum, duplicate detection |
| 10 | Multi-Level Cache System | L1/L2/L3 cache, LRU promotion, hit rate metrics |

## How to Run

### Prerequisites
- Java 11 or above

### Compile and Run a Single Problem
```bash
cd src/problems
javac Problem1_UsernameChecker.java
java Problem1_UsernameChecker
```

### Compile and Run All Problems
```bash
cd src/problems
javac *.java
for f in Problem*.java; do
  classname="${f%.java}"
  echo "=== Running $classname ==="
  java "$classname"
done
```

## Project Structure
```
hash-table-assignments/
├── README.md
└── src/
    └── problems/
        ├── Problem1_UsernameChecker.java
        ├── Problem2_InventoryManager.java
        ├── Problem3_DNSCache.java
        ├── Problem4_PlagiarismDetector.java
        ├── Problem5_AnalyticsDashboard.java
        ├── Problem6_RateLimiter.java
        ├── Problem7_AutocompleteSystem.java
        ├── Problem8_ParkingLot.java
        ├── Problem9_FinancialTransactions.java
        └── Problem10_MultiLevelCache.java
```

## Key Hash Table Concepts Covered

**Collision Resolution**
- Chaining (separate lists per bucket) — Problems 1, 3
- Open Addressing with linear probing — Problem 8

**Performance**
- O(1) average insert/lookup/delete
- Load factor management (resize at ~0.75)
- Benchmarking hash vs linear search

**Advanced Patterns**
- LRU eviction via `LinkedHashMap` (access-order mode)
- TTL-based expiration
- Thread-safe operations with `ConcurrentHashMap`
- Trie + HashMap hybrid for prefix matching
