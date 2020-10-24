package eecalcs.loads;

import tools.NotifierDelegate;

/**
This class implements the continuousness behavior that a load will have. This
 class acts as a helper class and must be used in composition with other
 classes.
 */
public class ContinuousBehavior implements Continuousness{
	private LoadType loadType = LoadType.NONCONTINUOUS;
	private NotifierDelegate notifierDelegate;
	private final Currents currents;

	/**
	 Constructs this class by initializing it with a Currents object.
	 @param currents The object encapsulating values for a nominal current
	 and an MCA.
	 @see Currents
	 */
	public ContinuousBehavior(Currents currents){
		if(currents == null)
			throw new IllegalArgumentException("Currents parameter cannot be null");
		this.currents = currents;
	}

	/**
	 Sets the notifier delegate for this class. As this class makes
	 modifications to the MCA and its continuousness, it needs to notify the
	 registered listeners of its owner class. It does it by using the
	 NotifierDelegate object belonging to its owner class.
	 @param notifierDelegate The owner's class notifier delegate to which
	 this class will have access to.
	 */
	public void setNotifierDelegate(NotifierDelegate notifierDelegate) {
		this.notifierDelegate = notifierDelegate;
	}

	@Override
	public LoadType getLoadType() {
		return loadType;
	}

	@Override
	public void setContinuous() {
		if(loadType == LoadType.CONTINUOUS)
			return;
		_setContinuousness(LoadType.CONTINUOUS);
	}

	/*Sets the new behavior of the load and notifies its listeners about it*/
	private void _setContinuousness(LoadType loadType){

		LoadType oldLoadType = this.loadType == loadType ? null :	this.loadType;
		double oldMCA = currents.MCA;
		this.loadType = loadType;
		if(loadType == LoadType.CONTINUOUS)
			currents.MCA = 1.25 * currents.nominalCurrent;
		else
			currents.MCA = currents.nominalCurrent;
		if(notifierDelegate != null) {
			if(oldLoadType != null)
				notifierDelegate.info.addFieldChange("loadType", oldLoadType, this.loadType);
			notifierDelegate.info.addFieldChange("MCA", oldMCA, currents.MCA);
			notifierDelegate.notifyAllListeners();
		}
	}

	@Override
	public void setNonContinuous() {
		if(loadType == LoadType.NONCONTINUOUS)
			return;
		_setContinuousness(LoadType.NONCONTINUOUS);
	}

	@Override
	public void setMixed(double MCA) {
		if(MCA == currents.MCA)
			return;
		if(MCA <= currents.nominalCurrent) { //this should never happen
			setNonContinuous();
			return;
		}
		LoadType oldLoadType = loadType;
		loadType = LoadType.MIXED;
		double oldMCA = currents.MCA;
		currents.MCA = MCA;
		if(notifierDelegate != null) {
			notifierDelegate.info.addFieldChange("loadType", oldLoadType, loadType);
			notifierDelegate.info.addFieldChange("MCA", oldMCA, currents.MCA);
			notifierDelegate.notifyAllListeners();
		}
	}

	/**
	 Indicates to this object that the nominal current of the load it belongs
	 to, has changed.
	 */
	public void updatedNominalCurrent(){
		if(loadType == LoadType.MIXED)
			_setContinuousness(LoadType.NONCONTINUOUS);
		else
			_setContinuousness(loadType);

	}

}
