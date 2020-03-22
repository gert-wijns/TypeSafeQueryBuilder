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
package be.shad.tsqb.selection;

import java.util.List;

import org.hibernate.transform.BasicTransformerAdapter;

/**
 * Support value converter when working without a selection dto.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class WithoutAliasesQueryResultTransformer extends BasicTransformerAdapter {
    private static final long serialVersionUID = 942223288493516089L;

    private final SelectionValueTransformer[] transformers;

    public WithoutAliasesQueryResultTransformer(List<SelectionValueTransformer<?, ?>> transformers) {
        this.transformers = transformers.toArray(new SelectionValueTransformer[0]);
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        for(int i=0; i < tuple.length; i++) {
            if (transformers[i] != null) {
                tuple[i] = transformers[i].convert(tuple[i]);
            }
        }
        return tuple.length == 1 ? tuple[0]: tuple;
    }

}
