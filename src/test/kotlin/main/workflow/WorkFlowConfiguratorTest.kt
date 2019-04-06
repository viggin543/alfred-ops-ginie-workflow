package main.workflow

import com.nhaarman.mockitokotlin2.mock
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import kotlin.test.Test
import kotlin.test.assertFalse

internal class WorkFlowConfiguratorTest {

    val reflections =          Reflections(
        "main.workflow",
        MethodAnnotationsScanner()
    )

    @Test
    fun `isConfigureCommand false case`() {
        val workflow: Workflow = mock()
        val unit = WorkFlowConfigurator(workflow,reflections)
        assertFalse(unit.isConfigureCommand(listOf()))
        assertFalse(unit.isConfigureCommand(listOf("banana")))
    }

}