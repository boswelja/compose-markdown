package com.boswelja.markdown.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.boswelja.markdown.style.RuleStyle

/**
 * Displays a Markdown Rule. A rule is a horizontal line, usually used to separate content.
 */
@Composable
internal fun MarkdownRule(
    ruleStyle: RuleStyle,
    modifier: Modifier = Modifier
) {
    Box(Modifier
        .fillMaxWidth()
        .height(ruleStyle.thickness)
        .background(ruleStyle.color, ruleStyle.shape)
        .then(modifier))
}
