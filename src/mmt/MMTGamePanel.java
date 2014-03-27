/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mmt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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
    private ClientPlayer self;
    private int itId;
    private Map<Integer,ClientPlayer> otherPlayers;
    
    private Socket mySocket;
    private final String ServerIP = "172.16.219.166";
    private Scanner mySocketScanner;
    private PrintWriter mySocketWriter;
    private final String NEW_PLAYER = "NEW_PLAYER";
    private final String LOC_UPDATE = "LOC_UPDATE";
    private final String NEW_IT = "NEW_IT";
    private final String REMOVE_PLAYER = "REMOVE_PLAYER";
    private final String UPDATE_TIME = "UPDATE_TIME";
    private final String KEY = "KEY";
    private final int SIZE = 10;
    private final int NAME_OFFSET = 12; // vertical offset for names floating above shape
    private  Font myFont;
    
    public MMTGamePanel()
    {
        super();
        AisDown = false;
        SisDown = false;
        DisDown = false;
        WisDown = false;
        myFont = new Font("Times",Font.PLAIN,12);
        otherPlayers = new HashMap<Integer,ClientPlayer>();
        System.out.println("test");
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
            //System.out.println("here1");
            mySocket = new Socket(ServerIP,5000);
            //System.out.println("here2");
            mySocketScanner =  new Scanner(mySocket.getInputStream());
            //System.out.println("here3");
            mySocketWriter = new PrintWriter(mySocket.getOutputStream());
            //System.out.println("here4");
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
        //System.out.println(result);
        return result;
    }
    
    /**
     * splits the command string by tabs and directs execution to the handler, 
     * according to the command type listed first on the row.
     * @param command 
     */
    public void parseCommand(String command)
    {
       // System.out.println(command);
        String[] commands = command.split("\t");
        if (commands[0].equals(NEW_PLAYER))
            handleNewPlayer(commands);
        if (commands[0].equals(LOC_UPDATE))
            handleLocUpdate(commands);
        if (commands[0].equals(NEW_IT))
            handleNewIT(commands);
        if (commands[0].equals(REMOVE_PLAYER))
            handleRemovePlayer(commands);
    }
    
    public void handleNewPlayer(String[] info)
    {
        System.out.println("Handling new player: "+info);
        ClientPlayer tempPlayer = new ClientPlayer(Integer.parseInt(info[1]),
                                                    info[2]);
        otherPlayers.put(Integer.parseInt(info[1]),tempPlayer);
        repaint();
    }

    public void handleLocUpdate(String[] info)
    {
        //System.out.println("Handling location update: "+info);
        int which = Integer.parseInt(info[1]);
        int x = Integer.parseInt(info[2]);
        int y = Integer.parseInt(info[3]);
        if (which == myId)
            self.setPos(x, y);
        else
            if (otherPlayers.containsKey(which))
                otherPlayers.get(which).setPos(x,y);
            else
                throw new RuntimeException("Attempted to modify position of object not on list.:"+which);
        repaint();
    }
    public void handleNewIT(String[] info)
    {
        System.out.println("Handling new \"it\": "+info);
        int newItId = Integer.parseInt(info[1]);
        if (newItId == myId)
            System.out.println("I am now it!");
        else if (otherPlayers.containsKey(newItId))
            System.out.println("Now player "+newItId+" is it.");
        else
            throw new RuntimeException("Attempted to make somebody it who isn't here!");
        itId = newItId;
        repaint();
    }
    public void handleRemovePlayer(String[] info)
    {
        System.out.println("Handling remove player: "+info);
        int idToRemove = Integer.parseInt(info[1]);
        if (idToRemove == myId)
            throw new RuntimeException("Attempted to eject current player!");
        else if (otherPlayers.containsKey(idToRemove))
            otherPlayers.remove(idToRemove);
        else
            throw new RuntimeException("Attempted to eject player who is not playing.");
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.setFont(myFont);
        super.paintComponent(g);
        for (int i :otherPlayers.keySet())
        {
            if (itId == otherPlayers.get(i).getId())
                g.setColor(Color.red);
            else
                g.setColor(Color.green);
            g.fillRect(otherPlayers.get(i).getX(),
                          otherPlayers.get(i).getY(),
                          SIZE,SIZE);
            int w = g.getFontMetrics().stringWidth(otherPlayers.get(i).getName());
            g.drawString(otherPlayers.get(i).getName(), 
                         otherPlayers.get(i).getX()+SIZE/2-w/2, 
                         otherPlayers.get(i).getY()-NAME_OFFSET);
        }   
        if (itId==self.getId())
            g.setColor(Color.red);
        else
            g.setColor(Color.green);
        g.fillOval(self.getX(), self.getY(), SIZE, SIZE);
        int w = g.getFontMetrics().stringWidth(self.getName());
        g.drawString(self.getName(),
                     self.getX()+SIZE/2 - w/2,
                     self.getY()-NAME_OFFSET);
        g.setColor(Color.black);
        g.drawOval(self.getX(), self.getY(), SIZE, SIZE);
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
        mySocketWriter.println(KEY+"\t"+getBinaryForKeys());
        mySocketWriter.flush();
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
        mySocketWriter.println(KEY+"\t"+getBinaryForKeys());
        mySocketWriter.flush();
    }
    
    public class IncomingReader implements Runnable
    {
        public void run()
        {
            
            try
            {
                myId = Integer.parseInt(mySocketScanner.nextLine());
                System.out.println("I have been assigned id#: "+myId);
                self = new ClientPlayer(myId,name);
                itId = myId;// Assume I'm it, until I hear otherwise.
                while (true)
                    parseCommand(mySocketScanner.nextLine());
                    //myTextArea.setText(myTextArea.getText()+mySocketScanner.nextLine()+"\n");
            }catch (NoSuchElementException nsee)
            {
                JOptionPane.showConfirmDialog(null, "Lost connection.");
                System.exit(1);
            }
        }
        
    }
    
}
