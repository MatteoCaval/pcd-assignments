package assignment1.common;

public enum States{
    IDLE("Idle"),
    STOPPED("Stopped"),
    OUT_OF_STEPS("Out of steps");

    private String stateString;

    States(String stateString){
        this.stateString = stateString;
    }

    public String getString(){
        return this.stateString;
    }

}
