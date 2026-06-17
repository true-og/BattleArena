repositories {
    mavenCentral()
}

dependencies {
    // Built from the libs/DiamondBank-OG submodule; provided at runtime by the DiamondBank-OG plugin.
    compileOnly(project(":libs:DiamondBank-OG"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
