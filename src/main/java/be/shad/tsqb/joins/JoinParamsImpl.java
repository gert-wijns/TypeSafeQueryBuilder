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
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JoinParamsImpl implements JoinParams {
	JoinType joinType;
	String name;
	boolean createAdditionalJoin;

	static JoinParamsImpl.JoinParamsImplBuilder builder() {
		return new JoinParamsImpl.JoinParamsImplBuilder();
	}

	public static class JoinParamsImplBuilder {
		private JoinType joinType = JoinType.Default;
		private boolean createAdditionalJoin;

		public JoinParamsImpl.JoinParamsImplBuilder fetch() {
			switch (joinType) {
				case Left:
				case LeftFetch:
					return joinType(JoinType.LeftFetch);
				case Inner:
				case Fetch:
				case Default:
					return joinType(JoinType.Fetch);
				case None:
				case Right:
				default:
					throw new IllegalArgumentException("Can't fetch on: " + joinType);
			}
		}
		public JoinParamsImpl.JoinParamsImplBuilder createAdditionalJoin() {
			return createAdditionalJoin(true);
		}

		JoinParamsImpl.JoinParamsImplBuilder createAdditionalJoin(boolean createAdditionalJoin) {
			this.createAdditionalJoin = createAdditionalJoin;
			return this;
		}

		JoinParamsImpl.JoinParamsImplBuilder joinType(JoinType joinType) {
			this.joinType = joinType;
			return this;
		}
	}
}
