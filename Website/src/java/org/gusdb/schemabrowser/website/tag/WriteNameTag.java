/**
 * 
 */
package org.gusdb.schemabrowser.website.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;


/**
 * @author msaffitz
 *
 */
public class WriteNameTag extends TableCellWriter {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.gusdb.schemabrowser.tag.TableCellWriter#writeCell(javax.servlet.jsp.JspWriter)
     */
    @Override
    protected void writeCell( JspWriter out ) throws IOException {
        out.println("<big>");
        out.println("<a name=\"" + getTable().getSchema().getName() + getTable().getName() + "\"/>");
        out.print("<a href=\"tableList.htm?schema=" + getTable().getSchema().getName() + "\">");
        out.print(getTable().getSchema().getName() + "</a>::" );
        out.print("<a href=\"table.htm?schema=" + getTable().getSchema().getName() + "&table=");
        out.println(getTable().getName() + "\">" + getTable().getName() + "</a>");
        out.println("</big>");
    }

}
