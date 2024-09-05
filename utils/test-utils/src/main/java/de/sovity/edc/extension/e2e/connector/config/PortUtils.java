/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.e2e.connector.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import static org.eclipse.edc.util.io.Ports.MAX_TCP_PORT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortUtils {

    private static final Random RANDOM = new Random();

    public static synchronized int getFreePortRange(int size) {
        // pick a random in a reasonable range
        int firstPort = RANDOM.nextInt(10_000, 50_000);

        int currentPort = firstPort;
        do {
            if (canUsePort(currentPort)) {
                currentPort++;
            } else {
                firstPort = currentPort++;
            }
        } while (currentPort <= firstPort + size);

        return firstPort;
    }

    private static boolean canUsePort(int port) {

        if (port <= 0 || port >= MAX_TCP_PORT) {
            throw new IllegalArgumentException("Lower bound must be > 0 and < " + MAX_TCP_PORT + " and be < upperBound");
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
