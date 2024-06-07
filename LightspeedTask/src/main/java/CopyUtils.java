import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class CopyUtils {
    private static Map<Object, Object> visited = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T original) {
        if (original == null) {
            return null;
        }

        if (visited.containsKey(original)) {
            return (T) visited.get(original);
        }

        if (original.getClass().isArray()) {
            int length = Array.getLength(original);
            Object copy = Array.newInstance(original.getClass().getComponentType(), length);
            visited.put(original, copy);
            for (int i = 0; i < length; i++) {
                Array.set(copy, i, deepCopy(Array.get(original, i)));
            }
            return (T) copy;
        }

        if (original instanceof Collection) {
            Collection<Object> copy = createCollection((Collection<?>) original);
            visited.put(original, copy);
            for (Object item : (Collection<?>) original) {
                copy.add(deepCopy(item));
            }
            return (T) copy;
        }

        if (original instanceof Map) {
            Map<Object, Object> copy = createMap((Map<?, ?>) original);
            visited.put(original, copy);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) original).entrySet()) {
                copy.put(deepCopy(entry.getKey()), deepCopy(entry.getValue()));
            }
            return (T) copy;
        }

        if (original instanceof Enum || original instanceof Number || original instanceof CharSequence || original instanceof Boolean) {
            return original;
        }

        T copy = instantiate((Class<T>) original.getClass());
        visited.put(original, copy);

        for (Field field : getAllFields(original.getClass())) {
            field.setAccessible(true);
            try {
                field.set(copy, deepCopy(field.get(original)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return copy;
    }

    private static <T> T instantiate(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            System.err.println("No no-argument constructor found for class: " + clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
        }
        return null;
    }


    private static Collection<Object> createCollection(Collection<?> original) {
        if (original instanceof List) {
            return new ArrayList<>();
        } else if (original instanceof Set) {
            return new HashSet<>();
        } else if (original instanceof Queue) {
            return new LinkedList<>();
        } else {
            throw new IllegalArgumentException("Unsupported collection type: " + original.getClass());
        }
    }

    private static Map<Object, Object> createMap(Map<?, ?> original) {
        if (original instanceof SortedMap) {
            return new TreeMap<>();
        } else {
            return new HashMap<>();
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
