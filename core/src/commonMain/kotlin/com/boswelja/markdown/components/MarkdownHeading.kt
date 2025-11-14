package com.boswelja.markdown.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.unit.sp
import com.boswelja.markdown.generator.MarkdownHeading
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles
import com.boswelja.markdown.style.TextUnitSize

/**
 * Displays a [MarkdownHeading]. A heading is a higher emphasis paragraph, usually used to separate
 * a document into sections.
 */
@Composable
internal fun MarkdownHeading(
    heading: MarkdownHeading,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    linkInteractionListener: LinkInteractionListener?,
    modifier: Modifier = Modifier
) {
    val (annotatedString, inlineContent) = remember(heading, textStyles) {
        val textStyle = when (heading.size) {
            MarkdownHeading.Size.Headline1 -> textStyles.headline1
            MarkdownHeading.Size.Headline2 -> textStyles.headline2
            MarkdownHeading.Size.Headline3 -> textStyles.headline3
            MarkdownHeading.Size.Headline4 -> textStyles.headline4
            MarkdownHeading.Size.Headline5 -> textStyles.headline5
            MarkdownHeading.Size.Headline6 -> textStyles.headline6
        }
        heading.children.buildTextWithContent(
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
