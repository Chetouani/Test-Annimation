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
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.os.Handler;


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
    List<Point> listePosition;
    private PlanPiece planPiece;
    private PlanPieceQuatre planPieceQuatre;
    public TextView x,t,w,coin,z;
    EditText var1,var2;
    ImageView drawingImageView;
    Button bt,valider;
    Canvas canvas;
    long i=1;
    String textMessage="";


    private double VAR1 = 0.89976;
    private double VAR2 = 7.7095;
    private static int TAILLE_LISTE = 0;
    private Trilateration trilateration;
    private QuadriLateration quadriLateration;

    long lastUpdate=-1;
    long curtime=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initElements();
        afficherPlan(null,null);
        initBeacon();
        verificationBluetooth();
    }

    private void afficherPlan(Point position,PlanPieceQuatre.Coin coin) {
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

        canvas.drawRect(planPieceQuatre.getPositionBeaconA().x
                        ,planPieceQuatre.getPositionBeaconA().y
                        ,planPieceQuatre.getPositionBeaconD().x
                        ,planPieceQuatre.getPositionBeaconD().y
                        ,rectanglePiece);

        canvas.drawLine(planPieceQuatre.getCentreDeLaPiece().x
                , planPieceQuatre.getPositionBeaconA().y
                , planPieceQuatre.getCentreDeLaPiece().x
                , planPieceQuatre.getPositionBeaconD().y, rectanglePiece);
        canvas.drawLine(planPieceQuatre.getPositionBeaconA().x
                ,planPieceQuatre.getCentreDeLaPiece().y
                ,planPieceQuatre.getPositionBeaconD().x
                ,planPieceQuatre.getCentreDeLaPiece().y,rectanglePiece);

        Paint pointBeacon = new Paint();
        pointBeacon.setColor(Color.GREEN);
        pointBeacon.setStrokeWidth(50);

        canvas.drawCircle(planPieceQuatre.getPositionBeaconA().x
                         , planPieceQuatre.getPositionBeaconA().y, 30, pointBeacon);
        canvas.drawCircle( planPieceQuatre.getPositionBeaconB().x
                        ,  planPieceQuatre.getPositionBeaconB().y, 30, pointBeacon);
        canvas.drawCircle( planPieceQuatre.getPositionBeaconC().x
                        ,  planPieceQuatre.getPositionBeaconC().y, 30, pointBeacon);
        canvas.drawCircle( planPieceQuatre.getPositionBeaconD().x
                        ,  planPieceQuatre.getPositionBeaconD().y, 30, pointBeacon);

        if (position != null) {
            // Rectangle
            Paint rectangleCoin = new Paint();
            rectangleCoin.setColor(Color.RED);
            rectangleCoin.setStyle(Paint.Style.FILL);
            rectangleCoin.setStrokeWidth(10);

            Paint pointRouge = new Paint();
            pointRouge.setColor(Color.BLUE);
            pointRouge.setStrokeWidth(10);

            for (Point p : listePosition)
                canvas.drawCircle(p.x, p.y, 10, pointRouge);

            /*if (coin == PlanPiece.Coin.COIN_HAUT_GAUCHE) {
                canvas.drawRect(planPiece.getCoinSupGauche().x
                        , planPiece.getCoinSupGauche().y
                        , planPiece.getCentreDeLaPiece().x
                        , planPiece.getCentreDeLaPiece().y
                        , rectangleCoin);
            }
            if (coin == PlanPiece.Coin.COIN_HAUT_DROITE) {
                canvas.drawRect(planPiece.getCentreDeLaPiece().x
                        , planPiece.getCoinSupGauche().y
                        , planPiece.getCoinInfDroite().x
                        , planPiece.getCentreDeLaPiece().y
                        , rectangleCoin);
            }
            if (coin == PlanPiece.Coin.COIN_BAS_GAUCHE) {
                canvas.drawRect(planPiece.getCoinSupGauche().x
                        , planPiece.getCentreDeLaPiece().y
                        , planPiece.getCentreDeLaPiece().x
                        , planPiece.getCoinInfDroite().y
                        , rectangleCoin);
            }
            if (coin == PlanPiece.Coin.COIN_BAS_DROITE) {
                canvas.drawRect(planPiece.getCentreDeLaPiece().x
                        , planPiece.getCentreDeLaPiece().y
                        , planPiece.getCoinInfDroite().x
                        , planPiece.getCoinInfDroite().y
                        , rectangleCoin);
            }

            } else {
                pointRouge.setColor(Color.RED);
                pointRouge.setStrokeWidth(50);
                canvas.drawCircle(0,0, 30, pointRouge);
            }*/
        }
    }


    @Override
    protected void onDestroy() {
       // sendEmail();
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
        //beaconManager.setDistanceSort(BeaconDevice.DistanceSort.ASC);
        beaconManager.setScanMode(BeaconManager.SCAN_MODE_LOW_LATENCY);

       // beaconManager.setForceScanConfiguration(new ForceScanConfiguration(1000*60*2,1000));

        // implement la méthode qui vas être appelé chaque fois que des beacons
        // sont trouvé
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<BeaconDevice> beaconDevices) {
                // si il y'a au moins un beacon trouvé..
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (beaconDevices.size() >= 4 && enCoursDeScan) {
                            for (BeaconDevice bd : beaconDevices) {
                                if (bd.getBeaconUniqueId().equals("TOyZ") && bd.getAccuracy()*100 >1){
                                    //trilateration.setDistanceBeaconXDQW(bd.getAccuracy() * 100);
                                    quadriLateration.setDistanceBeaconA(bd.getAccuracy()*100);
                                    x.setText("A " + bd.getAccuracy() * 100 + "\n");
                                }
                                if (bd.getBeaconUniqueId().equals("xdQW") && bd.getAccuracy()*100 >1){
                                    //trilateration.setDistanceBeaconTOYZ(bd.getAccuracy() * 100);
                                    quadriLateration.setDistanceBeaconB(bd.getAccuracy() * 100);
                                    t.setText("B " + bd.getAccuracy() * 100 + "\n");
                                }
                                if (bd.getBeaconUniqueId().equals("WMkW") && bd.getAccuracy()*100 >1){
                                    //trilateration.setDistanceBeaconWMKW(bd.getAccuracy() * 100);
                                    quadriLateration.setDistanceBeaconC(bd.getAccuracy() * 100);
                                    w.setText("C " + bd.getAccuracy() * 100 + "\n");
                                }
                                if (bd.getBeaconUniqueId().equals("4mQm") && bd.getAccuracy()*100 >1){
                                    //trilateration.setDistanceBeaconWMKW(bd.getAccuracy() * 100);
                                    quadriLateration.setDistanceBeaconD(bd.getAccuracy() * 100);
                                    z.setText("D " + bd.getAccuracy() * 100 + "\n");
                                }
                            }
                        }
                   }
                });
            }
        });
    }

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();




    private void calculerPoint() {
        /*if (distancesX.size() > TAILLE_LISTE && distancesT.size() > TAILLE_LISTE && distancesW.size() > TAILLE_LISTE ) {
            double totalX = 0;
            for (Double note : distancesX) {
                totalX += note;
            }
            double moyenneX = totalX / distancesX.size();
            trilateration.setDistanceBeaconXDQW(moyenneX);

            double totalT = 0;
            for (Double note : distancesT) {
                totalT += note;
            }
            double moyenneT = totalT / distancesT.size();
            trilateration.setDistanceBeaconTOYZ(moyenneT);

            double totalW = 0;
            for (Double note : distancesW) {
                totalW += note;
            }
            double moyenneW = totalW / distancesW.size();
            trilateration.setDistanceBeaconWMKW(moyenneW);

            Point pGsm = trilateration.getPositionGsm();

            x.setText("xdQW = " + moyenneX + "\n");
            textMessage +="xdQW = "+moyenneX+"\n";
            t.setText("TOYZ = " + moyenneT + "\n");
            textMessage +="TOYZ = "+moyenneT+"\n";
            w.setText("WMKW = " + moyenneW + "\n");
            textMessage +="WMKW = "+moyenneW+"\n";
            if (pGsm != null) {
                coin.setText("X " + pGsm.x + " Y " + pGsm.y + "\n");
                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();
                textMessage += today.format("%k:%M:%S") + "\n";
                textMessage += "X " + pGsm.x + " Y " + pGsm.y + "\n\n";
                switch (planPiece.getPositionCoin(pGsm)) {
                    case COIN_BAS_DROITE:
                        afficherPlan(pGsm, PlanPiece.Coin.COIN_BAS_DROITE);
                        break;
                    case COIN_BAS_GAUCHE:
                        afficherPlan(pGsm, PlanPiece.Coin.COIN_BAS_GAUCHE);
                        break;
                    case COIN_HAUT_DROITE:
                        afficherPlan(pGsm, PlanPiece.Coin.COIN_HAUT_DROITE);
                        break;
                    case COIN_HAUT_GAUCHE:
                        afficherPlan(pGsm, PlanPiece.Coin.COIN_HAUT_GAUCHE);
                        break;
                }
                Log.d(TAG_DEBUG, "X " + pGsm.x + " Y " + pGsm.y);
            } else {
                coin.setText("X = null Y = null\n");
                textMessage += "X = null Y = null\n\n";
            }

            distancesX.clear();
            distancesW.clear();
            distancesT.clear();
        }*/

        //Point pGsm = trilateration.getPositionGsm();
        Point pGsm = quadriLateration.getPositionGsm();

        if (pGsm != null) {
            /*if (pGsm.x < planPiece.getCoinSupGauche().x)
                pGsm.x = Math.abs(pGsm.x);
            if (pGsm.y < planPiece.getCoinSupGauche().y)
                pGsm.y = Math.abs(pGsm.y);
            if (pGsm.x > planPiece.getCoinInfDroite().x)
                pGsm.x = planPiece.getCoinInfDroite().x - (pGsm.x - planPiece.getCoinInfDroite().x);
            if (pGsm.y > planPiece.getCoinInfDroite().y)
                pGsm.y = planPiece.getCoinInfDroite().y - (pGsm.y - planPiece.getCoinInfDroite().y);*/

            coin.setText("X " + pGsm.x + " Y " + pGsm.y + "\n");
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            textMessage += today.format("%k:%M:%S") + "\n";
            textMessage += "X " + pGsm.x + " Y " + pGsm.y + "\n\n";
            listePosition.add(pGsm);
            switch (planPieceQuatre.getPositionCoin(pGsm)) {
                case COIN_BAS_DROITE:
                    afficherPlan(pGsm, PlanPieceQuatre.Coin.COIN_BAS_DROITE);
                    break;
                case COIN_BAS_GAUCHE:
                    afficherPlan(pGsm, PlanPieceQuatre.Coin.COIN_BAS_GAUCHE);
                    break;
                case COIN_HAUT_DROITE:
                    afficherPlan(pGsm, PlanPieceQuatre.Coin.COIN_HAUT_DROITE);
                    break;
                case COIN_HAUT_GAUCHE:
                    afficherPlan(pGsm, PlanPieceQuatre.Coin.COIN_HAUT_GAUCHE);
                    break;
            }
            Log.d(TAG_DEBUG, "X " + pGsm.x + " Y " + pGsm.y);
        } else {
            coin.setText("X = null Y = null\n");
            textMessage += "X = null Y = null\n\n";
        }
    }

    /**
     * Initialisation des boutons et autre composant de la fenetre principale.
     * Ici il y'en à aucun
     */
    private void initElements() {
        //getSupportActionBar().setBackgroundDrawable(
        //        new ColorDrawable(getResources().getColor(R.color.Orange)));
        planPieceQuatre = new PlanPieceQuatre(new Point(0,0), new Point(500,477));
        planPieceQuatre.setPositionBeaconA(new Point(0, 0));
        planPieceQuatre.setPositionBeaconB(new Point(0, 477));
        planPieceQuatre.setPositionBeaconC(new Point(500, 0));
        planPieceQuatre.setPositionBeaconD(new Point(500, 477));

        /*trilateration = new Trilateration(planPiece.getPositionBeaconXDQW()
                                            ,planPiece.getPositionBeaconT0YZ()
                                            ,planPiece.getPositionBeaconWMKW());*/
        quadriLateration = new QuadriLateration(planPieceQuatre.getPositionBeaconA(),planPieceQuatre.getPositionBeaconB()
                                                ,planPieceQuatre.getPositionBeaconC(),planPieceQuatre.getPositionBeaconD());
        w = (TextView) findViewById(R.id.w);
        x = (TextView) findViewById(R.id.x);
        t = (TextView) findViewById(R.id.t);
        z = (TextView) findViewById(R.id.z);
        coin = (TextView) findViewById(R.id.coin);

        var1 = (EditText) findViewById(R.id.var1);
        var2 = (EditText) findViewById(R.id.var2);
        valider = (Button) findViewById(R.id.valider);
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VAR1 =  Double.parseDouble(var1.getText().toString());
                //VAR2 =  Double.parseDouble(var2.getText().toString());
                TAILLE_LISTE = Integer.parseInt(var1.getText().toString());
            }
        });
        var1.setText(String.valueOf(VAR1));
        var2.setText(String.valueOf(VAR2));

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
        listePosition = new ArrayList<>();
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
            beaconManager.startRanging();
            enCoursDeScan = true;
            startTimer();
            Log.d(TAG_DEBUG,"Start Scan");
        } catch (RemoteException e) {
            Log.d(TAG_DEBUG,"Erreur de démarrage Scan");
        }
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 100, 200); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Stop le scan des beacons.
     */
    private void stopScan() {
        beaconManager.stopRanging();
        enCoursDeScan = false;
        stoptimertask();
        Log.d(TAG_DEBUG,"Stop Scan");
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //show the toast
                        calculerPoint();
                    }
                });
            }
        };
    }
    char b ='a';
    private double getDistance(int txPower, double rssi) {
        double ratio = rssi * 1.0 /txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        } else {
            return ((VAR1) * Math.pow(ratio,VAR2) + 0.111)*100;
        }
    }

    public void sendEmail() {
        String to = "abdel@uchrony.be";
        String subject = "Info trilateration";
        String message = textMessage;
        //String toCc = "email de destinataire en CC";
        //String toCci = "email de destinataire en CCi";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        //email.putExtra(Intent.EXTRA_CC, new String[]{ toCc});
        //email.putExtra(Intent.EXTRA_BCC, new String[]{toCci});
        //email.putExtra(Intent.EXTRA_STREAM, "file:///sdcard/file.pdf");
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choisissez un client de messagerie:"));
    }
}
