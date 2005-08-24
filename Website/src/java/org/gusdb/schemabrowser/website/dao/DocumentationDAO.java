/**
 * $Id:$
 */
package org.gusdb.schemabrowser.website.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gusdb.schemabrowser.website.model.Documentation;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * @author msaffitz
 */
public class DocumentationDAO extends HibernateDaoSupport {

    private Log  log = LogFactory.getLog( HibernateDaoSupport.class );
    private static HashMap docCache = new HashMap();
    
    public DocumentationDAO( ) {
        super( );
    }

    public String getDocumentation( String schema ) {
        if ( getDocumentationObject(schema, null, null ) == null ) return null;
        return getDocumentationObject(schema, null, null).getDocumentation();
    }

    public String getDocumentation( String schema, String table ) {
        if ( getDocumentationObject(schema, table, null ) == null ) return null;
        return getDocumentationObject(schema, table, null).getDocumentation();
    }

    public String getDocumentation( String schema, String table, String attribute ) {
        if ( getDocumentationObject(schema, table, attribute ) == null ) return null;
        return getDocumentationObject(schema, table, attribute).getDocumentation();
    }

    public Documentation getDocumentationObject( String schema, String table, String attribute ) {
        if ( docCache.isEmpty() ) cacheAll();

        String key = (schema + table + attribute).toLowerCase();
        return (Documentation) docCache.get( key );
    }

    /**
     * @param doc Documentation object to be persisted
     */
    public void saveDocumentationObject( Documentation doc ) {
        doc.setCreatedOn(new Date());
        getHibernateTemplate().save(doc);
        cacheObject(doc);
    }
    
    private Documentation fetchObject( String schema, String table, String attribute ) {
        ArrayList values = new ArrayList();
        values.add(schema.toLowerCase());
        
        String query = "from Documentation where lower(schemaname) = ? ";

        if ( table != null && ! table.equalsIgnoreCase("null") ) {
            query = query.concat(" and lower(tablename) = ? ");
            values.add(table.toLowerCase());
            if ( attribute != null && ! attribute.equalsIgnoreCase("null") ) {
                query = query.concat( " and lower(attributename) = ? ");
                values.add(attribute.toLowerCase());
            } else {
		query = query.concat( " and attributename is null ");
	    }
	} else {
	    query = query.concat( " and tablename is null ");
	}
        
        query = query.concat( "order by createdon desc");

        log.debug("running query: " + query + " with '" + values.subList(0,values.size()).toString() + "'");
        
        List docColl = getHibernateTemplate().find(query, values.subList(0,values.size()).toArray() );
        if ( docColl.size() == 0 ) return null;
        cacheObject((Documentation) docColl.get(0));
        return (Documentation) docColl.get(0);
    }
    
    private String getKey(Documentation doc) {
        return (doc.getSchemaName() + doc.getTableName() + doc.getAttributeName()).toLowerCase();
    }
    
    private void cacheObject(Documentation doc) {
        String key = getKey(doc);
        log.debug("Caching: '" + key + "'");
        docCache.put(key, doc);
    }
    
    private void cacheAll() {
        log.info("Caching all Objects");
        
        List docColl = getHibernateTemplate().find("from Documentation order by createdon desc");
        if ( docColl.isEmpty()) {
            log.warn("Failed to cache any object objects when trying to cache all");
            return;
        }
        
        for ( Iterator i = docColl.iterator(); i.hasNext(); ) {
            Documentation doc = (Documentation) i.next();
            if ( ! docCache.containsKey( getKey(doc)) ) cacheObject(doc);
        }
    }

}
