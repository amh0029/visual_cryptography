import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.*;

import java.awt.*;
import javax.swing.*;

public class MastersProjApp extends JFrame
{
   
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Extended VCS");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      JPanel top = new JPanel();
      top.setPreferredSize(new Dimension(470, 175));
      String directions = "Welcome to XXXX Visual Cryptography Tool!\n";
      directions += "Please select if you wish to encode a secret message in two innocent images\n";
      directions += "or decode a secret message from two images.";
      JTextArea description = new JTextArea(directions);
      description.setLineWrap(true);
      description.setWrapStyleWord(true);
      top.add(description);
      
      JPanel choices = new JPanel();
      choices.setPreferredSize(new Dimension(470, 100));
      JButton encrypt = new JButton("Encode");
      JButton decrypt = new JButton("Decode");
      choices.add(encrypt);
      choices.add(decrypt);
      
      JPanel mainPanel = new JPanel(new CardLayout());
      mainPanel.add(top);
      mainPanel.add(choices);
      
      frame.getContentPane().add(mainPanel);
      
      frame.setSize(500, 300);
      frame.pack();
      frame.setVisible(true);
   }
}