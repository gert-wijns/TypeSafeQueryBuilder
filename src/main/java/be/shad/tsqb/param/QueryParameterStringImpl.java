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

public class QueryParameterStringImpl extends QueryParameterSingleImpl<String> {

    public final static String EMPTY = "";
    
    private boolean upper;
    private boolean lower;
    private String prefix = EMPTY;
    private String postfix = EMPTY;

    public QueryParameterStringImpl(String name, String value) {
        super(name, String.class, value);
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
