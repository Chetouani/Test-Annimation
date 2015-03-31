package uchrony.test_annimation;

import android.graphics.Point;

/**
 * Created by abdel on 23/03/15.
 */
public class PlanPieceQuatre {

    public static enum Coin{COIN_HAUT_GAUCHE,COIN_HAUT_DROITE
        ,COIN_BAS_GAUCHE,COIN_BAS_DROITE};

    private Point coinA;
    private Point coinB;
    private Point coinC;
    private Point coinD;
    private Point centreDeLaPiece;

    //
    private Point positionBeaconA;
    private Point positionBeaconB;
    private Point positionBeaconC;
    private Point positionBeaconD;

    public PlanPieceQuatre(Point coinSupGauche, Point coinInfDroite) {
        this.coinA = coinSupGauche;
        this.coinD = coinInfDroite;
        this.coinB = new Point(coinA.x,coinD.y);
        this.coinC = new Point(coinD.x,coinA.y);


        this.centreDeLaPiece = new Point((int) coinInfDroite.x/2
                ,(int) coinInfDroite.y/2);
    }

    public Coin getPositionCoin(Point positon){
        if ( positon.x <= centreDeLaPiece.x ) {
            if ( positon.y <= centreDeLaPiece.y ) {
                return Coin.COIN_HAUT_GAUCHE;
            } else {
                return Coin.COIN_BAS_GAUCHE;
            }
        } else {
            if ( positon.y <= centreDeLaPiece.y ) {
                return Coin.COIN_HAUT_DROITE;
            } else {
                return Coin.COIN_BAS_DROITE;
            }
        }

    }

    public Point getPositionBeaconA() {
        return positionBeaconA;
    }

    public void setPositionBeaconA(Point positionBeaconA) {
        this.positionBeaconA = positionBeaconA;
    }

    public Point getPositionBeaconB() {
        return positionBeaconB;
    }

    public void setPositionBeaconB(Point positionBeaconB) {
        this.positionBeaconB = positionBeaconB;
    }

    public Point getPositionBeaconC() {
        return positionBeaconC;
    }

    public void setPositionBeaconC(Point positionBeaconC) {
        this.positionBeaconC = positionBeaconC;
    }

    public Point getPositionBeaconD() {
        return positionBeaconD;
    }

    public void setPositionBeaconD(Point positionBeaconD) {
        this.positionBeaconD = positionBeaconD;
    }

    public Point getCentreDeLaPiece() {
        return centreDeLaPiece;
    }

    public void setCentreDeLaPiece(Point centreDeLaPiece) {
        this.centreDeLaPiece = centreDeLaPiece;
    }
}
