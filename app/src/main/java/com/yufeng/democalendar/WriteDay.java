package com.yufeng.democalendar;

/**
 * Created by yufeng on 2018/9/4-0004.
 * 写作日期模型
 */

public class WriteDay {
    private int index;//索引，0表示在第一行第一个周日
    private String text;//值 01；02
    private boolean isSelected;//是否是用户选中
    private int dayStatus;//状态，0表示今天以前；1表示今天；2表示今天以后
    private boolean isUpdate;//是否更新章节

    static class Status {
        static final int PRE = 0;
        static final int CUR = 1;
        static final int NEX = 2;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getDayStatus() {
        return dayStatus;
    }

    public void setDayStatus(int dayStatus) {
        this.dayStatus = dayStatus;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @Override
    public String toString() {
        return "WriteDay{" +
                "index=" + index +
                ", text='" + text + '\'' +
                ", isSelected=" + isSelected +
                ", dayStatus=" + dayStatus +
                ", isUpdate=" + isUpdate +
                '}';
    }
}
