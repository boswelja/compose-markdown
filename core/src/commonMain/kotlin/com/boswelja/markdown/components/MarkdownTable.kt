package com.boswelja.markdown.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextStyle
import com.boswelja.markdown.generator.MarkdownTable
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TableStyle
import com.boswelja.markdown.style.TextStyleModifiers

/**
 * Displays a [MarkdownTable]. A table is a grid of labelled rows and columns that contain
 * paragraphs.
 */
@Composable
internal fun MarkdownTable(
    table: MarkdownTable,
    style: TableStyle,
    textStyle: TextStyle,
    textStyleModifiers: TextStyleModifiers,
    ruleStyle: RuleStyle,
    linkInteractionListener: LinkInteractionListener?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        // Add headers
        Row {
            table.columns.forEach {
                MarkdownParagraph(
                    paragraph = it.header,
                    textStyle = textStyle,
                    textStyleModifiers = textStyleModifiers,
                    linkInteractionListener = linkInteractionListener,
                    modifier = Modifier.weight(1f).padding(style.cellPadding)
                )
            }
        }
        MarkdownRule(ruleStyle)
        table.columns.first().cells.forEachIndexed { index, _ ->
            Row {
                table.columns.forEach {
                    MarkdownParagraph(
                        paragraph = it.cells[index],
                        textStyle = textStyle,
                        textStyleModifiers = textStyleModifiers,
                        linkInteractionListener = linkInteractionListener,
                        modifier = Modifier.weight(1f).padding(style.cellPadding)
                    )
                }
            }
            if (index < table.columns.first().cells.lastIndex) {
                MarkdownRule(ruleStyle)
            }
        }
    }
}
