package asap.ui.swing.useCase.panel;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.composite.decorable.EButtonPanel;
import asap.ui.swing.component.composite.decorable.ETextPaneScrollPanel;

@SuppressWarnings( "serial" )
public class PanelInstructionConfirmation extends EPanel {

    public ETextPaneScrollPanel instruction;

    public EButtonPanel         confirm;

    public PanelInstructionConfirmation( ) {
        setLayout( new BoxLayout( this,
                                  BoxLayout.Y_AXIS ) );
        instruction = new ETextPaneScrollPanel( );
        add( instruction );
        EPanel panel = new EPanel( );
        panel.setBorder( new EmptyBorder( 10,
                                          0,
                                          10,
                                          0 ) );
        add( panel );
        panel.setLayout( new FlowLayout( FlowLayout.CENTER,
                                         0,
                                         0 ) );
        confirm = new EButtonPanel( );
        panel.add( confirm );
        confirm.button.setText( "Confirm" );
    }
}
