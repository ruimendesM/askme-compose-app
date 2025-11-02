import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.ruimendes.convention.pathToPackageName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }

            extensions.configure<BuildKonfigExtension>() {
                packageName = target.pathToPackageName()
                defaultConfigs {
                    // add build configs when needed
                }
            }
        }
    }
}