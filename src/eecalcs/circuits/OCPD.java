package eecalcs.circuits;

import com.sun.deploy.util.ArrayUtil;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import tools.NotifierDelegate;

import java.lang.reflect.Array;

/**
 This class represents an OverCurrent Protection Device when providing
 simultaneous protection against short-circuit, ground-fault and overload.<br>
 The class provides the following services:<br>
 <ol>
 <li>Static methods for:
 <ul>
 <li>Proving the list of all standard ratings.</li>
 <li>Choosing the proper OCPD rating based on the given maximum value passed
 and if the next higher standard rating rule can or cannot be applied.</li>
 <li>Providing a list of device types (fuse, circuit breaker...)</li>
 </ul></li>
 <li>Instance methods for:
 <ul>
 <li>Associating this OCPD object to a circuit, through Constructor.</li>
 <li>Setting and getting the actual device type.</li>
 <li>Setting and getting info about if the actual device is 100% rated or
 not.</li>
 </ul>
 <li>This class determines the ratings of a circuit OCPD ratings as follows:
 it always uses the load's OCPD rating as maximum rating unless its value is
 zero (which means no OCPD device is required), in which case it determines
 the OCPD rating to protect the conductors only.</li>
 </ol>
 */

public class OCPD {
	private static final int[] standardRatings = {15, 20, 25, 30, 35, 40, 45,
			50, 60, 70, 80, 90, 100, 110, 125, 150, 175, 200, 225, 250, 300,
			350, 400, 450, 500, 600, 700, 800, 1000, 1200, 1600, 2000, 2500,
			3000, 4000, 5000, 6000};

	/**Table 250.122, rating of OCPD for sizing EGC*/
	private static final int[] ocpdEGC = {
			15, 20, 60, 100, 200,
			300, 400, 500, 600, 800,
			1000, 1200, 1600, 2000,
			2500, 3000, 4000, 5000,
			6000
	};

	/**Table 250.122 sizing of the copper EGC. It omits 300, 600 and 750.*/
	private static final Size[] copperEGC = {
			Size.AWG_14, Size.AWG_12, Size.AWG_10, Size.AWG_8, Size.AWG_6,
			Size.AWG_4, Size.AWG_3, Size.AWG_2, Size.AWG_1, Size.AWG_1$0,
			Size.AWG_2$0, Size.AWG_3$0, Size.AWG_4$0, Size.KCMIL_250,
			Size.KCMIL_350,	Size.KCMIL_400,	Size.KCMIL_500,	Size.KCMIL_700,
			Size.KCMIL_800
	};

	/**Table 250.122 sizing of the aluminum EGC. It omits 14, 3, 300, 500,
	800, 900, 1000; it repeats 600 and includes a non standard size (1200)
	twice, which is replaced in this software with 1250 and 1500.*/
	private static final Size[] aluminumEGC = {
			Size.AWG_12, Size.AWG_10, Size.AWG_8, Size.AWG_6, Size.AWG_4,
			Size.AWG_2, Size.AWG_1, Size.AWG_1$0, Size.AWG_2$0, Size.AWG_3$0,
			Size.AWG_4$0, Size.KCMIL_250, Size.KCMIL_350, Size.KCMIL_400,
			Size.KCMIL_600, Size.KCMIL_600, Size.KCMIL_750, Size.KCMIL_1250,
			Size.KCMIL_1500
	};

	/**
	 @return The index of the OCPD in table 250.122 that is equal or less
	 than the rating passed in the parameter and that if bigger than the
	 previous element in the table. Returns -1 if the rating passed exceeds
	 6000A.
	 For example, if the ocpd rating is 55, it returns the index of the ocpd
	 corresponding to 60.
	 @param ocpdRating the rating to search for.
	 */
	private static int indexOfOCPD(int ocpdRating){
		ocpdRating = Math.abs(ocpdRating);
		if(ocpdRating <= 15)
			return 0;
		for(int index = 0; index < ocpdEGC.length - 1; index++){
			int prev = ocpdEGC[index];
			int next = ocpdEGC[index + 1];
			if(ocpdRating == prev)
				return index;
			if(ocpdRating > prev && ocpdRating <= next)
				return index + 1;
		}
		return -1;
	}

	/**Indicates if this OCPD is 100% rated or not. By default it is not. This
	information is not used by this class in any of its behaviors. It is
	intended to be used by the Circuit class and Load class to decide if the
	1.25 factor is applied or not.
	*/
	private boolean _100PercentRated = false; //it's 80% rated by default.
	private final Circuit circuit;

