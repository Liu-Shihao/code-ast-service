package com.lsh.ast.service;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class JavaParserNeo4jIntegration {


    private static void processJavaFile(Path path, Session session) {
        try {
             // 配置 JavaParser 使用 Java 17 语言级别
            ParserConfiguration config = new ParserConfiguration();
            config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
            StaticJavaParser.setConfiguration(config);
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(path.toFile()));

            for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String className = clazz.getNameAsString();
                session.run("MERGE (c:Class {name: $name})", parameters("name", className));

                clazz.getFields().forEach(field -> {
                    for (VariableDeclarator var : field.getVariables()) {
                        session.run("""
                            MERGE (f:Field {name: $name, type: $type})
                            WITH f
                            MATCH (c:Class {name: $className})
                            MERGE (c)-[:CLASS_HAS_FIELD]->(f)
                        """, parameters(
                                "name", var.getNameAsString(),
                                "type", var.getType().asString(),
                                "className", className));
                    }
                });

                List<MethodDeclaration> methods = clazz.getMethods();
                for (MethodDeclaration method : methods) {
                    String methodName = method.getNameAsString();
                    session.run("""
                        MERGE (m:Method {name: $name, class: $className})
                        WITH m
                        MATCH (c:Class {name: $className})
                        MERGE (c)-[:CLASS_HAS_METHOD]->(m)
                    """, parameters("name", methodName, "className", className));
                }

                for (MethodDeclaration method : methods) {
                    String caller = method.getNameAsString();
                    method.findAll(MethodCallExpr.class).forEach(call -> {
                        String callee = call.getNameAsString();
                        session.run("""
                            MATCH (caller:Method {name: $caller, class: $className})
                            MATCH (callee:Method {name: $callee})
                            MERGE (caller)-[:METHOD_CALLS_METHOD]->(callee)
                        """, parameters("caller", caller, "callee", callee, "className", className));
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse " + path + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
        String sourceDir = "src/main/java/com/lsh/ast"; // 存放 .java 文件的目录

        Driver driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "neo4j123456"));

        try (Session session = driver.session()) {
            Files.walk(Paths.get(sourceDir))
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> processJavaFile(path, session));
        }

        driver.close();
    }
}
