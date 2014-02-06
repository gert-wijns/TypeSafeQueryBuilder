package be.shad.tsqb.grouping;

import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.hql.HqlQuery;
import be.shad.tsqb.hql.HqlQueryBuilder;

public class TypeSafeQueryGroupBys implements HqlQueryBuilder {
    private List<GroupBy> groupBys = new LinkedList<>();
    
    public List<GroupBy> getGroupBys() {
        return groupBys;
    }
    
    public void addGroupBy(GroupBy groupBy) {
        groupBys.add(groupBy);
    }
    
    @Override
    public void appendTo(HqlQuery query) {
        for(GroupBy groupBy: groupBys) {
            groupBy.appendTo(query);
        }
    }

}
