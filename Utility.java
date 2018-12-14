package com.android.consumerapp.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.consumerapp.R;
import com.android.consumerapp.activity.SupportActivity;
import com.android.consumerapp.activity.WelcomeScreenActivity;
import com.android.consumerapp.alertSettings.AlertSettingPreferenceModel;
import com.android.consumerapp.alertSettings.SettingsManager;
import com.android.consumerapp.analytics.AnalyticsManager;
import com.android.consumerapp.constant.Constants;
import com.android.consumerapp.constant.RemoteValues;
import com.android.consumerapp.data.Preferences;
import com.android.consumerapp.model.Event;
import com.android.consumerapp.model.Location;
import com.android.consumerapp.model.Preference;
import com.google.android.gms.common.util.Predicate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * This class contains utility functions required in the consumerApplication.
 *
 * @author Prashant Nayak
 */
public class Utility {

    private static final String FILE_PATH_FORMAT = "fixtures/%s.json";
    private static HashMap<String, String> stateList;


    public static void hideSoftKey(Activity activity) {
        // 1ST method
        View view = activity.getCurrentFocus();
        hideSoftKey(activity, view);
        // 2ND method
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static void hideSoftKey(Activity activity, View currentFocus) {
        InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            if (currentFocus != null) {
                if (currentFocus.getWindowToken() != null) {
                    inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    public static boolean isInternetConnected(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void showSoftKeyboard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            if (currentFocus != null) {
                if (currentFocus.getWindowToken() != null) {
                    inputManager.showSoftInput(currentFocus, 0);
                }
            }
        }
    }

    public static void hideSoftKeypad(Activity activity) {
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    public static BitmapDescriptor getBitmapDescriptorForVectorDrawable(Context context, Drawable vectorDrawable) {
        int h = vectorDrawable.getIntrinsicHeight();
        int w = vectorDrawable.getIntrinsicWidth();
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

    public static void logout(Context context) {
        Preferences preferences = new Preferences(context);
        preferences.clearAllPreferences();
        Intent intent = new Intent(context,
                WelcomeScreenActivity.class);
        preferences.storeInt("tracker", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void styleSnackBar(Context context, Snackbar snackbar) {
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.primary_red));
        View view = snackbar.getView();
        TextView textView =
                (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(Color.WHITE);
            textView.setMaxLines(4);
        }
    }

    public static void setMenuItemTextColor(MenuItem menuItem, int color) {
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        menuItem.setTitle(s);
    }

    public static void showMessage(View view, int msgResId) {
        Snackbar snackbar = Snackbar.make(view, msgResId, Snackbar.LENGTH_LONG);
        styleSnackBar(view.getContext(), snackbar);
        snackbar.show();
    }

    public static void setStatusBarColorWithColorInt(Window window, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public static int mixTwoColors(int color1, int color2, float amount) {
        //http://stackoverflow.com/questions/6070163/color-mixing-in-android
        final byte ALPHA_CHANNEL = 24;
        final byte RED_CHANNEL = 16;
        final byte GREEN_CHANNEL = 8;
        final byte BLUE_CHANNEL = 0;

        final float inverseAmount = 1.0f - amount;

        int a = ((int) (((float) (color1 >> ALPHA_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> ALPHA_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int r = ((int) (((float) (color1 >> RED_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> RED_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int g = ((int) (((float) (color1 >> GREEN_CHANNEL & 0xff) * amount) +
                ((float) (color2 >> GREEN_CHANNEL & 0xff) * inverseAmount))) & 0xff;
        int b = ((int) (((float) (color1 & 0xff) * amount) +
                ((float) (color2 & 0xff) * inverseAmount))) & 0xff;

        return a << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL | b << BLUE_CHANNEL;
    }

    public static void showKeyboardWithFocus(final EditText editText) {
        if (editText != null) {
            editText.requestFocus();
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 200);
        }
    }

    public static String capitalizeFirstChar(String input) {
        StringBuilder inputCapital = new StringBuilder(input.toLowerCase());
        inputCapital.setCharAt(0, Character.toUpperCase(inputCapital.charAt(0)));
        return inputCapital.toString();
    }

    public static boolean needUpdate(String currentVersion, String remoteVersion) {
        String[] currentVersionArray = currentVersion.split("\\.");
        String[] remoteVersionArray = remoteVersion.split("\\.");
        int maxLength = Math.max(currentVersionArray.length, remoteVersionArray.length);
        boolean needsUpdate = false;
        for (int i = 0; i < maxLength; ++i) {
            int cV = i < currentVersionArray.length ? Integer.parseInt(currentVersionArray[i]) : 0;
            int rV = i < remoteVersionArray.length ? Integer.parseInt(remoteVersionArray[i]) : 0;
            if (cV != rV) {
                needsUpdate = cV < rV;
                break;
            }
        }
        return needsUpdate;
    }

    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMaterialSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String formatContactNumber(String phone) {
        //  \W : A non-word character
        String contact = phone.replaceAll("\\W", "");
        if (contact.length() == 10 && Pattern.compile("^[0-9]+$").matcher(phone).matches()) {
            return String.format("(%s) %s-%s", contact.substring(0, 3), contact.substring(3, 6),
                    contact.substring(6, 10));
        }
        return phone;
    }

    public static String getGeozoneAddress(Address address) {

        if (address == null)
            return "";

        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(address.getAddressLine(0))) {
            builder.append(address.getAddressLine(0).trim());
        }
        if (!TextUtils.isEmpty(address.getAddressLine(1))) {
            builder.append(!TextUtils.isEmpty(builder.toString().trim()) ? ", " + address.getAddressLine(1) : address.getAddressLine(1));
        }
        if (!TextUtils.isEmpty(address.getAddressLine(2))) {
            builder.append(" " + address.getAddressLine(2));
        }
        return builder.toString();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }


    @SuppressWarnings("deprecation")
    public static boolean isScreenOn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isInteractive();
        } else {
            return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
        }
    }

    public static String getAddress(Location location) {
        if (location == null || location.address == null) return "--";
        StringBuilder builder = new StringBuilder();
        com.android.consumerapp.model.Address address = location.address;

        if (!TextUtils.isEmpty(address.line1)) {
            builder.append(address.line1);
        }

        if (!TextUtils.isEmpty(address.city)) {
            builder.append(!TextUtils.isEmpty(builder) ? ", " + address.city : address.city);
        }

        if (!TextUtils.isEmpty(address.stateOrProvince)) {
            builder.append(!TextUtils.isEmpty(builder) ? ", " + address.stateOrProvince : address.stateOrProvince);
        }

        if (!TextUtils.isEmpty(address.postalCode)) {
            builder.append(!TextUtils.isEmpty(builder) ? ", " + address.postalCode : address.postalCode);
        }

        return !TextUtils.isEmpty(builder.toString().trim()) ? builder.toString() : "--";
    }

    /**
     * This method takes input as context and location with lat, lng and uses makeGeoCodeApi to convert lat lng to address and build it
     * in proper display format using getGeoZoneAddress method.
     * @param context
     * @param location
     * @return
     */
    public static String getAddress(Context context, Location location) {

        if (location == null || location.address == null) return "--";
        String key = location.lat + "," + location.lng;
        String buildedAddress = "--";
        Address address = null;
        if (CacheManager.INSTANCE.hasData(key)) {
            address = CacheManager.INSTANCE.getCachedAddress(key);
        } else {
            LatLng latLng = new LatLng(location.lat, location.lng);
            address = makeGeoCodeAPI(context, latLng, "");
            CacheManager.INSTANCE.add(key, address);
        }

        buildedAddress = getGeozoneAddress(address);
        return buildedAddress;
    }

    static String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};

    public static String getHeading(String heading) {
        if (TextUtils.isEmpty(heading)) return "";
        try {
            double temp = Double.parseDouble(heading);
            return directions[(int) Math.round(((temp % 360) / 45))];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getStatusColor(String vehicleStatus) {
        if (Constants.VEHICLE_STATUS_MOVING.equals(vehicleStatus)) {
            return R.color.vehicle_status_moving;
        } else if (Constants.VEHICLE_STATUS_STOPPED.equals(vehicleStatus)) {
            return R.color.vehicle_status_stopped;
        } else if (Constants.VEHICLE_STATUS_IDLE.equals(vehicleStatus)) {
            return R.color.vehicle_status_idle;
        }
        return R.color.vehicle_status_stopped;
    }

    public static String getDistanceCovered(Double startOdometer, Double endOdometer) {
        //handling edge cases
        if (startOdometer == null && endOdometer == null) return "";
        if (startOdometer == null && endOdometer != null) return String.valueOf(endOdometer);
        if (startOdometer != null && endOdometer == null) return String.valueOf(startOdometer);
        return String.valueOf(new DecimalFormat("#.#").format(endOdometer - startOdometer));
    }

    public static Double getDistanceCoveredInMiles(Double startOdometer, Double endOdometer) {
        //handling edge cases
        if (startOdometer == null && endOdometer == null) return 0.0;
        if (startOdometer == null && endOdometer != null) return endOdometer;
        if (startOdometer != null && endOdometer == null) return startOdometer;
        Double resultDistanceCovered = endOdometer - startOdometer;
        return resultDistanceCovered;
    }

    private static final float COORDINATE_OFFSET = 0.000009f;

    /**
     * Check if another marker exist on the given lat lng if Yes, then get new lat lng with certain offset
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static String[] coordinateForMarker(ConcurrentHashMap<String, String> markerLocationMap, double latitude, double longitude) {
        String[] location = new String[2];
        // Condition should be equal to total number of markers
        for (int i = 0; i <= 10; i++) {

            if (mapAlreadyHasMarkerForLocation(markerLocationMap, (latitude + i
                    * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation(markerLocationMap, (latitude - i
                        * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {

                    continue;

                } else {
                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
                    break;
                }

            } else {
                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
                break;
            }
        }
        return location;
    }

    // Return whether marker with same location is already on map
    public static boolean mapAlreadyHasMarkerForLocation(ConcurrentHashMap<String, String> markerLocationMap, String location) {
        return (markerLocationMap.containsValue(location));
    }

    public static double convertCelsiusToFahrenheit(double temperature) {
        //Celsius to Fahrenheit method
        return (temperature * 9) / 5 + 32;
    }

    public static String generateUUID() {
        // Creating a random UUID (Universally unique identifier).
        return UUID.randomUUID().toString();
    }

    public static <C> List<C> asList(SparseArrayCompat<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }


    public static String getJsonResponse(Context context, String fileName) {

        AssetManager assetManager = context.getAssets();

        // To load text file
        InputStream input;
        try {
            input = assetManager.open(String.format(FILE_PATH_FORMAT, fileName));

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer);

            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Method responsible to calculate the distance between startLocation and endLocation
     *
     * @param startLocation
     * @param endLocation
     * @return
     */
    public static double getDistanceInMeters(LatLng startLocation, LatLng endLocation) {
        double distance = 0.0;
        if (startLocation != null && endLocation != null) {
            distance = SphericalUtil.computeDistanceBetween(startLocation, endLocation);
        }
        return distance;
    }

    /**
     * Method responsible to convert meter to miles
     *
     * @param meters
     * @return
     */
    public static double convertMeterToMiles(double meters) {
        double miles = meters * 0.00062137119;
        return miles;
    }

    /**
     * Calculate distance with respect to lat lng between startEvent and endEvent
     *
     * @param startEvent
     * @param endEvent
     * @return
     */
    public static double getDistance(Event startEvent, Event endEvent) {
        LatLng startPosition = new LatLng(startEvent.location.lat, startEvent.location.lng);
        LatLng endPosition = new LatLng(endEvent.location.lat, endEvent.location.lng);
        return convertMeterToMiles(getDistanceInMeters(startPosition, endPosition));
    }

    /**
     * Calculate AGE value from the given values
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return String.format("%d", age);
    }


    public static Address makeGeoCodeAPI(Context context, LatLng latLng, String origin) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return address.get(0);
        } catch (Exception exception) {
            HashMap<String, Object> extraData = new HashMap<>();
            extraData.put(AnalyticsManager.ExtrasKey.ORIGIN, origin);
            extraData.put(AnalyticsManager.ExtrasKey.LOCATION_SERVICE_ERROR_DETAILS, exception != null ? exception.getMessage() : "");
            AnalyticsManager.getInstance().trackEvent(AnalyticsManager.EventKey.LOCATION_SERVICE_REVERSE_GEOCODE_FAILED, extraData);
            exception.printStackTrace();
        }
        return null;
    }

    public static boolean isInsuranceOfferEnabled(ArrayList<AlertSettingPreferenceModel> list) {
        for (int i = 0; i < list.size(); i++) {
            Preference preference = list.get(i).preference;
            if (preference.key.equals(SettingsManager.PreferenceType.INSURANCE_OFFER_OPT_NOTIFICATION)
                    || preference.key.equals(SettingsManager.PreferenceType.INSURANCE_OFFER_OPT_EMAIL)) {
                if (preference.value) {
                    return preference.value;
                }

            }
        }
        return false;
    }

    public static void openPermissionSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }


    public static void makeCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number.replaceAll("\\W", "")));
        context.startActivity(intent);
    }
}



