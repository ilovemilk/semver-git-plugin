package io.wusa.extension

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

class Branch(private val project: Project) {
    @Internal
    val regexProperty: Property<String> = project.objects.property(String::class.java)
    var regex: String
        get() = regexProperty.get()
        set(value) = regexProperty.set(value)

    @Internal
    val incrementerProperty: Property<String> = project.objects.property(String::class.java)
    var incrementer: String
        get() = incrementerProperty.get()
        set(value) = incrementerProperty.set(value)

    @Internal
    val formatterProperty: Property<Any> = project.objects.property(Any::class.java)
    var formatter: Any
        get() = formatterProperty.get()
        set(value) = formatterProperty.set(value)

    override fun toString(): String {
        return "Branch(regex=$regex, incrementer=$incrementer)"
    }
}
