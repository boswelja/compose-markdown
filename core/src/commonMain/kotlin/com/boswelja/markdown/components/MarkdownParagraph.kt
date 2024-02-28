package com.boswelja.markdown.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.boswelja.markdown.generator.MarkdownParagraph
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextUnitSize

/**
 * Displays a [MarkdownParagraph]. A paragraph is a group of "spans". Spans are stylized sections of
 * text, but can also include inline images and links.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
internal fun MarkdownParagraph(
    paragraph: MarkdownParagraph,
    textStyle: TextStyle,
    textStyleModifiers: TextStyleModifiers,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (annotatedString, inlineContent) = remember(paragraph) {
        paragraph.children.buildTextWithContent(textStyle, textStyleModifiers, TextUnitSize(100.sp, 100.sp))
    }
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onLinkClick, annotatedString) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                val offset = layoutResult.getOffsetForPosition(pos)
                annotatedString.getUrlAnnotations(start = offset, end = offset).firstOrNull()?.let { annotation ->
                    onLinkClick(annotation.item.url)
                }
            }
        }
    }
    BasicText(
        text = annotatedString,
        modifier = modifier.then(pressIndicator),
        inlineContent = inlineContent,
        onTextLayout = {
            layoutResult.value = it
        }
    )
}
