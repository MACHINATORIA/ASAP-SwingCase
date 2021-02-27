package asap.ui.swing.useCase.panel;

import java.awt.BorderLayout;

import asap.ui.swing.component.composite.captionHint.CaptionHintReviewPassword;

@SuppressWarnings( "serial" )
public class PanelInstructionSequentialPasswordsLoad extends PanelInstructionSequentialInput {

    public CaptionHintReviewPassword typing;

    public PanelInstructionSequentialPasswordsLoad( ) {
        description.pane.setText( "Password description" );
        countCaption.label.setText( "Password count:" );
        sequenceCaption.label.setText( "Password sequence:" );
        typing = new CaptionHintReviewPassword( );
        inputPanel.add( typing,
                        BorderLayout.CENTER );
        typing.caption.label.setText( "Type the password:" );
        typing.password.field.setText( "0123456789ABC" );
        typing.review.pane.setText( "Waiting for password typing...\r\nTyping evaluation result." );
        clear.button.setText( "Clear Password" );
        accept.button.setText( "Accept Password" );
    }
}
