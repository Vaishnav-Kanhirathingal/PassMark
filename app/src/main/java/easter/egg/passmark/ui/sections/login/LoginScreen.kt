package easter.egg.passmark.ui.sections.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import easter.egg.passmark.BuildConfig
import easter.egg.passmark.R
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import kotlinx.coroutines.launch

object LoginScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        toHomeScreen: () -> Unit
    ) {
        val verticalColumnPadding = 40.dp
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
                Image(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(fraction = 0.4f)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(all = 28.dp),
                    painter = painterResource(id = R.drawable.passmark_app_icon),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "PassMark",
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    text = "Join PassMark and embark on a journey of secure password storage",
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                GoogleSignInButton(toHomeScreen = toHomeScreen)
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
            }
        )
    }

    @Composable
    private fun GoogleSignInButton(
        modifier: Modifier = Modifier,
        toHomeScreen: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        ElevatedButton(
            modifier = modifier.sizeIn(
                minWidth = PassMarkDimensions.minTouchSize,
                minHeight = PassMarkDimensions.minTouchSize
            ),
            onClick = {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.FIREBASE_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                coroutineScope.launch {
                    try {
                        val result = CredentialManager.create(context).getCredential(
                            request = request,
                            context = context,
                        )
                        Log.d(
                            TAG, "credential = ${
                                GsonBuilder().setPrettyPrinting().create()
                                    .toJson(result.credential.data)
                            }"
                        )

                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(data = result.credential.data)
                        val credential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        Firebase.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    toHomeScreen()
                                } else {
                                    TODO("Toast Login Failed")
                                }
                            }
                    } catch (e: GetCredentialCancellationException) {
                        Log.d(TAG, "cancelled sign in")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        TODO()
                    }
                }
            },
            content = {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.google_48),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Google")
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun LoginScreenPrev() {
    LoginScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        toHomeScreen = {}
    )
}