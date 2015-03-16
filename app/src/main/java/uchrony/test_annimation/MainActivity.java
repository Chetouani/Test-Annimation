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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.factory.Filters;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.ArrayList;
import java.util.HashSet;
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
    private HashSet<Region> listeRegion;
    // Permet de savoir si le scan est en cours ou alors arréter
    private boolean enCoursDeScan = false;
    private List<Double> distancesX = new ArrayList<>();
    private List<Double> distancesW = new ArrayList<>();
    private List<Double> distancesT = new ArrayList<>();
    private PlanPiece planPiece;

    TextView x,t,w,coin;
    ImageView drawingImageView;
    Button bt;
    Canvas canvas;
    long i=1;

    private Trilateration trilateration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initElements();
        afficherPlan(null);
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

        canvas.drawRect(planPiece.getCoinSupGauche().x
                        ,planPiece.getCoinSupGauche().y
                        ,planPiece.getCoinInfDroite().x
                        ,planPiece.getCoinInfDroite().y
                        ,rectanglePiece);

        canvas.drawLine(planPiece.getCentreDeLaPiece().x
                        ,planPiece.getCoinSupGauche().y
                        ,planPiece.getCentreDeLaPiece().x
                        ,planPiece.getCoinInfDroite().y,rectanglePiece);
        canvas.drawLine(planPiece.getCoinSupGauche().x
                ,planPiece.getCentreDeLaPiece().y
                ,planPiece.getCoinInfDroite().x
                ,planPiece.getCentreDeLaPiece().y,rectanglePiece);

        Paint pointBeacon = new Paint();
        pointBeacon.setColor(Color.GREEN);
        pointBeacon.setStrokeWidth(50);

        canvas.drawCircle( planPiece.getPositionBeaconXDQW().x
                        ,  planPiece.getPositionBeaconXDQW().y, 30, pointBeacon);
        canvas.drawCircle( planPiece.getPositionBeaconT0YZ().x
                        ,  planPiece.getPositionBeaconT0YZ().y, 30, pointBeacon);
        canvas.drawCircle( planPiece.getPositionBeaconWMKW().x
                        ,  planPiece.getPositionBeaconWMKW().y, 30, pointBeacon);

        if (position != null) {
            Paint pointRouge = new Paint();
            pointRouge.setColor(Color.RED);
            pointRouge.setStrokeWidth(50);

            canvas.drawCircle( position.x, position.y, 30, pointRouge);
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
        //beaconManager.setForceScanConfiguration(new ForceScanConfiguration(5000, 5000));
        beaconManager.setScanMode(BeaconManager.SCAN_MODE_LOW_LATENCY);

        // implement la méthode qui vas être appelé chaque fois que des beacons
        // sont trouvé
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<BeaconDevice> beaconDevices) {
                // si il y'a au moins un beacon trouvé..
                runOnUiThread(new Runnable() {
                    public void run() {

                        if (distancesX.size() > 10) {
                            distancesX.clear();
                            distancesW.clear();
                            distancesT.clear();
                        }

                        if (beaconDevices.size() >0 && enCoursDeScan) {
                            boolean A = false ,B= false ,C= false;
                            for (BeaconDevice bd : beaconDevices) {
                                if (bd.getBeaconUniqueId().equals("xdQW")){
                                    distancesX.add(bd.getAccuracy()*100);
                                    double total = 0;
                                    for (Double note : distancesX) {
                                        total += note;
                                    }
                                    double moyenne = total / distancesX.size();
                                    trilateration.setDistanceBeaconXDQW(moyenne);
                                    x.setText("xdQW = "+bd.getAccuracy()*100+"\n");
                                }
                                if (bd.getBeaconUniqueId().equals("TOyZ")){
                                    distancesT.add(bd.getAccuracy()*100);
                                    double total = 0;
                                    for (Double note : distancesT) {
                                        total += note;
                                    }
                                    double moyenne = total / distancesT.size();
                                    trilateration.setDistanceBeaconTOYZ(moyenne);
                                    t.setText("TOyZ = "+bd.getAccuracy()*100+"\n");
                                }
                                if (bd.getBeaconUniqueId().equals("WMkW")){
                                    distancesW.add(bd.getAccuracy()*100);
                                    double total = 0;
                                    for (Double note : distancesW) {
                                        total += note;
                                    }
                                    double moyenne = total / distancesW.size();
                                    trilateration.setDistanceBeaconWMKW(moyenne);
                                    w.setText("WMkW = "+bd.getAccuracy()*100+"\n");
                                }
                            }
                            Point pGsm = trilateration.getPositionGsm();
                            afficherPlan(pGsm);
                            coin.setText("X " + pGsm.x + " Y " + pGsm.y + "\n");
                            switch (planPiece.getPositionCoin(pGsm)) {
                                case COIN_BAS_DROITE:  coin.append("COIN_BAS_DROITE"); break;
                                case COIN_BAS_GAUCHE:  coin.append("COIN_BAS_GAUCHE"); break;
                                case COIN_HAUT_DROITE: coin.append("COIN_HAUT_DROITE"); break;
                                case COIN_HAUT_GAUCHE: coin.append("COIN_HAUT_GAUCHE"); break;
                            }
                            coin.append(""+beaconDevices.size());
                            Log.d(TAG_DEBUG,"X "+pGsm.x+" Y "+pGsm.y);
                        }

                    /*A = B = C = true;
                    trilateration.setDistanceBeaconXDQW(477);
                    trilateration.setDistanceBeaconTOYZ(250);
                    trilateration.setDistanceBeaconWMKW(691);
                    if (A && B && C) {
                        Point pGsm = trilateration.getPositionGsm();
                        afficherPlan(pGsm);
                        Log.d(TAG_DEBUG,"X "+pGsm.x+" Y "+pGsm.y);
                    }*/
                    }
                });
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
        planPiece = new PlanPiece(new Point(0,0), new Point(500,477));
        planPiece.setPositionBeaconXDQW(new Point(0,0));
        planPiece.setPositionBeaconT0YZ(new Point(250,477));
        planPiece.setPositionBeaconWMKW(new Point(500,0));

        trilateration = new Trilateration(planPiece.getPositionBeaconXDQW()
                                            ,planPiece.getPositionBeaconT0YZ()
                                            ,planPiece.getPositionBeaconWMKW());
        w = (TextView) findViewById(R.id.w);
        x = (TextView) findViewById(R.id.x);
        t = (TextView) findViewById(R.id.t);
        coin = (TextView) findViewById(R.id.coin);

        bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enCoursDeScan)
                    stopScan();
                else
                    startScan();
            }
        });

        listeRegion = new HashSet<>();
        listeRegion.add(new Region(UUID.fromString("F7826DA6-4FA2-4E98-8024-BC5B71E0893E")
                        ,13714,13269));
        listeRegion.add(new Region(UUID.fromString("F7826DA6-4FA2-4E98-8024-BC5B71E0893E")
                        ,14641,60464));
        listeRegion.add(new Region(UUID.fromString("F7826DA6-4FA2-4E98-8024-BC5B71E0893E")
                        ,62654,62892));
    }

    /**
     * Démarre le scan des beacons.
     */
    private void startScan() {
        try {
            beaconManager.startRanging(listeRegion);
            enCoursDeScan = true;
            Log.d(TAG_DEBUG,"Start Scan");
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
        Log.d(TAG_DEBUG,"Stop Scan");
    }
}
