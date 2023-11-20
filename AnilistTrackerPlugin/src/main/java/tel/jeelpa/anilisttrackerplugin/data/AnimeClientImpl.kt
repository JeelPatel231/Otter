package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import tel.jeelpa.anilisttrackerplugin.models.toApp
import tel.jeelpa.otter.anilisttrackerplugin.models.AnimeRecentlyUpdatedQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaSort
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient

data class MalMediaScrapedDetails(
    val name: String,
    val malId: Int,
    val type: String,
    val openings: List<String>,
    val endings: List<String>,
)

class AnimeClientImpl(
    private val anilistApolloClient: ApolloClient
) : BaseClient(anilistApolloClient), AnimeClient {
    override suspend fun search(query: String, page: Int, itemsInPage: Int): List<MediaCardData> =
        super.search(query, page, itemsInPage, AppMediaType.ANIME)

    override suspend fun getTrendingAnime(page: Int, itemsInPage: Int): List<MediaCardData> {
        return executeBaselineMediaQuery(
            page = page,
            perPage = itemsInPage,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.ANIME
        )
    }

    override suspend fun getPopularAnime(page: Int, itemsInPage: Int): List<MediaCardData> {
        return executeBaselineMediaQuery(
            page = page,
            perPage = itemsInPage,
            sort = listOf(MediaSort.POPULARITY_DESC),
            type = MediaType.ANIME
        )
    }

    override suspend fun getRecentlyUpdated(page: Int, itemsInPage: Int): List<MediaCardData> {
        return anilistApolloClient.query(
            AnimeRecentlyUpdatedQuery(
                lesser = (System.currentTimeMillis() / 1000).toInt() - 10000,
                page = Optional.present(page),
                perPage = Optional.present(itemsInPage)
            )
        ).execute().data?.Page?.airingSchedules?.mapNotNull { it?.media }?.map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0) / 10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        } ?: emptyList()
    }

    override suspend fun getAnimeDetails(id: Int): MediaDetailsFull = getMediaDetails(id)

    private suspend fun scrapeMalDetails(
        malId: Int,
        mediaType: AppMediaType
    ): MalMediaScrapedDetails = withContext(Dispatchers.IO) {
        val headers = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36"
        )
        val path = when (mediaType) {
            AppMediaType.ANIME -> "anime"
            AppMediaType.MANGA -> "manga"
            else -> throw IllegalStateException("Unknown Media Type")
        }

        val res = try {
            Jsoup.connect("https://myanimelist.net/$path/$malId")
                .headers(headers)
                .get()
        } catch (e: Throwable) {
            e.printStackTrace()
            return@withContext MalMediaScrapedDetails(
                name = "???",
                malId = malId,
                openings = listOf("Failed to get Openings"),
                endings = listOf("Failed to get Endings"),
                type = mediaType.name
            )
        }

        val baseNameHolder = res.select(".h1-title > [itemprop=\"name\"]")
        val nameText = baseNameHolder.textNodes().firstOrNull()?.text()
            ?: baseNameHolder.select(".title-name").text()

        val mediaFormat = res.select("div.spaceit_pad > a").first()!!.text()

        val openings = res.select(".opnening > table > tbody > tr > td").mapNotNull {
            it.text()
                .takeUnless { str -> str.contains("Help improve our database") || str.isBlank() }
        }

        val endings = res.select(".ending > table > tbody > tr > td").mapNotNull {
            it.text()
                .takeUnless { str -> str.contains("Help improve our database") || str.isBlank() }
        }

        return@withContext MalMediaScrapedDetails(
            name = nameText,
            malId = malId,
            openings = openings,
            endings = endings,
            type = mediaFormat
        )
    }

    private val malCacheLock = Mutex(false)
    private lateinit var malScrapedCache: MalMediaScrapedDetails

    override suspend fun getOpenings(id: Int): List<String> {
        malCacheLock.withLock {
            if (!::malScrapedCache.isInitialized) {
                malScrapedCache = scrapeMalDetails(id, AppMediaType.ANIME)
            }
        }
        return malScrapedCache.openings
    }

    override suspend fun getEndings(id: Int): List<String> {
        malCacheLock.withLock {
            if (!::malScrapedCache.isInitialized) {
                malScrapedCache = scrapeMalDetails(id, AppMediaType.ANIME)
            }
        }
        return malScrapedCache.endings
    }
}