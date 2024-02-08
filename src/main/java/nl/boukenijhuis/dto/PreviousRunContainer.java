package nl.boukenijhuis.dto;

public record PreviousRunContainer(
        String input
) {

    public PreviousRunContainer() {
        this("");
    }

    public PreviousRunContainer updateInput(String input) {
        return new PreviousRunContainer(input);
    }
}
