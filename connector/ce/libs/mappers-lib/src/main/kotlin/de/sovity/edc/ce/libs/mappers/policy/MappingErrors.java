/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.policy;

import de.sovity.edc.ce.api.common.model.UiPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Trying to reduce an ODRL {@link org.eclipse.edc.policy.model.Policy} to a
 * {@link UiPolicy} is a lossful operation.
 * <p>
 * During the mapping errors can occur, as parts of the ODRL Policy aren't supported.
 * <p>
 * This class helps to collect those errors.
 */
@RequiredArgsConstructor
public class MappingErrors {
    @Getter
    private final List<String> errors;
    private final String path;

    public static MappingErrors root() {
        return new MappingErrors(new ArrayList<>(), "$");
    }

    public void add(String message) {
        errors.add("%s: %s".formatted(path, message));
    }

    public MappingErrors forChildObject(String name) {
        return new MappingErrors(errors, "%s.%s".formatted(path, name));
    }

    public MappingErrors forChildArrayElement(int index) {
        return new MappingErrors(errors, "%s[%d]".formatted(path, index));
    }
}
