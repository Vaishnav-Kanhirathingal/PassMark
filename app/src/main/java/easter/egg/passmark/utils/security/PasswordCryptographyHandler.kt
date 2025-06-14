package easter.egg.passmark.utils.security

import com.google.gson.Gson
import easter.egg.passmark.data.models.User
import easter.egg.passmark.data.models.password.sensitive.SensitiveContent
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class PasswordCryptographyHandler private constructor(
    private val secretKeySpec: SecretKeySpec,
    private val ivSpec: IvParameterSpec
) {
    constructor(
        password: String,
        initializationVector: String
    ) : this(
        password = password,
        initializationVector = initializationVector.decodeBase64Bytes()
    )

    constructor(
        password: String,
        initializationVector: ByteArray
    ) : this(
        secretKeySpec = SecretKeySpec(
            MessageDigest.getInstance("SHA-256")
                .digest(password.toByteArray())
                .copyOfRange(0, 16), "AES"
        ),
        ivSpec = IvParameterSpec(initializationVector)
    )

    companion object {
        fun getNewInitializationVector(): ByteArray =
            ByteArray(size = 16).also { SecureRandom().nextBytes(it) }

        fun verifyPassword(
            password: String,
            cryptographyHandler: PasswordCryptographyHandler
        ): Boolean {
            val newHandler = PasswordCryptographyHandler(
                password = password,
                initializationVector = cryptographyHandler.initializationVectorAsString
            )
            return newHandler.solvesPuzzle(apiProvidedEncryptedPuzzle = cryptographyHandler.getEncryptedPuzzle())
        }
    }

    val initializationVectorAsString: String get() = ivSpec.iv.encodeBase64()

    private fun fetchCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    private fun encrypt(
        input: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec) }
            .doFinal(input.toByteArray()).encodeBase64()
    }

    private fun decrypt(
        encryptedInput: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec) }
            .doFinal(encryptedInput.decodeBase64Bytes())
            .let { String(it) }
    }

    fun encryptSensitiveContent(sensitiveContent: SensitiveContent): String {
        return encrypt(input = Gson().toJson(sensitiveContent))
    }

    fun decryptSensitiveContent(passwordData: String): SensitiveContent {
        return Gson().fromJson(
            this.decrypt(encryptedInput = passwordData),
            SensitiveContent::class.java
        )
    }

    fun getEncryptedPuzzle(): String {
        return encrypt(input = User.PUZZLE_SOLUTION)
    }

    /** returns `true` if puzzle is solved, `false` otherwise */
    fun solvesPuzzle(
        apiProvidedEncryptedPuzzle: String
    ): Boolean {
        return try {
            (decrypt(apiProvidedEncryptedPuzzle) == User.PUZZLE_SOLUTION)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

//fun main() {
//    val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
//    val cryptographyHandler = CryptographyHandler(
//        password = "some password",
//        initializationVector = iv
//    )
//
//    println(
//        cryptographyHandler.decrypt(
//            encryptedInput = cryptographyHandler.encrypt(
//                input = "sample"
//            )
//        )
//    )
//
//    println("something else".toByteArray().encodeBase64().decodeBase64String())
//    println(
//        mutableListOf(83, 111, 109, 101, 116, 104, 105, 110, 103, 32, 101, 108, 115, 101)
//            .map { it.toByte() }
//            .toByteArray()
//
//            .encodeBase64() //string
//
//            .decodeBase64Bytes()
//            .let { String(it) }
//
//    )
//}