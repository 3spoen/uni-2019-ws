
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static javax.swing.JOptionPane.showMessageDialog;

public class Client extends JFrame {

    final static int ServerPort = 51730;
    private  JTextField userText;
    private  JTextArea  chatarea;
    private JButton login;
    private JButton register;
    private JTextField userName;
    private JTextField userBasword;
    private JButton logOut;
    private JLabel namelabel;
    private JLabel passwordlabel;
    private JFrame frame;
   public String readuserName;
   public String readPassword;
    private  JScrollPane scroller;




   private Socket s = new Socket("127.0.0.1", ServerPort);
   private BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
   private PrintWriter out = new PrintWriter(s.getOutputStream(), true);
   private String msg;



    public Client()throws IOException{

        super("Chat");

        frame=new JFrame();

        namelabel=new JLabel("Username:");
        namelabel.setBounds(1,1,70,25);
        namelabel.setBackground(Color.DARK_GRAY);

        frame.add(namelabel);

        userName=new JTextField();
        userName.setBounds(80,1,300,25);
        frame.add(userName);


        register=new JButton("Register");
        register.setBounds(385,1,90,25);
        frame.add(register);
        register.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        out.println("REGISTER");

                        readuserName=userName.getText();


                        readPassword=userBasword.getText();

                        try {counacction();}catch (IOException ex){ex.printStackTrace();}




                    }
                }
        );

        passwordlabel=new JLabel("Password:");
        passwordlabel.setBounds(1,30,70,25);
        passwordlabel.setBackground(Color.DARK_GRAY);
        frame.add(passwordlabel);

        userBasword=new JTextField();
        userBasword.setBounds(80,30,300,25);
        frame.add(userBasword);

        login=new JButton("login");
        login.setBounds(385,30,90,25);
        frame.add(login);
        login.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        out.println("LOGIN");

                        readuserName=userName.getText();



                        readPassword=userBasword.getText();

                        try {counacction();}catch (IOException ex){ex.printStackTrace();}

                    }
                }
        );


        userText=new JTextField();
        userText.setBounds(1,58,377,25);
        frame.add(userText);
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        msg=userText.getText();
                        out.println(msg);
                        userText.setText("");

                    }
                }
        );



        logOut=new JButton("Sing out");
        logOut.setBounds(385,58,90,25);
        logOut.setBackground(Color.red);
        frame.add(logOut);
        logOut.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            out.println("sign out");

                            out.close();
                            in.close();
                            s.close();

                            userBasword.setEditable(false);
                            userName.setEditable(false);
                            userText.setEditable(false);
                            chatarea.setEditable(false);

                        }catch (IOException exeption){
                            exeption.printStackTrace();
                        }
                    }
                }
        );
        chatarea=new JTextArea();
        chatarea.setEditable(false);
        frame.setLayout(null);
        scroller=new JScrollPane(chatarea);
        scroller.setBounds(1,85,472,475);
        frame.add(scroller);


        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(493,600);
        frame.setBackground(Color.DARK_GRAY);
        frame.setVisible(true);

    }

//    Thread sendMessage = new Thread(new Runnable() {
//        @Override
//        public void run() throws NullPointerException{
//            while (true) {
//                // write on the output stream
//               out.println(msg);
//
//            }
//        }
//    });

    Thread readMessage = new Thread(new Runnable() {
        @Override
        public void run()  {

            while (true) {
                try {
                    // read the message sent to this client
                    String msg = in.readLine();
                    if(msg==null)
                        System.exit(1);
                    SwingUtilities.invokeLater(
                            new Runnable() {
                                @Override
                                public void run() {if(!msg.isEmpty()) {
                                    chatarea.append(msg+"\n");
                                }
                                }
                            }
                            );
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    });

    private void counacction()throws IOException{
        String  infoFromServer ;

        while (readuserName!=null && readPassword!=null){

              out.println(readuserName);
              out.println(readPassword);
              infoFromServer = in.readLine();

            if (infoFromServer.equals("Login Accepted!")||infoFromServer.equals("Registered New User")) {



                userText.setEditable(true);
                chatarea.setEditable(true);
                userName.setText("");
                userName.setEditable(false);
                userBasword.setText("");
                userBasword.setEditable(false);


                showMessageDialog(null,infoFromServer);


//                sendMessage.start();
                readMessage.start();

                break;

            } else{

                showMessageDialog(null,infoFromServer);
                readuserName=null;
                userName.setText("");
                readPassword=null;
                userBasword.setText("");
                break;

           }
        }
    }



    public static void main(String [] args) throws  IOException {
     new Client();
    }

}
//
//    public void sendMessag(String actioncomand){
//
//        try{
//            SwingUtilities.invokeLater(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            out.
//                        }
//                    }
//            );
//        }catch (IOException e){}
//    }
//
//
//    public  void abelTOType(boolean trORfa){
//        SwingUtilities.invokeLater(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        userText.setEditable(trORfa);
//                    }
//                }
//        );
//    }
//
//    public static void main(String [] args) throws UnknownHostException, IOException {
//
////        JFrame frame=new ClientGui("user interface");
////        frame.setVisible(true);
//
//
//
//        Scanner scn = new Scanner(System.in);
//        // establish the connection
//        Socket s = new Socket("127.0.0.1", ServerPort);
//
//
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(s.getInputStream()));
//        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
//
//        String username;
//        String password;
//        String login;
//
//        //Ask User to Register or Login and write choice to output stream
//        System.out.println("register \n  or \nlogin");
//        String userChoice = scn.nextLine().toUpperCase();
//
//        while (userChoice!=null) {
//            if(!userChoice.equals("LOGIN") || !userChoice.equals("REGISTER")){
//                System.out.println("Invalid Input, try again please");
//                System.out.println("register " + "\n" + "login");
//                userChoice = scn.nextLine().toUpperCase();
//            }if (userChoice.equals("LOGIN") || userChoice.equals("REGISTER")) {
//                System.out.println("Please Enter  Username and Password");
//                out.println(userChoice);
//                break;
//            }
//        }
//        while ((username = scn.nextLine()) != null
//                && (password = scn.nextLine()) != null) {
//            out.println(username);
//            out.println(password);
//            login = in.readLine();
//
//            if (login.equals(" Login Accepted!")||login.equals("Registered New User")) {
//                System.out.println(in.readLine());
//                break;
//            }
//            else if(login.equals("\nUsername is Already logged in please try again \n Register or Login? ")||login.equals("\nUsername is Already taken  please try again \n Register or Login? "))
//                System.out.println("\nInvalid Username or Password \n please try again");
//            break;
//        }
//
//        // sendMessage thread
//        Thread sendMessage = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    // read the message to deliver.
//                    String msg = scn.nextLine();
//                    // write on the output stream
//                    out.println(msg);
//                }
//            }
//        });
//
//        // readMessage thread
//        Thread readMessage = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while (true) {
//                    try {
//                        // read the message sent to this client
//                        String msg = in.readLine();
//                        if(msg==null)
//                            System.exit(1);
//                        System.out.println(msg);
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        readMessage.start();
//        sendMessage.start();
//
//
//    }



