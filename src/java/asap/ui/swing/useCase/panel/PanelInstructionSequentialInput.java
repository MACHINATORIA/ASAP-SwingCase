package asap.ui.swing.useCase.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.composite.decorable.EButtonPanel;
import asap.ui.swing.component.composite.decorable.ELabelPanel;
import asap.ui.swing.component.composite.decorable.ETextPanePanel;
import asap.ui.swing.component.composite.decorable.ETextPaneScrollPanel;

@SuppressWarnings( "serial" )
public abstract class PanelInstructionSequentialInput extends EPanel {

    public ETextPaneScrollPanel instruction;

    public ETextPanePanel       description;

    public ELabelPanel          countCaption;

    public ELabelPanel          countNumber;

    public ELabelPanel          countHint;

    public ELabelPanel          sequenceCaption;

    public ELabelPanel          sequenceNumber;

    public ELabelPanel          sequenceHint;

    public EPanel               inputPanel;

    public EButtonPanel         clear;

    public EButtonPanel         accept;

    public Component            inputFieldDelimiterH;

    public Component            inputPanelDelimiterH;

    public PanelInstructionSequentialInput( ) {
        setLayout( new BorderLayout( 0,
                                     0 ) );
        instruction = new ETextPaneScrollPanel( );
        add( instruction,
             BorderLayout.CENTER );
        EPanel panel = new EPanel( );
        panel.setBorder( new EmptyBorder( 10,
                                          0,
                                          0,
                                          0 ) );
        add( panel,
             BorderLayout.SOUTH );
        panel.setLayout( new FlowLayout( FlowLayout.CENTER,
                                         0,
                                         0 ) );
        EPanel panel_2 = new EPanel( );
        panel.add( panel_2 );
        panel_2.setBorder( new EtchedBorder( EtchedBorder.LOWERED,
                                             null,
                                             null ) );
        panel_2.setLayout( new BoxLayout( panel_2,
                                          BoxLayout.Y_AXIS ) );
        EPanel panel_3 = new EPanel( );
        panel_2.add( panel_3 );
        panel_3.setBorder( new EmptyBorder( 5,
                                            10,
                                            10,
                                            10 ) );
        panel_3.setLayout( new BoxLayout( panel_3,
                                          BoxLayout.Y_AXIS ) );
        EPanel panel_4 = new EPanel( );
        panel_4.setBorder( new EmptyBorder( 0,
                                            0,
                                            5,
                                            0 ) );
        FlowLayout fl_panel_4 = (FlowLayout) panel_4.getLayout( );
        fl_panel_4.setVgap( 0 );
        fl_panel_4.setHgap( 0 );
        panel_3.add( panel_4 );
        description = new ETextPanePanel( );
        description.pane.setContentType( "text/html" );
        description.pane.setEditable( false );
        description.pane.setBackground( SystemColor.control );
        description.pane.setText( "Input description" );
        panel_4.add( description );
        EPanel panel_5 = new EPanel( );
        panel_5.setBorder( new EmptyBorder( 0,
                                            0,
                                            5,
                                            0 ) );
        panel_3.add( panel_5 );
        GridBagLayout gbl_panel_5 = new GridBagLayout( );
        gbl_panel_5.columnWeights = new double[ ] { 0.0,
                                                    0.0,
                                                    1.0 };
        gbl_panel_5.rowWeights = new double[ ] { 0.0,
                                                 0.0 };
        panel_5.setLayout( gbl_panel_5 );
        countCaption = new ELabelPanel( );
        countCaption.label.setFont( countCaption.label.getFont( ).deriveFont( countCaption.label.getFont( ).getSize( )
                                                                              + 2f ) );
        countCaption.label.setHorizontalAlignment( SwingConstants.RIGHT );
        countCaption.label.setText( "Input count:" );
        GridBagConstraints gbc_countCaption = new GridBagConstraints( );
        gbc_countCaption.fill = GridBagConstraints.VERTICAL;
        gbc_countCaption.anchor = GridBagConstraints.EAST;
        gbc_countCaption.gridx = 0;
        gbc_countCaption.gridy = 0;
        panel_5.add( countCaption,
                     gbc_countCaption );
        countNumber = new ELabelPanel( );
        countNumber.label.setHorizontalAlignment( SwingConstants.LEFT );
        countNumber.label.setFont( countNumber.label.getFont( ).deriveFont( countNumber.label.getFont( ).getSize( )
                                                                            + 10f ) );
        countNumber.label.setText( "0" );
        GridBagConstraints gbc_countNumber = new GridBagConstraints( );
        gbc_countNumber.anchor = GridBagConstraints.SOUTH;
        gbc_countNumber.insets = new Insets( 0,
                                             10,
                                             0,
                                             0 );
        gbc_countNumber.gridx = 1;
        gbc_countNumber.gridy = 0;
        panel_5.add( countNumber,
                     gbc_countNumber );
        countHint = new ELabelPanel( );
        GridBagConstraints gbc_countHint = new GridBagConstraints( );
        gbc_countHint.fill = GridBagConstraints.VERTICAL;
        gbc_countHint.anchor = GridBagConstraints.WEST;
        gbc_countHint.insets = new Insets( 0,
                                           15,
                                           0,
                                           0 );
        gbc_countHint.gridx = 2;
        gbc_countHint.gridy = 0;
        panel_5.add( countHint,
                     gbc_countHint );
        countHint.label.setHorizontalAlignment( SwingConstants.LEFT );
        countHint.label.setForeground( Color.RED );
        countHint.label.setText( "Count hint" );
        countHint.label.setFont( countHint.label.getFont( ).deriveFont( countHint.label.getFont( ).getSize( ) + 2f ) );
        sequenceCaption = new ELabelPanel( );
        sequenceCaption.label.setHorizontalAlignment( SwingConstants.RIGHT );
        GridBagConstraints gbc_sequenceCaption = new GridBagConstraints( );
        gbc_sequenceCaption.fill = GridBagConstraints.VERTICAL;
        gbc_sequenceCaption.anchor = GridBagConstraints.EAST;
        gbc_sequenceCaption.gridx = 0;
        gbc_sequenceCaption.gridy = 1;
        panel_5.add( sequenceCaption,
                     gbc_sequenceCaption );
        sequenceCaption.label.setText( "Input sequence:" );
        sequenceCaption.label.setFont( sequenceCaption.label.getFont( ).deriveFont( sequenceCaption.label.getFont( ).getSize( )
                                                                                    + 2f ) );
        sequenceNumber = new ELabelPanel( );
        sequenceNumber.label.setHorizontalAlignment( SwingConstants.LEFT );
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints( );
        gbc_sequenceNumber.insets = new Insets( 0,
                                                10,
                                                0,
                                                0 );
        gbc_sequenceNumber.fill = GridBagConstraints.VERTICAL;
        gbc_sequenceNumber.gridx = 1;
        gbc_sequenceNumber.gridy = 1;
        panel_5.add( sequenceNumber,
                     gbc_sequenceNumber );
        sequenceNumber.label.setText( "1" );
        sequenceNumber.label.setFont( sequenceNumber.label.getFont( ).deriveFont( sequenceNumber.label.getFont( ).getSize( )
                                                                                  + 10f ) );
        sequenceHint = new ELabelPanel( );
        sequenceHint.label.setForeground( Color.RED );
        sequenceHint.label.setFont( sequenceHint.label.getFont( ).deriveFont( sequenceHint.label.getFont( ).getSize( )
                                                                              + 2f ) );
        sequenceHint.label.setText( "Sequence hint" );
        GridBagConstraints gbc_sequenceHint = new GridBagConstraints( );
        gbc_sequenceHint.fill = GridBagConstraints.VERTICAL;
        gbc_sequenceHint.anchor = GridBagConstraints.WEST;
        gbc_sequenceHint.insets = new Insets( 0,
                                              15,
                                              0,
                                              0 );
        gbc_sequenceHint.gridx = 2;
        gbc_sequenceHint.gridy = 1;
        panel_5.add( sequenceHint,
                     gbc_sequenceHint );
        EPanel panel_6 = new EPanel( );
        FlowLayout flowLayout = (FlowLayout) panel_6.getLayout( );
        flowLayout.setVgap( 0 );
        flowLayout.setHgap( 0 );
        panel_3.add( panel_6 );
        EPanel panel_1 = new EPanel( );
        panel_6.add( panel_1 );
        panel_1.setLayout( new BorderLayout( 0,
                                             0 ) );
        inputPanel = new EPanel( );
        panel_1.add( inputPanel,
                     BorderLayout.NORTH );
        inputPanel.setLayout( new BorderLayout( 0,
                                                0 ) );
        EPanel panel_7 = new EPanel( );
        panel_1.add( panel_7,
                     BorderLayout.CENTER );
        panel_7.setBorder( new EmptyBorder( 5,
                                            0,
                                            0,
                                            0 ) );
        panel_7.setLayout( new GridLayout( 0,
                                           2,
                                           0,
                                           0 ) );
        EPanel panel_8 = new EPanel( );
        panel_7.add( panel_8 );
        panel_8.setLayout( new FlowLayout( FlowLayout.CENTER,
                                           0,
                                           0 ) );
        clear = new EButtonPanel( );
        clear.button.setText( "Clear Input" );
        panel_8.add( clear );
        EPanel panel_9 = new EPanel( );
        FlowLayout fl_panel_9 = (FlowLayout) panel_9.getLayout( );
        fl_panel_9.setVgap( 0 );
        fl_panel_9.setHgap( 0 );
        panel_7.add( panel_9 );
        accept = new EButtonPanel( );
        accept.button.setText( "Accept Input" );
        panel_9.add( accept );
        inputFieldDelimiterH = Box.createHorizontalStrut( 250 );
        panel_1.add( inputFieldDelimiterH,
                     BorderLayout.SOUTH );
        inputPanelDelimiterH = Box.createHorizontalStrut( 650 );
        panel_3.add( inputPanelDelimiterH );
    }
}
