package com.ourcause.everest.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Matrix;
import android.graphics.Point;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import com.ourcause.everest.model.CameraImageInfo;
import com.ourcause.everest.R;
import com.ourcause.everest.utils.Dictionary;
import com.ourcause.everest.views.RulerView;


public class MeasureActivity extends AppCompatActivity {

    //页面组件
    private EditText edtVisualWidth = null;
    private EditText edtVisualHigh = null;

    //相机所拍摄的图片的基本信息
    private String IMAGE_FILE_NAME = "";
    private String IMAGE_FILE_PATH = "";
    public CameraImageInfo imageInfo = new CameraImageInfo();


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    //只执行一次， 设置图片参数内容
    private boolean isHaveInitRunnable = false;
    private final Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isHaveInitRunnable ) {

                isHaveInitRunnable = true;

                getImageInfo();

                Point point = getDefaultDisplaySize();

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutMeasure);

                //将变形图片大小与屏幕大小传入标尺
                RulerView rulerView1 = new RulerView(getApplicationContext(), imageInfo.getTranslatedWidth(), imageInfo.getTranslatedLength(), point.x, point.y);

                linearLayout.addView(rulerView1);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_measure);

        //控制界面的最大化和辅助输入窗口的显示
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.imageViewTarget);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        //初始化各组件
        edtVisualWidth = (EditText) findViewById(R.id.edtVisualWidth);
        edtVisualHigh = (EditText) findViewById(R.id.edtVisualHigh);

        //返回按钮的处理
        ImageButton imageButton = (ImageButton)findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {

                String widthSize = edtVisualWidth.getText().toString();
                String highSize = edtVisualHigh.getText().toString();

                //填充数据： EXIF 和 Measure 两项的数据
                imageInfo.setMagnification(1D);
                imageInfo.setMeasureWidth(Double.valueOf( (widthSize.isEmpty() ? "0": widthSize) ));
                imageInfo.setMeasureHeight(Double.valueOf( (highSize.isEmpty() ? "0": highSize) ));

                //将测量结果返回计算比对页面: 至少填写了一项数据
                if(widthSize.isEmpty() && highSize.isEmpty()) {

                    AlertDialog.Builder builder = new Builder(MeasureActivity.this);
                    builder.setMessage(getResources().getString(R.string.dialog_alert_measure_content));
                    builder.setTitle(getResources().getString(R.string.dialog_alert_measure_title));

                    //取消
                    builder.setNegativeButton(getResources().getString(R.string.dialog_alert_measure_button_1_title), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //设置焦点
                            edtVisualWidth.setFocusable(true);
                            edtVisualWidth.setFocusableInTouchMode(true);
                            edtVisualWidth.requestFocus();
                            edtVisualWidth.requestFocusFromTouch();

                            dialog.dismiss();
                        }
                    });

                    //忽略
                    builder.setPositiveButton(getResources().getString(R.string.dialog_alert_measure_button_2_title), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            visitCompareActivity(imageInfo);
                        }
                    });

                    builder.create().show();

                }else{

                    visitCompareActivity(imageInfo);
                }
            }
        });

        //传递图片路径， 并且显示
        //imageViewTarget = (ImageView) findViewById(R.id.imageViewTarget);
        Intent intent=getIntent();
        IMAGE_FILE_NAME = intent.getStringExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_NAME);
        IMAGE_FILE_PATH = intent.getStringExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_PATH);

    }

    //切换窗口： 计算比对页面
    public void visitCompareActivity(CameraImageInfo imageInfo){

        //将测量结果资料直接返回比对页面
        Intent intent = new Intent();
        intent.setClass(MeasureActivity.this, CompareActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Dictionary.ACTIVITY_CAMERA_IMAGE_INFO, imageInfo);
        intent.putExtras(bundle);

        startActivity(intent);

        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);

        mHandler.post(initRunnable);

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHandler.removeCallbacks(mShowPart2Runnable);
        mHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHandler.removeCallbacks(mHidePart2Runnable);
        mHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHandler.removeCallbacks(mHideRunnable);
        mHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //对图片进行处理，使之适应屏幕，并且绑定到页面
    public void getImageInfo(){

        Point point = getDefaultDisplaySize();

        imageInfo = readImageEXIF(IMAGE_FILE_PATH);
        imageInfo.setName(IMAGE_FILE_NAME);

        handleImageResource(Double.parseDouble(String.valueOf(point.x)),
                imageInfo.getWidth(),
                imageInfo.getLength(),
                IMAGE_FILE_PATH
                );

    }

    ////获取屏幕属性
    public Point getDefaultDisplaySize() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        return point;
    }

    /*生成一个与当前屏幕适配的图像源
        注意：
            1. 缩放之后屏幕显示的图片， 是让此图片的宽等于屏幕的宽度， 并且是保持原始图片的长宽之比;
                因为不是所有的手机屏幕与拍摄图片的长宽之比是一致的;
            2. 如果图片是横屏拍摄的则先旋转 90度为竖屏

     */
    public void handleImageResource(Double screenWidth, Integer imageWidth, Integer imageHeight, String filePath){

        ////获取屏幕属性
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);


        //确认图片竖横， 以便于在图像源中作旋转: 默认宽小于高， 也就是竖
        boolean isRotate = false;
        if(imageWidth > imageHeight){
            isRotate = true;
        }

        //图片比屏幕大: 计算出长与宽的缩小比率
        Double postWidthScaleRate = 1D;
        Double postHeightScaleRate;
        if(!isRotate){  //以竖屏方式拍摄

            if(imageWidth > screenWidth ) {
                postWidthScaleRate = (screenWidth / imageWidth);
            }

            postHeightScaleRate = ((imageHeight/imageWidth) * screenWidth)/imageHeight;  //保持原始的长宽之比

        }else{

            if(imageHeight > screenWidth ) {
                postWidthScaleRate = (screenWidth / imageHeight);
            }

            postHeightScaleRate = ((imageWidth/imageHeight) * screenWidth)/imageWidth;  //保持原始的长宽之比
        }

        Bitmap dsBitmap= BitmapFactory.decodeFile(filePath);
        Bitmap bitmap = null;
        Matrix matrix;

        //先旋转再缩放
        if((isRotate) && (postWidthScaleRate.compareTo(1D)==0)) {

            matrix = new Matrix();
            matrix.postRotate(90);

            bitmap = Bitmap.createBitmap(dsBitmap, 0, 0,
                    imageWidth,
                    imageHeight,
                    matrix, true);

        }else if((isRotate) && (postWidthScaleRate.compareTo(1D)!=0)){

            matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postScale(postWidthScaleRate.floatValue(), postHeightScaleRate.floatValue());

            bitmap = Bitmap.createBitmap(dsBitmap, 0, 0,
                    imageWidth,
                    imageHeight,
                    matrix, true);


        }else if((!isRotate) && (postWidthScaleRate.compareTo(1D)==0)){

            bitmap = Bitmap.createBitmap(dsBitmap, 0, 0,
                    imageWidth,
                    imageHeight,
                    null, true);

        }else if((!isRotate) && (postWidthScaleRate.compareTo(1D)!=0)){

            matrix = new Matrix();
            matrix.postScale(postWidthScaleRate.floatValue(), postHeightScaleRate.floatValue());

            bitmap = Bitmap.createBitmap(dsBitmap, 0, 0,
                    imageWidth,
                    imageHeight,
                    matrix, true);
        }

        //保存以按屏幕宽度缩放之后的图片
        //saveTranslatedBitmap(bitmap, fileName);

        dsBitmap.recycle();

        if(bitmap!=null) {
            //获取缩放之后的图片大小
            imageInfo.setTranslatedLength(bitmap.getHeight());
            imageInfo.setTranslatedWidth(bitmap.getWidth());

            //绑定图片源
            ((ImageView)mContentView).setImageBitmap(bitmap);
        }

    }

    /*
    //将变形图片保存至指定目录
    public void saveTranslatedBitmap(Bitmap bm, String fileName) {

        try {

            File f = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + java.io.File.separator + "everest_translated_images")
                    ,fileName);

            if (f.exists()) {

                f.delete();

            } else {

                try {

                    FileOutputStream out = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                    imageInfo.setTranslatedPath(f.getPath() + java.io.File.separator + fileName);

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    //获取图片 EXIF 信息
    private CameraImageInfo readImageEXIF(String path) {

        CameraImageInfo itemImageInfo = new CameraImageInfo();

        try {

            ExifInterface exif = new ExifInterface(path);

            String EXIF_TAG_DATETIME = exif.getAttribute(ExifInterface.TAG_DATETIME);                            //拍摄时间，取决于设备设置的时间。
            Integer EXIF_TAG_IMAGE_LENGTH = Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));    //图片高度。
            Integer EXIF_TAG_IMAGE_WIDTH = Integer.valueOf(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));      //图片宽度
            String EXIF_TAG_MAKE = exif.getAttribute(ExifInterface.TAG_MAKE);                                    //设备品牌
            Double EXIF_TAG_FOCAL_LENGTH;

            //解析焦距字串
            String tmp = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            if (tmp.indexOf("/") > 0) {
                String a = tmp.substring(0, tmp.indexOf("/"));
                String b = tmp.substring(tmp.indexOf("/") + 1);

                EXIF_TAG_FOCAL_LENGTH = (Double.parseDouble(a) / Double.parseDouble(b));

            } else {
                EXIF_TAG_FOCAL_LENGTH = Double.valueOf(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));  //焦距。
            }

            itemImageInfo.setPath(path);

            itemImageInfo.setCreateDateTime(EXIF_TAG_DATETIME);
            itemImageInfo.setFocalLength(EXIF_TAG_FOCAL_LENGTH);
            itemImageInfo.setLength(EXIF_TAG_IMAGE_LENGTH);
            itemImageInfo.setMake(EXIF_TAG_MAKE);
            itemImageInfo.setWidth(EXIF_TAG_IMAGE_WIDTH);

        }catch(Exception e){
            e.printStackTrace();
        }

        return itemImageInfo;
    }

}
