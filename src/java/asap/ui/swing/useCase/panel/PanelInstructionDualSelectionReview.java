package asap.ui.swing.useCase.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.composite.decorable.ETextPaneScrollPanel;

@SuppressWarnings( "serial" )
public class PanelInstructionDualSelectionReview extends EPanel {

    public ETextPaneScrollPanel instruction;

    public PanelSelectionReview item1;

    public PanelSelectionReview item2;

    public PanelInstructionDualSelectionReview( ) {
        GridBagLayout gridBagLayout = new GridBagLayout( );
        gridBagLayout.columnWeights = new double[ ] { 1.0 };
        gridBagLayout.rowWeights = new double[ ] { 1.0,
                                                   0.0 };
        setLayout( gridBagLayout );
        instruction = new ETextPaneScrollPanel( );
        GridBagConstraints gbc_instruction = new GridBagConstraints( );
        gbc_instruction.fill = GridBagConstraints.BOTH;
        gbc_instruction.gridx = 0;
        gbc_instruction.gridy = 0;
        add( instruction,
             gbc_instruction );
        EPanel panel = new EPanel( );
        GridBagConstraints gbc_panel = new GridBagConstraints( );
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        add( panel,
             gbc_panel );
        panel.setLayout( new BoxLayout( panel,
                                        BoxLayout.Y_AXIS ) );
        item1 = new PanelSelectionReview( );
        item1.setBorder( new EmptyBorder( 5,
                                          0,
                                          0,
                                          0 ) );
        panel.add( item1 );
        item2 = new PanelSelectionReview( );
        item2.setBorder( new EmptyBorder( 5,
                                          0,
                                          0,
                                          0 ) );
        panel.add( item2 );
    }
}
