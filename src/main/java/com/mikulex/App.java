package com.mikulex;

public class App {
    public static void main(String[] args) {
        ProjectGenerator generator = new ProjectGenerator();
        if (args.length == 0) {
            System.err.println("Arguments expected");
        } else {
            switch (args[0]) {
            case "generate":
                App.generate(args, generator);
                break;
            case "build":
                App.build();
                break;
            default:
                System.err.println("Command not recognized: " + args[0]);
            }
        }
    }

    public static void generate(String[] args, ProjectGenerator generator) {
        if (args.length < 3) {
            System.err.println("Expected more arguments");
        } else {
            switch (args[1]) {
            case "site":
                generator.generateSite(args[2]);
                break;
            case "page":
                generator.generateFile(args[2], "_pages");
                break;
            case "post":
                generator.generateFile(args[2], "_posts");
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
