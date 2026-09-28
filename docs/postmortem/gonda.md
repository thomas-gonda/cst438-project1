# Thomas Gonda - Project 01 Retrospective

## My work
- Merged PRs: 
- https://github.com/thomas-gonda/cst438-project1/pull/30
- https://github.com/thomas-gonda/cst438-project1/pull/29
- https://github.com/thomas-gonda/cst438-project1/pull/28
- https://github.com/thomas-gonda/cst438-project1/pull/23
- https://github.com/thomas-gonda/cst438-project1/pull/22
- https://github.com/thomas-gonda/cst438-project1/pull/18
- https://github.com/thomas-gonda/cst438-project1/pull/15

- My issues:
- https://github.com/thomas-gonda/cst438-project1/issues/24
- https://github.com/thomas-gonda/cst438-project1/issues/19
- https://github.com/thomas-gonda/cst438-project1/issues/14
- https://github.com/thomas-gonda/cst438-project1/issues/13
- https://github.com/thomas-gonda/cst438-project1/issues/12
- https://github.com/thomas-gonda/cst438-project1/issues/10
- https://github.com/thomas-gonda/cst438-project1/issues/4

- What I built: I created the alcohol search screen + individual alcohol screen.  This included api functionality + autocomplete on the search screen, and a rating system with persistence on the individual view.  To make this work I also created a seed file that the autocomplete could sample data from without making an api request upon every typed character in the search field.  I also shaped up the whole repo with detekt static analysis, and also did the initial physical drawings and architecture of the backend database.

## Biggest challenge
[What it was, why it was hard, how you handled it]
There was some miscommunication early on when assigning issues but luckily most of the work that people did still turned out to be useful in some way.  I did the same thing as Linus but we were able to combine that work, and I don't think anyone really got angry over it thankfully.  I also appreciate that Adrik had the patience to redo the video when it wasn't quite turning out right.  I'm glad that everyone did their work without complaints, so for me the whole thing seemed to go relatively smoothly.

## Most valuable thing I learned
I think if you want to do all this testing + static analysis stuff it's much better to add it right away and have all future work conform to the standards, rather than going back while it's already underway and fix everything.  

## What I carry into Project 02
1. Write github reviews + comments - I will know it worked if my teammates tell me they actually implement some changes I write about.
2. Don't let any PRs in that fail static analysis - I will know it worked if I can pull from main and not have anything fail.  I had to clean up the repo twice after some group member pushed and the tests started failing, I need to communicate that issue better.