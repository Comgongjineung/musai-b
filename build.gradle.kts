plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.musai"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"
extra["tanzuScgExtensionsVersion"] = "1.0.0"

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("mysql:mysql-connector-java")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
//	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	implementation("javazoom:jlayer:1.0.1")
	implementation("org.jaudiotagger:jaudiotagger:2.0.1")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation ("org.apache.httpcomponents:httpclient:4.5.13")
	implementation ("commons-codec:commons-codec:1.15")
	implementation("mysql:mysql-connector-java:8.0.33")

	implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation ("org.springframework.boot:spring-boot-starter-security")

	// Google 토큰 검증
	implementation ("com.google.api-client:google-api-client:2.2.0")
	implementation ("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
	implementation ("com.google.http-client:google-http-client-jackson2:1.43.3")

	// JWT 토큰 생성용 (jjwt 사용)
	implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON 처리

	//api 파서
	implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}

//dependencyManagement {
//	imports {
//		mavenBom("org.springframework.cloud:spring-cloud-dependencies${property("springCloudVersion")}")
//	}
//}

tasks.withType<Test> {
	useJUnitPlatform()
}
