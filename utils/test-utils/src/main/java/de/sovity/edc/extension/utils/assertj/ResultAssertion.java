/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.utils.assertj;

import org.assertj.core.api.AbstractAssert;
import org.eclipse.edc.spi.result.Result;

public class ResultAssertion<T> extends AbstractAssert<ResultAssertion<T>, Result<T>> {

    public ResultAssertion(Result<T> actual) {
        super(actual, ResultAssertion.class);
    }

    public static <T> ResultAssertion<T> assertThat(Result<T> actual) {
        return new ResultAssertion<>(actual);
    }

    public ResultAssertion<T> isSuccess() {
        isNotNull();
        if (!actual.succeeded()) {
            failWithMessage("Expected result to be successful.");
        }
        return this;
    }

    public ResultAssertion<T> isFailure() {
        isNotNull();
        if (!actual.failed()) {
            failWithMessage("Expected result to be failed.");
        }
        return this;
    }
}
