package be.shad.tsqb.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.values.NamedValueEnabled;

public class TypeSafeNamedsImpl implements TypeSafeNameds, Copyable {
    private final Map<String, Object> nameds = new HashMap<>();

    public TypeSafeNamedsImpl() {
        // nothing extra
    }

    /**
     * Copy constructor
     */
    protected TypeSafeNamedsImpl(CopyContext context, TypeSafeNamedsImpl original) {
        for(Entry<String, Object> named: original.nameds.entrySet()) {
            nameds.put(named.getKey(), context.get(named.getValue()));
        }
    }

    @Override
    public <NAMED> NAMED name(NAMED object, String name) {
        if (name == null) {
            throw new IllegalArgumentException(String.format("Name is null when trying to set name for [%s].", object));
        }
        if (isBlank(name)) {
            throw new IllegalArgumentException(String.format("Name is blank when trying to set name for [%s].", object));
        }
        Object named = nameds.put(name, object);
        if (named != null && named != object) {
            throw new IllegalArgumentException(String.format("Naming [%s] with name [%s] is illegal "
                    + "because another named object [%s] already used the name.",
                    object, name, named));
        }
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) throws IllegalArgumentException {
        return (T) get(Object.class, name);
    }

    @Override
    public <T> T get(Class<T> clazz, String name) throws IllegalArgumentException {
        return named(clazz, name);
    }

    @Override
    public <T> T getOrNull(Class<T> clazz, String name) {
        Object object = nameds.get(name);
        if (object == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(String.format(
                    "Named object [%s] doesn't have the correct type [%s].",
                    object, clazz));
        }
        return clazz.cast(object);
    }

    @Override
    public void setValue(String name, Object value) {
        named(NamedValueEnabled.class, name).setNamedValue(value);
    }

    /**
     * Get the named value and validates null and assignability.
     */
    private <T> T named(Class<T> clazz, String name) {
        T object = getOrNull(clazz, name);
        if (object == null) {
            throw new IllegalArgumentException(String.format(
                    "No named object found for [%s].", name));
        }
        return object;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeNamedsImpl(context, this);
    }

    /**
     * Copied (partially) from StringUtils.isBlank(String),
     * <p>
     * See {@link org.apache.commons.lang3.StringUtils#isBlank(String)}
     */
    private boolean isBlank(String name) {
        for (int i = 0, strLen = name.length(); i < strLen; i++) {
            if (!Character.isWhitespace(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
