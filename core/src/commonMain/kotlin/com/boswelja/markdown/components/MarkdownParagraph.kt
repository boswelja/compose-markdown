package com.boswelja.markdown.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.boswelja.markdown.generator.MarkdownParagraph
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextUnitSize

/**
 * Displays a [MarkdownParagraph]. A paragraph is a group of "spans". Spans are stylized sections of
 * text, but can also include inline images and links.
 */
@Composable
internal fun MarkdownParagraph(
    paragraph: MarkdownParagraph,
    textStyle: TextStyle,
    textStyleModifiers: TextStyleModifiers,
    linkInteractionListener: LinkInteractionListener?,
    modifier: Modifier = Modifier,
) {
    val (annotatedString, inlineContent) = remember(paragraph) {
        paragraph.children.buildTextWithContent(
            textStyle,
            textStyleModifiers,
            TextUnitSize(100.sp, 100.sp),
            linkInteractionListener
        )
    }

    BasicText(
        text = annotatedString,
        modifier = modifier,
        inlineContent = inlineContent
    )
}
