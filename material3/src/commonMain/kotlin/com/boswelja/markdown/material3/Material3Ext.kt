@file:Suppress("ForbiddenImport")
package com.boswelja.markdown.material3

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boswelja.markdown.style.BlockQuoteStyle
import com.boswelja.markdown.style.CodeBlockStyle
import com.boswelja.markdown.style.RuleStyle
import com.boswelja.markdown.style.TableStyle
import com.boswelja.markdown.style.TextStyleModifiers
import com.boswelja.markdown.style.TextStyles

/**
 * Constructs a [TextStyles] using recommended defaults from your Material 3 theme.
 */
@Composable
public fun m3TextStyles(
    textColor: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
    headline1: TextStyle = MaterialTheme.typography.displaySmall.copy(color = textColor),
    headline2: TextStyle = MaterialTheme.typography.headlineLarge.copy(color = textColor),
    headline3: TextStyle = MaterialTheme.typography.headlineMedium.copy(color = textColor),
    headline4: TextStyle = MaterialTheme.typography.headlineSmall.copy(color = textColor),
    headline5: TextStyle = MaterialTheme.typography.titleLarge.copy(color = textColor),
    headline6: TextStyle = MaterialTheme.typography.titleMedium.copy(color = textColor),
): TextStyles {
    return TextStyles(
        textStyle = textStyle,
        headline1 = headline1,
        headline2 = headline2,
        headline3 = headline3,
        headline4 = headline4,
        headline5 = headline5,
        headline6 = headline6,
    )
}

/**
 * Constructs a [TextStyleModifiers] using recommended defaults from your Material 3 theme.
 */
@Composable
public fun m3TextStyleModifiers(
    linkColor: Color = MaterialTheme.colorScheme.primary
): TextStyleModifiers {
    return TextStyleModifiers(
        bold = { it.copy(fontWeight = FontWeight.Bold) },
        italics = { it.copy(fontStyle = FontStyle.Italic) },
        strikethrough = { it.copy(textDecoration = TextDecoration.LineThrough) },
        link = { it.copy(color = linkColor, textDecoration = TextDecoration.Underline) },
        code = { it.copy(fontFamily = FontFamily.Monospace) }
    )
}

/**
 * Constructs a [BlockQuoteStyle] using recommended defaults for your Material 3 theme.
 */
@Composable
public fun m3BlockQuoteStyle(
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = MaterialTheme.shapes.medium,
    barWidth: Dp = 4.dp,
    barColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    barShape: Shape = RectangleShape,
    innerPadding: PaddingValues = PaddingValues(8.dp)
): BlockQuoteStyle {
    return BlockQuoteStyle(
        background = background,
        shape = shape,
        barWidth = barWidth,
        barColor = barColor,
        barShape = barShape,
        innerPadding = innerPadding
    )
}

/**
 * Constructs a [CodeBlockStyle] using recommended defaults for your Material 3 theme.
 */
@Composable
public fun m3CodeBlockStyle(
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = MaterialTheme.shapes.medium,
    innerPadding: PaddingValues = PaddingValues(8.dp)
): CodeBlockStyle {
    return CodeBlockStyle(
        background = background,
        shape = shape,
        innerPadding = innerPadding
    )
}

/**
 * Constructs a [RuleStyle] using recommended defaults for your Material 3 theme.
 */
@Composable
public fun m3RuleStyle(
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    shape: Shape = RectangleShape,
    thickness: Dp = Dp.Hairline
): RuleStyle {
    return RuleStyle(
        color = color,
        shape = shape,
        thickness = thickness
    )
}

/**
 * Constructs a [TableStyle] using recommended defaults for your Material 3 theme.
 */
@Composable
public fun m3TableStyle(
    cellPadding: PaddingValues = PaddingValues(8.dp)
): TableStyle {
    return TableStyle(cellPadding)
}
