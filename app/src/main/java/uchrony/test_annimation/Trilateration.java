package uchrony.test_annimation;

import android.graphics.Point;

/**
 * Crée par Abdel le 9/03/15.
 */
public class Trilateration {

    private double distanceBeaconA;
    private double distanceBeaconB;
    private double distanceBeaconC;

    private Point positionBeaconA;
    private Point positionBeaconB;
    private Point positionBeaconC;

    public Trilateration(Point positionBeaconA, Point positionBeaconB, Point positionBeaconC) {
        this.positionBeaconA = positionBeaconA;
        this.positionBeaconB = positionBeaconB;
        this.positionBeaconC = positionBeaconC;

        this.distanceBeaconA = 0;
        this.distanceBeaconB = 0;
        this.distanceBeaconC = 0;
    }

    public void setDistanceBeaconXDQW(double distanceBeaconA) {
        this.distanceBeaconA = distanceBeaconA;
    }

    public void setDistanceBeaconTOYZ(double distanceBeaconB) {
        this.distanceBeaconB = distanceBeaconB;
    }

    public void setDistanceBeaconWMKW(double distanceBeaconC) {
        this.distanceBeaconC = distanceBeaconC;
    }

    public Point getPositionGsm(){
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = distanceBeaconA * distanceBeaconA - distanceBeaconB * distanceBeaconB - positionBeaconA.x * positionBeaconA.x - positionBeaconA.y * positionBeaconA.y + positionBeaconB.x * positionBeaconB.x + positionBeaconB.y * positionBeaconB.y;
        Z = distanceBeaconB * distanceBeaconB - distanceBeaconC * distanceBeaconC - positionBeaconB.x * positionBeaconB.x - positionBeaconB.y * positionBeaconB.y + positionBeaconC.x * positionBeaconC.x + positionBeaconC.y * positionBeaconC.y;

        foundBeaconLat = (W * (positionBeaconC.y - positionBeaconB.y) - Z * (positionBeaconB.y - positionBeaconA.y)) / (2 * ((positionBeaconB.x - positionBeaconA.x) * (positionBeaconC.y - positionBeaconB.y) - (positionBeaconC.x - positionBeaconB.x) * (positionBeaconB.y - positionBeaconA.y)));
        foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconB.x - positionBeaconA.x)) / (2 * (positionBeaconB.y - positionBeaconA.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x)) / (2 * (positionBeaconC.y - positionBeaconB.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;

        Point positionGsm = new Point((int)foundBeaconLat,(int)foundBeaconLong);
        return positionGsm;
    }
}
