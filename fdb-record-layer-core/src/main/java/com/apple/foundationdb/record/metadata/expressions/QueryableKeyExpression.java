/*
 * QueryableKeyExpression.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2018 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb.record.metadata.expressions;

import com.apple.foundationdb.annotation.API;
import com.apple.foundationdb.record.EvaluationContext;
import com.apple.foundationdb.record.RecordCoreException;
import com.apple.foundationdb.record.metadata.Key;
import com.apple.foundationdb.record.provider.foundationdb.FDBRecord;
import com.apple.foundationdb.record.provider.foundationdb.FDBRecordStoreBase;
import com.google.protobuf.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link KeyExpression} that can be used with a {@link com.apple.foundationdb.record.query.expressions.QueryKeyExpressionWithComparison}.
 *
 * The index entries generated by the key expression are matched to the application of the key expression to the query record,
 * with an optional conversion of any comparison operand.
 */
@API(API.Status.EXPERIMENTAL)
public interface QueryableKeyExpression extends KeyExpression {
    @Nonnull
    String getName();

    @Nullable
    default <M extends Message> Object evalForQuery(@Nonnull FDBRecordStoreBase<M> store, @Nonnull EvaluationContext context, @Nullable FDBRecord<M> record, @Nullable Message message) {
        List<Key.Evaluated> keys = evaluateMessage(record, message);
        if (keys.size() != 1) {
            throw new RecordCoreException("Should evaluate to single key only");
        }
        Key.Evaluated key = keys.get(0);
        if (keys.size() != 1) {
            throw new RecordCoreException("Should evaluate to single key only");
        }
        return key.getObject(0);
    }

    /**
     * Get a function to be applied to the comparison operand before compairing it with the application of the key expression
     * to the record.
     * @return a conversion function or {@code null} for no conversion
     */
    @Nullable 
    default Function<Object, Object> getComparandConversionFunction() {
        return null;
    }
}