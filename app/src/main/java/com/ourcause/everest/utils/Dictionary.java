package com.ourcause.everest.utils;

/**
 * 数据字典： 记录常量的描述
 * Created by infonous on 16-8-13.
 */
public class Dictionary {

    //英寸和毫米的换算
    public static Double IN_TO_MM = 25.4D;

    //各页面传递时的参数名称
    public static String ACTIVITY_CAMERA_IMAGE_FILE_NAME = "image_File_Name";
    public static String ACTIVITY_CAMERA_IMAGE_FILE_PATH = "Image_File_Path";
    public static String ACTIVITY_CAMERA_IMAGE_FILE_URI = "Image_File_Uri";
    public static String ACTIVITY_CAMERA_IMAGE_INFO = "imageInfo";

    //边线标识： 高边、宽边
    public static String LINE_HIGH = "HIGH";
    public static String LINE_WIDE = "WIDE";

    //DB
    public static String DB_NAME = "everest.db";
    public static Integer DB_VER = 1;

    //各页面返回码标识
    public static int SYSTEM_CAMERA_REQUEST_CODE = 1001;         //系统相机返回主页面
}
