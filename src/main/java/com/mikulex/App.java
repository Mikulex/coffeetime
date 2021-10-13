package com.mikulex;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        switch (args[0]) {
            case "generate":
                App.generate(args);
                break;
            case "build":
                SiteGenerator generator = new SiteGenerator();
                generator.build();
                System.out.println("finished");
                break;
            default:
                System.err.println("Command not recognized");
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
                    // TODO
                    break;
                case "post":
                default:
                    System.err.println("Command not recognized");
                    break;
            }
        }
    }
}
