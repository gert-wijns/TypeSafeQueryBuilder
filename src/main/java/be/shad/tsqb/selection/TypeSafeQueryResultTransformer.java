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

import static be.shad.tsqb.selection.SelectionTree.getField;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.transform.ResultTransformer;

/**
 * Implementation to set values on nested select dtos.
 * Seems to be faster than the alias to bean result transformer too.
 */
public class TypeSafeQueryResultTransformer implements ResultTransformer {
    private static final long serialVersionUID = 4686800769621139636L;
    
    private final Class<?> resultClass;
    private final SelectionTree tree;
    private final Field[] setters;
    private final SelectionTree[] values;
    
    public TypeSafeQueryResultTransformer(Class<?> resultClass, List<String[]> aliases) {
        try {
            this.resultClass = resultClass;
            this.setters = new Field[aliases.size()];
            this.values = new SelectionTree[aliases.size()];
            this.tree = new SelectionTree(resultClass); 
            int a = 0;
            for(String[] alias: aliases) {
                SelectionTree subtree = tree;
                for(int i=0; i < alias.length-1; i++) {
                    subtree = subtree.getSubtree(alias[i]);
                }
                values[a] = subtree;
                setters[a++] = getField(subtree.getResultType(), alias[alias.length-1]);
            }
            AccessibleObject.setAccessible(setters, true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            tree.populate(resultClass.newInstance());
            for(int i=0; i < aliases.length; i++) {
                setters[i].set(values[i].getValue(), tuple[i]);
            }
            return tree.getValue();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List transformList(List collection) {
        return collection;
    }

}
