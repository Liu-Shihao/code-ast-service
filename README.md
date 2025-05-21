# Code AST Service
## 介绍
Code AST Service 是一个基于 AST（抽象语法树）的代码分析服务。它提供了一系列的接口，用于解析、分析和处理代码。
集成Neo4J图数据库，存储class，method，attribute以及调用关系，提供代码分析的能力。
结合LLM大模型，分析项目代码和功能


# AST
AST（Abstract Syntax Tree，抽象语法树）是源代码的一种树状结构表示。它将源代码的语法结构抽象成节点，每个节点表示一个语法结构（如类、方法、变量、表达式等）。AST 是编译器、代码分析、静态检查、代码生成等工具的核心组件。

# Neo4J
Neo4J是一个高性能的图数据库，它使用图数据模型来存储和查询数据。Neo4J的图数据模型由节点和关系组成，节点表示实体，关系表示实体之间的关系。Neo4J支持多种编程语言，包括Java、Python、JavaScript等。Neo4J的优势在于它可以处理大量的数据，并且可以快速地查询数据。

```shell
cd neo4j-community-5.19.0
/bin/neo4j console

```
Open http://localhost:7474 in your web browser.

默认密码neo4j