package main.workflow

import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertFalse

internal class WorkFlowConfiguratorTest {

    companion object {
        @JvmStatic
        fun argLists() = listOf(
            Arguments.of(listOf<String>()),
            Arguments.of(listOf("banana"))
        )
    }

    @ParameterizedTest
    @MethodSource("argLists")
    fun `isConfigureCommand false case`(args: List<String>) {
        val workflow: Workflow = mock()
        val unit = WorkFlowConfigurator(workflow)
        assertFalse(unit.isConfigureCommand(args))
    }

}