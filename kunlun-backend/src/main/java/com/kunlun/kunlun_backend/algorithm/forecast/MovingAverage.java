/**
 * 移动平均预测 —— 作为指数平滑的基准对比算法
 * 简单移动平均和加权移动平均
 */

package com.kunlun.kunlun_backend.algorithm.forecast;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动平均法 —— 作为预测基准对比
 */
public class MovingAverage {

    /**
     * 简单移动平均
     */
    public static List<Double> simpleMA(List<Double> data, int window) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i < window - 1) {
                result.add(data.get(i));
                continue;
            }
            double sum = 0;
            for (int j = i - window + 1; j <= i; j++) {
                sum += data.get(j);
            }
            result.add(sum / window);
        }
        return result;
    }

    /**
     * 预测：取最近window天的均值作为未来预测
     */
    public static double predict(List<Double> data, int window, int forecastDays) {
        int n = Math.min(window, data.size());
        double sum = 0;
        for (int i = data.size() - n; i < data.size(); i++) {
            sum += data.get(i);
        }
        return (sum / n) * forecastDays;
    }
}
