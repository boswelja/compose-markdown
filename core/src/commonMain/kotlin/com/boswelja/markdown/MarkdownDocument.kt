package com.boswelja.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.boswelja.markdown.components.MarkdownBlockQuote
import com.boswelja.markdown.components.MarkdownCodeBlock
import com.boswelja.markdown.components.MarkdownHeading
import com.boswelja.markdown.components.MarkdownHtmlBlock
import com.boswelja.markdown.components.MarkdownOrderedList
import com.boswelja.markdown.components.MarkdownParagraph
import com.boswelja.markdown.components.MarkdownRule
import com.boswelja.markdown.components.MarkdownTable
import com.boswelja.markdown.components.MarkdownUnorderedList
import com.boswelja.markdown.generator.MarkdownBlockQuote
import com.boswelja.markdown.generator.MarkdownCodeBlock
import com.boswelja.markdown.generator.MarkdownHeading
import com.boswelja.markdown.generator.MarkdownHtmlBlock
import com.boswelja.markdown.generator.MarkdownNode
import com.boswelja.markdown.generator.MarkdownNodeGenerator
import com.boswelja.markdown.generator.MarkdownOrderedList
import com.boswelja.markdown.generator.MarkdownParagraph
import com.boswelja.markdown.generator.MarkdownRule
import com.boswelja.markdown.generator.MarkdownTable
import com.boswelja.markdown.generator.MarkdownUnorderedList
import com.boswelja.markdown.style.BlockQuoteStyle
import com.boswelja.markdown.style.CodeBlockStyle
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TableStyle
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

/**
 * Displays a Markdown document.
 */
@Composable
public fun MarkdownDocument(
    markdown: String,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    blockQuoteStyle: BlockQuoteStyle,
    codeBlockStyle: CodeBlockStyle,
    ruleStyle: RuleStyle,
    tableStyle: TableStyle,
    onLinkClick: (LinkAnnotation) -> Unit,
    modifier: Modifier = Modifier,
    sectionSpacing: Dp = textStyles.textStyle.fontSize.toDp()
) {
    val parsedMarkdownNodes = remember(markdown) {
        val flavor = GFMFlavourDescriptor()
        val tree = MarkdownParser(flavor).buildMarkdownTreeFromString(markdown)
        MarkdownNodeGenerator(markdown, tree).generateNodes()
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(sectionSpacing)
    ) {
        parsedMarkdownNodes.forEach {
            MarkdownNode(
                node = it,
                textStyles = textStyles,
                textStyleModifiers = textStyleModifiers,
                blockQuoteStyle = blockQuoteStyle,
                codeBlockStyle = codeBlockStyle,
                ruleStyle = ruleStyle,
                tableStyle = tableStyle,
                onLinkClick = onLinkClick,
            )
        }
    }
}

@Composable
internal fun TextUnit.toDp(): Dp {
    return with(LocalDensity.current) {
        this@toDp.toPx().toDp()
    }
}

@Composable
internal fun MarkdownNode(
    node: MarkdownNode,
    textStyles: TextStyles,
    textStyleModifiers: TextStyleModifiers,
    blockQuoteStyle: BlockQuoteStyle,
    codeBlockStyle: CodeBlockStyle,
    ruleStyle: RuleStyle,
    tableStyle: TableStyle,
    onLinkClick: (LinkAnnotation) -> Unit,
    modifier: Modifier = Modifier
) {
    when (node) {
        is MarkdownBlockQuote -> MarkdownBlockQuote(
            blockQuote = node,
            style = blockQuoteStyle,
            textStyles = textStyles,
            textStyleModifiers = textStyleModifiers,
            codeBlockStyle = codeBlockStyle,
            ruleStyle = ruleStyle,
            tableStyle = tableStyle,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
        is MarkdownCodeBlock -> MarkdownCodeBlock(
            codeBlock = node,
            style = codeBlockStyle,
            textStyle = textStyles.textStyle.copy(fontFamily = FontFamily.Monospace),
            modifier = modifier,
        )
        is MarkdownHeading -> MarkdownHeading(
            heading = node,
            modifier = modifier,
            textStyles = textStyles,
            textStyleModifiers = textStyleModifiers,
        )
        is MarkdownOrderedList -> MarkdownOrderedList(
            list = node,
            textStyles = textStyles,
            textStyleModifiers = textStyleModifiers,
            blockQuoteStyle = blockQuoteStyle,
            codeBlockStyle = codeBlockStyle,
            ruleStyle = ruleStyle,
            tableStyle = tableStyle,
            onLinkClick = onLinkClick,
            modifier = modifier
        )
        is MarkdownParagraph -> MarkdownParagraph(
            paragraph = node,
            textStyle = textStyles.textStyle,
            textStyleModifiers = textStyleModifiers,
            onLinkClick = onLinkClick,
            modifier = modifier
        )
        MarkdownRule -> MarkdownRule(
            ruleStyle = ruleStyle,
            modifier = modifier
        )
        is MarkdownTable -> MarkdownTable(
            table = node,
            style = tableStyle,
            textStyle = textStyles.textStyle,
            textStyleModifiers = textStyleModifiers,
            ruleStyle = ruleStyle,
            onLinkClick = onLinkClick,
            modifier = modifier
        )
        is MarkdownHtmlBlock -> MarkdownHtmlBlock(
            htmlBlock = node,
            textStyle = textStyles.textStyle,
            modifier = modifier
        )
        is MarkdownUnorderedList -> MarkdownUnorderedList(
            list = node,
            textStyles = textStyles,
            textStyleModifiers = textStyleModifiers,
            blockQuoteStyle = blockQuoteStyle,
            codeBlockStyle = codeBlockStyle,
            ruleStyle = ruleStyle,
            tableStyle = tableStyle,
            onLinkClick = onLinkClick,
            modifier = modifier
        )
    }
}
