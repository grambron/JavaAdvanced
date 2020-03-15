package ru.ifmo.badikova.implementor;

import javax.swing.plaf.synth.SynthRootPaneUI;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *Class providing tools for implementation for {@link Implementor}.
 * @author Anastasiia Badikova
 * @version 1.0
 */

public class ImplementorCode {
    /**
     * Tokens to construct implementation source code.
     */
    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final String PACKAGE = "package";
    private static final String SEMICOLON = ";";
    private static final String IMPL_SUFFIX = "Impl";
    private static final String OPEN_BLOCK = "{";
    private static final String OPEN_BRACE = "(";
    private static final String CLOSE_BLOCK = "}";
    private static final String CLOSE_BRACE = ")";
    private static final String RETURN = "return";
    private static final String EOL = System.lineSeparator();

    /**
     * Class to generate unique arguments names.
     */
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

    /**
     *Returns a {@link String}, which consists of array of given {@link String} concatenated by given separator.
     * @param separator which is put between elements
     * @param strings the objects to separate
     * @return Collected and separated {@link String}
     */
    private static String combineLine(String separator, String... strings) {
        return Arrays.stream(strings).filter(s -> !"".equals(s)).collect(Collectors.joining(separator));
    }

    /**
     *Generates a {@link String} describes package info for class implementation
     * @param token {@link Class} to get package info
     * @return {@link String} containing package info declaration
     * @see #combineLine(String, String...) 
     */
    private static String generatePackageLine(Class<?> token) {
        return combineLine(EMPTY, PACKAGE, SPACE, token.getPackageName(), SEMICOLON);
    }

    /**
     * Generates class opening line including name and super class.
     * @param token {@link Class} which implementation is required
     * @return {@link String} describes class opening line
     */
    private static String getModifiers(Class<?> token) {
        return combineLine(SPACE, "public class", token.getSimpleName() + IMPL_SUFFIX, "implements", token.getCanonicalName());
    }

    /**
     *Generates class methods and opening block
     * @param token which implementation is required
     * @return space separated {@link String} modifiers with opening block
     * @see #getModifiers(Class)
     */
    private static String generateOpeningLine(Class<?> token) {
        return combineLine(SPACE, getModifiers(token), OPEN_BLOCK);
    }

    /**
     * Returns the default return value of an object of type {@code method}.
     * @param method to get default type of
     * @return the default return value for provided {@code method}
     */
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

    /**
     * Generates {@code method} complete code.
     * @param method which body is required
     * @return implementation of required {@code method}
     */
    private static String generateMethodBody(Method method) {
        return combineLine(SPACE, RETURN, getDefaultValue(method) + SEMICOLON, CLOSE_BLOCK, EOL);
    }

    /**
     * Generates {@link String} arguments separated by comma.
     * @param method which arguments are required
     * @return {@link String} with comma separated unique arguments of method
     */
    private static String getArguments(Method method) {
        Namer namer = new Namer();
        return Arrays.stream(method.getParameterTypes())
                .map(c -> combineLine(SPACE, c.getCanonicalName(), namer.getName()))
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns a comma separated list of {@code exceptions}.
     *
     * @param method to get exceptions
     * @return {@link String} representation of comma separated {@code exceptions} of given method
     */
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

    /**
     *Generates method header. Including name, arguments and exceptions.
     * @param method which header is required
     * @return {@link String} with headers of given method
     */
    private static String generateMethodHeader(Method method) {
        return combineLine(SPACE , "public", method.getReturnType().getCanonicalName(),
                method.getName(), OPEN_BRACE, getArguments(method), CLOSE_BRACE, getExceptions(method), OPEN_BLOCK);
    }

    /**
     *Generates completely methods for given {@link Class}
     * @param token {@link Class} which implementation is required
     * @return {@link String} with completely generated method
     * @see #generateMethodBody(Method)
     * @see #generateMethodHeader(Method)
     */
    private static String generateMethods(Class<?> token) {
        StringBuilder methods = new StringBuilder();
        for (Method method : token.getMethods()) {
            methods.append(combineLine(EOL, generateMethodHeader(method), generateMethodBody(method)));
        }
        return methods.toString();
    }

    /**
     * Generates completely source code for given {@link Class}
     * @param token {@link Class} which implementation is required
     * @return {@link String} complete generated source code
     */
    public static String generateCode(Class<?> token) {
        return encode(combineLine(EOL, generatePackageLine(token), generateOpeningLine(token), generateMethods(token), CLOSE_BLOCK));
    }

    /**
     *Encodes the provided {@code String}, escaping all unicode characters in {@code \\u} notation.
     * @param s {@link String} to be encoded
     * @return the encoded {@link String}
     */
    private static String encode(String s) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return sb.toString();
    }
}
