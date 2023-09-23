import zio.ZIOAppDefault
import zio.http.Server

import api.{MultiplexRoutes, MultiplexHandlerBasic}
import repository.MultiplexRepositoryInMemory

// object Main extends App {
//   println("Hello, World!")
// }

object ZioApp extends ZIOAppDefault {
  val layers = MultiplexRepositoryInMemory.layer ++ MultiplexHandlerBasic.layer

  override val run = {
    val routes = MultiplexRoutes()
    println("-------------Running server-------------")
    Server
      .serve(routes)
      .provide(
        Server.defaultWithPort(8081), 
        layers
        )
  }
}
