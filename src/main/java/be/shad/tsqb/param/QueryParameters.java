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

import java.util.HashMap;
import java.util.Map;

/**
 * Map of params, has addParam which throws an exception in 
 * case the named parameter was already bound.
 */
public class QueryParameters {
    private Map<String, QueryParameter> params = new HashMap<>();
    
    public void addParams(QueryParameter[] params) {
        if (params != null) {
            for(QueryParameter param: params) {
                addParam(param);
            }
        }
    }

    public QueryParameter[] getParams() {
        return params.values().toArray(new QueryParameter[params.size()]);
    }

    public void addParam(QueryParameter param) {
        QueryParameter previous = this.params.put(param.getName(), param);
        if (previous != null) {
            throw new QueryParameterAlreadyBoundException(String.format(
                    "Parameter with name [%s] is already bound.", 
                    param.getName()));
        }
    }
}
