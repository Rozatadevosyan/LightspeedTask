
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> books = new ArrayList<>();
        books.add("1984");
        books.add("New World");

        Man originalMan = new Man("John", 30, books);
        Man copiedMan = CopyUtils.deepCopy(originalMan);

        System.out.println("Original Man: " + originalMan.getName() + ", " + originalMan.getAge() + ", " + originalMan.getFavoriteBooks());
        System.out.println("Copied Man: " + copiedMan.getName() + ", " + copiedMan.getAge() + ", " + copiedMan.getFavoriteBooks());

        copiedMan.setName("Jane");
        copiedMan.getFavoriteBooks().add("Fahrenheit 451");

        System.out.println("Original Man after modifications to copy: " + originalMan.getName() + ", " + originalMan.getAge() + ", " + originalMan.getFavoriteBooks());
        System.out.println("Copied Man after modifications: " + copiedMan.getName() + ", " + copiedMan.getAge() + ", " + copiedMan.getFavoriteBooks());
    }
}
