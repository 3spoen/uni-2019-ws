import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    final static int ServerPort = 51730;

    public static void main(String [] args) throws UnknownHostException, IOException {
        Scanner scn = new Scanner(System.in);
        // establish the connection
        Socket s = new Socket("127.0.0.1", ServerPort);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);

        String username;
        String password;
        String login;

        //Ask User to Register or Login and write choice to output stream
        System.out.println("/register " + "\n" + "/login");
        String userChoice = scn.nextLine().toUpperCase();
        if (userChoice.equals("LOGIN") || userChoice.equals("REGISTER"))
            System.out.println("Please Enter  Username and Password");

        out.println(userChoice);

        while ((username = scn.nextLine()) != null
                && (password = scn.nextLine()) != null) {
            out.println(username);
            out.println(password);
            login = in.readLine();

            if (login.equals("true")) {
                System.out.println(in.readLine());
                break;
            }
            else
                System.out.println(in.readLine());
        }

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // read the message to deliver.
                    String msg = scn.nextLine();
                    // write on the output stream
                    out.println(msg);
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = in.readLine();
                        if(msg==null)
                            System.exit(1);
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
