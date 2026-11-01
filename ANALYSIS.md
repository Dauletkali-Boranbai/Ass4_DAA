# Assignment 4 — Dynamic Programming and String Processing Analysis

## 1. Environment and Commands

**Programming language:** Java

**Development environment:** IntelliJ IDEA

**Operating system:** macOS

**Build and testing command:**

```bash
python3 tools/run.py --check
```

**Public test results:**
- 44 out of 44 public tests passed.
- The state-reset test passed.
- All eight algorithms passed their public correctness tests.

The program uses a UTF-8 JSON Lines interface. Each input line contains a JSON request, and the program produces a corresponding JSON response.

The implementation was tested using the project's provided test runner.

**Java version, CPU architecture, and RAM:** To be recorded from the development machine using `java -version`, `uname -m`, and `sysctl -n hw.memsize`.

---

## 2. Algorithm Explanations

### 2.1 Longest Common Subsequence (LCS)

**Purpose**

The Longest Common Subsequence algorithm finds the longest sequence of characters appearing in the same relative order in two strings. The characters do not need to be adjacent.

**Dynamic programming state**

Let `dp[i][j]` represent the length of the longest common subsequence between the first `i` characters of string `a` and the first `j` characters of string `b`.

**Base cases**

If either string is empty:

`dp[i][0] = 0` and `dp[0][j] = 0`.

**Transition**

If the current characters match:

`dp[i][j] = dp[i-1][j-1] + 1`

Otherwise:

`dp[i][j] = max(dp[i-1][j], dp[i][j-1])`

**Processing order**

The table is filled from smaller prefixes to larger prefixes. Each state depends only on previously computed states.

**Correctness**

Every common subsequence either includes the matching final characters or excludes at least one of the final characters. The recurrence considers these possibilities, so the table computes the optimal length.

**Reconstruction**

Starting from the final table entry, the algorithm traces backward through the DP table to reconstruct a valid longest common subsequence. When several optimal subsequences exist, reconstruction must use a consistent tie-breaking rule.

**Complexity**

- Time: O(n × m)
- Space: O(n × m)

Here, `n` and `m` are the lengths of the input strings.

**Boundary test**

Input: `a = "abc"`, `b = "xyz"`

Expected output: `{"length":0,"sequence":""}`

This test detects incorrect DP initialization and reconstruction when no common characters exist.

### 2.2 Optimal Adjacent Merge

**Purpose**

The adjacent merge problem finds the minimum cost of merging consecutive groups, where only adjacent groups can be combined.

**Dynamic programming state**

Let `dp[i][j]` represent the minimum cost of merging all groups in the interval from index `i` to index `j`.

**Base case**

A single group requires no merging:

`dp[i][i] = 0`

**Transition**

For every possible split position `k`:

`dp[i][j] = min(dp[i][k] + dp[k+1][j] + sum(i,j))`

Here, `sum(i,j)` is the total size of groups in the interval.

Prefix sums can calculate interval sums efficiently.

**Processing order**

Intervals are processed by increasing length, ensuring that shorter intervals are solved before longer ones.

**Correctness**

Every valid sequence of adjacent merges has a final merge dividing the interval into two adjacent subintervals. The recurrence examines all such divisions and selects the minimum total cost.

**Reconstruction**

The selected split position for each interval can be stored and used recursively to reconstruct the merge plan. Equal-cost splits should be handled consistently.

**Complexity**

- Time: O(n³)
- Space: O(n²)

**Boundary test**

An empty list or a list containing one group should have total merge cost zero.

This detects incorrect handling of intervals that require no merge operations.

### 2.3 Maximum Weight Independent Set on a Tree

**Purpose**

The algorithm selects vertices of a tree with maximum total weight such that no two selected vertices are directly connected.

**Dynamic programming states**

For each vertex `v`:

- `dp[v][0]`: maximum weight when vertex `v` is not selected.
- `dp[v][1]`: maximum weight when vertex `v` is selected.

