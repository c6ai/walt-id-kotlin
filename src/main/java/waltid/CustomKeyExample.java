package waltid;

import id.walt.crypto.keys.JavaKey;
import id.walt.crypto.keys.Key;
import id.walt.crypto.keys.KeyMeta;
import id.walt.crypto.keys.KeyType;
import id.walt.crypto.keys.jwk.JWKKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.random.Random;
import kotlinx.serialization.json.JsonElement;
import kotlinx.serialization.json.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomKeyExample extends JavaKey {

    private String _xyz;

    public CustomKeyExample(String xyz) {
        this._xyz = xyz;
    }


    private byte[] reverseArray(byte[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            byte temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
        return arr;
    }

    @NotNull
    @Override
    public KeyMeta javaGetMeta() {
        return null;
    }

    @NotNull
    @Override
    public byte[] javaGetPublicKeyRepresentation() {
        return new byte[0];
    }

    @NotNull
    @Override
    public Key javaGetPublicKey() {
        return (Key) Objects.requireNonNull(JWKKey.Companion.generate(getKeyType(), null, new Continuation<JWKKey>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;

            }

            @Override
            public void resumeWith(@NotNull Object result) {
                System.out.println("Generated key: " + result);
            }
        }));
    }

    public byte[] javaVerifyRaw(@NotNull byte[] signed, @Nullable byte[] detachedPlaintext) {
        System.out.println("Verifying signed with custom key (example): " + Arrays.toString(signed));

        assert detachedPlaintext != null;
        if (signed == reverseArray(detachedPlaintext)) {
            return reverseArray(detachedPlaintext);
        } else {
            throw new IllegalArgumentException("Invalid signature!");
        }

    }

    @NotNull
    @Override
    public JsonElement javaVerifyJws() {
        return null;
    }

    @NotNull
    @Override
    public byte[] javaVerifyRaw() {
        return new byte[0];
    }

//    @NotNull
//    @Override
//    public JsonElement javaVerifyJws(@Language(value = "json") @NotNull String signedJws) {
//        if (Random.Default.nextBoolean()) {
//            return Json.Default.parseToJsonElement(signedJws);
//        } else {
//            throw new IllegalArgumentException("Illegal signature!");
//        }
//    }

    @NotNull
    @Override
    public String javaSignJws(@NotNull byte[] plaintext, @NotNull Map<String, String> headers) {
        var base64 = Base64.getUrlEncoder();

        String myHeaders = base64.encodeToString("{\"my-headers\": \"xyz\"}".getBytes(StandardCharsets.UTF_8));
        String myPayload = base64.encodeToString("{\"my-payload\": \"xyz\"}".getBytes(StandardCharsets.UTF_8));
        String mySignature = base64.encodeToString( javaSignRaw(plaintext));

        return myHeaders + "." + myPayload + "." + mySignature;
    }

    @NotNull
    @Override
    public byte[] javaSignRaw(@NotNull byte[] plaintext) {
        System.out.println("Signing plaintext with custom key (stub implementation): " + Arrays.toString(plaintext));
        return reverseArray(plaintext);
    }

    @NotNull
    @Override
    public String javaExportPEM() {
        throw new IllegalArgumentException("Not yet implemented!");
        // return null;
    }

    @NotNull
    @Override
    public JsonObject javaExportJWKObject() {
        throw new IllegalArgumentException("Not yet implemented!");
        // return null;
    }

    @NotNull
    @Override
    public String javaExportJWK() {
        return "eyJhbGciOiJIUzI1NiJ9...";
    }

    @NotNull
    @Override
    public String javaGetThumbprint() {
        return java.util.UUID.randomUUID().toString();
    }

    @NotNull
    @Override
    public String javaGetKeyId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public boolean javaHasPrivateKey() {
        return false;
    }

    @NotNull
    @Override
    public KeyType javaGetKeyType() {
        return switch (Random.Default.nextInt(0, 4)) {
            case 0 -> KeyType.RSA;
            case 1 -> KeyType.Ed25519;
            case 2 -> KeyType.secp256r1;
            case 3 -> KeyType.secp256k1;
            default -> throw new IllegalArgumentException("Unknown key type!");
        };
    }

    public static void main(String[] args) {
        var key = new CustomKeyExample("abc");
        System.out.println("A type: " + key.getKeyType());

        var plaintext = "plaintext".getBytes(StandardCharsets.UTF_8);
        System.out.println("Plaintext: " + new String(plaintext , StandardCharsets.UTF_8));

        var signed = (byte[]) key.javaSignRaw(plaintext);
        System.out.println("Stub signed: " + new String(signed , StandardCharsets.UTF_8));

        var verify = key.javaVerifyRaw(signed, plaintext);
        System.out.println("Stub verified: " + new String(verify , StandardCharsets.UTF_8));


    }




}
