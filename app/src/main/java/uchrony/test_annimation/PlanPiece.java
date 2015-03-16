package uchrony.test_annimation;

import android.graphics.Point;

/**
 * Crée par Abdel le 11/03/15.
 */
public class PlanPiece {

    public static enum Coin{COIN_HAUT_GAUCHE,COIN_HAUT_DROITE
                            ,COIN_BAS_GAUCHE,COIN_BAS_DROITE};

    private Point coinSupGauche;
    private Point coinInfDroite;
    private Point centreDeLaPiece;

    //
    private Point positionBeaconXDQW;
    private Point positionBeaconT0YZ;
    private Point positionBeaconWMKW;

    public PlanPiece(Point coinSupGauche, Point coinInfDroite) {
        this.coinSupGauche = coinSupGauche;
        this.coinInfDroite = coinInfDroite;

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

    public Point getCentreDeLaPiece() {
        return centreDeLaPiece;
    }

    public Point getCoinSupGauche() {
        return coinSupGauche;
    }

    public Point getCoinInfDroite() {
        return coinInfDroite;
    }

    public void setPositionBeaconWMKW(Point positionBeaconWMKW) {
        this.positionBeaconWMKW = positionBeaconWMKW;
    }

    public void setPositionBeaconT0YZ(Point positionBeaconT0YZ) {
        this.positionBeaconT0YZ = positionBeaconT0YZ;
    }

    public void setPositionBeaconXDQW(Point positionBeaconXDQW) {
        this.positionBeaconXDQW = positionBeaconXDQW;
    }

    public Point getPositionBeaconWMKW() {
        return positionBeaconWMKW;
    }

    public Point getPositionBeaconT0YZ() {
        return positionBeaconT0YZ;
    }

    public Point getPositionBeaconXDQW() {
        return positionBeaconXDQW;
    }
}
