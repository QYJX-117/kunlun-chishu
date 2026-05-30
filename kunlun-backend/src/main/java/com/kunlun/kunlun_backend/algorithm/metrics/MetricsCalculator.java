/**
 * 预测评估指标计算器
 * MSE(均方误差)、MAE(平均绝对误差)、MAPE(平均绝对百分比误差)
 * RMSE(均方根误差)、TrackingSignal(跟踪信号)
 * |TS|>4时建议重新校准模型 参考：盛志刚(2014)
 */

package com.kunlun.kunlun_backend.algorithm.metrics;

/**
 * 预测评估指标计算器
 * 提供 MSE, MAE, MAPE, TrackingSignal 等标准指标
 */
public class MetricsCalculator {

    /**
     * 均方误差 MSE = Σ(actual - predicted)² / n
     */
    public static double calcMSE(double[] actual, double[] predicted) {
        double sum = 0;
        int n = Math.min(actual.length, predicted.length);
        for (int i = 0; i < n; i++) {
            double err = actual[i] - predicted[i];
            sum += err * err;
        }
        return n > 0 ? sum / n : 0;
    }

    /**
     * 平均绝对误差 MAE = Σ|actual - predicted| / n
     */
    public static double calcMAE(double[] actual, double[] predicted) {
        double sum = 0;
        int n = Math.min(actual.length, predicted.length);
        for (int i = 0; i < n; i++) {
            sum += Math.abs(actual[i] - predicted[i]);
        }
        return n > 0 ? sum / n : 0;
    }

    /**
     * 平均绝对百分比误差 MAPE = Σ|(actual - predicted) / actual| / n × 100%
     */
    public static double calcMAPE(double[] actual, double[] predicted) {
        double sum = 0;
        int count = 0;
        int n = Math.min(actual.length, predicted.length);
        for (int i = 0; i < n; i++) {
            if (actual[i] != 0) {
                sum += Math.abs((actual[i] - predicted[i]) / actual[i]);
                count++;
            }
        }
        return count > 0 ? (sum / count) * 100 : 0;
    }

    /**
     * 均方根误差 RMSE = √MSE
     */
    public static double calcRMSE(double[] actual, double[] predicted) {
        return Math.sqrt(calcMSE(actual, predicted));
    }

    /**
     * 跟踪信号 TS = Σ误差 / MAD
     * |TS| > 4 时建议重新校准模型 (参考盛志刚2014)
     */
    public static double calcTrackingSignal(double[] actual, double[] predicted) {
        int n = Math.min(actual.length, predicted.length);
        if (n == 0) return 0;
        double sumError = 0, sumAbsError = 0;
        for (int i = 0; i < n; i++) {
            double err = predicted[i] - actual[i];
            sumError += err;
            sumAbsError += Math.abs(err);
        }
        return sumAbsError > 0 ? sumError / (sumAbsError / n) : 0;
    }
}
