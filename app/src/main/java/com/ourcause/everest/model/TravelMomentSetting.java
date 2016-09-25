package com.ourcause.everest.model;

import java.io.Serializable;

/**
 * Created by infonous on 16-9-3.
 */
public class TravelMomentSetting implements Serializable {

    private static final long serialVersionUID = -7020210544600464445L;

    private Integer travelNotesPtr;

    //长宽模式
    private Integer patternLength;
    private Integer patternWidth;

    //对角线项
    private Integer diagonalNumerator;
    private Double diagonalDenominator;
    private Double diagonalLength;
    private Double diagonalWidth;
    private boolean isDiagonalOK;

    //像素点项
    private Double pixelLength;
    private Double pixelWidth;
    private boolean isPixelOK;

    //校正： 因子系数
    private Double factorValue;

    public Integer getTravelNotesPtr() {
        return travelNotesPtr;
    }

    public void setTravelNotesPtr(Integer travelNotesPtr) {
        this.travelNotesPtr = travelNotesPtr;
    }

    public Integer getPatternLength() {
        return patternLength;
    }

    public void setPatternLength(Integer patternLength) {
        this.patternLength = patternLength;
    }

    public Integer getPatternWidth() {
        return patternWidth;
    }

    public void setPatternWidth(Integer patternWidth) {
        this.patternWidth = patternWidth;
    }

    public Integer getDiagonalNumerator() {
        return diagonalNumerator;
    }

    public void setDiagonalNumerator(Integer diagonalNumerator) {
        this.diagonalNumerator = diagonalNumerator;
    }

    public Double getDiagonalDenominator() {
        return diagonalDenominator;
    }

    public void setDiagonalDenominator(Double diagonalDenominator) {
        this.diagonalDenominator = diagonalDenominator;
    }

    public Double getDiagonalLength() {
        return diagonalLength;
    }

    public void setDiagonalLength(Double diagonalLength) {
        this.diagonalLength = diagonalLength;
    }

    public Double getDiagonalWidth() {
        return diagonalWidth;
    }

    public void setDiagonalWidth(Double diagonalWidth) {
        this.diagonalWidth = diagonalWidth;
    }

    public boolean isDiagonalOK() {
        return isDiagonalOK;
    }

    public void setDiagonalOK(boolean diagonalOK) {
        isDiagonalOK = diagonalOK;
    }

    public Double getPixelLength() {
        return pixelLength;
    }

    public void setPixelLength(Double pixelLength) {
        this.pixelLength = pixelLength;
    }

    public Double getPixelWidth() {
        return pixelWidth;
    }

    public void setPixelWidth(Double pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    public boolean isPixelOK() {
        return isPixelOK;
    }

    public void setPixelOK(boolean pixelOK) {
        isPixelOK = pixelOK;
    }

    public Double getFactorValue() {
        return factorValue;
    }

    public void setFactorValue(Double factorValue) {
        this.factorValue = factorValue;
    }
}
