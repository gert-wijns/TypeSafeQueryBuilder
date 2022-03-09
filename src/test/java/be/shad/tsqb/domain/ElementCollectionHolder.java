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
package be.shad.tsqb.domain;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ElementCollectionHolder")
@Getter
@Setter
public class ElementCollectionHolder extends DomainObject {
	public enum PropertyType {
		PLANNING, SALES
	}

	@Embeddable
	@Data
	public static class PropertyValue {
		private String value1;
		private String value2;
	}

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "ELEMENT_COLLECTION_VALUES_BY_ENUM", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyColumn(name = "PROPERTY_TYPE")
	@MapKeyEnumerated(STRING)
	private Map<PropertyType, PropertyValue> propertiesByEnum = new EnumMap<>(PropertyType.class);

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "ELEMENT_COLLECTION_VALUES_BY_STRING", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyColumn(name = "PROPERTY_TYPE")
	private Map<String, PropertyValue> propertiesByString = new HashMap<>();

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "ELEMENT_COLLECTION_SVALUES_BY_ENUM", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyColumn(name = "PROPERTY_TYPE")
	private Map<PropertyType, String> valuesByEnum = new HashMap<>();

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "ELEMENT_COLLECTION_SVALUES_BY_STRING", joinColumns = @JoinColumn(name = "ID"))
	@MapKeyColumn(name = "PROPERTY_TYPE")
	private Map<String, String> valuesByString = new HashMap<>();

}
