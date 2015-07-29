import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.*;

public class VCSTests
{
   public static void main(String[] args)
   {
      Scanner input = new Scanner(System.in);
      int userChoice;
      do
      {
         System.out.println("Please select which operation you wish to perform by entering the number associated with it.");
         System.out.println("\t1.  Encrypt a photo for secure sharing using the size invariant visual cryptography scheme");
         System.out.println("\t2.  Reveal the secret message from photo shares created by size invariant visual cryptography scheme");
         System.out.println("\t3.  Encrypt a photo for secure sharing using the extended visual cryptography scheme");
         System.out.println("\t4.  Reveal the secret message from photo shares created by extended visual cryptography scheme");
         System.out.println("\t5.  Terminate the program");
         System.out.print("\nYour choice:  ");
         userChoice = Integer.parseInt(input.nextLine());
         
         switch(userChoice)
         {
            case 1:
               String imgFileName;
               int numOfShares;
               int numToStack;
            
               //Get inputs
               System.out.print("\nEnter the name of your secret image file:  ");
               imgFileName = input.nextLine();
            
               System.out.print("With this scheme there are two values you need to enter.  n represents\n");
               System.out.print("the number of shares you want created, while k represents the number of\n");
               System.out.print("shares needed to stack in order to display the secret message.  Please\n");
               System.out.print("enter your k and n values.\n");
               boolean userError = true;
               do
               {
                  System.out.print("n:  ");
                  numOfShares = Integer.parseInt(input.nextLine());
                  System.out.print("k:  ");
                  numToStack = Integer.parseInt(input.nextLine());
                  if(numToStack <= numOfShares)
                  {
                     userError = false;
                  }
                  else
                  {
                     System.out.println("\nERROR:  Your k cannot be greater than n.  Please try again.");
                  }
               } while(userError);
            
               /*
                  Try to get the image file stored as a BufferedImage
               */
               BufferedImage secretMsg = null;
               boolean foundFile;
               try
               {
                  //Supports GIF, PNG, JPEG, BMP, and WBMP
                  //Plug-ins for TIFF and JPEG 2000
                  secretMsg = ImageIO.read(new File(imgFileName));
                  foundFile = true;
               }
               catch (IOException e)
               {
                  System.out.println("Error:  The file you tried to encrypt does not exist.");
                  foundFile = false;
               }
            
               /*
                  If the file is found, create an array for pixel info storage.
               */
               if(foundFile)
               {
                  SizeInvariantVCS mySIVCS = new SizeInvariantVCS(numToStack, numOfShares, secretMsg);
                  mySIVCS.encryptImage();
                  
                  //Create file names for the shares the message is hidden in
                  System.out.print("Do you wish to store these shared file in a subdirectory of this folder?  Yes or No:  ");
                  String folderResponse = input.nextLine();
                  boolean storeInFolder = (folderResponse.trim().charAt(0) == 'y' || folderResponse.trim().charAt(0) == 'Y');
                  String folderName = "";
                  if(storeInFolder)
                  {
                     System.out.print("Please enter the name of the folder without a trailing slash:  ");
                     folderName = input.nextLine();
                     File directory = new File(folderName);
                     directory.mkdir();
                  }
                  String[] shareFileNames = new String[numOfShares];
                  for(int i = 0; i < numOfShares; i++)
                  {
                     if(storeInFolder)
                     {
                        shareFileNames[i] = folderName + "/share" + (i + 1) + ".png";
                     }
                     else
                     {
                        shareFileNames[i] = "share" + (i + 1) + ".png";
                     }
                  }
                  
                  for(int i = 0; i < numOfShares; i++)
                  {
                     try
                     {
                        //Takes the pixel array and creates a new buffered image
                        BufferedImage tempShare = new BufferedImage(mySIVCS.getImgWidth(), mySIVCS.getImgHeight(), 
                           BufferedImage.TYPE_INT_ARGB);
                        tempShare.setRGB(0, 0, mySIVCS.getImgWidth(), mySIVCS.getImgHeight(), 
                           mySIVCS.getRGBPixelsForShares()[i], 0, mySIVCS.getImgWidth());
                     
                        //Creates the file name of the new image
                        File tempOutput = new File(shareFileNames[i]);
                     
                        //Writes the buffered image to a png file
                        ImageIO.write(tempShare, "png", tempOutput);
                     }
                     catch (IOException e)
                     {
                        System.out.println("Shit just got real!");
                     }
                  }
               }
               System.out.println("\n");
               break;
           
            case 2:
               //Get number of shares to be stacked
               System.out.print("Please enter the number of shares you have:  ");
               int numOfCurrShares = Integer.parseInt(input.nextLine());
               
               //Get filenames of those shares
               String[] shareFileNames = new String[numOfCurrShares];
               for(int i = 0; i < numOfCurrShares; i++)
               {
                  System.out.print("Please enter the filename for one of your shares:  ");
                  shareFileNames[i] = input.nextLine();
               }
               
               //Convert images to BufferedImage objects
               BufferedImage[] shares = new BufferedImage[numOfCurrShares];
               boolean foundFiles = false;
               for(int i = 0; i < numOfCurrShares; i++)
               {
                  try
                  {
                  //Supports GIF, PNG, JPEG, BMP, and WBMP
                  //Plug-ins for TIFF and JPEG 2000
                     shares[i] = ImageIO.read(new File(shareFileNames[i]));
                     foundFiles = true;
                  }
                  catch (IOException e)
                  {
                     System.out.println("Error:  The file you tried to stack does not exist.");
                     foundFiles = false;
                  }
               }
               
               if(foundFiles)
               {
                  SizeInvariantVCS mySIVCS = new SizeInvariantVCS(numOfCurrShares, shares);
                  mySIVCS.decryptImage();
               
                  //Write the new image
                  System.out.print("Please enter the file name you wish to give your stacked image:  ");
                  String secretMsgFileName = input.nextLine();
                  int imgFileExtension = secretMsgFileName.indexOf('.');
                  String extension = secretMsgFileName.substring(imgFileExtension + 1);
                  try
                  {
                        //Takes the pixel array and creates a new buffered image
                     BufferedImage secretImage = new BufferedImage(mySIVCS.getImgWidth(), mySIVCS.getImgHeight(), 
                           BufferedImage.TYPE_INT_ARGB);
                     secretImage.setRGB(0, 0, mySIVCS.getImgWidth(), mySIVCS.getImgHeight(), 
                        mySIVCS.getDecryptImgPixels(), 0, mySIVCS.getImgWidth());
                     
                        //Creates the file name of the new image
                     File tempOutput = new File(secretMsgFileName);
                     
                        //Writes the buffered image to a png file
                     ImageIO.write(secretImage, extension, tempOutput);
                  }
                  catch (IOException e)
                  {
                     System.out.println("Shit just got real!");
                  }
                  
               }
               System.out.println("\n");
               break;
            
            case 3:
               System.out.println("\nNote, this portion is currently a (2,2)-EVCS.  Changes will be made later.");
               
               String secretFile;
               String[] innocentFiles = new String[2];
               //get name of secret msg file
               System.out.print("Enter the name of your secret image file:  ");
               secretFile = input.nextLine();
               
               BufferedImage secretImage = null;
               boolean fileFound;
               try
               {
                  secretImage = ImageIO.read(new File(secretFile));
                  fileFound = true;
               }
               catch(IOException e)
               {
                  System.out.println("Error:  The file you tried to encrypt does not exist.");
                  fileFound = false;
               }
               
               if(fileFound)
               {
                  //get name of innocent 1
                  //get name of innocent 2
                  for(int i = 0; i < 2; i++)
                  {
                     System.out.print("Please enter the name of one of the \"innocent\" file shares:  ");
                     innocentFiles[i] = input.nextLine();
                  }
                  BufferedImage[] innocentShares = new BufferedImage[2];
                  for(int i = 0; i < 2; i++)
                  {
                     try
                     {
                        innocentShares[i] = ImageIO.read(new File(innocentFiles[i]));
                        fileFound = true;
                     }
                     catch(IOException e)
                     {
                        String errorString = "Error:  The file \"" + innocentFiles[i] + "\" does not exist.";
                        System.out.print(errorString);
                        fileFound = false;
                     }
                  }
               
                  //if all files found
                  if(fileFound)
                  {
                     //pass to extendedvcs obj
                     ExtendedVCS myEVCS = new ExtendedVCS(secretImage, innocentShares);
                     //encrypt
                     myEVCS.encryptImage();
                     
                     //get rgbs of new innocent files
                     int [][] newInnocentRGB = myEVCS.getRGBPixelsForShares();
                     
                     //print to image files
                     System.out.print("Do you wish to store these shared file in a subdirectory of this folder?  Yes or No:  ");
                     String folderResponse = input.nextLine();
                     boolean storeInFolder = (folderResponse.trim().charAt(0) == 'y' || folderResponse.trim().charAt(0) == 'Y');
                     String folderName = "";
                     if(storeInFolder)
                     {
                        System.out.print("Please enter the name of the folder without a trailing slash:  ");
                        folderName = input.nextLine();
                        File directory = new File(folderName);
                        directory.mkdir();
                     }
                     String[] shareFiles = new String[2];
                     for(int i = 0; i < 2; i++)
                     {
                        if(storeInFolder)
                        {
                           shareFiles[i] = folderName + "/share" + (i + 1) + ".png";
                        }
                        else
                        {
                           shareFiles[i] = "share" + (i + 1) + ".png";
                        }
                     }
                  
                     for(int i = 0; i < 2; i++)
                     {
                        try
                        {
                        //Takes the pixel array and creates a new buffered image
                           BufferedImage tempShare = new BufferedImage(myEVCS.getImgWidth(), myEVCS.getImgHeight(), 
                              BufferedImage.TYPE_INT_ARGB);
                           tempShare.setRGB(0, 0, myEVCS.getImgWidth(), myEVCS.getImgHeight(), 
                              newInnocentRGB[i], 0, myEVCS.getImgWidth());
                        
                        //Creates the file name of the new image
                           File tempOutput = new File(shareFiles[i]);
                        
                        //Writes the buffered image to a png file
                           ImageIO.write(tempShare, "png", tempOutput);
                        }
                        catch (IOException e)
                        {
                           System.out.println("Shit just got real!");
                        }
                     }
                  }
                  System.out.println("\n");  
               }
               break;
            
            case 4:
               System.out.println("\nNote, this portion is currently a (2,2)-EVCS.  Changes will be made later.");
               
               //Get number of shares to be stacked
               //System.out.print("Please enter the number of shares you have:  ");
               //int numOfCurrShares = Integer.parseInt(input.nextLine());
               
               //Get filenames of those shares
               String[] shareFiles = new String[2];
               for(int i = 0; i < 2; i++)
               {
                  System.out.print("Please enter the filename for one of your shares:  ");
                  shareFiles[i] = input.nextLine();
               }
               
               //Convert images to BufferedImage objects
               BufferedImage[] sharesEVCS = new BufferedImage[2];
               boolean foundFilesEVCS = false;
               for(int i = 0; i < 2; i++)
               {
                  try
                  {
                  //Supports GIF, PNG, JPEG, BMP, and WBMP
                  //Plug-ins for TIFF and JPEG 2000
                     sharesEVCS[i] = ImageIO.read(new File(shareFiles[i]));
                     foundFilesEVCS = true;
                  }
                  catch (IOException e)
                  {
                     String errorString = "Error:  The file \"" + shareFiles[i] + "\" does not exist, so it cannot be superimposed.";
                     System.out.print(errorString);
                     foundFilesEVCS = false;
                  }
               }
               
               if(foundFilesEVCS)
               {
                  ExtendedVCS myEVCS = new ExtendedVCS(sharesEVCS);
                  myEVCS.decryptImage();
               
                  //Write the new image
                  System.out.print("Please enter the file name you wish to give your stacked image:  ");
                  String decryptedFileName = input.nextLine();
                  int fileExtension = decryptedFileName.indexOf('.');
                  String extensionEVCS = decryptedFileName.substring(fileExtension + 1);
                  try
                  {
                        //Takes the pixel array and creates a new buffered image
                     BufferedImage decryptedImage = new BufferedImage(myEVCS.getImgWidth(), myEVCS.getImgHeight(), 
                           BufferedImage.TYPE_INT_ARGB);
                     decryptedImage.setRGB(0, 0, myEVCS.getImgWidth(), myEVCS.getImgHeight(), 
                        myEVCS.getDecryptImgPixels(), 0, myEVCS.getImgWidth());
                     
                        //Creates the file name of the new image
                     File tempOutput = new File(decryptedFileName);
                     
                        //Writes the buffered image to a png file
                     ImageIO.write(decryptedImage, extensionEVCS, tempOutput);
                  }
                  catch (IOException e)
                  {
                     System.out.println("Shit just got real!");
                  }
                  
               }
               System.out.println("\n");
               
               break;
            
            case 5:
               System.out.println("Terminating program...");
         }
      } while(userChoice != 5);
      
   }
}