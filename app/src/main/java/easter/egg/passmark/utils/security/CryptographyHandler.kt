package easter.egg.passmark.utils.security

import easter.egg.passmark.data.models.User
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptographyHandler(
    password: String,
    initializationVector: ByteArray,
) {
    private val secretKeySpec = SecretKeySpec(
        MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .copyOfRange(0, 16), "AES"
    )
    private val iv = IvParameterSpec(initializationVector)

    private fun fetchCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    fun encrypt(
        input: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv) }
            .doFinal(input.toByteArray()).encodeBase64()
    }

    fun decrypt(
        encryptedInput: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.DECRYPT_MODE, secretKeySpec, iv) }
            .doFinal(encryptedInput.decodeBase64Bytes())
            .let { String(it) }
    }

    fun getEncryptedPuzzle(): String {
        return encrypt(input = User.PUZZLE_SOLUTION)
    }

    fun checkValidation(
        apiProvidedEncryptedPuzzle: String
    ): Boolean = (decrypt(apiProvidedEncryptedPuzzle) == User.PUZZLE_SOLUTION)
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
//}