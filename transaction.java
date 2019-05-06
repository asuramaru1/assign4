public class transaction {
    private final int from;
    private final int to;
    private final int amount;

    public transaction(int from, int to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}