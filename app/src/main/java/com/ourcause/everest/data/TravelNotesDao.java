package com.ourcause.everest.data;

import android.content.Context;
import android.database.Cursor;

import com.ourcause.everest.model.CameraImageInfo;
import com.ourcause.everest.model.SystemSetting;
import com.ourcause.everest.model.TravelCameraImageInfo;
import com.ourcause.everest.model.TravelMomentSetting;
import com.ourcause.everest.model.TravelNotes;
import com.ourcause.everest.utils.DatabaseUtil;
import com.ourcause.everest.utils.Dictionary;

import java.util.Date;

/**
 * Created by infonous on 16-9-3.
 *
 * 将测量信息（图片 + 配置参数）保存在本地
 *
 */
public class TravelNotesDao {

    /************************* CREATE TABLE SQL **********************************************/

    //生成递增编号
    public String TRAVEL_NOTES_SEQ_CREATE_TABLE = "CREATE TABLE TRAVEL_NOTES_SEQ (PTR INTEGER PRIMARY KEY AUTOINCREMENT, DATE_TIME TIMESTAMP)";

    //生成主表
    public String TRAVEL_NOTES_CREATE_TABLE = "CREATE TABLE TRAVEL_NOTES (TRAVEL_NOTES_PTR INTEGER PRIMARY KEY, NOTES VARCHAR(320), DATE_TIME TIMESTAMP)";

    //当时的系统配置参数
    public String TRAVEL_NOTES_MOMENT_SETTING_CREATE_TABLE = "CREATE TABLE TRAVEL_MOMENT_SETTING (" +
            "TRAVEL_NOTES_PTR INTEGER PRIMARY KEY, " +
            "PATTERN_LENGTH INTEGER, " +
            "PATTERN_WIDTH INTEGER, " +
            "DIAGONAL_NUMERATOR INTEGER, " +
            "DIAGONAL_DENOMINATOR DOUBLE, " +
            "DIAGONAL_LENGTH DOUBLE, " +
            "DIAGONAL_WIDTH DOUBLE, " +
            "FLG_DIAGONAL_OK CHAR(1), " +
            "PIXEL_LENGTH DOUBLE, " +
            "PIXEL_WIDTH DOUBLE,  " +
            "FLG_PIXEL_OK CHAR(1), " +
            "FACTOR_VALUE DOUBLE" +
            ")";

    //图片参数
    public String TRAVEL_NOTES_CAMERA_IMAGE_INFO_CREATE_TABLE="CREATE TABLE TRAVEL_CAMERA_IMAGE_INFO (" +
            "TRAVEL_NOTES_PTR INTEGER PRIMARY KEY, " +
            "FILE_NAME VARCHAR(60)," +
            "PATH VARCHAR(400)," +
            "URI VARCHAR(400)," +
            "TRANSLATED_LENGTH INTEGER," +
            "TRANSLATED_WIDTH INTEGER," +
            "CREATE_DATE_TIME VARCHAR(60)," +
            "LENGTH  INTEGER," +
            "WIDTH INTEGER," +
            "MAKE VARCHAR(200)," +
            "MODEL VARCHAR(200)," +
            "FOCAL_LENGTH DOUBLE," +
            "REAL_WIDTH  DOUBLE," +
            "REAL_HEIGHT DOUBLE," +
            "MEASURE_WIDTH DOUBLE," +
            "MEASURE_HEIGHT  DOUBLE," +
            "MAGNIFICATION  DOUBLE" +
            ")";

    /************************* CREATE TABLE SQL **********************************************/

    /************************* INSERT TABLE SQL **********************************************/

    public String TRAVEL_NOTES_SEQ_INSERT = "INSERT INTO TRAVEL_NOTES_SEQ(DATE_TIME) VALUES(DATETIME())";

    public String TRAVEL_NOTES_SEQ_ROW_ID = "SELECT LAST_INSERT_ROWID() FROM TRAVEL_NOTES_SEQ";

    public String TRAVEL_NOTES_INSERT = "INSERT INTO TRAVEL_NOTES(TRAVEL_NOTES_PTR, NOTES, DATE_TIME) VALUES(?, ?, DATETIME())";

