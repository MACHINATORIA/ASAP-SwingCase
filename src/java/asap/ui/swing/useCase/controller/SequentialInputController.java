package asap.ui.swing.useCase.controller;

import asap.primitive.pattern.Lambdas.BooleanTask;
import asap.primitive.pattern.Lambdas.EventListener;
import asap.primitive.pattern.Lambdas.IntegerComputer;
import asap.primitive.pattern.Lambdas.IntegerFormatter;
import asap.primitive.pattern.Lambdas.PermissionRequest;
import asap.ui.swing.component.EButton;
import asap.ui.swing.component.ELabel;
import asap.ui.swing.component.ETextComponent;
import asap.ui.swing.component.ETextComponent.ErrorType;
import asap.ui.swing.component.ETextPane;

public class SequentialInputController< T extends ETextComponent > {

    public final ELabel                           countNumber;

    public final ELabel                           countHint;

    public final ELabel                           sequenceNumber;

    public final ELabel                           sequenceHint;

    public final ELabel                           typingHint;

    public final T                                typingField;

    public final ETextPane                        typingReview;

    public final T                                confirmationField;

    public final ELabel                           confirmationHint;

    public final ETextPane                        confirmationReview;

    public final EButton                          clearButton;

    public final EButton                          submitButton;

    public final int                              minimumLength;

    public final int                              maximumLength;

    public final int                              minimumCount;

    public final int                              maximumCount;

    public final GuidanceManager< InputGuidance > guidance;

    protected PermissionRequest                   inputPermission;

    protected IntegerFormatter                    countNumberFormatter;

    protected IntegerFormatter                    sequenceNumberFormatter;

    protected IntegerComputer< String >           lengthComputer;

    protected EventListener                       changeListener;

    protected BooleanTask                         submitTask;

    protected int                                 inputCount;

    protected boolean                             confirmationStarted = false;

    public static enum InputGuidance {
        Count_Disallowed,
        Sequence_Disallowed,
        Input_Disallowed,
        //
        Count_Zero,
        Count_BelowMinimum,
        Count_Valid,
        Count_PrecedesMaximum,
        Count_ReachesMaximum,
        //
        Sequence_ReachesMaximum,
        Sequence_Empty,
        Sequence_Typing,
        Sequence_TypingInvalid,
        Sequence_TypingValid,
        Sequence_Confirming,
        Sequence_ConfirmationInvalid,
        Sequence_Confirmed,
        //
        Input_ClipboardDisabled,
        Input_DragDropDisabled,
        Input_InvalidChar,
        //
        Typing_InvalidPaste,
        Typing_Empty,
        Typing_BelowMinimum,
        Typing_Valid,
        Typing_PrecedesMaximum,
        Typing_ReachesMaximum,
        Typing_AboveMaximum,
        Typing_Confirming,
        Typing_Confirmed,
        //
        Confirmation_Waiting,
        Confirmation_Empty,
        Confirmation_PartialMatch,
        Confirmation_SmallerSizeMismatch,
        Confirmation_SameSizeMismatch,
        Confirmation_BiggerSizeMismatch,
        Confirmation_Match,
    }

    public SequentialInputController( ELabel countNumber,
                                      ELabel countHint,
                                      ELabel sequenceNumber,
                                      ELabel sequenceHint,
                                      ELabel typingHint,
                                      T typingField,
                                      ETextPane typingReview,
                                      ELabel confirmationHint,
                                      T confirmationField,
                                      ETextPane confirmationReview,
                                      EButton clearButton,
                                      EButton submitButton,
                                      int minimumLength,
                                      int maximumLength,
                                      int minimumCount,
                                      int maximumCount ) {
        this.countNumber = countNumber;
        this.countHint = countHint;
        this.sequenceNumber = sequenceNumber;
        this.sequenceHint = sequenceHint;
        this.typingHint = typingHint;
        this.typingField = typingField;
        this.typingReview = typingReview;
        this.confirmationHint = confirmationHint;
        this.confirmationField = confirmationField;
        this.confirmationReview = confirmationReview;
        this.clearButton = clearButton;
        this.submitButton = submitButton;
        //
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
        this.minimumCount = minimumCount;
        this.maximumCount = maximumCount;
        //
        this.typingField.onChange( ( ) -> {
            this.fieldChanged( true );
        } );
        this.typingField.onError( ( error ) -> {
            this.fieldError( true,
                             error );
        } );
        if ( this.confirmationField != null ) {
            this.confirmationField.onChange( ( ) -> {
                this.fieldChanged( false );
            } );
            this.confirmationField.onError( ( error ) -> {
                this.fieldError( false,
                                 error );
            } );
        }
        this.clearButton.addActionListener( ( e ) -> {
            this.actionClear( );
        } );
        this.submitButton.addActionListener( ( e ) -> {
            this.actionSubmit( );
        } );
        //
        this.guidance = new GuidanceManager< InputGuidance >( );
        //
        this.setStaticGuidances( );
    }

