
package logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David Morales
 */
public class NodoDistribuido extends Thread{
    ArrayList<String> ips;
    public static final int PORT=4000;
    private ServerSocket serverSocket;
    private ArrayList<Conexion> conexiones;
    private boolean activo=true;
    private int puerto;
    
    public NodoDistribuido(int port,String serverIp) throws IOException {
        serverSocket = new ServerSocket(port);
        puerto=port;
        ips = new ArrayList<String>();
        conexiones = new ArrayList<Conexion>();
        this.start();
        
    }
    
    public String ipsAsStrings(){
        String r="";
        for (int i = 0; i < ips.size(); i++) {
            r+=ips.get(i);
            if(i!=ips.size()-1)r+=",";
        }
        return r;
    }
    
    public void agregarConexion(Socket socket) throws IOException{
        Conexion nueva=new Conexion(socket,this);
        conexiones.add(nueva);
        
        nueva.start();
        nueva.enviarMensaje("setIps;"+ipsAsStrings());
        
        ips.add(socket.getInetAddress().getHostAddress());
    }
    
    public void addIp(String ipString) throws UnknownHostException, IOException{
        for (String ip : ipString.split(",")) {
            agregarConexion(new Socket(ip, puerto));
        }
    }
    
    public void procesar(String comando,Conexion conexion){
        String com = comando.split(";")[0];
        if(com.equals("setIps")){
            try {
                addIp(comando.split(";")[1]);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else if(com.equals("getIps")){
            conexion.enviarMensaje("setIps;"+ipsAsStrings());
        }
    }

    @Override
    public void run() {
        while(activo){
            try {
                agregarConexion(serverSocket.accept());
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    } 
}
