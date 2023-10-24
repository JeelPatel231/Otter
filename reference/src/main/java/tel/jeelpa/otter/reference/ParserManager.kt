package tel.jeelpa.otter.reference

class ParserManager {
    private val _parsersLoaded = mutableListOf<Parser>()

    val parsers
        get() = _parsersLoaded.toList()

    // TODO : any parsers shouldn't be able to access this and register anything
    fun registerParser(parser: Parser){
        _parsersLoaded.add(parser)
    }
}