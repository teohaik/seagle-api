/**
 * 
 */
package se.uom.vcs.walker.filter.commit;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import se.uom.vcs.VCSCommit;

/**
 * A simple implementation of AND operator based on the given filters.
 * <p>
 * 
 * @author Elvis Ligu
 * @version 0.0.1
 * @since 0.0.1
 */
public class VCSCommitAndFilter<T extends VCSCommit> implements
      VCSCommitFilter<T> {

   /**
    * The set of filters that will be applied AND.
    * <p>
    */
   private final Set<VCSCommitFilter<T>> filters;

   /**
    * Create a new filter that will check if all of the given filters returns
    * true.
    * <p>
    * 
    * @param filters
    *           the filters to check if all of the filters returns true. Must
    *           not be null not or empty or contain a null filter. Must contain
    *           at least 2 filters.
    */
   public VCSCommitAndFilter(Collection<VCSCommitFilter<T>> filters) {
      if (filters == null) {
         throw new IllegalArgumentException("filters must not be null");
      }
      if (filters.isEmpty()) {
         throw new IllegalArgumentException("filters must not be empty");
      }
      this.filters = new LinkedHashSet<VCSCommitFilter<T>>();
      for (VCSCommitFilter<T> f : filters) {
         if (f == null) {
            throw new IllegalArgumentException("filters must not contain null");
         }

         this.extract(f);
      }
      if (this.filters.size() < 2) {
         throw new IllegalArgumentException(
               "filters must contain at least two elements");
      }
   }

   /**
    * Extract recursively the given filters.
    * <p>
    * 
    * If there are nested AND then they will be extracted to this AND. That is,
    * if the given filter is instance of this class all its filters will be
    * added to this instance filters.
    * 
    * @param filter
    *           to extract any nested AND.
    */
   private void extract(VCSCommitFilter<T> filter) {
      // If the given filter is a an AND filter (same class as this)
      // extract each filter that it contains.
      if (this.getClass().isAssignableFrom(filter.getClass())) {

         VCSCommitAndFilter<T> andFilter = ((VCSCommitAndFilter<T>) filter);

         for (VCSCommitFilter<T> f : andFilter.filters) {
            extract(f);
         }
      } else {
         this.filters.add(filter);
      }
   }

   /**
    * @return an unmodifiable set of filters of this AND operator
    */
   public Set<VCSCommitFilter<T>> getFilters() {
      return Collections.unmodifiableSet(this.filters);
   }

   /**
    * {@inheritDoc}
    * <p>
    * Returns true if all of the specified filters (during creation) returns
    * true.
    * <p>
    */
   @Override
   public boolean include(T entity) {

      for (VCSCommitFilter<T> f : filters) {
         if (!f.include(entity)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((filters == null) ? 0 : filters.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VCSCommitAndFilter<?> other = (VCSCommitAndFilter<?>) obj;
      if (filters == null) {
         if (other.filters != null)
            return false;
      } else if (!filters.equals(other.filters))
         return false;
      return true;
   }
}
