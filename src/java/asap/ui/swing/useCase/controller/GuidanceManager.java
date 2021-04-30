package asap.ui.swing.useCase.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asap.primitive.lang.ArrayHelper;
import asap.primitive.pattern.Lambdas.ValueBuilder;
import asap.primitive.string.HtmlHelper.HtmlColor;
import asap.primitive.string.StringHelper;
import asap.ui.swing.component.EComponent;

public class GuidanceManager< M extends Enum< M > > {

    protected ValueBuilder< M, GuidanceAttributes > dynamicBuilder;

    protected Map< M, GuidanceAttributes >          staticMap;

    public GuidanceManager( ) {
        this.staticMap = new HashMap< M, GuidanceAttributes >( );
    }

    public GuidanceManager< M > copyStatic( GuidanceManager< M > anotherManager ) {
        this.staticMap.putAll( anotherManager.staticMap );
        return this;
    }

    public GuidanceManager< M > setStatic( M[ ] messages,
                                           GuidanceAttributes attributes ) {
        for ( M tmpMessage : messages ) {
            this.staticMap.put( tmpMessage,
                                attributes );
        }
        return this;
    }

    public GuidanceManager< M > setStatic( M message,
                                           GuidanceAttributes attributes ) {
        return this.setStatic( ArrayHelper.build( message ),
                               attributes );
    }

    public GuidanceManager< M > setStatic( M[ ] messages,
                                           GuidanceType type,
                                           String... texts ) {
        return this.setStatic( messages,
                               new GuidanceAttributes( type,
                                                       texts ) );
    }

    public GuidanceManager< M > setStatic( M message,
                                           GuidanceType type,
                                           String... texts ) {
        return this.setStatic( ArrayHelper.build( message ),
                               type,
                               texts );
    }

    public GuidanceManager< M > setDynamicBuilder( ValueBuilder< M, GuidanceAttributes > dynamicBuilder ) {
        this.dynamicBuilder = dynamicBuilder;
        return this;
    }

    public GuidanceAttributes getGuidance( M message ) {
        GuidanceAttributes tmpAttributes = null;
        if ( message != null ) {
            tmpAttributes = this.staticMap.get( message );
            if ( tmpAttributes == null ) {
                tmpAttributes = this.getDynamicGuidance( message );
            }
        }
        return tmpAttributes;
    }

    public void setComponent( EComponent component,
                              M message ) {
        GuidanceAttributes tmpAttributes = this.getGuidance( message );
        boolean tmpIsVisible = ( tmpAttributes != null );
        component.setVisible( tmpIsVisible );
        if ( tmpIsVisible ) {
            component.setForeground( new Color( tmpAttributes.type.color.code ) );
            component.setText( StringHelper.listToSeparated( "\n",
                                                             tmpAttributes.texts ) );
        }
    }

    protected GuidanceAttributes getDynamicGuidance( M message ) {
        return ( this.dynamicBuilder == null ) ? null
                                               : this.dynamicBuilder.build( message );
    }

    public static enum GuidanceType {

        Information(
            HtmlColor.Black ),
        Highlight(
            HtmlColor.Blue ),
        Hint(
            HtmlColor.Magenta ),
        Warning(
            HtmlColor.Orange ),
        Alert(
            HtmlColor.Red ),
        Error(
            HtmlColor.Red ),
        //
        HighlightGreen(
            HtmlColor.Green ),
        HighlightCyan(
            HtmlColor.Cyan );

        public final HtmlColor color;

        private GuidanceType( HtmlColor color ) {
            this.color = color;
        }
    }

    public static class GuidanceAttributes {

        public GuidanceType   type;

        public List< String > texts;

        public GuidanceAttributes( ) {
            this.type = GuidanceType.Information;
            this.texts = new ArrayList< String >( );
        }

        public GuidanceAttributes( GuidanceType type,
                                   String... texts ) {
            this.type = type;
            this.texts = new ArrayList< String >( Arrays.asList( texts ) );
        }
    }
}
