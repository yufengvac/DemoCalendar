package com.yufeng.democalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by yufeng on 2018/9/4-0004.
 * 写作日历
 */

public class WriteCalendar extends View {

    private static final String[] WEEKS = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    private static final int TITLE_TEXT_SIZE = 60;
    private static final int DAY_TEXT_SIZE = 42;
    private static final int WEEK_TEXT_SIZE = 39;

    private static final int WEEK_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_PRE_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_CUR_TEXT_COLOR = Color.parseColor("#CD9B6B");
    private static final int DAY_NEX_TEXT_COLOR = Color.parseColor("#8D8E91");
    private static final int ARROW_DISABLE_COLOR = Color.parseColor("#ECECEC");
    private static final int ARROW_ABLE_COLOR = Color.parseColor("#CD9B6B");
    private static final int DAY_UPDATE_BG_COLOR = Color.parseColor("#F0DAC6");

    private Paint titlePaint, titleArrowPaint;
    private Paint weekPaint;
    private Paint dayPrePaint, dayCurPaint, dayNexPaint, daySelectPaint;
    private Paint daySelectBgPaint, dayUpdateBgPaint;

    private float totalWidth, weekTextWidth, weekBaseLineY, weekHeight;
    private float oneModuleWidth;

    private int lineSpaceHeight = 50;//日期行之间的间距
    private int radius = 45;

    private float dayTextWidth, dayTop, dayStartBaseLineY, dayTotalHeight, oneDayHeight;

    private float titleWidth, titleBaseLineY, titleHeight;
    private float arrowTopY, arrowBottomY, arrowMiddleY, arrowOffsetX;

    private int arrowMargin = 40;//箭头距离右边的间距
    private float arrowCompensate;//画箭头的补偿，因为画箭头存在画笔的宽度
    private float arrowStrokeWidth = 6f;//箭头的宽度

    private List<WriteDay> writeDayList;
    private int curYear, curMonth, curDay;//当前日历里面的年，月，会发生改变，可以通过翻上个月下个月改变

    private int realCurYear, realCurMonth;//表示当前世界的年，月，不会发生改变
    private boolean canTurnNext = true;

    private RectF updateRectF;

    private float titleStartX, titleEndX;//大标题2018.09的最左边坐标,最右边坐标
    private float leftArrowStartX, rightArrowEndX;//左箭头的最左边坐标,右箭头的最右边坐标
    private List<Float> dayBottomYList = new ArrayList<>();//每一个日期最底下的y坐标

    private OnDateChangeListener onDateChangeListener;
    private OnDaySelectedListener onDaySelectedListener;

    public WriteCalendar(Context context) {
        this(context, null);
    }

    public WriteCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WriteCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        updateRectF = new RectF();

        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(TITLE_TEXT_SIZE);
        titlePaint.setColor(WEEK_TEXT_COLOR);

        titleArrowPaint = new Paint();
        titleArrowPaint.setAntiAlias(true);
        titleArrowPaint.setColor(ARROW_ABLE_COLOR);
        titleArrowPaint.setStyle(Paint.Style.FILL);
        titleArrowPaint.setStrokeWidth(arrowStrokeWidth);

        weekPaint = new Paint();
        weekPaint.setAntiAlias(true);
        weekPaint.setTextSize(WEEK_TEXT_SIZE);
        weekPaint.setColor(WEEK_TEXT_COLOR);

        dayPrePaint = new Paint();
        dayPrePaint.setAntiAlias(true);
        dayPrePaint.setTextSize(DAY_TEXT_SIZE);
        dayPrePaint.setColor(DAY_PRE_TEXT_COLOR);

        dayCurPaint = new Paint();
        dayCurPaint.setAntiAlias(true);
        dayCurPaint.setTextSize(DAY_TEXT_SIZE);
        dayCurPaint.setColor(DAY_CUR_TEXT_COLOR);

        dayNexPaint = new Paint();
        dayNexPaint.setAntiAlias(true);
        dayNexPaint.setTextSize(DAY_TEXT_SIZE);
        dayNexPaint.setColor(DAY_NEX_TEXT_COLOR);

