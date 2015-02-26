 *
 *
 *
   protected RevCommit commit;   

    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
                     // in cases it is coppied/renamed).
          *
    *
          *
          *
    *
      try {
         final RevCommit base = walk.parseCommit(this.commit);
         final List<RevCommit> heads = new ArrayList<RevCommit>();
         for (final Ref ref : refs) {
            final RevCommit head = walk.parseCommit(ref.getObjectId());

            // Case when this commit is head
            if (AnyObjectId.equals(head, base)) {
               continue;
            }
            // If this commit is newer then the current head
            // do nothing else if is ancestor of head add it to list
            if (RevUtils.isAncestor(base, head, this.repo)) {
               heads.add(head);
            }

         return heads;
      } finally {
         walk.release();
      try {
         // Construct the path accordingly
         final VCSResource.Type type = RevUtils.resourceType(walk
               .getFileMode(0));
         if (type.equals(VCSResource.Type.FILE)) {
            return new VCSFileImp(this, path);
         } else if (type.equals(VCSResource.Type.DIR)) {
            return new VCSDirectoryImp(this, path);
         }
      } finally {
         walk.release();
         if (walker != null) {
            walker.release();
         }
    *
    *
    *
      } catch (final Exception e) {
         if (walker != null) {
            walker.release();
         }
    *
      if (this == obj) {
      }
      if (obj == null) {
      }
      if (getClass() != obj.getClass()) {
      }
         if (other.commit != null) {
         }
      } else if (!commit.equals(other.commit)) {
      }
         if (other.repo != null) {
         }
      } else if (!repo.getDirectory().equals(other.repo.getDirectory())) {
      }