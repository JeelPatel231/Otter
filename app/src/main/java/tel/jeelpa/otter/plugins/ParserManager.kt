package tel.jeelpa.otter.plugins

import tel.jeelpa.plugininterface.anime.parser.Parser

class ParserManager {
    private val _parsersLoaded = mutableListOf<Parser>()

    val parsers
        get() = _parsersLoaded.toList()

    fun registerParser(parser: Parser){
        _parsersLoaded.add(parser)
    }
}