package com.boswelja.markdown.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.boswelja.markdown.MarkdownNode
import com.boswelja.markdown.generator.MarkdownBlockQuote
import com.boswelja.markdown.style.BlockQuoteStyle
import com.boswelja.markdown.style.CodeBlockStyle
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles

/**
 * Displays a [MarkdownBlockQuote]. A block quote is a visually distinct section in a document,
 * usually used to reference external sources.
 */
@Composable
internal fun MarkdownBlockQuote(
    blockQuote: MarkdownBlockQuote,
    style: BlockQuoteStyle,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    codeBlockStyle: CodeBlockStyle,
    ruleStyle: RuleStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .clip(style.shape)
            .background(style.background)
            .then(modifier)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .clip(style.barShape)
                    .background(style.barColor)
                    .width(style.barWidth)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier.padding(style.innerPadding),
                verticalArrangement = Arrangement.spacedBy(
                    style.innerPadding.calculateBottomPadding()
                )
            ) {
                blockQuote.children.forEach {
                    MarkdownNode(
                        node = it,
                        textStyles = textStyles,
                        textStyleModifiers = textStyleModifiers,
                        blockQuoteStyle = style,
                        codeBlockStyle = codeBlockStyle,
                        ruleStyle = ruleStyle,
                    )
                }
            }
        }
    }
}
