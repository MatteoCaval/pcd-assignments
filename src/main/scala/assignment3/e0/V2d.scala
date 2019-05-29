/*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 *//*
 *   V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package assignment3.e0

/**
  *
  * 2-dimensional vector
  * objects are completely state-less
  *
  */
class V2d(var x: Double, var y: Double) extends Serializable {
  def sum(v: V2d) = new V2d(x + v.x, y + v.y)

  def abs: Double = Math.sqrt(x * x + y * y).toDouble

  def getNormalized: V2d = {
    val module = Math.sqrt(x * x + y * y).toDouble
    new V2d(x / module, y / module)
  }

  def mul(fact: Double) = new V2d(x * fact, y * fact)

  override def toString: String = "V2d(" + x + "," + y + ")"
}