/**
 * 
 */
package se.uom.vcs.walker.filter.resource;

import se.uom.vcs.VCSResource;

/**
 * Base class for all filters that do not are any kind of path filter.<p>
 * 
 * The {@link #enter(VCSResource)} will always return true.
 * 
 * @author Elvis Ligu
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class ExactFilter<T extends VCSResource> extends AbstractResourceFilter<T> {

    /**
     * {@inheritDoc}
     * <p>
     * All the exact filters (those that do not deal with the path of a resource) should return
     * true for every resource.
     */
    @Override
    public boolean enter(T resource) {
        return true;
    }
}
