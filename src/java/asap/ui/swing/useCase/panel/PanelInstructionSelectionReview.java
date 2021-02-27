package asap.ui.swing.useCase.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.composite.decorable.ETextPaneScrollPanel;

@SuppressWarnings( "serial" )
public class PanelInstructionSelectionReview extends EPanel {

    public ETextPaneScrollPanel   instruction;

    public PanelSelectionReview item;

    public PanelInstructionSelectionReview( ) {
        GridBagLayout gridBagLayout = new GridBagLayout( );
        gridBagLayout.columnWeights = new double[ ] { 1.0 };
        gridBagLayout.rowWeights = new double[ ] { 1.0,
                                                   0.0 };
        setLayout( gridBagLayout );
        instruction = new ETextPaneScrollPanel( );
        GridBagConstraints gbc_instruction = new GridBagConstraints( );
        gbc_instruction.fill = GridBagConstraints.BOTH;
        gbc_instruction.insets = new Insets( 0,
                                             0,
                                             5,
                                             0 );
        gbc_instruction.gridx = 0;
        gbc_instruction.gridy = 0;
        add( instruction,
             gbc_instruction );
        item = new PanelSelectionReview( );
        GridBagConstraints gbc_item = new GridBagConstraints( );
        gbc_item.anchor = GridBagConstraints.NORTH;
        gbc_item.fill = GridBagConstraints.HORIZONTAL;
        gbc_item.gridx = 0;
        gbc_item.gridy = 1;
        add( item,
             gbc_item );
    }
}
