package com.boswelja.markdown.material3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.nasdroid.core.markdown.style.BlockQuoteStyle
import com.nasdroid.core.markdown.style.CodeBlockStyle
import com.nasdroid.core.markdown.style.TextStyleModifiers
import com.nasdroid.core.markdown.style.TextStyles

/**
 * Displays a Markdown document with Material 3 styling.
 */
@Composable
public fun MarkdownDocument(
    markdown: String,
    textStyles: TextStyles = m3TextStyles(),
    textStyleModifiers: TextStyleModifiers = m3TextStyleModifiers(),
    blockQuoteStyle: BlockQuoteStyle = m3BlockQuoteStyle(),
    codeBlockStyle: CodeBlockStyle = m3CodeBlockStyle(),
    modifier: Modifier = Modifier,
    sectionSpacing: Dp = textStyles.textStyle.fontSize.toDp()
) {
    com.nasdroid.core.markdown.MarkdownDocument(
        markdown = markdown,
        textStyles = textStyles,
        textStyleModifiers = textStyleModifiers,
        blockQuoteStyle = blockQuoteStyle,
        codeBlockStyle = codeBlockStyle,
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
