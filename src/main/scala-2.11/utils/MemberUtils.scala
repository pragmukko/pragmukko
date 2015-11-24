package utils

import akka.cluster.Member

/**
 * Created by yishchuk on 05.11.2015.
 */
object MemberUtils {

  implicit class MemberWithId(member: Member) {
    def id = member.roles.find(_.startsWith("id:")).map {
      _.substring(3).trim()
    }
  }
  final case class MemberNotFound(memberId: String) extends RuntimeException(s"Member '$memberId' not found or inactive")
}
