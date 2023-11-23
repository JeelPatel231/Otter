package tel.jeelpa.otter.maltrackerplugin.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import tel.jeelpa.otter.maltrackerplugin.models.ResponseToken

class MalTokenInterceptor(
    private val clientId: String,
    private val liveTokenData: Flow<ResponseToken?>,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // TODO: DONT BLOCK
        val token = runBlocking { liveTokenData.first() }

        val req = chain.request()
            .newBuilder()
            .addHeader("X-MAL-CLIENT-ID", clientId)

        if(token?.accessToken != null) {
            req.apply { addHeader("Authorization", "Bearer ${token.accessToken}") }
        }
        return chain.proceed(req.build())
    }
}
