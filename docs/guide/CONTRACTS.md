# Assignment 4 — Algorithm Analysis

**Course:** Design and Analysis of Algorithms  
**Assignment:** Dynamic Programming and String Processing  
**Programming Language:** Java

## 1. Introduction

This assignment implements eight commands covering dynamic programming, tree algorithms, and string processing.

The implemented algorithms are:

1. Longest Common Subsequence (LCS)
2. Ordered Adjacent-Block Merging
3. Maximum-Weight Independent Set on a Tree
4. Naive String Matching
5. Knuth–Morris–Pratt (KMP)
6. Exact Rabin–Karp
7. Suffix Array with Kasai LCP
8. Aho–Corasick

The program processes UTF-8 JSON Lines input and produces one JSON result per request.

## 2. Longest Common Subsequence (LCS)

**Approach:** Dynamic Programming

The algorithm finds the longest sequence of characters appearing in two strings in the same relative order.

### DP State

Let `dp[i][j]` represent the length of the longest common subsequence of the first `i` characters of string `a` and the first `j` characters of string `b`.

### Transition

If the characters match:

`dp[i][j] = dp[i-1][j-1] + 1`

Otherwise:

`dp[i][j] = max(dp[i-1][j], dp[i][j-1])`

### Base Cases

`dp[i][0] = 0` and `dp[0][j] = 0`.

### Reconstruction

Starting from `dp[n][m]`, matching characters are added to the answer while moving diagonally. For a mismatch, reconstruction follows a neighboring state with the optimal value. The collected characters are reversed to obtain the subsequence.

### Correctness

When the final characters match, an optimal LCS can include that character. Otherwise, at least one final character must be excluded, so the maximum of the two smaller prefix problems is optimal.

The reconstruction follows these optimal decisions and therefore produces a valid longest common subsequence.

### Complexity

- Time: O(n × m)
- Auxiliary Space: O(n × m)

## 3. Ordered Adjacent-Block Merging

**Approach:** Interval Dynamic Programming

The objective is to merge adjacent blocks while minimizing the total merging cost.

### DP State

`dp[l][r]` represents the minimum cost of merging blocks from index `l` to index `r` into one block.

### Transition

For each possible final split `k`:

`dp[l][r] = min(dp[l][k] + dp[k+1][r] + sum(l,r))`

where `l <= k < r`.

### Base Case

`dp[i][i] = 0`, because one block requires no merging.

### Reconstruction

The best split for each interval is recorded. The algorithm recursively reconstructs the left and right subinterval merges before adding the final parent merge.

This produces a chronological plan containing exactly `n-1` merges when `n > 1`.

### Correctness

Every valid final merge combines two adjacent intervals. Considering every possible final split covers all valid solutions. Since both subintervals are solved optimally, the minimum transition gives the optimal total cost.

### Complexity

- Time: O(n³)
- Auxiliary Space: O(n²)

Long integer arithmetic is required because merging costs may exceed the 32-bit integer range.

## 4. Maximum-Weight Independent Set on a Tree

**Approach:** Tree Dynamic Programming

The algorithm selects vertices with maximum total weight while ensuring that no two selected vertices are adjacent.

### DP States

For each vertex `v`:

- `take[v]`: Maximum weight when vertex `v` is selected.
- `skip[v]`: Maximum weight when vertex `v` is not selected.

### Transitions

For each child `u` of `v`:

`take[v] = weight[v] + sum(skip[u])`

`skip[v] = sum(max(take[u], skip[u]))`

### Base Cases

For a leaf vertex, selecting it contributes its weight, while skipping it contributes zero.

An empty selection is allowed.

### Reconstruction

If a vertex is selected, its children must be skipped. If a vertex is skipped, each child can be selected or skipped according to the better DP value.

An iterative traversal avoids recursion-depth problems on long chains.

### Correctness

Every vertex has two possibilities: selected or excluded. Selecting a vertex forces its children to be excluded. Skipping it allows each child subtree to choose its optimal state.

These cases cover every valid independent set.

