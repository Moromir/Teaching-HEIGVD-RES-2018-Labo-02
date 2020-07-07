package ch.heigvd.res.labs.roulette.net.protocol;

public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;

    public String getStatus() {
        return status;
    }

    public int getNumberOfCommands() {
        return numberOfCommands;
    }

    public void setNumberOfCommands(int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}