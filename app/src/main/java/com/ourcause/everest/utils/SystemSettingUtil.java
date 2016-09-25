package com.ourcause.everest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ourcause.everest.model.SystemSetting;

/**
 * Created by infonous on 16-8-16.
 *
 * 获取系统设置各属性参数的公共类
 *
 */
public class SystemSettingUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SystemSettingUtil(Context context) {
        sp = context.getSharedPreferences("SystemSetting", context.MODE_PRIVATE);
        editor = sp.edit();
    }

    //清除所有的键值
    public void clearAll(){
        editor.clear();
        editor.commit();
    }

    //以 Meizu MX4 的参数作为初始值
    public SystemSetting newMeizuMx4SystemSetting(){

        SystemSetting item = new SystemSetting();

        item.setPatternLength( 4 );
        item.setPatternWidth( 3 );

        item.setDiagonalNumerator( 1 );
        item.setDiagonalDenominator( 2.3D );
        item.setDiagonalLength( 8.8348D );
        item.setDiagonalWidth( 6.6261D );
        item.setDiagonalOK( true );

        item.setPixelLength( 1.2D );
        item.setPixelWidth( 1.2D );
        item.setPixelOK( false );

        item.setFactorValue( 1.0D);

        return item;
    }

    //保存初始值: 以 Meizu MX4 的参数作为初始值
    public void restoreSystemSetting(){

        SystemSetting item = newMeizuMx4SystemSetting();

        saveSystemSetting(item);

    }

    //获取所有配置信息， 并且返回为类
    public SystemSetting getSystemSetting(){

        SystemSetting item = new SystemSetting();

        item.setPatternLength( getPatternLength() );
        item.setPatternWidth( getPatternWidth() );

        item.setDiagonalNumerator( getDiagonalNumerator() );
        item.setDiagonalDenominator( getDiagonalDenominator() );
        item.setDiagonalLength( getDiagonalLength() );
        item.setDiagonalWidth( getDiagonalWidth() );
        item.setDiagonalOK( isDiagonalOK() );

        item.setPixelLength( getPixelLength() );
        item.setPixelWidth( getPixelWidth() );
        item.setPixelOK( isPixelOK() );

        item.setFactorValue(getFactorValue());

        return item;

    }

    //以类为参数的保存
    public void saveSystemSetting(SystemSetting item){

        setPatternLength(item.getPatternLength());
        setPatternWidth(item.getPatternWidth());

        setDiagonalNumerator(item.getDiagonalNumerator());
        setDiagonalDenominator(item.getDiagonalDenominator());
        setDiagonalLength(item.getDiagonalLength());
        setDiagonalWidth(item.getDiagonalWidth());
        setDiagonalOK(item.isDiagonalOK());

        setPixelLength(item.getPixelLength() );
        setPixelWidth(item.getPixelWidth() );
        setPixelOK(item.isPixelOK() );

        setFactorValue(item.getFactorValue());

    }

    /**********************************************************************/


    public Integer getPatternLength() {
        return Integer.parseInt( sp.getString("PatternLength", "0") );
    }

    public void setPatternLength(Integer patternLength) {
        editor.putString("PatternLength", patternLength.toString());
        editor.commit();
    }

    public Integer getPatternWidth() {
        return Integer.parseInt( sp.getString("PatternWidth", "0") );
    }

    public void setPatternWidth(Integer patternWidth) {
        editor.putString("PatternWidth", patternWidth.toString());
        editor.commit();
    }

    public Integer getDiagonalNumerator() {
        return Integer.parseInt( sp.getString("DiagonalNumerator", "0") );
    }

    public void setDiagonalNumerator(Integer diagonalNumerator) {
        editor.putString("DiagonalNumerator", diagonalNumerator.toString());
        editor.commit();
    }

    public Double getDiagonalDenominator() {
        return Double.parseDouble( sp.getString("DiagonalDenominator", "0") );
    }

    public void setDiagonalDenominator(Double diagonalDenominator) {
        editor.putString("DiagonalDenominator", diagonalDenominator.toString());
        editor.commit();
    }

    public Double getDiagonalLength() {
        return Double.parseDouble( sp.getString("DiagonalLength", "0") );
    }

    public void setDiagonalLength(Double diagonalLength) {
        editor.putString("DiagonalLength", diagonalLength.toString());
        editor.commit();
    }

    public Double getDiagonalWidth() {
        return Double.parseDouble( sp.getString("DiagonalWidth", "0") );
    }

    public void setDiagonalWidth(Double diagonalWidth) {
        editor.putString("DiagonalWidth", diagonalWidth.toString());
        editor.commit();
    }

    public boolean isDiagonalOK() {
        return sp.getBoolean("isDiagonalOK", false);
    }

    public void setDiagonalOK(boolean diagonalOK) {
        editor.putBoolean("isDiagonalOK", diagonalOK);
        editor.commit();
    }

    public Double getPixelLength() {
        return Double.parseDouble( sp.getString("PixelLength", "0") );
    }

    public void setPixelLength(Double pixelLength) {
        editor.putString("PixelLength", pixelLength.toString());
        editor.commit();
    }

    public Double getPixelWidth() {
        return Double.parseDouble( sp.getString("PixelWidth", "0") );
    }

    public void setPixelWidth(Double pixelWidth) {
        editor.putString("PixelWidth", pixelWidth.toString());
        editor.commit();
    }

    public boolean isPixelOK() {
        return sp.getBoolean("isPixelOK", false);
    }

    public void setPixelOK(boolean pixelOK) {
        editor.putBoolean("isPixelOK", pixelOK);
        editor.commit();
    }

    public Double getFactorValue() {
        return Double.parseDouble( sp.getString("FactorValue", "1") );
    }

    public void setFactorValue(Double FactorValue) {
        editor.putString("FactorValue", FactorValue.toString());
        editor.commit();
    }

}
