package com.ourcause.everest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class RulerView extends View {

    //屏幕上缩放后图片大小 和 屏幕的尺寸大小
    private float image_size_width;
    private float image_size_height;
    private float screen_size_width;
    private float screen_size_height;

    // 创建 X 与 Y 轴方向上的刻度体系
    private ScaleInterval scaleInterX = new ScaleInterval();
    private ScaleInterval scaleInterY = new ScaleInterval();

    //建立瞄具
    private CrossStructure cross = new CrossStructure();

    //瞄具旋转斜率
    private float rotate = 0;

    Paint paintX = new Paint();
    Paint paintY = new Paint();

    public RulerView(Context context, int imageWidth, int imageHeight, int screenWidth, int screenHeight ) {
        super(context);

        image_size_width = imageWidth;
        image_size_height = imageHeight;
        screen_size_width = screenWidth;
        screen_size_height = screenHeight;

        newScaleIntervalXY();
        initCrossStructure();

        initPaint();

    }

    //初始化 X 与 Y 轴的画笔颜色
    public void initPaint(){

        paintX = new Paint();
        paintX.setColor(Color.rgb(74, 255, 255));
        paintX.setStyle(Style.STROKE);
        paintX.setStrokeWidth(2);
        paintX.setTextSize(30);

        paintY = new Paint();
        paintY.setColor(Color.rgb(230, 255, 0));
        paintY.setStyle(Style.STROKE);
        paintY.setStrokeWidth(2);
        paintY.setTextSize(30);
    }


    /*
        建立与当前屏幕大小无关的缩放后的图片测量刻度体系
        以达到与光学传感器设备成像大小的成比例测量目的
    */
    public void newScaleIntervalXY() {

        scaleInterX = new ScaleInterval();
        scaleInterX.setMax((image_size_width/10));
        scaleInterX.setMin((image_size_width/10/10));

        scaleInterY = new ScaleInterval();
        scaleInterY.setMax((image_size_height/10));
        scaleInterY.setMin((image_size_height/10/10));
    }

    //建立瞄具体系： 加入刻度
    public void initCrossStructure(){

        /*
        将变形图片分为 100 小格， 10 个大格：
            image_size_width / scaleInterX.getMin()
            image_size_height / scaleInterY.getMin()
         */
        cross = new CrossStructure(100, 100, (screen_size_width/2), (screen_size_height/2), 10,  10);
    }


    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.save();

        canvas.rotate(rotate, cross.getOriginX(), cross.getOriginY());

        int nText = 1;

        // X
        for (int i = 0; i <= cross.getStudioWidthX(); i++) {

            float offset = cross.getOriginX() - (cross.getStudioWidthX()/2 * scaleInterX.getMin());

            float cur_x =  offset + i * scaleInterX.getMin();

            float cur_y = cross.getOriginY();


            if (i % 10 == 0 && i != 0) {

                canvas.drawLine(cur_x, cur_y, cur_x, cur_y + 30, paintX);

                canvas.drawText(cross.getIntervalX().get(nText).toString(), cur_x, cur_y + 55, paintX);

                nText = nText + 1;

            }else if (i % 5 == 0 && i % 10 != 0 && i != 0) {

                canvas.drawLine(cur_x, cur_y, cur_x, cur_y + 20, paintX);

            } else if (i == 0) {

                canvas.drawLine(cur_x, cur_y, cur_x, cur_y + 30, paintX);

                canvas.drawText(cross.getIntervalX().get(0).toString(), cur_x, cur_y + 55, paintX);

            } else {

                canvas.drawLine( cur_x, cur_y, cur_x, cur_y + 10, paintX);
            }
        }

        nText = 1;

        // Y
        for (int i = 0; i <= cross.getStudioWidthY(); i++) {

            float offset = cross.getOriginY() - (cross.getStudioWidthY()/2*scaleInterY.getMin());

            float cur_x = cross.getOriginX();

            float cur_y = offset + i * scaleInterY.getMin();


            if (i % 10 == 0 && i != 0) {

                canvas.drawLine(cur_x, cur_y, cur_x + 30, cur_y, paintY);

                canvas.drawText(cross.getIntervalX().get(nText).toString(), cur_x + 55, cur_y, paintY);

                nText = nText + 1;

            }else if (i % 5 == 0 && i % 10 != 0 && i != 0) {

                canvas.drawLine(cur_x, cur_y, cur_x + 20, cur_y, paintY);

            } else if (i == 0) {

                canvas.drawLine(cur_x, cur_y, cur_x + 30, cur_y, paintY);

                canvas.drawText(cross.getIntervalX().get(0).toString() + "", cur_x + 55, cur_y, paintY);

            } else {

                canvas.drawLine( cur_x, cur_y, cur_x + 10, cur_y, paintY);
            }
        }

        canvas.restore();
    }


    public boolean onTouchEvent(MotionEvent event) {

        int pointerCount = event.getPointerCount();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                ////当手指滑动时， 坐标应该人性化地在手指的上方，免得自己都看不到位置。所以适当调节了下坐标距离
                cross.setOriginX(event.getX() - 10 * scaleInterX.getMin());
                cross.setOriginY(event.getY() - 10 * scaleInterY.getMin());

                if (
                        //(((screen_size_height/2) - (image_size_height/2)) > event.getY()) || (((screen_size_height/2) + (image_size_height/2)) < event.getY())

                        (screen_size_height/2) < event.getY()
                ){

                    return false;

                }


                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:

                ////当手指滑动时， 坐标应该人性化地在手指的上方，免得自己都看不到位置。所以适当调节了下坐标距离
                cross.setOriginX(event.getX() - 10 * scaleInterX.getMin());
                cross.setOriginY(event.getY() - 10 * scaleInterY.getMin());

                if(pointerCount == 2){

                    rotate = getRotateSlope(event);
                }

                invalidate();
                break;

        }

        return true;
    }

    //两点触摸时旋转的角度
    private float getRotateSlope(MotionEvent event) {

        double x = (event.getX(0) - event.getX(1));

        double y = (event.getY(0) - event.getY(1));

        double slope = Math.atan2(y, x);

        return (float) Math.toDegrees(slope);
    }

    //@Override
    //protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //    setMeasuredDimension((int)screen_size_width, (int)screen_size_height);
    //}
}


