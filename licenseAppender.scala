/*
* Copyright 2015-2016 Pragmukko Project [http://github.org/pragmukko]
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
*    [http://www.apache.org/licenses/LICENSE-2.0]
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

import java.io.{PrintWriter, File}

import scala.io.Source

type MyCloseable = {def close():Unit}

def readLicense(file:File) = {
  Source.fromFile(file).getLines().takeWhile(l => l.startsWith("/") || l.startsWith("*")).toList
}

implicit def toFile(name:String) : File = new File(name)

def arm[T, A <: MyCloseable](cls:A)(fnc: A => T) = {
  try {
    fnc(cls)
  } finally {
    cls.close()
  }
}

def prependLicence(file:File, license:List[String]) : Unit = {
  print(file.getPath + "....")
  val content = arm(Source.fromFile(file)) { s => s.getLines().toList }
  if (content.take(license.length) == license) {
    print("skip"); println()
    return
  }

  arm(new PrintWriter(file)) {
    writer =>
      (license ++ content).foreach(writer.println(_))
      print("ok"); println()
  }
}

def fileTypes(f:File) =
  f.getName.endsWith(".scala") ||
   // f.getName.endsWith(".js") ||
    f.getName.endsWith(".sbt")

val licenseLines = readLicense("licenseAppender.scala")
if (licenseLines.length == 0) {
  println("Missing license")
  System.exit(0)
}
println(licenseLines.mkString(System.lineSeparator()))

def overTree(dir:File) : Unit = {
  val files = dir.listFiles().toList
  files.filter(f => f.isFile && fileTypes(f)).foreach(prependLicence(_, licenseLines))
  files.filter(f => f.isDirectory).foreach(overTree(_))
}

overTree(".")



