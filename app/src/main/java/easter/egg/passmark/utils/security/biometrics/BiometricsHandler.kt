package easter.egg.passmark.utils.security.biometrics

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

object BiometricsHandler {
    enum class BiometricHandlerOutput {
        AUTHENTICATED, FAILED, ERROR;

        fun handleToast(
            context: Context,
            successMessage: String? = "Authentication success"
        ) {
            when (this) {
                ERROR -> "Biometrics error has occurred"
                FAILED -> "Failed to verify biometrics"
                AUTHENTICATED -> successMessage
            }?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun showBiometricsNotPresentToast(context: Context) {
        Toast.makeText(context, "Biometrics not present on device", Toast.LENGTH_SHORT).show()
    }

    /** handles settings opening if biometrics not set */
    fun performBiometricAuthentication(
        context: Context,
        activity: FragmentActivity,
        title: String = "Authenticate",
        subtitle: String,
        onComplete: (BiometricHandlerOutput) -> Unit,
        onBiometricsNotPresent: () -> Unit = { showBiometricsNotPresentToast(context = context) }
    ) {
        when (hasFingerprintSetup(context = context)) {
            true -> {
                BiometricPrompt(
                    activity,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            onComplete(BiometricHandlerOutput.AUTHENTICATED)
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            onComplete(BiometricHandlerOutput.FAILED)
                        }

                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            onComplete(BiometricHandlerOutput.ERROR)
                        }
                    }
                ).authenticate(
                    BiometricPrompt
                        .PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        .setNegativeButtonText("Cancel")
                        .build()
                )
            }

            false -> openSettings(context = context)
            null -> onBiometricsNotPresent()
        }
    }

    /** @return `true` if setup, `false` if present but not setup and `null` if biometrics not present */
    private fun hasFingerprintSetup(context: Context): Boolean? {
        return when (
            BiometricManager.from(context)
                .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> null
        }
    }

    private fun openSettings(context: Context) {
        if (Build.VERSION.SDK_INT > 29) {
            try {
                val enrollIntent =
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                context.startActivity(enrollIntent)
                Toast.makeText(
                    context,
                    "Biometrics not enabled on device. Go to Settings -> Security -> Fingerprint -> set fingerprint",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}