# Changelog

# 27.0
- Add new action: **Refresh Icons**. This will manually refresh all icons and reload the panes.
  - This should fix bugs such as <https://github.com/mallowigi/a-file-icon-idea/issues/93> and <https://github.com/mallowigi/a-file-icon-idea/issues/79>
- Reorganized icons: now icons belonging to the *Files* category and *PSI* category are separate from the rest of icons. As a result, toggling *UI Icons* should not affect *File Icons* and *PSI Icons* and vice-versa.
- Add icons to the **Atom Material Settings Toolbar Menu**.
- Add actions for switching between **Arrow Styles**

# 26.0
- New Option: "**Folder Color**" to customize Folder Colors from the current theme.
- Better detection of the current theme's accent color
- Add Checkboxes to **Accent Color** and **Folder Color** to allow customizing such colors instead of picking them from the theme
- Improve Hollow Folders opened files detection
- Put "Custom file associations" under the "Atom File Icon Settings"
- New Icons: brew, harbour, holyc, hjson, hoplon, houndci, hp, hy, hyper, question 

# 25.3
- Fixes many issues with custom associations

# 25.2
- Support for 2020.2 EAP

# 25.1
- Fix Psi Icons for PHP files

# 25.0
- The finally awaited **Custom Icon Associations** is here! This is an alpha version so it can contain bugs :)
    - Assign custom file associations and folder associations from the Settings
    - Preview Default File and Folder Associations from the Settings
    - Assign icons from your file system
    - **Important note**: most of the settings won't be applied to custom icons, so use them sparingly!
- The project has been revamped to Kotlin! This is a huge step so please report any issues you can find :)
- Fix issue with monospace colors not being persisted
- Changed color scheme of Kotlin PSI Icons, to differentiate with Java PSI Icons
- Now the _PSI Icons setting_ is dependent on the _UI Icons setting_, as this wouldn't work without it.
- And the _Hollow Folders Setting_ is dependent on the _Folder Decorations Setting_ as well.
- **New folder associations**: widgets, elements, users, members, partners, friends, projects, ui, dev, staging, qa, prod, serializers, types, io, stdlib, deps
- New file associations: `*_test.go`, `*_spec.go`, `Chart.yaml`, `values.yaml`, `helmfile.yaml`, scala test files
- Added `jsconfig` and `tsconfig`

# 24.0
- Added accent color selection in the settings
- new UI/PSI icons: Scala, Ruby, Typescript
- new redux icons: redux saga and redux epics, tweaked other colors too
- new test icons: test dart, test go, test haskell, test perl, test rust
- switched typescript and tslint icons
- new icons:  icomoon, idl, igor, imba, index, inform, ink, inkscape, inno, ioke, isabelle, jake, jasmine, jison, jolie, json5, jsonld, jsonnet, junos
- new/updated folders:  acre, agda, alacritty, app, appstore, arttext, atom, aurelia, bazaar, cabal, chef, client, cluster, container, content, controllers, coverage, cvs, messages
- new folder icons: viewmodel, deno, dependabot, devcontainer, dropbox, gitlab, istanbul, yarn, ruby, mercurial, meteor, svn, textmate, vagrant, vim, svg, firebase, xcassets, glyph
- Try to fix the weird border at start
- Fix wrong associations

