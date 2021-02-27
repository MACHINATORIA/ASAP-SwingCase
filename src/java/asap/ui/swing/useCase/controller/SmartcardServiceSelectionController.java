package asap.ui.swing.useCase.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.Timer;

import asap.primitive.log.LogService.LogManager;
import asap.primitive.log.LogService.Logger;
import asap.primitive.pattern.Lambdas.EventListener;
import asap.primitive.pattern.Lambdas.ExceptionListener;
import asap.primitive.pattern.ObserverPattern.ObservableValue;
import asap.primitive.pattern.StockPattern.StockListener;
import asap.primitive.string.HtmlHelper;
import asap.primitive.string.StringHelper;
import asap.smartcard.service.SmartcardService;
import asap.smartcard.terminal.SmartcardTerminal;
import asap.smartcard.terminal.SmartcardTerminalException;
import asap.smartcard.terminal.pcsc.PCSCManager;
import asap.smartcard.terminal.pcsc.PCSCManagerException;
import asap.smartcard.terminal.pcsc.PCSCReader;
import asap.smartcard.terminal.pcsc.PCSCReaderException;
import asap.ui.swing.component.EList;
import asap.ui.swing.component.ETextPane;
import asap.ui.swing.useCase.controller.GuidanceManager.GuidanceAttributes;
import asap.ui.swing.useCase.controller.GuidanceManager.GuidanceType;

public class SmartcardServiceSelectionController< T extends SmartcardService, S extends SmartcardService > {

    protected static Logger log = LogManager.getLogger( SmartcardServiceSelectionController.class );

    public static enum ReaderGuidance {
        NoReadersDetected,
        //
        NoReaderSelected,
        NoSmartcardPresent,
        NoServiceDetected,
        //
        ReaderSelectedOnSource,
        ReaderSelectedOnTarget,
        //
        InvalidService,
        ValidService,
        //
        TerminalFailure;
    }

    public static interface ServiceConstructor< T extends SmartcardService > {

        public T construct( SmartcardTerminal terminal,
                            GuidanceAttributes guidance,
                            List< String[ ] > report )
            throws SmartcardTerminalException;
    }

    public final ServiceSelection< S >                  source;

    public final ServiceSelection< T >                  target;

    protected EventListener                             changeListener;

    protected ExceptionListener< PCSCManagerException > pollExceptionListener;

    protected StockListener< PCSCReader >               pcscListener;

    protected Timer                                     pollTimer;

    protected DefaultListModel< String >                readerListModel;

    protected Map< String, PCSCReader >                 readerNameMap;

    protected < X extends SmartcardService > ServiceSelection< X > createSelection( boolean isSource,
                                                                                    EList readerList,
                                                                                    ETextPane reviewText,
                                                                                    ObservableValue< X > serviceObservable,
                                                                                    ServiceConstructor< X > serviceConstructor,
                                                                                    GuidanceManager< ReaderGuidance > guidance ) {
        if ( ( readerList == null )
             || ( reviewText == null )
             || ( serviceObservable == null )
             || ( serviceConstructor == null ) ) {
            return null;
        }
        ServiceSelection< X > tmpServiceSelection = new ServiceSelection< X >( isSource,
                                                                               readerList,
                                                                               this.readerListModel,
                                                                               reviewText,
                                                                               serviceObservable,
                                                                               serviceConstructor );
        tmpServiceSelection.guidance.copyStatic( guidance );
        return tmpServiceSelection;
    }

