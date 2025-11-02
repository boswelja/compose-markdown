package com.boswelja.markdown.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.boswelja.markdown.generator.MarkdownNodeBuilders.markdownLink
import com.boswelja.markdown.generator.MarkdownNodeBuilders.markdownParagraph
import com.boswelja.markdown.generator.MarkdownNodeBuilders.markdownText
import com.boswelja.markdown.generator.MarkdownWhitespace
import com.boswelja.markdown.style.TextStyleModifiers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import com.boswelja.markdown.components.MarkdownParagraph as MarkdownParagraphComponent

class MarkdownParagraphTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onLinkClick_triggers() = runComposeUiTest {
        var clickedAnnotation by mutableStateOf<LinkAnnotation?>(null)

        setContent {
            MarkdownParagraphComponent(
                modifier = Modifier.testTag("MarkdownParagraph"),
                paragraph = markdownParagraph(
                    markdownText("bold", isBold = true),
                    MarkdownWhitespace,
                    markdownText("italics", isItalics = true),
                    MarkdownWhitespace,
                    markdownText("strikethrough", isStrikethrough = true),
                    MarkdownWhitespace,
                    markdownLink("https://google.com", listOf(markdownText("Google")))
                ),
                textStyle = TextStyle(),
                textStyleModifiers = TextStyleModifiers(
                    bold = { it.copy(fontWeight = FontWeight.Bold) },
                    italics = { it.copy(fontStyle = FontStyle.Italic) },
                    strikethrough = { it.copy(textDecoration = TextDecoration.LineThrough) },
                    link = {
                        it.copy(
                            color = Color.Red,
                            textDecoration = TextDecoration.Underline
                        )
                    },
                    code = { it.copy(fontFamily = FontFamily.Monospace) }
                ),
                linkInteractionListener = {
                    clickedAnnotation = it
                }
            )
        }
        onNodeWithTag("MarkdownParagraph").performFirstLinkClick()
        assertNotNull(clickedAnnotation)
        assertEquals(assertIs<LinkAnnotation.Url>(clickedAnnotation).url, "https://google.com")
    }
}