package com.boswelja.markdown.style

import androidx.compose.foundation.layout.PaddingValues

/**
 * Describes how a Markdown table should appear.
 *
 * @property cellPadding The padding to be applied to individual cells in the table.
 */
public data class TableStyle(
    val cellPadding: PaddingValues,
)