    public void setInputPermission( PermissionRequest inputPermission ) {
        this.inputPermission = inputPermission;
    }

    public void setCountNumberFormatter( IntegerFormatter countNumberFormatter ) {
        this.countNumberFormatter = countNumberFormatter;
    }

    public void setSequenceNumberFormatter( IntegerFormatter sequenceNumberFormatter ) {
        this.sequenceNumberFormatter = sequenceNumberFormatter;
    }

    public void setLengthComputer( IntegerComputer< String > lengthComputer ) {
        this.lengthComputer = lengthComputer;
    }

    public void setChangeListener( EventListener changeListener ) {
        this.changeListener = changeListener;
    }

    public void setSubmitTask( BooleanTask submitTask ) {
        this.submitTask = submitTask;
    }

    public void reset( ) {
        this.inputCount = 0;
        this.actionClear( );
    }

    public void update( ) {
        //
        String tmpCountNumberText = "#";
        InputGuidance tmpCountHint = InputGuidance.Count_Disallowed;
        //
        String tmpSequenceNumberText = "#";
        InputGuidance tmpSequenceHint = InputGuidance.Sequence_Disallowed;
        //
        boolean tmpTypingFieldEnable = false;
        boolean tmpConfirmationFieldEnable = false;
        //
        InputGuidance tmpTypingReview = InputGuidance.Input_Disallowed;
        InputGuidance tmpConfirmationReview = InputGuidance.Input_Disallowed;
        //
        boolean tmpClearEnable = false;
        boolean tmpSubmitEnable = false;
        //
        if ( this.isInputPermitted( ) ) {
            //
            if ( this.inputCount < this.maximumCount ) {
                //
                tmpCountNumberText = this.formatCountNumber( this.inputCount );
                tmpSequenceNumberText = this.formatSequenceNumber( this.inputCount );
                tmpTypingFieldEnable = true;
                //
                if ( this.inputCount == 0 ) {
                    tmpCountHint = InputGuidance.Count_Zero;
                }
                else if ( this.inputCount < this.minimumCount ) {
                    tmpCountHint = InputGuidance.Count_BelowMinimum;
                }
                else if ( ( this.inputCount + 1 ) < this.maximumCount ) {
                    tmpCountHint = InputGuidance.Count_Valid;
                }
                else {
                    tmpCountHint = InputGuidance.Count_PrecedesMaximum;
                }
                //
                String tmpTypingText = this.typingField.getText( );
                int tmpTypingLength = this.computeLength( tmpTypingText );
                //
                if ( tmpTypingLength == 0 ) {
                    //
                    tmpSequenceHint = InputGuidance.Sequence_Empty;
                    tmpTypingReview = InputGuidance.Typing_Empty;
                    tmpConfirmationReview = InputGuidance.Confirmation_Waiting;
                }
                else {
                    tmpClearEnable = true;
                    if ( tmpTypingLength < this.minimumLength ) {
                        //
                        tmpSequenceHint = InputGuidance.Sequence_Typing;
                        tmpTypingReview = InputGuidance.Typing_BelowMinimum;
                        tmpConfirmationReview = InputGuidance.Confirmation_Waiting;
                    }
                    else if ( tmpTypingLength > this.maximumLength ) {
                        //
                        tmpSequenceHint = InputGuidance.Sequence_TypingInvalid;
                        tmpTypingReview = InputGuidance.Typing_AboveMaximum;
                        tmpConfirmationReview = InputGuidance.Confirmation_Waiting;
                    }
                    else if ( this.confirmationField == null ) {
                        tmpSequenceHint = InputGuidance.Sequence_TypingValid;
                        if ( tmpTypingLength < this.maximumLength ) {
                            if ( ( tmpTypingLength + 1 ) < this.maximumLength ) {
                                tmpTypingReview = InputGuidance.Typing_Valid;
                            }
                            else {
                                tmpTypingReview = InputGuidance.Typing_PrecedesMaximum;
                            }
                        }
                        else {
                            tmpTypingReview = InputGuidance.Typing_ReachesMaximum;
                        }
                        tmpSubmitEnable = true;
                    }
                    else {
                        tmpConfirmationFieldEnable = true;
                        String tmpConfirmationText = this.confirmationField.getText( );
                        int tmpConfirmationLength = this.computeLength( tmpConfirmationText );
                        //
                        if ( !this.confirmationStarted ) {
                            tmpSequenceHint = InputGuidance.Sequence_Typing;
                            if ( tmpTypingLength < this.maximumLength ) {
                                if ( ( tmpTypingLength + 1 ) < this.maximumLength ) {
                                    tmpTypingReview = InputGuidance.Typing_Valid;
                                }
                                else {
                                    tmpTypingReview = InputGuidance.Typing_PrecedesMaximum;
                                }
                            }
                            else {
                                tmpTypingReview = InputGuidance.Typing_ReachesMaximum;
                            }
                        }
                        else {
                            tmpSequenceHint = InputGuidance.Sequence_Confirming;
                            tmpTypingReview = InputGuidance.Typing_Confirming;
                        }
                        //
                        if ( tmpConfirmationLength == 0 ) {
                            //
                            if ( !this.confirmationStarted ) {
                                tmpConfirmationReview = InputGuidance.Confirmation_Waiting;
                            }
                            else {
                                tmpConfirmationReview = InputGuidance.Confirmation_Empty;
                            }
                        }
                        else if ( tmpConfirmationLength < tmpTypingLength ) {
                            //
                            if ( tmpTypingText.startsWith( tmpConfirmationText ) ) {
                                tmpConfirmationReview = InputGuidance.Confirmation_PartialMatch;
                            }
                            else {
                                tmpSequenceHint = InputGuidance.Sequence_ConfirmationInvalid;
                                tmpConfirmationReview = InputGuidance.Confirmation_SmallerSizeMismatch;
                            }
                        }
                        else if ( tmpConfirmationLength > tmpTypingLength ) {
                            tmpSequenceHint = InputGuidance.Sequence_ConfirmationInvalid;
                            tmpConfirmationReview = InputGuidance.Confirmation_BiggerSizeMismatch;
                        }
                        else if ( tmpTypingText.compareTo( tmpConfirmationText ) != 0 ) {
                            tmpSequenceHint = InputGuidance.Sequence_ConfirmationInvalid;
                            tmpConfirmationReview = InputGuidance.Confirmation_SameSizeMismatch;
                        }
                        else {
                            tmpSequenceHint = InputGuidance.Sequence_Confirmed;
                            tmpTypingReview = InputGuidance.Typing_Confirmed;
                            tmpConfirmationReview = InputGuidance.Confirmation_Match;
                            tmpSubmitEnable = true;
                        }
                    }
                }
            }
            else {
                tmpCountNumberText = Integer.toString( this.inputCount );
                tmpCountHint = InputGuidance.Count_ReachesMaximum;
                tmpSequenceHint = InputGuidance.Sequence_ReachesMaximum;
                tmpTypingReview = InputGuidance.Count_ReachesMaximum;
                tmpConfirmationReview = InputGuidance.Count_ReachesMaximum;
            }
        }
        //
        this.countNumber.setText( tmpCountNumberText );
        this.guidance.setComponent( this.countHint,
                                    tmpCountHint );
        //
        this.sequenceNumber.setText( tmpSequenceNumberText );
        this.guidance.setComponent( this.sequenceHint,
                                    tmpSequenceHint );
        //
        this.typingField.setEnabled( tmpTypingFieldEnable );
        this.guidance.setComponent( this.typingReview,
                                    tmpTypingReview );
        //
        if ( this.confirmationField != null ) {
            this.confirmationField.setEnabled( tmpConfirmationFieldEnable );
            this.guidance.setComponent( this.confirmationReview,
                                        tmpConfirmationReview );
        }
        //
        this.clearButton.setEnabled( tmpClearEnable );
        this.submitButton.setEnabled( tmpSubmitEnable );
        //
        this.inputChanged( );
    }

