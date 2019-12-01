package test;

public class Tools {
    public static void printTitle(String title) {
        String top = new String(new char[title.length()+2]).replace("\0", "-");
        String line1 = "/" + top + "\\";
        String line2 = "| " + title + " |";
        String line3 = "\\" + top + "/";
        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line3);
    }

    public static void print(String msg) {
        System.out.println(msg);
    }
}
