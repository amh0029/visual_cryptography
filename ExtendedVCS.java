import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

/*
Currently hardcoding this to be a (2,2)-EVCS
*/
public class ExtendedVCS
{
   private int k;
   private int n;
   private int imgWidth;
   private int imgHeight;
   private int numColumns;
   private BufferedImage secretMsg;
   private BufferedImage[] innocentShares;
   //private int[2][] shareOrigRGBPixels;
   private int[][] encryptedShareRGB;
   
   private int numSharesToDecrypt;
   private BufferedImage[] sharesToDecrypt;
   private int[] secretMsgPixels;
   
   //Matrices
   int[][] wwSw = new int[][]{
                    {1, 0, 0, 1},
                     {1, 0, 0, 0} };
   int[][] wwSb = new int[][]{
                    {1, 0, 0, 1},
                     {0, 1, 1, 0} };
   int[][] wbSw = new int [][]{
                    {1, 0, 0, 1},
                     {1, 0, 1, 1} };
   int[][] wbSb = new int [][]{
                    {1, 0, 0, 1},
                     {0, 1, 1, 1} };
   int[][] bwSw = new int [][]{
                    {1, 0, 1, 1},
                     {1, 0, 1, 0} };
   int[][] bwSb = new int [][]{
                    {1, 0, 1, 1},
                     {0, 1, 1, 0} };
   int[][] bbSw = new int [][]{
                    {1, 0, 1, 1},
                     {1, 0, 1, 1} };
   int[][] bbSb = new int [][]{
                    {1, 0, 1, 1},
                     {0, 1, 1, 1} };
   
   
   //For encryption purposes
   ExtendedVCS(BufferedImage secretMsgIn, BufferedImage[] innocentSharesIn)
   {
      k = 2;
      n = 2;
      secretMsg = secretMsgIn;
      imgWidth = secretMsg.getWidth();
      imgHeight = secretMsg.getHeight();
      innocentShares = innocentSharesIn;
   }
   
   //For decryption purposes
   ExtendedVCS(BufferedImage[] shareImgs)
   {
      numSharesToDecrypt = 2;
      sharesToDecrypt = shareImgs;
      imgWidth = shareImgs[0].getWidth();
      imgHeight = shareImgs[0].getHeight();
   }
   
   int getImgWidth()
   {
      return imgWidth;
   }
   
   int getImgHeight()
   {
      return imgHeight;
   }
   
   int[][] getRGBPixelsForShares()
   {
      return encryptedShareRGB;
   }
   
   int[] getDecryptImgPixels()
   {
      return secretMsgPixels;
   }
   
   void encryptImage()
   {
      int[] secretRGB = new int[imgWidth * imgHeight];
      int[][] shareOrigRGB = new int[2][imgWidth * imgHeight];
      secretMsg.getRGB(0, 0, imgWidth, imgHeight, secretRGB, 0, imgWidth);
      innocentShares[0].getRGB(0, 0, imgWidth, imgHeight, shareOrigRGB[0], 0, imgWidth);
      innocentShares[1].getRGB(0, 0, imgWidth, imgHeight, shareOrigRGB[1], 0, imgWidth);
      createPixelsOfShares(secretRGB, shareOrigRGB);
   }
   
   void createPixelsOfShares(int[] secretImgRGB, int[][] shareOriginalRGB)
   {
      encryptedShareRGB = new int[2][imgWidth * imgHeight];
      
      for(int i = 0; i < secretImgRGB.length; i++)
      {
         int redVal = (secretImgRGB[i] & 0x00ff0000) >> 16;
         int greenVal = (secretImgRGB[i] & 0x0000ff00) >> 8;
         int blueVal = (secretImgRGB[i] & 0x000000ff);
         PixelVCS orig = new PixelVCS(redVal, greenVal, blueVal);
         
         redVal = (shareOriginalRGB[0][i] & 0x00ff0000) >> 16;
         greenVal = (shareOriginalRGB[0][i] & 0x0000ff00) >> 8;
         blueVal = (shareOriginalRGB[0][i] & 0x000000ff);
         PixelVCS innocent0 = new PixelVCS(redVal, greenVal, blueVal);
         
         redVal = (shareOriginalRGB[1][i] & 0x00ff0000) >> 16;
         greenVal = (shareOriginalRGB[1][i] & 0x0000ff00) >> 8;
         blueVal = (shareOriginalRGB[1][i] & 0x000000ff);
         PixelVCS innocent1 = new PixelVCS(redVal, greenVal, blueVal);
         
         Random randomGen = new Random();
         int randomColumn = randomGen.nextInt(4);
                     
         //If pixel is white
         if(innocent0.isMoreWhiteThanBlack())
         {            
            if(innocent1.isMoreWhiteThanBlack())
            {
               if(orig.isMoreWhiteThanBlack())
               {
                  //Want to use matrix wwSw
                  if(wwSw[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(wwSw[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
               else
               {
                  //Want to use matrix wwSb
                  if(wwSb[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(wwSb[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
            }
            else
            {
               if(orig.isMoreWhiteThanBlack())
               {
                  //Want to use matrix wbSw
                  if(wbSw[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(wbSw[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
               else
               {
                  //Want to use matrix wbSb
                  if(wbSb[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(wbSb[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
            }
         }
         else
         {
            if(innocent1.isMoreWhiteThanBlack())
            {
               if(orig.isMoreWhiteThanBlack())
               {
                  //Want to use matrix bwSw
                  if(bwSw[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(bwSw[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
               else
               {
                  //Want to use matrix bwSb
                  if(bwSb[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(bwSb[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
            }
            else
            {
               if(orig.isMoreWhiteThanBlack())
               {
                  //Want to use matrix bbSw
                  if(bbSw[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(bbSw[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
               else
               {
                  //Want to use matrix bbSb
                  if(bbSb[0][randomColumn] == 0)
                     encryptedShareRGB[0][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[0][i] = Color.BLACK.getRGB();
                     
                  if(bbSb[1][randomColumn] == 0)
                     encryptedShareRGB[1][i] = Color.WHITE.getRGB();
                  else
                     encryptedShareRGB[1][i] = Color.BLACK.getRGB();
               }
            }
         }
      }
   }
   
   void decryptImage()
   {
      //Make a 2d array of pixel arrays
      int[][] pixelsToCompare = new int[numSharesToDecrypt][imgWidth * imgHeight];
      secretMsgPixels = new int[imgWidth * imgHeight];
                  
      //getRGB pixels of BufferedImages
      for(int i = 0; i < numSharesToDecrypt; i++)
      {
         sharesToDecrypt[i].getRGB(0, 0, imgWidth, imgHeight, pixelsToCompare[i], 0, imgWidth);
      }
                  
      //Logical OR pixel with all three share values
      int numOfPixels = pixelsToCompare[0].length;
      for(int i = 0; i < numOfPixels; i++)
      {
         int pixelColor = 0;
         for(int j = 0; j < numSharesToDecrypt; j++)
         {
            if(pixelsToCompare[j][i] == Color.WHITE.getRGB())
            {
               pixelColor = pixelColor | 0;
            }
            else
            {
               pixelColor = pixelColor | 1;
            }
         }
                     
         //Store the result in an array after converting to WHITE and BLACK
         if(pixelColor == 1)
         {
            secretMsgPixels[i] = Color.BLACK.getRGB();
         }
         else
         {
            secretMsgPixels[i] = Color.WHITE.getRGB();
         }
      }
   }
   
}