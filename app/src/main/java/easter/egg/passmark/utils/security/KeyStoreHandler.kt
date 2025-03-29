package easter.egg.passmark.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeyStoreHandler(
    private val authId: String
) {
    companion object {
        const val KEYSTORE_NAME = "AndroidKeyStore"
        private const val IV_SIZE = 16

        enum class KeyAliases {
            INTERNAL_DATASTORE;

            fun getKeyName(authId: String): String = "${authId}_${this.name}_KEY"
        }
    }

    private val TAG = this::class.simpleName
    private val keyAlias get() = KeyAliases.INTERNAL_DATASTORE.getKeyName(authId = authId)

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
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_ENCRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true)
//                .setKeySize(256)
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

    private fun getIvParameterSpec(): IvParameterSpec =
        IvParameterSpec(
            MessageDigest.getInstance("SHA-256")
                .digest(authId.padStart(length = 16, padChar = 'P').toByteArray())
                .copyOfRange(0, IV_SIZE)
        )

    private fun fetchCipher(): Cipher {
        return Cipher.getInstance("AES/CBC/PKCS7Padding")
    }

    fun encrypt(input: String): Pair<String, String> {
        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(input.toByteArray())

        return Pair(
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    fun decrypt(
        input: String,
        iv: String
    ): String {
        // TODO:
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val secretKey = keyStore.getKey("MyAESKey", null) as SecretKey

        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedBytes = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))

        return String(decryptedBytes)
    }
}
