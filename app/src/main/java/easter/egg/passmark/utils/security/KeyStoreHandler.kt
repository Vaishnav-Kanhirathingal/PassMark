package easter.egg.passmark.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeyStoreHandler(
    private val authId: String
) {
    companion object {
        const val KEYSTORE_NAME = "AndroidKeyStore"
    }

    private val TAG = this::class.simpleName
    private val keyAlias get() = "${authId}_INTERNAL_DATASTORE_KEY\""

    private fun fetchKeyStore() = try {
        KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    private fun getDataStoreKey(): SecretKey? {
        val keyStore = fetchKeyStore()!!
        return if (keyStore.containsAlias(keyAlias)) {
            try {
                (keyStore.getKey(keyAlias, null) as? SecretKey)!!
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME
        )
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return keyGenerator.generateKey()
    }

    fun initialize() {
        if (getDataStoreKey() == null) {
            generateKey()
            Log.d(TAG, "key generated")
        } else {
            Log.d(TAG, "key present")
        }
    }

    private fun fetchCipher(): Cipher {
        return Cipher.getInstance("AES/CBC/PKCS7Padding")
    }

    /** encrypts given input using the stored key present in keystore
     * @return a pair of <encryptedString, initializationVector>
     */
    fun encrypt(input: String): Pair<String, String> {
        val cipher = fetchCipher().also { it.init(Cipher.ENCRYPT_MODE, getDataStoreKey()) }
        return Pair(
            cipher.doFinal(input.toByteArray()).encodeBase64(),
            cipher.iv.encodeBase64()
        )
    }

    fun decrypt(
        input: String,
        iv: String
    ): String {
        val cipher = fetchCipher().also {
            it.init(
                Cipher.DECRYPT_MODE,
                getDataStoreKey()!!,
                IvParameterSpec(iv.decodeBase64Bytes())
            )
        }
        Log.d(TAG, "init called")
        return String(cipher.doFinal(input.decodeBase64Bytes()))
    }
}