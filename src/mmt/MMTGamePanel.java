/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author harlan.howe
 */
public class MMTGamePanel extends JPanel implements KeyListener
{
    private boolean AisDown,SisDown,DisDown,WisDown;
    private String name;
    private int myId;
    
    private Socket mySocket;
    private final String ServerIP = "172.16.218.183";
    private Scanner mySocketScanner;
    private PrintWriter mySocketWriter;
    
    public MMTGamePanel()
    {
        super();
        AisDown = false;
        SisDown = false;
        DisDown = false;
        WisDown = false;
        
        do
        {
            name = JOptionPane.showInputDialog("What is your name?");
        }while (name.equals(""));
        
        setupNetwork();
    }
    
    public void setupNetwork()
    {
        try
        {
            mySocket = new Socket(ServerIP,5000);
            mySocketScanner =  new Scanner(mySocket.getInputStream());
            mySocketWriter = new PrintWriter(mySocket.getOutputStream());
            
            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();
            
            mySocketWriter.println(name);
            mySocketWriter.flush();
            
            
        }
        catch (IOException ioe)
        {
            System.out.println("I couldn't connect.");
            ioe.printStackTrace();
        }
    }
    
    public int getBinaryForKeys()
    {
        int result = 0;
        if (WisDown)
            result+=1;
        if (DisDown)
            result+=2;
        if (SisDown)
            result+=4;
        if (AisDown)
            result+=8;
        return result;
    }
    /** 
     * detects when a key is pressed AND released. One of the required methods
     * in the KeyListener interface.
     * @param e 
     */
    @Override
    public void keyTyped(KeyEvent e) {
        ;//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * detects when a key is first pressed. One of the required methods in the
     * KeyListener interface.
     * @param e 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar()=='a') // notice... lower case, and this method only
            AisDown = true;      //    works with alphanumeric keys. If you 
        if (e.getKeyChar()=='s') //    want the arrow keys, you'll need to use 
            SisDown = true;      //    getKeyCode() instead. (See API.)
        if (e.getKeyChar()=='d')
            DisDown = true;
        if (e.getKeyChar()=='w')
            WisDown = true;
       
        
       
    }
    /**
     * detects when a key is let up by the user. One of the required methods
     * for the KeyListener interface.
     * @param e 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar()=='a')
            AisDown = false;
        if (e.getKeyChar()=='s')
            SisDown = false;
        if (e.getKeyChar()=='d')
            DisDown = false;
        if (e.getKeyChar()=='w')
            WisDown = false;
        
        
    }
    
    public class IncomingReader implements Runnable
    {
        public void run()
        {
            
            try
            {
                myId = Integer.parseInt(mySocketScanner.nextLine());
                while (true)
                    ;//parseCommand(mySocketScanner.nextLine();
                    //myTextArea.setText(myTextArea.getText()+mySocketScanner.nextLine()+"\n");
            }catch (NoSuchElementException nsee)
            {
                JOptionPane.showConfirmDialog(null, "Lost connection.");
                System.exit(1);
            }
        }
        
    }
    
}
