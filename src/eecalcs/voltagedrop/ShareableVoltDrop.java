package eecalcs.voltagedrop;

import eecalcs.conductors.Size;

public interface ShareableVoltDrop {
    /** Refer to {@link VoltDrop#getActualVoltageDropPercentageAC()} */
    public double getActualVoltageDropPercentageAC();

    /** Refer to {@link VoltDrop#getActualVoltageDropPercentageDC()} */
    public double getActualVoltageDropPercentageDC();

    /** Refer to {@link VoltDrop#getACVoltageAtLoad()} */
    public double getACVoltageAtLoad();

    /** Refer to {@link VoltDrop#getACVoltageDrop()} */
    public double getACVoltageDrop();

    /** Refer to {@link VoltDrop#getACVoltageDropPercentage()} */
    public double getACVoltageDropPercentage();

    /** Refer to {@link VoltDrop#getCalculatedSizeAC()} */
    public Size getCalculatedSizeAC();

    /** Refer to {@link VoltDrop#getCalculatedSizeDC()} */
    public Size getCalculatedSizeDC();

    /** Refer to {@link VoltDrop#getDCVoltageAtLoad()} */
    public double getDCVoltageAtLoad();

    /** Refer to {@link VoltDrop#getDCVoltageDrop()} */
    public double getDCVoltageDrop();

    /** Refer to {@link VoltDrop#getDCVoltageDropPercentage()} */
    public double getDCVoltageDropPercentage();

    /** Refer to {@link VoltDrop#getMaxLengthAC()} */
    public double getMaxLengthAC();

    /** Refer to {@link VoltDrop#getMaxLengthDC()} */
    public double getMaxLengthDC();

    /** Refer to {@link VoltDrop#setMaxVoltageDropPercent(double maxVoltageDropPercent)} */
    public void setMaxVoltageDropPercent(double maxVoltageDropPercent);

}
