package mikeshafter.token

import scala.collection.mutable


class FixedDeque(limit: Int) extends mutable.ArrayDeque {
this.size

  override def addOne(elem: Nothing): FixedDeque.this.type = super.addOne(elem)

  override def prepend(elem: Nothing): FixedDeque.this.type = super.prepend(elem)

  override def prependAll(elems: IterableOnce[Nothing]): FixedDeque.this.type = super.prependAll(elems)

  override def addAll(elems: IterableOnce[Nothing]): FixedDeque.this.type = super.addAll(elems)

  override def insert(idx: Int, elem: Nothing): Unit = super.insert(idx, elem)

  override def insertAll(idx: Int, elems: IterableOnce[Nothing]): Unit = super.insertAll(idx, elems)
}
