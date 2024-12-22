package com.boswelja.markdown.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.boswelja.markdown.generator.MarkdownCodeSpan
import com.boswelja.markdown.generator.MarkdownEol
import com.boswelja.markdown.generator.MarkdownImage
import com.boswelja.markdown.generator.MarkdownLink
import com.boswelja.markdown.generator.MarkdownSpanNode
import com.boswelja.markdown.generator.MarkdownText
import com.boswelja.markdown.generator.MarkdownWhitespace
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextUnitSize

/**
 * Maps a list of [MarkdownSpanNode]s to a [TextWithContent] for use in a Text Composable.
 */
internal fun List<MarkdownSpanNode>.buildTextWithContent(
    textStyles: TextStyle,
    textStyleModifiers: TextStyleModifiers,
    imageSize: TextUnitSize,
): TextWithContent {
    val content = mutableMapOf<String, InlineTextContent>()
    val text = buildAnnotatedString {
        this@buildTextWithContent.forEach { node ->
            if (node is MarkdownImage) {
                content[node.imageUrl] = InlineTextContent(
                    // TODO auto-size the content - https://issuetracker.google.com/issues/294110693
                    placeholder = Placeholder(imageSize.width, imageSize.height, PlaceholderVerticalAlign.TextBottom)
                ) { contentDescription ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .fetcherFactory(OkHttpNetworkFetcherFactory())
                            .data(node.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = contentDescription,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            append(node.toAnnotatedString(textStyles,textStyleModifiers))
        }
    }
    return TextWithContent(text, content)
}

/**
 * Describes an [AnnotatedString], along with a map describing inline content within the annotated
 * string.
 *
 * @property text The [AnnotatedString] to be displayed.
 * @property content The map containing [InlineTextContent]s to be displayed.
 */
internal data class TextWithContent(
    val text: AnnotatedString,
    val content: Map<String, InlineTextContent>
)

internal fun MarkdownSpanNode.toAnnotatedString(
    textStyle: TextStyle,
    textStyleModifiers: TextStyleModifiers,
): AnnotatedString {
    return when (this) {
        is MarkdownCodeSpan -> AnnotatedString(
            text = text,
            spanStyle = textStyleModifiers.code(textStyle).toSpanStyle()
        )
        is MarkdownImage -> buildAnnotatedString {
            appendInlineContent(imageUrl, contentDescription)
        }
        is MarkdownLink -> buildAnnotatedString {
            withLink(LinkAnnotation.Url(url)) {
                withStyle(textStyleModifiers.link(textStyle).toSpanStyle()) {
                    displayText.forEach {
                        append(it.toAnnotatedString(textStyle, textStyleModifiers))
                    }
                }
            }
        }
        is MarkdownText -> AnnotatedString(
            text = this.text,
            spanStyle = textStyle
                .maybeLet(isBold, textStyleModifiers.bold)
                .maybeLet(isItalics, textStyleModifiers.italics)
                .maybeLet(isStrikethrough, textStyleModifiers.strikethrough)
                .toSpanStyle()
        )
        MarkdownWhitespace -> AnnotatedString(" ", textStyle.toSpanStyle())
        MarkdownEol -> AnnotatedString("\n", textStyle.toSpanStyle())
    }
}

internal inline fun <T> T.maybeLet(condition: Boolean, block: (T) -> T): T {
    return this.let {
        if (condition) block(it) else it
    }
}
