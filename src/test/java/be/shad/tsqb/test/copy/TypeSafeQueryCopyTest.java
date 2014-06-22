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
package be.shad.tsqb.test.copy;

import static be.shad.tsqb.values.HqlQueryValueImpl.hql;
import static org.junit.Assert.assertNotEquals;
import be.shad.tsqb.query.TypeSafeRootQuery;
import be.shad.tsqb.test.TypeSafeQueryTest;
import be.shad.tsqb.values.HqlQueryValue;

public class TypeSafeQueryCopyTest extends TypeSafeQueryTest {
    protected static final String PERSON_OBJ = "PersonProxy";
    
    protected TypeSafeRootQuery copy;
    
    private HqlQueryValue originalHql;
    private HqlQueryValue copyHql;

    protected void validateChangedCopy(String hql, Object... params) {
        copyHql = hql(hql, params);
        validateHql();
    }

    protected void validateChangedOriginal(String hql, Object... params) {
        originalHql = hql(hql, params);
        validateHql();
    }

    /**
     * Validates the original, copies the original and validates
     * that the copy yields the same hql value.
     */
    protected <T> T validateAndCopy(String namedToReturn, String hql, Object... params) {
        originalHql = hql(hql, params);
        copyHql = hql(hql, params);
        
        // test initial query is as expected:
        validate(query, originalHql);
        
        copy = query.copy();
        validateHql();
        
        T copyValue = copy.named().get(namedToReturn);
        assertNotEquals(copyValue, query.named().get(namedToReturn));
        return copyValue;
    }
    
    /**
     * Test the query matches the original hql
     * and the copy matches the copy hql.
     */
    protected void validateHql() {
        validate(query, originalHql);
        validate(copy, copyHql);
        
    }
}
