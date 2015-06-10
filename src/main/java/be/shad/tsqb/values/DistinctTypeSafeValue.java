package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Couldn't use WrappedTypeSafeValue because that one adds brackets
 * around the value.
 * <p>
 * For a distinct value the brackets only work if the distinct
 * isn't wrapped in a count...
 */
public class DistinctTypeSafeValue<VAL> extends TypeSafeValueImpl<VAL> implements TypeSafeValueContainer, IsMaybeDistinct {
    private final TypeSafeValue<VAL> value;

    public DistinctTypeSafeValue(TypeSafeQuery query,
            TypeSafeValue<VAL> value) {
        super(query, value.getValueClass());
        this.value = value;
    }

    /**
     * Copy constructor
     */
    protected DistinctTypeSafeValue(CopyContext context, DistinctTypeSafeValue<VAL> original) {
        super(context, original);
        this.value = context.get(original.value);
    }

    @Override
    public HqlQueryValue toHqlQueryValue(HqlQueryBuilderParams params) {
        HqlQueryValue value = this.value.toHqlQueryValue(params);
        return new HqlQueryValueImpl("distinct "+value.getHql(), value.getParams());
    }

    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        validator.validateInScope(value);
    }

    @Override
    public boolean isDistinct() {
        return true;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new DistinctTypeSafeValue<>(context, this);
    }

}
