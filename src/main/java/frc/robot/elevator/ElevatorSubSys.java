package frc.robot.elevator;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.Rmath;

public class ElevatorSubSys extends SubsystemBase {
    public ElevatorConfig config;

    // Elevator Variables
    public double target_height;    // Relative to Ground
    public double target_pos;       // Relative to zero
    // public double target_test_pos = 20.0;

    public double mCurrPwr = 0;
    public double mCurrElevPwr;
    public double PID_output = 0;
    public double mCurrEncoderCnt = 0;
    public double mCurrElevPos;     // Elevator Height Zero at bottom (Inches)
    public double mCurrElevHt;      // Grabber Height off the floor (Inches)
    
    // Standard classes for controlling our elevator
    public final PIDController m_controller;
    public final Encoder m_encoder;
    public final Spark m_motor;
    //public final ElevatorTelemetry telemetry;
    public final DigitalInput elevLwrLimitSw;

    /** Creates a new ElevatorSim. */
    public ElevatorSubSys() {

        config = new ElevatorConfig();
        //telemetry = new ElevatorTelemetry(this);
        
        m_controller = new PIDController(config.kElevatorKp, 0, 0);
        m_encoder = new Encoder(config.kEncoderAChannel, config.kEncoderBChannel);
        m_motor = new Spark(config.kMotorPort);
        elevLwrLimitSw = new DigitalInput(config.kLowerLimitSwitchPort);

        m_encoder.setDistancePerPulse(config.kElevatorEncoderDistPerPulse);
    }

    // --------------------------------------------
    // ---------   Elevator Methods   -------------
    // --------------------------------------------

    public void setOutput(double speed) {
        m_motor.set(speed);
    }

    public void goToTarget(int height) {
        double pidOutput = m_controller.calculate(m_encoder.getDistance(), height);
        m_motor.setVoltage(pidOutput);
    }


    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
 
   // ------------ Stop Elevator Motor  ----------
   public void stop() {
    mCurrElevPwr = 0;        
    m_motor.stopMotor();            // Send Power to motor  
}

// ------------ Hold Elevator Position ----------
public void elevHoldMtr(){
    if (isLowerLmtReached() == true) {
        stop();
        return;	
    } else {
        mCurrElevPwr = config.KHoldSpeedDefault ;
    }
    m_motor.set(mCurrElevPwr);      // Send Power to motor  
}

// ------------ Lower Elevator ----------
public void elevLower() {
    System.out.println("************** Elevator Lower ***********");
    mCurrElevPwr = config.KLowerSpeedDefault;
    if (isLowerLmtReached()){  		
        stop();
        return;
    }
    //  This is for slowing down as we approach the bottom    		
    if( mCurrElevPos <= config.KLimitElevBottomSlowPos ) {
        mCurrElevPwr= config.KLowerSlowSpeed;
    }
    m_motor.set(mCurrElevPwr);      // Send Power to motor  
}

// ------------ Raise Elevator ----------
public void elevRaise() {
    System.out.println("************** Elevator Raise ***********");
    mCurrElevPwr = config.KRaiseSpeedDefault;
    if (isUpperLmtReached()) {
        elevHoldMtr();
        return;
    }
    //  This is for slowing down as we approach the top
    if( mCurrElevPos >= config.KLimitElevTopSlowPos )  {
        mCurrElevPwr = config.KRaiseSlowSpeed;
    }
    m_motor.set(mCurrElevPwr);    	// Send Power to motor  
}

// ------------ Manually Drive Elevator ----------
public void elevSetSpeed(double speed){
    mCurrElevPwr = speed;
    m_motor.set(mCurrElevPwr);      // Send Power to motor  
}

// ------------  Set Elevator to Position by PID  ----------
public void setPIDposition( double pos ) {
    // All motion controlled relative to bottom of the elevator (not height from floor)
    target_pos = limit_target_pos (pos);
    target_height = convertPosToHeight(target_pos);
    mCurrElevPwr = getPidCalcOut( target_pos );
    m_motor.set(mCurrElevPwr);                       // Send Power to motor  Pwr -1 to +1
    //m_motor.setVoltage(mCurrElevPwr);              // Voltage -12 to +12 ???????        
}

// ------------  Set Elevator to Height by PID  ----------    
public void setPIDheight( double ht ) {
    // All motion controlled relative to Floor
    target_pos = limit_target_pos(convertHeightToPos( ht ));
    target_height = convertPosToHeight( target_pos);
    mCurrElevPwr = getPidCalcOut( target_pos );
    System.out.println("    PID to " + ht + "   CurPos=" + mCurrElevPos + " out=" + mCurrElevPwr);
    m_motor.set(mCurrElevPwr);                       // Send Power to motor  Pwr -1 to +1
    //m_motor.setVoltage(mCurrElevPwr);              // Voltage -12 to +12 ???????  
}

// ---------  PID Out Calculator  --------------
public double getPidCalcOut(double tgt_setpoint) {
    double tgt = limit_target_pos (tgt_setpoint);
    double out = m_controller.calculate(mCurrElevPos, tgt );
    out = out + config.kElevatorKf;             // Add feedforward Term
    // Limit max pwr
    if ( out > config.kMaxPwr )  { out = config.kMaxPwr; }
    if ( out < -config.kMaxPwr ) { out = -config.kMaxPwr; }
    return out;
}

// Test PID Calc routine
public double getPidCalcTestOut(double test_setpoint) {
    return getPidCalcOut(test_setpoint);
}

// -----------------  Encoder Sensor Methods --------------------
public void updateCurrentElevPosition() {
    mCurrEncoderCnt = m_encoder.get(); 
    mCurrElevPos = Rmath.mRound((mCurrEncoderCnt * config.ELEV_ENCODER_CONV) , 2);
    mCurrElevHt =  Rmath.mRound( convertPosToHeight(mCurrElevPos) , 2 );
 }

public double getElevEncoderCnt()               { return mCurrEncoderCnt;}
public double getElevPosInches()                { return mCurrElevPos; }
public double getElevHeightInches()             { return mCurrElevHt; }

public double convertHeightToPos( double tgt)   { return tgt - config.ELEV_INCH_ABOVE_GROUND; }
public double convertPosToHeight( double tgt)   { return tgt + config.ELEV_INCH_ABOVE_GROUND; }

// ------------- Other Misc Methods  ---------------
public double getTargetHeight()         { return target_height; }
public double getElevMotorPwr()         { return mCurrElevPwr; }

public double limit_target_pos( double pos ){
    if (pos > config.KElevMaxTopPos)    { pos = config.KElevMaxTopPos; }
    if (pos < 0)                        { pos = 0; }
    return pos;
}

// -----------------  Lower/Upper Limits ----------------
public boolean isLowerLmtReached() {
    if (elevLwrLimitSw.get() == config.KLIMIT_SWITCH_PRESSED) return true;
   return false;
}

public boolean isUpperLmtReached() {
    // Check Encoder for exceeding top value
    if ( mCurrElevPos >= config.KElevMaxTopPos ) return true;
   return false;
}

public boolean isLowerLmtSwNotPressed() { return !isLowerLmtReached(); }
public boolean isUpperLmtNotReached()   { return !isUpperLmtReached(); }

public String getLimitSwStatus(){
    if ( isUpperLmtReached() )          { return "AT Top"; }
    if ( isLowerLmtReached() )          { return "AT Bottom"; }
    return "IN Between";
}

//public double getTargetTestPos()               { return target_test_pos; }
public double getTargetPos()                   { return target_pos; }


}
