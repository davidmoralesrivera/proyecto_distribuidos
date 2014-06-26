/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author RA302
 */
public class Nodo extends Thread {

    public static int PORT = 5000;
    ServerSocket serverSocket;
    int port;
    Socket socket;
    ArrayList<String> ips;
    boolean activo;
    ArrayList<Socket> clientes;
    GregorianCalendar hora;

    public Nodo(String ip_sever, int port) throws IOException {
        clientes = new ArrayList<Socket>();
        activo = false;
        ips=new ArrayList<>();
        this.port = port;
        if (ip_sever.isEmpty()) {
            activo = true;
            iniciarServidor();
            
        } else {
            socket = new Socket(ip_sever, port);
            iniciarCliente();
        }
    }
    public void setStringHora(String h){
        hora = new GregorianCalendar();
        hora.setTime(new Date(Long.parseLong(h)));
    }
   

    public void iniciarCliente() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    DataInputStream in;
                    try {
                        in = new DataInputStream(socket.getInputStream());
                        String p[] = in.readUTF().split(";");
                        if (p[0].equals("setIps")) {
                            addIps(p[1]);
                            System.out.println("Ips:"+ips);
                        }else if(p[0].equals("hora")){
                            setStringHora(p[1]);
                            System.out.format("servidor: %d:%d:%d:%d\n", hora.get(GregorianCalendar.HOUR_OF_DAY),hora.get(GregorianCalendar.MINUTE),hora.get(GregorianCalendar.SECOND),hora.get(GregorianCalendar.MILLISECOND));
        
                        }else if(p[0].equals("setServ")){
                            port=Integer.parseInt(p[1]);
                            for (int i = 0; i < ips.size(); i++) {
                                clientes.add(new Socket(ips.get(i), port));
                            }
                            iniciarServidor();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }).start();

    }

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(port);
        this.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.print("Bajar servidor (y/n): ");
                Scanner sc = new Scanner(System.in);
                String s=sc.nextLine();
                if(s.equals("y")){
                    for (int i = 0; i < clientes.size(); i++) {
                        DataOutputStream o;
                        DataInputStream in;
                        try {
                            o = new DataOutputStream(clientes.get(i).getOutputStream());
                            in=new DataInputStream(clientes.get(i).getInputStream());
                            o.writeUTF("pedirServ");
                            String res=in.readUTF();
                            if(!res.isEmpty()){
                                enviarMensaje(clientes.get(i), "setServ;"+res);
                                
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        
                    }
                }
            }
        }).start();
    }

    public void addIps(String list) {
        String s[] = list.split(",");
        ips.addAll(Arrays.asList(s));
    }

    public void enviarMensaje(Socket s, String msj) throws IOException {
        DataOutputStream o = new DataOutputStream(s.getOutputStream());
        o.writeUTF(msj);
    }

    public String getIpsAsString() {
        String r = "";
        for (int i = 0; i < ips.size(); i++) {
            r += ips.get(i);
            if (i != ips.size() - 1) {
                r += ",";
            }
        }
               
        return r;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                Socket aux = serverSocket.accept();
                DataInputStream in = new DataInputStream(aux.getInputStream());
                DataOutputStream out = new DataOutputStream(aux.getOutputStream());
                if(!ips.isEmpty()){
                    out.writeUTF("setIps;"+getIpsAsString());
                }
                for (int i = 0; i < clientes.size(); i++) {
                    enviarMensaje(clientes.get(i), "setIps;" + aux.getInetAddress().getHostAddress());
                }
                addIps(aux.getInetAddress().getHostAddress());
                out.writeUTF("hora;"+new GregorianCalendar().getTime().getTime());
                clientes.add(aux);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
