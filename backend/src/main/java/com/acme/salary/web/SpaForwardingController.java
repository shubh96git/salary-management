package com.acme.salary.web;

import org.springframework.stereotype.Controller;

/**
 * Serves the Angular app for its own routes, so refreshing or bookmarking a page such as
 * /insights loads the app instead of returning 404. API and static file paths are not affected.
 */
@Controller
class SpaForwardingController {
}
