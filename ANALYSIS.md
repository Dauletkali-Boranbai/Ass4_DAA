# Assignment 4 — analysis (complete in English)

## Environment and commands

Selected language/runtime version; OS/CPU/RAM; build/run/test commands. Record the final commit URL in Moodle. Report actual measurements only.

## Algorithm explanations

For each of lcs, merge, tree, naive, kmp, rk, suffix and aho:

- State or invariant; meaning of each index.
- Transition/update and base cases.
- Processing order and why dependencies are ready.
- Correctness argument.
- Reconstruction and tie handling, where applicable.
- Time and memory; include input/output size and actual sorting/container choices.
- One boundary/counterexample test and what defect it detects.

For suffix discuss doubling and Kasai separately. For rk use base 31/mod 101 and character verification; state the candidate-sensitive bound. For aho explain suffix-pattern counts, duplicates and reset.

## Own tests

|Command|Input or fixture path|Expected result|Actual result / log|What it checks|
|---|---|---|---|---|
| LCS | `tests/student/lcs_no_common.json` | `{"length":0,"sequence":""}` | PASS — `{"length":0,"sequence":""}` | Detects incorrect DP initialization or reconstruction when the strings have no common characters. |
| KMP | `tests/student/kmp_overlap.json` | `{"positions":[0,2,4]}` | PASS — `{"positions":[0,2,4]}` | Detects missed overlapping matches caused by incorrect prefix-table handling. |
| Rabin–Karp | `tests/student/rk_no_match.json` | `{"positions":[]}` | PASS — `{"positions":[]}` | Detects false matches when the pattern is absent from the text. |

## Search comparison

Use the same inputs for naive/KMP/RK. At three increasing sizes in the common domain (n, m≤3000), measure random and repetitive cases separately. Record generator/seed, n, m, pattern distribution and correctness verification first. At least three measured runs per algorithm/size/case; report raw times, unit and median. Exclude compilation; give the identical timing scope, warm-up/JIT policy and commands.

|Case|n|m|Seed/generator|Algorithm|Run 1|Run 2|Run 3|Median|Correct outputs checked|
|---|---:|---:|---|---|---:|---:|---:|---:|---|

Explain observations without treating measured times as a proof of asymptotic complexity. Discuss how the deliberately small modulus affects RK; do not generalize it to every production hash implementation. Add the known collision case as a correctness test.

## Sources and AI assistance

List tools/sources, contribution and your own verification. State if none were used.
