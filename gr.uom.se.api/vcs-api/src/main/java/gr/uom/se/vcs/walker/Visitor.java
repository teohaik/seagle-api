/**
 * 
 */
package gr.uom.se.vcs.walker;

/**
 * A helper interface that is used as a Visitor in visitor Pattern.
 * <p>
 * 
 * The visiting of entities should stop when {@link #visit(Object)} return
 * false.
 * 
 * @author Elvis Ligu
 * @version 0.0.1
 * @since 0.0.1
 */
public interface Visitor<T> {

   /**
    * Pass the current visiting entity to this visitor.
    * <p>
    * 
    * @param entity
    *           to visit
    * @return true if this visitor should accept more entities
    */
   boolean visit(T entity);
}
