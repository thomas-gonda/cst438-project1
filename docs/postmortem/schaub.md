# Linus Schaub - Project 01 Retrospective

## My work

- Merged PRs:
  - [PR #17 – Fetch LCBO products from GraphQL API](https://github.com/thomas-gonda/cst438-project1/pull/17)
  - [PR #20 – Create Room database for alcohol tracking](https://github.com/thomas-gonda/cst438-project1/pull/20)
  - [PR #25 – Add Room database instrumentation tests](https://github.com/thomas-gonda/cst438-project1/pull/25)
  - [PR #27 – Add user-specific alcohol consumption timeline](https://github.com/thomas-gonda/cst438-project1/pull/27)
  - [PR #31 – Fix alcohol consumption timeline](https://github.com/thomas-gonda/cst438-project1/pull/31)

- My issues:
  - [Issue #2 – Create database](https://github.com/thomas-gonda/cst438-project1/issues/2)
  - [Issue #4 – Get API to fetch data correctly using Kotlin](https://github.com/thomas-gonda/cst438-project1/issues/4)
  - [Issue #7 – Alcohol Consumption Timeline](https://github.com/thomas-gonda/cst438-project1/issues/7)
  - [Issue #8 – Design/Create Tables for Database](https://github.com/thomas-gonda/cst438-project1/issues/8)
  - [Issue #9 – Timeline fix](https://github.com/thomas-gonda/cst438-project1/issues/9)

- What I built: I designed and implemented the Room database for the application, including the database entities and relationships needed to store alcohol products, users, ratings, and consumption records. I also created the alcohol consumption timeline, including user-specific entries and repeated consumption records. In addition, I wrote database tests and contributed to the API functionality. Overall, I completed five merged pull requests and closed the five issues assigned to me.

## Biggest challenge

My biggest challenge was creating the database design and making the timeline work correctly with it. The database needed to support several different features, including users, alcohol products, ratings, and repeated consumption records. The timeline also needed to show the correct records for the current user instead of mixing data from different users.

I handled this by discussing the database structure and feature requirements with the team before making changes. I also wrote tests for the database and timeline behavior. These tests helped verify that records were saved correctly, repeated consumption entries were preserved, and the timeline displayed the appropriate user’s information.

## Most valuable thing I learned

The most valuable thing I learned was how important planning is for a software project. A database design affects many other parts of an application, so it is important to think through the structure before implementing features. I also learned more about how a project moves through GitHub using issues, branches, pull requests, reviews, and merges. Clear planning and organization make it easier for a team to work without duplicating effort.

One weakness in my process was that I did not submit a review on any teammate’s pull request during Project 01. The instructor feedback made clear that reviews are an important part of maintaining quality, not just an optional extra. I need to contribute to the review process as well as to the code itself.

## What I carry into Project 02

1. I will check the assigned issues and current GitHub work before starting an implementation. If another teammate is already working on a related issue, I will clarify the responsibilities in the issue comments or during the team meeting. I will know it worked if none of my tasks overlap with another teammate’s work without being discussed first.

2. I will participate actively in code reviews. I will submit a written GitHub review for every teammate pull request that is assigned to me or that I am responsible for checking before merge. I will know it worked if my GitHub activity shows a submitted review on each of those pull requests before it is merged.
