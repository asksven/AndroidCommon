/**
 * 
 */
package com.asksven.android.common;

import com.asksven.android.common.privateapiproxies.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author sven From
 *         http://www.androidsnippets.com/prompt-engaged-users-to-rate-
 *         your-app-in-the-android-market-appirater
 * Make sure to add following to strings.xml
 *    <string name="app_name">AndroidCommon</string>
 *    <string name="app_pname">com.asksven.androidcommon</string>
 *    <string name="label_button_remind">Remind me later</string>
 *    <string name="label_button_rate">Rate</string>
 *    <string name="label_button_no">No, thanks</string>
 *    <string name="text_dialog_rate">If you enjoy using %s, please take a moment to rate it. Thanks for your support!</string>
 * 
 * To test it and to tweak the dialog appearence, you can call AppRater.showRateDialog(this, null)
 * from your Activity.
 * 
 * Normal use is to invoke AppRater.app_launched(this) each time your activity is invoked
 * (eg. from within the onCreate method). If all conditions are met, the dialog appears.
 */
public class AppRater
{
	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;

	public static void app_launched(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean("dontshowagain", false))
		{
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0)
		{
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT)
		{
			if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
			{
				showRateDialog(ctx, editor);
			}
		}

		editor.commit();
	}

	public static void showRateDialog(final Context ctx, final SharedPreferences.Editor editor)
	{
		final Dialog dialog = new Dialog(ctx);
		dialog.setTitle(ctx.getString(R.string.label_button_rate) + " " + ctx.getString(R.string.app_name));

		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(ctx);
		tv.setText(ctx.getString(R.string.text_dialog_rate, ctx.getString(R.string.app_name)));
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		Button b1 = new Button(ctx);
		b1.setText(ctx.getString(R.string.label_button_rate));
		b1.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ctx.getString(R.string.app_pname))));
				if (editor != null)
				{
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b1);

		Button b2 = new Button(ctx);
		b2.setText(R.string.label_button_remind);
		b2.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(ctx);
		b3.setText(R.string.label_button_no);
		b3.setOnClickListener(new Button.OnClickListener()
		{
			public void onClick(View v)
			{
				if (editor != null)
				{
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);
		dialog.show();
	}
}
