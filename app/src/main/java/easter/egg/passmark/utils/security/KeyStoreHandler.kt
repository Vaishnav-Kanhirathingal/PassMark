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
        val keyStore = fetchKeyStore()
        return if (keyStore!!.containsAlias(keyAlias)) {
            try {
                keyStore.getKey(keyAlias, null) as? SecretKey
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
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
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

    private fun fetchCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    fun encrypt(
        input: String
    ): String? {
        val cipher = fetchCipher()
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getDataStoreKey()!!
        )
        val iv = cipher.iv

        Log.d(TAG, "iv = ${Base64.encodeToString(iv, Base64.DEFAULT)}, size = ${iv.size}")

        return Base64.encodeToString(iv + cipher.doFinal(input.toByteArray()), Base64.DEFAULT)
    }

    fun decrypt(
        input: String
    ): String {
        val bytes = Base64.decode(input, Base64.DEFAULT)
        val iv = bytes.copyOfRange(fromIndex = 0, 16)
        val encryptedData = bytes.copyOfRange(fromIndex = 16, bytes.size)

        val cipher = fetchCipher().also {
            it.init(
                Cipher.DECRYPT_MODE,
                getDataStoreKey(),
                IvParameterSpec(iv)
            )
        }

        return cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT)).toString()
    }

    // Encrypt text using AES
//    fun encrypt(
//        data: String,
//        secretKey: SecretKey = getDataStoreKey()!!
//    ): String {
//        val cipher = fetchCipher()
//        val iv = getIvParameterSpec().iv // Generate IV
//        val ivSpec = IvParameterSpec(iv)
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
//        val encryptedData = cipher.doFinal(data.toByteArray())
////        return Pair(
////            Base64.encodeToString(encryptedData, Base64.DEFAULT),
////            Base64.encodeToString(iv, Base64.DEFAULT)
////        )
//        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
//    }
//
//    // Decrypt text using AES
//    fun decrypt(
//        encryptedData: String,
//        secretKey: SecretKey = getDataStoreKey()!!,
//        iv: String = Base64.encodeToString(getIvParameterSpec().iv, Base64.DEFAULT)
//    ): String {
//        val cipher = fetchCipher()
//        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
//        val decodedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
//        return String(decodedBytes)
//    }

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

}