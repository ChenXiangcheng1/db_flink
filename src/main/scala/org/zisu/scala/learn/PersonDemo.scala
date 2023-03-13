package org.zisu.scala.learn

/**
 * 接口多继承案例
 */
object PersonDemo {

  def main(args: Array[String]): Unit = {
    val p1 = new Person("tom")
    val p2 = new Person("jack")

    p1.makeFriends(p2)
    p1.sayHello(p2.name)
  }

}

trait HelloTrait {
  def sayHello(name: String): Unit
}

trait MakeFriendsTrait {
  def makeFriends(p: Person): Unit
}

class Person(val name: String) extends HelloTrait with MakeFriendsTrait {
  override def sayHello(name: String): Unit = {
    println("hello, " + name)
  }

  override def makeFriends(p: Person): Unit = {
    println("my name is " + this.name + ", you name is " + p.name)
  }
}
