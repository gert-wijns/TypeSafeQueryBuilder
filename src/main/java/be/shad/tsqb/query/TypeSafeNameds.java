package be.shad.tsqb.query;

public interface TypeSafeNameds {

    /**
     * Names an object, after this, the object can be retrieved using the other methods.
     *
     * @throws IllegalArgumentException if another object was already named with the given <code>name</code>.
     */
    <NAMED> NAMED name(NAMED object, String name) throws IllegalArgumentException;

    /**
     * Same as proxy and value, but using Object.class, no class check support.
     * The named object must still not be null.
     * <p>
     * Delegates to {@link #get(Class, String)} with Object.class.
     */
    <T> T get(String name) throws IllegalArgumentException;

    /**
     * @return an existing proxy which was named.
     *
     * @throws IllegalArgumentException when no named proxy exists for the given <code>name</code>
     *                                  or when the proxy class dosn't match the given <code>clazz</code>.
     */
    <T> T get(Class<T> clazz, String name) throws IllegalArgumentException;

    /**
     * Get the proxy for the given name, unless it doesn't exist.
     */
    <T> T getOrNull(Class<T> clazz, String name);

    /**
     * Sets the value of a named param.
     * <p>
     * The value will be checked with the value type required
     * by the named param, unless it is null.
     * <p>
     * Name can be set by using where(entity.getName()).eq().named("paramAlias")
     * or by using {@link #name(Object, String)}
     *
     * @param name must be one which was set before calling this method
     */
    void setValue(String name, Object value);

}
