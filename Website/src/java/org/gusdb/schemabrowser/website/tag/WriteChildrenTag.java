/**
 * 
 */
package org.gusdb.schemabrowser.website.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.gusdb.dbadmin.model.Constraint;
import org.gusdb.dbadmin.model.GusTable;
import org.gusdb.dbadmin.model.Category;

/**
 * @author msaffitz
 */
public class WriteChildrenTag extends TagSupport {

    private GusTable table             = null;
    private HashMap  categoryTableHash;

    public void setTable( GusTable table ) {
        this.table = table;
    }

    public GusTable getTable( ) {
        return this.table;
    }

    public int doStartTag( ) {
        try {
            JspWriter out = pageContext.getOut( );
            categoryTableHash = new HashMap();
            processChildren( );
            writeChildren( out );
        }
        catch ( IOException ex ) {
            throw new Error( "Error rendering page" );
        }
        return SKIP_BODY;
    }

    public int doEndTag( ) {
        return SKIP_BODY;
    }

    private void processChildren( ) {
        for ( Iterator i = getTable( ).getReferentialConstraints( ).iterator( ); i.hasNext( ); ) {
            Constraint con = (Constraint) i.next( );
 
            GusTable tab = (GusTable) con.getConstrainedTable( );
            String category;
            
            if ( tab.getCategory( ) == null ||
                 tab.getCategory( ).getName() == null ) {
                category = "Uncategorized";
            } else {
                category = tab.getCategory( ).getName( );
            }

            if ( !categoryTableHash.containsKey( category ) ) {
                categoryTableHash.put( category, new TreeSet( ) );
            }
            ((Collection) categoryTableHash.get( category )).add( tab );
        }
    }

    private void writeChildren( JspWriter out ) throws IOException {
        // This preserves ordering
        for ( Iterator i = table.getSchema( ).getDatabase( ).getCategories( ).iterator( ); i.hasNext( ); ) {
            Category cat = (Category) i.next( );
            if ( categoryTableHash.get( cat.getName() ) != null ) {
                writeCategory( cat.getName( ), out );
            }
        }
        if ( categoryTableHash.get( "Uncategorized" ) != null ) {
            writeCategory( "Uncategorized", out );
        }
    }

    private void writeCategory( String category, JspWriter out ) throws IOException {
        out.println( "<strong>" + category + "</strong><br/>" );
        Collection tabCol = (Collection) categoryTableHash.get( category );
        if ( tabCol == null ) return;
        for ( Iterator j = tabCol.iterator( ); j.hasNext( ); ) {
            GusTable tab = (GusTable) j.next( );
            out.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"table.htm?schema=" );
            out.print( tab.getSchema( ).getName( ) + "&table=" + tab.getName( ) + "\">" );
            out.println( tab.getSchema( ).getName( ) + "::" + tab.getName( ) + "</a><br/>" );
        }
    }

}
