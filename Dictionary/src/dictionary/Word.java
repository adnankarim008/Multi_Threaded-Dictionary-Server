package dictionary;

public class Word {
    public String name;
    public String definition;

    @Override
    public String toString() {
        return name + " - " + definition;
    }
}
