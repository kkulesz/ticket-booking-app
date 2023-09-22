import zio.ZIOAppDefault
import zio.http.Server
import api.TestRoutes

// object Main extends App {
//   println("Hello, World!")
// }

object ZioApp extends ZIOAppDefault {
  override val run = {
    var routes = TestRoutes()
    println("-------------Running server-------------")
    Server.serve(routes).provide(Server.defaultWithPort(8081))
  }
}
