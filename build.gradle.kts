import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
	id("org.springframework.boot") version "2.4.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.convert") version "1.5.10"
	id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
	kotlin("plugin.jpa") version "1.3.72"
	kotlin("kapt") version "1.4.10"
}

apply(plugin = "kotlin-kapt")

group = "com.yosep.product"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val snippetsDir by extra { file("build/generated-snippets") }
extra["springCloudVersion"] = "2020.0.2"

dependencies {
	// jpa querydsl
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//	implementation("com.querydsl:querydsl-jpa:4.3.1")
//	kapt("com.querydsl:querydsl-apt:4.3.1:jpa")
	// r2dbc
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
//  아직 mongoDB외 querydsl은 지원하지 않는듯...
//	api("javax.annotation:javax.annotation-api:1.3.2")
//	api("com.querydsl:querydsl-core:4.3.1")
//	api("com.querydsl:querydsl-sql:4.3.1")
//	api("io.r2dbc:r2dbc-spi:0.8.1.RELEASE")
//	implementation("com.infobip:infobip-spring-data-r2dbc-querydsl-boot-starter:5.4.0")
//	kapt("com.querydsl:querydsl-apt:4.3.1")

//    Querydsl을 위해 추가
//	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//	annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
//	api("com.querydsl:querydsl-apt:4.2.2:jpa")

	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.cloud:spring-cloud-starter-bus-amqp")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.hateoas:spring-hateoas") {
		exclude("org.apache.tomcat","spring-boot-starter-tomcat")
	}
	implementation("com.zaxxer:HikariCP:4.0.3")
	
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("com.h2database:h2")
	implementation("io.r2dbc:r2dbc-h2")
	implementation("org.mariadb:r2dbc-mariadb")
	implementation("org.mariadb.jdbc:mariadb-java-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
//	testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
	kotlin.srcDir("src/main/generated")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	dependsOn(tasks.test)

}

tasks.register("copyHTML", Copy::class) {
	dependsOn(tasks.findByName("asciidoctor"))
	from(file("build/asciidoc/html5"))
	into(file("src/main/resources/static/docs"))
}

tasks.bootJar {
	dependsOn(tasks.asciidoctor)
	dependsOn(tasks.getByName("copyHTML"))
}