	/**
	 @return The notifier delegate object for this object.
	 */
	public NotifierDelegate getNotifier() {
		return notifier;
	}

	private final NotifierDelegate notifier = new NotifierDelegate(this);

	/**
	 @return The list of all standard ratings recognized by the NEC.
	 */
	public static int[] getStandardRatings() {
		return standardRatings;
	}

	/**
	 Determines the OCPD rating for the given current taking into account the
	 next higher standard rating rule.

	 @param ampacity Is the ampacity rating of a conduitable or the maximum
	 OCPD rating imposed by a load.
	 @param NHSR_Rule True if the next higher standard rating rule can be
	 applied, false otherwise.
	 @return The rating of a standard OCPD.<br> Notice the ampacity parameter
	 should correspond to:<br>
	 - The ampacity of a conduitable once corrected and adjusted, or<br>
	 - The maximum OCPD rating allowed by a load, when an OCDP is required.<br>

	 Do not use a value having a different meaning, like for example a
	 nominal current.
	 This is a general method to determine the OCPD rating based on the
	 NEC 240.4 rule. When combined with the proper meaning of the ampacity
	 parameter it offers a complete method for selecting the correct OCDP based
	 on the all articles of the NEC-2014.
	 */
	public static int getRatingFor(double ampacity, boolean NHSR_Rule) {
		int nextHigher = standardRatings[standardRatings.length - 1]; //6000
		for (int i = standardRatings.length - 1; i > 0; i--) {
			if (standardRatings[i] == ampacity)
				return standardRatings[i];
			if (standardRatings[i] < ampacity) {
				if (nextHigher > 800)
					//the NHSR_rule is overridden by NEC 240.4(B)
					return standardRatings[i];
				//the NHSR rule is accounted for
				if (NHSR_Rule)
					return nextHigher;
				else
					return standardRatings[i];
			}
			nextHigher = standardRatings[i];
		}
		return standardRatings[0]; //15 Amps
	}

	/**
	 @return The size of the EGC per table NEC-250.122. If any of the
	 parameters is invalid the return value is null.
	 @param ocpdRating Rating of the OCPD.
	 @param metal The metal of the conductor (Cu or Al)
	 */
	public static Size getEGCSize(int ocpdRating, Metal metal){
		if(metal == null)
			return null;
		if(ocpdRating == 0)
			return null;
		int index = indexOfOCPD(ocpdRating);
		if(index == -1)
			return null;
		if(metal == Metal.COPPER)
			return copperEGC[index];
		return aluminumEGC[index];
	}

	/**
	 Constructs an OCDP object that belongs to the specified circuit.
	 @param circuit The circuit that owes this OCPD object.
	 */
	public OCPD(Circuit circuit) {
		if (circuit == null)
			throw new IllegalArgumentException("Circuit parameter cannot be " +
				"null");
		this.circuit = circuit;
	}

	/**
	 @return The rating of this OCPD when owned by a circuit.
	 The rating is decided as follows: if the circuit's load has OCPD
	 requirements ({@link eecalcs.loads.Load#getMaxOCPDRating(boolean)}
	 returns a non zero value), it determines the OCPD rating per these load's
	 requirements, otherwise it determines the OCPD rating to protect the
	 circuit's conductors only, based on the ampacity of the circuit
	 conductors under all the existing conditions of installations.
	 */
	public int getRating() {
		double maxOCPD = circuit.getLoad().getMaxOCPDRating(_100PercentRated);
		//if the circuit's load has an OCPD requirement, use it!.
		if (maxOCPD != 0)
			return getRatingFor(maxOCPD, circuit.getLoad().NHSRRuleApplies());

		//No load OCPD requirement. So, select rating to protect conduitables.
		return getRatingFor(circuit.getCircuitAmpacity(),
				circuit.getLoad().NHSRRuleApplies());
	}

	/**
	 @return True if this OCPD object is 100% rated.
	 */
	public boolean is100PercentRated() {
		return _100PercentRated;
	}

	/**
	 Set this OCPD object rating percent.
	 @param flag If True, the OCPD is set as 100% rated, otherwise the OCPD
	 is set as 80% rated (the default)
	 */
	public void set100PercentRated(boolean flag) {
		if(_100PercentRated == flag)
			return;
		notifier.info.addFieldChange("_100PercentRated", _100PercentRated, flag );
		_100PercentRated = flag;
		notifier.notifyAllListeners();
	}
}
