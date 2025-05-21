package com.lsh.ast;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

public class Neo4jDBTest {

    public static void main(String... args) {

        // URI examples: "neo4j://localhost", "neo4j+s://xxx.databases.neo4j.io"
        final String dbUri = "neo4j://localhost";
        final String dbUser = "neo4j";
        final String dbPassword = "neo4j123456";

        try (var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            System.out.println("Connection established.");
        }
    }
}
