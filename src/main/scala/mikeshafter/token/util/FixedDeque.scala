package mikeshafter.token.util

import scala.reflect.ClassTag

class FixedDeque[A:ClassTag](capacity: Int) {
  private val arr: Array[A] = new Array[A](capacity)
  private var front: Int = 0
  private var size: Int = 0

  // Delete element from the front
  def deleteFront(): Option[A] = {
    // Empty deque
    if (size == 0)
      None
    else {
      val res = arr(front)
      // Move front index circularly
      front = (front + 1) % capacity
      size -= 1
      Some(res)
    }
  }

  // Insert element at the front
  def insertFront(x: A): Boolean = {
    // Full deque
    if (size == capacity)
      false
    else {
      // Move front index circularly
      front = (front - 1 + capacity) % capacity
      arr(front) = x
      size += 1
      true
    }
  }

  // Insert element at the rear
  def insertRear(x: A): Boolean = {
    // Full deque
    if (size == capacity)
      false
    else {
      // Calculate rear index
      val rear = (front + size) % capacity
      arr(rear) = x
      size += 1
      true
    }
  }

  // Delete element from the rear
  def deleteRear(): Option[A] = {
    // Empty deque
    if (size == 0)
      None
    else {
      // Calculate rear index
      val rear = (front + size - 1) % capacity
      size -= 1
      Some(arr(rear))
    }
  }

  // Get the front element
  def first(): Option[A] = {
    if (size == 0) None else Some(arr(front))
  }

  // Get the rear element
  def last(): Option[A] = {
    if (size == 0) {
      None
    } else {
      // Calculate rear index
      val rear = (front + size - 1) % capacity
      Some(arr(rear))
    }
  }

  // Helper methods for convenience
  def isEmpty: Boolean = size == 0
  def isFull: Boolean = size == capacity
  def length: Int = size
}
