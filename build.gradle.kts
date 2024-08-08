plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "lip6"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.github.bonede:tree-sitter:0.22.6")
    implementation("io.github.bonede:tree-sitter-java:0.21.0a")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation ("org.assertj:assertj-core:3.22.0")
    implementation ("org.springframework.boot:spring-boot-starter-web")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.kohsuke:github-api:1.123")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")


}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.test {
    useJUnitPlatform()
}