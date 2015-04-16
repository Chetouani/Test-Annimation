package uchrony.test_annimation;

import android.graphics.Point;

/**
 * Created by abdel on 23/03/15.
 */
public class QuadriLateration {


    private double distanceBeaconA;
    private double distanceBeaconB;
    private double distanceBeaconC;
    private double distanceBeaconD;

    private Point positionBeaconA;
    private Point positionBeaconB;
    private Point positionBeaconC;
    private Point positionBeaconD;

    public QuadriLateration(Point positionBeaconA, Point positionBeaconB, Point positionBeaconC, Point positionBeaconD) {
        this.positionBeaconA = positionBeaconA;
        this.positionBeaconB = positionBeaconB;
        this.positionBeaconC = positionBeaconC;
        this.positionBeaconD = positionBeaconD;

        this.distanceBeaconA = -1;
        this.distanceBeaconB = -1;
        this.distanceBeaconC = -1;
        this.distanceBeaconD = -1;
    }

    public void setDistanceBeaconA(double distanceBeaconA) {
        this.distanceBeaconA = distanceBeaconA;
    }

    public void setDistanceBeaconB(double distanceBeaconB) {
        this.distanceBeaconB = distanceBeaconB;
    }

    public void setDistanceBeaconC(double distanceBeaconC) {
        this.distanceBeaconC = distanceBeaconC;
    }

    public void setDistanceBeaconD(double distanceBeaconD) {
        this.distanceBeaconD = distanceBeaconD;
    }

    public double getDistanceBeaconA() {
        return distanceBeaconA;
    }

    public double getDistanceBeaconB() {
        return distanceBeaconB;
    }

    public double getDistanceBeaconC() {
        return distanceBeaconC;
    }

    public double getDistanceBeaconD() {
        return distanceBeaconD;
    }

    public Point getPositionGsm(){
        Point posiBeaconProche = getPositionBeaconProche();
        if (posiBeaconProche != null) {
            if (posiBeaconProche.equals(positionBeaconA.x,positionBeaconA.y)) {
                estPositionPossibleABC(trilaterationABC());
                return  trilaterationABC();
            }
            if (posiBeaconProche.equals(positionBeaconB.x,positionBeaconB.y)) {
                estPositionPossibleBAD(trilaterationBAD());
                return trilaterationBAD();
            }
            if (posiBeaconProche.equals(positionBeaconC.x,positionBeaconC.y)) {
                estPositionPossibleCAD(trilaterationCDA());
                return trilaterationCDA();
            }
            if (posiBeaconProche.equals(positionBeaconD.x,positionBeaconD.y)) {
                estPositionPossibleBCD(trilaterationBCD());
                return trilaterationBCD();
            }
        }
        return null;
    }

