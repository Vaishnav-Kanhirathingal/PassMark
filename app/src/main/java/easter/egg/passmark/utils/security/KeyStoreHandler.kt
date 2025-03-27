package easter.egg.passmark.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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

    private fun fetchKeyStore() = try {
        KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    private fun getDataStoreKey(): SecretKey? {
        val keyStore = fetchKeyStore()
        val alias = KeyAliases.INTERNAL_DATASTORE.getKeyName(authId = authId)
        return if (keyStore!!.containsAlias(alias)) {
            try {
                keyStore.getKey(alias, null) as? SecretKey
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    fun generateKey(): SecretKey {
        val keyAlias = KeyAliases.INTERNAL_DATASTORE.getKeyName(authId = authId)
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME
        )
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_ENCRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun fetchCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    private fun getIvParameterSpec(): IvParameterSpec =
        IvParameterSpec(
            MessageDigest.getInstance("SHA-256")
                .digest(authId.padStart(length = 16, padChar = 'P').toByteArray())
                .copyOfRange(0, IV_SIZE)
        )

//    fun encrypt(data: String): String {
//        val ivSpec = IvParameterSpec(getIv())
//        val cipher = fetchCipher().also {
//            it.init(Cipher.ENCRYPT_MODE, getDataStoreKey(), ivSpec)
//        }
//        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
//        return Base64.encodeToString(
//            ivSpec.iv + encryptedBytes,
//            Base64.DEFAULT
//        )
//    }
//
//    fun decrypt(encryptedData: String): String {
//        val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
//        val iv = decodedBytes.copyOfRange(0, ivSize) // Extract IV from first 16 bytes
//        val encryptedBytes = decodedBytes.copyOfRange(ivSize, decodedBytes.size)
//
//        val cipher = fetchCipher()
//        val ivSpec = IvParameterSpec(iv)
//        cipher.init(Cipher.DECRYPT_MODE, getDataStoreKey(), ivSpec)
//        val decryptedBytes = cipher.doFinal(encryptedBytes)
//        return String(decryptedBytes, Charsets.UTF_8)
//    }

//    fun encrypt(
//        data: ByteArray,
//    ): ByteArray {
//        val iv = getIv()
//        Log.d(TAG, "iv = ${iv.toString(charset = Charset.defaultCharset())}")
//        val cipher = fetchCipher().also {
//            it.init(
//                Cipher.ENCRYPT_MODE,
//                getDataStoreKey(),
//                IvParameterSpec(iv)
//            )
//        }
//        return cipher.doFinal(data) // IV + Encrypted data
//    }
//
//    fun decrypt(
//        encryptedData: ByteArray,
//    ): ByteArray? = try {
//        val iv = getIv()
//        val cipher = fetchCipher().also {
//            it.init(
//                Cipher.DECRYPT_MODE,
//                getDataStoreKey(),
//                GCMParameterSpec(128, iv)
//            )
//        }
//        cipher.doFinal(encryptedData)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
}