**Transitions**

When selecting `v`, none of its children can be selected:

`dp[v][1] = weight[v] + sum(dp[child][0])`

When not selecting `v`, each child may be selected or excluded:

`dp[v][0] = sum(max(dp[child][0], dp[child][1]))`

**Processing order**

The tree is processed in postorder so that every child is evaluated before its parent. An iterative traversal can avoid recursion-depth problems.

**Correctness**

Every vertex has two possibilities: selected or excluded. The recurrence considers both while respecting the restriction that adjacent vertices cannot both be selected.

Because a tree has no cycles, child subproblems are independent once the parent's selection state is fixed.

**Reconstruction**

The chosen states can be traced from the root to reconstruct the selected vertices. A deterministic tie-breaking rule can be used when both states have equal weight.

**Complexity**

- Time: O(n)
- Space: O(n)

**Boundary test**

A tree containing only negative-weight vertices should allow an empty selection with weight zero.

This detects algorithms that incorrectly force the selection of a vertex.

### 2.4 Naive String Matching

**Purpose**

The naive algorithm finds every occurrence of a pattern inside a text.

**Invariant**

For each candidate starting position, the algorithm compares the pattern with the corresponding substring of the text.

**Processing**

The algorithm checks possible starting positions from left to right and compares characters until a mismatch occurs or the complete pattern matches.

**Correctness**

Every valid starting position is examined. A position is reported only when all characters match, so the algorithm finds all exact occurrences.

**Complexity**

Let `n` be the text length and `m` the pattern length.

- Worst-case time: O(n × m)
- Auxiliary space: O(1), excluding the output list.

**Boundary test**

Text: `aaaaa`

Pattern: `aaa`

Expected positions: `[0,1,2]`

This detects implementations that skip overlapping matches.

### 2.5 Knuth–Morris–Pratt (KMP)

**Purpose**

KMP finds pattern occurrences efficiently by avoiding unnecessary repeated comparisons.

**Prefix function**

The prefix table, often called LPS, stores the length of the longest proper prefix that is also a suffix for each pattern prefix.

**Invariant**

During matching, the algorithm maintains the number of pattern characters currently matching the end of the processed text.

**Processing**

When characters match, the matched prefix length increases.

When a mismatch occurs, the prefix table determines the next possible prefix length without moving backward through the text.

**Correctness**

The prefix table preserves all possible partial matches that could still lead to a complete occurrence. Therefore, the algorithm does not skip valid matches.

**Complexity**

- Preprocessing: O(m)
- Searching: O(n)
- Total time: O(n + m)
- Auxiliary space: O(m), excluding output.

**Boundary test**

Text: `abababa`

Pattern: `aba`

Expected positions: `[0,2,4]`

This detects incorrect prefix-table handling after a successful match, which could cause overlapping occurrences to be missed.

### 2.6 Rabin–Karp

**Purpose**

Rabin–Karp uses rolling hashes to locate possible occurrences of a pattern.

**Hash configuration**

The assignment uses:

- Base: 31
- Modulus: 101

**Invariant**

The rolling hash represents the current text window of length `m`.

**Processing**

The algorithm computes the hash of the pattern and the initial text window.

For each subsequent window, the rolling hash is updated without recomputing the complete hash from the beginning.

When a text-window hash matches the pattern hash, the actual characters are compared to verify the candidate.

**Correctness**

Equal strings have equal hashes under the same hash function. However, different strings may also have equal hashes.

Character-by-character verification ensures that a hash collision cannot produce a false match.

**Complexity**

- Hash preprocessing: O(m)
- Rolling-hash updates: O(n)
- Candidate verification: O(m) for each hash match.
- Total time: O(n + m + c × m), where `c` is the number of candidate windows requiring verification.
- Worst-case time: O(n × m)
- Auxiliary space: O(1), excluding output.

**Boundary test**

