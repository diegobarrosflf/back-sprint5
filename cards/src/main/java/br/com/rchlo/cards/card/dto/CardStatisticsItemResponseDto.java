package br.com.rchlo.cards.card.dto;

import java.util.DoubleSummaryStatistics;

public class CardStatisticsItemResponseDto {

    private final long count;
    private final double sum;
    private final double average;
    private final double max;
    private final double min;

    public CardStatisticsItemResponseDto(DoubleSummaryStatistics transactionSummaryStatistics) {
        this.count = transactionSummaryStatistics.getCount();
        this.sum = transactionSummaryStatistics.getSum();
        this.average = transactionSummaryStatistics.getAverage();
        this.max = transactionSummaryStatistics.getMax();
        this.min = transactionSummaryStatistics.getMin();
    }

    public long getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    public double getAverage() {
        return average;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }
}
