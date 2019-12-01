package eecalcs.conduits;

/**
 Enum structure for the type of conduit recognized by the NEC.
 <br><br>
 <p><b>EMT</b>: Electrical Metallic Tubing;
 <p><b>ENT</b>: Electrical Nonmetallic Tubing;
 <p><b>FMC</b>: Flexible Metal Conduit;
 <p><b>IMC</b>: Intermediate Metal Conduit;
 <p><b>LFNCA</b>: Liquidtight Flexible Nonmetallic Conduit type A;
 <p><b>LFNCB</b>: Liquidtight Flexible Nonmetallic Conduit type A;
 <p><b>LFMC</b>: Liquidtight Flexible Metal Conduit;
 <p><b>RMC</b>: Rigid Metal Conduit;
 <p><b>PVC80</b>: Rigid Polyvinyl Chloride Conduit, schedule 80;
 <p><b>PVC40</b>: Rigid Polyvinyl Chloride Conduit, schedule 40;
 <p><b>HDPE</b>: High Density Polyethylene Conduit;
 <p><b>PVCA</b>: Rigid Polyvinyl Chloride Conduit, type A;
 <p><b>PVCEB</b>: Rigid Polyvinyl Chloride Conduit, type B;
 <p><b>EMTAL</b>: Electrical Metallic Tubing, Aluminum;
 <p><b>FMCAL</b>: Flexible Metal Conduit, Aluminum;
 <p><b>LFMCAL</b>: Liquidtight Flexible Metal Conduit, Aluminum;
 <p><b>RMCAL</b>: Rigid Metal Conduit, Aluminum;
 */
public enum Type {
    EMT("EMT"),
    ENT("ENT"),
    FMC("FMC"),
    IMC("IMC"),
    LFNCA("LFNC-A"),
    LFNCB("LFNC-B"),
    LFMC("LFMC"),
    RMC("RMC"),
    PVC80("PVC-80"),
    PVC40("PVC-40"),
    HDPE("HDPE"),
    PVCA("PVC-A"),
    PVCEB("PVC-EB"),
    EMTAL("EMT-AL"),
    FMCAL("FMC-AL"),
    LFMCAL("LFMC-AL"),
    RMCAL("RMC-AL");

    private String name;
    private static String[] names;

    static{
        names = new String[values().length];
        for(int i=0; i<values().length; i++)
            names[i] = values()[i].getName();
    }

    Type(String name){
        this.name = name;
    }

    /**
     * Returns the string name that this enum represents.
     * @return The string name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an array of the string names that the enum values represent
     * @return An array of strings
     */
    public static String[] getNames(){
        return names;
    }
}

