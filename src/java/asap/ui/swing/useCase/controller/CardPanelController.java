package asap.ui.swing.useCase.controller;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument;

import asap.primitive.file.FileHelper;
import asap.primitive.string.HtmlHelper;
import asap.primitive.string.HtmlHelper.HtmlHorizontalAlignment;
import asap.primitive.string.StringHelper;
import asap.ui.swing.component.EButton;
import asap.ui.swing.component.EPanel;
import asap.ui.swing.component.ETextPane;

public class CardPanelController {

    public final EButton               buttonBack;

    public final EButton               buttonNext;

    public final EButton               buttonCancel;

    public final CardFormController[ ] formControllers;

    protected int                      selectedFormIndex;

    protected boolean                  stopping;

    public CardPanelController( EButton buttonBack,
                                EButton buttonNext,
                                EButton buttonCancel,
                                CardFormController... formControllers ) {
        this.buttonBack = buttonBack;
        this.buttonNext = buttonNext;
        this.buttonCancel = buttonCancel;
        this.formControllers = formControllers;
        this.selectedFormIndex = 0;
        //
        for ( int tmpIndex = 0; tmpIndex < this.formControllers.length; tmpIndex++ ) {
            this.formControllers[ tmpIndex ].formPanel.setVisible( false );
            this.formControllers[ tmpIndex ].panelAttach( this,
                                                          tmpIndex );
        }
        this.formShow( 0 );
        this.buttonBack.addActionListener( ( actionEvent ) -> {
            if ( this.formControllers[ this.selectedFormIndex ].formConfirmBack( ) ) {
                this.formShow( this.selectedFormIndex - 1 );
            }
        } );
        this.buttonNext.addActionListener( ( actionEvent ) -> {
            if ( this.formControllers[ this.selectedFormIndex ].formConfirmNext( ) ) {
                this.formShow( this.selectedFormIndex + 1 );
            }
        } );
    }

    public boolean start( ) {
        this.stopping = false;
        List< CardFormController > tmpStartedForms = new ArrayList< CardFormController >( );
        for ( CardFormController tmpForm : this.formControllers ) {
            if ( !tmpForm.formStart( ) ) {
                this.stopping = true;
                for ( CardFormController tmpStartedForm : tmpStartedForms ) {
                    tmpStartedForm.formStop( );
                }
                return false;
            }
            tmpStartedForms.add( tmpForm );
        }
        return true;
    }

    public void stop( ) {
        this.stopping = true;
        for ( CardFormController tmpForm : this.formControllers ) {
            tmpForm.formStop( );
        }
    }

    public void recede( ) {
        this.formRecede( this.selectedFormIndex );
    }

    protected boolean isSelected( int formIndex ) {
        return ( formIndex == this.selectedFormIndex );
    }

    protected void navUpdate( int formIndex ) {
        if ( this.isSelected( formIndex ) ) {
            CardFormController tmpCurrentForm = this.formControllers[ formIndex ];
            this.buttonBack.setEnabled( tmpCurrentForm.formCanBack( ) );
            this.buttonNext.setEnabled( tmpCurrentForm.formCanNext( ) );
        }
    }

    protected void formShow( int formIndex ) {
        if ( formIndex >= this.formControllers.length ) {
            formIndex = 0;
        }
        else if ( formIndex < 0 ) {
            formIndex = ( this.formControllers.length - 1 );
        }
        if ( formIndex != this.selectedFormIndex ) {
            this.formControllers[ this.selectedFormIndex ].formPanel.setVisible( false );
            this.formControllers[ this.selectedFormIndex ].formHide( );
            this.selectedFormIndex = formIndex;
            this.formControllers[ this.selectedFormIndex ].formShow( );
            this.formControllers[ this.selectedFormIndex ].formPanel.setVisible( true );
            this.navUpdate( formIndex );
        }
    }

    protected void formBack( int formIndex ) {
        if ( this.isSelected( formIndex ) ) {
            this.formShow( this.selectedFormIndex - 1 );
        }
    }

    protected void formNext( int formIndex ) {
        if ( this.isSelected( formIndex ) ) {
            this.formShow( this.selectedFormIndex + 1 );
        }
    }

