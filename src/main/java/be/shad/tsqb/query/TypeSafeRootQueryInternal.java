package be.shad.tsqb.query;

import be.shad.tsqb.selection.TypeSafeQueryProjections;
import be.shad.tsqb.values.TypeSafeValue;

/**
 * Additional methods added to the TypeSafeRootQuery for internal use.
 * <p>
 * They are omitted from the TypeSafeRootQuery interface so it is not cluttered
 * with methods which hold no meaning to the users of the library.
 */
public interface TypeSafeRootQueryInternal extends TypeSafeRootQuery, TypeSafeQueryInternal {

    /**
     * Multiple projections are possible for the root query.
     * Exposes the projections to add values.
     */
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
