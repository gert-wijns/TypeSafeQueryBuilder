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
package be.shad.tsqb.joins;

import be.shad.tsqb.data.TypeSafeQueryProxyData;
import be.shad.tsqb.query.TypeSafeQueryInternal;
import be.shad.tsqb.values.ReferenceTypeSafeValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MapJoinImpl<K, V> implements MapJoinInternal<K, V> {
	private final TypeSafeQueryInternal query;
	private final TypeSafeQueryProxyData data;

	@Override
	public K getKey() {
		query.getRootQuery().queueValueSelected(query.customValue(Object.class,
				"index(" + data.getAlias() + ")"));
		return null;
	}

	@Override
	public V getValue() {
		if (data.getProxyAs() == null) {
			query.getRootQuery().queueValueSelected(new ReferenceTypeSafeValue<>(query, data));
		}
		return data.getProxyAs();
	}

	@Override
	public TypeSafeQueryProxyData getTypeSafeQueryProxyData() {
		return data;
	}
}
