import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static PrintWriter out;
    private static BufferedReader in;
    public static File users;

    public static void main(String[] args) throws Exception {

        try (var listener = new ServerSocket(51730)) {
            System.out.println("The game server is running...");
            users = new File("D:" + File.separator + "AUF2" + File.separator + "uni-2019-ws" + File.separator + "AUF2" + File.separator + "src" + File.separator + "accounts.txt");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {
                pool.execute(new Handler(listener.accept(), out, in));
            }
        }

    }

    private static class Handler implements Runnable {
        private Socket socket;
        private String[] listOfUsers;
        private HashSet<String> h;
        private List<String[]> records;
        private String username;
        private PrintWriter out;
        private BufferedReader in;

        public Handler(Socket socket, PrintWriter out, BufferedReader in) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }


        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            String userChoice = null;
            try {
                userChoice = in.readLine();
                if (userChoice.toUpperCase().equals("REGISTER")) {
                    registerUser();
                } else if (userChoice.toUpperCase().equals("LOGIN")) {
                    logUser();
                }  else
                    run();


                chat();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void list() {
            out.println(names);
        }

        public void chat() throws IOException {
            System.out.println(socket + "has joined the chat");
            writers.add(out);
            for (PrintWriter printWriter : writers) {
                printWriter.println(username + " has joined");
                printWriter.flush();
            }
            //The synchronization is mainly used to
            //To prevent thread interference.
            //To prevent consistency problem.
            synchronized (names) {
                try {
                    if (!username.isBlank() && !names.contains(username)) {
                        names.add(username);
                        out.println(names);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            // Accept messages from this client and broadcast them.
            while (true) {
                String input = in.readLine();
                if (input.toLowerCase().startsWith("sign out"))
                    break;
                if (input.toLowerCase().startsWith("list"))
                    list();
                for (PrintWriter writer : writers) {
                    writer.println("[" + username + "]" + ": " + input);
                    writer.flush();
                }
            }
            System.out.println(socket + "has left the chat");
            names.remove(username);
            for (PrintWriter writer : writers) {
                writer.println(username + "has left the chat");
                writer.flush();
            }
            writers.remove(out);
        }


        public void logUser() throws IOException {
            try (
                    // create CSVWriter object filewriter object as parameter
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));
            ) {
                String readUsername;
                String readPassword;
                boolean userExists = false;
           out.println("Please Enter  Username and Password");

                while (((readUsername = in.readLine()) != null) &&
                        ((readPassword = in.readLine()) != null)) {
                    String[] nextRecord;
                    CSVReader reader = new CSVReader(new FileReader(users));
// to make the userExists in the right value
                    while ((((nextRecord = reader.readNext())) != null)) {
                        if (nextRecord[0].equals(readUsername)) {
                            if (nextRecord[1].equals(readPassword)) {
                                userExists = true;
                            }
                        }
                    }
                        if (userExists && !names.contains(readUsername)) {

                            out.println(readUsername + " Login Accepted!");
                            username = readUsername;
                            System.out.println("Client: " + socket + " logged in with username " + readUsername);
                        } else {
                            out.println("\nUsername is Already logged in please try again \n Register or Login? ");
                            run();
                        }
                        break;


                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        }


        public void registerUser() throws IOException {
            try (
                    // create CSVWriter object filewriter object as parameter
                    CSVWriter writer = new CSVWriter(new FileWriter(users.getAbsoluteFile(), true));
            ) {
                String readUsername;
                String readPassword;
                boolean userExists = false;
                String[] nextRecord;
               out.println("Please Enter  Username and Password");
                CSVReader reader = new CSVReader(new FileReader(users));
                while (((readUsername = in.readLine()) != null) &&
                        ((readPassword = in.readLine()) != null && !userExists)) {

                    while ((nextRecord = reader.readNext()) != null) {
                        if (nextRecord[0].equals(readUsername)) {
                            out.println("\nUsername is Already taken  please try again \n Register or Login? ");
                            userExists = true;
                            run();
                        }
                    }
                    if (!userExists) {
                        String[] data = {readUsername, readPassword};
                        System.out.println(socket );
                        out.println("Registered New User");
                        username = readUsername;
                        writer.writeNext(data);
                        break;
                    }
                }

            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }
}