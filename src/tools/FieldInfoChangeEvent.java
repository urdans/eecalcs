package tools;

public class FieldInfoChangeEvent {
    public String fieldName = "";
    public Object oldValue;
    public Object newValue;

    public void setInfo(String fieldName, Object oldValue, Object newValue){
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public void clearInfo(){
        setInfo("", null, null);
    }
}