### Complexity

- Time: O(n)
- Auxiliary Space: O(n)

## 5. Naive String Matching

**Approach:** Direct Character Comparison

The algorithm checks every possible starting position in the text and compares the pattern character by character.

All matching positions are returned, including overlapping occurrences.

### Edge Cases

- Empty pattern: matches every position from `0` to `n`.
- Pattern longer than text: no matches.
- Empty text and empty pattern: one match at position `0`.

### Correctness

Every valid starting position is checked, and a position is reported only when all pattern characters match. Therefore, the algorithm finds all exact occurrences.

### Complexity

- Time: O(n × m + n + 1)
- Auxiliary Space: O(1)
- Output Space: O(z + 1)

Here `z` is the number of reported positions.

## 6. Knuth–Morris–Pratt (KMP)

**Approach:** Prefix Function and Linear String Matching

KMP improves string searching by reusing information from previously matched prefixes.

### Prefix Function

`pi[i]` stores the length of the longest proper prefix of the pattern prefix ending at `i` that is also its suffix.

### Search

When characters match, the current matched-prefix length increases.

When a mismatch occurs, the algorithm uses the prefix function to find the next possible matching prefix instead of restarting the search.

After a complete match, the algorithm also uses prefix fallback to preserve overlapping occurrences.

### Correctness

The prefix function identifies the longest prefix that could still match after a mismatch. All skipped alignments are incompatible with the characters already examined.

Therefore, no valid occurrence is missed.

### Complexity

- Time: O(n + m + z + 1)
- Auxiliary Space: O(m + 1)
- Output Space: O(z + 1)

## 7. Exact Rabin–Karp

**Approach:** Polynomial Rolling Hash

The algorithm compares the hash of the pattern with the hash of each text window.

### Hash Parameters

- Base: 31
- Modulus: 101
- Character values: ASCII codes

The leftmost character has the highest power.

### Rolling Hash

When moving the search window, the contribution of the outgoing character is removed, the hash is shifted by multiplying by the base, and the incoming character is added.

Negative remainders are normalized.

### Collision Verification

Two different strings may have the same hash.

Therefore, every equal-hash candidate must be verified by comparing the actual characters.

### Correctness

Equal strings have equal hashes. Consequently, no exact match is rejected by the hash filter.

Character verification prevents false matches caused by hash collisions.

### Complexity

- Expected performance depends on the number of hash collisions.
- Time: O(n + m + c × m), where `c` is the number of equal-hash candidate windows.
- Worst-case Time: O(n × m + n + m + 1)
- Auxiliary Space: O(m + 1), with a rolling-window implementation.
- Output Space: O(z + 1)

The algorithm does not guarantee linear worst-case time.

## 8. Suffix Array and Kasai LCP

**Approach:** Prefix Doubling and Kasai Algorithm

The suffix array stores the starting positions of all nonempty suffixes in lexicographical order.

### Suffix Array Construction

Prefix doubling repeatedly sorts suffixes using rank pairs.

At each iteration, the compared prefix length doubles.

The algorithm continues until all suffixes have distinct ranks.

### LCP Construction

Kasai's algorithm computes the longest common prefix between adjacent suffixes in suffix-array order.

It reuses the previous common-prefix length to avoid unnecessary comparisons.

`lcp[0] = 0`.

### Longest Repeated Substring

The maximum value in the LCP array determines the length of a longest repeated substring.

The corresponding substring can be reconstructed using its suffix-array position.

### Correctness

Prefix doubling eventually distinguishes suffixes according to their complete lexicographical order.

Any pair of suffixes sharing a longest prefix appears within a consecutive range of the sorted suffix array. Therefore, a maximum adjacent LCP identifies a longest repeated substring.

### Complexity

For comparison-based prefix doubling:

- Suffix Array Time: O(n log²(n + 1))
- Kasai Time: O(n)
- Auxiliary Space: O(n + 1)

With radix sorting of rank pairs, suffix-array construction can be reduced to O(n log(n + 1)).

## 9. Aho–Corasick

