package se.ariful;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Undefined variable: '" + name + "'");
        }
        return variables.get(name);
    }

    public void printOutput(Object value) {
        System.out.println(value);
    }
}