package com.boswelja.markdown.generator

import org.intellij.markdown.MarkdownElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

/**
 * A class that takes a Markdown document and its parsed [ASTNode], and produces a type-safe
 * representation of the document tree. See [MarkdownNode] for possible types.
 */
internal class MarkdownNodeGenerator(
    private val allFileText: String,
    private val rootNode: ASTNode
) {
    private val referenceLinkDefinitions = mutableMapOf<String, ReferenceLinkDefinition>()

    init {
        check(rootNode.type == MarkdownElementTypes.MARKDOWN_FILE) {
            "The root node provided must be of type MARKDOWN_FILE, but it was ${rootNode.type}"
        }
    }

    /**
     * Generates a list of [MarkdownNode]s from the data provided at construction time.
     */
    fun generateNodes(): List<MarkdownNode> {
        val nodes = rootNode.children.mapNotNull { parseGenericNode(it) }
        // Retroactively go back and see if there's any reference links we need to complete
        return updateReferenceLinks(nodes)
    }

    private fun updateReferenceLinks(nodes: List<MarkdownNode>): List<MarkdownNode> {
        return nodes.map {
            when (it) {
                is MarkdownBlockQuote -> it.copy(children = updateReferenceLinks(it.children))
                is MarkdownCodeBlock -> it
                is MarkdownHeading -> it.copy(children = updateReferenceLinkSpans(it.children))
                is MarkdownHtmlBlock -> it
                is MarkdownOrderedList -> it.copy(
                    listItems = it.listItems.map { MarkdownListItem(updateReferenceLinks(it.content)) }
                )
                is MarkdownParagraph -> it.copy(children = updateReferenceLinkSpans(it.children))
                MarkdownRule -> it
                is MarkdownTable -> it.copy(
                    columns = it.columns.map {
                        it.copy(
                            header = it.header.copy(
                                children = updateReferenceLinkSpans(it.header.children)
                            ),
                            cells = it.cells.map { cell ->
                                cell.copy(
                                    children = updateReferenceLinkSpans(cell.children)
                                )
                            }
                        )
                    }
                )
                is MarkdownUnorderedList -> it.copy(
                    listItems = it.listItems.map { MarkdownListItem(updateReferenceLinks(it.content)) }
                )
            }
        }
    }

    private fun updateReferenceLinkSpans(nodes: List<MarkdownSpanNode>): List<MarkdownSpanNode> {
        return nodes.map {
            when (it) {
                is MarkdownCodeSpan -> it
                MarkdownEol -> it
                is MarkdownImage -> it
                is MarkdownLink -> {
                    if (referenceLinkDefinitions.contains(it.url)) {
                        val definition = referenceLinkDefinitions[it.url]!!
                        it.copy(
                            url = definition.url,
                            titleText = definition.title
                        )
                    } else {
                        it
                    }
                }
                is MarkdownText -> it
                MarkdownWhitespace -> it
            }
        }
    }

    private fun parseGenericNode(astNode: ASTNode): MarkdownNode? {
        return when (astNode.type) {
            MarkdownElementTypes.ATX_1,
            MarkdownElementTypes.ATX_2,
            MarkdownElementTypes.ATX_3,
            MarkdownElementTypes.ATX_4,
            MarkdownElementTypes.ATX_5,
            MarkdownElementTypes.ATX_6,
            MarkdownElementTypes.SETEXT_1,
            MarkdownElementTypes.SETEXT_2 -> parseHeaderNode(astNode)
            MarkdownElementTypes.PARAGRAPH -> parseParagraphNode(astNode)
            MarkdownElementTypes.BLOCK_QUOTE -> parseBlockQuote(astNode)
            MarkdownTokenTypes.EOL,
            MarkdownTokenTypes.WHITE_SPACE -> null // Ignored in generic nodes, the renderer should handle it.
            MarkdownTokenTypes.HORIZONTAL_RULE -> MarkdownRule
            MarkdownElementTypes.CODE_BLOCK,
            MarkdownElementTypes.CODE_FENCE -> parseCodeBlock(astNode)
            GFMElementTypes.TABLE -> parseTable(astNode)
            MarkdownElementTypes.UNORDERED_LIST -> parseUnorderedList(astNode)
            MarkdownElementTypes.ORDERED_LIST -> parseOrderedList(astNode)
            MarkdownElementTypes.HTML_BLOCK -> MarkdownHtmlBlock(astNode.getTextInNode(allFileText).toString())
            MarkdownElementTypes.LINK_DEFINITION -> {
                parseLinkDefinition(astNode)
                null
            }
            else -> error("Unknown node type ${astNode.type}")
        }
    }

    private fun parseUnorderedList(astNode: ASTNode): MarkdownUnorderedList {
        val listItems = astNode.children
            .filter { it.type == MarkdownElementTypes.LIST_ITEM }
            .map { listItemNode ->
                MarkdownListItem(
                    content = listItemNode.children.drop(1)
                        .mapNotNull { parseGenericNode(it) }
                )
            }
        return MarkdownUnorderedList(listItems)
    }

    private fun parseOrderedList(astNode: ASTNode): MarkdownOrderedList {
        val list = parseUnorderedList(astNode)
        return MarkdownOrderedList(list.listItems)
    }

    private fun parseTable(astNode: ASTNode): MarkdownTable {
        val headers = astNode.children[0].children.filter { it.type == GFMTokenTypes.CELL }
        val bodyNodes = astNode.children
            .filter { it.type == GFMElementTypes.ROW }
        val columns = headers.mapIndexed { index: Int, headerNode: ASTNode ->
            val columnCellNodes = bodyNodes
                .map { row -> row.children.filter { it.type == GFMTokenTypes.CELL }.getOrNull(index) }
            MarkdownTable.Column(
                header = parseParagraphNode(headerNode),
                alignment = MarkdownTable.Alignment.LEFT,
                cells = columnCellNodes.map { node ->
                    node?.let { parseParagraphNode(it) } ?: MarkdownParagraph(listOf(MarkdownWhitespace))
                }
            )
        }
        return MarkdownTable(columns)
    }

    private fun parseCodeBlock(astNode: ASTNode): MarkdownCodeBlock {
        val language = astNode.children
            .firstOrNull { it.type == MarkdownTokenTypes.FENCE_LANG }
            ?.getTextInNode(allFileText)
            ?.toString()
        val code = astNode.children
            .filterNot {
                it.type == MarkdownTokenTypes.CODE_FENCE_START ||
                        it.type == MarkdownTokenTypes.CODE_FENCE_END ||
                        it.type == MarkdownTokenTypes.FENCE_LANG
            }
            .joinToString(separator = "") { node ->
                if (node.type == MarkdownTokenTypes.EOL) {
                    "\n"
                } else {
                    node.getTextInNode(allFileText)
                }
            }
            .trim('\n')
            .trimIndent()
        return MarkdownCodeBlock(code, language)
    }

    private fun parseBlockQuote(astNode: ASTNode): MarkdownBlockQuote {
        return MarkdownBlockQuote(
            children = astNode.children
                .filterNot { it.type == MarkdownTokenTypes.BLOCK_QUOTE }
                .mapNotNull { parseGenericNode(it) }
        )
    }

    private fun parseParagraphNode(astNode: ASTNode): MarkdownParagraph {
        val parsedChildren = astNode.children
            .dropWhile { it.type == MarkdownTokenTypes.WHITE_SPACE }
            .dropLastWhile { it.type == MarkdownTokenTypes.WHITE_SPACE }
            .map { childNode -> parseSpanNode(childNode) }
        return MarkdownParagraph(children = parsedChildren)
    }

    private fun parseSpanNode(astNode: ASTNode): MarkdownSpanNode {
        return when (astNode.type) {
            MarkdownElementTypes.STRONG,
            GFMElementTypes.STRIKETHROUGH,
            MarkdownTokenTypes.TEXT,
            MarkdownTokenTypes.SETEXT_CONTENT,
            MarkdownTokenTypes.ATX_CONTENT,
            MarkdownTokenTypes.HTML_TAG,
            MarkdownTokenTypes.HTML_BLOCK_CONTENT,
            MarkdownElementTypes.EMPH -> parseTextNode(astNode)
            MarkdownTokenTypes.WHITE_SPACE -> MarkdownWhitespace
            MarkdownTokenTypes.EOL -> MarkdownEol
            GFMTokenTypes.GFM_AUTOLINK,
            MarkdownElementTypes.INLINE_LINK,
            MarkdownElementTypes.AUTOLINK -> parseLinkNode(astNode)
            MarkdownElementTypes.IMAGE -> parseImageNode(astNode)
            MarkdownElementTypes.CODE_SPAN -> parseCodeSpan(astNode)
            MarkdownElementTypes.FULL_REFERENCE_LINK,
            MarkdownElementTypes.SHORT_REFERENCE_LINK -> parseReferenceLinkNode(astNode)
            else -> {
                if ((astNode.type as? MarkdownElementType)?.isToken == true) {
                    parseTextNode(astNode)
                } else {
                    error("Unsure how to handle type ${astNode.type} inside a PARAGRAPH")
                }
            }
        }
    }

    private fun parseCodeSpan(astNode: ASTNode): MarkdownCodeSpan {
        val text = astNode.children
            .filterNot { it.type == MarkdownTokenTypes.BACKTICK }
            .joinToString(separator = "") { it.getTextInNode(allFileText) }
        return MarkdownCodeSpan(text)
    }

    private fun parseImageNode(astNode: ASTNode): MarkdownImage {
        val imageLink = astNode.children[1]
        val link = parseLinkNode(imageLink)

        return MarkdownImage(
            imageUrl = link.url,
            contentDescription = link.displayText.joinToString(separator = " ") { (it as MarkdownText).text },
            titleText = link.titleText
        )
    }

    private fun parseHeaderNode(astNode: ASTNode): MarkdownHeading {
        return when (astNode.type) {
            MarkdownElementTypes.SETEXT_1,
            MarkdownElementTypes.ATX_1 -> MarkdownHeading(
                children = astNode.children
                    .filterNot {
                        it.type == MarkdownTokenTypes.SETEXT_1 ||
                                it.type == MarkdownTokenTypes.EOL ||
                                it.type == MarkdownTokenTypes.ATX_HEADER
                    }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline1,
            )
            MarkdownElementTypes.SETEXT_2,
            MarkdownElementTypes.ATX_2 -> MarkdownHeading(
                children = astNode.children
                    .filterNot {
                        it.type == MarkdownTokenTypes.SETEXT_2 ||
                                it.type == MarkdownTokenTypes.EOL ||
                                it.type == MarkdownTokenTypes.ATX_HEADER
                    }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline2,
            )
            MarkdownElementTypes.ATX_3 -> MarkdownHeading(
                children = astNode.children
                    .filterNot { it.type == MarkdownTokenTypes.ATX_HEADER }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline3,
            )
            MarkdownElementTypes.ATX_4 -> MarkdownHeading(
                children = astNode.children
                    .filterNot { it.type == MarkdownTokenTypes.ATX_HEADER }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline4,
            )
            MarkdownElementTypes.ATX_5 -> MarkdownHeading(
                children = astNode.children
                    .filterNot { it.type == MarkdownTokenTypes.ATX_HEADER }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline5,
            )
            MarkdownElementTypes.ATX_6 -> MarkdownHeading(
                children = astNode.children
                    .filterNot { it.type == MarkdownTokenTypes.ATX_HEADER }
                    .map { parseSpanNode(it) },
                size = MarkdownHeading.Size.Headline6,
            )
            else -> error("Unsure how to handle header type ${astNode.type}")
        }
    }

    private fun parseTextNode(astNode: ASTNode): MarkdownText {
        return when (astNode.type) {
            MarkdownElementTypes.STRONG,
            GFMElementTypes.STRIKETHROUGH,
            MarkdownElementTypes.EMPH -> MarkdownText(
                text = astNode.children
                    .filter { it.type == MarkdownTokenTypes.TEXT || it.type == MarkdownTokenTypes.WHITE_SPACE }
                    .joinToString { it.getTextInNode(allFileText) },
                isBold = astNode.type == MarkdownElementTypes.STRONG,
                isItalics = astNode.type == MarkdownElementTypes.EMPH,
                isStrikethrough = astNode.type == GFMElementTypes.STRIKETHROUGH,
            )
            else -> MarkdownText(
                text = astNode.getTextInNode(allFileText).trim().toString(),
                isBold = false,
                isItalics = false,
                isStrikethrough = false
            )
        }
    }

    private fun parseReferenceLinkNode(astNode: ASTNode): MarkdownLink {
        val text = astNode.children
            .first { it.type == MarkdownElementTypes.LINK_TEXT }
            .children
            .filterNot { it.type == MarkdownTokenTypes.LBRACKET || it.type == MarkdownTokenTypes.RBRACKET }
            .map { parseSpanNode(it) }
        val label = astNode.children
            .first { it.type == MarkdownElementTypes.LINK_LABEL }
            .getTextInNode(allFileText)
            .trimStart('[')
            .trimEnd(']')
        val definition = referenceLinkDefinitions[label]
        return if (definition == null) {
            // Placeholder link that needs to be substituted later
            MarkdownLink(
                displayText = text,
                url = label.toString(),
                titleText = null
            )
        } else {
            MarkdownLink(
                displayText = text,
                url = definition.url,
                titleText = definition.title
            )
        }
    }

    private fun parseLinkDefinition(astNode: ASTNode) {
        val link = astNode.children
            .first {
                it.type == MarkdownElementTypes.LINK_DESTINATION
            }
            .getTextInNode(allFileText)
            .trimStart('<', '\'', '"')
            .trimEnd('>', '\'', '"')
        val label = astNode.children
            .first {
                it.type == MarkdownElementTypes.LINK_LABEL
            }
            .getTextInNode(allFileText)
            .trimStart('[')
            .trimEnd(']')
            .toString()
        val titleText = astNode.children
            .firstOrNull {
                it.type == MarkdownElementTypes.LINK_TITLE
            }
            ?.getTextInNode(allFileText)
            ?.trimStart('"', '\'', '(')
            ?.trimEnd('"', '\'', ')')
        referenceLinkDefinitions[label] =
            ReferenceLinkDefinition(
                url = link.toString(),
                title = titleText?.toString()
            )
    }

    private fun parseLinkNode(astNode: ASTNode): MarkdownLink {
        return when (astNode.type) {
            MarkdownElementTypes.AUTOLINK -> {
                val url = astNode.children
                    .filter { it.type == MarkdownTokenTypes.AUTOLINK }
                    .joinToString(separator = "") { it.getTextInNode(allFileText) }
                MarkdownLink(
                    displayText = listOf(
                        MarkdownText(
                            text = url,
                            isBold = false,
                            isItalics = false,
                            isStrikethrough = false
                        )
                    ),
                    url = url,
                    titleText = null
                )
            }
            MarkdownElementTypes.INLINE_LINK -> {
                val link = astNode.children
                    .first { it.type == MarkdownElementTypes.LINK_DESTINATION }
                    .getTextInNode(allFileText)
                val label = astNode.children
                    .first { it.type == MarkdownElementTypes.LINK_TEXT }
                    .children
                    .filterNot { it.type == MarkdownTokenTypes.LBRACKET || it.type == MarkdownTokenTypes.RBRACKET }
                    .map { parseSpanNode(it) }
                val titleText = astNode.children
                    .firstOrNull { it.type == MarkdownElementTypes.LINK_TITLE }
                    ?.children
                    ?.first { it.type == MarkdownTokenTypes.TEXT }
                    ?.getTextInNode(allFileText)
                MarkdownLink(
                    displayText = label,
                    url = link.toString(),
                    titleText = titleText?.toString()
                )
            }
            GFMTokenTypes.GFM_AUTOLINK -> {
                val url = astNode.getTextInNode(allFileText).toString()
                MarkdownLink(
                    displayText = listOf(
                        MarkdownText(
                            text = url,
                            isBold = false,
                            isItalics = false,
                            isStrikethrough = false
                        )
                    ),
                    url = url,
                    titleText = null
                )
            }
            else -> {
                error("Unsure how to handle link type ${astNode.type}")
            }
        }
    }
}

internal data class ReferenceLinkDefinition(
    val url: String,
    val title: String? = null
)