    public final int getInputCount( ) {
        return this.inputCount;
    }

    public final String getTypingText( ) {
        return this.typingField.getText( );
    }

    public final int getTypingLength( ) {
        return this.computeLength( this.typingField.getText( ) );
    }

    public final String getConfirmationText( ) {
        return this.confirmationField.getText( );
    }

    public final int getConfirmationLength( ) {
        return this.computeLength( this.confirmationField.getText( ) );
    }

    protected void setStaticGuidances( ) {
    }

    protected boolean isInputPermitted( ) {
        return ( this.inputPermission == null ) ? true
                                                : this.inputPermission.isPermitted( );
    }

    protected String formatCountNumber( int countNumber ) {
        return ( this.countNumberFormatter == null ) ? Integer.toString( countNumber )
                                                     : this.countNumberFormatter.format( countNumber );
    }

    protected String formatSequenceNumber( int sequenceNumber ) {
        return ( this.sequenceNumberFormatter == null ) ? Integer.toString( sequenceNumber + 1 )
                                                        : this.sequenceNumberFormatter.format( sequenceNumber );
    }

    protected int computeLength( String inputText ) {
        return ( this.lengthComputer == null ) ? inputText.length( )
                                               : this.lengthComputer.compute( inputText );
    }

    protected void inputChanged( ) {
        if ( this.changeListener != null ) {
            this.changeListener.happened( );
        }
    }