    public SmartcardServiceSelectionController( EList sourceReaderList,
                                                ETextPane sourceReaderReviewText,
                                                ObservableValue< S > sourceService,
                                                ServiceConstructor< S > sourceConstructor,
                                                //
                                                EList targetReaderList,
                                                ETextPane targetReaderReviewText,
                                                ObservableValue< T > targetService,
                                                ServiceConstructor< T > targetConstructor ) {
        GuidanceManager< ReaderGuidance > tmpGuidance = new GuidanceManager< ReaderGuidance >( );
        tmpGuidance.setStatic( ReaderGuidance.NoReadersDetected,
                               GuidanceType.Alert,
                               "Não foi detectado nenhum leitor de smartcards." );
        tmpGuidance.setStatic( ReaderGuidance.NoReaderSelected,
                               GuidanceType.Warning,
                               "Selecione um leitor de smartcards." );
        tmpGuidance.setStatic( ReaderGuidance.NoSmartcardPresent,
                               GuidanceType.Warning,
                               "Nenhum smartcard inserido no leitor." );
        tmpGuidance.setStatic( ReaderGuidance.TerminalFailure,
                               GuidanceType.Error,
                               "Falha no leitor de smartcards." );
        tmpGuidance.setStatic( ReaderGuidance.ReaderSelectedOnSource,
                               GuidanceType.Error,
                               "Leitor já selecionado como origem." );
        tmpGuidance.setStatic( ReaderGuidance.ReaderSelectedOnTarget,
                               GuidanceType.Error,
                               "Leitor já selecionado como destino." );
        //
        this.readerListModel = new DefaultListModel< String >( );
        //
        this.source = this.createSelection( true,
                                            sourceReaderList,
                                            sourceReaderReviewText,
                                            sourceService,
                                            sourceConstructor,
                                            tmpGuidance );
        //
        this.target = this.createSelection( false,
                                            targetReaderList,
                                            targetReaderReviewText,
                                            targetService,
                                            targetConstructor,
                                            tmpGuidance );
        //
        this.readerNameMap = new HashMap< String, PCSCReader >( );
        //
        this.pcscListener = new StockListener< PCSCReader >( ) {

            @Override
            public void itemAdded( PCSCReader reader ) {
                SmartcardServiceSelectionController.this.eventReaderAdded( reader );
            }

            @Override
            public void itemRemoved( PCSCReader reader ) {
                SmartcardServiceSelectionController.this.eventReaderRemoved( reader );
            }
        };
    }

    public GuidanceManager< ReaderGuidance > getSourceGuidance( ) {
        return ( this.source == null ) ? null
                                       : this.source.guidance;
    }

    public GuidanceManager< ReaderGuidance > getTargetGuidance( ) {
        return ( this.target == null ) ? null
                                       : this.target.guidance;
    }

    public void start( Class< ? > processClass )
        throws PCSCManagerException {
        PCSCManager.start( processClass );
        PCSCManager.singleton.getReaderRegistry( ).addListener( this.pcscListener );
        //
        this.pollTimer = new Timer( 300,
                                    new ActionListener( ) {

                                        @Override
                                        public void actionPerformed( ActionEvent action ) {
                                            try {
                                                PCSCManager.singleton.poll( );
                                                SmartcardServiceSelectionController.this.update( null );
                                            }
                                            catch ( PCSCManagerException e ) {
                                                SmartcardServiceSelectionController.this.eventPollingError( e );
                                            }
                                        }
                                    } );
        //
        if ( this.source != null ) {
            this.source.readerList.onSelect( ( ) -> {
                this.update( true );
            } );
        }
        if ( this.target != null ) {
            this.target.readerList.onSelect( ( ) -> {
                this.update( false );
            } );
        }
        this.reset( );
        //
        this.pollTimer.start( );
    }

    public void stop( ) {
        if ( this.source != null ) {
            this.source.readerList.onSelect( null );
        }
        if ( this.target != null ) {
            this.target.readerList.onSelect( null );
        }
        //
        if ( this.pollTimer != null ) {
            if ( this.pollTimer.isRunning( ) ) {
                this.pollTimer.stop( );
            }
            this.pollTimer = null;
        }
        //
        PCSCManager.singleton.getReaderRegistry( ).removeListener( this.pcscListener );
        try {
            PCSCManager.singleton.stop( );
        }
        catch ( PCSCManagerException e ) {
            log.exception( e );
        }
    }

    public SmartcardServiceSelectionController< T, S > setChangeListener( EventListener changeListener ) {
        this.changeListener = changeListener;
        return this;
    }

    public SmartcardServiceSelectionController< T, S > setPollExceptionListener( ExceptionListener< PCSCManagerException > pollExceptionListener ) {
        this.pollExceptionListener = pollExceptionListener;
        return this;
    }

    public void reset( ) {
        if ( this.source != null ) {
            this.source.reset( );
        }
        if ( this.target != null ) {
            this.target.reset( );
        }
    }

    protected void update( Boolean sourceChanged ) {
        boolean tmpSelectionChanged = false;
        if ( sourceChanged == null ) {
            sourceChanged = true;
        }
        if ( this.source != null ) {
            tmpSelectionChanged |= this.source.update( );
        }
        if ( this.target != null ) {
            tmpSelectionChanged |= this.target.update( );
        }
        if ( tmpSelectionChanged ) {
            this.selectionChanged( );
        }
    }

    public class ServiceSelection< X extends SmartcardService > {

        public final boolean                           isSource;

        public final EList                             readerList;

        public final ETextPane                         reviewText;

        public final ObservableValue< X >              service;

        public final GuidanceManager< ReaderGuidance > guidance;

        protected ServiceConstructor< X >              serviceConstructor;

        protected Integer                              readerCount;

        protected String                               readerName;

        protected boolean                              readerSelected;