//刻度： 分两格， 例如厘米与毫米、分米与厘米
class ScaleInterval{

    private float max;

    private float min;

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }
}

//瞄具体系
class CrossStructure{

    private float studioWidthX;   // X 轴方向的长度， 即可多少个最小的标准间隔
    private float studioWidthY;   // Y 轴方向的长度， 即可多少个最小的标准间隔
    private float originX;  // 体系坐标原点 X
    private float originY;  // 体系坐标原点 Y
    private ArrayList intervalX = new ArrayList();      //自动生成每个最大刻度的文字
    private ArrayList intervalY = new ArrayList();      //

    public float getStudioWidthX() {
        return studioWidthX;
    }

    public void setStudioWidthX(float studioWidthX) {
        this.studioWidthX = studioWidthX;
    }

    public float getStudioWidthY() {
        return studioWidthY;
    }

    public void setStudioWidthY(float studioWidthY) {
        this.studioWidthY = studioWidthY;
    }

    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public ArrayList getIntervalX() {
        return intervalX;
    }

    public void setIntervalX(ArrayList intervalX) {
        this.intervalX = intervalX;
    }

    public ArrayList getIntervalY() {
        return intervalY;
    }

    public void setIntervalY(ArrayList intervalY) {
        this.intervalY = intervalY;
    }

    public CrossStructure() { }

    public CrossStructure(float studioWidthX, float studioWidthY, float originX, float originY, int alIntervalX,  int alIntervalY) {

        this.studioWidthX = studioWidthX;
        this.studioWidthY = studioWidthY;
        this.originX = originX;
        this.originY = originY;

        for(int i = 0; i < alIntervalX ; i++) {
            if(i< (alIntervalX/2)) {
                this.intervalX.add((alIntervalX/2) - i);
            }else{
                this.intervalX.add(i - (alIntervalX/2));
            }
        }

        for(int i = 0; i < alIntervalY ; i++) {
            if(i< (alIntervalY/2)) {
                this.intervalX.add((alIntervalY/2) - i);
            }else{
                this.intervalX.add(i - (alIntervalY/2));
            }
        }

    }
}

