package com.ourcause.everest.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by infonous on 16-8-16.
 *
 * 记录 APP 状态， 作为执行的标识
 */
public class SystemStatusUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SystemStatusUtil(Context context) {
        sp = context.getSharedPreferences("SystemStatus", context.MODE_PRIVATE);
        editor = sp.edit();
    }

    //清除所有的键值
    public void clearAll(){
        editor.clear();
        editor.commit();
    }

    //标识： 是否已把通用性的参数写入公共库
    public boolean isRestoreSystemSetting() {
        return sp.getBoolean("System_Setting_Have_Been_Set_Up", false);
    }

    public void restoreSystemSetting(boolean status) {
        editor.putBoolean("System_Setting_Have_Been_Set_Up", status);
        editor.commit();
    }

    //标识： 进入 “设置” 页面时是否弹出 “帮助信息” 说明窗口
    public boolean isReadHelpSystemSetting() {
        return sp.getBoolean("System_Setting_Help_Info_Have_Been_Read", false);
    }

    public void readHelpSystemSetting(boolean status) {
        editor.putBoolean("System_Setting_Help_Info_Have_Been_Read", status);
        editor.commit();
    }

}
