package dev.christopherbell.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC controller serving static application views.
 */
@Controller
public class ViewController {

  /**
   * Serves the SPA index page for primary app routes.
   *
   * @return {@code index.html}
   */
  @RequestMapping(value = {"/", "/blog", "/photos", "/wfl"})
  public String getHomePage() {
    return "index.html";
  }

  @RequestMapping(value = "/{path:^(?!api|static|.*\\.).*$}")
  public String redirect() {
    return "forward:/index.html";
  }

  /**
   * Serves the Void home page.
   *
   * @return {@code void/index.html}
   */
  @GetMapping(value = "/void")
  public String getVoidHomePage(HttpServletRequest request) {
    return "void/index.html";
  }

  /**
   * Serves the Profile page.
   *
   * @return {@code profile.html}
   */
  @GetMapping(value = "/profile")
  public String getProfilePage(HttpServletRequest request) {
    return "profile.html";
  }

  /**
   * Serves the login page.
   *
   * @return {@code login.html}
   */
  @GetMapping(value = "/login")
  public String getLoginPage(HttpServletRequest request) {
    return "login.html";
  }

  /**
   * Serves the sign-up page.
   *
   * @return {@code signup.html}
   */
  @GetMapping(value = "/signup")
  public String getSignupPage(HttpServletRequest request) {
    return "signup.html";
  }

  /**
   * Serves a public user profile/feed page by username.
   */
  @GetMapping(value = "/u/{username}")
  public String getPublicUserPage(HttpServletRequest request) {
    return "user.html";
  }

  /**
   * Serves the Void login page.
   *
   * @return {@code void/login.html}
   */
  @GetMapping(value = "/void/login")
  public String getVoidLoginPage(HttpServletRequest request) {
    return "void/login.html";
  }

  /**
   * Serves the Void signup page.
   *
   * @return {@code void/sign_up.html}
   */
  @GetMapping(value = "/void/signup")
  public String getVoidCreateAccountPage(HttpServletRequest request) {
    return "void/sign_up.html";
  }

  /**
   * Serves The Bell home page.
   *
   * @return {@code thebell/index.html}
   */
  @GetMapping(value = "/thebell")
  public String getTheBellHomePage(HttpServletRequest request) {
    return "thebell/index.html";
  }

  /**
   * Serves The Bell "Tony" page.
   *
   * @return {@code thebell/tony.html}
   */
  @GetMapping(value = "/thebell/tony")
  public String getTheBellTonyPage(HttpServletRequest request) {
    return "thebell/tony.html";
  }

  /** Serves an individual post page by id. */
  @GetMapping(value = "/p/{postId}")
  public String getPostPage(HttpServletRequest request) {
    return "post.html";
  }
}
