package store.redis

import akka.actor.ActorSystem
import env.RedisConfig
import redis.{RedisClientMasterSlaves, RedisServer}

object RedisClient {
  def redisClient(configuration: Option[RedisConfig], actorSystem: ActorSystem): Option[RedisClientMasterSlaves] =
    configuration.map { redisConfig =>
      val master = RedisServer(
        host = redisConfig.host,
        port = redisConfig.port,
        password = redisConfig.password,
        db = redisConfig.databaseId
      )

      val slaves: Seq[RedisServer] = redisConfig.slaves
        .map { configs =>
          configs.map { config =>
            RedisServer(
              host = config.host,
              port = config.port,
              password = config.password,
              db = config.databaseId
            )
          }
        }
        .getOrElse(Seq.empty[RedisServer])
      val cli: RedisClientMasterSlaves = RedisClientMasterSlaves(
        master,
        slaves
      )(actorSystem)
      cli
    }

}
