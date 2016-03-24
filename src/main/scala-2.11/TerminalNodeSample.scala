/*
* Copyright 2015-2016 Pragmukko Project [http://github.org/pragmukko]
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
*    [http://www.apache.org/licenses/LICENSE-2.0]
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
import actors.Messages.{MoveByGlobal, MoveBy, BinnaryCmd, ListMembers}
import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.cluster.{Member, Cluster}
import mavlink.pixhawk.DroneCommands
import utils.{ClusterNode, ConfigProvider}
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.io.StdIn
import akka.pattern._

/**
 * Created by max on 11/11/15.
 */
object TerminalNodeSample extends App with ClusterNode with DroneCommands {

  var handler:PartialFunction[List[String], Unit] = startHandler
  var intro = "> "

  while (true) {
    (handler orElse commonHandler) (Option(StdIn.readLine(intro)).map(_.split("\\s").toList).toList.flatten)
  }

  def startHandler:PartialFunction[List[String], Unit] = {

    case "list" :: Nil =>
      listMembers.zipWithIndex.foreach {
        m => println( List(m._2, m._1.uniqueAddress.address, m._1.roles.mkString(" ")).mkString(" "))
      }
      println()
    case "listm" :: Nil =>
      listMemberIds foreach { println }

    case "usem" :: memberId :: Nil =>
      if (listMemberIds.contains(memberId)) {
        handler = selectedTopicHandler(memberId)
        intro = s"$memberId > "
      } else println("Unknown member ID")

    case "use" :: index :: Nil =>
      handler = selectedNodeHandler(embedded(listMembers(index.toInt)))
      intro = listMembers(index.toInt).address + "> "

  }

  def selectedTopicHandler(memberId: String): PartialFunction[List[String], Unit] = {
    case "ping" :: Nil => println(askMember(memberId)("ping"))

    case "moveto" :: x :: y :: z :: Nil =>
      sendToMember(memberId)(moveTo(x.toFloat, y.toFloat, z.toFloat))

    case "direction" :: x :: y :: z :: Nil =>
      sendToMember(memberId)(moveTo(0, 0, 0, x.toFloat, y.toFloat, z.toFloat))

    case "where" :: Nil =>
      println( askMember(memberId)("where are you man?"))

    case "where_g" :: Nil =>
      println( askMember(memberId)("where are you man globally?"))

    case "moveby" :: dx :: dy :: dz :: Nil =>
      sendToMember(memberId)(MoveBy(dx.toFloat, dy.toFloat, dz.toFloat))

    case "moveby_g" :: dx :: dy :: dz :: Nil =>
      sendToMember(memberId)(MoveByGlobal(dx.toLong, dy.toLong, dz.toFloat))

    case "bye" :: Nil =>
      handler = startHandler
      intro = "> "

    case other => println(s"""Unknown command: '${other.mkString(" ")}'""")

  }

  def selectedNodeHandler(member:ActorRef) : PartialFunction[List[String], Unit] = {

    case "ping" :: Nil => println(member ?? "ping")

    case "moveto" :: x :: y :: z :: Nil =>
      member ! moveTo(x.toFloat, y.toFloat, z.toFloat)

    case "direction" :: vx :: vy :: Nil =>
      member ! moveTo(0, 0, 0, vx.toFloat, vy.toFloat, 0f)

    case "where" :: Nil =>
      println( member ?? "where are you man?" )

    case "where_g" :: Nil =>
      println( member ?? "where are you man globally?" )

    case "moveby" :: dx :: dy :: dz :: Nil =>
      member ! MoveBy(dx.toFloat, dy.toFloat, dz.toFloat)

    case "moveby_g" :: dx :: dy :: dz :: Nil =>
      member ! MoveByGlobal(dx.toLong, dy.toLong, dz.toFloat)

    case "bye" :: Nil =>
      handler = startHandler
      intro = "> "

  }

  def commonHandler: PartialFunction[List[String], Unit] = {

    case "help" :: rest => println(
      """
        | list  - list of cluster members
        | use X - start work with X member of cluster nodes
        | exit  - close terminal
        |
        |  Next commands works only after cluster node selected with 'use x'
        |
        | ping            - ping node (node mast return 'Pong')
        | where           - ask drone for physical position
        | moveto X Y Z    - tell drone move to new position relative to current
        | direction X Y Z - set vector of movement
        | bye             - stop working with current node
        |
      """.stripMargin)

    case "exit" :: Nil => System.exit(0)

    case other => println(s"""Unknown command: '${other.mkString(" ")}'""")

  }

}

