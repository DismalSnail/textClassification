package com.classification.text

import java.io.File
import java.util

import org.ansj.domain.Result
import org.ansj.library.DicLibrary
import org.ansj.recognition.impl.StopRecognition
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._


object WordSplit {
  def corpusSegment(utfCorpusPath: String, utfSegmentPath: String, trainLabelListPath: String, trainSegmentPath: String): Unit = {
    var count = 0
    val labelList = new util.ArrayList[String]()
    val contextList = new util.ArrayList[String]()
    val corpusDir: Array[File] = new File(utfCorpusPath).listFiles()

    for (corpusClassDir: File <- corpusDir) {

      for (utfText <- corpusClassDir.listFiles()) {

        count = count + 1
        val textSeg: Result = ToAnalysis.parse(FileUtils.readFileToString(utfText)
          .replace("\r\n", "")
          .replace("\r", "")
          .replace("\n", "")
          .replace(" ", "")
          .replace("\u3000", "")
          .replace("\t", "")
          .replaceAll(s"\\pP|\\pS|\\pC|\\pN|\\pZ", "")
          .trim
        )

        val stopWordList: Seq[String] = FileUtils.readFileToString(new File("./stopWords/stop_word_chinese.txt"))
          .split("\r\n").toSeq

        val filter = new StopRecognition()
        filter.insertStopWords(stopWordList)
        val result: Result = textSeg.recognition(filter)


        contextList.add(result.toStringWithOutNature)
        labelList.add(corpusClassDir.getName)

      }
    }
    println(count)
    FileUtils.writeLines(new File(trainSegmentPath), "UTF-8", contextList)
    FileUtils.writeLines(new File(trainLabelListPath), "UTF-8", labelList)

  }

  def main(args: Array[String]): Unit = {
    corpusSegment("./train/utf_train_corpus/", "./train/utf_train_segment/",
      "./train/train_label_list.txt", "./train/train_seg.txt")

    corpusSegment("./test/utf_test_corpus/", "./test/utf_test_segment/",
      "./test/test_label_list.txt", "./test/test_seg.txt")
  }
}


