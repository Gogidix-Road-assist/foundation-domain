package com.gogidix.rapidassist.ai.forecasting.domain.model;

/**
 * Enum representing the type of forecasting model.
 */
public enum ForecastModelType {
    ARIMA,
    PROPHET,
    LSTM,
    XGBOOST,
    LINEAR_REGRESSION,
    MOVING_AVERAGE,
    EXPONENTIAL_SMOOTHING,
    ENSEMBLE
}
