package asap.ui.swing.useCase.panel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;

import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.composite.captionHint.CaptionHintList;
import asap.ui.swing.component.composite.captionHint.CaptionHintTextPaneScroll;

@SuppressWarnings( "serial" )
public class PanelSelectionReview extends EPanel {

    public CaptionHintList           listPanel;

    public CaptionHintTextPaneScroll reviewPanel;

    public Component                 selectionDelimiterV;

    public Component                 selectionDelimiterH;

    public PanelSelectionReview( ) {
        GridBagLayout gbl_panel_1 = new GridBagLayout( );
        gbl_panel_1.columnWeights = new double[ ] { 0.0,
                                                    1.0,
                                                    0.0 };
        gbl_panel_1.rowWeights = new double[ ] { 0.0,
                                                 0.0 };
        setLayout( gbl_panel_1 );
        listPanel = new CaptionHintList( );
        GridBagConstraints gbc_selection = new GridBagConstraints( );
        gbc_selection.insets = new Insets( 0,
                                           0,
                                           0,
                                           5 );
        gbc_selection.fill = GridBagConstraints.BOTH;
        gbc_selection.gridx = 0;
        gbc_selection.gridy = 0;
        add( listPanel,
             gbc_selection );
        listPanel.caption.label.setText( "List Caption:" );
        reviewPanel = new CaptionHintTextPaneScroll( );
        GridBagConstraints gbc_review = new GridBagConstraints( );
        gbc_review.fill = GridBagConstraints.BOTH;
        gbc_review.gridx = 1;
        gbc_review.gridy = 0;
        add( reviewPanel,
             gbc_review );
        reviewPanel.caption.label.setText( "Review Caption:" );
        selectionDelimiterV = Box.createVerticalStrut( 165 );
        GridBagConstraints gbc_verticalStrut = new GridBagConstraints( );
        gbc_verticalStrut.fill = GridBagConstraints.VERTICAL;
        gbc_verticalStrut.insets = new Insets( 0,
                                               0,
                                               5,
                                               0 );
        gbc_verticalStrut.gridx = 2;
        gbc_verticalStrut.gridy = 0;
        add( selectionDelimiterV,
             gbc_verticalStrut );
        selectionDelimiterH = Box.createHorizontalStrut( 250 );
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints( );
        gbc_horizontalStrut.fill = GridBagConstraints.HORIZONTAL;
        gbc_horizontalStrut.insets = new Insets( 0,
                                                 0,
                                                 0,
                                                 5 );
        gbc_horizontalStrut.gridx = 0;
        gbc_horizontalStrut.gridy = 1;
        add( selectionDelimiterH,
             gbc_horizontalStrut );
    }
}
