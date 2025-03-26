package easter.egg.passmark.utils.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyHandler {
    companion object {
        const val KEYSTORE_NAME = "KEYSTORE_NAME"
        const val PUZZLE = "Some random puzzle"

        enum class KeyAliases {
            INTERNAL_DATASTORE, DATABASE;

            fun getKeyName(authId: String): String = "${authId}_${this.name}_KEY"
        }
    }

    private val keyStore = KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }

    fun getDataStoreKey(authId: String): Key? {
        val alias = KeyAliases.INTERNAL_DATASTORE.getKeyName(authId = authId)
        return if (keyStore.containsAlias(alias)) {
            try {
                keyStore.getKey(alias, null)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    fun getOrGenerateKey(
        authId: String
    ) {
        try {
            val keyAlias = KeyAliases.INTERNAL_DATASTORE.getKeyName(authId = authId)

            if (keyStore.containsAlias(keyAlias)) {
                keyStore.getKey(keyAlias, null) as SecretKey
            } else {
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
                keyGenerator.generateKey()
            }

        }
    }
}