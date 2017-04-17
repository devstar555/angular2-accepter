name := """accepter"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

retrieveManaged := true

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "io.swagger" %% "swagger-play2" % "1.5.3+",
  "com.h2database" % "h2" % "1.4.192",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.5.4"
)

EclipseKeys.preTasks := Seq(compile in Compile)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java 
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)
