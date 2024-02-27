# Compose Markdown

A fully native Compose Markdown renderer!

## Setup

### Version Catalogs

```toml
[versions]
composeMarkdown = "1.0.4"

[libraries]
boswelja-compose-core = { group = "io.github.boswelja.markdown", name = "core", version.ref = "composeMarkdown" }
boswelja-compose-markdown = { group = "io.github.boswelja.markdown", name = "material3", version.ref = "composeMarkdown" }
```

```kt
dependencies {
    implementation(libs.boswelja.compose.core)
    implementation(libs.boswelja.compose.markdown)
}
```
### String Notation

```kt
dependencies {
    val markdownVersion = "1.0.4"
    
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
