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

    /**
     * {@inheritDoc}
     */
    @Override
    public <NAMED> NAMED name(NAMED object, String name) {
        Object named = nameds.put(name, object);
        if (named != null && !named.equals(object)) {
            throw new IllegalArgumentException(String.format("Naming [%s] with name [%s] is illegal "
                    + "because another named object [%s] already used the name.", 
                    object, name, named));
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) throws IllegalArgumentException {
        return (T) get(Object.class, name);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T get(Class<T> clazz, String name) throws IllegalArgumentException {
        return named(clazz, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Object value) {
        value(name).setNamedValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedValueEnabled value(String name) throws IllegalArgumentException {
        return named(NamedValueEnabled.class, name);
    }
    
    /**
     * Get the named value and validates null and assignability.
     */
    private <T> T named(Class<T> clazz, String name) {
        Object object = nameds.get(name);
        if (object == null) {
            throw new IllegalArgumentException(String.format(
                    "No named object found for [%s].", name));
        }
        if (!clazz.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException(String.format(
                    "Named object [%s] doesn't have the correct type.", 
                    object, clazz));
        }
        return clazz.cast(object);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new TypeSafeNamedsImpl(context, this);
    }

}
