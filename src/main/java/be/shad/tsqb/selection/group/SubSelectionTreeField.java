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
package be.shad.tsqb.selection.group;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

import be.shad.tsqb.selection.SelectionTreeData;
import lombok.Value;

@Value
@SuppressWarnings({ "unchecked", "rawtypes" })
class SubSelectionTreeField<T> implements SelectionTreeFieldSetter {
	int subSelectionIndex;
	Supplier<Collection<T>> collectionSupplier;
	Field field;
	boolean identityField;

	public Object setField(SelectionTreeData data, SelectionTreeData[] dataArray, Object[] tuple)
			throws IllegalArgumentException, IllegalAccessException {
		SelectionTreeData subData = dataArray[subSelectionIndex];
		if (subData == null) {
			return null;
		}

		Collection<Object> collection = null;
		if (collectionSupplier != null) {
			collection = (Collection) field.get(data.getCurrentValue());
			if (collection == null) {
				collection = (Collection) collectionSupplier.get();
				field.set(data.getCurrentValue(), collection);
			}
		}

		Object subValue = subData.getBuiltValue();
		if (subValue == null) {
			return null;
		}

		if (collection != null) {
			Map<Object, Object> objects = subData.collectionValues
					.computeIfAbsent(data.getCurrentValue(), v -> new IdentityHashMap<>());
			if (objects.put(subValue, subValue) != null) {
				return null;
			}
			collection.add(subValue);
		} else {
			field.set(data.getCurrentValue(), subValue);
		}

		return subValue;
	}

	@Override
	public boolean isCollectionFieldSetter() {
		return collectionSupplier != null;
	}
}
