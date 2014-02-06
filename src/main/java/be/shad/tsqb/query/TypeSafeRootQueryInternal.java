package be.shad.tsqb.query;

import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.TypeSafeValue;

public interface TypeSafeRootQueryInternal extends TypeSafeRootQuery, TypeSafeQueryInternal {

    TypeSafeQueryProjections getProjections();

    /**
     * Queues the value as a selected value, this value will
     * take precedence over everything else when a proxy call to
     * a resultDto setter handled.
     */
    <T> T queueValueSelected(TypeSafeValue<T> selected);
    
    /**
     * Sets the queued value back to null and returns the value
     * if there was any.
     */
    TypeSafeValue<?> dequeueSelectedValue();

}
