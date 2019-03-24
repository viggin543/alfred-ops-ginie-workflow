package main.workflow.opsGinieApi

import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CacheTest {

    @After
    fun tearDown() {
        clean()
    }

    @Before
    fun setUp() {
        clean()
    }

    private fun clean() {
        File("cached_OpsGinieResponce.json").delete()
    }

    @Test
    fun `get cached responce if cache exists get cached responce`() {
        val unit = Cache()

        val opsGinieResponce = OpsGinieResponce(
            listOf(),
            Paging(first = "", last = ""),
            1.toDouble(),
            "id2"
        )


        File("cached_OpsGinieResponce.json")
            .writeText(
                Json.stringify(OpsGinieResponce.serializer(), opsGinieResponce)
            )

        val actual = unit.get {
            opsGinieResponce.copy(requestId = "nuevo di id")
        }

        assertEquals(opsGinieResponce, actual)


    }

    @Test
    fun `call action when no cahe file exists`() {
        val unit = Cache()
        val opsGinieResponce = OpsGinieResponce(
            listOf(),
            Paging(first = "", last = ""),
            1.toDouble(),
            "id"
        )
        val actual = unit.get {
            opsGinieResponce
        }

        assertEquals(opsGinieResponce, actual)
        assertTrue { File("cached_OpsGinieResponce.json").exists() }
        assertEquals(opsGinieResponce,Json.nonstrict.parse(
            OpsGinieResponce.serializer(),
            File("cached_OpsGinieResponce.json").readText()
        ))

    }
}