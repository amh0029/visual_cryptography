import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class SizeInvariantVCS
{
   private int k;
   private int n;
   private int imgWidth;
   private int imgHeight;
   private int numColumns;
   private BufferedImage secretMsg;
   private int[][] shareRGBPixels;
   
   private int numSharesToDecrypt;
   private BufferedImage[] sharesToDecrypt;
   private int[] secretMsgPixels;
   
   //Matrices
   int[][] c0 = null;
   int[][] c1 = null;
   int[][] s0 = null;
   int[][] s1 = null;
   
   SizeInvariantVCS(int numToStack, int numOfShares, BufferedImage secretMsgIn)
   {
      k = numToStack;
      n = numOfShares;
      secretMsg = secretMsgIn;
      imgWidth = secretMsg.getWidth();
      imgHeight = secretMsg.getHeight();
   }
   
   SizeInvariantVCS(int numOfCurrShares, BufferedImage[] shareImgs)
   {
      numSharesToDecrypt = numOfCurrShares;
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
      return shareRGBPixels;
   }
   
   int[] getDecryptImgPixels()
   {
      return secretMsgPixels;
   }
   
   void encryptImage()
   {
      int[] secretRGB = new int[imgWidth * imgHeight];
      secretMsg.getRGB(0, 0, imgWidth, imgHeight, secretRGB, 0, imgWidth);
      createCMatrices();
      randomSMatrix(s0, c0);
      randomSMatrix(s1, c1);
      createPixelsOfShares(secretRGB);
   }
   
   void createCMatrices()
   {
      if(k == 2)
      {
         //Can be used for any scheme that has k = 2 (aka (2,N)-VCS)
         numColumns = n;
         c0 = new int[n][n];
         s0 = new int[n][n];
         for(int r = 0; r < n; r++)
         {
            c0[r][0] = 1;
         }
                     
         c1 = new int[n][n];
         s1 = new int[n][n];
         for(int i = 0; i < n; i++)
         {
            c1[i][i] = 1;
         }
      }
      /*
      The portion below only works if (3,3)-SIVCS
      */
      else if(k == 3) //had as k==n
      {
         numColumns = (int)Math.pow(2, (n - 1));
         c0 = new int[n][numColumns];
         s0 = new int[n][numColumns];
         for(int c = 1; c < numColumns; c++)
         {
            for(int r = 0; r < n; r++)
            {
               if(c <= n)
               {
                  if(r != (c - 1))
                  {
                     c0[r][c] = 1;
                  }
               }
               else
               {
                  int tempC = c % (n + 1);
                  if(tempC != 0)
                  {
                     if(r != (tempC - 1))
                     {
                        c0[r][c] = 1;
                     }
                  }
               }
            }
         }
                     
         c1 = new int[n][numColumns];
         s1 = new int[n][numColumns];
         for(int c = 0; c < numColumns; c++)
         {
            for(int r = 0; r < n; r++)
            {
               if(c < n)
               {
                  if(c == (n - 1 - r))
                  {
                     c1[r][c] = 1;
                  }
               }
               else
               {
                  int tempC = c % (n + 1);
                  if(tempC == n)
                  {
                     c1[r][c] = 1;
                  }
                  else
                  {
                     if(tempC == (n - 1 - r))
                     {
                        c1[r][c] = 1;
                     }
                  }
               }
            }
         }
      }
      else
      {
         //TBD
      }
   }
   
   void randomSMatrix(int[][] sMatrix, int[][] cMatrix)
   {
      boolean[] colPlaced = new boolean[numColumns];
      int[] colOrder = new int[0];
      int colCount = 0;
      
      do
      {
         Random randomColGen = new Random();
         int randomColumn = randomColGen.nextInt(numColumns);
         if(!colPlaced[randomColumn])
         {
            colOrder = Arrays.copyOf(colOrder, colOrder.length + 1);
            colOrder[colCount] = randomColumn;
            colPlaced[randomColumn] = true;
            colCount += 1;
         }
      } while(colOrder.length != numColumns);
      
      colCount = 0;
      for(int c = 0; c < numColumns; c++)
      {
         for(int r = 0; r < n; r++)
         {
            sMatrix[r][c] = cMatrix[r][colOrder[c]];
         }
      }
   }
   
   void createPixelsOfShares(int[] origImgRGB)
   {
      shareRGBPixels = new int[n][imgWidth * imgHeight];
      for(int i = 0; i < origImgRGB.length; i++)
      {
         int redVal = (origImgRGB[i] & 0x00ff0000) >> 16;
         int greenVal = (origImgRGB[i] & 0x0000ff00) >> 8;
         int blueVal = (origImgRGB[i] & 0x000000ff);
                     
         //If pixel is white
         if(redVal >= 128 || greenVal >= 128 || blueVal >= 128)
         {
            //randomly choose column from s0
            Random randomGen = new Random();
            int randomColumn = randomGen.nextInt(numColumns);
                        
            for(int j = 0; j < n; j++)
            {
               //if value 0, store white
               if(s0[j][randomColumn] == 0)
               {
                  shareRGBPixels[j][i] = Color.WHITE.getRGB();
               }
               //if value 1, store black
               else
               {
                  shareRGBPixels[j][i] = Color.BLACK.getRGB();
               }
            }
         }
         //If pixel is black
         else
         {
            //randomly choose column from s1
            Random randomGen = new Random();
            int randomColumn = randomGen.nextInt(numColumns);
                        
            for(int j = 0; j < n; j++)
            {
               //if value 0, store white
               if(s1[j][randomColumn] == 0)
               {
                  shareRGBPixels[j][i] = Color.WHITE.getRGB();
               }
               //if value 1, store black
               else
               {
                  shareRGBPixels[j][i] = Color.BLACK.getRGB();
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