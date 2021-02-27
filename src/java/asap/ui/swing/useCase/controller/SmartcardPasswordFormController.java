package asap.ui.swing.useCase.controller;

import asap.primitive.lang.ArrayHelper;
import asap.primitive.pattern.ObserverPattern.ObservableValue;
import asap.smartcard.service.SmartcardService;
import asap.ui.swing.component.EButton;
import asap.ui.swing.component.ELabel;
import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.EPasswordField;
import asap.ui.swing.component.ETextPane;
import asap.ui.swing.useCase.controller.GuidanceManager.GuidanceType;
import asap.ui.swing.useCase.controller.SequentialInputController.InputGuidance;

public abstract class SmartcardPasswordFormController< S extends SmartcardService, T extends SmartcardService >
                extends SmartcardInputFormController< S, T, EPasswordField > {

    protected SmartcardPasswordFormController( EPanel formPanel,
                                               ETextPane instructionText,
                                               Class<?> instructionResourceClass,
                                               String instructionResourceName,
                                               ETextPane descriptionText,
                                               ELabel countNumber,
                                               ELabel countHint,
                                               ELabel sequenceNumber,
                                               ELabel sequenceHint,
                                               ELabel typingHint,
                                               EPasswordField typingField,
                                               ETextPane typingReview,
                                               ELabel confirmationHint,
                                               EPasswordField confirmationField,
                                               ETextPane confirmationReview,
                                               EButton clearButton,
                                               EButton acceptButton,
                                               int minimumLength,
                                               int maximumLength,
                                               int minimumCount,
                                               int maximumCount,
                                               ObservableValue< S > sourceService,
                                               ObservableValue< T > targetService ) {
        super( formPanel,
               instructionText,
               instructionResourceClass,
               instructionResourceName,
               descriptionText,
               countNumber,
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
               maximumCount,
               sourceService,
               targetService );
    }

    protected void setSpecificGuidance( ) {
        GuidanceManager< InputGuidance > tmpManager = this.inputController.guidance;
        /*
         * 
         */
        tmpManager.setStatic( InputGuidance.Count_Zero,
                              GuidanceType.Alert,
                              "Nenhuma senha definida" );
        tmpManager.setStatic( InputGuidance.Count_BelowMinimum,
                              GuidanceType.Warning,
                              "Quantidade de senhas inferior ao mínimo exigido" );
        tmpManager.setStatic( InputGuidance.Count_Valid,
                              GuidanceType.Highlight,
                              "Quantidade de senhas suficiente para prosseguir" );
        tmpManager.setStatic( InputGuidance.Count_PrecedesMaximum,
                              GuidanceType.Warning,
                              "Só será permitido definir mais uma senha" );
        tmpManager.setStatic( ArrayHelper.build( InputGuidance.Count_ReachesMaximum,
                                                 InputGuidance.Sequence_ReachesMaximum ),
                              GuidanceType.Alert,
                              "Quantidade de senhas atingiu o máximo permitido" );
        /*
         * 
         */
        tmpManager.setStatic( InputGuidance.Typing_Empty,
                              GuidanceType.Alert,
                              "Nenhuma senha digitada." );
        tmpManager.setStatic( InputGuidance.Typing_Confirming,
                              GuidanceType.Highlight,
                              "Confirmando a senha..." );
        tmpManager.setStatic( ArrayHelper.build( InputGuidance.Typing_Confirmed,
                                                 InputGuidance.Confirmation_Match ),
                              GuidanceType.Highlight,
                              "Senha confirmada." );
    }

    @Override
    protected String formatInputLength( int length ) {
        return String.format( "%d caracter%s",
                              length,
                              ( length > 1 ) ? "s"
                                             : "" );
    }

    @Override
    protected String formatInputReview( boolean valid,
                                        String typingLength ) {
        return String.format( "Senha %sválida (%s).",
                              valid ? ""
                                    : "in",
                              typingLength );
    }
}
