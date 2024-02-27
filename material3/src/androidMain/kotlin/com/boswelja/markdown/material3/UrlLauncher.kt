package com.boswelja.markdown.material3

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
public actual fun rememberUrlLauncher(): UrlLauncher {
    val context = LocalContext.current
    return remember(context) {
        DefaultUrlLauncher(context)
    }
}

internal class DefaultUrlLauncher(private val context: Context) : UrlLauncher {
    override fun launchUrl(url: String) {
        val intent = CustomTabsIntent.Builder()
            .build()
        intent.launchUrl(context, Uri.parse(url))
    }
}
