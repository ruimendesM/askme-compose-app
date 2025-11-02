package com.ruimendes.convention

import org.gradle.api.Project
import java.util.Locale

internal fun Project.pathToPackageName(): String {
    val relativePackageName = path.replace(":", ".")

    return "com.ruimendes${relativePackageName}"
}

internal fun Project.pathToResourcePrefix(): String {
    return path.replace(":", "_")
        .lowercase()
        .drop(1) + "_"
}

internal fun Project.pathToFrameworkName(): String {
    val parts = this.path.split(":", "-", "_", " ")

    return parts.joinToString("") {
        it.replaceFirstChar { char -> char.titlecase(Locale.ROOT) }
    }
}