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
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector.config;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class TestPorts {
    private static int last = 50000;

    private static Logger logger = Logger.getLogger(TestPorts.class.getCanonicalName());

    /**
     * Get a port range that is available.
     *
     * @param count How many ports should be free.
     * @return The first port in a series of {@code count} consecutive available ports.
     */
    public static synchronized int getFirstPortOfRange(int count) {
        var start = ++last;
        var end = start;

        while (end - start < count) {
            if (available(end)) {
                end++;
            } else {
                end++;
                start = end;
            }
        }

        logger.info("Found free ports: " + start + " - " + end);

        last = end + 1;
        return start;
    }


    /**
     * From <a href="http://svn.apache.org/viewvc/camel/trunk/components/camel-test/src/main/java/org/apache/camel/test/AvailablePortFinder.java?view=markup#l137">Apache Camel</a>
     * <br>
     * <p>
     * Checks to see if a specific port is available.
     *
     * @param port the port number to check for availability
     * @return <tt>true</tt> if the port is available, or <tt>false</tt> if not
     * @throws IllegalArgumentException is thrown if the port number is out of range
     */
    public static boolean available(int port) throws IllegalArgumentException {
        if (port > 65535) {
            throw new IllegalArgumentException("Invalid start currentMinPort: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // Do nothing
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
}
