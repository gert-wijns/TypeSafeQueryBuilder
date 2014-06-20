package be.shad.tsqb.values;

import be.shad.tsqb.query.TypeSafeQuery;

public class DirectTypeSafeStringValue extends DirectTypeSafeValue<String> {

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
     * When set, value.toUpperCase is applied when returning the value.
     */
    public boolean isUpper() {
        return upper;
    }
    
    /**
     * Resets the lower flag when the upper flag is set.
     */
    public void setUpper(boolean upper) {
        this.upper = upper;
        if (upper) {
            lower = false;
        }
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
    public void setLower(boolean lower) {
        this.lower = lower;
        if (lower) {
            upper = false;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
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

}
