package tel.jeelpa.otter.reference

data class RegisterUseCase(
    val registerExtractor: RegisterExtractorUseCase,
    val registerParser: RegisterParserUseCase,
)

class RegisterExtractorUseCase(private val extractorManager: ExtractorManager) {
    operator fun invoke(extractor: Extractor) =
        extractorManager.registerExtractor(extractor)

    operator fun invoke(vararg extractors: Extractor) =
        extractors.forEach { extractorManager.registerExtractor(it) }
}

class RegisterParserUseCase(private val parserManager: ParserManager) {
    operator fun invoke(parser: Parser) =
        parserManager.registerParser(parser)

    operator fun invoke(vararg parsers: Parser) =
        parsers.forEach { parserManager.registerParser(it) }
}