/**
 * $Id:$
 */
package org.gusdb.schemabrowser.website.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gusdb.dbadmin.model.Category;
import org.gusdb.dbadmin.model.GusSchema;
import org.gusdb.dbadmin.model.GusTable;
import org.gusdb.dbadmin.model.Table;
import org.springframework.web.servlet.ModelAndView;

public class TableDisplayController extends SchemaBrowserController {

    @Override
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        // Table Display
        if ( request.getParameter( "schema" ) != null && request.getParameter( "table" ) != null ) {
            return tableDisplay( request.getParameter( "schema" ), request.getParameter( "table" ) );
        }
        // List Display
        else {
            if ( request.getParameter("category") != null ) {
                return listCategoryDisplay(request.getParameter("category"), request.getParameter("sort"));
            }
            else if ( request.getParameter("schema") != null ) {
                return listSchemaDisplay(request.getParameter("schema"), request.getParameter("sort"));
            }
            return listAllDisplay(request.getParameter("sort"));
        }

    }

    private ArrayList<GusTable> sortTablesByName( List<GusTable> tables ) {
        return new ArrayList<>( new TreeSet<GusTable>( tables ) );
    }

    private ArrayList<GusTable> sortTablesBySchema( List<GusTable> tables ) {
        TreeSet<String> schemas = new TreeSet<>( );
        ArrayList<GusTable> results = new ArrayList<>( );
        for ( Iterator<GusTable> i = tables.iterator( ); i.hasNext( ); ) {
            GusTable table = i.next( );
            schemas.add( table.getSchema( ).getName( ) );
        }
        for ( Iterator<String> j = schemas.iterator( ); j.hasNext( ); ) {
            String schema = j.next( );
            for ( Iterator<Table> k = new TreeSet<Table>( tables ).iterator( ); k.hasNext( ); ) {
                GusTable table = (GusTable) k.next( );
                if ( table.getSchema( ).getName( ).equalsIgnoreCase( schema ) ) {
                    results.add( table );
                }
            }
        }
        return results;
    }

    private ArrayList<GusTable> sortTablesByCategory( List<GusTable> tables ) {
        TreeSet<Category> categories = new TreeSet<>( );
        ArrayList<GusTable> results = new ArrayList<>( );
        for ( Iterator<GusTable> i = tables.iterator( ); i.hasNext( ); ) {
            GusTable table = i.next( );
            if ( table.getCategory() != null ) {
                categories.add( table.getCategory( ) );
            }
        }
        for ( Iterator<Category> i = categories.iterator( ); i.hasNext( ); ) {
            Category category = i.next( );
            for ( Iterator<Table> k = new TreeSet<Table>( tables ).iterator( ); k.hasNext( ); ) {
                GusTable table = (GusTable) k.next( );
                if ( table.getCategory( ) != null &&
                    table.getCategory().getName().equalsIgnoreCase( category.getName() ) ) {
                    results.add( table );
                }
            }
        }
        for ( Iterator<Table> i = new TreeSet<Table>(tables).iterator(); i.hasNext(); ) {
            GusTable table = (GusTable) i.next();
            if ( table.getCategory() == null ) {
                results.add(table);
            }
        }
        return results;
    }

    private ModelAndView tableDisplay( String schemaName, String tableName ) {
        GusSchema schema = (GusSchema) getDatabaseFactory( ).getDatabase( ).getSchema( schemaName );
        GusTable table = null;
        if ( schema != null ) {
            table = (GusTable) schema.getTable( tableName );
        }

        if ( table != null ) {
            return new ModelAndView( "table", "table", table );
        }
        else {
            return new ModelAndView( "error", "error", "Unknown Table" );
        }
    }

    
    private ModelAndView listAllDisplay(String sort) {
        return doSortAndDisplay(getDatabaseFactory( ).getDatabase( ).getGusTables( ), sort);
    }
    
    private ModelAndView listCategoryDisplay( String category, String sort ) {
        if ( category == null ) return new ModelAndView("error", "error", "Invalid Category");
        List<GusTable> tables = new Vector<>();
        for ( Iterator<GusTable> i = getDatabaseFactory().getDatabase().getGusTables().iterator(); i.hasNext(); ) {
            GusTable table = i.next();
            if ( table.getCategory() != null && 
                    category.equalsIgnoreCase(table.getCategory().getName())) {
                tables.add(table);
            }
        }
        return doSortAndDisplay(tables, sort);
    }
    
    private ModelAndView listSchemaDisplay(String schemaName, String sort ) {
       GusSchema schema = (GusSchema) getDatabaseFactory( ).getDatabase( ).getSchema( schemaName );
       if ( schema == null ) return new ModelAndView( "error", "error", "Unknown Schema" );
       return doSortAndDisplay(new ArrayList<GusTable>(schema.getTables()), sort);
    }
    
    private ModelAndView doSortAndDisplay( List<GusTable> arrayList, String sort ) {
        if ( sort != null && sort.equalsIgnoreCase( "schema" ) ) {
            arrayList = sortTablesBySchema( arrayList );
        }
        else if ( sort != null && sort.equalsIgnoreCase( "name" ) ) {
            arrayList = sortTablesByName( arrayList );
        }
        else if ( sort != null && sort.equalsIgnoreCase( "category" ) ) {
            arrayList = sortTablesByCategory( arrayList );
        }
        else {
            arrayList = new ArrayList<GusTable>( arrayList );
        }
        return new ModelAndView( "tableList", "tables", arrayList );
    }

}