**Approach:** Trie and Failure Links

Aho–Corasick searches for multiple patterns simultaneously.

### Trie Construction

All dictionary patterns are inserted into a trie.

Each node represents a prefix of one or more patterns.

### Failure Links

Failure links identify the longest suffix of the current matched string that is also a trie prefix.

They allow matching to continue efficiently after a mismatch.

### Counting Matches

The algorithm processes each text independently and counts all pattern occurrences, including overlapping matches.

Patterns that are suffixes of other matched patterns must also be counted.

Duplicate dictionary entries retain separate output positions with equal counts.

### Correctness

Trie transitions recognize matching prefixes, while failure links preserve possible suffix matches after mismatches.

Aggregating visits through failure links counts patterns ending at each relevant state, including suffix-pattern occurrences.

### Complexity

Let `L` be total pattern length, `S` the number of trie states, `p` the number of patterns, and `T` the length of one text.

For the fixed 95-character ASCII alphabet:

- Construction Time: O(L + 95S + p)
- Construction Auxiliary Space: O(95S + p)
- Per-text Search Time with reverse-failure propagation: O(T + S + p)

The output matrix requires space proportional to the number of texts multiplied by the number of dictionary patterns.

## 10. Comparison of String Matching Algorithms

| Algorithm | Main Technique | Worst-Case Time |
|---|---|---|
| Naive | Direct comparison | O(nm + n + 1) |
| KMP | Prefix function | O(n + m + z + 1) |
| Exact Rabin–Karp | Rolling hash and verification | O(nm + n + m + 1) |

### Reproducible Comparison Procedure

The three algorithms should be executed on identical text-pattern pairs within their common input limits.

Recommended cases:

1. Empty pattern.
2. Pattern longer than text.
3. Multiple overlapping matches.
4. Repetitive text with frequent partial matches.
5. A Rabin–Karp hash collision.
6. Text containing spaces and mixed-case characters.

For every case, compare the complete returned `positions` arrays.

All three algorithms must produce identical positions because they implement the same exact-matching contract.

### Interpretation

Naive matching is simple but may repeat many character comparisons.

KMP avoids repeated work by using the prefix function and guarantees linear-time matching.

Rabin–Karp can reject many windows using hashes, but equal hashes require character verification. Its worst-case performance may be quadratic.

## 11. Testing and Validation

### Public Tests

The project was tested using:

```bash
python3 tools/run.py --setup
python3 tools/run.py --check
```

Observed results:

- Programming language: Java
- Setup: PASS
- Public tests: 44/44 PASS
- Repeated mixed-batch state-reset test: PASS

The public tests covered empty inputs, alternative optimal solutions, large numeric values, overlapping matches, hash collisions, and other boundary cases.

### Additional Student Tests

The following cases are recommended for independent testing:

| Algorithm | Additional Case |
|---|---|
| LCS | Identical strings and disjoint strings |
| Merge | Several equal-size blocks |
| Tree | Long chain and all-negative weights |
| Naive | Repeated overlapping occurrences |
| KMP | Repeated prefix-suffix patterns |
| Rabin–Karp | Different strings with equal hashes |
| Suffix | Text with all identical characters |
| Aho–Corasick | Duplicate patterns and multiple texts |

These cases should be added to `tests/student` and executed before submission.

### Limitations of Validation

Passing the public tests confirms correctness on the published test cases but does not guarantee correctness on every possible input or on hidden tests.

## 12. Sources and AI Assistance

### Sources

- Assignment 4 README and official specification.
- `docs/guide/CONTRACTS.md`.
- Course materials covering dynamic programming and string-processing algorithms.

## 13. Conclusion

This assignment covers eight algorithmic commands using dynamic programming, graph processing, and string-matching techniques.

The implementations passed all 44 published test cases and the repeated mixed-batch state-reset check.

The main concepts include optimal substructure, reconstruction of solutions, prefix functions, rolling hashes, suffix arrays, longest common prefixes, and finite-state pattern matching.

Further boundary testing and code review remain important before final submission.
