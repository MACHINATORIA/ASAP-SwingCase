package asap.ui.swing.useCase.panel;

import java.awt.Font;
import java.awt.GridLayout;

import asap.ui.swing.component.ETextField.ContentType;
import asap.ui.swing.component.composite.captionHint.CaptionHintReviewText;

@SuppressWarnings( "serial" )
public class PanelInstructionSequentialByteArraysSet extends PanelInstructionSequentialInput {

    public CaptionHintReviewText typing;

    public CaptionHintReviewText confirmation;

    public PanelInstructionSequentialByteArraysSet( ) {
        description.pane.setText( "Bytes description" );
        countCaption.label.setText( "Bytes count:" );
        sequenceCaption.label.setText( "Bytes sequence:" );
        inputPanel.setLayout( new GridLayout( 0,
                                              2,
                                              10,
                                              0 ) );
        typing = new CaptionHintReviewText( );
        typing.caption.label.setText( "Type the bytes:" );
        typing.text.field.setContentType( ContentType.HexaBytes );
        typing.text.field.setFont( new Font( "Consolas",
                                             typing.text.field.getFont( ).getStyle( ),
                                             typing.text.field.getFont( ).getSize( ) ) );
        typing.text.field.setText( "00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF" );
        typing.review.pane.setText( "Waiting for bytes typing...\r\nTyping evaluation result." );
        inputPanel.add( typing );
        confirmation = new CaptionHintReviewText( );
        confirmation.caption.label.setText( "Confirm the bytes:" );
        confirmation.text.field.setContentType( ContentType.HexaBytes );
        confirmation.text.field.setFont( new Font( "Consolas",
                                                   confirmation.text.field.getFont( ).getStyle( ),
                                                   confirmation.text.field.getFont( ).getSize( ) ) );
        confirmation.text.field.setText( "00 11 22 33 44 55 66 77 88 99 AA BB CC DD EE FF" );
        confirmation.review.pane.setText( "Waiting for bytes confirmation...\r\nConfirmation evaluation result." );
        inputPanel.add( confirmation );
        clear.button.setText( "Clear Bytes" );
        accept.button.setText( "Accept Bytes" );
    }
}
