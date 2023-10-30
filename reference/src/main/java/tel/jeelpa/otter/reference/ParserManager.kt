package tel.jeelpa.otter.reference

class ParserManager {
    private val _parsersLoaded = mutableListOf<Parser>()

    val parsers
        get() = _parsersLoaded.toList()

    fun registerParser(parser: Parser){
        _parsersLoaded.add(parser)
    }
}