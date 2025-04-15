package easter.egg.passmark.utils.security.biometrics

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

object BiometricsHandler {
    fun performBiometricAuthentication(
        activity: FragmentActivity,
        onComplete: () -> Unit,
        onSuccess: () -> Unit,
        showToast: (String) -> Unit
    ) {
        BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                    onComplete()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Biometrics failed")
                    onComplete()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("An authentication error has occurred")
                    onComplete()
                }
            }
        ).authenticate(
            BiometricPrompt
                .PromptInfo.Builder()
                .setTitle("Authenticate")
                .setSubtitle("Authenticate to copy password")
                .setNegativeButtonText("Cancel")
                .build()
        )
    }
}