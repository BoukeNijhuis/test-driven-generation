package nl.boukenijhuis.dto;

public final class PreviousRunContainer {
    private String input;

    public PreviousRunContainer(String input) {
        this.input = input;
    }

    public PreviousRunContainer() {
        this("");
    }

    public String getInput() {
        return input;
    }
}
