# Code Style Guide

In order to maintain a coherent code style throughout the project we ask every contributor to adhere to a few simple
style guidelines. We assume most developers will use at least something like `IntelliJ` and therefore have support for
automatic code formatting, we are not going to list the guidelines here. If you absolutely want to take a look, checkout
the [config written in XML](.) TODO: fix url after merge

## Checkstyle configuration

Checkstyle is a [tool](https://checkstyle.sourceforge.io/) that can statically analyze your source code to check against
a set of given rules. Those rules are formulated in an [XML document](.) <!-- TODO: fix url after merge -->. Many modern
IDEs have a plugin available for download that runs in the background and does code analysis.

This checkstyle config is based off of the [Google Style](https://checkstyle.sourceforge.io/google_style.html) with a
few
additional rules such as the naming of constants and Types.

_Note: currently we do **not** enforce the generation of Javadoc comments, even though documenting code is **highly**
recommended. We might enable this in the future, such that at least interfaces and public methods are commented._

## Running Checkstyle

In order to get better usability and on-the-fly reporting, Checkstyle is available as IDE plugins for many modern IDEs,
and it can run either on-demand or continuously in the background:

- [IntelliJ IDEA plugin [recommended]](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
- [Eclipse IDE [recommended]](https://checkstyle.org/eclipse-cs/#!/)

### Checkstyle as PR validation

Apart from running Checkstyle locally as IDE plugin, we do run it on
our [Github Actions pipeline](.github/workflows/code_analysis.yml). At this time, Checkstyle will only spew out warnings, but
we may tighten the rules at a future time and without notice. This will result in failing Github Action pipelines. Also,
committers might reject PRs due to Checkstyle warnings.

It is therefore **highly** recommended running Checkstyle locally as well.

If you **do not wish** to run Checkstyle on you local machine, that's fine, but be prepared to get your PRs rejected
simply because of a stupid naming or formatting error.

> _Note: we do not use the Checkstyle Gradle Plugin on Github Actions because violations would cause builds to fail. For
now, we only want to log warnings._

## [Recommended] IntelliJ Code Style Configuration

If you are using Jetbrains IntelliJ IDEA, we have created a specific code style configuration that will automatically
format your source code according to that style guide. This should eliminate most of the potential Checkstyle violations
right from the get-go. You will need to reformat your code manually or in a pre-commit hook though.

## [Optional] Generic `.editorconfig`

For most other editors and IDEs we've supplied an [.editorconfig](.editorconfig) file that can be
placed at the appropriate location. The specific location will largely depend on your editor and your OS, please refer
to the [official documentation](https://editorconfig.org) for details.

## Attribution

This file is adapted from the [eclipse-edc](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector) project.
