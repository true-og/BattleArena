plugins {
    id("java")
    eclipse
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "org.battleplugins"
    version = "4.0.1"

    repositories {
        maven("https://repo.papermc.io/repository/maven-public")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
