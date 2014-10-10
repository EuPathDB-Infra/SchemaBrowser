/**
 * 
 */
package org.gusdb.schemabrowser.website.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.gusdb.dbadmin.model.Category;
import org.gusdb.dbadmin.model.Constraint;
import org.gusdb.dbadmin.model.GusTable;

/**
 * @author msaffitz
 */
public class WriteChildrenTag extends TagSupport {

    private static final long serialVersionUID = 1L;
  
    private GusTable table             = null;
    private HashMap<String,Set<GusTable>>  categoryTableHash;

    public void setTable( GusTable table ) {
        this.table = table;
    }

    public GusTable getTable( ) {
        return this.table;
    }

    @Override
    public int doStartTag( ) {
        try {
            JspWriter out = pageContext.getOut( );
            categoryTableHash = new HashMap<>();
            processChildren( );
            writeChildren( out );
        }
        catch ( IOException ex ) {
            throw new Error( "Error rendering page" );
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag( ) {
        return SKIP_BODY;
    }

    private void processChildren( ) {
        for ( Iterator<Constraint> i = getTable( ).getReferentialConstraints( ).iterator( ); i.hasNext( ); ) {
            Constraint con = i.next( );
 
            GusTable tab = con.getConstrainedTable( );
            String category;
            
            if ( tab.getCategory( ) == null ||
                 tab.getCategory( ).getName() == null ) {
                category = "Uncategorized";
            } else {
                category = tab.getCategory( ).getName( );
            }

            if ( !categoryTableHash.containsKey( category ) ) {
                categoryTableHash.put( category, new TreeSet<GusTable>( ) );
            }
            categoryTableHash.get( category ).add( tab );
        }
    }

    private void writeChildren( JspWriter out ) throws IOException {
        // This preserves ordering
        for ( Iterator<Category> i = table.getSchema( ).getDatabase( ).getCategories( ).iterator( ); i.hasNext( ); ) {
            Category cat = i.next( );
            if ( categoryTableHash.get( cat.getName() ) != null ) {
                writeCategory( cat.getName( ), out );
            }
        }
        if ( categoryTableHash.get( "Uncategorized" ) != null ) {
            writeCategory( "Uncategorized", out );
        }
    }

    private void writeCategory( String category, JspWriter out ) throws IOException {
        out.println( "<strong><a href=\"categoryList.htm#c:" + category + "\">" + 
		     category + "</a></strong><br/>" );
        Set<GusTable> tabCol = categoryTableHash.get( category );
        if ( tabCol == null ) return;
        for ( Iterator<GusTable> j = tabCol.iterator( ); j.hasNext( ); ) {
            GusTable tab = j.next( );
            out.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"table.htm?schema=" );
            out.print( tab.getSchema( ).getName( ) + "&table=" + tab.getName( ) + "\">" );
            out.println( tab.getSchema( ).getName( ) + "::" + tab.getName( ) + "</a><br/>" );
        }
    }

}
