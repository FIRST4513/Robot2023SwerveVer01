package frc.robot.swerveDrive;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;
import java.util.Map;

public class SwerveDriveTelemetry {
    protected ShuffleboardTab tab;
    private SwerveDriveSubSys swerve;

    public SwerveDriveTelemetry(SwerveDriveSubSys swerve) {
        this.swerve = swerve;
        tab = Shuffleboard.getTab("Swerve");
        tab.addNumber("Heading", () -> swerve.getHeading().getDegrees()).withPosition(0, 0);
        tab.addNumber("Odometry X", () -> swerve.getPoseMeters().getX()).withPosition(0, 1);
        tab.addNumber("Odometry Y", () -> swerve.getPoseMeters().getY()).withPosition(0, 2);

        moduleLayout("Mod 0", 0, tab).withPosition(1, 0);
        moduleLayout("Mod 1", 1, tab).withPosition(2, 0);
        moduleLayout("Mod 2", 2, tab).withPosition(3, 0);
        moduleLayout("Mod 3", 3, tab).withPosition(4, 0);
    }

    public void testMode() {
        moduleLayout("Mod 0", 0, tab).withPosition(1, 0);
        moduleLayout("Mod 1", 1, tab).withPosition(2, 0);
        moduleLayout("Mod 2", 2, tab).withPosition(3, 0);
        moduleLayout("Mod 3", 3, tab).withPosition(4, 0);
    }

    public void logModuleStates(String key, SwerveModuleState[] states) {
       // Robot.log.logger.recordOutput(key, states);
    }

    public void logModuleAbsolutePositions() {
        // for (SwerveModule mod : swerve.mSwerveMods) {
        //      Robot.log.logger.recordOutput(
        //            "Mod " + mod.moduleNumber + " Absolute", mod.getAbsoluteAngle().getDegrees());
        // }
    }

    public ShuffleboardLayout moduleLayout(String name, int moduleNum, ShuffleboardTab tab) {
        ShuffleboardLayout modLayout = tab.getLayout(name, BuiltInLayouts.kGrid);
        // m_mod0Layout.withSize(1, 2);
        modLayout.withProperties(Map.of("Label position", "TOP"));

        // mod Name
        SuppliedValueWidget<String> modNameWidget =
                modLayout.addString(
                        "Name",
                        () -> swerve.mSwerveMods[moduleNum].getName());
        modNameWidget.withPosition(0, 0);

        // mod Cancoder Angle
        SuppliedValueWidget<Double> modCancoderAngleWidget =
                modLayout.addNumber(
                        "Cancoder Angle",
                        () -> swerve.mSwerveMods[moduleNum].getAbsoluteAngle().getDegrees());
        modCancoderAngleWidget.withPosition(0, 1);

        // mod Cancoder Angle with Offset
        SuppliedValueWidget<Double> modCancoderAngleWOWidget =
        modLayout.addNumber(
                "Cancoder Angle WO",
                () -> swerve.mSwerveMods[moduleNum].getAbsoluteAngleWithOffset());
        modCancoderAngleWOWidget.withPosition(0, 2);

        // mod Integrated Angle
        SuppliedValueWidget<Double> modIntegratedAngleWidget =
                modLayout.addNumber(
                        "Integrated Angle",
                        () -> swerve.mSwerveMods[moduleNum].getFalconAngle());
        modIntegratedAngleWidget.withPosition(0, 3);

        // mod Velocity
        SuppliedValueWidget<Double> modVelocityWidget =
                modLayout.addNumber(
                        "Wheel Velocity",
                        () -> swerve.mSwerveMods[moduleNum].getState().speedMetersPerSecond);
        modVelocityWidget.withPosition(0, 4);

        // mod TargetAngle
        SuppliedValueWidget<Double> modTargetAngleWidget =
                modLayout.addNumber(
                        "Target Angle", () -> swerve.mSwerveMods[moduleNum].getTargetAngle());
        modTargetAngleWidget.withPosition(0, 5);
        return modLayout;
    }
}
