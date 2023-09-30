package edu.school21.app;

public class Program {
    public static void main(String[] args) {
        Reflect r = new Reflect();
        System.out.println("Classes: ");
        r.getClasses();
        System.out.println("------------------------------------------");
        System.out.print("Enter class name:\n-> ");
        r.getFieldMethod();
        System.out.println("------------------------------------------");
        System.out.println("Let’s create an object.");
        r.createObject();
        System.out.println("------------------------------------------");
        System.out.print("Enter name of the field for changing:\n-> ");
        r.changeField();
        System.out.println("------------------------------------------");
        System.out.print("Enter name of the method for call:\n-> ");
        r.callMethod();
        System.out.println("------------------------------------------");
    }
}