Text: `abcdef`

Pattern: `xyz`

Expected positions: `[]`

This detects false matches when the pattern is absent.

**Hash collision consideration**

The small modulus 101 makes collisions more likely than with larger hash moduli. Therefore, character verification is essential. The collision behavior of this assignment's implementation should not be generalized to every production hashing system.

### 2.7 Suffix Array and Kasai Algorithm

**Purpose**

A suffix array stores the starting positions of all suffixes of a string in lexicographic order.

The LCP array records the longest common prefix length between neighboring suffixes in suffix-array order.

**Suffix array construction**

A common approach is prefix doubling.

Initially, each suffix is ranked according to its first character.

During each iteration, suffixes are sorted according to pairs of ranks representing prefixes of increasing length.

The compared prefix length doubles after each iteration.

**Invariant**

After an iteration, the ranks represent the relative order of suffixes based on the prefix length processed so far.

**Correctness**

When the compared prefix length becomes sufficiently large, the ranks distinguish suffixes according to their complete lexicographic order.

**Kasai algorithm**

Kasai's algorithm calculates LCP values using the suffix array and an inverse-rank array.

The key observation is that the known common-prefix length can usually be decreased by at most one when moving to the next suffix starting position.

This avoids repeatedly comparing prefixes from the beginning.

**Complexity**

For prefix doubling with comparison-based sorting:

- Suffix-array construction: O(n log² n)
- Kasai LCP construction: O(n)
- Auxiliary space: O(n), excluding sorting overhead.

If integer-key or counting-based sorting is used in every doubling round, suffix-array construction can achieve O(n log n).

The precise construction bound depends on the actual sorting implementation.

**Boundary test**

An empty input string should produce empty suffix and LCP arrays.

This detects incorrect handling of zero-length inputs.

### 2.8 Aho–Corasick

**Purpose**

Aho–Corasick finds occurrences of multiple patterns in a text using a trie and failure links.

**Data structures**

- Trie nodes represent pattern prefixes.
- Trie edges represent character transitions.
- Failure links connect nodes to the longest available suffix state.
- Output information identifies patterns ending at each state.

**Construction**

Patterns are inserted into the trie.

Failure links are constructed using breadth-first search.

During text processing, the automaton follows transitions and failure links to maintain the longest relevant matched suffix.

**Correctness**

Every automaton state represents a suffix of the processed text that is also a prefix of at least one pattern.

Failure links preserve shorter valid suffixes when a transition fails.

Terminal and suffix-output information allow the algorithm to recognize patterns ending at each position.

**Duplicates and suffix patterns**

Duplicate dictionary entries must be counted according to the assignment's required semantics.

Patterns that are suffixes of other patterns must also be recognized through failure-link output propagation.

**State reset**

Independent requests must not share stale matching counts or automaton state.

**Complexity**

Let `L` be the total length of all patterns, `n` the text length, and `z` the total number of reported matches.

With suitable transition storage:

- Trie construction: O(L)
- Failure-link construction: depends on alphabet representation and transition processing.
- Matching: O(n + z) with appropriately precomputed transitions and output handling.
- Space: O(L) for a sparse trie, plus alphabet-dependent transition and output storage.

**Boundary test**

An empty dictionary should produce no pattern matches.

This detects incorrect initialization and empty-input handling.

---

## 3. Own Tests

Three additional student tests were created to verify correctness and important boundary cases.

The request fixtures and actual results are stored in the `tests/student` directory.

| Command | Input or fixture path | Expected result | Actual result / log | What it checks |
|---|---|---|---|---|
| LCS | `tests/student/lcs_no_common.json` | `{"length":0,"sequence":""}` | PASS — `{"length":0,"sequence":""}` | Detects incorrect DP initialization or reconstruction when the strings have no common characters. |
| KMP | `tests/student/kmp_overlap.json` | `{"positions":[0,2,4]}` | PASS — `{"positions":[0,2,4]}` | Detects missed overlapping matches caused by incorrect prefix-table handling. |
| Rabin–Karp | `tests/student/rk_no_match.json` | `{"positions":[]}` | PASS — `{"positions":[]}` | Detects false matches when the pattern is absent from the text. |

