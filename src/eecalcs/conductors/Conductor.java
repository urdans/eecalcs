package eecalcs.conductors;

public class Conductor {
	//region static
	private static class TempCorrectionFactor{
		private int minTC;
		private int maxTC;
		private int minTF;
		private int maxTF;
		private double correctionFactor60;
		private double correctionFactor75;
		private double correctionFactor90;

		public TempCorrectionFactor(int minTC, int maxTC, int minTF, int maxTF, double correctionFactor60, double correctionFactor75,
		                            double correctionFactor90) {
			this.minTC = minTC;
			this.maxTC = maxTC;
			this.minTF = minTF;
			this.maxTF = maxTF;
			this.correctionFactor60 = correctionFactor60;
			this.correctionFactor75 = correctionFactor75;
			this.correctionFactor90 = correctionFactor90;
		}

		public double getCorrectionFactor(int tempRating){
			if(tempRating == 60) return correctionFactor60;
			if(tempRating == 75) return correctionFactor75;
			if(tempRating == 90) return correctionFactor90;
			return 0;
		}

		public boolean inRangeC(int ambientTempC){
			return ambientTempC >= minTC & ambientTempC <= maxTC;
		}

		public boolean inRangeF(int ambientTempF){
			return ambientTempF >= minTF & ambientTempF <= maxTF;
		}
	}
	private static TempCorrectionFactor[] tempCorrectionFactors;

	private static double getCorrectionFactorC(int ambientTemperatureC, int temperatureRating){
		for(TempCorrectionFactor tcf: tempCorrectionFactors){
			if(tcf.inRangeC(ambientTemperatureC)) return tcf.getCorrectionFactor(temperatureRating);
		}
		return 0;
	}

	private static double getCorrectionFactorF(int ambientTemperatureF, int temperatureRating){
		for(TempCorrectionFactor tcf: tempCorrectionFactors){
			if(tcf.inRangeF(ambientTemperatureF)) return tcf.getCorrectionFactor(temperatureRating);
		}
		return 0;
	}

	static {
		tempCorrectionFactors = new TempCorrectionFactor[]{
				new TempCorrectionFactor(-15, 10, 5, 50, 1.29, 1.2, 1.15),
				new TempCorrectionFactor(11, 15, 51, 59, 1.22, 1.15, 1.12),
				new TempCorrectionFactor(16, 20, 60, 68, 1.15, 1.11, 1.08),
				new TempCorrectionFactor(21, 25, 69, 77, 1.08, 1.05, 1.04),
				new TempCorrectionFactor(26, 30, 78, 86, 1, 1, 1),
				new TempCorrectionFactor(31, 35, 87, 95, 0.91, 0.94, 0.96),
				new TempCorrectionFactor(36, 40, 96, 104, 0.82, 0.88, 0.91),
				new TempCorrectionFactor(41, 45, 105, 113, 0.71, 0.82, 0.87),
				new TempCorrectionFactor(46, 50, 114, 122, 0.58, 0.75, 0.82),
				new TempCorrectionFactor(51, 55, 123, 131, 0.41, 0.67, 0.76),
				new TempCorrectionFactor(56, 60, 132, 140, 0, 0.58, 0.71),
				new TempCorrectionFactor(61, 65, 141, 149, 0, 0.47, 0.65),
				new TempCorrectionFactor(66, 70, 150, 158, 0, 0.33, 0.58),
				new TempCorrectionFactor(71, 75, 159, 167, 0, 0, 0.5),
				new TempCorrectionFactor(76, 80, 168, 176, 0, 0, 0.41),
				new TempCorrectionFactor(81, 85, 177, 185, 0, 0, 0.29),
		};
	}
	//endregion

	protected String size = "12"; //empty means invalid size
	protected Metal metal = Metal.COPPER;
	protected String insulation = "THW"; //empty means invalid insulation
	protected double length = 100;
	protected double ampacity = 0; //size or insulation is invalid
	protected int temperatureRating = 75; //zero means size or insulation is invalid
	protected int ambientTemperatureC = 30;
	protected int ambientTemperatureF = 86;
	protected boolean copperCoated = Coating.UNCOATED;

	public Conductor(String size, Metal metal, String insulation, double length) {
		this.size = CondProp.isValidSize(size) ? size : "";
		this.insulation = CondProp.isValidInsulationName(insulation)? insulation: "";
		this.metal = metal;
		this.length = Math.abs(length);
		temperatureRating = CondProp.getInsulationTemperatureCelsius(insulation);
		setAmpacity();
	}

	public Conductor(Conductor conductor) {
		this.size = conductor.size;
		this.metal = conductor.metal;
		this.insulation = conductor.insulation;
		this.length = conductor.length;
		this.ampacity = conductor.ampacity;
		this.temperatureRating = conductor.temperatureRating;
		this.ambientTemperatureC = conductor.ambientTemperatureC;
		this.ambientTemperatureF = conductor.ambientTemperatureF;
	}

	public Conductor(){
		setAmpacity();
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = CondProp.isValidSize(size) ? size : "";
		setAmpacity();
	}

	public Metal getMetal() {
		return metal;
	}

	public void setMetal(Metal metal) {
		this.metal = metal;
		setAmpacity();
	}

	private void setAmpacity(){
		if(!isValid()){
			ampacity = 0;
			return;
		}
		ampacity = CondProp.bySize(size).byMetal(metal).getAmpacity(temperatureRating);
	}

	public String getInsulation() {
		return insulation;
	}

	public void setInsulation(String insulation) {
		this.insulation = CondProp.isValidInsulationName(insulation)? insulation: "";
		temperatureRating = CondProp.getInsulationTemperatureCelsius(insulation);
		setAmpacity();
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = Math.abs(length);
	}

	public double getAmpacity(){
		return ampacity * getCorrectionFactorF(ambientTemperatureF, temperatureRating);
	}

	public boolean isValid(){
		return CondProp.isValidSize(size) & CondProp.isValidInsulationName(insulation);
	}

	public int getTemperatureRating() {
		return temperatureRating;
	}

	public double getInsulatedAreaIn2(){
		return CondProp.getInsulatedAreaIn2(size, insulation);
	}

	public double getAreaCM(){
		return CondProp.bySize(size).getAreaCM();
	}

	public int getAmbientTemperatureC() {
		return ambientTemperatureC;
	}

	public void setAmbientTemperatureC(int ambientTemperatureC) {
		this.ambientTemperatureC = ambientTemperatureC;
		this.ambientTemperatureF = (int)Math.floor(ambientTemperatureC * 1.8 + 32);
	}

	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}

	public void setAmbientTemperatureF(int ambientTemperatureF) {
		this.ambientTemperatureF = ambientTemperatureF;
		this.ambientTemperatureC = (int)Math.ceil((ambientTemperatureF - 32) * 5/9);
	}

	public boolean isCopperCoated() {
		return copperCoated;
	}

	public void setCopperCoated(boolean copperCoated) {
		this.copperCoated = copperCoated;
	}
}
