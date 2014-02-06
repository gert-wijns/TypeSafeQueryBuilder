package be.shad.tsqb.values;

/**
 * The TypeSafeValue is a wrapper which represents a generic value type.
 * <p>
 * Most method calls which accept Strings/Numbers/basic types convert the
 * input data and pass it on to a method which accepts a TypeSafeValue.
 * <p>
 * This wrapper extends the HqlQueryValueBuilder to be able to convert 
 * it and add it to the HqlQuery when the query is converted.
 */
public interface TypeSafeValue<V> extends HqlQueryValueBuilder {
    
    /**
     * @return the type represented by this type safe value.
     */
    Class<V> getValueClass();

    /**
     * End the expression with select() to select the value into a dto.
     * This method may only be used when selecting into a resultDto.
     * <p>
     * An exception will be thrown if the next call is not 
     * a setter on the result dto.
     */
    V select();
    
}
