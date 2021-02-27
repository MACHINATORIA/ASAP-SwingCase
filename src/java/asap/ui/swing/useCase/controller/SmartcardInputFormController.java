package asap.ui.swing.useCase.controller;

import asap.primitive.lang.ArrayHelper;
import asap.primitive.pattern.ObserverPattern.ObservableValue;
import asap.smartcard.service.SmartcardService;
import asap.ui.swing.component.EButton;
import asap.ui.swing.component.ELabel;
import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.ETextComponent;
import asap.ui.swing.component.ETextPane;
import asap.ui.swing.useCase.controller.GuidanceManager.GuidanceAttributes;
import asap.ui.swing.useCase.controller.GuidanceManager.GuidanceType;
import asap.ui.swing.useCase.controller.SequentialInputController.InputGuidance;

public abstract class SmartcardInputFormController< SS extends SmartcardService, TS extends SmartcardService, TC extends ETextComponent >
                extends SmartcardFormController< SS, TS > {

    protected SequentialInputController< TC > inputController;

    protected SmartcardInputFormController( EPanel formPanel,
                                            ETextPane instructionText,
                                            Class<?> instructionResourceClass,
                                            String instructionResourceName,
                                            ETextPane descriptionText,
                                            ELabel countNumber,
                                            ELabel countHint,
                                            ELabel sequenceNumber,
                                            ELabel sequenceHint,
                                            ELabel typingHint,
                                            TC typingField,
                                            ETextPane typingReview,
                                            ELabel confirmationHint,
                                            TC confirmationField,
                                            ETextPane confirmationReview,
                                            EButton clearButton,
                                            EButton acceptButton,
                                            int minimumLength,
                                            int maximumLength,
                                            int minimumCount,
                                            int maximumCount,
                                            ObservableValue< SS > sourceService,
                                            ObservableValue< TS > targetService ) {
        super( formPanel,
               instructionText,
               instructionResourceClass,
               instructionResourceName,
               descriptionText,
               sourceService,
               targetService );
        this.inputController = new SequentialInputController< TC >( countNumber,
                                                                    countHint,
                                                                    sequenceNumber,
                                                                    sequenceHint,
                                                                    typingHint,
                                                                    typingField,
                                                                    typingReview,
                                                                    confirmationHint,
                                                                    confirmationField,
                                                                    confirmationReview,
                                                                    clearButton,
                                                                    acceptButton,
                                                                    minimumLength,
                                                                    maximumLength,
                                                                    minimumCount,
                                                                    maximumCount );
        //
        this.setRegularGuidance( );
        this.setSpecificGuidance( );
        this.inputController.guidance.setDynamic( ( message ) -> {
            return this.buildDynamicGuidance( message );
        } );
        this.inputController.setInputPermission( ( ) -> {
            return this.isServicesValid( );
        } );
        //
        this.inputController.setSubmitTask( ( ) -> {
            return this.submitInput( );
        } );
        this.inputController.setChangeListener( ( ) -> {
            this.navUpdate( );
        } );
        //
    }

    protected boolean isServicesValid( ) {
        return ( ( ( this.sourceService == null ) || ( this.sourceService.get( ) != null ) )
                 && ( ( this.targetService == null ) || ( this.targetService.get( ) != null ) ) );
    }

    protected void setRegularGuidance( ) {
        GuidanceManager< InputGuidance > tmpManager = this.inputController.guidance;
        /*
         * 
         */
        tmpManager.setStatic( ArrayHelper.build( InputGuidance.Count_Disallowed,
                                                 InputGuidance.Sequence_Disallowed,
                                                 InputGuidance.Input_Disallowed ),
                              GuidanceType.Alert,
                              "Nenhum cartão de custódia selecionado." );
        /*
         * 
         */
        tmpManager.setStatic( InputGuidance.Sequence_Empty,
                              GuidanceType.Alert,
                              "Aguardando a digitação" );
        tmpManager.setStatic( InputGuidance.Sequence_Typing,
                              GuidanceType.Hint,
                              "Em digitação..." );
        tmpManager.setStatic( InputGuidance.Sequence_TypingValid,
                              GuidanceType.Hint,
                              "Digitação válida" );
        tmpManager.setStatic( InputGuidance.Sequence_TypingInvalid,
                              GuidanceType.Alert,
                              "Digitação inválida" );
        tmpManager.setStatic( InputGuidance.Sequence_Confirming,
                              GuidanceType.Hint,
                              "Em confirmação..." );
        tmpManager.setStatic( InputGuidance.Sequence_ConfirmationInvalid,
                              GuidanceType.Alert,
                              "Confirmação inválida" );
        tmpManager.setStatic( InputGuidance.Sequence_Confirmed,
                              GuidanceType.Highlight,
                              "Digitada e confirmada" );
        /*
         * 
         */
        tmpManager.setStatic( InputGuidance.Input_ClipboardDisabled,
                              GuidanceType.Alert,
                              "Não é permitido copiar ou colar." );
        tmpManager.setStatic( InputGuidance.Input_DragDropDisabled,
                              GuidanceType.Alert,
                              "Não é permitido arrastar ou soltar." );
        tmpManager.setStatic( InputGuidance.Input_InvalidChar,
                              GuidanceType.Alert,
                              "O caracter digitado não é permitido." );
        /*
         * 
         */
        tmpManager.setStatic( InputGuidance.Confirmation_Waiting,
                              GuidanceType.Alert,
                              "Aguardando a digitação." );
        tmpManager.setStatic( InputGuidance.Confirmation_Empty,
                              GuidanceType.Warning,
                              "Aguardando a confirmação." );
        tmpManager.setStatic( InputGuidance.Confirmation_PartialMatch,
                              GuidanceType.Warning,
                              "Tamanho menor que a digitação." );
        tmpManager.setStatic( InputGuidance.Confirmation_SmallerSizeMismatch,
                              GuidanceType.Alert,
                              "Tamanho e conteúdo diferentes da digitação." );
        tmpManager.setStatic( InputGuidance.Confirmation_SameSizeMismatch,
                              GuidanceType.Alert,
                              "Conteúdo diferente da digitação." );
        tmpManager.setStatic( InputGuidance.Confirmation_BiggerSizeMismatch,
                              GuidanceType.Alert,
                              "Tamanho maior que o da digitação." );
    }

    protected GuidanceAttributes buildDynamicGuidance( InputGuidance message ) {
        switch ( message ) {
            case Typing_BelowMinimum:
            case Typing_Valid:
            case Typing_PrecedesMaximum:
            case Typing_ReachesMaximum:
            case Typing_AboveMaximum:
                boolean tmpValid = false;
                switch ( message ) {
                    case Typing_Valid:
                    case Typing_PrecedesMaximum:
                    case Typing_ReachesMaximum:
                        tmpValid = true;
                        break;
                    default:
                        break;
                }
                String tmpReview = this.formatInputReview( tmpValid,
                                                           this.formatInputLength( this.inputController.getTypingLength( ) ) );
                switch ( message ) {
                    case Typing_BelowMinimum:
                        return new GuidanceAttributes( GuidanceType.Warning,
                                                       tmpReview,
                                                       String.format( "Tamanho menor que o mínimo exigido (%s)",
                                                                      this.formatInputLength( this.inputController.minimumLength ) ) );
                    case Typing_Valid:
                        return new GuidanceAttributes( GuidanceType.Highlight,
                                                       tmpReview );
                    case Typing_PrecedesMaximum:
                        return new GuidanceAttributes( GuidanceType.Highlight,
                                                       tmpReview,
                                                       String.format( "Tamanho prestes a atingir o máximo permitido (%s).",
                                                                      this.formatInputLength( this.inputController.maximumLength ) ) );
                    case Typing_ReachesMaximum:
                        return new GuidanceAttributes( GuidanceType.Highlight,
                                                       tmpReview,
                                                       String.format( "Tamanho atingiu o máximo permitido (%s).",
                                                                      this.formatInputLength( this.inputController.maximumLength ) ) );
                    case Typing_AboveMaximum:
                        return new GuidanceAttributes( GuidanceType.Alert,
                                                       tmpReview,
                                                       String.format( "Tamanho excedeu o máximo permitido (%s).",
                                                                      this.formatInputLength( this.inputController.maximumLength ) ) );
                    default:
                        break;
                }
            default:
                break;
        }
        return null;
    }

    @Override
    protected void formReset( ) {
        this.inputController.reset( );
    }

    @Override
    protected boolean formCanNext( ) {
        return ( this.isServicesValid( )
                 && ( this.inputController.getInputCount( ) >= this.inputController.minimumCount ) );
    }

    protected abstract void setSpecificGuidance( );

    protected abstract String formatInputLength( int length );

    protected abstract String formatInputReview( boolean valid,
                                                 String typingLength );

    protected abstract boolean submitInput( );
}
