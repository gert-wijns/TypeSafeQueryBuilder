package be.shad.tsqb.values;

public interface TypeSafeValue<V extends Object> extends HqlQueryValueBuilder {
    
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
