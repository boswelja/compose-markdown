package com.nasdroid.core.markdown.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Describes how a Markdown horizontal rule should appear.
 *
 * @property color The color of the horizontal rule.
 * @property shape The shape of the horizontal rule. Only applicable when [thickness] is larger than
 * hairline.
 * @property thickness How thick, in [Dp], the horizontal rule should be.
 */
public data class RuleStyle(
    val color: Color,
    val shape: Shape,
    val thickness: Dp,
)
