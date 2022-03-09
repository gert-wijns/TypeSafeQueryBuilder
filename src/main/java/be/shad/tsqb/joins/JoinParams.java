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

import be.shad.tsqb.query.JoinType;

public interface JoinParams {
	JoinType getJoinType();

	default String getName() {
		return null;
	}

	default boolean isCreateAdditionalJoin() {
		return false;
	}

	/**
	 * Create params with default jointype (resolves to no join if only the primary key of the referred entity is used is selected).
	 */
	static JoinParamsImpl.JoinParamsImplBuilder defaultJoin() {
		return JoinParamsImpl.builder().joinType(JoinType.Default);
	}

	/**
	 * Create params with inner jointype, to fetch followup with .fetch().
	 */
	static JoinParamsImpl.JoinParamsImplBuilder innerJoin() {
		return JoinParamsImpl.builder().joinType(JoinType.Inner);
	}

	/**
	 * Create params with left jointype, to fetch followup with .fetch().
	 */
	static JoinParamsImpl.JoinParamsImplBuilder leftJoin() {
		return JoinParamsImpl.builder().joinType(JoinType.Left);
	}

	/**
	 * Create params with inner jointype, fetching is not allowed for this jointype..
	 */
	static JoinParamsImpl.JoinParamsImplBuilder rightJoin() {
		return JoinParamsImpl.builder().joinType(JoinType.Right);
	}
}
