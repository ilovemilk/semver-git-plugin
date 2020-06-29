package io.wusa.extension

import io.wusa.Info
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
    val incrementerProperty: Property<String> = project.objects.property(String::class.java)
    var incrementer: String
        get() = incrementerProperty.get()
        set(value) = incrementerProperty.set(value)

    @Internal
    val snapshotSuffixProperty: Property<String> = project.objects.property(String::class.java)
    var snapshotSuffix: String
        get() = snapshotSuffixProperty.get()
        set(value) = snapshotSuffixProperty.set(value)

    @Internal
    val formatterProperty: Property<Any> = project.objects.property(Any::class.java)
    // Object type Any for groovy (GString) and kotlin (String) interoperability
    var formatter: Transformer<Any, Info>
        get() = formatterProperty.get() as Transformer<Any, Info>
        set(value) = formatterProperty.set(value)

    override fun toString(): String {
        return "Branch(regex=$regex, incrementer=$incrementer, snapshotSuffix=$snapshotSuffix)"
    }
}
