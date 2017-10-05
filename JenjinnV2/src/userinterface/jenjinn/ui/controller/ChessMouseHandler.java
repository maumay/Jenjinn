/**
 * 
 */
package jenjinn.ui.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import jenjinn.engine.enums.Sq;

/**
 * @author ThomasB
 *
 */
public final class ChessMouseHandler implements EventHandler<MouseEvent>
{
     private final Sq attachedSquare;
     private boolean userInteractionDisabled;
     
     public ChessMouseHandler(final Sq squareToAttach)
     {
          attachedSquare = squareToAttach;
     }

     @Override
     public void handle(final MouseEvent e)
     {
          if (!userInteractionDisabled && e.isPrimaryButtonDown())
          {
               ChessGameController.getInstance().processUserClick(attachedSquare);
          }
          if (!userInteractionDisabled && e.isSecondaryButtonDown())
          {
               ChessGameController.getInstance().clearMovementMarkers();
          }
     }
     
     public void setUserActionDisabled(final boolean disable)
     {
          userInteractionDisabled = disable;
     }
}
