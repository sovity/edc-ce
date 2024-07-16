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

package de.sovity.edc.extension.db.directaccess;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

@ExtensionPoint
@RequiredArgsConstructor
public class DslContextFactoryImpl implements DslContextFactory {

    private final DataSource dataSource;

    public DSLContext newDslContext() {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }

}
