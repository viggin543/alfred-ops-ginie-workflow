package main.workflow

import com.nhaarman.mockitokotlin2.mock
import kotlin.test.Test
import kotlin.test.assertFalse

internal class WorkFlowConfiguratorTest {


    @Test
    fun `isConfigureCommand false case`() {
        val workflow: Workflow = mock()
        val unit = WorkFlowConfigurator(workflow)
        assertFalse(unit.isConfigureCommand(listOf()))
        assertFalse(unit.isConfigureCommand(listOf("banana")))
    }

}