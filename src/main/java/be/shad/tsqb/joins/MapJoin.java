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

/**
 * This object represents the {@code key} and {@code value}
 * when performing an element collection join.
 */
public interface MapJoin<K, V> {
	/**
	 * The map key, can be used in a joinWith() for further filtering on specific map keys
	 * or for selecting the map key in the result.
	 */
	K getKey();

	/**
	 * The map value, can be used for filtering and selection of the element collection map value.
	 */
	V getValue();
}
