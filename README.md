nRF-Beacon-Service-API
======================

The public library with nRF Beacon Service API. Allows applications to register for iBeacon monitoring and ranging. 

The nRF Beacon Service has been designed to work like Location Core from iOS on Android. It works in a background and scans periodically for registered iBeacons.

There are two ways of registering for regions: passive and active. 

* The *passive* way works when the application is not in an active state. When going into background it may register a region with a notification that will be shown if region will be entered. Only region monitoring (enter/exit events) is supported in this mode.
* The *active* approach uses binding to the service. It allows to both monitor (enter/exit events) and range regions. With a use os _BeaconServiceConnection_ application may register listeners: _BeaconsListener_ and _RegionListener_. The fist one is notified every second about all beacons mathing the registered regions. The later one gets in-app event when region is entered or exited.
