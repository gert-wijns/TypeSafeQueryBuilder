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
package be.shad.tsqb.selection;

import java.util.HashMap;
import java.util.Map;

public class SelectionIdentityTree {
    private final Map<Object, SelectionIdentityTree> subtrees;
    private Object identityValue;

    public SelectionIdentityTree() {
        this.subtrees = new HashMap<>();
    }

    public SelectionIdentityTree getSubtree(Object value) {
        return subtrees.get(value);
    }
    
    public SelectionIdentityTree createSubtree(Object value) {
        SelectionIdentityTree subtree = new SelectionIdentityTree();
        subtrees.put(value, subtree);
        return subtree;
    }
    
    public Object getIdentityValue() {
        return identityValue;
    }
    
    public void setIdentityValue(Object identityValue) {
        this.identityValue = identityValue;
    }
}
