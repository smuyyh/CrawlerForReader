package com.qy.reader.common.widgets.reader;

import com.qy.reader.common.Global;
import com.qy.reader.common.utils.SPUtils;
import com.qy.reader.common.utils.ScreenUtils;
import com.qy.reader.common.widgets.reader.annotation.DensityLevel;
import com.qy.reader.common.widgets.reader.annotation.FontType;

public class SettingManager {

    @FontType
    public static int getFontType() {
        return SPUtils.getInstance().getInt("read_font_type", FontType.SIMPLIFIED);
    }

    public static void setFontType(@FontType int fontType) {
        SPUtils.getInstance().put("read_font_type", fontType);
    }

    public static boolean isTraditionalFont() {
        return getFontType() == FontType.TRADITIONAL;
    }

    public static int getTextSizeSP() {
        return SPUtils.getInstance().getInt("read_text_size_sp", 16);
    }

    public static void setTextSizeSP(int textSizeSP) {
        SPUtils.getInstance().put("read_text_size_sp", textSizeSP);
    }

    @DensityLevel
    public static int getDensityLevel() {
        return SPUtils.getInstance().getInt("read_density_level", DensityLevel.LEVEL_3);
    }

    public static void setDensityLevel(@DensityLevel int level) {
        SPUtils.getInstance().put("read_density_level", level);
    }

    public static boolean isLandscape() {
        return SPUtils.getInstance().getBoolean("read_lanscape", false);
    }

    public static void setLandscape(boolean isLandscape) {
        SPUtils.getInstance().put("read_lanscape", isLandscape);
    }


    public static int getBrightness() {
        return SPUtils.getInstance().getInt("read_brightness", (int) ScreenUtils.getScreenBrightness(Global.getApplication()));
    }

    public static void setBrightness(int brightness) {
        SPUtils.getInstance().put("read_brightness", brightness);
    }

    public static boolean isUseSystemBrightness() {
        return SPUtils.getInstance().getBoolean("read_use_system_brightness", true);
    }

    public static void setUseSystemBrightness(boolean useSystemBrightness) {
        SPUtils.getInstance().put("read_use_system_brightness", useSystemBrightness);
    }

    public static boolean isNightMode() {
        return SPUtils.getInstance().getBoolean("read_night_mode", false);
    }

    public static void setNightMode(boolean isNightMode) {
        SPUtils.getInstance().put("read_night_mode", isNightMode);
    }

    public static int getThemeColorIndex() {
        return SPUtils.getInstance().getInt("read_theme_color", 0);
    }

    public static void setThemeColorIndex(int index) {
        SPUtils.getInstance().put("read_theme_color", index);
    }

    public static void setEnableVolumePage(boolean enable) {
        SPUtils.getInstance().put("read_volume_page", enable);
    }

    public static boolean isEnableVolumePage() {
        return SPUtils.getInstance().getBoolean("read_volume_page", false);
    }

    public static void saveLastReadChapter(String bookNum, String chapterNum) {
        SPUtils.getInstance().put("last_read_chapter" + bookNum, chapterNum);
    }

    public static String getLastReadChapter(String bookNum) {
        return SPUtils.getInstance().getString("last_read_chapter" + bookNum);
    }


    public static void saveLastReadPage(String bookNum, int currentPage) {
        SPUtils.getInstance().put("last_read_page" + bookNum, currentPage);
    }

    public static int getLastReadPage(String bookNum) {
        return SPUtils.getInstance().getInt("last_read_page" + bookNum, 1);
    }
}