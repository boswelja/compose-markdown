package com.boswelja.markdown.generator

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
open class MarkdownNodeGeneratorBenchmark {
    private lateinit var generator: MarkdownNodeGenerator

    @Setup
    fun prepare() {
        val flavor = GFMFlavourDescriptor()
        val tree = MarkdownParser(flavor).buildMarkdownTreeFromString(TestMarkdown)
        generator = MarkdownNodeGenerator(TestMarkdown, tree)
    }

    @Benchmark
    fun benchmarkGenerateNodes(): Any {
        return generator.generateNodes()
    }
}
