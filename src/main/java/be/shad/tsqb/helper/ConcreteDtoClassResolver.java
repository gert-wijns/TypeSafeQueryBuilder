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
package be.shad.tsqb.helper;

public interface ConcreteDtoClassResolver {
    /**
     * Resolves the actual class to use when the requested class is an interface.
     * Used to use HashMap when the field is a Map for example.
     */
    <T> Class<T> getConcreteClass(Class<T> requestedClass);

	/**
	 * Generate the builder spec which will be used during result transformation.
	 */
	<SB, SR> SelectionBuilderSpec<SB, SR> createBuilderSpec(Class<SB> builderClass);
}
