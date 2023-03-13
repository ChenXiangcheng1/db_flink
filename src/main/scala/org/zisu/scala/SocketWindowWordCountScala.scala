package org.zisu.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * 需求：通过socket实时产生一些单词
 * 1使用Flink实时接收数据
 * 2对指定时间窗口内(例如：2秒)的数据进行聚合统计
 * 3并且把时间窗口内计算的结果打印出来
 */
object SocketWindowWordCountScala {
  def main(args: Array[String]): Unit = {
    // 获取运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 连接socket获取数据
    val text = env.socketTextStream("10.40.91.160", 9001)

    // 处理数据
    import org.apache.flink.api.scala._
    val wordCount = text.flatMap(_.split(" "))
      .map((_, 1))
      //      .keyBy(0)
      .keyBy(_._1)
      .timeWindow(Time.seconds(2))
      //      .sum(1)
      .reduce((t1, t2) => (t1._1, t1._2 + t2._2))

    // 使用一个线程执行打印操作
    wordCount.print().setParallelism(1)

    // 执行程序
    env.execute("SocketWindowWordCountScala")
  }
}
