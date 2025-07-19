package easter.egg.passmark.utils.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

interface Describable {
    val desc: String

    companion object {
        fun Modifier.setDescription(describable: Describable): Modifier {
            return this
                .semantics { this.contentDescription = describable.desc }
                .testTag(tag = describable.desc)
        }
    }
}