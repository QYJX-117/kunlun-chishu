/**
 * 指数平滑预测算法
 * 支持一次平滑(St=αYt+(1-α)St-1)和二次平滑
 * α自动寻优：0.01~0.99步长0.01遍历，选MSE最小
 * 同时运行两种模型，自动选优输出
 * 参考：盛志刚(2014)云南大学MBA、赵振学等(2022)油气储运
 */

package com.kunlun.kunlun_backend.algorithm.forecast;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 指数平滑预测算法
 * 支持一次/二次指数平滑 + alpha自动寻优(步长0.01)
 * 参考：盛志刚(2014)云南大学MBA论文、赵振学等(2022)油气储运
 */
public class ExponentialSmoothing {

    private static final double ALPHA_MIN = 0.01;
    private static final double ALPHA_MAX = 0.99;
    private static final double ALPHA_STEP = 0.01;

    /**
     * 一次指数平滑 St = α*Yt + (1-α)*S(t-1)
     */
    public static List<Double> simpleSmooth(List<Double> data, double alpha) {
        List<Double> result = new ArrayList<>();
        if (data.isEmpty()) return result;
        double smoothed = data.get(0);
        result.add(smoothed);
        for (int i = 1; i < data.size(); i++) {
            smoothed = alpha * data.get(i) + (1 - alpha) * smoothed;
            result.add(smoothed);
        }
        return result;
    }

    /**
     * 二次指数平滑：在一次平滑基础上再做一次平滑，适合有趋势的数据
     */
    public static List<Double> doubleSmooth(List<Double> data, double alpha) {
        List<Double> firstSmooth = simpleSmooth(data, alpha);
        return simpleSmooth(firstSmooth, alpha);
    }

    /**
     * 计算预测值（未来N天总需求）
     * 同时用一次和二次平滑，选MSE更小的
     */
    public static ForecastResult predict(List<Double> data, int periods) {
        double bestAlpha1 = findBestAlpha(data, false);
        double bestAlpha2 = findBestAlpha(data, true);

        List<Double> ses = simpleSmooth(data, bestAlpha1);
        double mse1 = calcMSE(data, ses);
        double sesForecast = ses.get(ses.size() - 1);

        List<Double> des = doubleSmooth(data, bestAlpha2);
        double mse2 = calcMSE(data, des);
        double desForecast = 2 * des.get(des.size() - 1) - simpleSmooth(data, bestAlpha2).get(data.size() - 1);

        ForecastResult result = new ForecastResult();
        if (mse1 <= mse2) {
            result.setModelName("SINGLE_EXP_SMOOTH");
            result.setAlpha(bestAlpha1);
            result.setMse(mse1);
            result.setForecastValue(sesForecast * periods);
            result.setSmoothValues(ses);
        } else {
            result.setModelName("DOUBLE_EXP_SMOOTH");
            result.setAlpha(bestAlpha2);
            result.setMse(mse2);
            result.setForecastValue(Math.max(0, desForecast * periods));
            result.setSmoothValues(des);
        }
        return result;
    }

    /**
     * alpha自动寻优，步长0.01，选MSE最小的
     */
    public static double findBestAlpha(List<Double> data, boolean doubleSmooth) {
        double bestAlpha = 0.30;
        double bestMSE = Double.MAX_VALUE;
        for (double alpha = ALPHA_MIN; alpha <= ALPHA_MAX; alpha += ALPHA_STEP) {
            List<Double> smoothed = doubleSmooth ? doubleSmooth(data, alpha) : simpleSmooth(data, alpha);
            double mse = calcMSE(data, smoothed);
            if (mse < bestMSE) {
                bestMSE = mse;
                bestAlpha = alpha;
            }
        }
        return Math.round(bestAlpha * 100.0) / 100.0;
    }

    private static double calcMSE(List<Double> actual, List<Double> predicted) {
        double sum = 0;
        int n = Math.min(actual.size(), predicted.size());
        for (int i = 1; i < n; i++) {
            double err = actual.get(i) - predicted.get(i - 1);
            sum += err * err;
        }
        return n > 1 ? sum / (n - 1) : 0;
    }

    public static class ForecastResult {
        private String modelName;
        private double alpha;
        private double mse;
        private double forecastValue;
        private List<Double> smoothValues;

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public double getAlpha() { return alpha; }
        public void setAlpha(double alpha) { this.alpha = alpha; }
        public double getMse() { return mse; }
        public void setMse(double mse) { this.mse = mse; }
        public double getForecastValue() { return forecastValue; }
        public void setForecastValue(double forecastValue) { this.forecastValue = forecastValue; }
        public List<Double> getSmoothValues() { return smoothValues; }
        public void setSmoothValues(List<Double> smoothValues) { this.smoothValues = smoothValues; }
    }
}
