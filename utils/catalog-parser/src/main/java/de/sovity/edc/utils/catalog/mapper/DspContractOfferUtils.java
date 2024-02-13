package de.sovity.edc.utils.catalog.mapper;

import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DspContractOfferUtils {

    /**
     * /!\ Workaround
     * <p>
     * The Eclipse EDC uses a new random UUID for each policy that it returns and in turn a new contract ID.
     * This Eclipse ID can't be used as such.
     * As a workaround, we must introduce our own ID.
     * For a first iteration, we will assume that the content of the policy remains the same (same content, same order)
     * and hash it to use it as a key.
     *
     * @param contract The contract to compute an ID from
     * @return A base64 string that can be used as an id for the {@code contract}
     */
    public static String buildStableId(JsonObject contract) {
        // FIXME: This doesn't enforce any property order and may cause trouble if the returned policy schema is not consistent.
        //  Use canonical form if needed later.
        val noId = Json.createObjectBuilder(contract).remove(Prop.ID).build();
        val policyId = hash(noId);

        val currentId = ContractId.parseId(JsonLdUtils.string(contract, Prop.ID))
                .orElseThrow((failure) -> {
                    throw new RuntimeException("Failed to parse the contract id: " + failure.getFailureDetail());
                });

        return currentId.definitionPart() + ":" + currentId.assetIdPart() + ":" + policyId;
    }

    @NotNull
    private static String hash(JsonObject noId) {
        val policyJsonString = JsonUtils.toJson(noId);
        val sha1 = sha1(policyJsonString);
        // encoding with base16 to make the hash readable to humans (similarly to how the random UUID would have been readable)
        val base16 = toBase16(sha1);
        return toBase64(base16);
    }

    @NotNull
    private static String toBase64(String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = Base64.getEncoder().encode(stringBytes);
        return new String(bytes);
    }

    @NotNull
    private static String toBase16(byte[] bytes) {
        val sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Character.forDigit(b >> 4 & 0xf, 16));
            sb.append(Character.forDigit(b & 0xf, 16));
        }
        return sb.toString();
    }

    private static byte[] sha1(String string) {
        try {
            return MessageDigest.getInstance("sha-1").digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
