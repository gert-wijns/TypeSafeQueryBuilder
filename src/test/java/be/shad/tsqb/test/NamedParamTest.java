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
package be.shad.tsqb.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.people.Person;

/**
 * Extra named param tests, basic chainables with naming params is tested in the OnGoing...Tests
 */
public class NamedParamTest extends TypeSafeQueryTest {
    private String NAMED_PARAM_1 = "NAMED_PARAM_1";
    
    /**
     * The collection should expect a number, because ID is a number.
     * Setting a string should result in an exception.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNamedParamValueTypeValidation() {
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).in().named(NAMED_PARAM_1);
        query.namedValue(NAMED_PARAM_1, "Astring");
    }

    /**
     * The collection should expect a number, because ID is a number.
     * Setting a string should result in an exception.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNamedParamValueInCollectionTypeValidation() {
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).in().named(NAMED_PARAM_1);
        query.namedValue(NAMED_PARAM_1, Arrays.asList(10d, "Moo"));
    }

    /**
     * Just to test it doesn't simply always throw an exception
     */
    @Test
    public void testNamedParamValueInCollectionTypeValidationPassesWhenCorrect() {
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).in().named(NAMED_PARAM_1);
        query.namedValue(NAMED_PARAM_1, Arrays.asList(10d, 20d));
    }

    /**
     * When checking a single value, for example eq(...), 
     * then it is not allowed to set a collection.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNamedParamCollectionNotAllowedForSingleValuesValidation() { 
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).eq().named(NAMED_PARAM_1);
        query.namedValue(NAMED_PARAM_1, Arrays.asList(10D));
    }

    /**
     * Setting a named value which was not named is not allowed.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNamedValueMustExistValidation() { 
        query.namedValue(NAMED_PARAM_1, Arrays.asList(10D));
    }

    /**
     * Check that duplicate aliases are not allowed.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNamedValueNotDuplicateValidation() { 
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).eq().named(NAMED_PARAM_1);
        query.where(personProxy.getName()).eq().named(NAMED_PARAM_1);
    }

    /**
     * It is possible to provide the value in the named(..), 
     * then it is set as initial value (though still changeable)
     */
    @Test
    public void testNamedParamWithImmediateValue() {
        List<Long> ids = Arrays.asList(10L, 5L);
        Person personProxy = query.from(Person.class);
        query.where(personProxy.getId()).in().named(NAMED_PARAM_1, ids);
        
        validate(" from Person hobj1 where hobj1.id in :np1", ids);
    }
    
}
