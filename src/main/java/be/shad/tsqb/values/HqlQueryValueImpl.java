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
package be.shad.tsqb.values;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import be.shad.tsqb.query.copy.CopyContext;
import be.shad.tsqb.query.copy.Copyable;

/**
 * Wraps an hql stringbuilder and params and provides
 * convenient methods to append to them.
 */
public class HqlQueryValueImpl implements HqlQueryValue, Copyable {
    private final List<Object> params = new LinkedList<>();
    private StringBuilder hql;

    /**
     * Factory method for shorthand hql query value creation.
     * Use hql(...) + static import to make shorter code.
     */
    public static HqlQueryValue hql(String hql, Object... params) {
        return new HqlQueryValueImpl(hql, params);
    }

    /**
     * Factory method for shorthand hql query value creation.
     * Use hql(...) + static import to make shorter code.
     */
    public static HqlQueryValue hql(String hql, Collection<Object> params) {
        return new HqlQueryValueImpl(hql, params);
    }

    public HqlQueryValueImpl() {
        this("");
    }

    public HqlQueryValueImpl(String hql) {
        this.hql = new StringBuilder(hql);
    }

    public HqlQueryValueImpl(String hql, Object... params) {
        this(hql);
        if (params != null) {
            for(Object param: params) {
                addParam(param);
            }
        }
    }

    public HqlQueryValueImpl(String hql, Collection<Object> params) {
        this(hql);
        if (params != null) {
            addParams(params);
        }
    }

    /**
     * Copy constructor
     */
    protected HqlQueryValueImpl(CopyContext context, HqlQueryValueImpl original) {
        this.hql = original.hql;
        for(Object param: original.params) {
            params.add(context.getOrOriginal(param));
        }
    }

    public boolean isEmpty() {
        return hql.length() == 0;
    }

    public String getHql() {
        return hql.toString();
    }

    public void setHql(String hql) {
        this.hql = new StringBuilder(hql);
    }

    public StringBuilder appendHql(String hql) {
        this.hql.append(hql);
        return this.hql;
    }

    public Collection<Object> getParams() {
        return params;
    }

    public void addParam(Object param) {
        params.add(param);
    }

    public void addParams(Collection<Object> params) {
        this.params.addAll(params);
    }

    @Override
    public Copyable copy(CopyContext context) {
        return new HqlQueryValueImpl(context, this);
    }

}
