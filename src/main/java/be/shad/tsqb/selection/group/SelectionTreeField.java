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
import java.util.Map;

import be.shad.tsqb.selection.SelectionTreeData;
import be.shad.tsqb.selection.SelectionValueTransformer;
import lombok.Value;

@Value
@SuppressWarnings({ "unchecked", "rawtypes" })
class SelectionTreeField implements SelectionTreeFieldSetter {
	SelectionValueTransformer valueTransformer;
	Field field;
	String mapSelectionKey;
	int tupleValueIndex;
	boolean identityField;

	public Object setField(SelectionTreeData data, SelectionTreeData[] dataArray, Object[] tuple)
			throws IllegalArgumentException, IllegalAccessException {
		Object value = tuple[tupleValueIndex];
		if (valueTransformer != null) {
			value = valueTransformer.convert(value);
		}
		if (mapSelectionKey != null) {
			((Map) data.getCurrentValue()).put(mapSelectionKey, value);
		} else {
			field.set(data.getCurrentValue(), value);
		}
		return value;
	}

	@Override
	public boolean isCollectionFieldSetter() {
		return false;
	}
}
