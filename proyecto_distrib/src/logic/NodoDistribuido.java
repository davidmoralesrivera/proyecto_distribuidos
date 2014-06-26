
package logic;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

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
    private String ipServer;
    
    public NodoDistribuido(int port,String serverIp) throws IOException {
        serverSocket = new ServerSocket(port);
        puerto=port;
        ips = new ArrayList<String>();
        conexiones = new ArrayList<Conexion>();
        this.start();
        ipServer=serverIp;
        if(!serverIp.equals("")){
            agregarConexion(new Socket(serverIp,port));
        }
       
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
        

        if(ipServer.isEmpty() && !ips.isEmpty()){

            nueva.enviarMensaje("setIps;"+ipsAsStrings());
        }
        
        ips.add(socket.getInetAddress().getHostAddress());
    }
    
    public void addIp(String ipString) throws UnknownHostException, IOException{
        for (String ip : ipString.split(",")) {
            agregarConexion(new Socket(ip, puerto));
        }
    }
    
    public synchronized void recibirArchivo(Conexion conexion,String t) throws FileNotFoundException, IOException{
        System.out.println("recibiendo archivo, "+t+ " bytes");
        byte[] mybytearray = new byte[Integer.parseInt(t)];
        InputStream is = conexion.getSocket().getInputStream();
        JFileChooser j=new JFileChooser();
        j.showSaveDialog(null);
        FileOutputStream fos = new FileOutputStream(j.getSelectedFile().getPath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        System.out.println(bytesRead);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
    }
    
    public synchronized void enviarArchivo(Conexion conexion) throws FileNotFoundException, IOException{
        JFileChooser j=new JFileChooser();
        j.showOpenDialog(null);
        
        File myFile = j.getSelectedFile();
        conexion.enviarMensaje("setFile;"+myFile.length());
        byte[] mybytearray = new byte[(int) myFile.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = conexion.getSocket().getOutputStream();
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
    }
    
    public synchronized void procesar(String comando,Conexion conexion){
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
        }else if(com.equals("getFile")){
            try {
                enviarArchivo(conexion);
                
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else if(com.equals("setFile")){
            try {
                recibirArchivo(conexion,comando.split(";")[1]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Iniciado...");
        while(activo){
            try {
                
                agregarConexion(serverSocket.accept());
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public ArrayList<Conexion> getConexiones() {
        return conexiones;
    }

    public void setConexiones(ArrayList<Conexion> conexiones) {
        this.conexiones = conexiones;
    }

    public ArrayList<String> getIps() {
        return ips;
    }

    public void setIps(ArrayList<String> ips) {
        this.ips = ips;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    
}
