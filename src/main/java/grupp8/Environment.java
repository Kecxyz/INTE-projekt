package grupp8;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Environment parent;
    private final Scanner scanner;

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
        this.scanner = parent == null ? new Scanner(System.in) : parent.scanner;
    }

    public void defineVariable(String name, Object value) {
        variables.put(name, value);
    }

    public void setVariable(String name, Object value) {
        if (!variables.containsKey(name) && parent != null && parent.hasVariable(name)) {
            parent.setVariable(name, value);
        } else {
            variables.put(name, value);
        }
    }

    private boolean hasVariable(String name) {
        return variables.containsKey(name) || (parent != null && parent.hasVariable(name));
    }

    public Object getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.getVariable(name);
        }
        throw new RuntimeException("Körningsfel: Variabeln '" + name + "' är inte definierad");
    }

    public void printOutput(Object value) {
        System.out.println(value);
    }

    public String input() {
        return scanner.nextLine();
    }
}
