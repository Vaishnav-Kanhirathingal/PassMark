package easter.egg.passmark.utils.functions

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

object SharedFunctions {
    private val TAG = this::class.simpleName

    suspend fun copyToClipboard(
        clipboard: Clipboard,
        text: String,
        context: Context
    ) {
        clipboard.setClipEntry(
            clipEntry = ClipEntry(
                clipData = ClipData.newPlainText(text, text)
            )
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        } else {
            Log.d(TAG, "system has it's own toast")
        }
    }
}