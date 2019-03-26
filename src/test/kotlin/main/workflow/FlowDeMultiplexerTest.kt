package main.workflow

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import main.workflow.alfred.AlfredItems
import kotlin.test.Test
import kotlin.test.assertEquals


class FlowDeMultiplexerTest {

    @Test
    fun `calls list alerts whan args list is empty`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow)

        whenever(workflow.listAlerts())
            .thenReturn(AlfredItems(listOf()))

        unit.deMultiplex(listOf())

        verify(workflow).listAlerts()
    }

    @Test
    fun `calls filter alerts when args list contains filter`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow)

        val args = listOf("banana")

        whenever(workflow.listFilteredAlerts(args))
            .thenReturn(AlfredItems(listOf()))

        unit.deMultiplex(args)

        verify(workflow).listFilteredAlerts(args)
    }

    @Test
    fun `call close alert when arg contains magic __CLOSE__ string`() {
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow)

        val args = listOf("__CLOSE__123")

        whenever(workflow.close("123"))
            .thenReturn("alert closed")

        unit.deMultiplex(args)

        verify(workflow).close("123")
    }

    @Test
    fun `call close all alert like this when args list contains magic string __CLOSE_LIKE_THIS__`(){
        val workflow: Workflow = mock()
        val unit = FlowDeMultiplexer(workflow)

        val args = listOf("__CLOSE_LIKE_THIS__SOME message of alert that exists 100 times")

        whenever(workflow.closeAllLikeThis("SOME message of alert that exists 100 times"))
            .thenReturn("alerts closed")

        unit.deMultiplex(args)

        verify(workflow).closeAllLikeThis("SOME message of alert that exists 100 times")
    }

}