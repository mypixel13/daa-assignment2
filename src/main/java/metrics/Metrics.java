package metrics;

// same idea as in assignment 1 - a little counter object we pass into every
// method instead of using static/global counters, so different runs don't mix
public class Metrics {

    public long steps = 0;       // one array-cell read, or one hop to the next linked list node
    public long moves = 0;       // one element shifted in the array, or one pointer/link update
    public long comparisons = 0; // one comparison between two elements

    public void step() {
        steps++;
    }

    public void move() {
        moves++;
    }

    public void compare() {
        comparisons++;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }

    @Override
    public String toString() {
        return "steps=" + steps + ", moves=" + moves + ", comparisons=" + comparisons;
    }
}
