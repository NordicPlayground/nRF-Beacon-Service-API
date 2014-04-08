nRF-Beacon-Service-API
======================

The public library with nRF Beacon Service API. Allows applications to register for iBeacon monitoring and ranging. 

The nRF Beacon Service has been designed to work like Location Core from iOS on Android. It operates as a Android service and scans periodically for registered iBeacons. The service is started when beacon region is being registered for passive monitoring or an application binds to it.

### Application
The nRF Beacon Service application may be downloaded from the Google Play: https://play.google.com/store/apps/details?id=no.nordicsemi.android.beacon.service. The library provides API to allow 3rd party applications use the service which may save a lot of battery.

### Scanning modes
There are two ways of registering for regions: passive and active. 

* The **passive** way works when the registering application is not in an active state. When going into background it may register a region with a notification that will be shown if region will be entered. Only region monitoring (enter/exit events) is supported in this mode. Scanning in passive mode is performed every 15 seconds for 1 second to save the battery. Passive (background) scanning may be disabled by user on the Settings screen.
* The **active** approach uses binding to the service. It allows to both monitor (enter/exit events) and range regions. With a use os _BeaconServiceConnection_ application may register listeners: _BeaconsListener_ and _RegionListener_. The fist one is notified every second about all beacons in range mathing the registered regions. The later one gets in-app event when region is entered or exited. Active mode works even if Background scanning has been disabled in options.

User is always notified when service is running in the Notification Bar.

### Example
Check the example project to see how to register for region monitoring and ranging in both passive and active mode.



```java
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
```
