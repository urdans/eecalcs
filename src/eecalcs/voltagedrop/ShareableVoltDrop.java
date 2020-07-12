package eecalcs.voltagedrop;

import eecalcs.conductors.Size;

/**
 This interface represents a read only {@link VoltDrop} class. As such, any
 instance of this interface will have access to the getters of the voltage drop
 class. Only the setMaxVoltageDropPercent setter is accessible through this
 interface.
 */
public interface ShareableVoltDrop {
    /**
    Returns the actual AC voltage drop for the calculated conductor size, under
    the given conditions.

    @return The AC voltage drop in volts, of the calculated conductor size.
    @see #getCalculatedSizeAC()
    */
    double getActualVoltageDropPercentageAC();

    /**
    Returns the actual DC voltage drop for the calculated conductor size, under
    the given conditions.

    @return The DC voltage drop in volts, of the calculated conductor. If the
    returned value is zero, check the resultMessage field content.
    @see #getCalculatedSizeDC()
    */
    double getActualVoltageDropPercentageDC();

    /**
    Calculates and returns the AC voltage in volts at the load terminals
    under the preset conditions.

    @return The voltage at the load terminals in volts. If the result is zero,
    the resultMessage field content must be checked to determine the reason.
    */
    double getACVoltageAtLoad();

    /**
    Calculates and returns the AC voltage drop in volts across the set of
    conductors under the preset conditions.

    @return The voltage drop in volts. If the result is zero, the
    resultMessage field content must be checked to determine the reason.
    */
    double getACVoltageDrop();

    /**
    Calculates and returns the AC voltage drop percentage across the set of
    conductors under the preset conditions.

    @return The voltage drop in percentage. If the result is zero, the
    resultMessage field content must be checked to determine the reason.
    */
    double getACVoltageDropPercentage();

    /**
    Returns the size of the circuit's conductor whose AC voltage drop (under
    the given conditions) is less or equals to the given maximum voltage drop.
    If the returned Size is null the user should check for errors in the
    resultMessage field.

    @return The size of the conductor as defined in {@link Size}.
    */
    Size getCalculatedSizeAC();

    /**
    Returns the size of the circuit's conductor whose DC voltage drop (under
    the given conditions) is less or equals to the given maximum voltage drop.
    If the returned value is null the user must check the resultMessage field
    content.

    @return The size of the conductor as defined in {@link Size}.
    */
    Size getCalculatedSizeDC();

    /**
    Calculates and returns the DC voltage in volts at the load terminals
    under the preset conditions.

    @return The voltage at the load terminals in volts. If the result is zero,
    the resultMessage field content must be checked to determine the reason.
    */
    double getDCVoltageAtLoad();

    /**
    Calculates and returns the DC voltage drop in volts across the set of
    conductors under the preset conditions.

    @return The voltage drop in volts. If the result is zero, the
    resultMessage field content must be checked to determine the reason.
    */
    double getDCVoltageDrop();

    /**
    Calculates and returns the DC voltage drop percentage across the set of
    conductors under the preset conditions.

    @return The voltage drop in percentage. If the result is zero, the
    resultMessage field content must be checked to determine the reason.
    */
    double getDCVoltageDropPercentage();

    /**
    Returns the one way length of the circuit's calculated conductor size whose
    AC voltage drop (under the given conditions) is less or equals to the given
    maximum voltage drop. If the returned value is zero, the user must check
    the result Message field content.

    @return The maximum conductor's length in feet.
    @see #getCalculatedSizeAC()
    */
    double getMaxLengthACForCalculatedSize();

    /**
    Returns the one way length of the circuit's actual conductor size that
    would reach the given maximum voltage drop.
    If the returned value is zero, the user must check the result Message field
    content.

    @return The maximum conductor's length in feet.
    @see #setMaxVoltageDropPercent(double)
    */
    double getMaxLengthACForActualConductor();

    /**
     Returns the calculated one way length of the circuit's conductor size whose
     DC voltage drop (under the given conditions) is less or equals than the
     given maximum voltage drop. If the returned value is zero, the user must
     check the resultMessage field content.

     @return The maximum conductor's length in feet.
     @see #getCalculatedSizeDC()
     */
    double getMaxLengthDCForCalculatedSize();

    /**
    Returns the calculated one way length of the circuit's actual conductor size
    that would reach the given maximum voltage drop under actual conditions.
    If the returned value is zero, the user must check the resultMessage field
    content.

    @return The maximum conductor's length in feet.
    @see #setMaxVoltageDropPercent(double)
    */
    double getMaxLengthDCForActualConductor();

    /**
    Sets the maximum allowed voltage drop. This value is used to compute the
    the size and the maximum length of the circuit conductors that would have a
    voltage drop less or equal to the specified value.

    @param maxVoltageDropPercent The maximum voltage drop in percentage.
    Notice that no validation is performed at this point. The user
    must check for the presence of errors or warnings after obtaining a
    calculation result of zero.
    */
    void setMaxVoltageDropPercent(double maxVoltageDropPercent);

    /**
     @return  The maximum voltage drop in percentage, used to compute the
     the size and the maximum length of the circuit conductors that would have a
     voltage drop less or equal to the specified value
     */
    double getMaxVoltageDropPercent();

}
