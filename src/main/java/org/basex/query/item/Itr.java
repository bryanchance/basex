package org.basex.query.item;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.basex.query.QueryException;
import org.basex.util.Token;

/**
 * Integer item.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public class Itr extends Item {
  /** Zero value. */
  public static final Itr ZERO = new Itr(0);
  /** Constant values. */
  private static final Itr[] NUM = { ZERO, new Itr(1), new Itr(2),
    new Itr(3), new Itr(4), new Itr(5), new Itr(6), new Itr(7),
    new Itr(8), new Itr(9) };
  /** Integer value. */
  private final long val;

  /**
   * Constructor.
   * @param v value
   */
  private Itr(final long v) {
    this(v, Type.ITR);
  }

  /**
   * Constructor.
   * @param v value
   * @param t data type
   */
  Itr(final long v, final Type t) {
    super(t);
    val = v;
  }

  /**
   * Constructor.
   * @param d date time
   */
  Itr(final Date d) {
    this(d.xc.toGregorianCalendar().getTimeInMillis(), Type.LNG);
  }

  /**
   * Returns an instance of this class.
   * @param v value
   * @return instance
   */
  public static Itr get(final long v) {
    return v >= 0 && v <= 9 ? NUM[(int) v] : new Itr(v);
  }

  /**
   * Returns an instance of this class.
   * @param v value
   * @param t data type
   * @return instance
   */
  public static Itr get(final long v, final Type t) {
    return t == Type.ITR ? get(v) : new Itr(v, t);
  }

  @Override
  public final byte[] str() {
    return val == 0 ? Token.ZERO : Token.token(val);
  }

  @Override
  public final boolean bool() {
    return val != 0;
  }

  @Override
  public final long itr() {
    return val;
  }

  @Override
  public final float flt() {
    return val;
  }

  @Override
  public final double dbl() {
    return val;
  }

  @Override
  public final BigDecimal dec() {
    return BigDecimal.valueOf(val);
  }

  @Override
  public final boolean eq(final Item it) throws QueryException {
    return it instanceof Itr ? val == ((Itr) it).val : val == it.dbl();
  }

  @Override
  public final int diff(final Item it) throws QueryException {
    if(it instanceof Itr) {
      final long i = ((Itr) it).val;
      return val < i ? -1 : val > i ? 1 : 0;
    }
    final double n = it.dbl();
    return n != n ? UNDEF : val < n ? -1 : val > n ? 1 : 0;
  }

  @Override
  public final Object toJava() {
    switch(type) {
      case BYT: return (byte) val;
      case SHR:
      case UBY: return (short) val;
      case INT:
      case USH: return (int) val;
      case LNG:
      case UIN: return val;
      default:  return new BigInteger(Token.string(str()));
    }
  }

  @Override
  public final int hashCode() {
    return (int) val;
  }

  /**
   * Converts the given item into a long value.
   * @param val value to be converted
   * @return long value
   * @throws QueryException query exception
   */
  static long parse(final byte[] val) throws QueryException {
    try {
      final String v = Token.string(Token.trim(val));
      return Long.parseLong(v.startsWith("+") ? v.substring(1) : v);
    } catch(final NumberFormatException ex) {
      ZERO.castErr(val);
      return 0;
    }
  }
}
