package com.boswelja.markdown.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.boswelja.markdown.MarkdownNode
import com.boswelja.markdown.generator.MarkdownOrderedList
import com.boswelja.markdown.generator.MarkdownUnorderedList
import com.boswelja.markdown.style.BlockQuoteStyle
import com.boswelja.markdown.style.CodeBlockStyle
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles

private const val ORDERED_LIST_PREFIX_LENGTH = 3

/**
 * Displays a [MarkdownOrderedList]. An ordered list is a list where each item is prefixed with its
 * index in the list.
 */
@Composable
internal fun MarkdownOrderedList(
    list: MarkdownOrderedList,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    blockQuoteStyle: BlockQuoteStyle,
    codeBlockStyle: CodeBlockStyle,
    ruleStyle: RuleStyle,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        list.listItems.forEachIndexed { index, markdownNode ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top
            ) {
                BasicText(
                    text = "${index + 1}".padStart(ORDERED_LIST_PREFIX_LENGTH) + ".",
                    style = textStyles.textStyle
                )
                Column {
                    markdownNode.content.forEach {
                        MarkdownNode(
                            node = it,
                            textStyles = textStyles,
                            textStyleModifiers = textStyleModifiers,
                            blockQuoteStyle = blockQuoteStyle,
                            codeBlockStyle = codeBlockStyle,
                            ruleStyle = ruleStyle,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays a [MarkdownUnorderedList]. An unordered list is a list where each item is prefixed with
 * a bullet of some description.
 */
@Composable
internal fun MarkdownUnorderedList(
    list: MarkdownUnorderedList,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    blockQuoteStyle: BlockQuoteStyle,
    codeBlockStyle: CodeBlockStyle,
    ruleStyle: RuleStyle,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        list.listItems.forEach { markdownNode ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BasicText(
                    text = "\t\u2022",
                    style = textStyles.textStyle
                )
                Column {
                    markdownNode.content.forEach {
                        MarkdownNode(
                            node = it,
                            textStyles = textStyles,
                            textStyleModifiers = textStyleModifiers,
                            blockQuoteStyle = blockQuoteStyle,
                            codeBlockStyle = codeBlockStyle,
                            ruleStyle = ruleStyle,
                        )
                    }
                }
            }
        }
    }
}
