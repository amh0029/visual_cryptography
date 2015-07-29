import java.awt.Color;

public class PixelVCS
{
   private int redVal;
   private int greenVal;
   private int blueVal;
   
   public PixelVCS(int redIn, int greenIn, int blueIn)
   {
      redVal = redIn;
      greenVal = greenIn;
      blueVal = blueIn;
   }
   
   //Used to determine if pixel is closer to white than black
   public boolean isMoreWhiteThanBlack()
   {
      int sum = redVal + greenVal + blueVal;
      int avg = sum / 3;
      return (avg >= 128);
   }
}