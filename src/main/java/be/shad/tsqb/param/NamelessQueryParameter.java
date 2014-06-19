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

import java.util.ArrayList;
import java.util.List;

public class NamelessQueryParameter implements QueryParameter<Object> {
    
    private Object value;
    
    public NamelessQueryParameter(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Class<Object> getValueClass() {
        return Object.class;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public void setAlias(String alias) {
        throw new UnsupportedOperationException("Alias not "
                + "supported for custom parameter.");
    }

    @Override
    public String getName() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParameterValue() {
        return getValue();
    }
    
    public static List<QueryParameter<?>> toParams(Object... objects) {
        if (objects != null) {
            List<QueryParameter<?>> params = new ArrayList<>();
            for(Object obj: objects) {
                params.add(new NamelessQueryParameter(obj));
            }
            return params;
        } else {
            return null;
        }
    }
    
    public static List<QueryParameter<?>> toParams(List<Object> objects) {
        if (objects != null) {
            return toParams(objects.toArray());
        } else {
            return null;
        }
    }
}
