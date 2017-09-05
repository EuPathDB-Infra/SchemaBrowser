package org.gusdb.schemabrowser.website.tag;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.gusdb.dbadmin.model.Column;
import org.gusdb.dbadmin.model.Column.ColumnType;
import org.gusdb.dbadmin.model.Constraint;
import org.gusdb.dbadmin.model.Constraint.ConstraintType;
import org.gusdb.dbadmin.model.GusColumn;

/**
 * @author msaffitz
 */
public class WriteAttributeRowTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private Column _column = null;
    private boolean _fromSuperclass = false;

    public void setColumn( Column column ) {
        _column = column;
    }

    public Column getColumn( ) {
        return _column;
    }

    public boolean isFromSuperclass() {
        return _fromSuperclass;
    }
    
    public void setFromSuperclass(boolean fromSuperclass) {
        _fromSuperclass = fromSuperclass;
    }
    
    @Override
    public int doStartTag( ) {
        try {
            JspWriter out = pageContext.getOut( );
            writeAttributeRow(out);
        }
        catch ( IOException ex ) {
            throw new Error( "Error writing attribute row." );
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag( ) {
        return SKIP_BODY;
    }

    private void writeAttributeRow(JspWriter out) throws IOException {
        String superRow = isFromSuperclass( ) ? " superRow" : "";

        out.println("<tr class=\"tableRow attrRow" + superRow +  "\">");
        
        out.println("<td>" + getColumn().getName().toLowerCase() + "</td>");

        out.print("<td>");
        if (! getColumn().isNullable() ) out.print("no");
        out.println("</td>");
        
        out.print("<td>" + writeType(getColumn()).toLowerCase() + "</td>");
        
        if ( getColumn().getClass() == GusColumn.class && 
                ((GusColumn) getColumn()).getDocumentation() != null ) {
            out.println("<td>" + ((GusColumn) getColumn()).getDocumentation());
        } else {
            out.println("<td> ");
        }
        out.print(" <small><a href=\"edit/edit.htm?schema=" + getColumn().getTable().getSchema().getName() );
        out.println("&table=" + getColumn().getTable().getName() + "&attribute=" + getColumn().getName() + 
                "\">Edit</a>");
        
        out.println("</small></td></tr>");
    }
    
    private String writeType( Column column ) {
        String trueType = writeTrueType( column );
        if ( !column.getConstraints( ).isEmpty( ) ) {
            for ( Iterator<Constraint> i = column.getConstraints( ).iterator( ); i.hasNext( ); ) {
                Constraint cons = i.next( );

                if ( cons.getType( ) == ConstraintType.FOREIGN_KEY ) {
                    return writeRefType( cons );
                }
                if ( cons.getType( ) == ConstraintType.PRIMARY_KEY ) {
                    return trueType;
                }
            }
        }
        return trueType;
    }

    private String writeTrueType( Column column ) {
        String type = column.getType( ).toString( );

        if ( column.getType( ) == ColumnType.STRING || column.getType( ) == ColumnType.CHARACTER ) {
            type = type + "(" + column.getLength( ) + ") ";
        }
        if ( column.getType( ) == ColumnType.NUMBER && column.getLength( ) != 0 ) {
            type = type + "(" + column.getLength( ) + "," + column.getPrecision( ) + ") ";
        }
        return type;
    }

    private String writeRefType( Constraint cons ) {
        String result = "<a href=\"table.htm?schema=" + cons.getReferencedTable( ).getSchema( ).getName( ) + "&table="
                + cons.getReferencedTable( ).getName( ) + "\">"
                + cons.getReferencedTable( ).getSchema( ).getName( ) + "::" + cons.getReferencedTable( ).getName( )
                + "</a> ";
        return result;
    }

}
