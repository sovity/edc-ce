# Contributing to the Project

Thank you for your interest in contributing to this project

## Table of Contents

* [Code Of Conduct](#code-of-conduct)
* [How to Contribute](#how-to-contribute)
    * [Discuss](#discuss)
    * [Create an Issue](#create-an-issue)
    * [Submit a Pull Request](#submit-a-pull-request)
    * [Report on Flaky Tests](#report-on-flaky-tests)
* [Etiquette for pull requests](#etiquette-for-pull-requests)
* [Contact Us](#contact-us)

## Code Of Conduct

See the [Code Of Conduct](CODE_OF_CONDUCT.md).

## How to Contribute

### Discuss

If you want to share an idea to further enhance the project or discuss potential use cases, please feel free to create a
discussion at the `GitHub Discussions page`]
If you feel there is a bug or an issue, contribute to the discussions in `existing issues`
otherwise [create a new issue](#create-an-issue).

### Create an Issue

If you have identified a bug or want to formulate a working item that you want to concentrate on, feel free to create a
new issue at our project's corresponding `GitHub Issues page`. Before doing so, please consider searching for
potentially suitable `existing issues`.

We also
use [GitHub's default label set](https://docs.github.com/en/issues/using-labels-and-milestones-to-track-work/managing-labels)
extended by custom ones to classify issues and improve findability.

If an issue appears to cover changes that will have a (huge) impact on the code base and needs to
first be discussed, or if you just have a question regarding the usage of the software, please
create a `discussion` before raising an issue.

Please note that if an issue covers a topic or the response to a question that may be interesting
for other developers or contributors, or for further discussions, it should be converted to a
discussion and not be closed.

### Adhere to Coding Style Guide

We aim for a coherent and consistent code base, thus the coding style detailed in the [styleguide](STYLEGUIDE.md) should
be followed.

### Submit a Pull Request

We would appreciate if your pull request applies to the following points:

* Conform to following [Etiquette for pull requests](#etiquette-for-pull-requests):

* Make sure to adjust copyright headers appropriately.

* The git commit messages should comply to the following format:
    ```
    <prefix>(<scope>): <description>
    ```

  Use the [imperative mood](https://github.com/git/git/blob/master/Documentation/SubmittingPatches)
  as in "Fix bug" or "Add feature" rather than "Fixed bug" or "Added feature" and
  [mention the GitHub issue](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue)
  e.g. `chore(transfer process): improve logging`.

* Add meaningful tests to verify your submission acts as expected.

* Where code is not self-explanatory, add documentation providing extra clarification.

* PR descriptions should use the current [PR template](.github/PULL_REQUEST_TEMPLATE.md)

* Submit a draft pull request at early-stage and add people previously working on the same code as
  reviewer. Make sure automatic checks pass before marking it as "ready for review":

    * _Continuous Integration_ performing various test conventions.

### Report on Flaky Tests

If you discover a randomly failing ("flaky") test, please take the time to check whether an issue for that already
exists and if not, create an issue yourself, providing meaningful description and a link to the failing run. Please also
label it with `Bug` and `github`. Then assign it to whoever was the original author of the relevant piece of code or
whoever worked on it last. If assigning the issue is not possible due to missing rights, please just comment and
@mention the author/last editor.

Please do not just restart the run, as this would overwrite the results. If you need to, a better way of doing this is
to push an empty commit. This will trigger another run.

```bash
git commit --allow-empty -m "trigger CI" && git push
```

If an issue labeled with `Bug` and `github` is assigned to you, please prioritize addressing this issue as other
people will be affected.
We are taking the quality of our code very serious and reporting on flaky tests is an important step toward improvement
in that area.

## Etiquette for pull requests

### As an author

Submitting pull requests should be done while adhering to a couple of simple rules.

- Familiarize yourself with [coding style](STYLEGUIDE.md), architectural patterns and other contribution guidelines.
- No surprise PRs please. Before you submit a PR, open a discussion or an issue outlining your planned work and give
  people time to comment. It may even be advisable to contact committers using the `@mention` feature. Unsolicited PRs
  may get ignored or rejected.
- Create focused PRs: your work should be focused on one particular feature or bug. Do not create broad-scoped PRs that
  solve multiple issues as reviewers may reject those PR bombs outright.
- Provide a clear description and motivation in the PR description in GitHub. This makes the reviewer's life much
  easier. It is also helpful to outline the broad changes that were made, e.g. "Changes the schema of XYZ-Entity:
  the `age` field changed from `long` to `String`".
- If you introduce new 3rd party dependencies, be sure to note them in the PR description and explain why they are
  necessary.
- Stick to the established code style, please refer to the [styleguide document](STYLEGUIDE.md).
- All tests should be green, especially when your PR is in `"Ready for review"`
- Mark PRs as `"Ready for review"` only when you're prepared to defend your work. By that time you have completed your
  work and shouldn't need to push any more commits other than to incorporate review comments.
- Merge conflicts should be resolved by squashing all commits on the PR branch, rebasing onto `main` and
  force-pushing. Do this when your PR is ready to review.
- If you require a reviewer's input while it's still in draft, please contact the designated reviewer using
  the `@mention` feature and let them know what you'd like them to look at.
- Re-request reviews after all remarks have been adopted. This helps reviewers track their work in GitHub.
- If you disagree with a committer's remarks, feel free to object and argue, but if no agreement is reached, you'll have
  to either accept the decision or withdraw your PR.
- Be civil and objective. No foul language, insulting or otherwise abusive language will be tolerated.
- The PR titles must follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
    - The title must follow the format as `<type>(<optional scope>): <description>`.
      `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `revert`, `style`, `test` are allowed for
      the `<type>`.
    - The length must be kept under 80 characters.

### As a reviewer

- Have a look at [Pull Request Review Pyramide](https://www.morling.dev/blog/the-code-review-pyramid/)
- Please complete reviews within two business days or delegate to another committer, removing yourself as a reviewer.
- If you have been requested as reviewer, but cannot do the review for any reason (time, lack of knowledge in particular
  area, etc.) please comment that in the PR and remove yourself as a reviewer, suggesting a stand-in.
- Don't be overly pedantic.
- Don't argue basic principles (code style, architectural decisions, etc.)
- Use the `suggestion` feature of GitHub for small/simple changes.
- The following could serve you as a review checklist:
    - no unnecessary dependencies in `build.gradle.kts`
    - sensible unit tests, prefer unit tests over integration tests wherever possible (test runtime). Also check the
      usage of test tags.
    - code style
    - simplicity and "uncluttered-ness" of the code
    - overall focus of the PR
- Don't just wave through any PR. Please take the time to look at them carefully.
- Be civil and objective. No foul language, insulting or otherwise abusive language will be tolerated. The goal is to
  _encourage_ contributions.

## Contact Us

If you have questions or suggestions, do not hesitate to contact the project developers via https://github.com/sovity.

## Attribution

This file is adapted from the [eclipse-edc](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector) project.
