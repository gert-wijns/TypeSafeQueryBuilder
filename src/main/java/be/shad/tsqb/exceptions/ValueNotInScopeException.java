package be.shad.tsqb.exceptions;

public class ValueNotInScopeException extends RuntimeException {
    private static final long serialVersionUID = 4598264079089437433L;

    public ValueNotInScopeException(String message) {
        super(message);
    }
    
}
