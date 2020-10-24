package eecalcs.conductors;

import eecalcs.systems.TempRating;

/**
 This interface is intended to be used by the class {@link Conductor} in
 order to
 hide some of its public methods.
 */
public interface RoConductor {

	/**
	 @return The size of this conductor
	 */
	Size getSize();

	/** Refer to {@link ROConduitable#getInsulation()} ()} */
	Insul getInsulation();

	/** Refer to {@link ROConduitable#getAmpacity()} ()} */
	double getAmpacity();

	/** Refer to {@link ROConduitable#getCorrectionFactor()} ()} */
	double getCorrectionFactor();

	/** Refer to {@link ROConduitable#getAdjustmentFactor()} ()} */
	double getAdjustmentFactor();

	/** Refer to {@link ROConduitable#getTemperatureRating()} ()} */
	TempRating getTemperatureRating();

	/** Refer to {@link ROConduitable#getMetal()} ()} */
	Metal getMetal();

	/** Refer to {@link ROConduitable#getCompoundFactor()} */
	double getCompoundFactor();

	/** Refer to {@link ROConduitable#getAmbientTemperatureF()} */
	int getAmbientTemperatureF();

	/** Refer to {@link ROConduitable#getLength()} */
	double getLength();

	/**
	 @return True if this conductor object is part of a bundle object,
	 false otherwise.
	 */
	boolean hasBundle();
}
