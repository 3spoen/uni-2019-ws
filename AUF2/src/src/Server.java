import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends JFrame{

    private static Set<String> names = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();
    private static PrintWriter out; private static BufferedReader in;
    public static File users;
    private   JTextArea chatarea;
    private  JTextField onlineUsers;

    public Server(){
        onlineUsers=new JTextField();
        onlineUsers.setBackground(Color.GRAY);
        add(new JScrollPane(onlineUsers),BorderLayout.NORTH);
        chatarea=new JTextArea();
        add(new JScrollPane(chatarea),BorderLayout.CENTER);
        setSize(600,800);
        setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setSize(300,400);

    }

    public static void main(String[] args) throws IOException {
         Server server=new Server();

        try (var listener = new ServerSocket(51730)) {
            System.out.println("The game server is running...");
            users = new File("F:" + File.separator+"pro 219" +File.separator+ "AUF2" + File.separator + "src" + File.separator + "accounts.txt");
            ExecutorService pool = Executors.newFixedThreadPool(10);
            while (true) {

                pool.execute(new Handler(listener.accept(), out, in,server));
            }

        }

    }

    private static class Handler extends JFrame implements Runnable {


//       private   JTextArea chatarea;
//       private  JTextField onlineUsers;


//       private JList<String> nameliste =new JList<>();

        private Socket socket;
        private String[] listOfUsers;
        private HashSet<String> h;
        private List<String[]> records;
        private String username;
        private PrintWriter out;
        private BufferedReader in;
        private boolean permit=false;
        private Server server;



        public Handler(Socket socket, PrintWriter out, BufferedReader in,Server server) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.server=server;







//            onlineUsers=new JTextField();
//            onlineUsers.setBackground(Color.GRAY);
//            add(new JScrollPane(onlineUsers),BorderLayout.NORTH);
//            chatarea=new JTextArea();
//            add(new JScrollPane(chatarea),BorderLayout.CENTER);
//            setSize(600,800);
//            setVisible(true);
//            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//            this.pack();
//            this.setSize(300,400);

        }

        /*Handler(Socket socket) throws IOException {
            this.socket = socket;
        }*/

        @Override
        public void run() {

           server.chatarea.append("Connected: " + socket+"\n");
            String userChoice = null;
            try {


                while (true){
                    userChoice = in.readLine();
                    if (userChoice.toUpperCase().equals("REGISTER")) {
                        registerUser();
                        if (permit){
                            chat();
                            break;
                        }
                    } else if (userChoice.toUpperCase().equals("LOGIN")) {
                        logUser();
                         if (permit){
                            chat();
                            break;
                         }
                    }

                }out.println("Server is closed");
                socket.close();
                in.close();
                out.close();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void chat() throws IOException {
            server.chatarea.append(username+" has joined.\n");


            for (PrintWriter printWriter : writers) {
                printWriter.println(username + " has joined.\n");
                printWriter.flush();
            }


            //names
            synchronized (names) {
                if (!username.isBlank() && !names.contains(username)) {
                    names.add(username);
                    out.println(names);
                    server.onlineUsers.setText("");
                    server.onlineUsers.setText(names.toString());
                }
            }writers.add(out);
            // Accept messages from this client and broadcast them.
            while (true) {
                String input = in.readLine();

                if (input.toLowerCase().startsWith("sign out"))
                    break;

                if(!input.isEmpty()){

                    System.out.println("if is ok");

                    server.chatarea.append("@"+username+ " :  "+input+".\n");
                    for (PrintWriter writer : writers) {
                        writer.println("@"+ username.toUpperCase() + "@: \n" + input );
                        writer.flush();
                    }
                }


            }
            server.chatarea.append(username.toUpperCase() + "@  has left the chat.\n");
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

                while (((readUsername = in.readLine()) != null) && ((readPassword = in.readLine()) != null)) {
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

                        out.println("Login Accepted!");
                        username = readUsername;
                        permit=true;
                    } else if(userExists && names.contains(readUsername)) {
                        out.println("Username is Already logged in please try again ");

                    }else {
                        out.println("username or Password is incorrect!!!");
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
                CSVReader reader = new CSVReader(new FileReader(users));
                while (true) {
                    readUsername=in.readLine();
                    readPassword=in.readLine();

                    while ((nextRecord = reader.readNext()) != null) {
                        if (nextRecord[0].equals(readUsername)) {
                            out.println("Username is Already taken  please try again.  ");
                            userExists = true;
                            break;
                        }
                    }

                    break;
                }

                if (!userExists) {
                    String[] data = {readUsername, readPassword};

                    out.println("Registered New User");
                    username = readUsername;
                    writer.writeNext(data);
                    permit=true;
                }
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }
}