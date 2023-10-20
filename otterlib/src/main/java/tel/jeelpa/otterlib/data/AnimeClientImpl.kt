package tel.jeelpa.otterlib.data

import com.apollographql.apollo3.ApolloClient
import org.jsoup.Jsoup
import tel.jeelpa.otter.AnimeRecentlyUpdatedQuery
import tel.jeelpa.otter.type.MediaSort
import tel.jeelpa.otter.type.MediaType
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MalMediaScrapedDetails
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaDetailsFull
import tel.jeelpa.otterlib.models.toApp
import tel.jeelpa.otterlib.repository.AnimeClient

class AnimeClientImpl(
    private val anilistApolloClient: ApolloClient
) : BaseClient(anilistApolloClient), AnimeClient {

    override suspend fun getTrendingAnime(): List<MediaCardData> {
        return executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.ANIME
        )
    }

    override suspend fun getPopularAnime(): List<MediaCardData> {
        return executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.POPULARITY_DESC),
            type = MediaType.ANIME
        )
    }

    override suspend fun getRecentlyUpdated(): List<MediaCardData> {
        return anilistApolloClient.query(
            AnimeRecentlyUpdatedQuery(lesser = (System.currentTimeMillis() / 1000).toInt() - 10000)
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

    override suspend fun getAnimeDetails(id: Int): MediaDetailsFull  = getMediaDetails(id)

    private fun scrapeMalDetails(malId:Int, mediaType: AppMediaType): MalMediaScrapedDetails {
        val headers = mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36"
        )
        val path = when (mediaType) {
            AppMediaType.ANIME -> "anime"
            AppMediaType.MANGA -> "manga"
            else -> throw IllegalStateException("Unknown Media Type")
        }

        val res = Jsoup.connect("https://myanimelist.net/$path/$malId")
            .headers(headers)
            .get()

        val baseNameHolder = res.select(".h1-title > [itemprop=\"name\"]")
        val nameText = baseNameHolder.textNodes().firstOrNull()?.text()
            ?: baseNameHolder.select(".title-name").text()

        val mediaFormat = res.select("div.spaceit_pad > a").first()!!.text()

        val openings = res.select(".opnening > table > tbody > tr > td").mapNotNull {
            it.text().takeUnless { str -> str.contains("Help improve our database") || str.isBlank() }
        }

        val endings = res.select(".ending > table > tbody > tr > td").mapNotNull {
            it.text().takeUnless { str -> str.contains("Help improve our database") || str.isBlank() }
        }

        return MalMediaScrapedDetails(
            name = nameText,
            malId = malId,
            openings =  openings,
            endings = endings,
            type = mediaFormat
        )
    }

    override suspend fun getOpenings(id: Int): List<String> {
        return scrapeMalDetails(id, AppMediaType.ANIME).openings
    }

    override suspend fun getEndings(id: Int): List<String> {
        return scrapeMalDetails(id, AppMediaType.ANIME).endings
    }
}