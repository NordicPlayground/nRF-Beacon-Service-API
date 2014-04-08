package no.nordicsemi.android.beacon.test;

import java.util.UUID;

import no.nordicsemi.android.beacon.Beacon;
import no.nordicsemi.android.beacon.BeaconRegion;
import no.nordicsemi.android.beacon.BeaconServiceConnection;
import no.nordicsemi.android.beacon.ServiceProxy;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private UUID mMyUuid = UUID.fromString("01122334-4556-6778-899A-ABBCCDDEEFF0");
	private UUID mAnyUuid = BeaconRegion.ANY_UUID;

	private BeaconServiceConnection.RegionListener mRegionListener = new BeaconServiceConnection.RegionListener() {
		@Override
		public void onEnterRegion(final BeaconRegion region) {
			Log.i(TAG, "onEnterRegion: " + region);
		}

		@Override
		public void onExitRegion(final BeaconRegion region) {
			Log.i(TAG, "onExitRegion: " + region);
		}
	};

	private BeaconServiceConnection.BeaconsListener mBeaconsListener = new BeaconServiceConnection.BeaconsListener() {
		@Override
		public void onBeaconsInRegion(final Beacon[] beacons, final BeaconRegion region) {
			Log.i(TAG, "onBeaconsInRegion Region: " + region);

			for (final Beacon beacon : beacons)
				Log.d(TAG, beacon.toString());
		}
	};

	private BeaconServiceConnection mConnection = new BeaconServiceConnection() {
		@Override
		public void onServiceConnected() {
			Log.v(TAG, "Service connected");
			startMonitoringForRegion(mAnyUuid, mRegionListener);
			startRangingBeaconsInRegion(mMyUuid, mBeaconsListener);
			startRangingBeaconsInRegion(mMyUuid, 5, mBeaconsListener);
			startRangingBeaconsInRegion(mAnyUuid, mBeaconsListener);
		}

		@Override
		public void onServiceDisconnected() {
			Log.v(TAG, "Service disconnected");
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ServiceProxy.bindService(this, mConnection);
		ServiceProxy.stopMonitoringForRegion(this, mMyUuid);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// This intent will be launched when user press the notification
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// Create a pending intent
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Create and configure the notification
		final Notification.Builder builder = new Notification.Builder(this); // the notification icon (small icon) will be overwritten by the BeaconService.
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).setContentTitle("iBeacon is in range!").setContentText("Click to open app.");
		builder.setAutoCancel(true).setOnlyAlertOnce(true).setContentIntent(pendingIntent);
		// Start monitoring for the region
		ServiceProxy.startMonitoringForRegion(this, mMyUuid, builder.build());

		mConnection.stopMonitoringForRegion(mRegionListener);
		mConnection.stopRangingBeaconsInRegion(mBeaconsListener);
		ServiceProxy.unbindService(this, mConnection);
	}
}
