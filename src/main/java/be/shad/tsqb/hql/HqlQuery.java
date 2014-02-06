package be.shad.tsqb.hql;

import java.util.LinkedList;

import be.shad.tsqb.values.HqlQueryValue;

public class HqlQuery implements HqlQueryValue {
    private StringBuilder select = new StringBuilder();
    private StringBuilder from = new StringBuilder();
    private StringBuilder where = new StringBuilder();
    private StringBuilder groupBy = new StringBuilder();
    private StringBuilder orderBy = new StringBuilder();
    private LinkedList<Object> params = new LinkedList<>();
    private Class<?> resultClass;
    
    /**
     * Use with {@link org.hibernate.transform.Transformers#aliasToBean(Class) aliasToBean} 
     * to select the aliased values into a dto.
     */
    public Class<?> getResultClass() {
        return resultClass;
    }
    
    public void setResultClass(Class<?> resultClass) {
        this.resultClass = resultClass;
    }

    public String getSelect() {
        if( select.length() > 0 ) {
            return "select " + select.toString();
        }
        return "";
    }
    
    public void appendSelect(String selectPart) {
        if( select.length() > 0 ) {
            select.append(", ");
        }
        select.append(selectPart);
    }

    public String getFrom() {
        return " from " + from.toString();
    }
    
    public void appendFrom(String fromPart) {
        if( from.length() > 0 ) {
            from.append(", ");
        }
        from.append(fromPart);
    }

    public String getWhere() {
        if( where.length() > 0 ) {
            return " where " + where.toString();
        }
        return "";
    }
    
    public void appendWhere(String wherePart) {
        if( where.length() >  0 ) {
            where.append(" and ");
        }
        where.append(wherePart);
    }

    public String getGroupBy() {
        if( groupBy.length() > 0 ) {
            return " group by " + groupBy.toString();
        }
        return "";
    }
    
    public void appendGroupBy(String groupByPart) {
        if( groupBy.length() > 0 ) {
            groupBy.append(", ");
        }
        groupBy.append(groupByPart);
    }
    
    public String getOrderBy() {
        if( orderBy.length() > 0 ) {
            return " order by " + orderBy.toString();
        }
        return "";
    }
    
    public void appendOrderBy(String orderByPart) {
        if( orderBy.length() > 0 ) {
            orderBy.append(", ");
        }
        orderBy.append(orderByPart);
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public void addParam(Object param) {
        params.add(param);
    }

    public void addParams(Object[] params) {
        for(Object param: params) {
            this.params.add(param);
        }
    }
    
    public String getHql() {
        return getSelect() + getFrom() + getWhere() + getGroupBy() + getOrderBy();
    }

    @Override
    public String toString() {
        String str = getHql().
                replace("select", "\nselect").
                replace("from", "\nfrom").
                replace("join hobj", "\n  join hobj").
                replace("where", "\n  where").
                replace(" and ", "\n    and ").
                replace(" or ", "\n   or ").
                replace("order by","\norder by").
                replace("group by","\ngroup by");
        
        StringBuilder format = new StringBuilder();
        int depth = 0;
        for(int i=0; i < str.length(); i++) {
            format.append(str.charAt(i));
            if( str.charAt(i) == '\n' ) {
                for(int d=0; d < depth; d++) {
                    format.append("  ");
                }
            } else if ( str.charAt(i) == '(' ) {
                depth++;
            } else if ( str.charAt(i) == ')' ) {
                depth--;
            }
        }
        return format + "\n --- with params: " + params;
    }
    
}
