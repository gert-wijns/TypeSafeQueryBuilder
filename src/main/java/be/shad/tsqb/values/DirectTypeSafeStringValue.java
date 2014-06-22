package be.shad.tsqb.values;

import static be.shad.tsqb.restrictions.RestrictionOperator.EQUAL;
import static be.shad.tsqb.restrictions.RestrictionOperator.LIKE;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_EQUAL;
import static be.shad.tsqb.restrictions.RestrictionOperator.NOT_LIKE;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;
import be.shad.tsqb.restrictions.RestrictionOperator;

public class DirectTypeSafeStringValue extends DirectTypeSafeValue<String> implements OperatorAwareValue {

    public final static String EMPTY = "";
    
    private boolean upper;
    private boolean lower;
    private String prefix = EMPTY;
    private String postfix = EMPTY;
    
    public DirectTypeSafeStringValue(TypeSafeQuery query, String value) {
        this(query);
        setValue(value);
    }

    public DirectTypeSafeStringValue(TypeSafeQuery query) {
        super(query, String.class);
    }

    /**
     * Copy constructor
     */
    protected DirectTypeSafeStringValue(CopyContext context, DirectTypeSafeStringValue original) {
        super(context, original);
        this.upper = original.upper;
        this.lower = original.lower;
        this.prefix = original.prefix;
        this.postfix = original.postfix;
    }

    /**
     * When set, value.toUpperCase is applied when returning the value.
     */
    public boolean isUpper() {
        return upper;
    }
    
    /**
     * Resets the lower flag when the upper flag is set.
     */
    public DirectTypeSafeStringValue setUpper(boolean upper) {
        this.upper = upper;
        if (upper) {
            lower = false;
        }
        return this;
    }

    /**
     * When set, value.toLowerCase is applied when returning the value.
     */
    public boolean isLower() {
        return lower;
    }
    
    /**
     * Resets the upper flag when the lower flag is set.
     */
    public DirectTypeSafeStringValue setLower(boolean lower) {
        this.lower = lower;
        if (lower) {
            upper = false;
        }
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public DirectTypeSafeStringValue setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public String getPostfix() {
        return postfix;
    }

    public DirectTypeSafeStringValue setPostfix(String postfix) {
        this.postfix = postfix;
        return this;
    }

    /**
     * Takes the string flags into account to determine the string value.
     */
    @Override
    public String getValue() {
        String wrapped = super.getValue();
        if (wrapped != null) {
            wrapped = prefix + wrapped + postfix;
            if (upper) {
                wrapped = wrapped.toUpperCase();
            } else if (lower) {
                wrapped = wrapped.toLowerCase();
            }
        }
        return wrapped;
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new DirectTypeSafeStringValue(context, this);
    }

    /**
     * Use a more specific operator no wildcard is set.
     */
    @Override
    public RestrictionOperator getOperator(RestrictionOperator original) {
        switch (original) {
            case EQUAL: return isLike() ? LIKE: EQUAL;
            case NOT_EQUAL: return isLike() ? NOT_LIKE: NOT_EQUAL;
            default: 
        }
        return original;
    }
    
    /**
     * Use like when a prefix or postfix was set.
     */
    private boolean isLike() {
        return !EMPTY.equals(prefix) || !EMPTY.equals(postfix);
    }
    
}
