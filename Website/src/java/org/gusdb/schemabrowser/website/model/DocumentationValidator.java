/**
 * 
 */
package org.gusdb.schemabrowser.website.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * @author msaffitz
 *
 */
public class DocumentationValidator implements Validator {

    protected final Log log = LogFactory.getLog(getClass());
    
    @SuppressWarnings("rawtypes") // must use raw types to override Validator method
    @Override
    public boolean supports (Class clazz) {
        return clazz.equals(Documentation.class);
    }

    @Override
    public void validate( Object obj, Errors errors ) {
        log.info("Validating...");
    }
    
}
