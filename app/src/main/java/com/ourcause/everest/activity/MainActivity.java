package com.ourcause.everest.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ourcause.everest.R;
import com.ourcause.everest.utils.Dictionary;
import com.ourcause.everest.utils.SystemSettingUtil;
import com.ourcause.everest.utils.SystemStatusUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //控制按返回键时的退出界面的时间标识
    private long exitTime;

    //操作导航的填充资料
    private int[] icon = null;
    private String[] iconName = null;
    private List<Map<String, Object>> dataSource = new ArrayList<>();

    //当前拍摄的图片路径及文件名称
    private String IMAGE_FILE_NAME = "";
    private String IMAGE_FILE_PATH = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取时间标识
        exitTime = System.currentTimeMillis();

        //工具条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(this.getString(R.string.title_activity_main));
        toolbar.inflateMenu(R.menu.main_activity_actions);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                               @Override
                                               public boolean onMenuItemClick(MenuItem item) {
                                                   switch	(item.getItemId())
                                                   {
                                                       case R.id.menu_logout:
                                                           finish();
                                                           System.exit(0);
                                                           return true;
                                                       default:
                                                           return true;
                                                   }
                                               }
                                           });

        //填充操作导航的资料
        initDataSource();
        dataSource = createData();

        //绑定显示图标列表
        GridView gridView = (GridView) findViewById(R.id.grid_view_menu);

        SimpleAdapter simpleAdapterMenu = new SimpleAdapter(this, dataSource, R.layout.item_main_gridview,
                new String[]{"grid_view_image","grid_view_title"},
                new int[] {R.id.grid_view_image,R.id.grid_view_title}
        );
        gridView.setAdapter(simpleAdapterMenu);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                switch(icon[position]){
                    case R.drawable.measure:
                        IMAGE_FILE_PATH = "";
                        doneCallByCamera();
                        break;
                    case R.drawable.histroy:
                        SystemStatusUtil systemStatusUtil = new SystemStatusUtil(getApplicationContext());
                        systemStatusUtil.clearAll();

                        SystemSettingUtil done = new SystemSettingUtil(getApplicationContext());
                        done.clearAll();
                        break;
                    case R.drawable.cloud:

                        String f = "P60819-162255.jpg";

                        String p = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                + java.io.File.separator + "everest_images" + java.io.File.separator + "P60819-162255.jpg";

                        Intent intentMeasure = new Intent(MainActivity.this, MeasureActivity.class);
                        intentMeasure.putExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_NAME, f);
                        intentMeasure.putExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_PATH, p);

                        startActivity(intentMeasure);

                        break;
                    case R.drawable.setting:
                        Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intentSetting);
                        break;
                    case R.drawable.help:
                        break;
                    case R.drawable.home:
                        break;
                    default:
                        break;
                }
            }
        });


    }

    //初始化各字段资料
    public void initDataSource(){

        icon = new int[]{
                R.drawable.measure,
                R.drawable.histroy,
                R.drawable.cloud,
                R.drawable.setting,
                R.drawable.help,
                R.drawable.home
        };

        iconName = new String[]{
                this.getString(R.string.main_grid_view_measure),
                this.getString(R.string.main_grid_view_history),
                this.getString(R.string.main_grid_view_cloud),
                this.getString(R.string.main_grid_view_setting),
                this.getString(R.string.main_grid_view_help),
                this.getString(R.string.main_grid_view_home)
        };
    }

    //生成数据源
    public List<Map<String, Object>> createData(){
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("grid_view_image", icon[i]);
            map.put("grid_view_title", iconName[i]);
            dataSource.add(map);
        }

        return dataSource;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - exitTime > 2000){
                Toast.makeText(this, this.getString(R.string.tips_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    /******************** 相机图片操作处理 ***************/
    //调用系统相机: 将目标图片保存至指定目录
    private void doneCallByCamera(){

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        //获取系统相机所生成的图片文件的 URI 路径
        prepareForSaveImageFileFromCamera();

        if(null != IMAGE_FILE_PATH){
            if(!IMAGE_FILE_PATH.isEmpty()) {

                File file = new File(IMAGE_FILE_PATH);
                Uri imageUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }

        startActivityForResult(intent, Dictionary.SYSTEM_CAMERA_REQUEST_CODE);
    }

    //拼凑图片文件名称： 返回文件路径名 img_yyyyMMdd_HHmmss.jpg
    private String returnFileNameFormat(String prefix){

        String fileName = "";

        //取日期格式: yyyy-MM-dd HH:mm:ss
        String timeStamp =new SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
                .format(new Date());

        fileName += (prefix + "_" + timeStamp + ".jpg");

        return fileName;

    }

    //将系统相机生成的图片保存至指定目录
    private void prepareForSaveImageFileFromCamera() {

        String storageState = Environment.getExternalStorageState();

        if (Environment.MEDIA_REMOVED.equals(storageState)){
            Toast.makeText(getApplicationContext(), this.getString(R.string.tips_not_sd), Toast.LENGTH_SHORT).show();
            return;
        }

        //检测正式图片目录， 如果不存在则创建目录
        File mediaStorageDir = new File (Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                ,"everest_images");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Toast.makeText(getApplicationContext(), this.getString(R.string.tips_create_dir), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        /*
        //将变形图片保存至指定目录
        mediaStorageDir = new File (Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                ,"everest_translated_images");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Toast.makeText(getApplicationContext(), this.getString(R.string.tips_create_dir), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        */

        //相机图片及其实预览图片文件路径
        IMAGE_FILE_NAME = returnFileNameFormat("IMG");
        IMAGE_FILE_PATH = mediaStorageDir.getPath() + java.io.File.separator + IMAGE_FILE_NAME;

    }

    /******************** 相机图片操作处理 ***************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //从相机调用中返回
        if(resultCode==RESULT_OK ){
            switch(requestCode) {
                case 1001:              // Dictionary.SYSTEM_CAMERA_REQUEST_CODE
                    if(null == data) {

                        if(null != IMAGE_FILE_PATH){

                            if(!IMAGE_FILE_PATH.isEmpty()) {

                                Intent intent = new Intent(MainActivity.this, MeasureActivity.class);
                                intent.putExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_NAME, IMAGE_FILE_NAME);
                                intent.putExtra(Dictionary.ACTIVITY_CAMERA_IMAGE_FILE_PATH, IMAGE_FILE_PATH);

                                startActivity(intent);
                            }
                        }
                    }

                    break;
            }
        }
    }
}
