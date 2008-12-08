package org.basex.query.xquery.expr;

import java.io.IOException;

import org.basex.data.MetaData;
import org.basex.data.Serializer;
import org.basex.index.FTIndexAcsbl;
import org.basex.index.FTIndexEq;
import org.basex.query.FTOpt;
import org.basex.query.xquery.XQException;
import org.basex.query.xquery.XQContext;
import org.basex.query.xquery.iter.Iter;

/**
 * FTOptions expression.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class FTOptions extends FTExpr {
  /** FTOptions. */
  public FTOpt opt;

  /**
   * Constructor.
   * @param e expression
   * @param o ft options
   */
  public FTOptions(final FTExpr e, final FTOpt o) {
    super(e);
    opt = o;
  }

  @Override
  public FTExpr comp(final XQContext ctx) throws XQException {
    final FTOpt tmp = ctx.ftopt;
    opt.compile(tmp);
    ctx.ftopt = opt;
    expr[0] = expr[0].comp(ctx);
    ctx.ftopt = tmp;
    return this;
  }

  @Override
  public Iter iter(final XQContext ctx) throws XQException {
    final FTOpt tmp = ctx.ftopt;
    ctx.ftopt = opt;
    final Iter it = ctx.iter(expr[0]);
    ctx.ftopt = tmp;
    return it;
  }

  @Override
  public void indexAccessible(final XQContext ctx, final FTIndexAcsbl ia) 
    throws XQException {
    // if the following conditions yield true, the index is accessed:
    // - case sensitivity, diacritics and stemming flag comply with index flag
    // - no stop words are specified
    // - if wildcards are specified, the fulltext index is a trie
    final MetaData meta = ia.data.meta;
    ia.io = meta.ftcs == opt.is(FTOpt.CS) &&
    meta.ftdc == opt.is(FTOpt.DC) && meta.ftst == opt.is(FTOpt.ST) &&
    opt.sw == null && (!opt.is(FTOpt.WC) || !meta.ftfz);
    final FTOpt tmp = ctx.ftopt;
    ctx.ftopt = opt;
    expr[0].indexAccessible(ctx, ia);
    ctx.ftopt = tmp;
  }

  @Override
  public Expr indexEquivalent(final XQContext ctx, final FTIndexEq ieq) {
    return new FTOptions((FTExpr) expr[0].indexEquivalent(ctx, ieq), opt);
  }

  
  @Override
  public void plan(final Serializer ser) throws IOException {
    ser.startElement(this);
    opt.plan(ser);
    ser.finishElement();
    expr[0].plan(ser);
    ser.closeElement();
  }

  @Override
  public String toString() {
    return expr[0].toString() + opt;
  }
}
