package org.basex.index;

import org.basex.query.xquery.path.Step;

/**
 * Container for collecting all information needed, to find a index equivelant
 * query. 
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */

public class FTIndexEq {
  /** Flag for indexuse. */
  public boolean iu;
  /** Flag for sequential processing. */
  public boolean seq;
  /** Current Step. */
  public Step curr;

  /**
   * Constructor.
   */
  public FTIndexEq() {     
  }
  
  /**
   * Constructor.
   * @param c current step
   * @param s boolean for sequential processing
   */
  public FTIndexEq(final Step c, final boolean s) {
    curr = c;
    seq = s;
  }
  
}
