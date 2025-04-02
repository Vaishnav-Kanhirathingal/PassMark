package easter.egg.passmark.utils.security

import easter.egg.passmark.data.models.User
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

// TODO: rename to password data encryption handler
class CryptographyHandler private constructor(
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

    val initializationVectorAsString: String get() = ivSpec.iv.encodeBase64()

    private fun fetchCipher(): Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    // TODO: overload both encrypt and decrypt functions for 'password data' object

    fun encrypt(
        input: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec) }
            .doFinal(input.toByteArray()).encodeBase64()
    }

    fun decrypt(
        encryptedInput: String
    ): String {
        return fetchCipher()
            .also { it.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec) }
            .doFinal(encryptedInput.decodeBase64Bytes())
            .let { String(it) }
    }

    fun getEncryptedPuzzle(): String {
        return encrypt(input = User.PUZZLE_SOLUTION)
    }

    /** returns `true` if puzzle is solved, `false` otherwise */
    fun solvePuzzle(
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