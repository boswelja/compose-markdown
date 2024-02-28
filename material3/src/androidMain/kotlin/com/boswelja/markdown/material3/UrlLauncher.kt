package com.boswelja.markdown.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
public actual fun rememberUrlLauncher(): UrlLauncher {
    val uriHandler = LocalUriHandler.current
    return remember(uriHandler) {
        DefaultUrlLauncher(uriHandler)
    }
}

internal class DefaultUrlLauncher(private val handler: UriHandler) : UrlLauncher {
    override fun launchUrl(url: String) {
        handler.openUri(url)
    }
}
