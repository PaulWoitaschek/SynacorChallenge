plugins {
  alias(libs.plugins.kotlin.jvm)
  application
}

dependencies {
  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

application {
  mainClass.set("de.woitaschek.synacorchallenge.ChallengeKt")
}

tasks.named("run") {
  this as JavaExec
  standardInput = System.`in`
}
