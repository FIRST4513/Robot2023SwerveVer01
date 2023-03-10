// Based on Code from Team364 - BaseFalconSwerve
// https://github.com/Team364/BaseFalconSwerve/tree/338c0278cb63714a617f1601a6b9648c64ee78d1

package frc.lib.swerve;

public class SwerveModuleConfig {
    public final String moduleName;
    public final int driveMotorID;
    public final int angleMotorID;
    public final int cancoderID;
    public final double angleOffset;
    public final double angleOffsetPractice;

    /**
     * Swerve Module Config to be used when creating swerve modules.
     *
     * @param moduleName Name of Module ie) "Front Left"
     * @param driveMotorID ID of the drive motor
     * @param angleMotorID ID of the angle motor
     * @param canCoderID ID of the canCoder
     * @param angleOffset Offset of the angle motor
     * @param angleOffsetPractice Offset of the angle motor for practice bot
     */
    public SwerveModuleConfig(
            String moduleName,
            int driveMotorID,
            int angleMotorID,
            int canCoderID,
            double angleOffset,
            double angleOffsetPractice) {
        this.moduleName = moduleName;
        this.driveMotorID = driveMotorID;
        this.angleMotorID = angleMotorID;
        this.cancoderID = canCoderID;
        this.angleOffset = angleOffset;
        this.angleOffsetPractice = angleOffsetPractice;
    }
}
