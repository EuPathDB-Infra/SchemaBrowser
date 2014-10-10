/**
 * $Id:$
 */
package org.gusdb.schemabrowser.website.controller;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gusdb.dbadmin.model.Column;
import org.gusdb.dbadmin.model.GusColumn;
import org.gusdb.dbadmin.model.GusTable;
import org.gusdb.dbadmin.model.Table;
import org.gusdb.schemabrowser.website.DatabaseFactory;
import org.gusdb.schemabrowser.website.dao.DocumentationDAO;
import org.springframework.web.servlet.mvc.Controller;

public abstract class SchemaBrowserController implements Controller {

    protected final Log               log          = LogFactory.getLog( getClass( ) );
    protected static DatabaseFactory  dbFactory;
    protected static DocumentationDAO docDAO;
    protected static boolean          docPopulated = false;

    protected void populateTableDocumentation( List<GusTable> tables ) {
        log.info( "Populating Table Documentation" );
        for ( Iterator<? extends Table> i = tables.iterator( ); i.hasNext( ); ) {
            GusTable table = (GusTable)i.next( );
            table.setDocumentation( getDocumentationDAO( ).getDocumentation( table.getSchema( ).getName( ),
                    table.getName( ) ) );
            for ( Iterator<Column> j = table.getColumnsExcludeSuperclass( false ).iterator( ); j.hasNext( ); ) {
                GusColumn col = (GusColumn) j.next( );
                col.setDocumentation( getDocumentationDAO( ).getDocumentation( table.getSchema( ).getName( ),
                        table.getName( ), col.getName( ) ) );
            }
        }
    }

    protected DatabaseFactory getDatabaseFactory( ) {
        if ( !docPopulated ) {
            List<GusTable> dbTables = SchemaBrowserController.dbFactory.getDatabase( ).getGusTables( );
            populateTableDocumentation( dbTables );
            docPopulated = true;
        }
        return dbFactory;
    }

    public void setDatabaseFactory( DatabaseFactory dbFactory ) {
      SchemaBrowserController.dbFactory = dbFactory;
    }

    protected DocumentationDAO getDocumentationDAO( ) {
        return SchemaBrowserController.docDAO;
    }

    public void setDocumentationDAO( DocumentationDAO docDAO ) {
      SchemaBrowserController.docDAO = docDAO;
    }

}
