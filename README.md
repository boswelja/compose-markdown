# Compose Markdown

A fully native Compose Markdown renderer!

## Setup

```kt
dependencies {
    val markdownVersion = "1.0.1"
    
    // Core provides a generic implementation fit for any design system
    implementation("io.github.boswelja.markdown:core:$markdownVersion")
    
    // material3 contains opinionated components to streamline development
    implementation("io.github.boswelja.markdown:material3:$markdownVersion")
}
```

## Usage

```kotlin
@Composable
fun MyComposable() {
    MarkdownDocument(
        """
        # Heading
        
        Text
        
        """.trimIndent())
}
```

## Documentation

Docs are published with each release to https://boswelja.github.io/compose-markdown/.
