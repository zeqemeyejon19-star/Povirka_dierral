package com.poverka.httpFileClient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.core.view.GravityCompat;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.containers.Address;
import com.poverka.httpFileClient.containers.Town;
import com.poverka.httpFileClient.searchableSpinner.SearchableAdapter;
import com.poverka.httpFileClient.searchableSpinner.SearchableSpinner;
import com.poverka.httpFileClient.util.MyFileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class ClientActivity {
    private static final String TAG = "ClientActivity";
    private final Activity activity;
    private final Dialog dialog;
    private final OnClientResult resultListener;
    private boolean showSpinnerAddress;
    private int sleepSeconds;
    private final Timer sleepTimer;
    private final int todayVerificationNumber;
    private final View view;

    interface OnClientResult {
        void clientResult(Bundle bundle);
    }

    static /* synthetic */ int access$608(ClientActivity x0) {
        int i = x0.sleepSeconds;
        x0.sleepSeconds = i + 1;
        return i;
    }

    ClientActivity(Activity activity, OnClientResult resultListener, Bundle bundle) {
        this.activity = activity;
        this.resultListener = resultListener;
        View viewInflate = activity.getLayoutInflater().inflate(R.layout.activity_client, (ViewGroup) null);
        this.view = viewInflate;
        Dialog dialog = new Dialog(activity, android.R.style.Theme.DeviceDefault.Light.NoActionBar);
        this.dialog = dialog;
        this.sleepSeconds = 0;
        Timer timer = new Timer();
        this.sleepTimer = timer;
        timer.schedule(new SleepTimerTask(), 0L, 1000L);
        String id = bundle.getString("id");
        String protocolNumber = bundle.getString("protocolNumber");
        this.todayVerificationNumber = bundle.getInt("todayVerificationNumber");
        String counterNumber = bundle.getString("counterNumber");
        String dateTime = bundle.getString("dateTime");
        float waterTemperature = bundle.getFloat("waterTemperature");
        uiStuff(id);
        initViews(id, protocolNumber, counterNumber, dateTime, waterTemperature);
        dialog.setContentView(viewInflate);
        dialog.show();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:25:0x03a7  */
    /* JADX WARN: Type inference failed for: r1v127, types: [android.widget.SpinnerAdapter, com.poverka.httpFileClient.activity.ClientActivity$1] */
    /* JADX WARN: Type inference failed for: r1v128 */
    /* JADX WARN: Type inference failed for: r1v129 */
    /* JADX WARN: Type inference failed for: r1v130, types: [android.widget.EditText] */
    /* JADX WARN: Type inference failed for: r1v163 */
    /* JADX WARN: Type inference failed for: r1v165 */
    /* JADX WARN: Type inference failed for: r1v169 */
    /* JADX WARN: Type inference failed for: r1v172, types: [android.widget.EditText] */
    /* JADX WARN: Type inference failed for: r1v173 */
    /* JADX WARN: Type inference failed for: r1v174 */
    /* JADX WARN: Type inference failed for: r1v175 */
    /* JADX WARN: Type inference failed for: r1v176 */
    /* JADX WARN: Type inference failed for: r1v177 */
    /* JADX WARN: Type inference failed for: r1v178 */
    /* JADX WARN: Type inference failed for: r1v179 */
    /* JADX WARN: Type inference failed for: r1v180 */
    /* JADX WARN: Type inference failed for: r1v181 */
    /* JADX WARN: Type inference failed for: r1v182 */
    /* JADX WARN: Type inference failed for: r3v1, types: [android.widget.Spinner] */
    /* JADX WARN: Type inference failed for: r3v13, types: [org.json.JSONObject] */
    /* JADX WARN: Type inference failed for: r3v15 */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v18 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v20, types: [android.widget.EditText] */
    /* JADX WARN: Type inference failed for: r3v21 */
    /* JADX WARN: Type inference failed for: r3v22 */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v24 */
    /* JADX WARN: Type inference failed for: r3v25 */
    /* JADX WARN: Type inference failed for: r3v26 */
    /* JADX WARN: Type inference failed for: r3v27 */
    /* JADX WARN: Type inference failed for: r3v28 */
    /* JADX WARN: Type inference failed for: r3v29 */
    /* JADX WARN: Type inference failed for: r3v30 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r3v9, types: [android.widget.EditText] */
    /* JADX WARN: Type inference failed for: r5v37, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r5v38 */
    /* JADX WARN: Type inference failed for: r5v39 */
    /* JADX WARN: Type inference failed for: r5v40, types: [com.poverka.httpFileClient.searchableSpinner.SearchableSpinner] */
    /* JADX WARN: Type inference failed for: r5v42 */
    /* JADX WARN: Type inference failed for: r5v43 */
    /* JADX WARN: Type inference failed for: r5v44 */
    /* JADX WARN: Type inference failed for: r5v45 */
    /* JADX WARN: Type inference failed for: r5v46 */
    /* JADX WARN: Type inference failed for: r5v47 */
    /* JADX WARN: Type inference failed for: r5v50 */
    /* JADX WARN: Type inference failed for: r5v53 */
    /* JADX WARN: Type inference failed for: r5v54 */
    /* JADX WARN: Type inference failed for: r5v55 */
    /* JADX WARN: Type inference failed for: r5v56 */
    /* JADX WARN: Type inference failed for: r5v57 */
    /* JADX WARN: Type inference failed for: r64v1 */
    /* JADX WARN: Type inference failed for: r6v10 */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v14 */
    /* JADX WARN: Type inference failed for: r6v18 */
    /* JADX WARN: Type inference failed for: r6v19 */
    /* JADX WARN: Type inference failed for: r6v2, types: [int] */
    /* JADX WARN: Type inference failed for: r6v21 */
    /* JADX WARN: Type inference failed for: r6v22 */
    /* JADX WARN: Type inference failed for: r6v23, types: [android.widget.Spinner] */
    /* JADX WARN: Type inference failed for: r6v24 */
    /* JADX WARN: Type inference failed for: r6v25 */
    /* JADX WARN: Type inference failed for: r6v26 */
    /* JADX WARN: Type inference failed for: r6v27 */
    /* JADX WARN: Type inference failed for: r6v28 */
    /* JADX WARN: Type inference failed for: r6v29 */
    /* JADX WARN: Type inference failed for: r6v3 */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v5, types: [android.widget.Spinner] */
    /* JADX WARN: Type inference failed for: r6v7 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void uiStuff(java.lang.String r104) {
        /*
            Method dump skipped, instruction units count: 1461
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.activity.ClientActivity.uiStuff(java.lang.String):void");
    }

    /* JADX WARN: Can't wrap try/catch for region: R(27:0|2|(1:4)(1:5)|6|(1:8)|(2:191|9)|(3:187|11|(7:13|14|(38:159|16|20|(1:22)(1:23)|24|(1:26)(1:27)|28|189|29|30|169|31|32|175|33|(1:35)(1:36)|37|153|38|39|40|(1:42)(1:43)|44|179|45|46|47|(1:49)(1:50)|51|157|52|53|54|(1:56)(1:57)|58|183|59|(3:171|61|(6:63|197|64|(6:67|68|69|(5:71|163|72|73|201)(2:76|200)|77|65)|199|78)(9:83|161|84|85|165|86|87|193|88))(0))(36:20|(0)(0)|24|(0)(0)|28|189|29|30|169|31|32|175|33|(0)(0)|37|153|38|39|40|(0)(0)|44|179|45|46|47|(0)(0)|51|157|52|53|54|(0)(0)|58|183|59|(0)(0))|147|(1:149)(1:150)|151|152)(1:110))(1:113)|155|114|115|167|116|117|181|118|(5:185|120|121|177|122)(1:128)|129|173|130|131|195|132|147|(0)(0)|151|152|(1:(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(28:0|2|(1:4)(1:5)|6|(1:8)|191|9|(3:187|11|(7:13|14|(38:159|16|20|(1:22)(1:23)|24|(1:26)(1:27)|28|189|29|30|169|31|32|175|33|(1:35)(1:36)|37|153|38|39|40|(1:42)(1:43)|44|179|45|46|47|(1:49)(1:50)|51|157|52|53|54|(1:56)(1:57)|58|183|59|(3:171|61|(6:63|197|64|(6:67|68|69|(5:71|163|72|73|201)(2:76|200)|77|65)|199|78)(9:83|161|84|85|165|86|87|193|88))(0))(36:20|(0)(0)|24|(0)(0)|28|189|29|30|169|31|32|175|33|(0)(0)|37|153|38|39|40|(0)(0)|44|179|45|46|47|(0)(0)|51|157|52|53|54|(0)(0)|58|183|59|(0)(0))|147|(1:149)(1:150)|151|152)(1:110))(1:113)|155|114|115|167|116|117|181|118|(5:185|120|121|177|122)(1:128)|129|173|130|131|195|132|147|(0)(0)|151|152|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x046f, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x0471, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x0475, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x0476, code lost:
    
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x047b, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x047c, code lost:
    
        r2 = r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x0483, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x0484, code lost:
    
        r2 = r13;
     */
    /* JADX WARN: Removed duplicated region for block: B:149:0x04ae  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x04b4  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x029f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0206  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0208 A[Catch: JSONException -> 0x03eb, TryCatch #17 {JSONException -> 0x03eb, blocks: (B:11:0x01a6, B:13:0x01ac, B:20:0x01d7, B:24:0x020c, B:27:0x021d, B:23:0x0208), top: B:187:0x01a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x021b  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x021d A[Catch: JSONException -> 0x03eb, TRY_LEAVE, TryCatch #17 {JSONException -> 0x03eb, blocks: (B:11:0x01a6, B:13:0x01ac, B:20:0x01d7, B:24:0x020c, B:27:0x021d, B:23:0x0208), top: B:187:0x01a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0245 A[Catch: JSONException -> 0x03b1, TRY_LEAVE, TryCatch #11 {JSONException -> 0x03b1, blocks: (B:33:0x0239, B:40:0x0252, B:47:0x026b, B:54:0x0284, B:57:0x0290, B:50:0x0277, B:43:0x025e, B:36:0x0245), top: B:175:0x0239 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x025c  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x025e A[Catch: JSONException -> 0x03b1, TRY_LEAVE, TryCatch #11 {JSONException -> 0x03b1, blocks: (B:33:0x0239, B:40:0x0252, B:47:0x026b, B:54:0x0284, B:57:0x0290, B:50:0x0277, B:43:0x025e, B:36:0x0245), top: B:175:0x0239 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x0275  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0277 A[Catch: JSONException -> 0x03b1, TRY_LEAVE, TryCatch #11 {JSONException -> 0x03b1, blocks: (B:33:0x0239, B:40:0x0252, B:47:0x026b, B:54:0x0284, B:57:0x0290, B:50:0x0277, B:43:0x025e, B:36:0x0245), top: B:175:0x0239 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x028e  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0290 A[Catch: JSONException -> 0x03b1, TRY_LEAVE, TryCatch #11 {JSONException -> 0x03b1, blocks: (B:33:0x0239, B:40:0x0252, B:47:0x026b, B:54:0x0284, B:57:0x0290, B:50:0x0277, B:43:0x025e, B:36:0x0245), top: B:175:0x0239 }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x032f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void initViews(java.lang.String r42, java.lang.String r43, java.lang.String r44, java.lang.String r45, float r46) {
        /*
            Method dump skipped, instruction units count: 1262
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.activity.ClientActivity.initViews(java.lang.String, java.lang.String, java.lang.String, java.lang.String, float):void");
    }

    private void initAddress(final int textSHeight, final int textLHeight, final int marginHeight) throws JSONException {
        Spinner spinnerCity = (Spinner) this.view.findViewById(R.id.spinnerCity);
        SearchableSpinner spinnerStreet = (SearchableSpinner) this.view.findViewById(R.id.spinnerStreet);
        JSONObject addressJSON = new JSONObject(MyFileReader.readAndroidFile(this.activity.getFilesDir(), "address.json"));
        JSONArray streetsJSON = addressJSON.getJSONArray("streets");
        ArrayList<Town> cityList = new ArrayList<>();
        cityList.add(new Town(addressJSON.getInt("city_id"), addressJSON.getString("city_name")));
        ArrayList<Address> streetList = new ArrayList<>();
        for (int i = 0; i < streetsJSON.length(); i++) {
            streetList.add(new Address(streetsJSON.getJSONObject(i).getInt("ID"), streetsJSON.getJSONObject(i).getString("STREET_NAME")));
        }
        Activity activity = this.activity;
        int i2 = R.layout.spinner_item;
        ArrayAdapter<Town> adapterCity = new ArrayAdapter<Town>(activity, i2, cityList) { // from class: com.poverka.httpFileClient.activity.ClientActivity.7
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(0, textSHeight);
                ((TextView) v).setGravity(GravityCompat.START);
                return v;
            }

            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(0, textLHeight);
                int i3 = marginHeight;
                v.setPadding(i3, i3, 0, 0);
                ((TextView) v).setGravity(GravityCompat.START);
                return v;
            }
        };
        SearchableAdapter adapterStreet = new SearchableAdapter(this.activity, i2, streetList) { // from class: com.poverka.httpFileClient.activity.ClientActivity.8
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(0, textSHeight);
                ((TextView) v).setGravity(GravityCompat.START);
                return v;
            }

            @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(0, textLHeight);
                int i3 = marginHeight;
                v.setPadding(i3, i3, 0, 0);
                ((TextView) v).setGravity(GravityCompat.START);
                return v;
            }
        };
        spinnerCity.setAdapter((SpinnerAdapter) adapterCity);
        spinnerStreet.setTitle(this.activity.getString(R.string.select_street));
        spinnerStreet.setPositiveButton("");
        spinnerStreet.setAdapter((SpinnerAdapter) adapterStreet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ImageQrClicked() {
        EditText editProtocolNumber = (EditText) this.view.findViewById(R.id.editProtocolNumber);
        String inputValue = String.format(Locale.ROOT, "https://serrp.info/search?id=101%s", editProtocolNumber.getText().toString().replace("-", ""));
        ImageView image = new ImageView(this.activity);
        DisplayMetrics metrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, (int) (metrics.heightPixels * 0.45f));
        image.setImageBitmap(qrgEncoder.getBitmap());
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.verification_docs);
        builder.setMessage(inputValue);
        builder.setView(image);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ShowMoreClicked() {
        int i;
        Button buttonShowMore = (Button) this.view.findViewById(R.id.buttonShowMore);
        ImageButton imageQr = (ImageButton) this.view.findViewById(R.id.imageButtonQR);
        EditText editProtocolNumber = (EditText) this.view.findViewById(R.id.editProtocolNumber);
        EditText editDateTime = (EditText) this.view.findViewById(R.id.editDateTime);
        EditText editCounterNumber = (EditText) this.view.findViewById(R.id.editCounterNumber);
        EditText editSurname = (EditText) this.view.findViewById(R.id.editSurname);
        EditText editCity = (EditText) this.view.findViewById(R.id.editCity);
        Spinner spinnerCity = (Spinner) this.view.findViewById(R.id.spinnerCity);
        EditText editStreet = (EditText) this.view.findViewById(R.id.editStreet);
        SearchableSpinner spinnerStreet = (SearchableSpinner) this.view.findViewById(R.id.spinnerStreet);
        LinearLayout layoutBuilding = (LinearLayout) this.view.findViewById(R.id.layoutBuilding);
        LinearLayout layoutApartment = (LinearLayout) this.view.findViewById(R.id.layoutApartment);
        EditText editPhoneNumber = (EditText) this.view.findViewById(R.id.editPhone);
        EditText editPhoneNumberDop = (EditText) this.view.findViewById(R.id.editPhoneDop);
        EditText editPhoneNumberDop2 = (EditText) this.view.findViewById(R.id.editPhoneDop2);
        EditText editEmail = (EditText) this.view.findViewById(R.id.editEmail);
        TextView textProtocolNumber = (TextView) this.view.findViewById(R.id.textProtocolNumber);
        TextView textDateTime = (TextView) this.view.findViewById(R.id.textDateTime);
        TextView textCounterNumber = (TextView) this.view.findViewById(R.id.textCounterNumber);
        TextView textSurname = (TextView) this.view.findViewById(R.id.textSurname);
        TextView textCity = (TextView) this.view.findViewById(R.id.textStationCity);
        TextView textStreet = (TextView) this.view.findViewById(R.id.textStreet);
        TextView textBuilding = (TextView) this.view.findViewById(R.id.textBuilding);
        TextView textApartment = (TextView) this.view.findViewById(R.id.textApartment);
        TextView textPhoneNumber = (TextView) this.view.findViewById(R.id.textPhone);
        TextView textEmail = (TextView) this.view.findViewById(R.id.textEmail);
        if (editSurname.getVisibility() == 0) {
            buttonShowMore.setText(R.string.show_more);
            imageQr.setVisibility(8);
            editProtocolNumber.setVisibility(8);
            editDateTime.setVisibility(8);
            editCounterNumber.setVisibility(8);
            editSurname.setVisibility(8);
            editCity.setVisibility(8);
            spinnerCity.setVisibility(8);
            editStreet.setVisibility(8);
            spinnerStreet.setVisibility(8);
            layoutBuilding.setVisibility(8);
            layoutApartment.setVisibility(8);
            editPhoneNumber.setVisibility(8);
            editPhoneNumberDop.setVisibility(8);
            editPhoneNumberDop2.setVisibility(8);
            editEmail.setVisibility(8);
            textProtocolNumber.setVisibility(8);
            textDateTime.setVisibility(8);
            textCounterNumber.setVisibility(8);
            textSurname.setVisibility(8);
            textCity.setVisibility(8);
            textStreet.setVisibility(8);
            textBuilding.setVisibility(8);
            textApartment.setVisibility(8);
            textPhoneNumber.setVisibility(8);
            textEmail.setVisibility(8);
            return;
        }
        if (editSurname.getVisibility() == 8) {
            buttonShowMore.setText(R.string.hide_more);
            imageQr.setVisibility(0);
            editProtocolNumber.setVisibility(0);
            editDateTime.setVisibility(0);
            editCounterNumber.setVisibility(0);
            editSurname.setVisibility(0);
            if (this.showSpinnerAddress) {
                i = 0;
                spinnerCity.setVisibility(0);
                spinnerStreet.setVisibility(0);
            } else {
                i = 0;
                editCity.setVisibility(0);
                editStreet.setVisibility(0);
            }
            layoutBuilding.setVisibility(i);
            layoutApartment.setVisibility(i);
            editPhoneNumber.setVisibility(i);
            editPhoneNumberDop.setVisibility(i);
            editPhoneNumberDop2.setVisibility(i);
            editEmail.setVisibility(i);
            textProtocolNumber.setVisibility(i);
            textDateTime.setVisibility(i);
            textCounterNumber.setVisibility(i);
            textSurname.setVisibility(i);
            textCity.setVisibility(i);
            textStreet.setVisibility(i);
            textBuilding.setVisibility(i);
            textApartment.setVisibility(i);
            textPhoneNumber.setVisibility(i);
            textEmail.setVisibility(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void RepeatClicked() {
        EditText editSurname = (EditText) this.view.findViewById(R.id.editSurname);
        EditText editCity = (EditText) this.view.findViewById(R.id.editCity);
        EditText editStreet = (EditText) this.view.findViewById(R.id.editStreet);
        EditText editBuilding = (EditText) this.view.findViewById(R.id.editBuilding);
        EditText editBuildingBukva = (EditText) this.view.findViewById(R.id.editBuildingBukva);
        EditText editBuildingKorpus = (EditText) this.view.findViewById(R.id.editBuildingKorpus);
        EditText editApartment = (EditText) this.view.findViewById(R.id.editApartment);
        EditText editApartmentBukva = (EditText) this.view.findViewById(R.id.editApartmentBukva);
        EditText editSeal = (EditText) this.view.findViewById(R.id.editSeal);
        EditText editPhoneNumber = (EditText) this.view.findViewById(R.id.editPhone);
        EditText editPhoneNumberDop = (EditText) this.view.findViewById(R.id.editPhoneDop);
        EditText editPhoneNumberDop2 = (EditText) this.view.findViewById(R.id.editPhoneDop2);
        EditText editEmail = (EditText) this.view.findViewById(R.id.editEmail);
        Button buttonRepeat = (Button) this.view.findViewById(R.id.buttonRepeat);
        if (!buttonRepeat.getText().toString().contains(this.activity.getString(R.string.fill_client))) {
            if (buttonRepeat.getText().toString().contains(this.activity.getString(R.string.clear_client))) {
                editSurname.setText("");
                editCity.setText("");
                editStreet.setText("");
                editBuilding.setText("");
                editBuildingBukva.setText("");
                editBuildingKorpus.setText("");
                editApartment.setText("");
                editApartmentBukva.setText("");
                editSeal.setText("");
                editPhoneNumber.setText("");
                editPhoneNumberDop.setText("");
                editPhoneNumberDop2.setText("");
                editEmail.setText("");
                buttonRepeat.setText(R.string.fill_client);
                return;
            }
            return;
        }
        File client = new File(this.activity.getApplicationContext().getFilesDir(), "client.json");
        if (client.exists()) {
            try {
                String text = MyFileReader.readAndroidFile(this.activity.getFilesDir(), client.getName());
                JSONObject clientInfo = new JSONObject(text);
                editSurname.setText(clientInfo.getString("SURNAME"));
                editBuilding.setText(String.valueOf(clientInfo.optInt("BUILDING")).equals("0") ? "" : String.valueOf(clientInfo.optInt("BUILDING")));
                editBuildingBukva.setText(clientInfo.getString("BUILDING_BUKVA").equals("null") ? "" : clientInfo.getString("BUILDING_BUKVA"));
                editBuildingKorpus.setText(clientInfo.getString("BUILDING_KORPUS").equals("null") ? "" : clientInfo.getString("BUILDING_KORPUS"));
                editApartment.setText(String.valueOf(clientInfo.optInt("APARTMENT")).equals("0") ? "" : String.valueOf(clientInfo.optInt("APARTMENT")));
                try {
                    editApartmentBukva.setText(clientInfo.getString("APARTMENT_BUKVA").equals("null") ? "" : clientInfo.getString("APARTMENT_BUKVA"));
                    try {
                        editPhoneNumber.setText(clientInfo.getString("PHONE_NUMBER").equals("null") ? "" : clientInfo.getString("PHONE_NUMBER"));
                        try {
                            editPhoneNumberDop.setText(clientInfo.getString("PHONE_NUMBER_DOP").equals("null") ? "" : clientInfo.getString("PHONE_NUMBER_DOP"));
                            try {
                                editPhoneNumberDop2.setText(clientInfo.getString("PHONE_NUMBER_DOP2").equals("null") ? "" : clientInfo.getString("PHONE_NUMBER_DOP2"));
                            } catch (JSONException e) {
                                e = e;
                            }
                        } catch (JSONException e2) {
                            e = e2;
                        }
                        try {
                            editEmail.setText(clientInfo.getString("E_MAIL").equals("null") ? "" : clientInfo.getString("E_MAIL"));
                        } catch (JSONException e3) {
                            e = e3;
                            e.printStackTrace();
                        }
                    } catch (JSONException e4) {
                        e = e4;
                    }
                } catch (JSONException e5) {
                    e = e5;
                }
            } catch (JSONException e6) {
                e = e6;
            }
        }
        if (editSurname.getVisibility() == 8) {
            ShowMoreClicked();
        }
        buttonRepeat.setText(R.string.clear_client);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void SaveClicked() {
        EditText editPaymentAccount = (EditText) this.view.findViewById(R.id.editPaymentAccount);
        EditText editIdNumber = (EditText) this.view.findViewById(R.id.editIdNumber);
        EditText editBuilding = (EditText) this.view.findViewById(R.id.editBuilding);
        EditText editApartment = (EditText) this.view.findViewById(R.id.editApartment);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.alert);
        String paymentAccountCurrent = editPaymentAccount.getText().toString();
        int paymentAccountNumber = 0;
        try {
            paymentAccountNumber = Integer.parseInt(paymentAccountCurrent);
        } catch (NumberFormatException e) {
            if (paymentAccountCurrent.length() != 0) {
                builder.setMessage(R.string.has_to_be_number);
                builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.12
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog stateDialog = builder.create();
                stateDialog.show();
                return;
            }
        }
        if (paymentAccountCurrent.length() != 5) {
            builder.setMessage(R.string.has_to_be_5_digit);
            builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.10
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog stateDialog2 = builder.create();
            stateDialog2.show();
            return;
        }
        if (paymentAccountNumber >= 1 && paymentAccountNumber <= 99999) {
            if (!TextUtils.isEmpty(editBuilding.getText().toString())) {
                try {
                    Integer.parseInt(editBuilding.getText().toString());
                } catch (NumberFormatException e2) {
                    builder.setMessage(R.string.error_building_has_to_be_number);
                    builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.13
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog stateDialog3 = builder.create();
                    stateDialog3.show();
                    return;
                }
            }
            if (!TextUtils.isEmpty(editApartment.getText().toString())) {
                try {
                    Integer.parseInt(editApartment.getText().toString());
                } catch (NumberFormatException e3) {
                    builder.setMessage(R.string.error_apartment_has_to_be_number);
                    builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.14
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog stateDialog4 = builder.create();
                    stateDialog4.show();
                    return;
                }
            }
            final int paymentAccountNumberFinal = paymentAccountNumber;
            if (MainActivity.MARKER == 0 && editIdNumber.isEnabled() && TextUtils.isEmpty(editIdNumber.getText().toString())) {
                String message = this.activity.getString(R.string.want_to_send_to_dispatcher);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.15
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(ClientActivity.TAG, "Диспетчеру");
                        ClientActivity.this.saveClient(1, paymentAccountNumberFinal);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.16
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(ClientActivity.TAG, "Метрологу");
                        ClientActivity.this.saveClient(2, paymentAccountNumberFinal);
                    }
                });
                builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.17
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage(message);
                AlertDialog stateDialog5 = builder.create();
                stateDialog5.show();
                return;
            }
            if (MainActivity.MARKER == 0 && editIdNumber.isEnabled() && !TextUtils.isEmpty(editIdNumber.getText().toString())) {
                String message2 = this.activity.getString(R.string.protocol_will_not_go_to_dispatcher);
                builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.18
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(ClientActivity.TAG, "Метрологу");
                        ClientActivity.this.saveClient(2, paymentAccountNumberFinal);
                    }
                });
                builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.19
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage(message2);
                AlertDialog stateDialog6 = builder.create();
                stateDialog6.show();
                return;
            }
            if (MainActivity.MARKER == 0 && !editIdNumber.isEnabled()) {
                Log.e(TAG, "Диспетчеру");
                saveClient(0, paymentAccountNumberFinal);
                return;
            } else {
                Log.e(TAG, "Метрологу");
                saveClient(0, paymentAccountNumberFinal);
                return;
            }
        }
        builder.setMessage(R.string.has_to_be_00001_99999);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog stateDialog7 = builder.create();
        stateDialog7.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(57:0|2|214|3|4|227|5|6|204|(4:219|8|(4:10|11|12|(4:24|25|26|27)(6:16|17|221|18|19|20))(2:29|(5:31|32|33|34|35)(2:36|(5:38|39|40|41|42)(1:61)))|62)(4:45|46|(4:48|(4:50|51|52|53)(3:54|55|56)|57|58)(0)|62)|205|71|72|208|(2:74|75)(2:79|(1:81)(43:83|84|(1:86)|87|88|212|89|202|90|96|(1:98)(1:100)|101|(1:103)(1:105)|106|(1:108)(1:110)|111|(1:113)(1:115)|116|(1:118)(1:120)|121|(1:123)(1:125)|126|(1:128)(1:130)|131|(1:133)(1:135)|136|(1:138)(1:140)|141|(1:143)(1:145)|146|147|229|148|223|(4:150|225|151|(6:153|154|210|155|(4:157|(6:160|161|162|(2:164|232)(1:233)|165|158)|231|166)(1:167)|168)(1:171))(1:174)|175|176|(3:178|(1:180)(1:181)|182)(1:183)|184|185|217|186|234))|207|87|88|212|89|202|90|96|(0)(0)|101|(0)(0)|106|(0)(0)|111|(0)(0)|116|(0)(0)|121|(0)(0)|126|(0)(0)|131|(0)(0)|136|(0)(0)|141|(0)(0)|146|147|229|148|223|(0)(0)|175|176|(0)(0)|184|185|217|186|234|(1:(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x03a2, code lost:
    
        r0.put("CITY", r15);
        r0.put("STREET", r1);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:0x03bd A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:103:0x03e0 A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:105:0x03e3 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:108:0x03fe A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:110:0x0401 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:113:0x041c A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:115:0x041f A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0442 A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0445 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:123:0x0463 A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0466 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0481 A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0484 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:133:0x049f A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:135:0x04a2 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:138:0x04bd A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:140:0x04c0 A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:143:0x04db A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Removed duplicated region for block: B:145:0x04de A[Catch: JSONException -> 0x06d2, TRY_ENTER, TryCatch #6 {JSONException -> 0x06d2, blocks: (B:89:0x035e, B:96:0x03a8, B:101:0x03cd, B:106:0x03eb, B:111:0x0409, B:116:0x042f, B:121:0x044d, B:126:0x046e, B:131:0x048c, B:136:0x04aa, B:141:0x04c8, B:146:0x04e6, B:145:0x04de, B:140:0x04c0, B:135:0x04a2, B:130:0x0484, B:125:0x0466, B:120:0x0445, B:115:0x041f, B:110:0x0401, B:105:0x03e3, B:100:0x03bd, B:95:0x03a2, B:90:0x0387), top: B:212:0x035e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0578  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0655  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0675 A[Catch: JSONException -> 0x06cc, TRY_ENTER, TryCatch #12 {JSONException -> 0x06cc, blocks: (B:162:0x05c4, B:164:0x05ca, B:165:0x05dc, B:168:0x05ec, B:175:0x065b, B:178:0x0675, B:182:0x068e, B:184:0x069d, B:181:0x0686, B:183:0x0692, B:167:0x05e4), top: B:223:0x0576 }] */
    /* JADX WARN: Removed duplicated region for block: B:183:0x0692 A[Catch: JSONException -> 0x06cc, TryCatch #12 {JSONException -> 0x06cc, blocks: (B:162:0x05c4, B:164:0x05ca, B:165:0x05dc, B:168:0x05ec, B:175:0x065b, B:178:0x0675, B:182:0x068e, B:184:0x069d, B:181:0x0686, B:183:0x0692, B:167:0x05e4), top: B:223:0x0576 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x02e1 A[PHI: r39
  0x02e1: PHI (r39v6 android.widget.EditText) = (r39v4 android.widget.EditText), (r39v8 android.widget.EditText) binds: [B:37:0x0240, B:47:0x0291] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x031a  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x032c A[Catch: JSONException -> 0x06dc, TRY_ENTER, TRY_LEAVE, TryCatch #2 {JSONException -> 0x06dc, blocks: (B:71:0x0304, B:87:0x0350, B:79:0x032c, B:83:0x0340), top: B:205:0x0304 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x03ba A[Catch: JSONException -> 0x0396, TRY_ENTER, TRY_LEAVE, TryCatch #8 {JSONException -> 0x0396, blocks: (B:90:0x0387, B:98:0x03ba, B:103:0x03e0, B:108:0x03fe, B:113:0x041c, B:118:0x0442, B:123:0x0463, B:128:0x0481, B:133:0x049f, B:138:0x04bd, B:143:0x04db), top: B:202:0x0387 }] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.json.JSONObject] */
    /* JADX WARN: Type inference failed for: r15v18 */
    /* JADX WARN: Type inference failed for: r15v19 */
    /* JADX WARN: Type inference failed for: r15v20, types: [java.lang.Object, java.lang.String] */
    /* JADX WARN: Type inference failed for: r15v22 */
    /* JADX WARN: Type inference failed for: r15v23, types: [int] */
    /* JADX WARN: Type inference failed for: r15v24 */
    /* JADX WARN: Type inference failed for: r15v26 */
    /* JADX WARN: Type inference failed for: r15v27 */
    /* JADX WARN: Type inference failed for: r15v30 */
    /* JADX WARN: Type inference failed for: r15v33 */
    /* JADX WARN: Type inference failed for: r15v40 */
    /* JADX WARN: Type inference failed for: r15v41 */
    /* JADX WARN: Type inference failed for: r15v42 */
    /* JADX WARN: Type inference failed for: r15v46 */
    /* JADX WARN: Type inference failed for: r15v47 */
    /* JADX WARN: Type inference failed for: r15v48 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void saveClient(int r42, int r43) {
        /*
            Method dump skipped, instruction units count: 1786
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.poverka.httpFileClient.activity.ClientActivity.saveClient(int, int):void");
    }

    private JSONObject getClientInfoById(int id) {
        String taskString = MyFileReader.readAndroidFile(this.activity.getFilesDir(), "localTasks.json");
        JSONObject clientInfo = null;
        try {
            JSONArray array = new JSONArray(taskString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject currentClient = array.getJSONObject(i);
                if (currentClient.getInt("ID") == id) {
                    clientInfo = currentClient;
                }
            }
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return clientInfo;
    }

    private void updateClientInfo(JSONObject clientInfo) throws JSONException {
        EditText editSurname = (EditText) this.view.findViewById(R.id.editSurname);
        EditText editCity = (EditText) this.view.findViewById(R.id.editCity);
        EditText editStreet = (EditText) this.view.findViewById(R.id.editStreet);
        EditText editBuilding = (EditText) this.view.findViewById(R.id.editBuilding);
        EditText editApartment = (EditText) this.view.findViewById(R.id.editApartment);
        EditText editPhoneNumber = (EditText) this.view.findViewById(R.id.editPhone);
        EditText editEmail = (EditText) this.view.findViewById(R.id.editEmail);
        editSurname.setText(clientInfo.getString("SURNAME"));
        editCity.setText(clientInfo.getString("CITY"));
        editStreet.setText(clientInfo.getString("STREET"));
        editBuilding.setText(clientInfo.getString("BUILDING"));
        editApartment.setText(clientInfo.getString("APARTMENT"));
        editPhoneNumber.setText(clientInfo.getString("PHONE_NUMBER"));
        editEmail.setText(clientInfo.getString("E-MAIL"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopSleepTimer() {
        Timer timer = this.sleepTimer;
        if (timer != null) {
            timer.cancel();
            this.sleepTimer.purge();
        }
    }

    private class SleepTimerTask extends TimerTask {
        AlertDialog alertDialog;
        final AlertDialog.Builder alertDialogBuilder;

        public SleepTimerTask() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClientActivity.this.activity);
            this.alertDialogBuilder = builder;
            builder.setCancelable(false);
            builder.setTitle(R.string.alert);
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            ClientActivity.access$608(ClientActivity.this);
            if (ClientActivity.this.sleepSeconds == 300) {
                ClientActivity.this.activity.runOnUiThread(new Runnable() { // from class: com.poverka.httpFileClient.activity.ClientActivity.SleepTimerTask.1
                    @Override // java.lang.Runnable
                    public void run() {
                        SleepTimerTask.this.alertDialogBuilder.setMessage(R.string.station_is_going_to_sleep);
                        SleepTimerTask.this.alertDialogBuilder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.SleepTimerTask.1.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialog, int which) {
                                ClientActivity.this.sleepSeconds = 0;
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("delay_sleep", true);
                                ClientActivity.this.resultListener.clientResult(bundle);
                            }
                        });
                        SleepTimerTask sleepTimerTask = SleepTimerTask.this;
                        sleepTimerTask.alertDialog = sleepTimerTask.alertDialogBuilder.create();
                        SleepTimerTask.this.alertDialog.show();
                    }
                });
            } else if (ClientActivity.this.sleepSeconds == 600) {
                ClientActivity.this.activity.runOnUiThread(new Runnable() { // from class: com.poverka.httpFileClient.activity.ClientActivity.SleepTimerTask.2
                    @Override // java.lang.Runnable
                    public void run() {
                        SleepTimerTask.this.alertDialog.dismiss();
                        SleepTimerTask.this.alertDialogBuilder.setMessage(R.string.station_is_in_sleep_mode);
                        SleepTimerTask.this.alertDialogBuilder.setPositiveButton("ОК", new DialogInterface.OnClickListener() { // from class: com.poverka.httpFileClient.activity.ClientActivity.SleepTimerTask.2.1
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogI, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("delay_sleep", true);
                                bundle.putBoolean("sleeping", true);
                                ClientActivity.this.resultListener.clientResult(bundle);
                                ClientActivity.this.stopSleepTimer();
                                ClientActivity.this.dialog.dismiss();
                            }
                        });
                        SleepTimerTask sleepTimerTask = SleepTimerTask.this;
                        sleepTimerTask.alertDialog = sleepTimerTask.alertDialogBuilder.create();
                        SleepTimerTask.this.alertDialog.show();
                    }
                });
            }
        }
    }
}
