package com.classification.text

import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.feature.{HashingTF, IDF, IDFModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Classification {

  def getDocumentsAndLabels(sc: SparkContext, segPath: String, labelListPath: String): (RDD[Seq[String]], Iterator[String]) = {
    (sc.textFile(segPath).map(_.split(",").toSeq), sc.textFile(labelListPath).collect().toSeq.toIterator)
  }

  def train(sc: SparkContext, trainSegPath: String, trainLabelListPath: String): NaiveBayesModel = {

    val (documents, labelList) = getDocumentsAndLabels(sc, trainSegPath, trainLabelListPath)
    val hashingTF: HashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)
    tf.cache()
    val idf: IDFModel = new IDF().fit(tf)
    val tfIdf: RDD[Vector] = idf.transform(tf)
    val training: RDD[LabeledPoint] = tfIdf.map {
      vector: Vector => LabeledPoint(getDoubleOfLabel(labelList.next()), vector)
    }
    training.foreach(println)
    NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")
  }

  def test(sc: SparkContext, testSegPath: String, testLabelListPath: String, model: NaiveBayesModel): Double = {
    val (documents, labelList) = getDocumentsAndLabels(sc, testSegPath, testLabelListPath)
    val hashingTF: HashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)
    tf.cache()
    val idf: IDFModel = new IDF().fit(tf)
    val tfIdf: RDD[Vector] = idf.transform(tf)
    val test: RDD[LabeledPoint] = tfIdf.map {
      vector: Vector => LabeledPoint(getDoubleOfLabel(labelList.next()), vector)
    }
    val predictionAndLabel: RDD[(Double, Double)] = test.map((p: LabeledPoint) => (model.predict(p.features), p.label))
    1.0 * predictionAndLabel.filter((x: (Double, Double)) => x._1 == x._2).count() / test.count()
  }

  def getDoubleOfLabel(label: String): Double = {
    label.split("-")(0).tail.toDouble
  }

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("Classification").setMaster("local")
    val sc: SparkContext = new SparkContext(conf)
    println(test(sc, "./test/test_seg.txt",
                     "./test/test_label_list.txt",
                      train(sc,
                        "./train/train_seg.txt",
                        "./train/train_label_list.txt"
                      )
                )
    )
  }
}
