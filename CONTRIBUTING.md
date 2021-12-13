# Contributing to Manhunt Plus

## Setup

- Clone this repo
- In a separate folder, follow these instructions: https://www.spigotmc.org/wiki/buildtools/
- Copy the spigot-<version>.jar file into a new folder which will run your local server
- Create a start.command file with this data, and swap out version with correct version:
  ```sh
  #!/bin/bash
  cd "$(dirname "$0")"
  exec java -Xms2G -Xmx2G -jar spigot-<version>.jar nogui
  ```
- Run start.command
- Change eula.txt to have eula=true
- Open project in IntelliJ, and mvn clean install
- Navigate to target folder and copy the .jar file into the server's plugins folder
  
## Issue tracking

### Templates

Githun issue templates have been set up to streamline the process of creating a new issue. 

An issue can either be a [**Feature Request**](.gitlab/issue_templates/Feature.md), a [**Bug**](.gitlab/issue_templates/Bug.md) or **Uncategorized**. Please inspect the raw Markdown code when viewing these templates.

The issue templates are adapted to resemble an informal user story.

### Labels

Each issue is assigned one or multiple labels. An overview of our custom project labels can be found
in `Issues -> Labels` on Github. The labels serve to prioritze, scope and categorize the issues.

### Connecting issues to merge requests

An issue should always be closed by a merge request, unless the issue is obviously not connected to part of the code base.

### Github boards

An overview of the state of an issue (backlog, in progress, done, etc...) can be found in `Issues -> Boards`.

### Scope of an issue

An issue should not be too large. We seek to scope an issue to a maximum work length of 12 hours. If the issue must be larger than that, the issue must have a checklist of subtasks.

#### Example: Scope of an issue

Issue name: **#10 - Setup frontend**

- [x] Add dependencies
- [ ] Make controllers
- [ ] etc...

## Commit Culture

In short, a commit should be short, concise and descriptive.

### Commit title

For some commits, only a commit title is sufficient. Oftentimes, adding a description explaining what is done, and why it is done is helpful for new developers joining the project.

Commit messages must be categorized. One should not blend or mix categories in one single commit, in order to maintain a clean Git timeline and to increase readability for the developer.

#### Example

The following is an example of a good commit.

```txt
# Title

docs: Add CONTRIBUTING.md

# Description

WHAT: Add documentation for contribution to the code base

WHY: New developers should quickly be able to contribute new code to our code base
```
  
## Branches and merge requests

### Naming

Branch names should be clear and concise, and be marked with the id number of the issue it resolves.
Example: `feat/#10-frontend`.
  
## Typical Crashes
- "... Is it up to date?"
  - If you've added a new command, remember to add it in existingCompletions, getCompletions and plugin.yml
