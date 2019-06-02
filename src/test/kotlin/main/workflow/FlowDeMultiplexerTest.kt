package main.workflow

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import viggin543.alfred.workflow.*
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import kotlin.test.Test
import kotlin.test.assertEquals


class FlowDeMultiplexerTest {

    private val reflections =  Reflections(
        "main.workflow",
        MethodAnnotationsScanner()
    )

    @Test
    fun `calls list alerts when args list is empty`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow, WorkFlowConfigurator(workflow,reflections),reflections)

        whenever(workflow.listFilteredAlerts(listOf()))
            .thenReturn(AlfredItems(listOf()))

        unit.deMultiplex(listOf())

        verify(workflow).listFilteredAlerts(listOf())
    }

    @Test
    fun `calls filter alerts when args list contains filter`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow,WorkFlowConfigurator(workflow,reflections),reflections)

        val args = listOf("banana")

        whenever(workflow.listFilteredAlerts(args))
            .thenReturn(AlfredItems(listOf()))

        unit.deMultiplex(args)

        verify(workflow).listFilteredAlerts(args)
    }

    @Test
    fun `call close alert when arg contains magic __CLOSE__ string`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow,WorkFlowConfigurator(workflow,reflections),reflections)

        val args = listOf("__CLOSE__123")

        whenever(workflow.close("123"))
            .thenReturn("alert closed")

        unit.deMultiplex(args)

        verify(workflow).close("123")
    }

    @Test
    fun `ack_this mod should call workflow ack method`(){
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow,WorkFlowConfigurator(workflow,reflections),reflections)

        val args = listOf("__ACK_THIS__123")

        whenever(workflow.ack("123"))
            .thenReturn("ok boss")

        unit.deMultiplex(args)

        verify(workflow).ack("123")
    }


    @Test
    fun extractArgs(){
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow,WorkFlowConfigurator(workflow,reflections),reflections)
        val actual = unit.extractArgs(
            listOf("__CLOSE_LIKE_THIS__[PPP]","-","[XXX]","xxx","yyy","-","asd","111","banana"),
            "__CLOSE_LIKE_THIS__"
        )

        assertEquals(actual,"[PPP] - [XXX] xxx yyy - asd 111 banana")

    }



    @Test
    fun `call close all alert like this when args list contains magic string __CLOSE_LIKE_THIS__`(){
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow,WorkFlowConfigurator(workflow,reflections),reflections)

        val args = listOf("__CLOSE_LIKE_THIS__SOME message of alert that exists 1 times")

        whenever(workflow.closeAllLikeThis("SOME message of alert that exists 1 times"))
            .thenReturn("1: alerts where closed")

        unit.deMultiplex(args)

        verify(workflow).closeAllLikeThis("SOME message of alert that exists 1 times")
    }

}