        daySelectBgPaint = new Paint();
        daySelectBgPaint.setAntiAlias(true);
        daySelectBgPaint.setColor(DAY_CUR_TEXT_COLOR);

        daySelectPaint = new Paint();
        daySelectPaint.setAntiAlias(true);
        daySelectPaint.setTextSize(DAY_TEXT_SIZE);
        daySelectPaint.setColor(Color.WHITE);

        dayUpdateBgPaint = new Paint();
        dayUpdateBgPaint.setAntiAlias(true);
        dayUpdateBgPaint.setColor(DAY_UPDATE_BG_COLOR);

        writeDayList = new ArrayList<>();
        computeTitle();
        computeWeek();
        computeDay();
    }

    private void computeTitle() {


        Calendar calendar = Calendar.getInstance();

        curDay = calendar.get(Calendar.DAY_OF_MONTH);//表示几号
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH) + 1;//少1

        realCurYear = curYear;
        realCurMonth = curMonth;

        canTurnNext = false;


        String title = curYear + "." + String.format(Locale.CANADA, "%02d", curMonth);
        titleWidth = titlePaint.measureText(title);

        Rect rect = new Rect();
        titlePaint.getTextBounds(title, 0, title.length(), rect);

        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        titleBaseLineY = -fontMetrics.top + lineSpaceHeight;

        titleHeight = fontMetrics.bottom - fontMetrics.top + lineSpaceHeight;

        weekBaseLineY = titleBaseLineY + fontMetrics.bottom + lineSpaceHeight;

        arrowTopY = titleBaseLineY + rect.top + 7;
        arrowMiddleY = titleBaseLineY + rect.top + (rect.bottom - rect.top) / 2;
        arrowBottomY = titleBaseLineY + rect.bottom - 7;

        arrowOffsetX = (float) Math.sqrt((arrowMiddleY - arrowTopY) * (arrowBottomY - arrowMiddleY));//画三角的左右顶点偏移量，可保证三角是直角
        arrowCompensate = (float) Math.sqrt(arrowStrokeWidth);

    }

    private void computeWeek() {
        weekTextWidth = weekPaint.measureText(WEEKS[0]);

        Paint.FontMetrics fontMetrics = weekPaint.getFontMetrics();
        weekBaseLineY += -fontMetrics.top;

        weekHeight = fontMetrics.bottom - fontMetrics.top + lineSpaceHeight;

        dayStartBaseLineY = weekBaseLineY + fontMetrics.bottom + lineSpaceHeight;
    }

    private void computeDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(curYear, curMonth - 1, 1);
        int firstIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        writeDayList.clear();
        for (int i = 0; i < maxDayInMonth; i++) {
            WriteDay writeDay = new WriteDay();

            writeDay.setIndex(firstIndex + i);

            writeDay.setText(String.format(Locale.CANADA, "%02d", (i + 1)));

            if (curDay > 0) {
                if (i < curDay - 1) {
                    writeDay.setDayStatus(WriteDay.Status.PRE);
                } else if (i == curDay - 1) {
                    writeDay.setDayStatus(WriteDay.Status.CUR);
                } else if (i > curDay - 1) {
                    writeDay.setDayStatus(WriteDay.Status.NEX);
                }
            } else {
                writeDay.setDayStatus(WriteDay.Status.PRE);
            }

            writeDay.setUpdate(i % 7 == 0 || (i + 1) % 7 == 0);

            writeDayList.add(writeDay);
        }

        writeDayList.get(5).setUpdate(true);

        for (int i = 0; i < writeDayList.size(); i++) {
            WriteDay writeDay = writeDayList.get(i);
            if (!writeDay.isUpdate()) {
                continue;
            }

            int step = 0;
            while (i + 1 + step < writeDayList.size() && writeDayList.get(i + 1 + step).isUpdate() && step < 7 - writeDay.getIndex() % 7 - 1) {
                step++;
            }
            if (step == 0) {
                writeDayList.get(i).setDayUpdateBgStyle(WriteDay.Style.SINGLE);
            } else {
                for (int j = i; j < i + step + 1; j++) {
                    if (j == i) {
                        writeDayList.get(j).setDayUpdateBgStyle(WriteDay.Style.LEFT);
                    } else if (j < i + step) {

                        writeDayList.get(j).setDayUpdateBgStyle(WriteDay.Style.FULL);

                    } else if (j == i + step) {
                        writeDayList.get(j).setDayUpdateBgStyle(WriteDay.Style.RIGHT);
                        i = j;
                        break;
                    }
                }
            }
        }

        int lineNum = writeDayList.get(writeDayList.size() - 1).getIndex() / 7 + 1;//计算行数

        dayTextWidth = dayPrePaint.measureText("01");

        Paint.FontMetrics fontMetrics = dayPrePaint.getFontMetrics();
        dayTop = -fontMetrics.top;

        oneDayHeight = fontMetrics.bottom - fontMetrics.top;

        dayTotalHeight = (oneDayHeight + lineSpaceHeight) * (lineNum + 1);//最后一行添加一行底部间距

        dayBottomYList.clear();
        for (int i = 0; i < lineNum; i++) {
            dayBottomYList.add(titleHeight + lineSpaceHeight / 2 + weekHeight + (lineSpaceHeight + oneDayHeight) * (i + 1));
        }
    }


    /**
     * 表示点击的是第row行第index个日期，需要更新一下模型
     *
     * @param row    row行
     * @param column column列
     */
    private void updateWriteDay(int row, int column) {
        if (writeDayList == null || writeDayList.size() == 0){
            return;
        }
        int index = row * 7 + column;
        WriteDay selectedDay = null;
        if (index < writeDayList.get(0).getIndex() || index > writeDayList.get(writeDayList.size()-1).getIndex()){
            return;
        }
        for (WriteDay writeDay : writeDayList) {
            if (writeDay.getIndex() == index) {
                writeDay.setSelected(true);
                selectedDay = writeDay;
            } else {
                writeDay.setSelected(false);
            }
        }
        invalidate();
        if (onDaySelectedListener != null && selectedDay != null) {
            onDaySelectedListener.onDaySelected(curYear, curMonth, Integer.valueOf(selectedDay.getText()));
        }
    }

    /**
     * 翻到上一个月，点击title左边箭头
     */
    private void turnPreMonth() {

        if (curMonth == 1) {
            curYear = curYear - 1;
        }
        curMonth = (curMonth - 1) % 12;
        if (curMonth == 0) {
            curMonth = 12;
        }
        if (curYear == realCurYear && curMonth == realCurMonth) {
            Calendar calendar = Calendar.getInstance();
            curDay = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            curDay = -1;
            canTurnNext = true;
        }
        computeDay();
        invalidate();

        if (onDateChangeListener != null) {
            onDateChangeListener.onDateChange(curYear, curMonth);
        }
    }

    /**
     * 翻到下一个月，点击title右边箭头
     */
    private void turnNextMonth() {
        if (!canTurnNext) {
            return;
        }
        if (curMonth == 12) {
            curYear = curYear + 1;
        }
        curMonth = (curMonth + 1) % 13;
        if (curMonth == 0) {
            curMonth = 1;
        }

        if (curYear == realCurYear && curMonth == realCurMonth) {
            Calendar calendar = Calendar.getInstance();
            curDay = calendar.get(Calendar.DAY_OF_MONTH);
            canTurnNext = false;
        } else {
            curDay = -1;
        }
        computeDay();
        invalidate();

        if (onDateChangeListener != null) {
            onDateChangeListener.onDateChange(curYear, curMonth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTitle(canvas);
        drawWeek(canvas);
        drawDay(canvas);
//        drawTestLine(canvas);
    }

    private void drawTitle(Canvas canvas) {
        String title = curYear + "." + String.format(Locale.CANADA, "%02d", curMonth);
        float startX = getPaddingLeft() + (totalWidth - titleWidth) / 2;
        float startX1 = getPaddingLeft() + titleWidth + (totalWidth - titleWidth) / 2;
        canvas.drawText(title, startX, titleBaseLineY, titlePaint);

        titleArrowPaint.setColor(ARROW_ABLE_COLOR);
        canvas.drawLine(startX - arrowMargin, arrowTopY, (int) (startX - arrowMargin - arrowOffsetX - arrowCompensate), (int) (arrowMiddleY + arrowCompensate), titleArrowPaint);
        canvas.drawLine(startX - arrowMargin - arrowOffsetX, arrowMiddleY, startX - arrowMargin, arrowBottomY, titleArrowPaint);

        if (!canTurnNext) {
            titleArrowPaint.setColor(ARROW_DISABLE_COLOR);
        }
        canvas.drawLine(startX1 + arrowMargin, arrowTopY, (int) (startX1 + arrowMargin + arrowOffsetX + arrowCompensate), (int) (arrowMiddleY + arrowCompensate), titleArrowPaint);
        canvas.drawLine(startX1 + arrowMargin + arrowOffsetX, arrowMiddleY, startX1 + arrowMargin, arrowBottomY, titleArrowPaint);

        titleStartX = startX;
        titleEndX = startX1;
        leftArrowStartX = startX - arrowMargin - arrowOffsetX - arrowCompensate;
        rightArrowEndX = startX1 + arrowMargin + arrowOffsetX + arrowCompensate;
    }

    private void drawWeek(Canvas canvas) {
        for (int i = 0; i < WEEKS.length; i++) {
            String week = WEEKS[i];
            canvas.drawText(week, getPaddingLeft() + (oneModuleWidth - weekTextWidth) / 2 + (i * oneModuleWidth), weekBaseLineY, weekPaint);
        }
    }

    private void drawDay(Canvas canvas) {
        for (int i = 0; i < writeDayList.size(); i++) {
            WriteDay writeDay = writeDayList.get(i);

            int row = writeDay.getIndex() / 7;

            float startX = getPaddingLeft() + (oneModuleWidth - dayTextWidth) / 2 + (writeDay.getIndex() % 7 * oneModuleWidth);
            float baseLineY = dayStartBaseLineY + row * (lineSpaceHeight + oneDayHeight) + dayTop;

            if (writeDay.isUpdate()) {//画有更新的背景
                float cx = startX + dayTextWidth / 2;
                float cy = titleHeight + lineSpaceHeight / 2 + weekHeight + (oneDayHeight + lineSpaceHeight) * row + (oneDayHeight + lineSpaceHeight) / 2;
                if (writeDay.getDayUpdateBgStyle() == WriteDay.Style.SINGLE) {

                    canvas.drawCircle(cx, cy, radius, dayUpdateBgPaint);

                } else if (writeDay.getDayUpdateBgStyle() == WriteDay.Style.LEFT) {

                    updateRectF.set((int) (cx - radius), (int) (cy - radius), (int) (cx + radius), (int) (cy + radius));

                    canvas.drawArc(updateRectF, 90, 180, false, dayUpdateBgPaint);

                    updateRectF.set((int) cx, (int) (cy - radius), (int) (cx + dayTextWidth / 2 + (oneModuleWidth - dayTextWidth) / 2), (int) (cy + radius));
                    canvas.drawRect(updateRectF, dayUpdateBgPaint);

                } else if (writeDay.getDayUpdateBgStyle() == WriteDay.Style.RIGHT) {

                    updateRectF.set((int) (cx - radius), (int) (cy - radius), (int) (cx + radius), (int) (cy + radius));
                    canvas.drawArc(updateRectF, 270, 180, false, dayUpdateBgPaint);

                    updateRectF.set((int) (startX - (oneModuleWidth - dayTextWidth) / 2), (int) (cy - radius), (int) cx, (int) (cy + radius));
                    canvas.drawRect(updateRectF, dayUpdateBgPaint);
                } else if (writeDay.getDayUpdateBgStyle() == WriteDay.Style.FULL) {

                    updateRectF.set((int) (cx - oneModuleWidth / 2), (int) (cy - radius), (int) (cx + oneModuleWidth / 2), (int) (cy + radius));
                    canvas.drawRect(updateRectF, dayUpdateBgPaint);
                }
            }

            if (writeDay.isSelected()) {//画被选中的背景
                float cx = startX + dayTextWidth / 2;
                float cy = titleHeight + lineSpaceHeight / 2 + weekHeight + (oneDayHeight + lineSpaceHeight) * row + (oneDayHeight + lineSpaceHeight) / 2;
                canvas.drawCircle(cx, cy, radius, daySelectBgPaint);
            }

            Paint textPaint = dayPrePaint;
            if (writeDay.getDayStatus() == WriteDay.Status.PRE) {
                textPaint = dayPrePaint;
            } else if (writeDay.getDayStatus() == WriteDay.Status.CUR) {
                textPaint = dayCurPaint;
            } else if (writeDay.getDayStatus() == WriteDay.Status.NEX) {
                textPaint = dayNexPaint;
            }
            if (writeDay.isSelected()) {
                textPaint = daySelectPaint;
            }
            canvas.drawText(writeDay.getText(), startX, baseLineY, textPaint);
        }


//        for (int i = 0; i < lineNum; i++) {
//            canvas.drawLine(getPaddingLeft(), dayBottomYList.get(i), totalWidth + getPaddingLeft(), dayBottomYList.get(i), dayCurPaint);
//        }
    }

//    private void drawTestLine(Canvas canvas) {
//        canvas.drawLine(getPaddingLeft(), titleBaseLineY, totalWidth + getPaddingLeft(), titleBaseLineY, titlePaint);
//        canvas.drawLine(getPaddingLeft(), weekBaseLineY, totalWidth + getPaddingLeft(), weekBaseLineY, titlePaint);

//        canvas.drawLine(getPaddingLeft(), titleHeight + lineSpaceHeight / 2, totalWidth + getPaddingLeft(), titleHeight + lineSpaceHeight / 2, dayCurPaint);
//        canvas.drawLine(getPaddingLeft(), titleHeight + lineSpaceHeight / 2 + weekHeight, totalWidth + getPaddingLeft(), titleHeight + lineSpaceHeight / 2 + weekHeight, dayCurPaint);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        totalWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();   //获取宽的尺寸
        oneModuleWidth = totalWidth * 1.0f / WEEKS.length;
        float height = titleHeight + weekHeight + dayTotalHeight;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int) height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            handleClick(event.getX(), event.getY());
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleClick(float x, float y) {
        if (y <= titleHeight + lineSpaceHeight / 2) { //标题栏高度
            if (x < titleStartX && x >= leftArrowStartX - 50) {//点击了左箭头
                turnPreMonth();
            } else if (x > titleEndX && x <= rightArrowEndX + 50) {//点击了右箭头
                turnNextMonth();
            }
        } else if (y >= titleHeight + lineSpaceHeight / 2 + weekHeight && y < titleHeight + weekHeight + dayTotalHeight - lineSpaceHeight - oneDayHeight) {//日期第一行高度
            int row = 0;
            for (int i = 0; i < dayBottomYList.size(); i++) {
                if (y <= dayBottomYList.get(i)) {
                    row = i;
                    break;
                }
            }
            int xIndex = (int) (x / oneModuleWidth);
            updateWriteDay(row, xIndex);
        }
    }

    /**********************暴露给外面的方法*************************/

    @SuppressWarnings("unused")
    public void setArrowMargin(int margin) {
        arrowMargin = margin;
    }

    @SuppressWarnings("unused")
    public void setCircleRadius(int radius) {
        this.radius = radius;
    }

    public int getCurYear(){
        return curYear;
    }

    public int getCurMonth(){
        return curMonth;
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        this.onDateChangeListener = listener;
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.onDaySelectedListener = listener;
    }

    public interface OnDateChangeListener {
        void onDateChange(int year, int month);
    }

    public interface OnDaySelectedListener {
        void onDaySelected(int year, int month, int day);
    }
}
