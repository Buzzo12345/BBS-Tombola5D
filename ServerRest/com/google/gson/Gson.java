package com.google.gson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Gson {
    private final boolean prettyPrinting;

    Gson(boolean prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    public Gson() {
        this(false);
    }

    public String toJson(Object value) {
        StringBuilder builder = new StringBuilder();
        writeJson(value, builder, 0);
        return builder.toString();
    }

    public <T> T fromJson(Reader reader, Class<T> classOfT) {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
            return fromJson(writer.toString(), classOfT);
        } catch (IOException e) {
            throw new JsonSyntaxException("Errore nella lettura del JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }

        JsonParser parser = new JsonParser(json);
        Object parsed = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isAtEnd()) {
            throw new JsonSyntaxException("JSON non valido: caratteri extra dopo il valore");
        }

        return convertValue(parsed, classOfT);
    }

    private void writeJson(Object value, StringBuilder builder, int indentLevel) {
        if (value == null) {
            builder.append("null");
            return;
        }

        if (value instanceof String) {
            appendString((String) value, builder);
            return;
        }

        if (value instanceof Number || value instanceof Boolean) {
            builder.append(value.toString());
            return;
        }

        if (value instanceof Map) {
            writeMap((Map<?, ?>) value, builder, indentLevel);
            return;
        }

        if (value instanceof Collection) {
            writeCollection((Collection<?>) value, builder, indentLevel);
            return;
        }

        if (value.getClass().isArray()) {
            writeArray(value, builder, indentLevel);
            return;
        }

        writeObject(value, builder, indentLevel);
    }

    private void writeMap(Map<?, ?> map, StringBuilder builder, int indentLevel) {
        builder.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            appendNewLineIfNeeded(builder, indentLevel + 1);
            appendString(String.valueOf(entry.getKey()), builder);
            builder.append(prettyPrinting ? ": " : ":");
            writeJson(entry.getValue(), builder, indentLevel + 1);
            first = false;
        }
        appendClosingBrace(builder, indentLevel, first);
    }

    private void writeCollection(Collection<?> collection, StringBuilder builder, int indentLevel) {
        builder.append('[');
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                builder.append(',');
            }
            appendNewLineIfNeeded(builder, indentLevel + 1);
            writeJson(item, builder, indentLevel + 1);
            first = false;
        }
        appendClosingBracket(builder, indentLevel, first);
    }

    private void writeArray(Object array, StringBuilder builder, int indentLevel) {
        builder.append('[');
        int length = Array.getLength(array);
        for (int index = 0; index < length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            appendNewLineIfNeeded(builder, indentLevel + 1);
            writeJson(Array.get(array, index), builder, indentLevel + 1);
        }
        appendClosingBracket(builder, indentLevel, length == 0);
    }

    private void writeObject(Object value, StringBuilder builder, int indentLevel) {
        builder.append('{');
        boolean first = true;
        for (Field field : getAllFields(value.getClass())) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(value);
                if (!first) {
                    builder.append(',');
                }
                appendNewLineIfNeeded(builder, indentLevel + 1);
                appendString(field.getName(), builder);
                builder.append(prettyPrinting ? ": " : ":");
                writeJson(fieldValue, builder, indentLevel + 1);
                first = false;
            } catch (IllegalAccessException e) {
                throw new JsonSyntaxException("Impossibile serializzare il campo: " + field.getName(), e);
            }
        }
        appendClosingBrace(builder, indentLevel, first);
    }

    private void appendClosingBrace(StringBuilder builder, int indentLevel, boolean empty) {
        if (prettyPrinting && !empty) {
            builder.append('\n');
            appendIndent(builder, indentLevel);
        }
        builder.append('}');
    }

    private void appendClosingBracket(StringBuilder builder, int indentLevel, boolean empty) {
        if (prettyPrinting && !empty) {
            builder.append('\n');
            appendIndent(builder, indentLevel);
        }
        builder.append(']');
    }

    private void appendNewLineIfNeeded(StringBuilder builder, int indentLevel) {
        if (prettyPrinting) {
            builder.append('\n');
            appendIndent(builder, indentLevel);
        }
    }

    private void appendIndent(StringBuilder builder, int indentLevel) {
        for (int index = 0; index < indentLevel; index++) {
            builder.append("  ");
        }
    }

    private void appendString(String value, StringBuilder builder) {
        builder.append('"');
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        builder.append(String.format("\\u%04x", (int) character));
                    } else {
                        builder.append(character);
                    }
            }
        }
        builder.append('"');
    }

    private <T> T convertValue(Object value, Class<T> classOfT) {
        if (value == null) {
            return null;
        }

        if (classOfT.isInstance(value)) {
            return classOfT.cast(value);
        }

        try {
            if (classOfT == String.class) {
                return classOfT.cast(String.valueOf(value));
            }

            if (classOfT == Double.class || classOfT == double.class) {
                return classOfT.cast(asDouble(value));
            }

            if (classOfT == Integer.class || classOfT == int.class) {
                return classOfT.cast((int) asDouble(value));
            }

            if (classOfT == Long.class || classOfT == long.class) {
                return classOfT.cast((long) asDouble(value));
            }

            if (classOfT == Boolean.class || classOfT == boolean.class) {
                return classOfT.cast(asBoolean(value));
            }

            if (!(value instanceof Map)) {
                throw new JsonSyntaxException("JSON non compatibile con " + classOfT.getName());
            }

            Map<?, ?> map = (Map<?, ?>) value;
            Constructor<T> constructor = classOfT.getDeclaredConstructor();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();

            for (Field field : getAllFields(classOfT)) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }

                Object fieldValue = map.get(field.getName());
                if (!map.containsKey(field.getName()) || fieldValue == null) {
                    continue;
                }

                field.setAccessible(true);
                field.set(instance, convertForField(field.getType(), fieldValue));
            }

            return instance;
        } catch (JsonSyntaxException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonSyntaxException("Impossibile convertire il JSON in " + classOfT.getName(), e);
        }
    }

    private Object convertForField(Class<?> fieldType, Object value) {
        if (fieldType == String.class) {
            return String.valueOf(value);
        }
        if (fieldType == double.class || fieldType == Double.class) {
            return asDouble(value);
        }
        if (fieldType == int.class || fieldType == Integer.class) {
            return (int) asDouble(value);
        }
        if (fieldType == long.class || fieldType == Long.class) {
            return (long) asDouble(value);
        }
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return asBoolean(value);
        }
        if (fieldType.isEnum() && value instanceof String) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Enum enumValue = Enum.valueOf((Class<? extends Enum>) fieldType, (String) value);
            return enumValue;
        }
        return value;
    }

    private double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("Numero non valido: " + value, e);
            }
        }
        throw new JsonSyntaxException("Numero non valido: " + value);
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        throw new JsonSyntaxException("Valore booleano non valido: " + value);
    }

    private Field[] getAllFields(Class<?> type) {
        java.util.List<Field> fields = new java.util.ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            Field[] declaredFields = current.getDeclaredFields();
            for (Field field : declaredFields) {
                fields.add(field);
            }
            current = current.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    private static final class JsonParser {
        private final String json;
        private int index;

        private JsonParser(String json) {
            this.json = json;
        }

        private Object parseValue() {
            skipWhitespace();
            if (isAtEnd()) {
                throw new JsonSyntaxException("JSON vuoto");
            }

            char character = json.charAt(index);
            switch (character) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                case 'f':
                    return parseBoolean();
                case 'n':
                    return parseNull();
                default:
                    return parseNumber();
            }
        }

        private Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> result = new LinkedHashMap<>();
            skipWhitespace();
            if (peek('}')) {
                index++;
                return result;
            }

            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                result.put(key, value);
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    continue;
                }
                if (peek('}')) {
                    index++;
                    break;
                }
                throw new JsonSyntaxException("JSON oggetto non valido");
            }

            return result;
        }

        private java.util.List<Object> parseArray() {
            expect('[');
            java.util.List<Object> result = new java.util.ArrayList<>();
            skipWhitespace();
            if (peek(']')) {
                index++;
                return result;
            }

            while (true) {
                result.add(parseValue());
                skipWhitespace();
                if (peek(',')) {
                    index++;
                    continue;
                }
                if (peek(']')) {
                    index++;
                    break;
                }
                throw new JsonSyntaxException("JSON array non valido");
            }

            return result;
        }

        private String parseString() {
            expect('"');
            StringBuilder builder = new StringBuilder();
            while (!isAtEnd()) {
                char character = json.charAt(index++);
                if (character == '"') {
                    return builder.toString();
                }
                if (character == '\\') {
                    if (isAtEnd()) {
                        throw new JsonSyntaxException("Escape JSON incompleto");
                    }
                    char escaped = json.charAt(index++);
                    switch (escaped) {
                        case '"': builder.append('"'); break;
                        case '\\': builder.append('\\'); break;
                        case '/': builder.append('/'); break;
                        case 'b': builder.append('\b'); break;
                        case 'f': builder.append('\f'); break;
                        case 'n': builder.append('\n'); break;
                        case 'r': builder.append('\r'); break;
                        case 't': builder.append('\t'); break;
                        case 'u':
                            builder.append(parseUnicode());
                            break;
                        default:
                            throw new JsonSyntaxException("Escape JSON non valido: \\" + escaped);
                    }
                } else {
                    builder.append(character);
                }
            }
            throw new JsonSyntaxException("Stringa JSON non terminata");
        }

        private char parseUnicode() {
            if (index + 4 > json.length()) {
                throw new JsonSyntaxException("Unicode escape incompleto");
            }
            String hex = json.substring(index, index + 4);
            index += 4;
            try {
                return (char) Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("Unicode escape non valido: \\u" + hex, e);
            }
        }

        private Object parseBoolean() {
            if (match("true")) {
                return Boolean.TRUE;
            }
            if (match("false")) {
                return Boolean.FALSE;
            }
            throw new JsonSyntaxException("Valore booleano non valido");
        }

        private Object parseNull() {
            if (match("null")) {
                return null;
            }
            throw new JsonSyntaxException("Valore null non valido");
        }

        private Number parseNumber() {
            int start = index;
            if (peek('-')) {
                index++;
            }
            consumeDigits();
            if (peek('.')) {
                index++;
                consumeDigits();
            }
            if (peek('e') || peek('E')) {
                index++;
                if (peek('+') || peek('-')) {
                    index++;
                }
                consumeDigits();
            }

            String numberText = json.substring(start, index);
            try {
                if (numberText.contains(".") || numberText.contains("e") || numberText.contains("E")) {
                    return Double.parseDouble(numberText);
                }
                long asLong = Long.parseLong(numberText);
                if (asLong >= Integer.MIN_VALUE && asLong <= Integer.MAX_VALUE) {
                    return (int) asLong;
                }
                return asLong;
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("Numero JSON non valido: " + numberText, e);
            }
        }

        private void consumeDigits() {
            if (isAtEnd() || !Character.isDigit(json.charAt(index))) {
                throw new JsonSyntaxException("Numero JSON non valido");
            }
            while (!isAtEnd() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        private boolean match(String expected) {
            if (json.regionMatches(index, expected, 0, expected.length())) {
                index += expected.length();
                return true;
            }
            return false;
        }

        private void expect(char expected) {
            skipWhitespace();
            if (isAtEnd() || json.charAt(index) != expected) {
                throw new JsonSyntaxException("Atteso carattere: " + expected);
            }
            index++;
        }

        private boolean peek(char expected) {
            return !isAtEnd() && json.charAt(index) == expected;
        }

        private void skipWhitespace() {
            while (!isAtEnd()) {
                char character = json.charAt(index);
                if (character == ' ' || character == '\n' || character == '\r' || character == '\t') {
                    index++;
                } else {
                    break;
                }
            }
        }

        private boolean isAtEnd() {
            return index >= json.length();
        }
    }
}