### Public test verification

The following command was executed:

```bash
python3 tools/run.py --check
```

**Results:**
- All 44 public tests passed.
- The state-reset test passed.
- No public test failures were reported.

The student tests were also executed individually through the Java JSON Lines interface. All three actual outputs matched their expected outputs.

---

## 4. Search Comparison

### Objective

Compare the performance of Naive String Matching, KMP, and Rabin–Karp using identical text and pattern inputs.

### Experimental design

The comparison should include both random and repetitive input strings.

For each case:

1. Generate a text and a pattern using a recorded random seed or deterministic generator.
2. Run all three algorithms using the same input.
3. Verify that their returned match positions are identical.
4. Measure execution time at least three times for each algorithm.
5. Calculate the median execution time.
6. Exclude compilation time from measurements.
7. Use a consistent warm-up and timing policy.

The assignment requires three increasing input sizes with `n, m ≤ 3000`.

### Measurement results

The following table must be filled using actual measured execution times from the development machine.

| Case | n | m | Seed/generator | Algorithm | Run 1 | Run 2 | Run 3 | Median | Correct outputs checked |
|---|---:|---:|---|---|---:|---:|---:|---:|---|
| Random 1 | 300 | 10 | To be recorded | Naive | — | — | — | — | Pending |
| Random 1 | 300 | 10 | To be recorded | KMP | — | — | — | — | Pending |
| Random 1 | 300 | 10 | To be recorded | Rabin–Karp | — | — | — | — | Pending |
| Random 2 | 1000 | 30 | To be recorded | Naive | — | — | — | — | Pending |
| Random 2 | 1000 | 30 | To be recorded | KMP | — | — | — | — | Pending |
| Random 2 | 1000 | 30 | To be recorded | Rabin–Karp | — | — | — | — | Pending |
| Random 3 | 3000 | 60 | To be recorded | Naive | — | — | — | — | Pending |
| Random 3 | 3000 | 60 | To be recorded | KMP | — | — | — | — | Pending |
| Random 3 | 3000 | 60 | To be recorded | Rabin–Karp | — | — | — | — | Pending |

Equivalent repetitive-input cases must also be measured and recorded.

### Theoretical comparison

**Naive String Matching**

The naive algorithm has worst-case time complexity O(n × m). It can perform well when mismatches occur quickly, but repetitive inputs can cause many repeated character comparisons.

**KMP**

KMP has time complexity O(n + m). Its prefix table prevents repeated comparisons and provides a linear worst-case bound.

**Rabin–Karp**

Rabin–Karp uses rolling hashes to process candidate windows efficiently. However, hash collisions require additional character comparisons.

Because this assignment uses modulus 101, collisions may occur relatively frequently. Consequently, its observed performance may differ from implementations using larger moduli or other hashing strategies.

### Interpretation

Measured execution times depend on input characteristics, Java runtime behavior, JIT compilation, measurement overhead, and hardware.

Timing measurements should not be treated as a proof of asymptotic complexity.

Actual conclusions must be added after the benchmark has been executed.

---

## 5. Sources and AI Assistance

### Sources

- Assignment 4 README and provided specifications.
- Assignment 4 public test fixtures.
- Course materials covering dynamic programming and string-processing algorithms.
- Java documentation and development tools.

### Verification

The Java implementation was checked using the assignment's official test runner.

All 44 public tests passed, and the state-reset test passed.

Three additional student tests were executed and their actual results were compared with expected outputs.

The algorithm explanations and complexity discussions should be checked against the final submitted implementation. Performance measurements must be recorded from actual benchmark executions rather than estimated or generated values.
