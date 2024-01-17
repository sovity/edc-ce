---
name: Release
about: Create an issue to track a release process.
title: "Release x.y.z"
labels: ["task/release", "scope/ce"]
assignees: ""
---

# Release

## Work Breakdown

Feel free to edit this release checklist in-progress depending on what tasks need to be done:
- [ ] Decide a release version depending on major/minor/patch changes in the CHANGELOG.md.
- [ ] Update this issue's title to the new version
- [ ] `release-prep` PR:
  - [ ] Update the CHANGELOG.md.
    - [ ] Add a clean `Unreleased` version.
    - [ ] Add the version to the old section.
    - [ ] Add the current date to the old version.
    - [ ] Reorder, reword or combine changelog entries from a product perspective for consistency.
    - [ ] Check the [Dependabot Alerts](https://github.com/sovity/edc-ui/security/dependabot) for anything fixable pre-release.
    - [ ] Write or review a `Deployment Migration Notes` section.
    - [ ] Write or review a release summary.
    - [ ] Remove empty sections from the patch notes.
  - [ ] Review the Screenshots in the Readme and update them if necessary.
  - [ ] Merge the `release-prep` PR.
- [ ] Wait for the main branch to be green.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Revisit the changed list of tasks and compare it with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-ui/blob/main/.github/ISSUE_TEMPLATE/release.md). Propose changes where it
  makes sense.
- [ ] Close this issue.
