package com.yufeng.democalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

    private static final int TITLE_TEXT_SIZE = 20;
    private static final int DAY_TEXT_SIZE = 18;
    private static final int WEEK_TEXT_SIZE = 17;

    private static final int WEEK_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_PRE_TEXT_COLOR = Color.parseColor("#2D3035");
    private static final int DAY_CUR_TEXT_COLOR = Color.parseColor("#CD9B6B");
    private static final int DAY_NEX_TEXT_COLOR = Color.parseColor("#8D8E91");

    private Paint titlePaint, titleArrowPaint;
    private Paint weekPaint;
    private Paint dayPrePaint, dayCurPaint, dayNexPaint;

    private float totalWidth, oneWeekWidth, weekTextWidth, weekBaseLineY, weekHeight;

    private int lineSpaceHeight = 40;//日期行之间的间距

    private float dayTextWidth, dayAscent, dayStartBaseLineY, dayTotalHeight, oneDayHeight;

    private float titleWidth, titleBaseLineY, titleHeight;
    private float arrowTopY, arrowBottomY, arrowMiddleY;

    private List<WriteDay> writeDayList;
    private int curYear, curMonth, curDay;//当前日历里面的年，月


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
        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(sp2px(context, TITLE_TEXT_SIZE));
        titlePaint.setColor(WEEK_TEXT_COLOR);

        titleArrowPaint = new Paint();
        titleArrowPaint.setAntiAlias(true);
        titleArrowPaint.setColor(WEEK_TEXT_COLOR);
        titleArrowPaint.setStyle(Paint.Style.FILL);
        titleArrowPaint.setStrokeWidth(4f);

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
        computeTitle();
        computeWeek();
        computeDay();
    }

    private void computeWeek() {
        weekTextWidth = weekPaint.measureText(WEEKS[0]);

        Paint.FontMetrics fontMetrics = weekPaint.getFontMetrics();
        weekBaseLineY += -fontMetrics.ascent;

        weekHeight = fontMetrics.bottom - fontMetrics.top + lineSpaceHeight;

        dayStartBaseLineY = weekBaseLineY + fontMetrics.descent + lineSpaceHeight;
    }

    private void computeDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(curYear, curMonth, 1);
        int firstIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int maxDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < maxDayInMonth; i++) {
            WriteDay writeDay = new WriteDay();

            writeDay.setIndex(firstIndex + i);

            writeDay.setText(String.format(Locale.CANADA, "%02d", (i + 1)));

            if (i < curDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.PRE);
            } else if (i == curDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.CUR);
            } else if (i > curDay - 1) {
                writeDay.setDayStatus(WriteDay.Status.NEX);
            }

            Log.e("WriteCalendar", "writeDay=" + writeDay.toString());
            writeDayList.add(writeDay);
        }

        int lineNum = writeDayList.get(writeDayList.size() - 1).getIndex() / 7 + 1;//计算行数

        dayTextWidth = dayPrePaint.measureText("01");

        Paint.FontMetrics fontMetrics = dayPrePaint.getFontMetrics();
        dayAscent = -fontMetrics.ascent;

        oneDayHeight = fontMetrics.bottom - fontMetrics.top;

        dayTotalHeight = (fontMetrics.bottom - fontMetrics.top + lineSpaceHeight) * (lineNum + 1);//最后一行添加一行底部间距
    }

    private void computeTitle() {

        Calendar calendar = Calendar.getInstance();
        curDay = calendar.get(Calendar.DAY_OF_MONTH);//表示几号
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH);

        String title = curYear + "." + curMonth;
        titleWidth = titlePaint.measureText(title);

        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        titleBaseLineY = -fontMetrics.ascent + lineSpaceHeight;

        titleHeight = fontMetrics.bottom - fontMetrics.top + lineSpaceHeight;

        weekBaseLineY = titleBaseLineY + fontMetrics.descent + lineSpaceHeight;

        Rect rect = new Rect();
        titlePaint.getTextBounds(title, 0, 0 ,rect);
//        arrowTopY = lineSpaceHeight + fontMetrics.ascent - fontMetrics.top;
//        arrowBottomY = titleBaseLineY + fontMetrics.descent;
//        arrowMiddleY = arrowTopY + (fontMetrics.descent - fontMetrics.ascent)/2 ;
        arrowTopY = titleBaseLineY - rect.top;
        arrowMiddleY = titleBaseLineY - rect.top + (rect.top + rect.bottom)/2;
        arrowBottomY = titleBaseLineY + rect.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTitle(canvas);
        drawWeek(canvas);
        drawDay(canvas);
        drawTestLine(canvas);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void drawTitle(Canvas canvas) {
        String title = curYear + "." + curMonth;
        float startX = getPaddingLeft() + (totalWidth - titleWidth) / 2;
        canvas.drawText(title, startX, titleBaseLineY, titlePaint);

        canvas.drawLine(startX - 20, arrowTopY, startX - 60, arrowMiddleY, titleArrowPaint);
        canvas.drawLine(startX- 60, arrowMiddleY, startX - 20, arrowBottomY, titleArrowPaint);
    }

    private void drawWeek(Canvas canvas) {
        for (int i = 0; i < WEEKS.length; i++) {
            String week = WEEKS[i];
            canvas.drawText(week, getPaddingLeft() + (oneWeekWidth - weekTextWidth) / 2 + (i * oneWeekWidth), weekBaseLineY, weekPaint);
        }
    }

    private void drawDay(Canvas canvas) {
        for (int i = 0; i < writeDayList.size(); i++) {
            WriteDay writeDay = writeDayList.get(i);
            float startX = getPaddingLeft() + (oneWeekWidth - dayTextWidth) / 2 + (writeDay.getIndex() % 7 * oneWeekWidth);
            float baseLineY = dayStartBaseLineY + (writeDay.getIndex() / 7) * (lineSpaceHeight + oneDayHeight) + dayAscent;

            canvas.drawLine(getPaddingLeft(), baseLineY, totalWidth + getPaddingLeft(), baseLineY, titlePaint);

            if (writeDay.getDayStatus() == WriteDay.Status.PRE) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayPrePaint);
            } else if (writeDay.getDayStatus() == WriteDay.Status.CUR) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayCurPaint);
            } else if (writeDay.getDayStatus() == WriteDay.Status.NEX) {
                canvas.drawText(writeDay.getText(), startX, baseLineY, dayNexPaint);
            }

        }
    }

    private void drawTestLine(Canvas canvas) {
        canvas.drawLine(getPaddingLeft(), titleBaseLineY, totalWidth + getPaddingLeft(), titleBaseLineY, titlePaint);
        canvas.drawLine(getPaddingLeft(), weekBaseLineY, totalWidth + getPaddingLeft(), weekBaseLineY, titlePaint);
//        canvas.drawLine(0, dayStartBaseLineY, totalWidth, dayStartBaseLineY, titlePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        totalWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();   //获取宽的尺寸
        oneWeekWidth = totalWidth * 1.0f / WEEKS.length;
//        float height = (oneHeight + lineSpaceHeight) * 2 + lineNum * (oneHeight + lineSpaceHeight);
        float height = titleHeight + weekHeight + dayTotalHeight;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int) height);
    }

}
