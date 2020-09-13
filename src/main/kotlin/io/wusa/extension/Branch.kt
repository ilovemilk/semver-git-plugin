package io.wusa.extension

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

class Branch(project: Project) {
    @Internal
    val regexProperty: Property<String> = project.objects.property(String::class.java)
    var regex: String
        get() = regexProperty.get()
        set(value) = regexProperty.set(value)

    @Internal
    val incrementerProperty: Property<Any> = project.objects.property(Any::class.java)
    var incrementer: Transformer<Version, Version>
        get() = incrementerProperty.get() as Transformer<Version, Version>
        set(value) = incrementerProperty.set(value)

    @Internal
    val formatterProperty: Property<Any> = project.objects.property(Any::class.java)
    // Object type Any for groovy (GString) and kotlin (String) interoperability
    var formatter: Transformer<Any, Info>
        get() = formatterProperty.get() as Transformer<Any, Info>
        set(value) = formatterProperty.set(value)

    override fun toString(): String {
        return "Branch(regex=$regex, incrementer=$incrementer)"
    }
}
