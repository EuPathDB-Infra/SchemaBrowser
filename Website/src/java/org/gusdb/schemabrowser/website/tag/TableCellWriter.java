/**
 * 
 */
package org.gusdb.schemabrowser.website.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.gusdb.dbadmin.model.Table;

/**
 * @author msaffitz
 */
public abstract class TableCellWriter extends TagSupport {

    private static final long serialVersionUID = 1L;

    protected Table table = null;

    public void setTable( Table table ) {
        this.table = table;
    }

    public Table getTable( ) {
        return this.table;
    }

    @Override
    public int doStartTag( ) {
        try {
            JspWriter out = pageContext.getOut( );
            out.println("<td>");
            writeCell(out);
            out.println("</td>");
        }
        catch ( IOException ex ) {
            throw new Error( "Error writing table cell" );
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag( ) {
        return SKIP_BODY;
    }

    abstract protected void writeCell(JspWriter out) throws IOException;

}