    public String TRAVEL_NOTES_MOMENT_SETTING_INSERT = "INSERT INTO TRAVEL_MOMENT_SETTING (" +
            "TRAVEL_NOTES_PTR, " +
            "PATTERN_LENGTH, " +
            "PATTERN_WIDTH, " +
            "DIAGONAL_NUMERATOR, " +
            "DIAGONAL_DENOMINATOR, " +
            "DIAGONAL_LENGTH, " +
            "DIAGONAL_WIDTH, " +
            "FLG_DIAGONAL_OK, " +
            "PIXEL_LENGTH, " +
            "PIXEL_WIDTH,  " +
            "FLG_PIXEL_OK, " +
            "FACTOR_VALUE" +
            ") VALUES(" +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?)";

    public String TRAVEL_NOTES_CAMERA_IMAGE_INFO_INSERT = "INSERT INTO TRAVEL_CAMERA_IMAGE_INFO (" +
            "TRAVEL_NOTES_PTR, " +
            "FILE_NAME," +
            "PATH," +
            "URI," +
            "TRANSLATED_LENGTH," +
            "TRANSLATED_WIDTH," +
            "CREATE_DATE_TIME," +
            "LENGTH," +
            "WIDTH," +
            "MAKE," +
            "MODEL," +
            "FOCAL_LENGTH," +
            "REAL_WIDTH," +
            "REAL_HEIGHT," +
            "MEASURE_WIDTH," +
            "MEASURE_HEIGHT," +
            "MAGNIFICATION" +
            ") VALUES(" +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?," +
            "?)";

    /************************* INSERT TABLE SQL **********************************************/



    public DatabaseUtil databaseUtil = null;

    public TravelNotesDao(Context context){
        databaseUtil = new DatabaseUtil(context, Dictionary.DB_NAME, Dictionary.DB_VER);
    }

    //创建历史测量的本地相关表： TravelNotesSeq TravelNotes TravelMomentSetting TravelCameraImageInfo
    public void createTravelNotesTables(){

        databaseUtil.execSQL(TRAVEL_NOTES_SEQ_CREATE_TABLE);

        databaseUtil.execSQL(TRAVEL_NOTES_CREATE_TABLE);

        databaseUtil.execSQL(TRAVEL_NOTES_MOMENT_SETTING_CREATE_TABLE);

        databaseUtil.execSQL(TRAVEL_NOTES_CAMERA_IMAGE_INFO_CREATE_TABLE);

    }

    //保存一条信息
    public void add(CameraImageInfo itemImageInfo, SystemSetting itemSystemSetting, String notes){

        int travelNotesPtr = -1;

        //插入一条记录， 再获取序号作标识
        databaseUtil.execSQL(TRAVEL_NOTES_SEQ_INSERT);
        Cursor cursor = databaseUtil.rawQuerySQL(TRAVEL_NOTES_SEQ_ROW_ID, null);
        if(cursor.moveToFirst()) {
            travelNotesPtr = cursor.getInt(0);
        }

        if(travelNotesPtr > 0){

            //插入图片属性表
            TravelCameraImageInfo itemTravelCameraImageInfo = convertByCameraImageInfo(travelNotesPtr, itemImageInfo);

            //插入系统参数表
            TravelMomentSetting itemTravelMomentSetting = convertBySystemSetting(travelNotesPtr, itemSystemSetting);

            //填充主体数据
            TravelNotes itemTravelNotes = new TravelNotes();
            itemTravelNotes.setTravelNotesPtr(travelNotesPtr);
            itemTravelNotes.setItemTravelCameraImageInfo(itemTravelCameraImageInfo);
            itemTravelNotes.setItemTravelMomentSetting(itemTravelMomentSetting);
            itemTravelNotes.setLastUpdateDateTime(new Date());
            itemTravelNotes.setNotes(notes);

            //资料写入库: 保存主表的备注记录
            databaseUtil.execSQL(TRAVEL_NOTES_INSERT, new Object[]{
                    itemTravelNotes.getTravelNotesPtr(),
                    itemTravelNotes.getNotes()
            });

            //资料写入库: 设置库
            databaseUtil.execSQL(TRAVEL_NOTES_MOMENT_SETTING_INSERT, new Object[]{
                    itemTravelMomentSetting.getTravelNotesPtr(),
                    itemTravelMomentSetting.getPatternLength(),
                    itemTravelMomentSetting.getPatternWidth(),
                    itemTravelMomentSetting.getDiagonalNumerator(),
                    itemTravelMomentSetting.getDiagonalDenominator(),
                    itemTravelMomentSetting.getDiagonalLength(),
                    itemTravelMomentSetting.getDiagonalWidth(),
                    itemTravelMomentSetting.isDiagonalOK(),
                    itemTravelMomentSetting.getPixelLength(),
                    itemTravelMomentSetting.getPixelWidth(),
                    itemTravelMomentSetting.isPixelOK(),
                    itemTravelMomentSetting.getFactorValue()
            });


            //资料写入库: 图片库
            databaseUtil.execSQL(TRAVEL_NOTES_CAMERA_IMAGE_INFO_INSERT, new Object[]{
                    itemTravelCameraImageInfo.getTravelNotesPtr(),
                    itemTravelCameraImageInfo.getFileName(),
                    itemTravelCameraImageInfo.getPath(),
                    itemTravelCameraImageInfo.getUri(),
                    itemTravelCameraImageInfo.getTranslatedLength(),
                    itemTravelCameraImageInfo.getTranslatedWidth(),
                    itemTravelCameraImageInfo.getCreateDateTime(),
                    itemTravelCameraImageInfo.getLength(),
                    itemTravelCameraImageInfo.getWidth(),
                    itemTravelCameraImageInfo.getMake(),
                    itemTravelCameraImageInfo.getModel(),
                    itemTravelCameraImageInfo.getFocalLength(),
                    itemTravelCameraImageInfo.getRealWidth(),
                    itemTravelCameraImageInfo.getRealHeight(),
                    itemTravelCameraImageInfo.getMeasureWidth(),
                    itemTravelCameraImageInfo.getMeasureHeight(),
                    itemTravelCameraImageInfo.getMagnification()
            });

        }

    }

    //将现行的图片属性写入库表
    public TravelCameraImageInfo convertByCameraImageInfo(int travelNotesPtr, CameraImageInfo itemImageInfo){

        TravelCameraImageInfo itemTravelCameraImageInfo = new TravelCameraImageInfo();

        itemTravelCameraImageInfo.setTravelNotesPtr(travelNotesPtr);

        itemTravelCameraImageInfo.setFileName(itemImageInfo.getName());             //文件名称
        itemTravelCameraImageInfo.setPath(itemImageInfo.getPath());                 //文件路径
        itemTravelCameraImageInfo.setUri(itemImageInfo.getUri().toString());        //URI 资源路径

        itemTravelCameraImageInfo.setTranslatedLength(itemImageInfo.getTranslatedLength());   //原图缩放保存之后的图片宽
        itemTravelCameraImageInfo.setTranslatedWidth(itemImageInfo.getTranslatedWidth());    //原图缩放保存之后的图片宽

        itemTravelCameraImageInfo.setCreateDateTime(itemImageInfo.getCreateDateTime());  //图片拍摄时间
        itemTravelCameraImageInfo.setLength(itemImageInfo.getLength());                 //图片像素：长宽
        itemTravelCameraImageInfo.setWidth(itemImageInfo.getWidth());                   //图片像素：长宽
        itemTravelCameraImageInfo.setMake(itemImageInfo.getMake());                     //制造商
        itemTravelCameraImageInfo.setModel(itemImageInfo.getModel());                   //相机型号
        itemTravelCameraImageInfo.setFocalLength(itemImageInfo.getFocalLength());       //焦距

        itemTravelCameraImageInfo.setRealWidth(itemImageInfo.getRealWidth());    //真实的物体宽宽
        itemTravelCameraImageInfo.setRealHeight(itemImageInfo.getRealHeight());   //真实的物体高度

        itemTravelCameraImageInfo.setMeasureWidth(itemImageInfo.getMeasureWidth());    //测量之目标宽
        itemTravelCameraImageInfo.setMeasureHeight(itemImageInfo.getMeasureHeight());   //测量之目标高
        itemTravelCameraImageInfo.setMagnification(itemImageInfo.getMagnification());   //在拍摄图片时， 用户可能作放大操作，默认为 1 倍，也就是不变

        return itemTravelCameraImageInfo;
    }

    //将现行的系统配置写入库表
    public TravelMomentSetting convertBySystemSetting(int travelNotesPtr, SystemSetting itemSystemSetting){

        TravelMomentSetting itemTravelMomentSetting = new TravelMomentSetting();
        itemTravelMomentSetting.setTravelNotesPtr(travelNotesPtr);

        //长宽模式
        itemTravelMomentSetting.setPatternLength(itemSystemSetting.getPatternLength());
        itemTravelMomentSetting.setPatternWidth(itemSystemSetting.getPatternWidth());

        //对角线项
        itemTravelMomentSetting.setDiagonalNumerator(itemSystemSetting.getDiagonalNumerator());
        itemTravelMomentSetting.setDiagonalDenominator(itemSystemSetting.getDiagonalDenominator());
        itemTravelMomentSetting.setDiagonalLength(itemSystemSetting.getDiagonalLength());
        itemTravelMomentSetting.setDiagonalWidth(itemSystemSetting.getDiagonalWidth());
        itemTravelMomentSetting.setDiagonalOK(itemSystemSetting.isDiagonalOK());

        //像素点项
        itemTravelMomentSetting.setPixelLength(itemSystemSetting.getPixelLength());
        itemTravelMomentSetting.setPixelWidth(itemSystemSetting.getPixelWidth());
        itemTravelMomentSetting.setPixelOK(itemSystemSetting.isPixelOK());

        return itemTravelMomentSetting;

    }


}