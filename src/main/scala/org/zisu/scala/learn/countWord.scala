package org.zisu.scala.learn

import scala.io.Source._


object countWord {

  def main(args: Array[String]): Unit = {

    val line1 = fromFile("./a.txt").mkString
    val line2 = fromFile("./b.txt").mkString
    val l = List(1,  2, 3)
    val line = List(line1, line2)
    var sum = line.flatMap(_.split(" ")).map((_, 1)).map(_._2).sum
    println(sum)
  }

}
