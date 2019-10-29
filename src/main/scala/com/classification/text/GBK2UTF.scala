package com.classification.text

import java.io.File

import org.apache.commons.io.FileUtils

object GBK2UTF {
  def GBK2UTF8(GBKCorpusPath: String, UTF8CorpusPath: String): Unit = {

    val GBKCorpusDir: Array[File] = new File(GBKCorpusPath).listFiles()
    val UTFCorpusDir: File = new File(UTF8CorpusPath);
    if (!UTFCorpusDir.exists()) {
      UTFCorpusDir.mkdir()
    }

    for (gbkClassDir: File <- GBKCorpusDir) {

      val UTFClassDirPath: String = UTF8CorpusPath + gbkClassDir.getName
      val UTFClassDir: File = new File(UTFClassDirPath)
      if (!UTFClassDir.exists()) {
        UTFClassDir.mkdir()
      }

      for (gbkText: File <- gbkClassDir.listFiles()) {

        FileUtils.write(new File(UTFClassDirPath + "/" + gbkText),
          FileUtils.readFileToString(gbkText, "GBK"), "UTF-8")
      }
    }

  }


  def main(args: Array[String]): Unit = {
    GBK2UTF8("./train_corpus/", "./utf_train_corpus/")
    GBK2UTF8("./test_corpus/", "./utf_test_corpus/")
  }

}

