package asap.ui.swing.useCase.controller;

import asap.primitive.pattern.ObserverPattern.ObservableValue;
import asap.primitive.string.HtmlHelper;
import asap.smartcard.service.SmartcardService;
import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.ETextPane;
import asap.ui.swing.useCase.controller.CardPanelController.CardTextFormController;

public abstract class SmartcardFormController< S extends SmartcardService, T extends SmartcardService >
                extends CardTextFormController {

    protected ETextPane            descriptionText;

    protected ObservableValue< S > sourceService;

    protected ObservableValue< T > targetService;

    protected SmartcardFormController( EPanel formPanel,
                                       ETextPane textPane,
                                       Class< ? > textResourceClass,
                                       String textResourceName,
                                       ETextPane descriptionText,
                                       ObservableValue< S > sourceService,
                                       ObservableValue< T > targetService ) {
        super( formPanel,
               textPane,
               textResourceClass,
               textResourceName );
        this.descriptionText = descriptionText;
        this.sourceService = sourceService;
        this.targetService = targetService;
    }

    @Override
    protected void formAttach( ) {
        if ( this.sourceService != null ) {
            this.sourceService.attachObserver( ( oldValue,
                                                 newValue ) -> {
                this.eventServiceChanged( true );
            } );
        }
        if ( this.targetService != null ) {
            this.targetService.attachObserver( ( oldValue,
                                                 newValue ) -> {
                this.eventServiceChanged( false );
            } );
        }
    }

    protected void eventServiceChanged( boolean isSourceService ) {
        this.formReset( );
        if ( !this.navStopping( ) ) {
            if ( this.descriptionText != null ) {
                String[ ] tmpDescription = this.buildServicesDescriptions( isSourceService );
                if ( tmpDescription != null ) {
                    this.descriptionText.setText( HtmlHelper.formatSingleColumnTable( tmpDescription ) );
                }
            }
        }
        if ( !this.navStopping( ) //
             && this.navSelected( )
             && ( ( isSourceService ? this.sourceService
                                    : this.targetService ).get( ) == null ) ) {
            this.dealWithServiceRemoval( isSourceService );
        }
    }

    protected abstract String[ ] buildServicesDescriptions( boolean sourceServiceChanged );

    protected abstract void dealWithServiceRemoval( boolean isSourceService );
}
