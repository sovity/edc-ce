/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.security.KeyPair
import java.security.SecureRandom
import java.security.Security
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

class SecretUtils {

    init {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    data class KeysAndCertificate(
        val privateKeyPem: String,
        val certificatePem: String,
    )

    fun generateKeysAndCertificate(method: SupportedCertificateMethod, keyId: String): KeysAndCertificate {
        val key = getRsaKeyPair(method, keyId)
        val privateKeyPem = toPemFileFormat(PemObject("PRIVATE KEY", key.toPrivateKey().encoded))
        val certificate = generateSelfSignedCertificate(
            key.toKeyPair(),
            "CN=company-name",
            validityDays = 365
        )
        val certificatePem = toPemFileFormat(PemObject("CERTIFICATE", certificate.encoded))
        return KeysAndCertificate(privateKeyPem, certificatePem)
    }

    private fun getRsaKeyPair(method: SupportedCertificateMethod, keyId: String): RSAKey {
        return when (method) {
            SupportedCertificateMethod.RSA4096 ->
                RSAKeyGenerator(4096).keyID(keyId).generate()

            SupportedCertificateMethod.RSA2048 ->
                RSAKeyGenerator(2048).keyID(keyId).generate()
        }
    }

    private fun generateSelfSignedCertificate(
        keyPair: KeyPair,
        subjectDomain: String,
        validityDays: Long
    ): X509Certificate {
        val now = Instant.now()
        val expiry = now.plus(validityDays, ChronoUnit.DAYS)
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        val serial = BigInteger(bytes)

        val certBuilder = JcaX509v3CertificateBuilder(
            X500Name(subjectDomain),
            serial,
            Date.from(now),
            Date.from(expiry),
            X500Name(subjectDomain),
            keyPair.public
        )

        certBuilder.addExtension(
            Extension.keyUsage,
            true,
            KeyUsage(KeyUsage.digitalSignature)
        )

        certBuilder.addExtension(
            Extension.basicConstraints,
            true,
            BasicConstraints(false)  // Not a CA
        )

        val extensionUtils = JcaX509ExtensionUtils()
        val subjectKeyId = extensionUtils.createSubjectKeyIdentifier(keyPair.public)
        certBuilder.addExtension(
            Extension.subjectKeyIdentifier,
            false,
            subjectKeyId
        )
        val authorityKeyId = extensionUtils.createAuthorityKeyIdentifier(keyPair.public)
        certBuilder.addExtension(
            Extension.authorityKeyIdentifier,
            false,
            authorityKeyId
        )

        val contentSigner = JcaContentSignerBuilder("sha256WithRSAEncryption")
            .setProvider("BC")
            .build(keyPair.private)

        return JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certBuilder.build(contentSigner))
    }

    private fun toPemFileFormat(pemObject: PemObject): String {
        val byteStream = ByteArrayOutputStream()

        byteStream.use { byteStream ->
            PemWriter(OutputStreamWriter(byteStream)).use { pemWriter ->
                pemWriter.writeObject(pemObject)
            }
        }

        return String(bytes = byteStream.toByteArray())
    }
}
