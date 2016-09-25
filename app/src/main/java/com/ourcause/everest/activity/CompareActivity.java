package com.ourcause.everest.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ourcause.everest.R;
import com.ourcause.everest.data.TravelNotesDao;
import com.ourcause.everest.model.CameraImageInfo;
import com.ourcause.everest.model.SystemSetting;
import com.ourcause.everest.utils.Dictionary;
import com.ourcause.everest.utils.SystemSettingUtil;

import java.io.File;
import java.text.NumberFormat;

public class CompareActivity extends AppCompatActivity {

    //相片属性参数类 / 当前配置
    public CameraImageInfo itemImageInfo = new CameraImageInfo();
    public SystemSetting itemSystemSetting = new SystemSetting();

    //页面 UI 标签
    private EditText edtRealWidth = null;
    private EditText edtRealHigh = null;
    private EditText edtVisualWidth = null;
    private EditText edtVisualHigh = null;
    private ImageView imageViewBrowse = null;
    private TextView labResultByHigh = null;
    private TextView labResultByWide = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        //工具条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.title_activity_compare));
        toolbar.inflateMenu(R.menu.compare_activity_actions);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch	(item.getItemId())
                {
                    case R.id.menu_compare_activity_home: //返回主页
                        finish();
                        return true;
                    case R.id.menu_compare_activity_save: //记录本次测量资料
                        save(itemImageInfo, itemSystemSetting, "");
                        return true;
                    case R.id.menu_compare_activity_save_base: //将本次测量资料转化为参照物资料
                        return true;
                    case R.id.menu_compare_activity_save_cloud:
                        return true;
                    default:
                        return true;
                }
            }
        });

        //设置返回按钮
        if(getSupportActionBar() != null) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setSupportActionBar(toolbar);
        }

        //初始化页面控件
        initUI();

        //获取页面传递参数
        getIntentResult();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSystemSettingInfo();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSystemSettingInfo();
    }

    //获取当前系统配置信息
    public void getSystemSettingInfo(){
        SystemSettingUtil systemSettingUtil = new SystemSettingUtil(getApplication());
        itemSystemSetting = systemSettingUtil.getSystemSetting();
    }

    //初始化各个页面标签 UI
    public void initUI(){

        //参照物
        edtRealWidth = (EditText) findViewById(R.id.edtRealWidth);
        edtRealWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
                compare(itemImageInfo, itemSystemSetting);
            }
        });

        edtRealHigh = (EditText) findViewById(R.id.edtRealHigh);
        edtRealHigh.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
                compare(itemImageInfo, itemSystemSetting);
            }
        });


        //成像
        edtVisualWidth = (EditText) findViewById(R.id.edtVisualWidth);
        edtVisualWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
                compare(itemImageInfo, itemSystemSetting);
            }
        });

        edtVisualHigh = (EditText) findViewById(R.id.edtVisualHigh);
        edtVisualHigh.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {//在输入数据时监听
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,//输入数据之前的监听
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {//输入数据之后监听
                compare(itemImageInfo, itemSystemSetting);
            }
        });


        //图像
        imageViewBrowse = (ImageView) findViewById(R.id.imageViewBrowse);

        //计算结果: 高度与宽度
        labResultByHigh = (TextView) findViewById(R.id.labResultByHigh);
        labResultByWide = (TextView) findViewById(R.id.labResultByWide);

    }

    //获取并且填充从测量页面传递过来的测量数据
    public void getIntentResult() {

        Intent intent = this.getIntent();

        itemImageInfo = (CameraImageInfo)intent.getSerializableExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_INFO);

        if(itemImageInfo!=null){

            if(itemImageInfo.getMeasureWidth().compareTo(0D)!=0)
                edtVisualWidth.setText(itemImageInfo.getMeasureWidth().toString());

            if(itemImageInfo.getMeasureHeight().compareTo(0D)!=0)
                edtVisualHigh.setText(itemImageInfo.getMeasureHeight().toString());

            //获取和设置图片 URI
            File file = new File(itemImageInfo.getPath());
            Uri imageUri = Uri.fromFile(file);

            imageViewBrowse.setImageURI(imageUri);

        }

    }

    /*********************************** 保存本次测量资料 **************************************************/
    public void save(CameraImageInfo itemImageInfo, SystemSetting itemSystemSetting, String notes){

        TravelNotesDao dao = new TravelNotesDao(getApplicationContext());
        dao.add(itemImageInfo, itemSystemSetting, notes);
    }
    /*********************************** 保存本次测量资料 **************************************************/


    /*********************************** 计算距离 **************************************************/
    //分别以高和宽的数据来计算结果
    public void compare(CameraImageInfo imageInfo, SystemSetting systemSetting){

        //数据格式检测
        if(
                ((edtRealHigh.getText().length() > 0) && (edtVisualHigh.getText().length() > 0)) ||
                ((edtRealWidth.getText().length() > 0) && (edtVisualWidth.getText().length() > 0))
        ){

            Double resultHigh = 0D;
            Double resultWidth = 0D;

            //格式化输出
            NumberFormat fmt= NumberFormat.getNumberInstance() ;
            fmt.setMaximumFractionDigits(4);

            //填充具体数据
            imageInfo.setRealHeight((edtRealHigh.getText().length() > 0) ? Double.parseDouble(edtRealHigh.getText().toString()) : 0D);
            imageInfo.setRealWidth((edtRealWidth.getText().length() > 0) ? Double.parseDouble(edtRealWidth.getText().toString()) : 0D);
            imageInfo.setMeasureHeight((edtVisualHigh.getText().length() > 0) ? Double.parseDouble(edtVisualHigh.getText().toString()) : 0D);
            imageInfo.setMeasureWidth((edtVisualWidth.getText().length() > 0) ? Double.parseDouble(edtVisualWidth.getText().toString()) : 0D);


            //以对角线模式计算
            if((systemSetting != null)&&(systemSetting.isDiagonalOK())){

                resultHigh = compareByDiagonalLength(imageInfo, systemSetting);
                resultWidth = compareByDiagonalWidth(imageInfo, systemSetting);

                labResultByHigh.setText( resultHigh.compareTo(0D) > 0? fmt.format(resultHigh) : getString(R.string.lab_setting_null_space)  );
                labResultByWide.setText( resultWidth.compareTo(0D) > 0 ? fmt.format(resultWidth) : getString(R.string.lab_setting_null_space) );

            }else if((systemSetting != null)&&(systemSetting.isPixelOK())){

                resultHigh = compareByPixelLength(imageInfo, systemSetting);
                resultWidth = compareByPixelWidth(imageInfo, systemSetting);

                labResultByHigh.setText( resultHigh.compareTo(0D) > 0 ? fmt.format(resultHigh) : getString(R.string.lab_setting_null_space)  );
                labResultByWide.setText( resultWidth.compareTo(0D) > 0 ? fmt.format(resultWidth) : getString(R.string.lab_setting_null_space)  );

            }else{

                labResultByHigh.setText(getString(R.string.lab_setting_null_value));

                labResultByWide.setText(getString(R.string.lab_setting_null_value));

                Toast.makeText(getApplicationContext(), getString(R.string.tips_compare_error_1), Toast.LENGTH_SHORT).show();
            }

        }else{

            Toast.makeText(getApplicationContext(), getString(R.string.tips_compare_error_2), Toast.LENGTH_SHORT).show();

        }

    }

    //按对角线的长宽计算: 按高边的比例大小进行计算
    public Double compareByDiagonalLength(CameraImageInfo imageInfo, SystemSetting systemSetting){

        Double result = 0D;

        if((systemSetting != null) &&(systemSetting.getDiagonalLength().compareTo(0D) != 0 ) ){

            Double length = imageInfo.getMeasureHeight();

            Double diagonalLength = systemSetting.getDiagonalLength();
            Double realLength = imageInfo.getRealHeight();
            Double focalLength = imageInfo.getFocalLength();
            Double factor = systemSetting.getFactorValue();

            result = (realLength / (diagonalLength * (length/10))) * focalLength *factor;

        }

        return result;

    }

    //按对角线的长宽计算: 按宽边的比例大小进行计算
    public Double compareByDiagonalWidth(CameraImageInfo imageInfo, SystemSetting systemSetting){

        Double result = 0D;

        if((systemSetting != null) &&(systemSetting.getDiagonalWidth().compareTo(0D) != 0 ) ){

            Double width = imageInfo.getMeasureWidth();

            Double diagonalWidth = systemSetting.getDiagonalWidth();
            Double realWidth = imageInfo.getRealWidth();
            Double focalLength = imageInfo.getFocalLength();
            Double factor = systemSetting.getFactorValue();

            result = (realWidth / (diagonalWidth * (width/10))) * focalLength *factor;

        }

        return result;

    }

    //按像素点大小计算： 以图片的高度 × 像素点
    public Double compareByPixelLength(CameraImageInfo imageInfo, SystemSetting systemSetting){

        Double result = 0D;

        if((systemSetting != null) &&(systemSetting.getPixelLength().compareTo(0D) != 0 ) ){

            Double length = imageInfo.getMeasureHeight();

            Double pixelLength = systemSetting.getPixelLength();
            Double imageLength = Double.parseDouble( imageInfo.getLength().toString() );
            Double realLength = imageInfo.getRealHeight();
            Double focalLength = imageInfo.getFocalLength();
            Double factor = systemSetting.getFactorValue();

            result = (realLength / (  (pixelLength/1000) * (imageLength * (length/10)) )  ) * focalLength *factor;

        }

        return result;

    }

    //按像素点大小计算： 以图片的高度 × 像素点
    public Double compareByPixelWidth(CameraImageInfo imageInfo, SystemSetting systemSetting){

        Double result = 0D;

        if((systemSetting != null) &&(systemSetting.getPixelWidth().compareTo(0D) != 0 ) ){

            Double width = imageInfo.getMeasureWidth();

            Double pixelWidth = systemSetting.getPixelWidth();
            Double imageWidth = Double.parseDouble( imageInfo.getWidth().toString() );
            Double realWidth = imageInfo.getRealWidth();
            Double focalLength = imageInfo.getFocalLength();

            Double factor = systemSetting.getFactorValue();

            result = (realWidth / (  (pixelWidth/1000) * (imageWidth * (width/10)) )  ) * focalLength *factor;

        }

        return result;

    }
    /*********************************** 计算距离 **************************************************/

}
