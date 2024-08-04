# Treesitter in Java for Java using Tree Sitter NG

This version is adapted from the ng example to parse the simplest Java file with a single empty class A.
It then listst all the nodes of the syntax tree.

## Links
### Tree-Sitter
https://tree-sitter.github.io/tree-sitter/

### Tree Sitter NG
https://github.com/bonede/tree-sitter-ng?tab=readme-ov-file

## Getting Started
### Gradle Dependencies
```kts
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.bonede:tree-sitter:0.22.6")
    implementation("io.github.bonede:tree-sitter-java:0.21.0a")
}
```

## Dependency Graph Implementation
How is the graph to be used, once built after parsing?

There are at least two different uses cases: with or without a coupling constraint.
Without a coupling coonstraint the graph displays all the dependencies.
With a coupling constraint, the forbidden dependencies should be highlighted.

### Types of nodes
Ideally the types of nodes would not be hard-coded (e.g. a class per type) but a mere parameter of the graph representation.






