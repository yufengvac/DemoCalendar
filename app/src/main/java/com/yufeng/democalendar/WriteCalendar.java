package com.yufeng.democalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
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


    private static final int DAY_TEXT_SIZE = 18;
    private static final int WEEK_TEXT_SIZE = 17;

    private static final int WEEK_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_PRE_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_CUR_TEXT_COLOR = Color.parseColor("#CD9B6B");
    private static final int DAY_NEX_TEXT_COLOR = Color.parseColor("#8D8E91");

    private Paint weekPaint;
    private Paint dayPrePaint, dayCurPaint, dayNexPaint;

    private float totalWidth, oneWeekWidth, weekTextWidth, weekBaseLineY;

    private float oneHeight = 80;//每一行的高度
    private int lineNum;//日期的行数
    private int lineSpaceHeight = 40;//日期行之间的间距

    private float dayTextWidth;
    private float dayDescent;

    private List<WriteDay> writeDayList;


    public WriteCalendar(Context context) {
        this(context, null);
    }

    public WriteCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WriteCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        weekPaint = new Paint();
        weekPaint.setAntiAlias(true);
        weekPaint.setTextSize(sp2px(context, WEEK_TEXT_SIZE));
        weekPaint.setColor(WEEK_TEXT_COLOR);

        dayPrePaint = new Paint();
        dayPrePaint.setAntiAlias(true);
        dayPrePaint.setTextSize(sp2px(context, DAY_TEXT_SIZE));
        dayPrePaint.setColor(DAY_PRE_TEXT_COLOR);

        dayCurPaint = new Paint();
        dayCurPaint.setAntiAlias(true);
        dayCurPaint.setTextSize(sp2px(context, DAY_TEXT_SIZE));
        dayCurPaint.setColor(DAY_CUR_TEXT_COLOR);

        dayNexPaint = new Paint();
        dayNexPaint.setAntiAlias(true);
        dayNexPaint.setTextSize(sp2px(context, DAY_TEXT_SIZE));
        dayNexPaint.setColor(DAY_NEX_TEXT_COLOR);

        writeDayList = new ArrayList<>();
        computeWeek();
        computeDay();
    }

    private void computeWeek() {
        weekTextWidth = weekPaint.measureText(WEEKS[0]);

        Paint.FontMetrics fontMetrics = weekPaint.getFontMetrics();
        weekBaseLineY = fontMetrics.descent + oneHeight;
    }

    private void computeDay() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);//表示几号

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, 1);
        int firstIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < maxDayInMonth; i++) {
            WriteDay writeDay = new WriteDay();

            writeDay.setIndex(firstIndex + i);

            writeDay.setText(String.format(Locale.CANADA, "%02d", (i + 1)));

            if (i < currentDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.PRE);
            } else if (i == currentDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.CUR);
            } else if (i > currentDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.NEX);
            }

            Log.e("WriteCalendar", "writeDay=" + writeDay.toString());
            writeDayList.add(writeDay);
        }

        lineNum = writeDayList.get(writeDayList.size() - 1).getIndex() / 7 + 1;//计算行数

        dayTextWidth = dayPrePaint.measureText("01");
        dayDescent = dayPrePaint.getFontMetrics().descent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeek(canvas);
        drawDay(canvas);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void drawWeek(Canvas canvas) {
        for (int i = 0; i < WEEKS.length; i++) {
            String week = WEEKS[i];
            canvas.drawText(week, (oneWeekWidth - weekTextWidth) / 2 + (i * oneWeekWidth), weekBaseLineY, weekPaint);
        }
    }

    private void drawDay(Canvas canvas) {
        for (int i = 0; i < writeDayList.size(); i++) {
            WriteDay writeDay = writeDayList.get(i);
            float startX = (oneWeekWidth - dayTextWidth) / 2 + (writeDay.getIndex() % 7 * oneWeekWidth);
            float baseLineY = oneHeight * 2 + lineSpaceHeight + (writeDay.getIndex() / 7) * (lineSpaceHeight + oneHeight) + dayDescent;

            if (writeDay.getDayStatus() == WriteDay.Status.PRE) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayPrePaint);
            } else if (writeDay.getDayStatus() == WriteDay.Status.CUR) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayCurPaint);
            } else if (writeDay.getDayStatus() == WriteDay.Status.NEX) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayNexPaint);
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        oneWeekWidth = totalWidth * 1.0f / WEEKS.length;
        float height = oneHeight * 2 + lineNum * (oneHeight + lineSpaceHeight);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int) height);
    }

}
