package com.boswelja.markdown.material3

import androidx.compose.runtime.Composable

/**
 * A contract that allows launching a URL, ideally from a Compose side-effect. See [launchUrl] for
 * details.
 */
public interface UrlLauncher {

    /**
     * "Launches" the given URL in that platform-preferred manner. For example, on Android a Custom
     * Tab might be launched.
     */
    public fun launchUrl(url: String)
}

/**
 * Remembers the default URL launcher implementation.
 */
@Composable
public expect fun rememberUrlLauncher(): UrlLauncher
