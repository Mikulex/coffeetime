package com.mikulex;

public class App {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Arguments expected");
        } else {
            switch (args[0]) {
                case "generate":
                    App.generate(args);
                    break;
                case "build":
                    App.build();
                    break;
                default:
                    System.err.println("Command not recognized: " + args[0]);
            }
        }
    }

    public static void generate(String[] args) {
        if (args.length < 3) {
            System.err.println("Expected more arguments");
        } else {
            switch (args[1]) {
                case "site":
                    ProjectGenerator.generateSite(args[2]);
                    break;
                case "page":
                    ProjectGenerator.generateFile(args[2], "_pages");
                    break;
                case "post":
                    ProjectGenerator.generateFile(args[2], "_posts");
                    break;
                default:
                    System.err.println("Command not recognized: " + args[2]);
                    break;
            }
        }
    }

    public static void build() {
        try {
            SiteGenerator generator = new SiteGenerator();
            generator.build();
            System.out.println("Finished building site");

        } catch (Exception e) {
            System.err.println("Failed while building site");
            System.err.println(e);
        }
    }
}
