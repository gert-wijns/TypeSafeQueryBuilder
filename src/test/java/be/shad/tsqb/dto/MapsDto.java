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
package be.shad.tsqb.dto;

import java.util.Map;
import java.util.SortedMap;

/**
 * @author Gert
 *
 */
public class MapsDto {
    private MapsDto nestedMaps;
    private SortedMap<String, Object> sortedMap;
    private Map<String, Object> genericMap;
    private CustomMap<String, Object> customMap;

    public MapsDto getNestedMaps() {
        return nestedMaps;
    }

    public void setNestedMaps(MapsDto nestedMaps) {
        this.nestedMaps = nestedMaps;
    }

    public SortedMap<String, Object> getSortedMap() {
        return sortedMap;
    }

    public void setSortedMap(SortedMap<String, Object> sortedMap) {
        this.sortedMap = sortedMap;
    }

    public Map<String, Object> getGenericMap() {
        return genericMap;
    }

    public void setGenericMap(Map<String, Object> genericMap) {
        this.genericMap = genericMap;
    }

    public CustomMap<String, Object> getCustomMap() {
        return customMap;
    }

    public void setCustomMap(CustomMap<String, Object> customMap) {
        this.customMap = customMap;
    }
}
