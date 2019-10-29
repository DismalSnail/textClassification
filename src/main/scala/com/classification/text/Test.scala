package com.classification.text
import java.io.File

import org.apache.commons.io.FileUtils

object Test {
  def main(args: Array[String]): Unit = {
    val file = new File("./train/train_seg.txt")
    val setList = FileUtils.readLines(file)
    println("s")

  }
}
