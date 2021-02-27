package asap.ui.swing.useCase.panel;

import java.awt.GridLayout;

import asap.ui.swing.component.composite.captionHint.CaptionHintReviewPassword;
import java.awt.Dimension;

@SuppressWarnings( "serial" )
public class PanelInstructionSequentialPasswordsSet extends PanelInstructionSequentialInput {

    public CaptionHintReviewPassword typing;

    public CaptionHintReviewPassword confirmation;

    public PanelInstructionSequentialPasswordsSet( ) {
        inputPanelDelimiterH.setPreferredSize(new Dimension(700, 0));
        inputFieldDelimiterH.setPreferredSize(new Dimension(500, 0));
        description.pane.setText( "Password description" );
        countCaption.label.setText( "Password count:" );
        sequenceCaption.label.setText( "Password sequence:" );
        inputPanel.setLayout( new GridLayout( 0,
                                              2,
                                              10,
                                              0 ) );
        typing = new CaptionHintReviewPassword( );
        typing.caption.label.setText( "Type the password:" );
        typing.password.field.setText( "0123456789ABC" );
        typing.review.pane.setText( "Waiting for password typing...\r\nTyping evaluation result." );
        inputPanel.add( typing );
        confirmation = new CaptionHintReviewPassword( );
        confirmation.caption.label.setText( "Confirm the password:" );
        confirmation.password.field.setText( "0123456789ABC" );
        confirmation.review.pane.setText( "Waiting for password confirmation...\nConfirmation evaluation result." );
        inputPanel.add( confirmation );
        clear.button.setText( "Clear Password" );
        accept.button.setText( "Accept Password" );
    }
}
