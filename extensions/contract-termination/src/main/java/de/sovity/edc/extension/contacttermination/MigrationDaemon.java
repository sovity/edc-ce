/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;


public class MigrationDaemon implements ServiceExtension {

    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;

    @Inject
    private DslContextFactory dslContextFactory;

    @Inject
    private Monitor monitor;

    @Override
    public void start() {
        monitor.info("Starting the contract agreements migration daemon.");
        startMarkingContractsAsTerminated();
    }

    private void startMarkingContractsAsTerminated() {
        Runnable runnable = () -> {
            val toTerminate = dslContextFactory.transactionResult(trx -> {
                val a = Tables.EDC_CONTRACT_AGREEMENT;
                val t = Tables.SOVITY_CONTRACT_TERMINATION;

                monitor.info("Querying existing contracts...");
                return trx.select(a.AGR_ID, a.SOVITY_MARKED_FOR_TERMINATION)
                    .from(a)
                    .leftJoin(t)
                    .on(a.AGR_ID.eq(t.CONTRACT_AGREEMENT_ID))
                    .where(a.SOVITY_MARKED_FOR_TERMINATION.isNull());
            });

            toTerminate.forEach(record -> {
                monitor.info("Terminating " + record.value1() + " as part of the migration to the core EDC v0.7.2");
                dslContextFactory.transaction(trx -> {
                    val termination = new ContractTerminationParam(
                        record.value1(),
                        "Migration",
                        "This contract was terminated because it's not compatible with the EDC v0.7"
                    );
                    contractAgreementTerminationService.terminateAgreementOrThrow(trx, termination);
                });
            });
        };

        runAsDaemon(runnable);
    }

    private static void runAsDaemon(Runnable runnable) {
        val daemon = new Thread(new ThreadGroup("Migration"), runnable);
        daemon.setDaemon(true);
        daemon.start();

        try {
            daemon.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