        protected Boolean                              cardPresent;

        public ServiceSelection( boolean isSource,
                                 EList readerList,
                                 ListModel< String > readerListModel,
                                 ETextPane reviewText,
                                 ObservableValue< X > serviceObservable,
                                 ServiceConstructor< X > serviceConstructor ) {
            this.isSource = isSource;
            this.readerList = readerList;
            this.readerList.setModel( readerListModel );
            this.reviewText = reviewText;
            this.service = serviceObservable;
            this.serviceConstructor = serviceConstructor;
            this.guidance = new GuidanceManager< ReaderGuidance >( );
        }

        public void reset( ) {
            this.readerCount = null;
            this.readerName = "";
            this.readerSelected = false;
            this.cardPresent = null;
            this.readerList.setSelectedIndex( -1 );
        }

        /*
         * Estados:
         *   Nenhum leitor detectado
         *   Nenhum leitor selecionado
         *   Leitor sem cartão
         *   Serviço não detectado
         *   Serviço rejeitado
         *   Serviço válido
         *   Falha na detecção/identificação
         *   
         */
        public boolean update( ) {
            ReaderGuidance tmpGuidanceMessage = null;
            //
            GuidanceAttributes tmpGuidanceAttributes = null;
            List< String[ ] > tmpReportContent = new ArrayList< String[ ] >( );
            //
            int tmpReaderCount = SmartcardServiceSelectionController.this.readerNameMap.size( );
            int tmpReaderCountPrevious = ( this.readerCount == null ) ? 0
                                                                      : this.readerCount;
            boolean tmpReaderCountChanged = ( ( this.readerCount == null ) || ( tmpReaderCount != this.readerCount ) );
            //
            String tmpReaderName = null;
            boolean tmpReaderNameChanged = true;
            //
            Boolean tmpCardPresent = null;
            boolean tmpCardPresentChanged = true;
            //
            X tmpService = null;
            X tmpServicePrevious = this.service.get( );
            //
            if ( tmpReaderCount == 0 ) {
                if ( tmpReaderCountChanged ) {
                    tmpGuidanceMessage = ReaderGuidance.NoReadersDetected;
                }
            }
            else {
                int tmpSelectedReaderIndex = this.readerList.getSelectedIndex( );
                PCSCReader tmpSelectedReader = ( tmpSelectedReaderIndex < 0 ) ? null
                                                                              : SmartcardServiceSelectionController.this.readerNameMap.get( SmartcardServiceSelectionController.this.readerListModel.get( tmpSelectedReaderIndex ) );
                tmpReaderName = ( tmpSelectedReader == null ) ? null
                                                              : tmpSelectedReader.getName( );
                tmpReaderNameChanged = ( tmpReaderName == null ) ? ( this.readerName != null )
                                                                 : ( ( this.readerName == null )
                                                                     || ( tmpReaderName.compareTo( this.readerName ) != 0 ) );
                try {
                    if ( tmpReaderName == null ) {
                        if ( ( this.readerName != null ) && this.readerSelected ) {
                            tmpSelectedReader = SmartcardServiceSelectionController.this.readerNameMap.get( this.readerName );
                            try {
                                if ( ( tmpSelectedReader != null )
                                     && tmpSelectedReader.isAttached( )
                                     && tmpSelectedReader.isConnected( )
                                     && tmpSelectedReader.isCardPresent( ) ) {
                                    tmpSelectedReader.deactivate( );
                                }
                            }
                            catch ( PCSCReaderException e ) {
                            }
                        }
                        if ( tmpReaderNameChanged || ( tmpReaderCountChanged && ( tmpReaderCountPrevious == 0 ) ) ) {
                            tmpGuidanceMessage = ReaderGuidance.NoReaderSelected;
                        }
                    }
                    else {
                        ServiceSelection< ? extends SmartcardService > tmpAnotherSelection = ( this.isSource ? SmartcardServiceSelectionController.this.target
                                                                                                             : SmartcardServiceSelectionController.this.source );
                        if ( ( tmpAnotherSelection != null )
                             && ( tmpAnotherSelection.readerName != null )
                             && ( tmpAnotherSelection.readerSelected )
                             && ( tmpReaderName.compareTo( tmpAnotherSelection.readerName ) == 0 ) ) {
                            this.readerSelected = false;
                            tmpGuidanceMessage = this.isSource ? ReaderGuidance.ReaderSelectedOnTarget
                                                               : ReaderGuidance.ReaderSelectedOnSource;
                        }
                        else {
                            this.readerSelected = true;
                            if ( !tmpSelectedReader.isConnected( ) ) {
                                tmpSelectedReader.connect( false );
                            }
                            tmpCardPresent = ( ( tmpSelectedReader.isCardPresent( ) || true ) //
                                               && ( tmpSelectedReader.isCardPresent( ) || true ) //
                                               && ( tmpSelectedReader.isCardPresent( ) ) );
                            tmpCardPresentChanged = ( ( this.cardPresent == null ) //
                                                      || ( tmpCardPresent != this.cardPresent ) );
                            if ( tmpReaderNameChanged || tmpCardPresentChanged ) {
                                if ( !tmpCardPresent ) {
                                    tmpGuidanceMessage = ReaderGuidance.NoSmartcardPresent;
                                }
                                else {
                                    tmpGuidanceAttributes = new GuidanceAttributes( );
                                    tmpService = this.serviceConstructor.construct( tmpSelectedReader,
                                                                                    tmpGuidanceAttributes,
                                                                                    tmpReportContent );
                                    tmpGuidanceMessage = ( tmpService == null ) ? ReaderGuidance.InvalidService
                                                                                : ReaderGuidance.ValidService;
                                }
                            }
                            if ( ( tmpServicePrevious == null ) && ( tmpService == null ) ) {
                                tmpSelectedReader.disconnect( );
                            }
                        }
                    }
                }
                catch ( SmartcardTerminalException e ) {
                    tmpGuidanceMessage = ReaderGuidance.TerminalFailure;
                    tmpReportContent.add( new String[ ] { e.getLocalizedMessage( ) } );
                }
            }
            //
            if ( tmpGuidanceMessage != null ) {
                if ( tmpReaderCountChanged ) {
                    this.readerCount = tmpReaderCount;
                }
                if ( tmpReaderNameChanged ) {
                    this.readerName = tmpReaderName;
                }
                if ( tmpCardPresentChanged ) {
                    this.cardPresent = tmpCardPresent;
                }
                boolean tmpServiceChanged = ( ( tmpService == null ) ^ ( tmpServicePrevious == null ) );
                if ( tmpServiceChanged ) {
                    if ( tmpServicePrevious != null ) {
                        SmartcardTerminal tmpTerminalPrevious = tmpServicePrevious.getTerminal( );
                        if ( !tmpTerminalPrevious.isAttached( ) ) {
                            log.debug( "Leitor anterior '%s' desligado do computador.",
                                       tmpTerminalPrevious.getName( ) );
                        }
                        else {
                            try {
                                if ( !tmpTerminalPrevious.isConnected( ) ) {
                                    log.debug( "Leitor anterior '%s' já desconectado anteriormente",
                                               tmpTerminalPrevious.getName( ) );
                                }
                                else {
                                    tmpTerminalPrevious.disconnect( );
                                    log.debug( "Leitor anterior '%s' desconectado",
                                               tmpTerminalPrevious.getName( ) );
                                }
                            }
                            catch ( SmartcardTerminalException e ) {
                                tmpReportContent.add( new String[ ] { "[red][b]Falha ao liberar o leitor selecionado anteriormente.[/b][/red]" } );
                            }
                        }
                    }
                    this.service.set( tmpService );
                }
                if ( tmpGuidanceAttributes == null ) {
                    tmpGuidanceAttributes = this.guidance.getGuidance( tmpGuidanceMessage );
                }
                String tmpHtmlReaderReport = StringHelper.concatenate( HtmlHelper.formatText( false,
                                                                                              null,
                                                                                              null,
                                                                                              String.format( "[%1$s][%2$s][b]%3$s[/b][/%2$s][/%1$s]",
                                                                                                             "f5",
                                                                                                             tmpGuidanceAttributes.type.color.tag,
                                                                                                             StringHelper.listToLines( tmpGuidanceAttributes.texts ) ) ),
                                                                       HtmlHelper.formatStripedTable( null,
                                                                                                      false,
                                                                                                      tmpReportContent ) );
                this.reviewText.setText( tmpHtmlReaderReport );
                //
                return true;
            }
            return false;
        }
    }

    protected void selectionChanged( ) {
        if ( this.changeListener != null ) {
            this.changeListener.happened( );
        }
    }

    protected void eventReaderAdded( PCSCReader reader ) {
        this.readerNameMap.put( reader.getName( ),
                                reader );
        this.readerListModel.addElement( reader.getName( ) );
    }

    protected void eventReaderRemoved( PCSCReader reader ) {
        this.readerListModel.removeElement( reader.getName( ) );
        this.readerNameMap.remove( reader.getName( ) );
    }

    protected void eventPollingError( PCSCManagerException exception ) {
        if ( this.pollExceptionListener != null ) {
            this.pollExceptionListener.thrown( exception );
        }
    }
}
