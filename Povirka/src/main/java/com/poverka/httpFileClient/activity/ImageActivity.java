package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.util.MyDeviceTypeHelper;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
class ImageActivity {
    private static final float BUTTON_HEIGHT_RATIO = 0.14f;
    private static final float EDIT_WIDTH_RATIO = 0.27f;
    private static final float MARGIN_HEIGHT_RATIO = 0.02f;
    private static final float TEXT_L_HEIGHT_RATIO = 0.065f;
    private static final float TEXT_S_HEIGHT_RATIO = 0.045f;
    private Activity activity;
    private int clicked;
    private Dialog dialog;
    private LayoutType layoutType;
    private int mImageHeight;
    private int mImageWidth;
    private MoveGestureDetector mMoveDetector;
    private RotateGestureDetector mRotateDetector;
    private ScaleGestureDetector mScaleDetector;
    private OnImageResult resultListener;
    private int screenHeight;
    private int screenWidth;
    private View view;
    private Matrix mMatrix = new Matrix();
    private float mScaleFactor = 1.0f;
    private float mRotationDegrees = 0.0f;
    private float mFocusX = 0.0f;
    private float mFocusY = 0.0f;
    private View.OnTouchListener touchListener = new View.OnTouchListener() { // from class: com.poverka.httpFileClient.activity.ImageActivity.5
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            ImageActivity.this.mScaleDetector.onTouchEvent(event);
            ImageActivity.this.mRotateDetector.onTouchEvent(event);
            ImageActivity.this.mMoveDetector.onTouchEvent(event);
            float scaledImageCenterX = (ImageActivity.this.mImageWidth * ImageActivity.this.mScaleFactor) / 2.0f;
            float scaledImageCenterY = (ImageActivity.this.mImageHeight * ImageActivity.this.mScaleFactor) / 2.0f;
            ImageActivity.this.mMatrix.reset();
            ImageActivity.this.mMatrix.postScale(ImageActivity.this.mScaleFactor, ImageActivity.this.mScaleFactor);
            ImageActivity.this.mMatrix.postRotate(ImageActivity.this.mRotationDegrees, scaledImageCenterX, scaledImageCenterY);
            ImageActivity.this.mMatrix.postTranslate(ImageActivity.this.mFocusX - scaledImageCenterX, ImageActivity.this.mFocusY - scaledImageCenterY);
            ImageView view = (ImageView) v;
            view.setImageMatrix(ImageActivity.this.mMatrix);
            return true;
        }
    };

    public enum LayoutType {
        START,
        MEASUREMENT
    }

    interface OnImageResult {
        void imageResult(Bundle bundle);
    }

    static /* synthetic */ float access$1024(ImageActivity x0, float x1) {
        float f = x0.mRotationDegrees - x1;
        x0.mRotationDegrees = f;
        return f;
    }

    static /* synthetic */ float access$1116(ImageActivity x0, float x1) {
        float f = x0.mFocusX + x1;
        x0.mFocusX = f;
        return f;
    }

    static /* synthetic */ float access$1216(ImageActivity x0, float x1) {
        float f = x0.mFocusY + x1;
        x0.mFocusY = f;
        return f;
    }

    static /* synthetic */ float access$932(ImageActivity x0, float x1) {
        float f = x0.mScaleFactor * x1;
        x0.mScaleFactor = f;
        return f;
    }

    ImageActivity(Activity activity, OnImageResult resultListener, Bundle bundle) {
        String counterNumber;
        String char1;
        String char2;
        int volume;
        int year;
        int dnTypeID;
        int value;
        this.activity = activity;
        this.resultListener = resultListener;
        this.view = activity.getLayoutInflater().inflate(R.layout.activity_image, (ViewGroup) null);
        this.dialog = new Dialog(activity, android.R.style.Theme.DeviceDefault.Light.NoActionBar);
        this.layoutType = (LayoutType) bundle.getSerializable("layoutType");
        this.clicked = bundle.getInt("clicked");
        Bitmap image = (Bitmap) bundle.getParcelable("image");
        if (this.layoutType == LayoutType.START) {
            String counterNumber2 = bundle.getString("number");
            int volume2 = bundle.getInt("volume");
            int year2 = bundle.getInt("year");
            int dnTypeID2 = bundle.getInt("dnType");
            String char12 = bundle.getString("char1");
            String char22 = bundle.getString("char2");
            counterNumber = counterNumber2;
            char1 = char12;
            char2 = char22;
            volume = volume2;
            year = year2;
            dnTypeID = dnTypeID2;
            value = 0;
        } else if (this.layoutType != LayoutType.MEASUREMENT) {
            counterNumber = "";
            char1 = "";
            char2 = "";
            volume = 0;
            year = 0;
            dnTypeID = 0;
            value = 0;
        } else {
            int value2 = bundle.getInt("value");
            counterNumber = "";
            char1 = "";
            char2 = "";
            volume = 0;
            year = 0;
            dnTypeID = 0;
            value = value2;
        }
        initImage(image);
        initViews(counterNumber, volume, year, dnTypeID, value, char1, char2);
        this.dialog.setContentView(this.view);
        this.dialog.show();
    }

    private void initImage(Bitmap bitmap) {
        DisplayMetrics metrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.screenWidth = metrics.widthPixels;
        this.screenHeight = metrics.heightPixels;
        Log.d("tag", this.screenWidth + " : " + this.screenHeight);
        this.mFocusX = (this.screenWidth * 0.6f) / 2.0f;
        this.mFocusY = (this.screenHeight - 40) / 2.0f;
        ImageView image = (ImageView) this.view.findViewById(R.id.imageLarge);
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
        image.setScaleType(ImageView.ScaleType.MATRIX);
        image.setOnTouchListener(this.touchListener);
        this.mImageWidth = image.getDrawable().getIntrinsicWidth();
        int intrinsicHeight = image.getDrawable().getIntrinsicHeight();
        this.mImageHeight = intrinsicHeight;
        float f = this.mImageWidth;
        float f2 = this.mScaleFactor;
        float scaledImageCenterX = (f * f2) / 2.0f;
        float scaledImageCenterY = (intrinsicHeight * f2) / 2.0f;
        this.mMatrix.postScale(f2, f2);
        this.mMatrix.postTranslate(this.mFocusX - scaledImageCenterX, this.mFocusY - scaledImageCenterY);
        image.setImageMatrix(this.mMatrix);
        this.mScaleDetector = new ScaleGestureDetector(this.activity.getApplicationContext(), new ScaleListener());
        this.mRotateDetector = new RotateGestureDetector(this.activity.getApplicationContext(), new RotateListener());
        this.mMoveDetector = new MoveGestureDetector(this.activity.getApplicationContext(), new MoveListener());
    }

    private void initViews(String counterNumber, int volume, int year, int dnTypeID, int value, final String char1, final String char2) {
        final int marginHeight;
        int textLHeight;
        int buttonHeight;
        Button buttonSave;
        int textSHeight;
        TextView textImageName;
        int textSHeight2;
        int editWidth;
        JSONObject deviceType;
        TextView textImageName2 = (TextView) this.view.findViewById(R.id.textImageName);
        Button buttonSave2 = (Button) this.view.findViewById(R.id.buttonSave);
        int i = this.screenHeight;
        int buttonHeight2 = (int) (i * BUTTON_HEIGHT_RATIO);
        final int editWidth2 = (int) (this.screenWidth * EDIT_WIDTH_RATIO);
        final int textLHeight2 = (int) (i * TEXT_L_HEIGHT_RATIO);
        final int textSHeight3 = (int) (i * TEXT_S_HEIGHT_RATIO);
        int marginHeight2 = (int) (i * MARGIN_HEIGHT_RATIO);
        ((LinearLayout.LayoutParams) textImageName2.getLayoutParams()).bottomMargin = marginHeight2;
        textImageName2.requestLayout();
        if (this.layoutType != LayoutType.START) {
            marginHeight = marginHeight2;
            textLHeight = textLHeight2;
            buttonHeight = buttonHeight2;
            buttonSave = buttonSave2;
            if (this.layoutType == LayoutType.MEASUREMENT) {
                int i2 = this.clicked;
                int meas = ((i2 - 1) / 2) + 1;
                if ((i2 - 1) % 2 == 0) {
                    textImageName = textImageName2;
                    textImageName.setText(String.format(Locale.ROOT, this.activity.getString(R.string.start_number), Integer.valueOf(meas)));
                } else {
                    textImageName = textImageName2;
                    textImageName.setText(String.format(Locale.ROOT, this.activity.getString(R.string.finish_number), Integer.valueOf(meas)));
                }
                this.view.findViewById(R.id.startLayout).setVisibility(8);
                TextView textLiter = (TextView) this.view.findViewById(R.id.textLiter);
                EditText editLiter = (EditText) this.view.findViewById(R.id.editLiter);
                ((ConstraintLayout.LayoutParams) editLiter.getLayoutParams()).width = editWidth2;
                editLiter.requestLayout();
                textSHeight = textSHeight3;
                textLiter.setTextSize(0, textSHeight);
                editLiter.setTextSize(0, textSHeight);
                editLiter.setText(String.format(Locale.ROOT, "%.2f", Float.valueOf(value / 1000.0f)));
                editLiter.requestFocus();
            } else {
                textSHeight = textSHeight3;
                textImageName = textImageName2;
            }
        } else {
            textImageName2.setText(R.string.test_photo);
            this.view.findViewById(R.id.measurementLayout).setVisibility(8);
            TextView textNumber = (TextView) this.view.findViewById(R.id.textNumber);
            EditText editNumber = (EditText) this.view.findViewById(R.id.editNumber);
            TextView textVolume = (TextView) this.view.findViewById(R.id.textVolume);
            EditText editVolume = (EditText) this.view.findViewById(R.id.editVolume);
            TextView textYear = (TextView) this.view.findViewById(R.id.textYear);
            EditText editYear = (EditText) this.view.findViewById(R.id.editYear);
            TextView textDN = (TextView) this.view.findViewById(R.id.textDN);
            final Spinner spinnerDN = (Spinner) this.view.findViewById(R.id.spinnerDN);
            TextView textType = (TextView) this.view.findViewById(R.id.textType);
            final Spinner spinnerName = (Spinner) this.view.findViewById(R.id.spinnerType);
            final Spinner spinnerVendor = (Spinner) this.view.findViewById(R.id.spinnerVendor);
            ConstraintLayout.LayoutParams paramsC = (ConstraintLayout.LayoutParams) editNumber.getLayoutParams();
            paramsC.width = editWidth2;
            paramsC.bottomMargin = marginHeight2;
            editNumber.requestLayout();
            ConstraintLayout.LayoutParams paramsC2 = (ConstraintLayout.LayoutParams) editVolume.getLayoutParams();
            paramsC2.width = editWidth2;
            paramsC2.bottomMargin = marginHeight2;
            editVolume.requestLayout();
            ConstraintLayout.LayoutParams paramsC3 = (ConstraintLayout.LayoutParams) editYear.getLayoutParams();
            paramsC3.width = editWidth2;
            paramsC3.bottomMargin = marginHeight2;
            editYear.requestLayout();
            ((ConstraintLayout.LayoutParams) spinnerDN.getLayoutParams()).width = editWidth2;
            spinnerDN.requestLayout();
            ((ConstraintLayout.LayoutParams) spinnerName.getLayoutParams()).width = editWidth2;
            spinnerName.requestLayout();
            marginHeight = marginHeight2;
            textNumber.setTextSize(0, textSHeight3);
            editNumber.setTextSize(0, textSHeight3);
            textVolume.setTextSize(0, textSHeight3);
            editVolume.setTextSize(0, textSHeight3);
            textYear.setTextSize(0, textSHeight3);
            editYear.setTextSize(0, textSHeight3);
            textDN.setTextSize(0, textSHeight3);
            textType.setTextSize(0, textSHeight3);
            editNumber.setText(counterNumber);
            editVolume.setText(String.format(Locale.ROOT, "%05d", Integer.valueOf(volume)));
            editYear.setText(String.format(Locale.ROOT, "%04d", Integer.valueOf(year)));
            JSONObject deviceType2 = MyDeviceTypeHelper.getJsonById(this.activity, dnTypeID);
            try {
                try {
                    String[] names = MyDeviceTypeHelper.getNamesByChars(this.activity, char1, char2);
                    Arrays.sort(names);
                    try {
                        String[] array = new String[names.length + 1];
                        try {
                            try {
                                array[0] = this.activity.getString(R.string.select);
                                System.arraycopy(names, 0, array, 1, names.length);
                                try {
                                    textSHeight2 = textSHeight3;
                                    textLHeight = textLHeight2;
                                    editWidth = editWidth2;
                                    try {
                                        ArrayAdapter<String> adapterName = new ArrayAdapter<String>(this.activity, R.layout.spinner_item, array) { // from class: com.poverka.httpFileClient.activity.ImageActivity.1
                                            @Override // android.widget.ArrayAdapter, android.widget.Adapter
                                            public View getView(int position, View convertView, ViewGroup parent) {
                                                View v = super.getView(position, convertView, parent);
                                                ((TextView) v).setTextSize(0, textSHeight3);
                                                return v;
                                            }

                                            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
                                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                                View v = super.getDropDownView(position, convertView, parent);
                                                ((TextView) v).setTextSize(0, textLHeight2);
                                                int i3 = marginHeight;
                                                v.setPadding(i3, i3, 0, 0);
                                                ((TextView) v).setWidth((int) (((double) editWidth2) * 1.5d));
                                                return v;
                                            }
                                        };
                                        spinnerName.setAdapter((SpinnerAdapter) adapterName);
                                        deviceType = deviceType2;
                                        if (deviceType != null) {
                                            try {
                                                spinnerName.setSelection(adapterName.getPosition(deviceType.getString("name")));
                                            } catch (JSONException e) {
                                                e = e;
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e2) {
                                        e = e2;
                                        deviceType = deviceType2;
                                    }
                                } catch (JSONException e3) {
                                    e = e3;
                                    textSHeight2 = textSHeight3;
                                    editWidth = editWidth2;
                                    textLHeight = textLHeight2;
                                    deviceType = deviceType2;
                                }
                            } catch (JSONException e4) {
                                e = e4;
                                textSHeight2 = textSHeight3;
                                editWidth = editWidth2;
                                textLHeight = textLHeight2;
                                deviceType = deviceType2;
                            }
                        } catch (JSONException e5) {
                            e = e5;
                            textSHeight2 = textSHeight3;
                            editWidth = editWidth2;
                            textLHeight = textLHeight2;
                            deviceType = deviceType2;
                        }
                    } catch (JSONException e6) {
                        e = e6;
                        textSHeight2 = textSHeight3;
                        editWidth = editWidth2;
                        textLHeight = textLHeight2;
                        deviceType = deviceType2;
                    }
                } catch (JSONException e7) {
                    e = e7;
                    textSHeight2 = textSHeight3;
                    editWidth = editWidth2;
                    textLHeight = textLHeight2;
                    deviceType = deviceType2;
                }
            } catch (JSONException e8) {
                e = e8;
                textSHeight2 = textSHeight3;
                editWidth = editWidth2;
                textLHeight = textLHeight2;
                deviceType = deviceType2;
            }
            final int i3 = textSHeight2;
            final int i4 = textLHeight;
            final JSONObject deviceType3 = deviceType;
            final int i5 = editWidth;
            buttonHeight = buttonHeight2;
            buttonSave = buttonSave2;
            spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.poverka.httpFileClient.activity.ImageActivity.2
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String name = spinnerName.getSelectedItem().toString();
                        String[] DNs = MyDeviceTypeHelper.getDNsByNameAndChars(ImageActivity.this.activity, name, char1, char2);
                        Arrays.sort(DNs);
                        if (DNs.length > 1) {
                            String[] array2 = new String[DNs.length + 1];
                            array2[0] = ImageActivity.this.activity.getString(R.string.select);
                            System.arraycopy(DNs, 0, array2, 1, DNs.length);
                            DNs = array2;
                        }
                        ArrayAdapter<String> adapterDN = new ArrayAdapter<String>(ImageActivity.this.activity, R.layout.spinner_item, DNs) { // from class: com.poverka.httpFileClient.activity.ImageActivity.2.1
                            @Override // android.widget.ArrayAdapter, android.widget.Adapter
                            public View getView(int position2, View convertView, ViewGroup parent2) {
                                View v = super.getView(position2, convertView, parent2);
                                ((TextView) v).setTextSize(0, i3);
                                return v;
                            }

                            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
                            public View getDropDownView(int position2, View convertView, ViewGroup parent2) {
                                View v = super.getDropDownView(position2, convertView, parent2);
                                ((TextView) v).setTextSize(0, i4);
                                v.setPadding(marginHeight, marginHeight, 0, 0);
                                ((TextView) v).setWidth((int) (((double) i5) * 1.5d));
                                return v;
                            }
                        };
                        spinnerDN.setAdapter((SpinnerAdapter) adapterDN);
                        JSONObject jSONObject = deviceType3;
                        if (jSONObject != null) {
                            spinnerDN.setSelection(adapterDN.getPosition(jSONObject.getString("dn")));
                        }
                    } catch (JSONException e9) {
                        e9.printStackTrace();
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            final int i6 = textSHeight2;
            final int i7 = textLHeight;
            final int i8 = editWidth;
            spinnerDN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.poverka.httpFileClient.activity.ImageActivity.3
                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String dn = spinnerDN.getSelectedItem().toString();
                        String name = spinnerName.getSelectedItem().toString();
                        String[] vendors = MyDeviceTypeHelper.getVendorsByIDs(ImageActivity.this.activity, MyDeviceTypeHelper.getVendorIdsByAll(ImageActivity.this.activity, dn, name, char1, char2));
                        Arrays.sort(vendors);
                        if (vendors.length > 1) {
                            String[] array2 = new String[vendors.length + 1];
                            array2[0] = ImageActivity.this.activity.getString(R.string.select);
                            System.arraycopy(vendors, 0, array2, 1, vendors.length);
                            vendors = array2;
                        }
                        ArrayAdapter<String> adapterVendor = new ArrayAdapter<String>(ImageActivity.this.activity, R.layout.spinner_item, vendors) { // from class: com.poverka.httpFileClient.activity.ImageActivity.3.1
                            @Override // android.widget.ArrayAdapter, android.widget.Adapter
                            public View getView(int position2, View convertView, ViewGroup parent2) {
                                View v = super.getView(position2, convertView, parent2);
                                ((TextView) v).setTextSize(0, i6);
                                return v;
                            }

                            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
                            public View getDropDownView(int position2, View convertView, ViewGroup parent2) {
                                View v = super.getDropDownView(position2, convertView, parent2);
                                ((TextView) v).setTextSize(0, i7);
                                v.setPadding(marginHeight, marginHeight, 0, 0);
                                ((TextView) v).setWidth((int) (((double) i8) * 1.5d));
                                return v;
                            }
                        };
                        spinnerVendor.setAdapter((SpinnerAdapter) adapterVendor);
                        if (deviceType3 != null) {
                            spinnerVendor.setSelection(adapterVendor.getPosition(MyDeviceTypeHelper.getVendorById(ImageActivity.this.activity, deviceType3.getInt("vendorId"))));
                        }
                    } catch (JSONException e9) {
                        e9.printStackTrace();
                    }
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            textSHeight = textSHeight2;
            textImageName = textImageName2;
        }
        LinearLayout.LayoutParams paramsL = (LinearLayout.LayoutParams) buttonSave.getLayoutParams();
        paramsL.height = buttonHeight;
        paramsL.topMargin = marginHeight;
        buttonSave.requestLayout();
        textImageName.setTextSize(0, textLHeight);
        Button buttonSave3 = buttonSave;
        buttonSave3.setTextSize(0, textSHeight);
        buttonSave3.setOnClickListener(new View.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ImageActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (ImageActivity.this.layoutType != LayoutType.START) {
                    if (ImageActivity.this.layoutType == LayoutType.MEASUREMENT) {
                        EditText editLiter2 = (EditText) ImageActivity.this.view.findViewById(R.id.editLiter);
                        if (editLiter2.getText().length() == 0) {
                            bundle.putInt("value", 0);
                        } else {
                            bundle.putInt("value", (int) (Float.parseFloat(editLiter2.getText().toString()) * 1000.0f));
                        }
                    }
                } else {
                    Calendar calendar = Calendar.getInstance();
                    EditText editNumber2 = (EditText) ImageActivity.this.view.findViewById(R.id.editNumber);
                    EditText editVolume2 = (EditText) ImageActivity.this.view.findViewById(R.id.editVolume);
                    EditText editYear2 = (EditText) ImageActivity.this.view.findViewById(R.id.editYear);
                    Spinner spinnerDN2 = (Spinner) ImageActivity.this.view.findViewById(R.id.spinnerDN);
                    Spinner spinnerName2 = (Spinner) ImageActivity.this.view.findViewById(R.id.spinnerType);
                    Spinner spinnerVendor2 = (Spinner) ImageActivity.this.view.findViewById(R.id.spinnerVendor);
                    if (spinnerDN2.getSelectedItem() == null || spinnerName2.getSelectedItem() == null || spinnerVendor2.getSelectedItem() == null) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.error_type_selection), 1).show();
                        return;
                    }
                    String dn = spinnerDN2.getSelectedItem().toString();
                    String name = spinnerName2.getSelectedItem().toString();
                    int vendorId = MyDeviceTypeHelper.getVendorIdByName(ImageActivity.this.activity, spinnerVendor2.getSelectedItem().toString());
                    int deviceTypeId = MyDeviceTypeHelper.typeToID(ImageActivity.this.activity, dn, name, char1, char2, vendorId);
                    if (editVolume2.length() != 5) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.error_volume_size), 1).show();
                        return;
                    }
                    if (editYear2.length() != 4) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.error_year), 1).show();
                        return;
                    }
                    if (Integer.parseInt(editYear2.getText().toString()) > calendar.get(1)) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.check_year), 1).show();
                        return;
                    }
                    if (spinnerName2.getSelectedItem().toString().equals(ImageActivity.this.activity.getString(R.string.select))) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.select_type), 1).show();
                        return;
                    }
                    if (spinnerDN2.getSelectedItem().toString().equals(ImageActivity.this.activity.getString(R.string.select))) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.select_dn), 1).show();
                        return;
                    }
                    if (spinnerVendor2.getSelectedItem().toString().equals(ImageActivity.this.activity.getString(R.string.select))) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.select_vendor), 1).show();
                        return;
                    }
                    if (deviceTypeId == 0) {
                        Toast.makeText(ImageActivity.this.activity.getApplicationContext(), ImageActivity.this.activity.getString(R.string.error_counter_info), 1).show();
                        return;
                    }
                    Log.d("TAG", String.valueOf(deviceTypeId));
                    bundle.putString("number", editNumber2.getText().toString().trim());
                    bundle.putInt("volume", Integer.parseInt(editVolume2.getText().toString()));
                    bundle.putInt("year", Integer.parseInt(editYear2.getText().toString()));
                    bundle.putInt("dnType", deviceTypeId);
                }
                bundle.putInt("clicked", ImageActivity.this.clicked);
                bundle.putSerializable("layoutType", ImageActivity.this.layoutType);
                ImageActivity.this.resultListener.imageResult(bundle);
                ImageActivity.this.dialog.dismiss();
            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            ImageActivity.access$932(ImageActivity.this, detector.getScaleFactor());
            ImageActivity imageActivity = ImageActivity.this;
            imageActivity.mScaleFactor = Math.max(0.1f, Math.min(imageActivity.mScaleFactor, 10.0f));
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        private RotateListener() {
        }

        @Override // com.almeros.android.multitouch.RotateGestureDetector.SimpleOnRotateGestureListener, com.almeros.android.multitouch.RotateGestureDetector.OnRotateGestureListener
        public boolean onRotate(RotateGestureDetector detector) {
            ImageActivity.access$1024(ImageActivity.this, detector.getRotationDegreesDelta());
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        private MoveListener() {
        }

        @Override // com.almeros.android.multitouch.MoveGestureDetector.SimpleOnMoveGestureListener, com.almeros.android.multitouch.MoveGestureDetector.OnMoveGestureListener
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            ImageActivity.access$1116(ImageActivity.this, d.x);
            ImageActivity.access$1216(ImageActivity.this, d.y);
            return true;
        }
    }
}
