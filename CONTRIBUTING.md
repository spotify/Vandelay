# How to Contribute

We'd love to get contributions from you!

## Building the Project

This project uses [Maven][maven] to build and release.

To build all modules, run the following:
```bash
mvn clean compile
```

## Workflow

We follow the [GitHub Flow Workflow][github-flow]

1.  Fork the project
1.  Check out the `main` branch
1.  Create a feature branch
1.  Write code and tests for your change
1.  From your branch, make a pull request against `https://github.com/spotify/Vandelay/main`
1.  Work with repo maintainers to get your change reviewed
1.  Wait for your change to be pulled into `https://github.com/spotify/Vandelay/main`
1.  Delete your feature branch

## Testing

This project uses JUnit 5 as the unit testing framework for code coverage.

To run the tests locally, run the following:
```bash
mvn clean verify
```

This project also runs integration tests against an actual GCP test environment as part of the 
release process. These tests are disabled as part of local development, but can be enabled 
and configured by locating tests with the `@Disabled` annotation and setting their 
`projectId` and `instanceId` fields.

## Style

Code for this project has been written using the [Google Style Guide][google-style-guide]. 

To format your code, run the following:
```bash
mvn fmt:format
```

## Issues

When creating an issue please try to adhere to the following format:

    module-name: One line summary of the issue (less than 72 characters)

    ### Expected behavior

    As concisely as possible, describe the expected behavior.

    ### Actual behavior

    As concisely as possible, describe the observed behavior.

    ### Steps to reproduce the behavior

    List all relevant steps to reproduce the observed behavior.

## Pull Requests

Comments should be formatted to a width no greater than 80 columns.

Files should be exempt of trailing spaces.

We adhere to a specific format for commit messages. Please write your commit
messages along these guidelines. Please keep the line width no greater than 80
columns (You can use `fmt -n -p -w 80` to accomplish this).

    module-name: One line description of your change (less than 72 characters)

    Problem

    Explain the context and why you're making that change.  What is the problem
    you're trying to solve? In some cases there is not a problem and this can be
    thought of being the motivation for your change.

    Solution

    Describe the modifications you've done.

    Result

    What will change as a result of your pull request? Note that sometimes this
    section is unnecessary because it is self-explanatory based on the solution.

Some important notes regarding the summary line:

* Describe what was done; not the result 
* Use the active voice 
* Use the present tense 
* Capitalize properly 
* Do not end in a period — this is a title/subject 
* Prefix the subject with its scope

## Documentation

We also welcome improvements to the project documentation or to the existing
docs. Please file an [issue][issue].

# License 

By contributing your code, you agree to license your contribution under the 
terms of the [LICENSE][license]

# Code of Conduct

Read our [Code of Conduct][code-of-conduct] for the project.

[code-of-conduct]: <https://github.com/spotify/Vandelay/blob/main/CODE_OF_CONDUCT.md>
[github-flow]: <https://guides.github.com/introduction/flow/>
[google-style-guide]: <https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml>
[issue]: <https://github.com/spotify/Vandelay/issues/New>
[license]: <https://github.com/spotify/Vandelay/blob/main/LICENSE>
[maven]: <https://maven.apache.org/>