More info about associations [here](http://www.material-theme.com/docs/reference/associations/) and [here](http://www.material-theme.com/docs/reference/folder-associations/)


# 23.0
- More icons: walt, watchman, wdl, webgl, owl, webvtt, wenyan, wget, wix, wordpress, workbox, wurst, x10, x11, xmos, xojo, xpages, xproc, xtend, yara, yorick, yui, yasm, zbrush, zeit, zenscript, zig, zilog, zimpl, zork, awk, bazaar, brewfile, hitachi, intel, vax, kerboscript, keybase, keynote, kicad, kitchenci, kml, knime, kotlin, knockout, krl, kubernetes, q, qasm, qlikview, qt, quasar
- New angular icon: interceptor
- New redux icons: saga and selector
- New pattern: main.js is now associated with nodejs
- Add cron/scheduler folder icons


# 22.0
- You can now preview icons at <https://github.com/mallowigi/a-file-icon-idea> !
- You can now get the list of available associations at <http://www.material-theme.com/docs/reference/associations/> and <http://www.material-theme.com/docs/reference/folder_associations/> !
- Extracted icons to its own repository so it can be used with multiple projects
- Optimized icons (reduce icons size, set colors in paths, etc)
    - Also the icons look much more like the Atom original plugin ones
- Added fileNames in associations, to be used with the examples generator
- Added colors in associations, for use with the font generator
- Update UI Icons with latest icons
- **New/Updated UI Icons**: Ant, Haml, Handlebars, DataGrip, JavaScript, Kotlin, DevKit, Dart, Docker
- **New icons**: 4d, agda, alpine linux, analytica, adobe animate, ansible, antwar, aplus, buck, build boot, caddy, caffe, caffe2, cakefile, carthage, chai, chapel, chartjs, cheetah, chocolatey, cpp, csharp, cdf, chrome, chuck, circuit, cirru, clarion, clean, click, clips, closuretpl, cloudfoundry, cocoapods, codacy, codeclimate, cokekit, codeship, conan, conda, conll, coq, corel, coveralls, cp, cpan, credits, creole, crowdin, csound, cuneiform, curl, curry, cwl, collada, nvidia, fabric, factor, falcon, fancy, fantom, fexl, fbx, faust, fasta, finaldraft, firebase bolt, firefox, flask, flutter, flux, font bitmap, fontforge, fossa, fountain, fanca, frege, fuel ux, fusebox, futhark, Jetbrains Theme Files
- *Update icon associations*: actionscript, ansible, antlr, applescript, asciidoc, asm, atom, atoum, babel, backbone, behat, bison, bootstrap, bower,  cabal, c, composer.lock, chef, credits, csharp, cypress, todo, finder, firebase, flash, fortran, gem, archives, audio, video

# 21.0
- Fix issue with Arrow Colors being black in Darcula
- Fix issue with Plugins labels being black in Darcula
- Add new 2020.1 UI Icons
- Add folder associations: bin and units

# 20.0
- Restore the "Accent Color" function to colorize icons with the Accent color (defined from Material Theme, or from the current active tab indicator color)
- Fix issue where arrows style were not persisted
- Add sql/mysql/pgsql folder
- Fix Kotlin icons being unaffected by PSI File Icons setting
- Add bz2, xar, egg and sit to "Archive" file association
- Make Icon Settings "dumb aware" (not affected by the indexing process)

# 19.0
- New feature: Arrows Style
- Fix issue with icons not being applied on start
- Add missing icons since Nov 2019
- New pattern: angular controller

# 18.0
- Support for 2020.1

# 17.0
- New Icons:
  - 1c, 3dmodel, 3dsmax, abap, abif, acre, advpl, affectscript, affinity, alacritty, alex, alloy, ampl, amusewiki, angelscript, animestudio, ant
  - apache, apl, appcelerator, arc, archlinux, asymptote, atoum, ats, audacity, augeas, avro, backbone, bnf
  - bem, bibtext, biml, bintray, blender, bluespec, boo, bootstrap, brakeman, broccoli, brotli, browsersync, brunch, byond, gnu
- New Feature: Hide File Icons
- New Feature: Hollow Directories

# 16.0
- New icons: Cargo, Codecov, codeowners, cypress, cython, gridsome, netlify, pnpm, posthtml, sapper, svelte-config, uml, v, vala
- New folders: events, gulp, json, netlify, relay
- Sync with new UI Icons

# 15.0
- Support for 2019.3 EAP

# 14.0
- New File Icons:
  - edge, autoit, azure, bithound, blink, bucklescript, buildkite, certificate, commitlint, credits, history
  - graphcool, helm, istanbul, key, kivy, lib, livescript, markojs, mdx, merlin, mint, moonscript, mxml, nest
  - houdini, now, nunjucks, prisma, processing, restql, san, sequelize, swc, unity, velocity, vm, webassembly, webhint, wepy, yang
- New Folder Icons:
  - ci, class, container, content, css, delta, dump, error, examples, flow, helper, modals, maps
  - pipe, prisma, private, stack, utils, vm
- Sync with Material Icons

# 13.0
- New Icons and associations
- Sync with Material Icons

# 12.0
- Support for 2019.2
- Sync with Material Icons

# 11.0 #
* Add light equivalents of icons
* Update new icons: Resharper (Rider)
* Sync with Material Icons
* Fix errors

# 10.0
- New setting: **PSI Icons**. Replaces icons about program structure: classes, interfaces, methods, enums etc.
- Import last set of UI Icons from the Material Theme
- Update for 2019.1

# 9.0
- Add Monochrome Icons' tint color selection
- Fix UI Icons setting needing the IDE to be restarted
- Fix settings not being persisted
- Add more contrast to monochrome icons
- Fix some directories icons' contrast between the directory and the icon
- Add more directories: guard, providers, animations, grunt, icons, e2e, custom, rules, screens, storybook, stylus, syntax, security, meta-inf, fixtures, channels, concerns, support, features, fabricators, nyc, reviews
- Add more files: ANTLR, brainfuck, cobol, delphi, eiffel, fortran, idris, io, j, lerna, postscript, prolog, racket, red, supercollider, scheme, terraform, test-react, turing, toml

# 8.0
- Atom File Icons has now the complete UI Icons feature, allowing you to replace IntelliJ icons with Material Icons
- New Option: Monochrome Icons

# 7.0
- Fix fatal error

# 6.0
- Added new **file icons**: Jenkins, Rspec, Rubocop
- Added new **folder icons**: android, deploy, api, archives, config, constants, core, download, env, exclude, functions, generated, hooks, ios, include, jinja, job, keys, layouts, mailers, meta, middleware, notification, other, packages, posts, react, resourceIOS, routes, scripts, server, serverless, shared, sublime, tasks, themes, utils, upload, vendors, views, vue

# 5.0
- Added new **file icons**: Arduino, Assembly, Authors, Ballerina, Crystal, CSSMap, Dotjs, DTS, Favicon, Firebase, Flash, Gatsby, Gemfile, Po, JSMap, MJML, Nimble, Raml, Razor, Redux Actions/Stores/Reducers, Smarty, Solidity, Sonar, Stencil, Storybook, Wallaby
- Added new **folder icons**: benchmarks, coverage, controllers, debug, excluded, expo, jinja, less, maven, messages, models, plugin, python, react, redux, routes, scripts, sublime, sync, tasks

# 4.0
- Convert icons to SVG
- Add more system UI icons

# 3.0
- Add settings page to control which components to enable or disable.
- Components: File Icons, Directory Icons and UI Icons (experimental)

# 2.0
- Add folder icons feature
- Add more icons

# 1.2
- feat(project): add PHPStorm and Pycharm files and plugin support
- fix(project): rename "files" to "files" to that it replaces all icons
- feat(project): Add "nodes" icons for various file trees
- fix(FileIconProvider): make it DumbAware
- chore(project): Add markdown to html for changelog
- chore(project): add gradle.properties for easy installing
- feat(icons): add new icons
  - Github Code of conduct, Github Contributing, Commit message convention, Github Template
  - README
  - Prefs
  - Atom files
  - Angular component, service, directive, guard, pipe, routing, resolver
  - Autohotkey
  - API Blueprint
  - Appveyor
  - Java Beans
  - Browserslist
  - Cabal
  - CNAME
  - Compass files
  - C++
  - Dockerignore
  - Doxygen
  - Dylib
  - Eslintignore
  - MAC OSX files
  - Flow Config
  - Ghostscript
  - Godot
  - Gradlew
  - H files
  - Adobe Indesign
  - Jekyll config.yml
  - jQuery
  - Makefile
  - Markdown
  - Manpage
  - Mathematica
  - Maven pom.xml
  - Maya
  - Mocha
  - Nib
  - NPM package.lock
  - NVM
  - OpenOffice
  - Objective C
  - Patch files
  - Phalcon
  - PostCSS Config
  - Prettier
  - Powershell
  - ReasonML
  - Restructured
  - Robot
  - RVM
  - Spring
  - Tern
  - Tomcat
  - Travis
  - VHDL
  - Webpack
  - XCode
  - Yarn files
  - Minified javascripts
