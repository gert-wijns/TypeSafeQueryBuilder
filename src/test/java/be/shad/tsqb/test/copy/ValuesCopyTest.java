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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import be.shad.tsqb.domain.people.Person;
import be.shad.tsqb.values.CaseTypeSafeValue;
import be.shad.tsqb.values.CoalesceTypeSafeValue;
import be.shad.tsqb.values.CustomTypeSafeValue;
import be.shad.tsqb.values.DirectTypeSafeStringValue;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValue;
import be.shad.tsqb.values.arithmetic.ArithmeticTypeSafeValueFactory;


public class ValuesCopyTest extends TypeSafeQueryCopyTest {

    @Test
    public void testArithmeticTypeSafeValueCopy() {
        ArithmeticTypeSafeValueFactory ar = query.getArithmeticsBuilder();
        
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        query.named().name(ar.value(personProxy.getId()).add(10), "arithmeticValue");
        query.select(query.named().get("arithmeticValue"));
        
        Person personProxyCopy = validateAndCopy(PERSON_OBJ, 
                "select (hobj1.id + 10) from Person hobj1");
        
        ArithmeticTypeSafeValue val = copy.named().get("arithmeticValue");
        val.add(personProxyCopy.getAge());
        validateChangedCopy("select (hobj1.id + 10 + hobj1.age) from Person hobj1");
    }

    @Test
    public void testCaseTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        CaseTypeSafeValue<Long> caseWhen = query.hqlFunction().caseWhen(Long.class);
        caseWhen.is(10L).when(personProxy.getName()).startsWith().named("nameParam", "An");
        caseWhen.is(personProxy.getId()).otherwise();
        query.select(caseWhen);
        
        validateAndCopy(PERSON_OBJ, 
                "select (case when (hobj1.name like 'An%') then 10 else hobj1.id end) from Person hobj1");
        
        DirectTypeSafeStringValue copiedNameCheck = copy.named().get("nameParam");
        copiedNameCheck.setValue("John");
        validateChangedCopy("select (case when (hobj1.name like 'John%') then 10 else hobj1.id end) from Person hobj1");
    }

    @Test
    public void testCastTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        query.select(query.hqlFunction().cast(personProxy.getId(), Integer.class));

        validateAndCopy(PERSON_OBJ, 
                "select cast(hobj1.id as integer) from Person hobj1");
    }

    @Test
    public void testCoalesceTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        CoalesceTypeSafeValue<Number> originalCoalesce = query.hqlFunction().coalesce(
                (Number) personProxy.getId()).or(personProxy.getAge());
        query.select(query.named().name(originalCoalesce, "coalesceValue"));

        validateAndCopy(PERSON_OBJ, 
                "select coalesce (hobj1.id,hobj1.age) from Person hobj1");
        
        CoalesceTypeSafeValue<Number> copiedCoalesce = copy.named().get("coalesceValue");
        copiedCoalesce.or(10d);
        validateChangedCopy("select coalesce (hobj1.id,hobj1.age,:np1) from Person hobj1", 10d);
    }

    @Test
    public void testCollectionSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        List<String> originalNames = new ArrayList<>(asList("A", "B"));
        query.where(personProxy.getName()).in().named("namesParam", originalNames);

        validateAndCopy(PERSON_OBJ, 
                " from Person hobj1 where hobj1.name in :np1", originalNames);
        
        List<String> copyNames = asList("A", "B", "C");
        copy.named().setValue("namesParam", copyNames);
        validateChangedCopy(" from Person hobj1 where hobj1.name in :np1", copyNames);
    }

    @Test
    public void testCountTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        query.select(query.hqlFunction().count());
        
        Person personProxyCopy = validateAndCopy(PERSON_OBJ, 
                "select count(*) from Person hobj1");
        
        copy.select(copy.hqlFunction().max(personProxyCopy.getId()));
        validateChangedCopy("select count(*), max(hobj1.id) from Person hobj1");
    }

    @Test
    public void testCustomTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
       
        CustomTypeSafeValue<Long> originalValue = query.named().name(query.
                customValue(Long.class, "(hobj1.id)"), "testValue");
        query.select(originalValue);
        
        validateAndCopy(PERSON_OBJ, "select (hobj1.id) from Person hobj1");
        CustomTypeSafeValue<Long> copiedValue = copy.named().get("testValue");
        Assert.assertTrue(copiedValue != null && copiedValue != originalValue);
    }

    @Test
    public void testDirectTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        query.where(personProxy.getId()).eq().named("idParam", 50L);

        validateAndCopy(PERSON_OBJ, " from Person hobj1 where hobj1.id = :np1", 50L);
        
        copy.named().setValue("idParam", 60L);
        validateChangedCopy(" from Person hobj1 where hobj1.id = :np1", 60L);
    }

    @Test
    public void testStringDirectTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        query.where(personProxy.getName()).eq().named("nameParam", "Peter");

        validateAndCopy(PERSON_OBJ, " from Person hobj1 where hobj1.name = :np1", "Peter");
        
        DirectTypeSafeStringValue nameParam = copy.named().get("nameParam");
        nameParam.setValue("Petra");
        validateChangedCopy(" from Person hobj1 where hobj1.name = :np1", "Petra");
    }

    @Test
    public void testDistinctTypeSafeValueCopy() {
        Person personProxy = query.from(Person.class);
        query.named().name(personProxy, PERSON_OBJ);
        
        query.select(query.hqlFunction().distinct(personProxy.getName()));
        
        validateAndCopy(PERSON_OBJ, "select distinct hobj1.name from Person hobj1");
    }
}
