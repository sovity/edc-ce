/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.utils

object TestKeypairs {
    val dummyEdrEncryptionKeypair = KeypairInPkcs(
        privateKey = """
            -----BEGIN PRIVATE KEY-----
            MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgSVkfyOqQ4E6No+v6
            h/wotfYuGqiqierJ2YXx2v3rP1GhRANCAASMlEMAwv9jf8FAKDAxrnPGWVGBBzbD
            wt3VQrrM5i/DOwCzF1XH7v6iYbvpYe9P0Qvf5ndqYYBklqLkXHAR37Vz
            -----END PRIVATE KEY-----
        """.trimIndent(),
        certificate = """
            -----BEGIN CERTIFICATE-----
            MIICkDCCAjegAwIBAgIUCL+kBzVdqMGzurpuYwIxkuLbYrgwCgYIKoZIzj0EAwIw
            czELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNh
            biBGcmFuY2lzY28xGTAXBgNVBAoTEG9yZzEuZXhhbXBsZS5jb20xHDAaBgNVBAMT
            E2NhLm9yZzEuZXhhbXBsZS5jb20wHhcNMTkwMjA1MDgyMjAwWhcNMjAwMjA1MDgy
            NzAwWjBDMTAwDQYDVQQLEwZjbGllbnQwCwYDVQQLEwRvcmcxMBIGA1UECxMLZGVw
            YXJ0bWVudDExDzANBgNVBAMTBm5hdGhhbjBZMBMGByqGSM49AgEGCCqGSM49AwEH
            A0IABIyUQwDC/2N/wUAoMDGuc8ZZUYEHNsPC3dVCuszmL8M7ALMXVcfu/qJhu+lh
            70/RC9/md2phgGSWouRccBHftXOjgdgwgdUwDgYDVR0PAQH/BAQDAgeAMAwGA1Ud
            EwEB/wQCMAAwHQYDVR0OBBYEFJYSgUGno6j2eYUKjLs9BRzreUY1MCsGA1UdIwQk
            MCKAIEI5qg3NdtruuLoM2nAYUdFFBNMarRst3dusalc2Xkl8MGkGCCoDBAUGBwgB
            BF17ImF0dHJzIjp7ImhmLkFmZmlsaWF0aW9uIjoib3JnMS5kZXBhcnRtZW50MSIs
            ImhmLkVucm9sbG1lbnRJRCI6Im5hdGhhbiIsImhmLlR5cGUiOiJjbGllbnQifX0w
            CgYIKoZIzj0EAwIDRwAwRAIgbYQ4UscWT5rgqLwrhcj8kRNN0kfA5n12Zpl1Fclw
            +7QCIAlTx9oMsGBAeaNxJ3PV6mo9Zng5aMNnAmwW2PVcDlXt
            -----END CERTIFICATE-----
        """.trimIndent()
    )

    data class KeypairInPkcs(
        val certificate: String,
        val privateKey: String
    )
}

