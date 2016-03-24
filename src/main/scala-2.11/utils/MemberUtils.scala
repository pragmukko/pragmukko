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
