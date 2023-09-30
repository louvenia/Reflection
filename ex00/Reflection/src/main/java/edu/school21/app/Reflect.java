package edu.school21.app;

import edu.school21.exception.InvalidInputData;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class Reflect {
    private final Scanner in = new Scanner(System.in);
    private Set<Class<?>> classes;
    private Class<?> currentClass;
    private Field[] fields;
    private Method[] methods;
    private Object newObject;

    public void getClasses() {
        Reflections reflections = new Reflections("edu.school21.classes", new SubTypesScanner(false));
        classes = reflections.getSubTypesOf(Object.class);
        for(Class<?> cl : classes) {
            System.out.println("- " + cl.getSimpleName());
        }
    }

    public void getFieldMethod() {
        String[] nameClasses = in.nextLine().split("\\s+");
        try {
            if(nameClasses.length != 1) {
                throw new InvalidInputData("One class name is required");
            }
            for(Class<?> cl : classes) {
                if(cl.getSimpleName().equals(nameClasses[0])) {
                    currentClass = cl;
                }
            }
            if(currentClass == null) {
                throw new InvalidInputData("There is no class with that name");
            }
            fields = currentClass.getDeclaredFields();
            methods = currentClass.getDeclaredMethods();
            printInfClass();
        } catch (InvalidInputData e) {
            e.printStackTrace();
            System.out.print("Enter class name:\n-> ");
            getFieldMethod();
        }
    }

    public void createObject() {
        try {
            newObject = currentClass.newInstance();
            for(Field f : fields) {
                System.out.print(f.getName() + ":\n-> ");
                initField(f);
            }
            System.out.println("Object created: " + newObject);
        } catch (InstantiationException | IllegalAccessException | NumberFormatException | NullPointerException | InvalidInputData e) {
            e.printStackTrace();
            createObject();
        }
    }

    public void changeField() {
        boolean flagField = false;
        String[] selectedField = in.nextLine().split("\\s+");
        try {
            if(selectedField.length != 1) {
                throw new InvalidInputData("You can change one field");
            }
            for(Field f : fields) {
                if(f.getName().equals(selectedField[0])) {
                    System.out.print("Enter " + f.getType().getSimpleName() + " value:\n-> ");
                    flagField = true;
                    initField(f);
                }
            }
            if(!flagField) {
                throw new InvalidInputData("Field with this name not found");
            }
            System.out.println("Object updated: " + newObject);
        } catch(IllegalAccessException | NumberFormatException | NullPointerException | InvalidInputData e) {
            e.printStackTrace();
            System.out.print("Enter name of the field for changing:\n-> ");
            changeField();
        }
    }

    public void callMethod() {
        boolean flagMethod = false;
        String[] selectedMethod = in.nextLine().split("\\s+");
        try {
            if(selectedMethod.length != 1) {
                throw new InvalidInputData("You can call one method");
            }
            for(Method m : methods) {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getName());
                if(m.getParameters().length > 0) {
                    sb.append('(');
                    int countParameters = m.getParameterCount();
                    for(Parameter p : m.getParameters()) {
                        --countParameters;
                        sb.append(p.getType().getSimpleName());
                        if(countParameters > 0) {
                            sb.append(',');
                        }
                    }
                    sb.append(')');
                }
                String nameMethod = sb.toString();
                if(nameMethod.equals(selectedMethod[0])) {
                    flagMethod = true;
                    readParameter(m);
                }
            }
            if(!flagMethod) {
                throw new InvalidInputData("Method with this name was not found");
            }
        } catch(NumberFormatException | NullPointerException | InvocationTargetException | IllegalAccessException | InvalidInputData e) {
            e.printStackTrace();
            System.out.print("Enter name of the method for call:\n-> ");
            callMethod();
        }
    }

    private void printInfClass() {
        System.out.println("------------------------------------------");
        System.out.println("fields:");
        for(Field f : fields) {
            System.out.println("\t" + f.getType().getSimpleName() + " " + f.getName());
        }
        System.out.println("methods:");
        for(Method m : methods) {
            if (!validateOverride(m)) {
                System.out.print("\t" + m.getReturnType().getSimpleName() + " " + m.getName() + "(");
                int countParameters = m.getParameterCount();
                for(Parameter p : m.getParameters()) {
                    --countParameters;
                    System.out.print(p.getType().getSimpleName());
                    if(countParameters > 0) {
                        System.out.print(",");
                    }
                }
                System.out.println(")");
            }
        }
    }

    private boolean validateOverride(Method method) {
        boolean status = true;
        Class<?> methodClass = method.getDeclaringClass();
        Class<?> superClass = methodClass.getSuperclass();
        if(superClass != null) {
            try {
                Method superMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
                if (!Objects.equals(method.getName(), superMethod.getName()) ||
                        !Objects.equals(method.getReturnType(), superMethod.getReturnType()) ||
                        methodClass == superMethod.getDeclaringClass()) {
                    status = false;
                }
            } catch (NoSuchMethodException e) {
                status = false;
            }
        } else {
            status = false;
        }
        return status;
    }

    private void initField(Field f) throws IllegalAccessException, NumberFormatException, NullPointerException, InvalidInputData {
        f.setAccessible(true);
        String currentType = f.getType().getSimpleName();
        String[] valueField = in.nextLine().split("\\s+");
        if(valueField.length != 1) {
            throw new InvalidInputData("Incorrect number of values for one field");
        }
        if(currentType.equals("Integer") || currentType.equals("int")) {
            f.setInt(newObject, Integer.parseInt(valueField[0]));
        } else if(currentType.equals("Double") || currentType.equals("double")) {
            f.setDouble(newObject, Double.parseDouble(valueField[0]));
        } else if(currentType.equals("Boolean") || currentType.equals("boolean")) {
            f.setBoolean(newObject, Boolean.parseBoolean(valueField[0]));
        } else if(currentType.equals("Long") || currentType.equals("long")) {
            f.setLong(newObject, Long.parseLong(valueField[0]));
        } else {
            f.set(newObject, valueField[0]);
        }
    }

    private void readParameter(Method method) throws NumberFormatException, NullPointerException, InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        ArrayList<Object> objects = new ArrayList<>();
        for (Class<?> type : method.getParameterTypes()) {
            printEnterParam(type.getSimpleName());
            String[] valueParameter = in.nextLine().split("\\s+");
            if(valueParameter.length != 1) {
                throw new InvalidInputData("Incorrect number of values for one parameter");
            }
            if(type.getSimpleName().equals("Integer") || type.getSimpleName().equals("int")) {
                objects.add(Integer.parseInt(valueParameter[0]));
            } else if(type.getSimpleName().equals("Double") || type.getSimpleName().equals("double")) {
                objects.add(Double.parseDouble(valueParameter[0]));
            } else if(type.getSimpleName().equals("Boolean") || type.getSimpleName().equals("boolean")) {
                objects.add(Boolean.parseBoolean(valueParameter[0]));
            } else if(type.getSimpleName().equals("Long") || type.getSimpleName().equals("long")) {
                objects.add(Long.parseLong(valueParameter[0]));
            } else {
                objects.add(valueParameter[0]);
            }
        }
        Object[] arguments = objects.toArray(new Object[0]);
        if(method.getReturnType().getSimpleName().equals("void")) {
            method.invoke(newObject, arguments);
        } else {
            System.out.println("Method returned:");
            System.out.println(method.invoke(newObject, arguments));
        }
    }

    private void printEnterParam(String type) {
        System.out.print("Enter " + type + " value:\n-> ");
    }
}
