package com.derek.todolist;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class OnAlarmReceiver extends BroadcastReceiver {
	private NotificationManager nm;
	private Notification nf;
	@Override
	public void onReceive(Context context, Intent intent) {
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intentNew = new Intent(context, MainPageActivity.class);
		PendingIntent pendingintent =  PendingIntent.getActivity(context, 0, intentNew, 0);
		nf = new Notification.Builder(context)
		.setContentTitle(intent.getStringExtra("title"))
		.setContentText(intent.getStringExtra("content"))
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pendingintent)
		.setDefaults(Notification.DEFAULT_SOUND)
		.build();

		nm.notify(1000, nf);
		Toast.makeText(context, intent.getStringExtra("title"), Toast.LENGTH_LONG).show();
	}
}
