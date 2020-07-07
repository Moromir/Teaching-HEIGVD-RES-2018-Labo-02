package ch.heigvd.res.labs.roulette.net.protocol;

public class LoadCommandResponse {
    private String status;
    private int numberOfNewStudents;

    public String getStatus() {
        return status;
    }

    public int getNumberOfNewStudents() {
        return numberOfNewStudents;
    }

    public void setNumberOfNewStudents(int numberOfNewStudents) {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}