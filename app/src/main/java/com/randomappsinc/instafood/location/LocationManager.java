package com.randomappsinc.instafood.location;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.PermissionUtils;

public class LocationManager implements LocationForm.Listener {

    // NOTE: If an activity uses this class, IT CANNOT USE MATCHING CODES
    public static final int LOCATION_SERVICES_CODE = 350;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;

    private static final long DESIRED_LOCATION_TURNAROUND = 1000L;
    private static final long MILLISECONDS_BEFORE_FAILURE = 10000L;

    public interface Listener {
        void onServicesOrPermissionChoice();

        void onLocationFetched(String location);
    }

    private final Handler locationChecker = new Handler();
    private final Runnable locationCheckTask = new Runnable() {
        @Override
        public void run() {
            stopFetchingCurrentLocation();
            if (!locationFetched) {
                onLocationFetchFail();
            }
        }
    };

    @NonNull protected Listener listener;
    @NonNull protected Activity activity;

    protected FusedLocationProviderClient locationFetcher;
    protected LocationRequest locationRequest;
    protected boolean locationFetched;

    protected LocationServicesManager locationServicesManager;
    protected MaterialDialog locationDenialDialog;
    protected MaterialDialog locationPermissionDialog;
    protected LocationForm locationForm;

    protected PreferencesManager preferencesManager;

    public LocationManager(@NonNull Listener listener, @NonNull Activity activity) {
        this.listener = listener;
        this.activity = activity;
        initNonContext();
    }

    private void initNonContext() {
        locationServicesManager = new LocationServicesManager(activity);
        locationForm = new LocationForm(activity, this);
        locationDenialDialog = new MaterialDialog.Builder(activity)
                .cancelable(false)
                .title(R.string.location_services_needed)
                .content(R.string.location_services_denial)
                .positiveText(R.string.location_services_confirm)
                .negativeText(R.string.enter_location_manually)
                .onPositive((dialog, which) -> {
                    locationServicesManager.askForLocationServices(LOCATION_SERVICES_CODE);
                    listener.onServicesOrPermissionChoice();
                })
                .onNegative((dialog, which) -> {
                    locationForm.show(R.string.location_form_prompt);
                    listener.onServicesOrPermissionChoice();
                })
                .build();

        locationPermissionDialog = new MaterialDialog.Builder(activity)
                .cancelable(false)
                .title(R.string.location_permission_needed)
                .content(R.string.location_permission_denial)
                .positiveText(R.string.give_location_permission)
                .negativeText(R.string.enter_location_manually)
                .onPositive((dialog, which) -> {
                    requestLocationPermission();
                    listener.onServicesOrPermissionChoice();
                })
                .onNegative((dialog, which) -> {
                    locationForm.show(R.string.location_form_prompt);
                    listener.onServicesOrPermissionChoice();
                })
                .build();

        locationFetcher = LocationServices.getFusedLocationProviderClient(activity);
        locationRequest = LocationRequest.create()
                .setInterval(DESIRED_LOCATION_TURNAROUND)
                .setFastestInterval(DESIRED_LOCATION_TURNAROUND)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        preferencesManager = new PreferencesManager(activity);
    }

    @Override
    public void onLocationEntered(String location) {
        stopFetchingCurrentLocation();
        listener.onLocationFetched(location);
    }

    public void fetchCurrentLocation() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, activity)) {
            checkLocationServicesAndFetchLocationIfOn();
        } else {
            requestLocationPermission();
        }
    }

    private void checkLocationServicesAndFetchLocationIfOn() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(activity);
        client.checkLocationSettings(builder.build())
                .addOnSuccessListener(locationSettingsResponse -> fetchAutomaticLocation())
                .addOnFailureListener(exception -> {
                    if (exception instanceof ResolvableApiException) {
                        locationServicesManager.askForLocationServices(LOCATION_SERVICES_CODE);
                    } else {
                        onLocationFetchFail();
                    }
                });
    }

    protected void requestLocationPermission() {
        PermissionUtils.requestPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void fetchAutomaticLocation() {
        locationFetched = false;
        try {
            locationChecker.postDelayed(locationCheckTask, MILLISECONDS_BEFORE_FAILURE);
            locationFetcher.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException exception) {
            requestLocationPermission();
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                stopFetchingCurrentLocation();
                locationFetched = true;
                Location location = locationResult.getLastLocation();
                String latLongString = location.getLatitude() + ", " + location.getLongitude();
                listener.onLocationFetched(latLongString);
            } else {
                onLocationFetchFail();
            }
        }
    };

    protected void stopFetchingCurrentLocation() {
        locationChecker.removeCallbacks(locationCheckTask);
        locationFetcher.removeLocationUpdates(locationCallback);
    }

    public void showLocationDenialDialog() {
        locationDenialDialog.show();
    }

    public void showLocationPermissionDialog() {
        locationPermissionDialog.show();
    }

    protected void onLocationFetchFail() {
        locationForm.show(R.string.location_form_after_fail);
    }
}
