import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileListMaker {
    private static ArrayList<String> list = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static boolean needsToBeSaved = false;
    private static String currentFilename = "";

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\nMenu Options:");
            System.out.println("A - Add an item to the list");
            System.out.println("D - Delete an item from the list");
            System.out.println("I - Insert an item into the list");
            System.out.println("M - Move an item in the list");
            System.out.println("V - View the current list");
            System.out.println("O - Open a list file from disk");
            System.out.println("S - Save the current list to disk");
            System.out.println("C - Clear the current list");
            System.out.println("Q - Quit the program");

            String choice = SafeInput.getRegExString(scanner, "Enter your choice", "[AaDdIiMmVvOoSsCcQq]");

            try {
                switch (choice.toUpperCase()) {
                    case "A":
                        addItem();
                        break;
                    case "D":
                        deleteItem();
                        break;
                    case "I":
                        insertItem();
                        break;
                    case "M":
                        moveItem();
                        break;
                    case "V":
                        displayList();
                        break;
                    case "O":
                        openList();
                        break;
                    case "S":
                        saveList();
                        break;
                    case "C":
                        clearList();
                        break;
                    case "Q":
                        running = !confirmQuit();
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayList() {
        if (list.isEmpty()) {
            System.out.println("\nThe list is currently empty");
        } else {
            System.out.println("\nCurrent List:");
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, list.get(i));
            }
        }
    }

    private static void addItem() {
        String item = SafeInput.getNonZeroLenString(scanner, "Enter the item to add");
        list.add(item);
        needsToBeSaved = true;
        System.out.printf("'%s' added to the list%n", item);
    }

    private static void deleteItem() {
        if (list.isEmpty()) {
            System.out.println("The list is empty - nothing to delete");
            return;
        }

        displayList();
        int itemNum = SafeInput.getRangedInt(scanner, "Enter the item number to delete", 1, list.size());
        String removedItem = list.remove(itemNum - 1);
        needsToBeSaved = true;
        System.out.printf("'%s' removed from the list%n", removedItem);
    }

    private static void insertItem() {
        if (list.isEmpty()) {
            System.out.println("List is empty - adding item at position 1");
            addItem();
            return;
        }

        displayList();
        int position = SafeInput.getRangedInt(scanner, "Enter the position to insert at", 1, list.size() + 1);
        String item = SafeInput.getNonZeroLenString(scanner, "Enter the item to insert");
        list.add(position - 1, item);
        needsToBeSaved = true;
        System.out.printf("'%s' inserted at position %d%n", item, position);
    }

    private static void moveItem() {
        if (list.isEmpty()) {
            System.out.println("The list is empty - nothing to move");
            return;
        }

        displayList();
        int currentPos = SafeInput.getRangedInt(scanner, "Enter the item number to move", 1, list.size());
        int newPos = SafeInput.getRangedInt(scanner, "Enter the new position for this item", 1, list.size());

        String item = list.remove(currentPos - 1);
        list.add(newPos - 1, item);
        needsToBeSaved = true;
        System.out.printf("Moved item '%s' from position %d to position %d%n", item, currentPos, newPos);
    }

    private static void clearList() {
        if (!list.isEmpty()) {
            boolean confirm = SafeInput.getYNConfirm(scanner, "Are you sure you want to clear the list?");
            if (confirm) {
                list.clear();
                needsToBeSaved = true;
                System.out.println("List cleared");
            }
        } else {
            System.out.println("List is already empty");
        }
    }

    private static void openList() throws IOException {
        if (needsToBeSaved) {
            boolean saveFirst = SafeInput.getYNConfirm(scanner, "Current list has unsaved changes. Save first?");
            if (saveFirst) {
                saveList();
            }
        }

        String filename = SafeInput.getNonZeroLenString(scanner, "Enter filename to open (without .txt extension)");
        Path filePath = Paths.get(filename + ".txt");

        if (Files.exists(filePath)) {
            list = new ArrayList<>(Files.readAllLines(filePath));
            currentFilename = filename;
            needsToBeSaved = false;
            System.out.printf("List loaded from %s.txt%n", filename);
            displayList();
        } else {
            System.out.println("File not found. Starting with empty list.");
            list.clear();
            currentFilename = filename;
            needsToBeSaved = false;
        }
    }

    private static void saveList() throws IOException {
        if (list.isEmpty()) {
            System.out.println("List is empty - nothing to save");
            return;
        }

        String filename;
        if (currentFilename.isEmpty()) {
            filename = SafeInput.getNonZeroLenString(scanner, "Enter filename to save as (without .txt extension)");
            currentFilename = filename;
        } else {
            filename = currentFilename;
            System.out.printf("Saving to %s.txt%n", filename);
        }

        Path filePath = Paths.get(filename + ".txt");
        Files.write(filePath, list);
        needsToBeSaved = false;
        System.out.printf("List saved to %s.txt%n", filename);
    }

    private static boolean confirmQuit() {
        if (needsToBeSaved) {
            boolean save = SafeInput.getYNConfirm(scanner, "You have unsaved changes. Save before quitting?");
            if (save) {
                try {
                    saveList();
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
        }

        boolean quit = SafeInput.getYNConfirm(scanner, "Are you sure you want to quit?");
        if (quit) {
            System.out.println("Goodbye!");
        }
        return quit;
    }
}