package client;

public class Data {
    private String number;
    private String cowBull;

    Data(String number, String cowBull) {
        this.number = number;
        this.cowBull = cowBull;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCowBull() {
        return cowBull;
    }

    public void setCowBull(String cowBull) {
        this.cowBull = cowBull;
    }
}