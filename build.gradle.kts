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
        maven("https://repo.purpurmc.org/snapshots")
        maven("https://maven.enginehub.org/repo/")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.GRAAL_VM)
        }

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(17)
    }
}
