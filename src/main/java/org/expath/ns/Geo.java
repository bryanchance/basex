package org.expath.ns;

import static org.basex.util.Token.*;

import java.io.*;

import org.basex.build.*;
import org.basex.build.xml.*;
import org.basex.io.*;
import org.basex.query.*;
import org.basex.query.util.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.*;
import com.vividsolutions.jts.io.gml2.*;

/**
 * This module contains geo spatial functions for the Geo module.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Masoumeh Seydi
 */
public class Geo extends QueryModule {
  /** GML URI. */
  private static final byte[] GMLURI = token("http://www.opengis.net/gml");

  /** QName gml:Point. */
  private static final QNm Q_GML_POINT = new QNm("gml:Point", GMLURI);
  /** QName gml:MultiPoint. */
  private static final QNm Q_GML_MULTIPOINT = new QNm("gml:MultiPoint", GMLURI);
  /** QName gml:LineString. */
  private static final QNm Q_GML_LINESTRING = new QNm("gml:LineString", GMLURI);
  /** QName gml:LinearRing. */
  private static final QNm Q_GML_LINEARRING = new QNm("gml:LinearRing", GMLURI);
  /** QName gml:MultiLineString. */
  private static final QNm Q_GML_MULTILINESTRING = new QNm("gml:MultiLineString", GMLURI);
  /** QName gml:Polygon. */
  private static final QNm Q_GML_POLYGON = new QNm("gml:Polygon", GMLURI);
  /** QName gml:MultiPolygon. */
  private static final QNm Q_GML_MULTIPOLYGON = new QNm("gml:MultiPolygon", GMLURI);

  /** Array containing all QNames. */
  private static final QNm[] QNAMES = {
    Q_GML_POINT, Q_GML_LINESTRING, Q_GML_POLYGON, Q_GML_MULTIPOINT,
    Q_GML_MULTILINESTRING, Q_GML_MULTIPOLYGON, Q_GML_LINEARRING
  };

  /**
   * Returns the dimension of an item.
   * @param node xml element containing gml object(s)
   * @return dimension
   * @throws QueryException query exception
   */
  public Int dimension(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Int.get(geom.getDimension());
  }

  /**
   * Returns the name of the geometry type in the GML namespace, or the empty sequence.
   * @param node xml element containing gml object(s)
   * @return geometry type
   * @throws QueryException query exception
   */
  public QNm geometryType(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : new QNm("gml:" + geom.getGeometryType());
  }

  /**
   * Returns the name of the geometry type in the GML namespace, or the empty sequence.
   * @param node xml element containing gml object(s)
   * @return integer value of CRS of the geometry
   * @throws QueryException query exception
   */
  public Int getSRID(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Int.get(geom.getSRID());
  }

