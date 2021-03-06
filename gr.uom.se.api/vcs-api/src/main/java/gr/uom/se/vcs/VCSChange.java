/**
 * 
 */
package gr.uom.se.vcs;

import gr.uom.se.vcs.walker.ChangeVisitor;

/**
 * A change type that is produced as a result of a diff between two commits.
 * <p>
 * A change is linked to a repository resource (a file or directory). A change
 * is the result produced by calculating the differences between a base commit
 * (the old one) and a new commit.
 * 
 * @author Elvis Ligu
 * @since 0.0.1
 * @version 0.0.1
 * @see VCSCommit#walkChanges(VCSCommit, ChangeVisitor)
 */
public interface VCSChange<T extends VCSResource> {

   /**
    * A type of a change for a given resource, calculated as difference between
    * two commits.
    * <p>
    * 
    * @author Elvis Ligu
    * @since 0.0.1
    * @version 0.0.1
    * 
    */
   public static enum Type {
      /**
       * When a resource is added from older to newer commit.
       * <p>
       * That is, the resource is not available at old commit but is available
       * at new one.
       */
      ADDED,

      /**
       * When a resource is deleted from to newer commit.
       * <p>
       * That is, the resource is available at old commit but is not available
       * at new one.
       */
      DELETED,

      /**
       * When a resource is modified from older to newer commit.
       * <p>
       */
      MODIFIED,

      /**
       * When a resource has changed its location from older to newer commit.
       * <p>
       */
      COPIED,

      /**
       * When a resource has been renamed from older to newer commit.
       * <p>
       */
      RENAMED,

      /**
       * Denotes an unspecified type of change.
       * <p>
       * 
       * Usually this is due to unsupported change that some specific VCSs
       * support.
       */
      NONE;

      /**
       * @return <code>true</code> if this type is equal to {@link #ADDED}
       */
      public boolean isAdd() {
         return this.equals(ADDED);
      }

      /**
       * @return <code>true</code> if this type is equal to {@link #DELETED}
       */
      public boolean isDelete() {
         return this.equals(DELETED);
      }

      /**
       * @return <code>true</code> if this type is equal to {@link #MODIFIED}
       */
      public boolean isModify() {
         return this.equals(MODIFIED);
      }

      /**
       * @return <code>true</code> if this type is equal to {@link #COPIED}
       */
      public boolean isCopy() {
         return this.equals(COPIED);
      }

      /**
       * @return <code>true</code> if this type is equal to {@link #RENAMED}
       */
      public boolean isRename() {
         return this.equals(RENAMED);
      }

      /**
       * @return <code>true</code> if this type is equal to {@link #NONE}
       */
      public boolean isNone() {
         return this.equals(NONE);
      }
   }

   /**
    * Get the new resource, the representation of the resource in the new
    * revision.
    * <p>
    * It may be null if the change type is {@link Type#DELETED}.
    * 
    * @return the new resource of this change
    */
   T getNewResource();

   /**
    * Get the old resource, the presentation of the resource in the old
    * revision.
    * <p>
    * It may be null if the change type is {@link Type#ADDED}.
    * 
    * @return the old resource of this change
    */
   T getOldResource();

   /**
    * Get the new commit.
    * <p>
    * 
    * @return the new commit of this change
    */
   VCSCommit getNewCommit();

   /**
    * Get the old commit (the base).
    * <p>
    * 
    * @return the old commit of this change
    */
   VCSCommit getOldCommit();

   /**
    * Get the type of this change.
    * <p>
    * 
    * @return the type of this change
    * @see Type
    */
   Type getType();
}
