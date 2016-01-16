package org.basex.index;

/**
 * This enumeration lists available index types.
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Christian Gruen
 */
public enum IndexType {
  /** Attribute names. */
  ATTNAME,
  /** Element names. */
  TAG,
  /** Text index. */
  TEXT,
  /** Attribute index. */
  ATTRIBUTE,
  /** Token index. */
  TOKEN,
  /** Full-text index. */
  FULLTEXT,
  /** Path index. */
  PATH
}
