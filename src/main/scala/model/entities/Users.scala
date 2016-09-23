package model.entities

import java.util.UUID

/**
  * Created by joaquinbucca on 9/15/16.
  */

case class UserEntity(id: Option[Long] = None, username: String, password: String) {
  require(!username.isEmpty, "username.empty")
  require(!password.isEmpty, "password.empty")
}

case class TokenEntity(userId: Long, token: String = UUID.randomUUID().toString.replaceAll("-", ""))
