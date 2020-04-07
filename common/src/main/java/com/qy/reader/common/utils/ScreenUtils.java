package com.qy.reader.common.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.qy.reader.common.Global;

import java.lang.reflect.InvocationTargetException;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Android screen utils
 * <p>
 * Created by yuyuhang on 2018/1/8.
 */
public class ScreenUtils {

    public static int getScreenWidth() {
        return Global.getApplication().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Global.getApplication().getResources().getDisplayMetrics().heightPixels;
    }

    public static float dpToPx(float dp) {
        return dp * Global.getApplication().getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(float dp) {
        return (int) (dpToPx(dp) + 0.5f);
    }

    public static float pxToDp(float px) {
        return px / Global.getApplication().getResources().getDisplayMetrics().density;
    }

    public static int pxToDpInt(float px) {
        return (int) (pxToDp(px) + 0.5f);
    }

    public static float pxToSp(float pxValue) {
        return pxValue / Global.getApplication().getResources().getDisplayMetrics().scaledDensity;
    }

    public static float spToPx(float spValue) {
        return spValue * Global.getApplication().getResources().getDisplayMetrics().scaledDensity;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Get pixels from dps
     *
     * @param resources
     * @param dp
     * @return pixels
     */
    public static int getIntPixels(final Resources resources, final int dp) {
        float pixels = TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) Math.floor(pixels + 0.5F);
    }

    public static int getActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static final boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static final boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * set alpha for phonewindow
     *
     * @param from     from>=0&&from<=1.0f
     * @param to       to>=0&&to<=1.0f
     * @param activity
     */
    public static void dimBackground(final float from, final float to, Activity activity) {
        final Window window = activity.getWindow();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (Float) valueAnimator.getAnimatedValue();
                window.setAttributes(params);
            }
        });
        valueAnimator.start();
    }

    /**
     * get is auto brightness
     *
     * @param activity
     * @return
     */
    public static boolean isAutoBrightness(Activity activity) {
        boolean isAutoAdjustBright = false;
        try {
            isAutoAdjustBright = Settings.System.getInt(
                    activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isAutoAdjustBright;
    }

    /**
     * disable auto brightness
     *
     * @param activity
     */
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * enable auto brightness
     *
     * @param activity
     */

    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    public static float getScreenBrightness(Context mContext) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness / 255.0F * 100;
    }

    public static void saveScreenBrightness(int paramInt, Context mContext) {
        if (paramInt <= 5) {
            paramInt = 5;
        }
        try {
            float f = paramInt / 100.0F * 255;
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setScreenBrightness(int paramInt, Activity mActivity) {
        if (paramInt <= 5) {
            paramInt = 5;
        }
        Window localWindow = mActivity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 100.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    /**
     * 获取虚拟键高度
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Point getNavigationBarSize(Context context) {
        if (ScreenUtils.getScreenHeight() < ScreenUtils.getScreenWidth()) {
            return new Point(); // 横屏模式
        }

        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
            }
        }

        return size;
    }

    /**
     * 屏幕是否存在刘海;
     * @return
     */
    @Nullable
    public static DisplayCutout getCutout() {
        Application application = Global.getApplication();
        WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return null;
        }
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return display.getCutout();
        }

        return null;
    }
}
