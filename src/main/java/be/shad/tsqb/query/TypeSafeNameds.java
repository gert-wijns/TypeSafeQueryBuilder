package be.shad.tsqb.query;

import be.shad.tsqb.values.NamedValueEnabled;

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
     * @throws IllegalArgumentException if named object doesn't exist or is of the wrong type.
     */
    NamedValueEnabled value(String name) throws IllegalArgumentException;
}
