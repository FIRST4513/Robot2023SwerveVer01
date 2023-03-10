package frc.robot.pilot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.gamepads.Gamepad;
import frc.lib.gamepads.mapping.ExpCurve;
import frc.robot.logger.commands.LoggerCmds;
import frc.robot.pilot.PilotGamepadConfig.MaxSpeeds;
import frc.robot.pilot.commands.PilotGamepadCmds;
import frc.robot.swerveDrive.commands.SwerveDriveCmds;

/** Used to add buttons to the pilot gamepad and configure the joysticks */
public class PilotGamepad extends Gamepad {
    //public final PilotGamepadTelemetry telemetry;
    public static ExpCurve forwardSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.forwardSpeedExp,
                    PilotGamepadConfig.forwardSpeedOffset,
                    PilotGamepadConfig.forwardSpeedScaler,
                    PilotGamepadConfig.forwardSpeedDeadband);
    public static ExpCurve sidewaysSpeedCurve =
            new ExpCurve(
                    PilotGamepadConfig.sidewaysSpeedExp,
                    PilotGamepadConfig.sidewaysSpeedOffset,
                    PilotGamepadConfig.sidewaysSpeedScaler,
                    PilotGamepadConfig.sidewaysSpeedDeadband);
    public static ExpCurve rotationCurve =
            new ExpCurve(
                    PilotGamepadConfig.rotationSpeedExp,
                    PilotGamepadConfig.rotationSpeedOffset,
                    PilotGamepadConfig.rotationSpeedScaler,
                    PilotGamepadConfig.rotationSpeedDeadband);
    SendableChooser<String> speedChooser = new SendableChooser<String>();

    public PilotGamepad() {
        super("Pilot", PilotGamepadConfig.port);

        // Setup Speed Selector
    	speedChooser.setDefaultOption("1. SLOW", 	"Slow");
    	speedChooser.addOption ("2. MED. Slow",	    "MedSlow");
    	speedChooser.addOption ("3. MED. Fast", 	"MedFast");
    	speedChooser.addOption ("4. Fast", 	        "Fast");
        SmartDashboard.putData(speedChooser);

        //telemetry = new PilotGamepadTelemetry(this);
    }

    public void setupTeleopButtons() {

        // Rest Gyro to 0
        gamepad.startButton.onTrue(SwerveDriveCmds.zeroGyroHeadingCmd());

        // Teleop Drive with R0bot Perspective
        gamepad.aButton.whileTrue(PilotGamepadCmds.rpvPilotSwerveCmd());
        gamepad.yButton.onTrue(LoggerCmds.WriteOutLogFileCmd());

    }

    public void setupDisabledButtons() {
    }

    public void setupTestButtons() {
        gamepad.aButton.whileTrue(SwerveDriveCmds.testWheelFwdCmd());
        gamepad.bButton.whileTrue(SwerveDriveCmds.testWheelFwdLeftCmd());
        gamepad.xButton.whileTrue(SwerveDriveCmds.testWheelFwdRightCmd());
        gamepad.yButton.whileTrue(SwerveDriveCmds.lockSwerveCmd());
        gamepad.startButton.whileTrue(SwerveDriveCmds.resetFalconAnglesCmd());
    }

    // forward/backward down the field
    public double getDriveFwdPositive() {
        return forwardSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getY());
    }

    // side-to-side across the field
    public double getDriveLeftPositive() {
        // right will be priority in code, but not primarily used in driving practice
        if (Math.abs(this.gamepad.rightStick.getX()) > Math.abs(this.gamepad.leftStick.getX())) {
            return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.rightStick.getX()) * 1;  // change later to scale
        } else {
            return sidewaysSpeedCurve.calculateMappedVal(this.gamepad.leftStick.getX());
        }
    }

    //Positive is counter-clockwise, left Trigger is positive
    public double getDriveRotationCCWPositive() {
		double value = this.gamepad.triggers.getTwist();
		value = rotationCurve.calculateMappedVal(value);
		return value;        
    }

    // Return the angle created by the left stick in radians, 0 is up, pi/2 is left
    public Double getDriveAngle() {
        return Math.atan2(
            getDriveFwdPositive(),
            -getDriveLeftPositive());
    }

    public MaxSpeeds getSelectedSpeed(){
        String speed = speedChooser.getSelected();;
        if ( speed == "Fast") return MaxSpeeds.FAST;
        if ( speed == "MedFast") return MaxSpeeds.MEDFAST;
        if ( speed == "MedSlow") return MaxSpeeds.MEDSLOW;
        return MaxSpeeds.SLOW;
    }

    public void setMaxSpeeds(MaxSpeeds speed){
        switch (speed) { 
            case FAST:
                forwardSpeedCurve.setScalar(PilotGamepadConfig.FastfowardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.FastsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.FastsidewaysVelocity);
                System.out.println("Driver Speeds set to FAST !!!");
                break;
            case MEDFAST:
                forwardSpeedCurve.setScalar(PilotGamepadConfig.MedFastfowardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.MedFastsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.MedFastsidewaysVelocity);
                System.out.println("Driver Speeds set to MEDFAST !!!");
                break;
            case MEDSLOW:
                forwardSpeedCurve.setScalar(PilotGamepadConfig.MedSlowfowardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.MedSlowsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.MedSlowsidewaysVelocity);
                System.out.println("Driver Speeds set to MEDSLOW !!!");
                break;
            default:
                forwardSpeedCurve.setScalar(PilotGamepadConfig.SlowfowardVelocity);
                sidewaysSpeedCurve.setScalar(PilotGamepadConfig.SlowsidewaysVelocity);
                rotationCurve.setScalar(PilotGamepadConfig.SlowsidewaysVelocity);
                System.out.println("Driver Speeds set to Slow !!!");
                break;
        }
    }



    public void rumble(double intensity) {
        this.gamepad.setRumble(intensity, intensity);
    }
}
