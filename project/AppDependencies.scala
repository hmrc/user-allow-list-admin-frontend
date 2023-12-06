import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.1.0"
  
  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"   % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"   % "8.1.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-30" % "1.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion,
    "org.mockito"             %% "mockito-scala"              % "1.17.29"
  ).map(_ % "test, it")
}
