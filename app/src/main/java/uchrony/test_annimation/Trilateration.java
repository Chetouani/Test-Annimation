package uchrony.test_annimation;

import android.graphics.Point;
import android.widget.Toast;

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

        this.distanceBeaconA = -1;
        this.distanceBeaconB = -1;
        this.distanceBeaconC = -1;
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
        Point positionTheorique = trilaterationAlgo();
       // estPositionPossible(positionTheorique);
       // positionTheorique = trilaterationAlgo();
        return positionTheorique;
    }

    private Point trilaterationAlgo() {
        if ( distanceBeaconA != -1 && distanceBeaconB != -1 && distanceBeaconC != -1) {
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
        } else {
            return null;
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

    public double getDistanceBeaconA() {
        return distanceBeaconA;
    }

    public double getDistanceBeaconB() {
        return distanceBeaconB;
    }

    public double getDistanceBeaconC() {
        return distanceBeaconC;
    }

/*
    public int findMyLocation(int bx1,int bx2,int bx3,int by1,int by2,int by3,double R1,double R2,double R3) {

        if (R1 < -250 || R2 < -250 || R3 < -250) {

        } else {
            int x = (((bx1-bx2)^2)+((by1-by2)^2))^1/2;
            int y = (((bx2-bx3)^2)+((by2-by3)^2))^1/2;
            int z = (((bx3-bx1)^2)+((by3-by1)^2))^1/2;

            // R1
            double B_old1 = ((Math.abs(R1)-60)*0.15);
            String B_new1 = String.format("%.1f",B_old1);
            double B1 = Double.parseDouble(B_new1);

            // R2
            double B_old2 = ((Math.abs(R2)-60)*0.15);
            String B_new2 = String.format("%.1f",B_old2);
            double B2 = Double.parseDouble(B_new2);

            // R3
            double B_old3 = ((Math.abs(R3)-60)*0.15);
            String B_new3 = String.format("%.1f",B_old3);
            double B3 = Double.parseDouble(B_new3);

            // formule de heron
            double s1 = (B1+B2+x)*1/2.0;
            double s2 = (B2+B3+x)*1/2.0;
            double s3 = (B3+B1+x)*1/2.0;

            // triangle 1
            double Areax = s1*((s1-B1)*(s1-B2)*(s1-x));
            int areax12 = (int) Areax^1/2;
            // triangle 2
            double Areay = s2*((s2-B2)*(s2-B3)*(s2-y));
            int areay23 = (int) Areay^1/2;
            // triangle 3
            double Areaz = s3*((s3-B3)*(s3-B1)*(s3-z));
            int areaz31 = (int) Areaz^1/2;

            double s = (x+y+z)*1/2;
            double areaxyz = s*((s-x)*(s-y)*(s-z));
            int areabeacons = (int) areaxyz^1/2;

            int areaa11 = Math.abs(areax12) + Math.abs(areay23) + Math.abs(areaz31);

            if (areabeacons > areaa11) {
                return 0;
            } else {
                int[] minb1={3,2,1};
                int[] minb2={5,4,3};
                int[] minb3={8,7,6};
                int[] maxb1={4,3,2};
                int[] maxb2={6,5,4};
                int[] maxb3={9,8,7};

                int sid = findmusquare(B1,B2,B3,minb1,maxb1,minb2,maxb2,minb3,maxb3);
                return sid;
            }

        }
        return 0;
    }

    private int findmusquare(double b1, double b2, double b3, int[] minb1, int[] maxb1, int[] minb2, int[] maxb2, int[] minb3, int[] maxb3) {

        for (int i=0; i<minb1.length;i++) {
            if(b1 >= maxb1[i] && b1 <= maxb1[i]){
                if(b2 >= maxb2[i] && b2 <= maxb2[i]){
                    if(b3 >= maxb3[i] && b3 <= maxb3[i]){
                        return i;
                    }
                }
            }
        }
        return  -1;
    }
*/


}
