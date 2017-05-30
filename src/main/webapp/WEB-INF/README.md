## Information on the used libraries
**Important** : All the manually-downloaded libraries which were here intially are now being installed using npm.

### Libraries present:
* `materialize`: Materialize is a modern responsive CSS framework based on Material Design by Google.
* `backbone`: Used as the primary front-end MVC framework for this project.
* `backbone.localstorage`: For sessions.
* `cookie`: It is a library that helps in storing cookies.
* `jquery`: Used by materialize and in backbone for AJAX calls and component selections.
* `src-noconfict`: Contains ACE code editor. Used to provide an alternative to P4 file upload by providing an interface to write the P4 code on the browser.
* `underscore`: It is a library which provides utility functions for common programming tasks. Highly integrable with backbone.
* `views`: Contains all the backbone views which render and serve the html pages.

### Libraries removed:
* `interact`: It is a library used for drag and drop, resizing and multi-touch gesture features. It was present as there were plans to make a physical graph by drag and drop as a substitute to using the P4 code.
* `bootstrap`: It is a front-end framework. In this project, it has been replaced by a newer materialize-css which follows material design.
* `todomvc-common`: Was used for custom css and js files. No longer used.
* `w2ui`: It is a Javascript support library for bootstrap. As bootstrap is scrapped, w2ui is no longer needed.
* `wysiwyg`: It is a library for putting a WYSIWYG editor for the description part of the experiment. We might go with a markdown editor than a WYSIWYG one.
* `dragula`: Similar to interact. It is used for drag and drop.