/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rjs
 */
public class ElementNotFoundException extends RuntimeException
{
   /******************************************************************
     Sets up this exception with an appropriate message.
   ******************************************************************/
   public ElementNotFoundException (String collection)
   {
      super ("The target element is not in this " + collection);
   }
}
