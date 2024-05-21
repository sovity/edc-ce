import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC
import java.lang.String as JavaString


plugins {
    `java-library`
}

group = libs.versions.sovityEdcExtensionGroup.get()

buildscript {
    dependencies {
        classpath(libs.squareup.javapoet)
    }
}

val generateGradleVersions by tasks.creating {
    val generatedSourcesTarget = project.layout.buildDirectory.file("generated/sources/gradle/main/java")
    doLast {
        val versionsClass = TypeSpec.classBuilder("GradleVersions")
            .addModifiers(PUBLIC, FINAL)
            .addField(
                FieldSpec.builder(TypeName.get(JavaString::class.java), "POSTGRES_IMAGE_TAG")
                    .initializer("\$S", libs.versions.postgresDbImage.get())
                    .addModifiers(PUBLIC, STATIC, FINAL)
                    .build()
            )
            .build()
        val packageName = "de.sovity.edc.utils.versions"
        val javaFile = JavaFile.builder(packageName, versionsClass)
            .build()

        val target = file(
            generatedSourcesTarget
        )
        javaFile.writeTo(target)
    }
    sourceSets["main"].java.srcDir(generatedSourcesTarget)
}

tasks.getByName("compileJava") {
    dependsOn(generateGradleVersions)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
