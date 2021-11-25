package org.whitneyrobotics.ftc.teamcode.subsys;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.lib.util.SimpleTimer;
import org.whitneyrobotics.ftc.teamcode.lib.util.Toggler;

public class Carousel {
    private DcMotorEx wheel;
    private final static double slowPower = 0.4;
    private final static double zoomPower = 1;
    private SimpleTimer timer = new SimpleTimer();
    private Toggler allianceSwitch = new Toggler(2);
    private boolean carouselInProgress = false;
    private boolean slowDone = false;
    private boolean firstSlow = true;
    private boolean firstFast = true;
    public String carouselAlliance;
    public int carouselState = 0;
    private Toggler powerSwitch = new Toggler(2);

    public Carousel(HardwareMap hardwareMap){
        wheel = hardwareMap.get(DcMotorEx.class,"carouselMotor");
        wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void operate(boolean start, boolean changeAlliance){
        allianceSwitch.changeState(changeAlliance);
        if(start && !carouselInProgress) {
            switch (carouselState) {
                case 0:
                    timer.set(1);
                    carouselInProgress = true;
                    carouselState++;
                    break;
                case 1:
                    if (!timer.isExpired()) {
                        wheel.setPower(slowPower * (allianceSwitch.currentState() == 1 ? -1 : 1));
                    } else {
                        carouselState++;
                    }
                    break;
                case 2:
                    timer.set(2);
                    carouselState++;
                    break;
                case 3:
                    if (!timer.isExpired()) {
                        wheel.setPower(zoomPower * (allianceSwitch.currentState() == 1 ? -1 : 1));
                    } else {
                        carouselState++;
                    }
                    break;
                case 4:
                    wheel.setPower(0);
                    carouselInProgress = false;
                    carouselState = 0;
                    break;
            }
        }
    }

    public void getCarouselAlliance(){
        carouselAlliance = allianceSwitch.currentState() == 0 ? "Red" : "Blue";
    }

    public void operateAuto(boolean blue){
        switch (carouselState){
            case 0:
                timer.set(1);
                carouselInProgress = true;
                carouselState++;
                break;
            case 1:
                if (!timer.isExpired()){
                    wheel.setPower(slowPower * (blue ? -1 : 1));
                } else {
                    carouselState++;
                }
                break;
            case 2:
                timer.set(2);
                carouselState++;
                break;
            case 3:
                if (!timer.isExpired()){
                    wheel.setPower(zoomPower * (blue ? -1 : 1));
                } else {
                    carouselState++;
                }
                break;
            case 4:
                wheel.setPower(0);
                carouselInProgress = false;
                carouselState = 0;
                break;
        }
    }

    public void togglerOperate(boolean input, boolean alliance){
        allianceSwitch.changeState(alliance);
        if(!carouselInProgress){powerSwitch.changeState(input);}
        if(powerSwitch.currentState() == 0){
            wheel.setPower(0);
        } else {
            wheel.setPower(0.5 * ((allianceSwitch.currentState() == 0) ? 1 : -1));
        }
    }

    public String getAlliance(){return (allianceSwitch.currentState() == 0) ? "Red" : "Blue";}
    public boolean isCarouselInProgress() {return carouselInProgress;}
}
