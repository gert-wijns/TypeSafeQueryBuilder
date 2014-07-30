/*
 * Copyright Gert Wijns gert.wijns@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.shad.tsqb.hql;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.transform.ResultTransformer;

import be.shad.tsqb.values.HqlQueryValue;

public class HqlQuery implements HqlQueryValue {
    private StringBuilder select = new StringBuilder();
    private StringBuilder from = new StringBuilder();
    private StringBuilder where = new StringBuilder();
    private StringBuilder groupBy = new StringBuilder();
    private StringBuilder having = new StringBuilder();
    private StringBuilder orderBy = new StringBuilder();
    private List<Object> params = new LinkedList<Object>();
    private ResultTransformer resultTransformer;

    public ResultTransformer getResultTransformer() {
        return resultTransformer;
    }
    
    public void setResultTransformer(ResultTransformer resultTransformer) {
        this.resultTransformer = resultTransformer;
    }

    @Override
    public Collection<Object> getParams() {
        return params;
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
    
    public String getHaving() {
        if( having.length() > 0 ) {
            return " having " + having.toString();
        }
        return "";
    }
    
    public void appendHaving(String havingPart) {
        if( having.length() >  0 ) {
            having.append(" and ");
        }
        having.append(havingPart);
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

    public void addParams(Collection<Object> params) {
        this.params.addAll(params);
    }

    public String getHql() {
        return getSelect() + getFrom() + getWhere() + getGroupBy() + getHaving() + getOrderBy();
    }

    /**
     * String with newlines and spaces to outline the query in a pretty format.
     */
    public String toFormattedString() {
        BasicFormatterImpl formatter = new BasicFormatterImpl();
        String prettyHql = formatter.format(getHql());
        prettyHql = prettyHql.replaceAll("((from|join|select|where)\\n\\s*)", "$2 ");
        return prettyHql + "\n --- with: " + params.toString().replace("PARAM [", "\n[");
    }
    
    @Override
    public String toString() {
        return getHql() + " --- with: " + params;
    }
    
}