    private Point trilaterationBCD() {
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = Math.pow(distanceBeaconB,2) - Math.pow(distanceBeaconC,2) - Math.pow(positionBeaconB.x,2)
                - Math.pow(positionBeaconB.y,2) + Math.pow(positionBeaconC.x,2)
                + Math.pow(positionBeaconC.y,2);
        Z = Math.pow(distanceBeaconC,2) - Math.pow(distanceBeaconD,2) - Math.pow(positionBeaconC.x,2)
                - Math.pow(positionBeaconC.y,2) + Math.pow(positionBeaconD.x,2)
                + Math.pow(positionBeaconD.y,2);

        foundBeaconLat
                = (W * (positionBeaconD.y - positionBeaconC.y) - Z * (positionBeaconC.y - positionBeaconB.y))
                / (2 * ((positionBeaconC.x - positionBeaconB.x) * (positionBeaconD.y - positionBeaconC.y)
                - (positionBeaconD.x - positionBeaconC.x) * (positionBeaconC.y - positionBeaconB.y)));
        foundBeaconLong
                = (W - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x))
                / (2 * (positionBeaconC.y - positionBeaconB.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter
                = (Z - 2 * foundBeaconLat * (positionBeaconD.x - positionBeaconC.x))
                / (2 * (positionBeaconD.y - positionBeaconC.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;
        return new Point((int)foundBeaconLat,(int)foundBeaconLong);
    }

    private Point trilaterationCDA() { // a-c  c-d  d-a
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = Math.pow(distanceBeaconC,2) - Math.pow(distanceBeaconD,2) - Math.pow(positionBeaconC.x,2)
                - Math.pow(positionBeaconC.y,2) + Math.pow(positionBeaconD.x,2)
                + Math.pow(positionBeaconD.y,2);
        Z = Math.pow(distanceBeaconD,2) - Math.pow(distanceBeaconA,2) - Math.pow(positionBeaconD.x,2)
                - Math.pow(positionBeaconD.y,2) + Math.pow(positionBeaconA.x,2)
                + Math.pow(positionBeaconA.y,2);

        foundBeaconLat = (W * (positionBeaconA.y - positionBeaconD.y) - Z * (positionBeaconD.y - positionBeaconC.y))
                / (2 * ((positionBeaconD.x - positionBeaconC.x) * (positionBeaconA.y - positionBeaconD.y)
                - (positionBeaconA.x - positionBeaconD.x) * (positionBeaconD.y - positionBeaconC.y)));
        foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconD.x - positionBeaconC.x))
                / (2 * (positionBeaconD.y - positionBeaconC.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconA.x - positionBeaconD.x))
                / (2 * (positionBeaconA.y - positionBeaconD.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;
        return new Point((int)foundBeaconLat,(int)foundBeaconLong);
    }

    private Point trilaterationABC() {
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = Math.pow(distanceBeaconA,2) - Math.pow(distanceBeaconB,2) - Math.pow(positionBeaconA.x,2)
                - Math.pow(positionBeaconA.y,2) + Math.pow(positionBeaconB.x,2)
                + Math.pow(positionBeaconB.y,2);
        Z = Math.pow(distanceBeaconB,2) - Math.pow(distanceBeaconC,2) - Math.pow(positionBeaconB.x,2)
                - Math.pow(positionBeaconB.y,2) + Math.pow(positionBeaconC.x,2)
                + Math.pow(positionBeaconC.y,2);

        foundBeaconLat = (W * (positionBeaconC.y - positionBeaconB.y) - Z * (positionBeaconB.y - positionBeaconA.y))
                / (2 * ((positionBeaconB.x - positionBeaconA.x) * (positionBeaconC.y - positionBeaconB.y)
                - (positionBeaconC.x - positionBeaconB.x) * (positionBeaconB.y - positionBeaconA.y)));
        foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconB.x - positionBeaconA.x))
                / (2 * (positionBeaconB.y - positionBeaconA.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x))
                / (2 * (positionBeaconC.y - positionBeaconB.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;
        return new Point((int)foundBeaconLat,(int)foundBeaconLong);
    }

    private Point trilaterationBAD() {
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = Math.pow(distanceBeaconB,2) - Math.pow(distanceBeaconA,2) - Math.pow(positionBeaconB.x,2)
                - Math.pow(positionBeaconB.y,2) + Math.pow(positionBeaconA.x,2)
                + Math.pow(positionBeaconA.y,2);
        Z = Math.pow(distanceBeaconA,2) - Math.pow(distanceBeaconD,2) - Math.pow(positionBeaconA.x,2)
                - Math.pow(positionBeaconA.y,2) + Math.pow(positionBeaconD.x,2)
                + Math.pow(positionBeaconD.y,2);

        foundBeaconLat = (W * (positionBeaconD.y - positionBeaconA.y) - Z * (positionBeaconA.y - positionBeaconB.y))
                / (2 * ((positionBeaconA.x - positionBeaconB.x) * (positionBeaconD.y - positionBeaconA.y)
                - (positionBeaconD.x - positionBeaconA.x) * (positionBeaconA.y - positionBeaconB.y)));
        foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconA.x - positionBeaconB.x))
                / (2 * (positionBeaconA.y - positionBeaconB.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconD.x - positionBeaconA.x))
                / (2 * (positionBeaconD.y - positionBeaconA.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;
        return new Point((int)foundBeaconLat,(int)foundBeaconLong);
    }

    private Point getPositionBeaconProche() {
        Point posi = null;

        if ( distanceBeaconA != -1 && distanceBeaconB != -1 && distanceBeaconC != -1 && distanceBeaconD != -1) {
            posi = positionBeaconA;
            double min = distanceBeaconA;
            Point listePosi[] = new Point[4];
            double tabDist[] = new double[4];

            tabDist[0] = distanceBeaconA;
            tabDist[1] = distanceBeaconB;
            tabDist[2] = distanceBeaconC;
            tabDist[3] = distanceBeaconD;
            listePosi[0] = positionBeaconA;
            listePosi[1] = positionBeaconB;
            listePosi[2] = positionBeaconC;
            listePosi[3] = positionBeaconD;

            for (int i = 0; i < tabDist.length; i++) {
                if (tabDist[i] < min) {
                    min = tabDist[i];
                    posi = listePosi[i];
                }
            }
        }
        return posi;
    }


    private Point trilaterationAlgo() {
        if ( distanceBeaconA != -1 && distanceBeaconB != -1 && distanceBeaconC != -1 && distanceBeaconD != -1) {

            double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
            W = Math.pow(distanceBeaconA,2) - Math.pow(distanceBeaconB,2) - Math.pow(positionBeaconA.x,2)
                    - Math.pow(positionBeaconA.y,2) + Math.pow(positionBeaconB.x,2)
                    + Math.pow(positionBeaconB.y,2);
            Z = Math.pow(distanceBeaconB,2) - Math.pow(distanceBeaconC,2) - Math.pow(positionBeaconB.x,2)
                    - Math.pow(positionBeaconB.y,2) + Math.pow(positionBeaconC.x,2) + Math.pow(positionBeaconC.y,2);

            foundBeaconLat = (W * (positionBeaconC.y - positionBeaconB.y) - Z * (positionBeaconB.y - positionBeaconA.y))
                    / (2 * ((positionBeaconB.x - positionBeaconA.x) * (positionBeaconC.y - positionBeaconB.y)
                    - (positionBeaconC.x - positionBeaconB.x) * (positionBeaconB.y - positionBeaconA.y)));

            foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconB.x - positionBeaconA.x))
                    / (2 * (positionBeaconB.y - positionBeaconA.y));
            //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
            foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x))
                    / (2 * (positionBeaconC.y - positionBeaconB.y));

            foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;

            Point positionGsm1 = new Point((int)foundBeaconLat,(int)foundBeaconLong);

            W = distanceBeaconB * distanceBeaconB - distanceBeaconC * distanceBeaconC - positionBeaconB.x * positionBeaconB.x - positionBeaconB.y * positionBeaconB.y + positionBeaconC.x * positionBeaconC.x + positionBeaconC.y * positionBeaconC.y;
            Z = distanceBeaconC * distanceBeaconC - distanceBeaconD * distanceBeaconD - positionBeaconC.x * positionBeaconC.x - positionBeaconC.y * positionBeaconC.y + positionBeaconD.x * positionBeaconD.x + positionBeaconD.y * positionBeaconD.y;

            foundBeaconLat = (W * (positionBeaconD.y - positionBeaconC.y) - Z * (positionBeaconC.y - positionBeaconB.y)) / (2 * ((positionBeaconC.x - positionBeaconB.x) * (positionBeaconD.y - positionBeaconC.y) - (positionBeaconD.x - positionBeaconC.x) * (positionBeaconC.y - positionBeaconB.y)));
            foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x)) / (2 * (positionBeaconC.y - positionBeaconB.y));
            //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
            foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconD.x - positionBeaconC.x)) / (2 * (positionBeaconD.y - positionBeaconC.y));

            foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;

            Point positionGsm2 = new Point((int)foundBeaconLat,(int)foundBeaconLong);

            return new Point((positionGsm1.x+positionGsm2.x)/2,(positionGsm1.y+positionGsm2.y)/2);
        } else {
            return null;
        }
    }

    private void estPositionPossibleABC(Point pGsm) {
        if (pGsm != null) {
            double dA1 = pGsm.x - positionBeaconA.x;
            double dA2 = pGsm.y - positionBeaconA.y;
            dA1 = Math.abs(dA1);
            dA2 = Math.abs(dA2);
            double distA = Math.sqrt(Math.pow(dA1, 2) + Math.pow(dA2, 2));

            double dB1 = pGsm.x - positionBeaconB.x;
            double dB2 = pGsm.y - positionBeaconB.y;
            dB1 = Math.abs(dB1);
            dB2 = Math.abs(dB2);
            double distB = Math.sqrt(Math.pow(dB1, 2) + Math.pow(dB2, 2));

            double dC1 = pGsm.x - positionBeaconC.x;
            double dC2 = pGsm.y - positionBeaconC.y;
            dC1 = Math.abs(dC1);
            dC2 = Math.abs(dC2);
            double distC = Math.sqrt(Math.pow(dC1, 2) + Math.pow(dC2, 2));

            double MaxDistRecu = distanceBeaconA + distanceBeaconB + distanceBeaconC;
            double MaxDistCalculer = distA + distB + distC;

            double ratioDistRecu = MaxDistRecu / 100;
            double pctA = distA / ratioDistRecu;
            double pctB = distB / ratioDistRecu;
            double pctC = distC / ratioDistRecu;

            double ratioDistCalculer = MaxDistCalculer / 100;
            distanceBeaconA = pctA * ratioDistCalculer;
            distanceBeaconB = pctB * ratioDistCalculer;
            distanceBeaconC = pctC * ratioDistCalculer;
        } else {
            distanceBeaconA = -1;
            distanceBeaconB = -1;
            distanceBeaconC = -1;
        }
    }

    private void estPositionPossibleBAD(Point pGsm) {
        if (pGsm != null) {
            double dA1 = pGsm.x - positionBeaconB.x;
            double dA2 = pGsm.y - positionBeaconB.y;
            dA1 = Math.abs(dA1);
            dA2 = Math.abs(dA2);
            double distA = Math.sqrt(Math.pow(dA1, 2) + Math.pow(dA2, 2));

            double dB1 = pGsm.x - positionBeaconA.x;
            double dB2 = pGsm.y - positionBeaconA.y;
            dB1 = Math.abs(dB1);
            dB2 = Math.abs(dB2);
            double distB = Math.sqrt(Math.pow(dB1, 2) + Math.pow(dB2, 2));

            double dC1 = pGsm.x - positionBeaconD.x;
            double dC2 = pGsm.y - positionBeaconD.y;
            dC1 = Math.abs(dC1);
            dC2 = Math.abs(dC2);
            double distC = Math.sqrt(Math.pow(dC1, 2) + Math.pow(dC2, 2));

            double MaxDistRecu = distanceBeaconA + distanceBeaconB + distanceBeaconD;
            double MaxDistCalculer = distA + distB + distC;

            double ratioDistRecu = MaxDistRecu / 100;
            double pctA = distA / ratioDistRecu;
            double pctB = distB / ratioDistRecu;
            double pctC = distC / ratioDistRecu;

            double ratioDistCalculer = MaxDistCalculer / 100;
            distanceBeaconB = pctA * ratioDistCalculer;
            distanceBeaconA = pctB * ratioDistCalculer;
            distanceBeaconD = pctC * ratioDistCalculer;
        } else {
            distanceBeaconB = -1;
            distanceBeaconA = -1;
            distanceBeaconD = -1;
        }
    }

    private void estPositionPossibleCAD(Point pGsm) {
        if (pGsm != null) {
            double dA1 = pGsm.x - positionBeaconC.x;
            double dA2 = pGsm.y - positionBeaconC.y;
            dA1 = Math.abs(dA1);
            dA2 = Math.abs(dA2);
            double distA = Math.sqrt(Math.pow(dA1, 2) + Math.pow(dA2, 2));

            double dB1 = pGsm.x - positionBeaconA.x;
            double dB2 = pGsm.y - positionBeaconA.y;
            dB1 = Math.abs(dB1);
            dB2 = Math.abs(dB2);
            double distB = Math.sqrt(Math.pow(dB1, 2) + Math.pow(dB2, 2));

            double dC1 = pGsm.x - positionBeaconD.x;
            double dC2 = pGsm.y - positionBeaconD.y;
            dC1 = Math.abs(dC1);
            dC2 = Math.abs(dC2);
            double distC = Math.sqrt(Math.pow(dC1, 2) + Math.pow(dC2, 2));

            double MaxDistRecu = distanceBeaconA + distanceBeaconC + distanceBeaconD;
            double MaxDistCalculer = distA + distB + distC;

            double ratioDistRecu = MaxDistRecu / 100;
            double pctA = distA / ratioDistRecu;
            double pctB = distB / ratioDistRecu;
            double pctC = distC / ratioDistRecu;

            double ratioDistCalculer = MaxDistCalculer / 100;
            distanceBeaconC = pctA * ratioDistCalculer;
            distanceBeaconA = pctB * ratioDistCalculer;
            distanceBeaconD = pctC * ratioDistCalculer;
        } else {
            distanceBeaconC = -1;
            distanceBeaconA = -1;
            distanceBeaconD = -1;
        }
    }

    private void estPositionPossibleBCD(Point pGsm) {
        if (pGsm != null) {
            double dA1 = pGsm.x - positionBeaconB.x;
            double dA2 = pGsm.y - positionBeaconB.y;
            dA1 = Math.abs(dA1);
            dA2 = Math.abs(dA2);
            double distA = Math.sqrt(Math.pow(dA1, 2) + Math.pow(dA2, 2));

            double dB1 = pGsm.x - positionBeaconC.x;
            double dB2 = pGsm.y - positionBeaconC.y;
            dB1 = Math.abs(dB1);
            dB2 = Math.abs(dB2);
            double distB = Math.sqrt(Math.pow(dB1, 2) + Math.pow(dB2, 2));

            double dC1 = pGsm.x - positionBeaconD.x;
            double dC2 = pGsm.y - positionBeaconD.y;
            dC1 = Math.abs(dC1);
            dC2 = Math.abs(dC2);
            double distC = Math.sqrt(Math.pow(dC1, 2) + Math.pow(dC2, 2));

            double MaxDistRecu = distanceBeaconB + distanceBeaconC + distanceBeaconD;
            double MaxDistCalculer = distA + distB + distC;

            double ratioDistRecu = MaxDistRecu / 100;
            double pctA = distA / ratioDistRecu;
            double pctB = distB / ratioDistRecu;
            double pctC = distC / ratioDistRecu;

            double ratioDistCalculer = MaxDistCalculer / 100;
            distanceBeaconB = pctA * ratioDistCalculer;
            distanceBeaconC = pctB * ratioDistCalculer;
            distanceBeaconD = pctC * ratioDistCalculer;
        } else {
            distanceBeaconB = -1;
            distanceBeaconC = -1;
            distanceBeaconD = -1;
        }
    }

    private void estPositionPossible(Point pGsm) {

        if (pGsm != null) {
            double dA1 = pGsm.x - positionBeaconA.x;
            double dA2 = pGsm.y - positionBeaconA.y;
            dA1 = Math.abs(dA1);
            dA2 = Math.abs(dA2);
            double distA = Math.sqrt(Math.pow(dA1, 2) + Math.pow(dA2, 2));

            double dB1 = pGsm.x - positionBeaconB.x;
            double dB2 = pGsm.y - positionBeaconB.y;
            dB1 = Math.abs(dB1);
            dB2 = Math.abs(dB2);
            double distB = Math.sqrt(Math.pow(dB1, 2) + Math.pow(dB2, 2));

            double dC1 = pGsm.x - positionBeaconC.x;
            double dC2 = pGsm.y - positionBeaconC.y;
            dC1 = Math.abs(dC1);
            dC2 = Math.abs(dC2);
            double distC = Math.sqrt(Math.pow(dC1, 2) + Math.pow(dC2, 2));

            double MaxDistRecu = distanceBeaconA + distanceBeaconB + distanceBeaconC;
            double MaxDistCalculer = distA + distB + distC;

            double ratioDistRecu = MaxDistRecu / 100;
            double pctA = distA / ratioDistRecu;
            double pctB = distB / ratioDistRecu;
            double pctC = distC / ratioDistRecu;

            double ratioDistCalculer = MaxDistCalculer / 100;
            distanceBeaconA = pctA * ratioDistCalculer;
            distanceBeaconB = pctB * ratioDistCalculer;
            distanceBeaconC = pctC * ratioDistCalculer;
        } else {
            distanceBeaconA = -1;
            distanceBeaconB = -1;
            distanceBeaconC = -1;
        }

    }

    private void Algo() {
        double W, Z, foundBeaconLat, foundBeaconLong, foundBeaconLongFilter;
        W = Math.pow(distanceBeaconA,2) - Math.pow(distanceBeaconB,2) - Math.pow(positionBeaconA.x,2)
                - Math.pow(positionBeaconA.y,2) + Math.pow(positionBeaconB.x,2)
                + Math.pow(positionBeaconB.y,2);
        Z = Math.pow(distanceBeaconB,2) - Math.pow(distanceBeaconC,2) - Math.pow(positionBeaconB.x,2)
                - Math.pow(positionBeaconB.y,2) + Math.pow(positionBeaconC.x,2)
                + Math.pow(positionBeaconC.y,2);

        foundBeaconLat = (W * (positionBeaconC.y - positionBeaconB.y) - Z * (positionBeaconB.y - positionBeaconA.y))
                / (2 * ((positionBeaconB.x - positionBeaconA.x) * (positionBeaconC.y - positionBeaconB.y)
                - (positionBeaconC.x - positionBeaconB.x) * (positionBeaconB.y - positionBeaconA.y)));

        foundBeaconLong = (W - 2 * foundBeaconLat * (positionBeaconB.x - positionBeaconA.x))
                / (2 * (positionBeaconB.y - positionBeaconA.y));
        //'foundBeaconLongFilter` is a second measure of `foundBeaconLong` to mitigate errors
        foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (positionBeaconC.x - positionBeaconB.x))
                / (2 * (positionBeaconC.y - positionBeaconB.y));

        foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;

        Point positionGsm1 = new Point((int)foundBeaconLat,(int)foundBeaconLong);
    }


}