  /**
   * Returns the gml:Envelope of the specified geometry.
   * The envelope is the minimum bounding box of this geometry.
   * @param node xml element containing gml object(s)
   * @return envelop element
   * @throws QueryException query exception
   */
  public Value envelope(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.getEnvelope());
  }

  /**
   * Returns the WKT format of a geometry.
   * @param node xml element containing gml object(s)
   * @return Well-Known Text geometry representation
   * @throws QueryException query exception
   */
  public Str asText(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Str.get(new WKTWriter().write(geom));
  }

  /**
   * Returns the WKB format of a geometry.
   * @param node xml element containing gml object(s)
   * @return Well-Known Binary geometry representation
   * @throws QueryException query exception
   */
  public B64 asBinary(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : new B64(new WKBWriter().write(geom));
  }

  /**
   * Returns a boolean value which shows if the specified geometry is simple or not,
   * which has no anomalous geometric points, such as self intersection or self tangency.
   * @param node xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln isSimple(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Bln.get(geom.isSimple());
  }

  /**
   * Returns the boundary of the geometry, in GML.
   * The return value is a sequence of either gml:Point or gml:LinearRing elements.
   * @param node xml element containing gml object(s)
   * @return boundary element (geometry)
   * @throws QueryException query exception
   */
  public Value boundary(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.getBoundary());
  }

  /**
   * Returns a boolean value that shows if two geometries are equal or not.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln equals(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.equals(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry is disjoint to another geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln disjoint(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.disjoint(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry intersects another geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln intersects(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.intersects(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry touches the specified geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln touches(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.touches(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry crosses the specified geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln crosses(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.crosses(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry is within the specified geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln within(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.within(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry contains the specified geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln contains(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.contains(geom2));
  }

  /**
   * Returns a boolean value that shows if this geometry overlaps the specified geometry.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln overlaps(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Bln.get(geom1.overlaps(geom2));
  }

  /**
   * Returns a boolean value that shows if whether relationships between the boundaries,
   * interiors and exteriors of two geometries match
   * the pattern specified in intersection-matrix-pattern.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @param intersectionMatrix intersection matrix for two geometries
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln relate(final ANode node1, final ANode node2, final Str intersectionMatrix)
      throws QueryException {

    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null :
      Bln.get(geom1.relate(geom2, intersectionMatrix.toJava()));
  }

  /**
   * Returns the shortest distance in the units of the spatial reference system
   * of geometry, between the geometries.
   * The distance is the distance between a point on each of the geometries.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return distance double value
   * @throws QueryException query exception
   */
  public Dbl distance(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : Dbl.get(geom1.distance(geom2));
  }

  /**
   * Returns a polygon that represents all Points whose distance from this
   * geometric object is less than or equal to distance.
   * The returned element must be either gml:Polygon, gml:LineString or gml:Point.
   * @param node xml element containing gml object(s)
   * @param distance specific distance from the $geometry (the buffer width)
   * @return buffer geometry as gml element
   * @throws QueryException query exception
   */
  public Value buffer(final ANode node, final Dbl distance) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.buffer(distance.dbl()));
  }

  /**
   * Returns the convex hull geometry of a geometry in GML, or the empty sequence.
   * The returned element must be either gml:Polygon, gml:LineString or gml:Point.
   * @param node xml element containing gml object(s)
   * @return convex hull geometry as a gml element
   * @throws QueryException query exception
   */
  public Value convexHull(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.convexHull());
  }

  /**
   * Returns a geometric object representing the Point set intersection of two geometries.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return intersection geometry as a gml element
   * @throws QueryException query exception
   */
  public Value intersection(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : gmlWriter(geom1.intersection(geom2));
  }

  /**
   * Returns a geometric object that represents the Point set union of two geometries.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return union geometry as a gml element
   * @throws QueryException query exception
   */
  public Value union(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : gmlWriter(geom1.union(geom2));
  }

  /**
   * Returns a geometric object that represents the
   * Point set difference of two geometries.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return difference geometry as a gml element
   * @throws QueryException query exception
   */
  public Value difference(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : gmlWriter(geom1.difference(geom2));
  }

  /**
   * Returns a geometric object that represents the
   * Point set symmetric difference of two geometries.
   * @param node1 xml element containing gml object(s)
   * @param node2 xml element containing gml object(s)
   * @return symmetric difference geometry as a gml element
   * @throws QueryException query exception
   */
  public Value symDifference(final ANode node1, final ANode node2) throws QueryException {
    final Geometry geom1 = gmlReader(node1);
    final Geometry geom2 = gmlReader(node2);
    return geom1 == null || geom2 == null ? null : gmlWriter(geom1.symDifference(geom2));
  }

  /**
   * Returns number of geometries in a geometry collection,
   * or 1 if the input is not a collection.
   * @param node xml element containing gml object(s)
   * @return integer value of number of geometries
   * @throws QueryException query exception
   */
  public Int numGeometries(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Int.get(geom.getNumGeometries());
  }

  /**
   * Returns the nth geometry of a geometry collection,
   * or the geometry if the input is not a collection.
   * @param node xml element containing gml object(s)
   * @param geoNumber integer number as the index of nth geometry
   * @return geometry as a gml element
   * @throws QueryException query exception
   */
  public Value geometryN(final ANode node, final Int geoNumber) throws QueryException {
    final Geometry geom = gmlReader(node);
    if(geom == null) return null;

    final long n = geoNumber.itr();
    if(n < 1 || n > geom.getNumGeometries()) throw GeoErrors.outOfRangeIdx(geoNumber);
    return gmlWriter(geom.getGeometryN((int) n - 1));
  }

  /**
   * Returns the x-coordinate value for point.
   * @param node xml element containing gml object(s)
   * @return x double value
   * @throws QueryException query exception
   */
  public Dbl x(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_POINT);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.pointNeeded(node.qname().local());

    return Dbl.get(geom.getCoordinate().x);
  }

  /**
   * Returns the y-coordinate value for point.
   * @param node xml element containing gml object(s)
   * @return y double value
   * @throws QueryException query exception
   */
  public Dbl y(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_POINT);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.pointNeeded(node.qname().local());

    return Dbl.get(geom.getCoordinate().y);
  }

  /**
   * Returns the z-coordinate value for point.
   * @param node xml element containing gml object(s)
   * @return z double value
   * @throws QueryException query exception
   */
  public Dbl z(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_POINT);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.pointNeeded(node.qname().local());

    return Dbl.get(geom.getCoordinate().z);
  }

  /**
   * Returns the length of this Geometry. Linear geometries return their length.
   * Areal geometries return their parameter. Others return 0.0
   * @param node xml element containing gml object(s)
   * @return length double value
   * @throws QueryException query exception
   */
  public Dbl length(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Dbl.get(geom.getLength());
  }

  /**
   * Returns the start point of a line.
   * @param node xml element containing gml object(s)
   * @return start point geometry as a gml element
   * @throws QueryException query exception
   */
  public Value startPoint(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_LINEARRING, Q_GML_LINESTRING);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.lineNeeded(node.qname().local());

    return gmlWriter(geom instanceof LineString ?
       ((LineString) geom).getStartPoint() :
       ((LinearRing) geom).getStartPoint());
  }

  /**
   * Returns the end point of a line.
   * @param node xml element containing gml object(s)
   * @return end point geometry as a gml element
   * @throws QueryException query exception
   */
  public Value endPoint(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_LINEARRING, Q_GML_LINESTRING);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.lineNeeded(node.qname().local());

    return gmlWriter(geom instanceof LineString ?
       ((LineString) geom).getEndPoint() :
       ((LinearRing) geom).getEndPoint());
  }

  /**
   * Checks if the line is closed loop.
   * That is, if the start Point is same with end Point.
   * @param node xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln isClosed(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node,
        Q_GML_LINEARRING, Q_GML_LINESTRING, Q_GML_MULTILINESTRING);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.lineNeeded(node.qname().local());

    return Bln.get(geom instanceof LineString ?
       ((LineString) geom).isClosed() : geom instanceof LinearRing ?
       ((LinearRing) geom).isClosed() :
       ((MultiLineString) geom).isClosed());
  }

  /**
   * Return a boolean value that shows weather the line is a ring or not.
   * A line is a ring if it is closed and simple.
   * @param node xml element containing gml object(s)
   * @return boolean value
   * @throws QueryException query exception
   */
  public Bln isRing(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_LINEARRING, Q_GML_LINESTRING);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.lineNeeded(node.qname().local());

    return Bln.get(geom instanceof LineString ?
       ((LineString) geom).isRing() :
       ((LinearRing) geom).isRing());
  }

  /**
   * Returns the number of points in a geometry.
   * @param node xml element containing gml object(s)
   * @return number of points int value
   * @throws QueryException query exception
   */
  public Int numPoints(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Int.get(geom.getNumPoints());
  }

  /**
   * Returns the nth point of a line.
   * @param node xml element containing gml object(s)
   * @param pointNumber index of i-th point
   * @return n-th point as a gml element
   * @throws QueryException query exception
   */
  public Value pointN(final ANode node, final Int pointNumber) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_LINEARRING, Q_GML_LINESTRING);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.lineNeeded(node.qname().local());

    final int max = geom.getNumPoints();
    final long n = pointNumber.itr();
    if(n < 1 || n > max) throw GeoErrors.outOfRangeIdx(pointNumber);

    return gmlWriter(geom instanceof LineString ?
       ((LineString) geom).getPointN((int) n - 1) :
       ((LinearRing) geom).getPointN((int) n - 1));
  }

  /**
   * Returns the area of a Geometry. Areal Geometries have a non-zero area.
   * Returns zero for Point and Lines.
   * @param node xml element containing gml object(s)
   * @return geometry area as a double vaue
   * @throws QueryException query exception
   */
  public Dbl area(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : Dbl.get(geom.getArea());
  }

  /**
   * Returns the mathematical centroid of the geometry as a gml:Point.
   * The point is not guaranteed to be on the surface.
   * @param node xml element containing gml object(s)
   * @return centroid geometry as a gml element
   * @throws QueryException query exception
   */
  public Value centroid(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.getCentroid());
  }

  /**
   * Returns a gml:Point that is interior of this geometry.
   * If it cannot be inside the geometry, then it will be on the boundary.
   * @param node xml element containing gml object(s)
   * @return a point as a gml element
   * @throws QueryException query exception
   */
  public Value pointOnSurface(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node);
    return geom == null ? null : gmlWriter(geom.getInteriorPoint());
  }

  /**
   * Returns the outer ring of a polygon, in GML.
   * @param node xml element containing gml object(s)
   * @return exterior ring geometry (LineString) as a gml element
   * @throws QueryException query exception
   */
  public Value exteriorRing(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_POLYGON);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.polygonNeeded(node.qname().local());

    return gmlWriter(((Polygon) geom).getExteriorRing());
  }

  /**
   * Returns the number of interior rings in a polygon.
   * @param node xml element containing gml object(s)
   * @return integer number of interior rings
   * @throws QueryException query exception
   */
  public Int numInteriorRing(final ANode node) throws QueryException {
    final Geometry geom = gmlReader(node, Q_GML_POLYGON);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.polygonNeeded(node.qname().local());

    return Int.get(((Polygon) geom).getNumInteriorRing());
  }

  /**
   * Returns the nth geometry of a geometry collection.
   * @param node xml element containing gml object(s)
   * @param ringNumber index of i-th interior ring
   * @return n-th interior ring geometry (LineString) as a gml element
   * @throws QueryException query exception
   */
  public Value interiorRingN(final ANode node, final Int ringNumber)
      throws QueryException {

    final Geometry geom = gmlReader(node, Q_GML_POLYGON);
    if(geom == null && gmlReader(node) != null)
      throw GeoErrors.polygonNeeded(node.qname().local());

    final long n = ringNumber.itr();
    final int max = ((Polygon) geom).getNumInteriorRing();
    if(n < 1 || n > max) throw GeoErrors.outOfRangeIdx(ringNumber);
    return gmlWriter(((Polygon) geom).getInteriorRingN((int) n - 1));
  }

  // PRIVATE METHODS (hidden from user of module) ========================================

  /**
   * Reads an element as a gml node and returns the geometry.
   * @param element xml node containing gml object(s)
   * @param geoName the geometry type to be used to validate the node qname.
   * @return geometry, or {@code null}
   * @throws QueryException exception
   */
  private Geometry gmlReader(final ANode element, final QNm... geoName)
      throws QueryException {

    if(!checkNode(element, geoName)) return null;
    try {
      final String input = element.serialize().toString();
      final GMLReader gmlReader = new GMLReader();
      final GeometryFactory geoFactory = new GeometryFactory();
      return gmlReader.read(input, geoFactory);
    } catch (final Throwable e) {
      throw GeoErrors.gmlReaderErr(e);
    }
  }

  /**
   * Checks if the node is an element with a valid QName.
   * @param  node xml element containing gml object(s)
   * @param geoName the geometry type to be used to validate the node qname.
   * @return boolean value
   * @throws QueryException exception
   */
  private boolean checkNode(final ANode node, final QNm... geoName)
      throws QueryException {

    if(node.type != NodeType.ELM)
      Err.FUNCMP.thrw(null, this, NodeType.ELM, node.type);

    final QNm qname = node.qname();
    if(geoName.length != 0) {
      // check limited set of types
      for(final QNm geo : geoName) if(qname.eq(geo)) return true;
      return false;
    }
    // check all supported types
    for(final QNm geo : QNAMES) if(qname.eq(geo)) return true;
    throw GeoErrors.unrecognizedGeo(qname.local());
  }

  /**
   * Writes an geometry and returns a string representation of the geometry.
   * @param geometry geometry
   * @return string output written string
   * @throws QueryException exception
   */
  private DBNode gmlWriter(final Geometry geometry) throws QueryException {
    if(geometry.isEmpty()) return null;

    String geom;
    try {
      geom = new GMLWriter().write(geometry);
    } catch(final IOException ex) {
      throw GeoErrors.gmlWriterErr(ex);
    }

    try {
      final IO io = new IOContent(geom);
      return new DBNode(MemBuilder.build(new XMLParser(io, context.context.prop)));
    } catch(final IOException ex) {
      throw Err.IOERR.thrw(null, ex);
    }
  }
}