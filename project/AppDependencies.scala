import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  
  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"   % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"   % "10.1.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-30" % "3.0.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion,
    "org.scalatestplus"       %% "mockito-3-4"                % "3.2.10.0"
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID] = Seq.empty
}
