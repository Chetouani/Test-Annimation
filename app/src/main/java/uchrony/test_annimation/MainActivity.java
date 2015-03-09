package uchrony.test_annimation;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.factory.Filters;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.List;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    // Balise utilisé pour le debuguage
    private String TAG_DEBUG = "TAG_DEBUG_MainActivity";
    // identifient de la demande d'activation du blue
    // pour verifier que l'activation est ok
    private static final int CODE_ACTIVATION_BLUETOOTH = 1;
    // Permet la gestion des beacons et du scan
    private BeaconManager beaconManager;
    // Permet de savoir si le scan est en cours ou alors arréter
    private boolean enCoursDeScan = false;
    //
    ImageView drawingImageView;
    Canvas canvas;
    long i=1;

    private Trilateration trilateration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        afficherPlan(null);

        initElements();
        initBeacon();

        verificationBluetooth();
    }

    private void afficherPlan(Point position) {
        drawingImageView = (ImageView) this.findViewById(R.id.img);
        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawingImageView.setImageBitmap(bitmap);

        // Rectangle
        Paint rectanglePiece = new Paint();
        rectanglePiece.setColor(Color.BLACK);
        rectanglePiece.setStyle(Paint.Style.STROKE);
        rectanglePiece.setStrokeWidth(10);
        float leftx = 0;
        float topy = 0;
        float rightx = 1750;
        float bottomy = 1000;
        canvas.drawRect(leftx, topy, rightx, bottomy, rectanglePiece);
        if (position != null) {
            Paint pointRouge = new Paint();
            pointRouge.setColor(Color.RED);
            pointRouge.setStrokeWidth(50);

            canvas.drawPoint(position.x, 1000 - position.y, pointRouge);
            //canvas.drawPoint(posi,721, pointRouge);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        beaconManager.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (enCoursDeScan) {
            startScan();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (enCoursDeScan) {
            startScan();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * //TODO
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CODE_ACTIVATION_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                connect();
            } else {
                Toast.makeText(this, "Erreur activation Bluetooth", Toast.LENGTH_LONG).show();
                getSupportActionBar().setSubtitle("Erreur activation Bluetooth");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Lance la connection du BeaconManager et démarre le scan
     */
    private void connect() {
        try {
            beaconManager.connect(new OnServiceBoundListener() {
                @Override
                public void onServiceBound() throws RemoteException {
                    startScan();
                }
            });
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG, e.getMessage());
        }
    }

    /**
     * Vérifie si votre GSM posséde le BLE et si il est allumé.
     * Si vous ne l'êtes pas elle tente de l'allumer.
     */
    private void verificationBluetooth() {
        // Verifie que on posséde le bluetooth LE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Votre gsm n'a pas le bluetooth LE", Toast.LENGTH_SHORT).show();
            //TODO quitter l'application si pas de BLE
        }

        if(!beaconManager.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, CODE_ACTIVATION_BLUETOOTH);
        } else if(beaconManager.isConnected()) {
            startScan();
        } else {
            connect();
        }
    }

    /*----------------------------------------------------------------------------------------*/
    /*----------------------------------    BEACON    ----------------------------------------*/
    /*----------------------------------------------------------------------------------------*/

    /**
     * Initialise l'utilisation des beacons, j'ai limité la Region au beacon de chez
     * Kontakt avec un Uuid F7826DA6-4FA2-4E98-8024-BC5B71E0893E.
     * Et c'est içi que je définie ce qu'il faut faire chaque fois que je détécte des beacons
     */
    private void initBeacon() {
        beaconManager = BeaconManager.newInstance(this);
        // limite le UUID
        beaconManager.addFilter(Filters.newProximityUUIDFilter(
                UUID.fromString("F7826DA6-4FA2-4E98-8024-BC5B71E0893E")));
        // trie la liste de beacons par ordre croisant sur la distance
        beaconManager.setDistanceSort(BeaconDevice.DistanceSort.ASC);
        // implement la méthode qui vas être appelé chaque fois que des beacons
        // sont trouvé
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<BeaconDevice> beaconDevices) {
                // si il y'a au moins un beacon trouvé..
                if (beaconDevices.size() >0 && enCoursDeScan) {
                    boolean A = false ,B= false ,C= false;

                    for (BeaconDevice bd : beaconDevices) {
                        if (bd.getBeaconUniqueId().equals("xdQW")){
                            trilateration.setDistanceBeaconA(bd.getAccuracy()*100);
                            A = true;
                        }
                        if (bd.getBeaconUniqueId().equals("TOyZ")){
                            trilateration.setDistanceBeaconB(bd.getAccuracy()*100);
                            B = true;
                        }
                        if (bd.getBeaconUniqueId().equals("WMkW")){
                            trilateration.setDistanceBeaconC(bd.getAccuracy()*100);
                            C = true;
                        }
                    }
                    if (A && B && C) {
                        Point pGsm = trilateration.getPositionGsm();
                        afficherPlan(pGsm);
                        Log.d(TAG_DEBUG,"X "+pGsm.x+" Y "+pGsm.y);
                    }
                    /*
                    A = B = C = true;
                    trilateration.setDistanceBeaconA(424);
                    trilateration.setDistanceBeaconB(368);
                    trilateration.setDistanceBeaconC(736);
                    if (A && B && C) {
                        Point pGsm = trilateration.getPositionGsm();
                        afficherPlan(pGsm);
                        Log.d(TAG_DEBUG,"X "+pGsm.x+" Y "+pGsm.y);
                    }
                    */

                }
            }
        });
    }

    /**
     * Initialisation des boutons et autre composant de la fenetre principale.
     * Ici il y'en à aucun
     */
    private void initElements() {
        //getSupportActionBar().setBackgroundDrawable(
        //        new ColorDrawable(getResources().getColor(R.color.Orange)));
        trilateration = new Trilateration(new Point(0,0),new Point(875,1000),new Point(1750,0));
    }

    /**
     * Démarre le scan des beacons.
     */
    private void startScan() {
        try {
            beaconManager.startRanging();
            enCoursDeScan = true;
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,"Erreur de démarrage Scan");
        }
    }

    /**
     * Stop le scan des beacons.
     */
    private void stopScan() {
        beaconManager.stopRanging();
        enCoursDeScan = false;
    }
}
