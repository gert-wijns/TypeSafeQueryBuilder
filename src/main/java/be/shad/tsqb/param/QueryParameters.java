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
package be.shad.tsqb.param;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import be.shad.tsqb.exceptions.QueryParameterAlreadyBoundException;

/**
 * Map of params, has addParam which throws an exception in 
 * case the named parameter was already bound.
 */
public class QueryParameters {
    private Map<String, QueryParameter<?>> paramsByQyeryAlias = new HashMap<>();
    private Map<String, QueryParameter<?>> paramsByUserAlias = new HashMap<>();
    
    public void addParams(Collection<QueryParameter<?>> params) {
        if (params != null) {
            for(QueryParameter<?> param: params) {
                addParam(param);
            }
        }
    }

    public Collection<QueryParameter<?>> getParams() {
        return paramsByQyeryAlias.values();
    }
    
    public void addParam(QueryParameter<?> param) {
        QueryParameter<?> previous = this.paramsByQyeryAlias.put(
                param.getName(), param);
        if (previous != null) {
            throw new QueryParameterAlreadyBoundException(String.format(
                    "Parameter with query alias [%s] is already bound.", 
                    param));
        }
        if (param.getAlias() != null) {
            setUserAlias(param, param.getAlias());
        }
    }

    public void setUserAlias(QueryParameter<?> param, String userAlias) {
        if (param.getAlias() != null) {
            paramsByUserAlias.remove(userAlias);
        }
        param.setAlias(userAlias);
        QueryParameter<?> previous = paramsByUserAlias.put(userAlias, param);
        if (previous != null) {
            throw new QueryParameterAlreadyBoundException(String.format(
                    "Parameter with user alias [%s] is already bound.", 
                    param));
        }
    }

    public QueryParameter<?> getParamForAlias(String paramAlias) {
        return paramsByUserAlias.get(paramAlias);
    }
    
}
