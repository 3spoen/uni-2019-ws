import java.io.*;
import java.util.Formatter;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Registration Page");
        System.out.println("NOTE: your username is a unique one so it cannot be changed.");
        System.out.printf("Username: ");
        String user = sc.nextLine().toUpperCase();


        System.out.printf("Password: ");
        String pass = sc.nextLine();


//        int passInt = Integer.parseInt(pass);

//        BufferedReader zz = null;
//
//            zz = new BufferedReader(new FileReader("F:\\AUF2\\src\\accounts.txt"));
//            Scanner z= new Scanner(zz);
                Formatter x = null;
        File file = new File("F:" + File.separator + "AUF2" + File.separator + "src" + File.separator + "accounts.txt");

        try {
                    FileWriter f = new FileWriter(file.getAbsoluteFile(), true);
                    x = new Formatter(f);
                    x.format("%s %s%n", user.toUpperCase(), pass);
                    System.out.println("You registered succesfully");
                    x.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }




            System.out.println("Password and confirm password are not matching");
        }
    }


