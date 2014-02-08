package be.shad.tsqb.values;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQuery;
import be.shad.tsqb.query.TypeSafeQueryScopeValidator;
import be.shad.tsqb.restrictions.Restriction;

/**
 * Represents a case when() then ... (else ...) end.
 */
public class CaseTypeSafeValue<T> extends TypeSafeValueImpl<T> implements OnGoingCase<T>, OnGoingCaseWhen<T>, TypeSafeValueContainer {
    private List<Restriction> whens = new LinkedList<>();
    private List<TypeSafeValue<T>> thens = new LinkedList<>();

    public CaseTypeSafeValue(TypeSafeQuery query, Class<T> valueType) {
        super(query, valueType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCaseWhen<T> when(Restriction restriction) {
        whens.add(restriction);
        if( whens.size() > thens.size()+1 ) {
            throw new IllegalStateException("A when(restriction) was provided, "
                    + "but it appears no then or else value was provided.");
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCaseWhen<T> then(TypeSafeValue<T> value) {
        thens.add(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnGoingCaseWhen<T> then(T value) {
        if( value == null ) {
            // this implementation was madto prevent 'null' to slip into the
            // query.toValue because it should never be used that way.
            TypeSafeQueryProxyData data = query.dequeueInvocation();
            if( data == null ) {
                return then(new CustomTypeSafeValue<T>(query, getValueClass(), "null", null));
            }
            query.invocationWasMade(data); // add it back to the query.
        }
        return then(query.toValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<T> otherwise(TypeSafeValue<T> value) {
        then(value);
        return end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<T> otherwise(T value) {
        then(value);
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TypeSafeValue<T> end() {
        return this;
    }

    @Override
    public HqlQueryValue toHqlQueryValue() {
        HqlQueryValueImpl value = new HqlQueryValueImpl();
        if(whens.size() > 0 ) {
            value.appendHql("(");
        }
        for(int i=0; i < whens.size(); i++) {
            if( i == 0 ){
                value.appendHql("case when (");
            } else {
                value.appendHql(" when (");
            }
            HqlQueryValue when = whens.get(i).toHqlQueryValue();
            value.appendHql(when.getHql());
            value.addParams(when.getParams());
            value.appendHql(") then ");
            
            HqlQueryValue then = thens.get(i).toHqlQueryValue();
            value.appendHql(then.getHql());
            value.addParams(then.getParams());
        }
        if(thens.size() > whens.size()) {
            value.appendHql(" else ");
            HqlQueryValue otherwise = thens.get(thens.size()-1).toHqlQueryValue();
            value.appendHql(otherwise.getHql());
            value.addParams(otherwise.getParams());
        }
        if(whens.size() > 0 ) {
            value.appendHql(" end)");
        }
        return query.getHelper().replaceParamsWithLiterals(value);
    }
    
    @Override
    public void validateContainedInScope(TypeSafeQueryScopeValidator validator) {
        for(TypeSafeValue<T> then: thens) {
            validator.validateInScope(then);
        }
    }
}
