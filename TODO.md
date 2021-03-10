# TODO

## Features

### high priority
* ~~piracy check: check periodically if valid purchase and disable/enable premium accordingly~~
* ~~create releases config + upload it as beta to the play store~~
* ~~show changelog somewhere~~
* ~~landscape layout~~
   * ~~restore values on configuration change (-> activated filters)~~
   * ~~keep position of shutter-snappyRecyclerView and snap it~~

### normal priority
* examine: change two base themes to be variants of Theme.AppCompat.DayNight?
* add (optional) aperture selection to fine-tune resulting exposure time
* add (optional) ISO selection to fine-tune resulting exposure time
* implement timer
   * make system alarm when in background
   * (also display days, not just hours)
   * ~~restore state on configuration change~~
   * ~~add a reset button for when paused~~
   * ~~better layout on large screens~~
   * ~~make alarm sound quieter~~
   * ~~vibrate~~
   * ~~fix: wrong state when finished and configuration change happens~~
   * ~~better blinking~~
* add privacy policy
   * (if using crashlytics: http://try.crashlytics.com/terms/privacy-policy.pdf)
* ~~decrease distance of numbers in resultView~~
* ~~additional themes~~
   * ~~dark~~
   * ~~all black~~
   * ~~all white (?)~~
* ~~filter calibrator~~
* ~~Crashlytics~~
* ~~monetization~~

### low priority
* German localization
* add setting: set minimal exposure time (1/8000?, 1/4000? etc.)
* android wear support (timer)
* intro/howtos on
   * using nd filters
   * using this app
   * when to use which exposure time (foggy waves, dynamic waves, disappearing people, ...)
   * how to use the timer when no remote shutter release is at hand
* add Google Analytics
   * How many filters are used?
   * How often is the calibrator used (when adding/editing a new filter)?
   * How often is the timer used?
   * ~~What theme is used?~~
* ~~animate addition/removal of filters in `myfilters`~~


## Bugs
* ~~filter sort order: make it case insensitive~~
* ~~when shutter-list is scrolled and user changes ev step size, list is repositioned and not snapped any more.~~


## Monetization
* freemium-features:
   1. additional themes
   2. more than 3 (or 4?) filters
   3. timer
* ~~maybe instead: implement donation via In-App Purchase~~
   * ~~01,00€: small coffee~~
   * ~~02,00€: small beer~~
   * ~~03,00€: regular beer~~
   * ~~10,00€: Oktoberfest~~