    protected void formRecede( int formIndex ) {
        if ( this.isSelected( formIndex ) ) {
            while ( ( formIndex > 0 ) && this.formControllers[ formIndex ].formShouldRecede( ) ) {
                this.formControllers[ formIndex ].formReset( );
                formIndex -= 1;
            }
            this.formShow( formIndex );
        }
    }

    public static abstract class CardFormController {

        private CardPanelController panelController;

        private EPanel              formPanel;

        private int                 formIndex;

        protected CardFormController( EPanel formPanel ) {
            this.formPanel = formPanel;
        }

        protected void panelAttach( CardPanelController panelController,
                                    int formIndex ) {
            this.panelController = panelController;
            this.formIndex = formIndex;
            this.formAttach( );
        }

        protected void navUpdate( ) {
            this.panelController.navUpdate( this.formIndex );
        }

        protected void navBack( ) {
            this.panelController.formBack( this.formIndex );
        }

        protected void navNext( ) {
            this.panelController.formNext( this.formIndex );
        }

        protected void navRecede( ) {
            this.panelController.formRecede( this.formIndex );
        }

        protected boolean navSelected( ) {
            return this.panelController.isSelected( this.formIndex );
        }

        protected boolean navStopping( ) {
            return this.panelController.stopping;
        }

        protected void formAttach( ) {
        }

        protected boolean formStart( ) {
            return true;
        }

        protected void formStop( ) {
        }

        protected void formReset( ) {
        }

        protected void formHide( ) {
        }

        protected void formShow( ) {
        }

        protected boolean formCanBack( ) {
            return true;
        }

        protected boolean formCanNext( ) {
            return true;
        }

        protected boolean formShouldRecede( ) {
            return true;
        }

        protected boolean formConfirmBack( ) {
            return true;
        }

        protected boolean formConfirmNext( ) {
            return true;
        }
    }

    public static abstract class CardTextFormController extends CardFormController {

        protected ETextPane textPane;

        protected CardTextFormController( EPanel formPanel,
                                          ETextPane textPane,
                                          Class< ? > textResourceClass,
                                          String textResourceName ) {
            super( formPanel );
            this.textPane = textPane;
            //
            if ( this.textPane != null ) {
                Font tmpFont = this.textPane.getFont( );
                String tmpStyleSheetRules = StringHelper.arrayToSeparated( " ",
                                                                           String.format( "body { font-family: %s; font-size: %dpt; }",
                                                                                          tmpFont.getFamily( ),
                                                                                          ( tmpFont.getSize( ) + 1 ) ),
                                                                           "ul { padding: 0px; padding-left: 20px; margin: 0px; }" );
                ( (HTMLDocument) this.textPane.getDocument( ) ).getStyleSheet( ).addRule( tmpStyleSheetRules );
                //
                if ( textResourceName != null ) {
                    this.textPane.setText( this.taggedResourceToHtml( textResourceClass,
                                                                      textResourceName ) );
                }
            }
        }

        protected String tagggedTextsToHtml( List< String > taggedTexts ) {
            return HtmlHelper.formatSingleColumnTable( null,
                                                       null,
                                                       false,
                                                       HtmlHorizontalAlignment.Justify,
                                                       null,
                                                       taggedTexts.toArray( new String[ 0 ] ) );
        }

        protected String taggedResourceToHtml( Class< ? > resourceClass,
                                               String resourceName ) {
            List< String > tmpTextLines = new ArrayList< String >( );
            try {
                String tmpTextContent = FileHelper.loadTextResource( String.format( "%s/%s",
                                                                                    FileHelper.getResourcePath( resourceClass,
                                                                                                                "resources" ),
                                                                                    resourceName ) );
                tmpTextLines.addAll( StringHelper.linesToList( tmpTextContent ) );
            }
            catch ( IOException e1 ) {
                tmpTextLines.add( String.format( "%s: %s",
                                                 e1.getClass( ).getSimpleName( ),
                                                 e1.getLocalizedMessage( ) ) );
            }
            return this.tagggedTextsToHtml( tmpTextLines );
        }
    }
}
