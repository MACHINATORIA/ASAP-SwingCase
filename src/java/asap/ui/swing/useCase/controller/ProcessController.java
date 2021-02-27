package asap.ui.swing.useCase.controller;

import asap.primitive.log.LogService.LogException;
import asap.primitive.log.LogService.LogLevel;
import asap.primitive.log.LogService.LogManager;
import asap.primitive.log.LogService.Logger;
import asap.primitive.pattern.Lambdas.EventListener;
import asap.primitive.process.ProcessHelper;
import asap.primitive.swing.SwingProcess;
import asap.ui.swing.component.composite.application.AppSimpleFrame;

public abstract class ProcessController< F extends AppSimpleFrame > extends SwingProcess {

    protected static Logger                             log = LogManager.getLogger( ProcessController.class );

    protected Class< ? extends ProcessController< F > > processClass;

    protected String                                    artifactName;

    protected F                                         ui;

    protected CardPanelController                       uiController;

    protected ProcessController( Class< ? extends ProcessController< F > > processClass,
                                 String artifactName ) {
        this.processClass = processClass;
        this.artifactName = artifactName;
    }

    @Override
    protected void startProcess( ) {
        ///
        if ( !this.initLogger( ) //
             || !this.startUI( ) ) {
            this.stopProcess( );
            return;
        }
        String tmoVersionString = String.format( "versão %s - %s",
                                                 ProcessHelper.readArtifactVersion( this.artifactName ),
                                                 ProcessHelper.readArtifactBuildDate( this.artifactName ) );
        this.ui.appHeader.version.setText( tmoVersionString );
        EventListener tmpCloseListener = ( ) -> {
            if ( this.canClose( ) ) {
                this.shutdown( );
            }
        };
        if ( this.ui != null ) {
            this.ui.onClose( tmpCloseListener );
        }
        if ( this.uiController != null ) {
            this.uiController.start( );
            if ( this.uiController.buttonCancel != null ) {
                this.uiController.buttonCancel.onClick( tmpCloseListener );
            }
        }
        //
        // SwingHelper.maximizeFrame( tmpMainFrame );
        this.setMainFrame( this.ui );
    }

    @Override
    protected void stopProcess( ) {
        this.stopUI( );
    }

    protected boolean initLogger( ) {
        try {
            LogManager.configFromResource( this.processClass,
                                           String.format( "%s-Log.xml",
                                                          this.processClass.getSimpleName( ) ) );
        }
        catch ( LogException e ) {
            log.exception( e );
        }
        LogManager.setMaxLevel( LogLevel.Debug );
        return true;
    }

    protected abstract boolean startUI( );

    protected void stopUI( ) {
        if ( this.uiController != null ) {
            this.uiController.stop( );
        }
        if ( this.ui != null ) {
            this.ui.dispose( );
            this.ui = null;
        }
    }

    protected abstract boolean canClose( );
}
