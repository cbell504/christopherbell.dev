package dev.christopherbell.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

  /**
   * Returns the home page.
   *
   * @return index/home
   */
  @RequestMapping(value = {"/", "/blog", "/photos", "/login", "/signup", "/wfl"})
  public String getHomePage() {
    return "index.html";
  }

  @RequestMapping(value = "/{path:^(?!api|static|.*\\.).*$}")
  public String redirect() {
    return "forward:/index.html";
  }

  /**
   * Returns the void's home page.
   *
   * @return index/html
   */
  @GetMapping(value = "/void")
  public String getVoidHomePage(HttpServletRequest request) {
    return "void/index.html";
  }

  /**
   * Returns the void's login page.
   *
   * @return login/html
   */
  @GetMapping(value = "/void/login")
  public String getVoidLoginPage(HttpServletRequest request) {
    return "void/login.html";
  }

  /**
   * Returns the void's create account page.
   *
   * @return sign_up/html
   */
  @GetMapping(value = "/void/signup")
  public String getVoidCreateAccountPage(HttpServletRequest request) {
    return "void/sign_up.html";
  }

  /**
   * Returns The Bell's home page.
   *
   * @return index/html
   */
  @GetMapping(value = "/thebell")
  public String getTheBellHomePage(HttpServletRequest request) {
    return "thebell/index.html";
  }

  /**
   * Returns The Bell's Tony page.
   *
   * @return tony/html
   */
  @GetMapping(value = "/thebell/tony")
  public String getTheBellTonyPage(HttpServletRequest request) {
    return "thebell/tony.html";
  }

}
