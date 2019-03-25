package main.workflow.opsGinieApi

import kotlinx.serialization.json.Json
import main.workflow.App
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit


class Cache {

    private val log = LoggerFactory.getLogger(App::class.java)!!

    fun invalidate() = File(cacheFile).delete()

    fun get(action: () -> OpsGinieResponce?): OpsGinieResponce? {
        return when {
            cacheFileExists() && cacheFileLessThen5min() -> getCachedAlerts()
            else -> callAndStore(action)
        }
    }

    private fun callAndStore(action: () -> OpsGinieResponce?): OpsGinieResponce? {
        val resp = action()
        if (resp != null)
            File(cacheFile).writeText(
                Json.stringify(
                    OpsGinieResponce.serializer(),
                    resp
                )
            )
        return resp
    }

    private fun getCachedAlerts(): OpsGinieResponce {

        log.info("getting cached alerts")
        return Json.nonstrict.parse(
            OpsGinieResponce.serializer()
            , File(cacheFile).readText(Charsets.UTF_8)
        )
    }

    private fun cacheFileLessThen5min(): Boolean {
        val cacheTime = TimeUnit.MINUTES.convert(
            Math.abs(System.currentTimeMillis() - File(cacheFile).lastModified()),
            TimeUnit.MILLISECONDS
        )
        log.info("cache is $cacheTime minutes old")
        return cacheTime < 2
    }

    private val cacheFile = "cached_OpsGinieResponce.json"

    private fun cacheFileExists() = File(cacheFile).exists()
}