    protected boolean submitInput( ) {
        return ( this.submitTask == null ) ? false
                                           : this.submitTask.execute( );
    }

    protected void fieldChanged( boolean typingChange ) {
        if ( typingChange ) {
            this.fieldError( true,
                             null );
            if ( this.confirmationField != null ) {
                this.confirmationField.getExtension( ).setTextNoChange( "" );
            }
        }
        else {
            this.confirmationStarted = ( this.confirmationField.getText( ).length( ) > 0 );
            this.fieldError( false,
                             null );
        }
        this.update( );
    }

    protected void fieldError( boolean typingError,
                               ErrorType error ) {
        InputGuidance tmpMessage = null;
        if ( error != null ) {
            switch ( error ) {
                case InvalidCharacter:
                    tmpMessage = InputGuidance.Input_InvalidChar;
                    break;
                case InvalidDragOperation:
                case InvalidDropOperation:
                    tmpMessage = InputGuidance.Input_DragDropDisabled;
                    break;
                case InvalidCopyOperation:
                case InvalidCutOperation:
                case InvalidPasteOperation:
                    tmpMessage = InputGuidance.Input_ClipboardDisabled;
                    break;
                case InvalidPastePosition:
                case InvalidPasteContent:
                    tmpMessage = InputGuidance.Typing_InvalidPaste;
                    break;
                default:
                    break;
            }
        }
        this.guidance.setComponent( typingError ? this.typingHint
                                                : this.confirmationHint,
                                    tmpMessage );
    }

    protected void actionClear( ) {
        this.typingField.getExtension( ).setTextNoChange( "" );
        if ( this.confirmationField != null ) {
            this.confirmationField.getExtension( ).setTextNoChange( "" );
        }
        this.confirmationStarted = false;
        this.update( );
    }

    protected void actionSubmit( ) {
        if ( ( this.submitTask != null ) && this.submitTask.execute( ) ) {
            this.inputCount += 1;
            this.actionClear( );
        }
        else {
            this.reset( );
        }
    }
}
