package org.zisu.java;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * 需求：通过socket实时产生一些单词
 * 1使用Flink实时接收数据
 * 2对指定时间窗口内(例如：2秒)的数据进行聚合统计
 * 并且把时间窗口内计算的结果打印出来
 */
public class SocketWindowWordCountJava {
    public static void main(String[] args) throws Exception {
        // 1获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2连接socket获取数据。服务器通过nc -l 9001
        DataStreamSource<String> text = env.socketTextStream("10.40.91.160", 9001);

        // 3处理数据，指定transaction算子
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCount = text.flatMap(new FlatMapFunction<String, String>() {  // // 调用 flatMap 方法将每行输入数据切分成单词
            public void flatMap(String line, Collector<String> out) throws Exception {
                String[] words = line.split(" ");
                for (String word : words) {
                    out.collect(word);  // 使用 Collector 对象收集起来
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {  // 调用 map 方法将每个单词转换成一个元组
            public Tuple2<String, Integer> map(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }
        }).keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
            public String getKey(Tuple2<String, Integer> tup) throws Exception {
                return tup.f0;
            }
        })//.keyBy(0)  // 调用 keyBy 方法将元组按照第一个元素（即单词本身）分组
            .timeWindow(Time.seconds(2))  // 调用 timeWindow 方法将每个分组的数据按照指定的时间窗口进行分组
            .sum(1);  // 调用 sum 方法对分组的数据进行聚合统计

        // 4指定数据sink目的地，使用一个线程执行打印操作
        wordCount.print().setParallelism(1);  // 单线程输出

        // 5调用execute()执行程序
        env.execute("SocketWindowsWordCountJava");
    }
}
