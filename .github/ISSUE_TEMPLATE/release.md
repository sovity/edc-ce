---
name: Release
about: Create an issue to track a release process.
title: "Release v0.0.1-milestone-8-sovity{{version}}"
labels: ["task/release", "scope/ce"]
assignees: ""
---

# Release

## Work Breakdown

Feel free to edit this release checklist in-progress depending on what tasks need to be done:
- [ ] Decide a release version depending on the previous release.
- [ ] Update this issue's title to the new version
- [ ] `release-prep` PR:
  - [ ] Update the CHANGELOG.md.
    - [ ] Add a clean `Unreleased` version.
    - [ ] Add the version to the old section.
    - [ ] Add the current date to the old version.
    - [ ] Write or review a `Deployment Migration Notes` section.
    - [ ] Write or review a release summary.
    - [ ] Remove empty sections from the patch notes.
  - [ ] Merge the `release-prep` PR.
- [ ] Wait for the main branch to be green.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Revisit the changed list of tasks and compare it with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-ui/blob/main/.github/ISSUE_TEMPLATE/release.md). Propose changes where it
  makes sense.
- [ ] Close this issue.
