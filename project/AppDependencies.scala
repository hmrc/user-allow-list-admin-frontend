import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.14.0"
  
  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"   % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"           % "6.7.0-play-28",
    "uk.gov.hmrc"             %% "internal-auth-client-play-28" % "1.2.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion,
    "org.mockito"             %% "mockito-scala"              % "1.16.42",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "2.27.2"
  ).map(_ % "test, it")
}
