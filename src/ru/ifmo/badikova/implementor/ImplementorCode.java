package ru.ifmo.badikova.implementor;

import javax.swing.plaf.synth.SynthRootPaneUI;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ImplementorCode {
    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final String PACKAGE = "package";
    private static final String SEMICOLON = ";";
    private static final String IMPL_SUFFIX = "Impl";
    private static final String OPEN_BLOCK = "{";
    private static final String OPEN_BRACE = "(";
    private static final String CLOSE_BLOCK = "}";
    private static final String CLOSE_BRACE = ")";
    private static final String TAB = "\t";
    private static final String RETURN = "return";
    private static final String EOL = System.lineSeparator();

    private static class Namer {
        int index;
        final String NAME_PREF = "arg";
        Namer() {
            index = 0;
        }

        private String getName() {
            return NAME_PREF + (index++);
        }
    }

    private static String combineLine(String separator, String... strings) {
        return Arrays.stream(strings).filter(s -> !"".equals(s)).collect(Collectors.joining(separator));
    }

    private static String generatePackageLine(Class<?> token) {
        return combineLine(EMPTY, PACKAGE, SPACE, token.getPackageName(), SEMICOLON);
    }

    private static String getModifiers(Class<?> token) {
        return combineLine(SPACE, "public class", token.getSimpleName() + IMPL_SUFFIX, "implements", token.getCanonicalName());
    }

    private static String generateOpeningLine(Class<?> token) {
        return combineLine(SPACE, getModifiers(token), OPEN_BLOCK);
    }

    private static String getDefaultValue(Method method) {
        Class<?> type = method.getReturnType();
        if (!type.isPrimitive()) {
            return "null";
        } else if (type.equals(void.class)) {
            return "";
        } else if (type.equals(boolean.class)) {
            return "true";
        } else {
            return "0";
        }
    }

    private static String generateMethodBody(Method method) {
        return combineLine(SPACE, RETURN, getDefaultValue(method) + SEMICOLON, CLOSE_BLOCK, EOL);
    }

    private static String getArguments(Method method) {
        Namer namer = new Namer();
        return Arrays.stream(method.getParameterTypes())
                .map(c -> combineLine(SPACE, c.getCanonicalName(), namer.getName()))
                .collect(Collectors.joining(", "));
    }

    private static String getExceptions(Method method) {
        StringBuilder builder = new StringBuilder();
        Class<?>[] exceptions = method.getExceptionTypes();
        if (exceptions.length > 0) {
            builder.append(" throws ");
        }
        builder.append(Arrays.stream(exceptions).map(Class::getCanonicalName)
                .collect(Collectors.joining(", ")));
        return builder.toString();
     }

    private static String generateMethodHeader(Method method) {
        return combineLine(SPACE , "public", method.getReturnType().getCanonicalName(),
                method.getName(), OPEN_BRACE, getArguments(method), CLOSE_BRACE, getExceptions(method), OPEN_BLOCK);
    }

    private static String generateMethods(Class<?> token) {
        StringBuilder methods = new StringBuilder();
        for (Method method : token.getMethods()) {
            methods.append(combineLine(EOL, generateMethodHeader(method), generateMethodBody(method)));
        }
        return methods.toString();
    }

    public static String generateCode(Class<?> token) {
        return combineLine(EOL, generatePackageLine(token), generateOpeningLine(token), generateMethods(token), CLOSE_BLOCK);
    }

}
