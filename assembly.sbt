import AssemblyKeys._

assemblySettings

jarName in assembly := "NewsExtractor.jar"

mainClass in assembly := Some("it.dtk.Main")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("org", "cyberneko", "html", xs @ _*) => MergeStrategy.last
    case x => old(x)
  }
}