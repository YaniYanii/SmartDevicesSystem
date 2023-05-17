package iotInfrustructure.gateWay.jarLoader.dirMonitor;

public class ObserverMessage {
    private final String fileName;
    private final EnumEvent event;

    ObserverMessage(String fileName, EnumEvent event){
        this.fileName = fileName;
        this.event = event;
    }

    public String getFileName() {
    return fileName;
    }

    public EnumEvent getEvent() {
        return event;
    }
}
