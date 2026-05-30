/**
 * 简单线性回归 y=a+bx —— 趋势外推预测
 * 输出拟合优度R²用于评估趋势可靠性
 */

package com.kunlun.kunlun_backend.algorithm.forecast;

/**
 * 简单线性回归 y = a + bx (用于趋势外推预测)
 */
public class LinearRegression {

    private double slope;
    private double intercept;
    private double rSquared;

    public void fit(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
            sumY2 += y[i] * y[i];
        }
        double denom = n * sumX2 - sumX * sumX;
        if (denom == 0) {
            slope = 0;
            intercept = sumY / n;
            return;
        }
        slope = (n * sumXY - sumX * sumY) / denom;
        intercept = (sumY - slope * sumX) / n;

        double ssTot = 0, ssRes = 0;
        double meanY = sumY / n;
        for (int i = 0; i < n; i++) {
            double pred = intercept + slope * x[i];
            ssTot += (y[i] - meanY) * (y[i] - meanY);
            ssRes += (y[i] - pred) * (y[i] - pred);
        }
        rSquared = ssTot > 0 ? 1 - ssRes / ssTot : 0;
    }

    public double predict(double x) {
        return intercept + slope * x;
    }

    public double getSlope() { return slope; }
    public double getIntercept() { return intercept; }
    public double getRSquared() { return rSquared; }
}
