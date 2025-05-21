/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db.jooq;

import org.eclipse.edc.spi.EdcException;

// Needs to extend EdcException or it will get wrapped by the EDC exception handler
public class RollbackException extends EdcException {
    public RollbackException() {
        super("Rolled back.");
    }
}
