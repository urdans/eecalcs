package eecalcs.conductors;

import java.util.List;

public class Conduit {
    private String tradeSize;
    private List<Circuit> circuitList;
    private List<Conductor> conductorList;

    public Conduit(String tradeSize) {
    }

    //returning a negative number: error; positive: warning; zero: ok
    public int add(Circuit circuit){
        return 0;
    }

//    public int add(Circuit circuit, boolean locking){
//        return 0;
//    }

    public void remove(Circuit circuit){

    }

    public int add(Conductor conductor){
        return 0;
    }

    public void remove(Conductor conductor){

    }

    public String getTradeSize() {
        return tradeSize;
    }

    public List<Conductor> getConductorList() {
        return conductorList;
    }


}
