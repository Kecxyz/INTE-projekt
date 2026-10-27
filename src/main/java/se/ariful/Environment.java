package se.ariful;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Runtime Error: Undefined variable '" + name + "'");
        }
        return variables.get(name);
    }

    public void printOutput(Object value) {
        System.out.println(value);
    }

    public String input() {
        return scanner.nextLine();
    }
}