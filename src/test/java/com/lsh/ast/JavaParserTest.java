package com.lsh.ast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.FileNotFoundException;

public class JavaParserTest {

    public static void parserJavaCode(CompilationUnit cu){
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            System.out.println("Class: " + clazz.getName());

            clazz.getFields().forEach(field -> {
                field.getVariables().forEach(var -> {
                    System.out.println("  Field: " + var.getType() + " " + var.getName());
                });
            });

            clazz.getMethods().forEach(method -> {
                System.out.println("  Method: " + method.getName());

                method.findAll(MethodCallExpr.class).forEach(call -> {
                    System.out.println("    Calls: " + call);
                });
            });
        });

    }

    public static void parseJavaFile()throws FileNotFoundException{
        CompilationUnit cu = StaticJavaParser.parse(new File("Hello.java"));
        parserJavaCode(cu);
    }

    public static void parseCodeString(){
//        String code = """
//            public class Hello {
//                public void sayHi() {
//                    System.out.println("Hi");
//                }
//            }
//        """;

        String code = """
                public class Hello {public void sayHi() {System.out.println("Hi");}}
                """;

        CompilationUnit cu = StaticJavaParser.parse(code);
        parserJavaCode(cu);
    }

    public static void main(String[] args) throws Exception {
        parseJavaFile();
        System.out.println("= = = = = = = = = = =");
        parseCodeString();
        /**
         * Class: Hello
         *   Field: String name
         *   Method: hello
         *   Method: main
         *     Calls: System.out.println(hello(name))
         *     Calls: hello(name)
         * = = = = = = = = = = =
         * Class: Hello
         *   Method: sayHi
         *     Calls: System.out.println("Hi")
         */

    }
}
