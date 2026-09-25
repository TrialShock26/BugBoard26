package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JsonMapping {

    private JsonMapping() { }

    static String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : String.valueOf(v);
    }

    static int intVal(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? 0 : ((Number) v).intValue();
    }

    static Integer intOrNull(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : ((Number) v).intValue();
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> mapOrNull(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v instanceof Map ? (Map<String, Object>) v : null;
    }

    static Double doubleOrNull(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : ((Number) v).doubleValue();
    }

    static boolean boolVal(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return Boolean.TRUE.equals(v);
    }

    static <E extends Enum<E>> E enumVal(Map<String, Object> m, String key, Class<E> type) {
        String v = str(m, key);
        return v == null ? null : Enum.valueOf(type, v);
    }

    @SuppressWarnings("unchecked")
    static List<String> stringList(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (!(v instanceof List)) return new ArrayList<>();
        List<String> out = new ArrayList<>();
        for (Object o : (List<Object>) v) out.add(String.valueOf(o));
        return out;
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> mapList(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (!(v instanceof List)) return new ArrayList<>();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : (List<Object>) v) out.add((Map<String, Object>) o);
        return out;
    }
}
