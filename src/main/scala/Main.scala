import zio.ZIOAppDefault
import zio.http.Server
import api.{TestRoutes, MultiplexRoutes}
import repository.MultiplexRepositoryInMemory

// object Main extends App {
//   println("Hello, World!")
// }

object ZioApp extends ZIOAppDefault {
  override val run = {
    val routes = TestRoutes() ++ MultiplexRoutes()
    println("-------------Running server-------------")
    Server
      .serve(routes)
      .provide(
        Server.defaultWithPort(8081), 
        MultiplexRepositoryInMemory.layer
        )
  }
}
