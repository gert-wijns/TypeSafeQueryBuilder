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
package be.shad.tsqb.test.restrictions;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import be.shad.tsqb.domain.Building;
import be.shad.tsqb.domain.usertype.TextWrappingObject;
import be.shad.tsqb.test.TypeSafeQueryTest;

public class OnGoingObjectRestrictionTest extends TypeSafeQueryTest {

    @Test
    public void testIsNull() {
        Building building = query.from(Building.class);
        query.where(query.toValue(building.getText())).isNull();
        validate(" from Building hobj1 where hobj1.text is null ");
    }

    @Test
    public void testIsNotNull() {
        Building building = query.from(Building.class);
        query.where(query.toValue(building.getText())).isNotNull();
        validate(" from Building hobj1 where hobj1.text is not null ");
    }

    @Test
    public void testTypeSafeValueInCollection() {
        Building building = query.from(Building.class);
        List<TextWrappingObject> names = Arrays.asList(
                new TextWrappingObject("Jos"),
                new TextWrappingObject("Marie"),
                new TextWrappingObject("Katrien"));
        query.where(query.toValue(building.getText())).in(names);
        validate(" from Building hobj1 where hobj1.text in (:np1)", names);
    }
}