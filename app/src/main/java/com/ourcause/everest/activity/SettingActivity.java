package com.ourcause.everest.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ourcause.everest.R;
import com.ourcause.everest.model.SystemSetting;
import com.ourcause.everest.utils.Dictionary;
import com.ourcause.everest.utils.SystemSettingUtil;
import com.ourcause.everest.utils.SystemStatusUtil;

import java.text.NumberFormat;

public class SettingActivity extends AppCompatActivity {

    //页面 UI 控件
    private EditText edtPatternLength;
    private EditText edtPatternWidth;
    private CheckBox chkDiagonal;
    private EditText edtDiagonalLineNumerator;
    private EditText edtDiagonalLineDenominator;
    private CheckBox chkPixel;
    private EditText edtPixelLength;
    private EditText edtPixelWidth;
    private TextView labDiagonalCountLength;
    private TextView labDiagonalCountWidth;
    private EditText edtFactorValue;

    //系统配置的公共库操作类
    private SystemSettingUtil systemSettingUtil = null;

    //生成读取公共状态的子线程
    private boolean isInitContentUIFlg = false;   //线程中是否已从公共库读取配置参数，并写入页面标签的标识
    private SystemStatusUtil systemStatusUtil = null;
    private final Handler initHandler = new Handler();
    private final Runnable initRunnable = new Runnable() {
        @Override
        public void run() {

            /*
                判断公共库里是否已写入配置参数， 否则直接将通用的、默认的配置参数还原至公共库，
                稍候用户可以设置这些参数配置
             */
            if ( !systemStatusUtil.isRestoreSystemSetting() ){

                systemSettingUtil.restoreSystemSetting();

                systemStatusUtil.restoreSystemSetting(true); //更改已还原原始配置的标识

            }

            //从公共库读取配置参数， 并写入页面标签
            if(!isInitContentUIFlg){

                initContentUI();

                isInitContentUIFlg = true;
            }

            //是否不再弹出提示窗口
            if ( !systemStatusUtil.isReadHelpSystemSetting() ){

                showReadHelpInfoAlertDialog();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        systemSettingUtil = new SystemSettingUtil(getApplication());
        systemStatusUtil = new SystemStatusUtil(getApplication());

        //绑定页面控件的事件处理
        initControlUI();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //进入主页面之后， 作系统参数的初始化处理
        initHandler.post(initRunnable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //至少有一项是完整的才能保存
        if(checkDiagonalLine() || checkPixel()) {
            //退出时保存数据至公共库
            saveSystemSettingFromUI();
        }else{
            showCheckContentAlertDialog();
        }
    }

    //将页面标签内容写入公共库
    public void saveSystemSettingFromUI(){

        SystemSetting item = new SystemSetting();

        //如长宽模式任何一项为空则取标准的 4/3
        if((!edtPatternLength.getText().toString().isEmpty()) && (!edtPatternWidth.getText().toString().isEmpty())){
            item.setPatternLength(Integer.parseInt(edtPatternLength.getText().toString()));
            item.setPatternWidth(Integer.parseInt(edtPatternWidth.getText().toString()));
        }else{
            item.setPatternLength(4);
            item.setPatternWidth(3);
        }


        item.setDiagonalOK(chkDiagonal.isChecked());

        item.setDiagonalNumerator(Integer.parseInt(edtDiagonalLineNumerator.getText().toString().isEmpty() ? "0" : edtDiagonalLineNumerator.getText().toString()));
        item.setDiagonalDenominator(Double.parseDouble(edtDiagonalLineDenominator.getText().toString().isEmpty() ? "0" : edtDiagonalLineDenominator.getText().toString()));

        item.setDiagonalLength(Double.parseDouble(labDiagonalCountLength.getText().toString().isEmpty() ? "0" : labDiagonalCountLength.getText().toString()));
        item.setDiagonalWidth(Double.parseDouble(labDiagonalCountWidth.getText().toString().isEmpty() ? "0" : labDiagonalCountWidth.getText().toString()));


        item.setPixelOK(chkPixel.isChecked());

        item.setPixelLength((Double.parseDouble(edtPixelLength.getText().toString().isEmpty() ? "0" : edtPixelLength.getText().toString())));
        item.setPixelWidth((Double.parseDouble(edtPixelWidth.getText().toString().isEmpty() ? "0" : edtPixelWidth.getText().toString())));

        item.setFactorValue((Double.parseDouble(edtFactorValue.getText().toString().isEmpty() ? "1" : edtFactorValue.getText().toString())));

        systemSettingUtil.saveSystemSetting(item);

    }

    //填充各设置属性的具体内容
    public void initContentUI(){

        SystemSetting item = systemSettingUtil.getSystemSetting();

        edtPatternLength.setText( (item.getPatternLength().compareTo(0) == 0) ? "" : item.getPatternLength().toString());
        edtPatternWidth.setText( (item.getPatternWidth().compareTo(0) == 0) ? "" : item.getPatternWidth().toString());

        edtDiagonalLineNumerator.setText( (item.getDiagonalNumerator().compareTo(0) == 0) ? "" : item.getDiagonalNumerator().toString());
        edtDiagonalLineDenominator.setText( (item.getDiagonalDenominator().compareTo(0D) == 0) ? "" : item.getDiagonalDenominator().toString());

        //格式化输出
        NumberFormat fmt= NumberFormat.getNumberInstance() ;
        fmt.setMaximumFractionDigits(4);

        labDiagonalCountLength.setText(fmt.format(item.getDiagonalLength()));
        labDiagonalCountWidth.setText(fmt.format(item.getDiagonalWidth()));

        edtPixelLength.setText( (item.getPixelLength().compareTo(0D) == 0) ? "" : item.getPixelLength().toString());
        edtPixelWidth.setText( (item.getPixelWidth().compareTo(0D) == 0) ? "" : item.getPixelWidth().toString());

        //先给其它标签赋值，否则出现提示
        chkDiagonal.setChecked(item.isDiagonalOK());
        chkPixel.setChecked(item.isPixelOK());

        //校正系数
        edtFactorValue.setText(item.getFactorValue().toString());

    }

    //初始化各页面 UI 控件
    public void initControlUI(){
        //
        edtPatternLength = (EditText) findViewById(R.id.edt_pattern_length);
        edtPatternWidth = (EditText) findViewById(R.id.edt_pattern_width);

        chkDiagonal = (CheckBox) findViewById(R.id.chk_diagonal);
        edtDiagonalLineNumerator = (EditText) findViewById(R.id.edt_diagonal_line_numerator);
        edtDiagonalLineDenominator = (EditText) findViewById(R.id.edt_diagonal_line_denominator);

        chkPixel = (CheckBox) findViewById(R.id.chk_pixel);
        edtPixelLength = (EditText) findViewById(R.id.edt_pixel_length);
        edtPixelWidth = (EditText) findViewById(R.id.edt_pixel_width);

        labDiagonalCountLength = (TextView) findViewById(R.id.lab_setting_diagonal_line_count_length);
        labDiagonalCountWidth  = (TextView) findViewById(R.id.lab_setting_diagonal_line_count_width);

        edtFactorValue = (EditText) findViewById(R.id.edt_factor_value);
        edtFactorValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
                if(Double.parseDouble(edtFactorValue.getText().toString().isEmpty() ? "0" : edtFactorValue.getText().toString()) == 0D) {
                    Toast.makeText(getApplicationContext(), getString(R.string.tips_setting_error_3), Toast.LENGTH_SHORT).show();
                }
            }
        });


        /********************************* 传感器/像素点 勾选控制 ******************************************/
        /*    在对角线尺寸、像素点大小这两项设置中， 仅能选用其中一项   */
        chkDiagonal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    if(checkDiagonalLine()){
                        chkPixel.setChecked(false);
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.tips_setting_error_1), Toast.LENGTH_SHORT).show();
                        chkDiagonal.setChecked(false);
                    }
                }
            }
        });

        chkPixel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    if(checkPixel()){
                        chkDiagonal.setChecked(false);
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.tips_setting_error_2), Toast.LENGTH_SHORT).show();
                        chkPixel.setChecked(false);
                    }
                }
            }
        });

        /********************************* 长宽比模式文本的输入 ******************************************/
        //长宽比模式： 长度
        edtPatternLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByDiagonal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });

        //长宽比模式： 宽度
        edtPatternWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByDiagonal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });


        /********************************* 传感器文本的输入 ******************************************/
        //传感器： 长度
        edtDiagonalLineNumerator.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByDiagonal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });

        //传感器： 宽度
        edtDiagonalLineDenominator.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByDiagonal();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });


        /********************************* 像素点文本的输入 ******************************************/
        //像素点： 长度
        edtPixelLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByPixel();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });

        //像素点： 宽度
        edtPixelWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听

                refreshViewByPixel();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
            }
        });

    }

    /*
        1. 按文本的输入内容来判断勾选标签控件是否能用或有效
        2. 自动计算结果
     */
    public void refreshViewByDiagonal(){

        //传感器尺寸分子分母: 内容不完整或格式不当， 则禁用勾选
        if(checkDiagonalLine()){
            calculationByDiagonalLine();
            chkDiagonal.setEnabled(true);
        }else{
            chkDiagonal.setChecked(false);
            chkDiagonal.setEnabled(false);

            labDiagonalCountLength.setText("0");
            labDiagonalCountWidth.setText("0");
        }
    }


    /*
        按文本的输入内容来判断勾选标签控件是否能用或有效
     */
    public void refreshViewByPixel(){

        if(checkPixel()) {
            chkPixel.setEnabled(true);
        }else{
            chkPixel.setChecked(false);
            chkPixel.setEnabled(false);
        }

    }


    //在对角线尺寸的标签内容 数据检测
    public boolean checkDiagonalLine(){

        boolean isOK = false;

        //判断是否非空， 再判断是否等于
        if(
            (!edtDiagonalLineNumerator.getText().toString().isEmpty()) && (!edtDiagonalLineDenominator.getText().toString().isEmpty())
        ){

            Double numerator = Double.parseDouble("0" + edtDiagonalLineNumerator.getText().toString());
            Double denominator = Double.parseDouble("0" + edtDiagonalLineDenominator.getText().toString());

            if(
                (Double.compare( numerator, 0D) > 0) && (Double.compare( denominator, 0D) > 0)
             ){
                isOK = true;
            }
        }

        return isOK;

    }

    //像素点大小的标签内容 数据检测
    public boolean checkPixel(){

        boolean isOK = false;

        //判断是否非空， 再判断是否等于
        if(
                (!edtPixelLength.getText().toString().isEmpty()) && (!edtPixelWidth.getText().toString().isEmpty())
        ){

            Double length = Double.parseDouble("0" + edtPixelLength.getText().toString());
            Double width = Double.parseDouble("0" + edtPixelWidth.getText().toString());

            if(
                    (Double.compare( length, 0D) > 0) && (Double.compare( width, 0D) > 0)
            ){
                isOK = true;
            }
        }

        return isOK;

    }

    //按对角线长度分别推算出长与宽
    public void calculationByDiagonalLine(){

        //设置长宽比模式的默认值： 4/3
        if(edtPatternLength.getText().toString().isEmpty() || edtPatternWidth.getText().toString().isEmpty()){
            edtPatternLength.setText("4");
            edtPatternWidth.setText("3");
        }

        //获取长宽比例模式， 默认为4：3
        Double patternLength = Double.parseDouble( (edtPatternLength.getText().toString().isEmpty() ? "4" : edtPatternLength.getText().toString() ) );
        Double patternWidth = Double.parseDouble( (edtPatternWidth.getText().toString().isEmpty() ? "3" : edtPatternWidth.getText().toString()) );

        //获取对角线分子与分母
        Double numerator = Double.parseDouble(edtDiagonalLineNumerator.getText().toString());
        Double denominator = Double.parseDouble(edtDiagonalLineDenominator.getText().toString());

        //对角线的长度（毫米）
        Double diagonalLine = Dictionary.IN_TO_MM * (numerator / denominator);

        Double length = Math.sqrt((Math.pow(diagonalLine, 2) / (Math.pow(patternLength, 2) + Math.pow(patternWidth, 2))) * Math.pow(patternLength, 2));
        Double width = Math.sqrt((Math.pow(diagonalLine, 2) / (Math.pow(patternLength, 2) + Math.pow(patternWidth, 2))) * Math.pow(patternWidth, 2));

        NumberFormat fmt = NumberFormat.getNumberInstance();
        fmt.setMaximumFractionDigits(4);

        labDiagonalCountLength.setText(fmt.format(length));
        labDiagonalCountWidth.setText(fmt.format(width));

    }


    //是否不再弹出帮助对话框
    public void showReadHelpInfoAlertDialog(){

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_alert_system_setting_title))
                .setMessage(getResources().getString(R.string.dialog_alert_system_setting_content))
                .setPositiveButton(R.string.dialog_alert_system_setting_button_1_title, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //我知道了
                    }
                })
                .setNegativeButton(R.string.dialog_alert_system_setting_button_2_title, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //以后不再提示
                        systemStatusUtil.readHelpSystemSetting(true);
                    }
                }).show();
    }

    //弹出内容不完整的窗口提示
    public void showCheckContentAlertDialog(){

        /*
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_alert_system_setting_title_2))
                .setMessage(getResources().getString(R.string.dialog_alert_system_setting_content_2))
                .setPositiveButton(R.string.dialog_alert_system_setting_button_1_title_2, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //我知道了
                    }
                }).show();
               */

        Toast.makeText(getApplicationContext(), this.getString(R.string.dialog_alert_system_setting_content_2), Toast.LENGTH_SHORT).show();
    }

}
