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
package be.shad.tsqb.query.copy;

import java.util.IdentityHashMap;

public class CopyContext {

    // using identity map/set to make sure equals doesn't break the copy
    private final IdentityHashMap<Object, Object> data = new IdentityHashMap<>();
    
    /**
     * Looks up the copy of <code>originalOrCopy</code>.
     * If it was already a copy, then itself will be returned.
     * If it is an original, and a copy was already made, the copy will be returned.
     * If it is an original, and the copy was not found, then the original is copied.
     */
    public <T> T get(T originalOrCopy) {
        return getInternal(originalOrCopy, true);
    }

    /**
     * Same as get, except no exception is thrown if it is not copyable
     * Instead, the original value is returned if it cannot be copied.
     */
    public <T> T getOrOriginal(T originalOrCopy) {
        return getInternal(originalOrCopy, false);
    }

    @SuppressWarnings("unchecked")
    private <T> T getInternal(T originalOrCopy, boolean copyableRequired) {
        if (originalOrCopy == null) {
            return null;
        }
        if (originalOrCopy instanceof Stateless) {
            return originalOrCopy;
        }
        Object copy = data.get(originalOrCopy);
        if (copy == null) {
            if (originalOrCopy instanceof Copyable) {
                // if no copy exists, create a copy and 
                copy = ((Copyable) originalOrCopy).copy(this);
            } else if (copyableRequired) {
                throw new IllegalStateException(String.format("Object [%s] is not copyable, "
                        + "so its copy should have been added before trying to get its copy.",
                        originalOrCopy));
            } else {
                copy = originalOrCopy;
            }
            put(originalOrCopy, (T) copy);
        }
        return (T) copy;
    }
    
    /**
     * Add the original and its copy to the identity 
     * map so future gets will yield the copied object
     * <p>
     * This should be called first in the copy constructor of
     * a copyable object.
     */
    public <T, V extends T> V put(T original, V copy) {
        data.put(copy, copy);
        data.put(original, copy);
        return copy;
    }
    
}
