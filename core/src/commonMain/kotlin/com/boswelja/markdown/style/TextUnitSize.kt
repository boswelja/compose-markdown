package com.boswelja.markdown.style

import androidx.compose.ui.unit.TextUnit

/**
 * Describes the size of an element rendered inline with text.
 *
 * @property width The width of the element.
 * @property height THe height of the element.
 */
public data class TextUnitSize(
    val width: TextUnit,
    val height: TextUnit,
)
