package com.boswelja.markdown.material3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.boswelja.markdown.style.BlockQuoteStyle
import com.boswelja.markdown.style.CodeBlockStyle
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TableStyle
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles

/**
 * Displays a Markdown document with Material 3 styling.
 */
@Composable
public fun MarkdownDocument(
    markdown: String,
    modifier: Modifier = Modifier,
    textStyles: TextStyles = m3TextStyles(),
    textStyleModifiers: TextStyleModifiers = m3TextStyleModifiers(),
    blockQuoteStyle: BlockQuoteStyle = m3BlockQuoteStyle(),
    codeBlockStyle: CodeBlockStyle = m3CodeBlockStyle(),
    ruleStyle: RuleStyle = m3RuleStyle(),
    tableStyle: TableStyle = m3TableStyle(),
    sectionSpacing: Dp = textStyles.textStyle.fontSize.toDp(),
    urlLauncher: UrlLauncher = rememberUrlLauncher()
) {
    com.boswelja.markdown.MarkdownDocument(
        markdown = markdown,
        textStyles = textStyles,
        textStyleModifiers = textStyleModifiers,
        blockQuoteStyle = blockQuoteStyle,
        codeBlockStyle = codeBlockStyle,
        ruleStyle = ruleStyle,
        tableStyle = tableStyle,
        onLinkClick = { urlLauncher.launchUrl(it) },
        modifier = modifier,
        sectionSpacing = sectionSpacing
    )
}

@Composable
internal fun TextUnit.toDp(): Dp {
    return with(LocalDensity.current) {
        this@toDp.toPx().toDp()
    }
}
