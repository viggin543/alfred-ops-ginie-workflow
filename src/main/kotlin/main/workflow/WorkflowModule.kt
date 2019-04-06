package main.workflow

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner

class WorkflowModule : AbstractModule() {
    override fun configure() {}

    @Provides
    fun reflectionsProvider() =
         Reflections(
            "main.workflow",
            MethodAnnotationsScanner()
        )

}

