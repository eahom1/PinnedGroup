package com.eahom.pinned;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 *
 * @author Eahom
 *
 */
public class UiUtil {
	// should never instantiate
	private UiUtil() {
	}

	public static int getScreenWidthPixels(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeightPixels(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels;
	}

	public static int dip2px(Context context,int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	public static int px2dip(Context context, int px) {
		return (int) (px / getScreenDensity(context) + 0.5f);
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
					.getMetrics(dm);
			return dm.density;
		} catch (Exception e) {
			return DisplayMetrics.DENSITY_DEFAULT;
		}
	}
	
	public static int dp2px(Context context, int dp) {
		Resources r = context.getResources();
	    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
	    return Math.round(px);
	}
}
