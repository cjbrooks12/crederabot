---
---


<div class="row">
    <div class="6u 12u(small)">
        {{ leftCol() }}
    </div>
    <div class="6u 12u(small)">
        {{ rightCol() }}
    </div>
</div>

{% macro leftCol() %}
{% filter compileAs('md') %}

# About and Usage

Credera++ is a simple slackbot to rate your friends! Simply `++` or `--` them in Slack and let Credd be the Judge. 
Credera++ also keeps track of the reasons why someone was given points or had them taken away, for you to forever 
remember their glory or shame.

<a href="https://slack.com/oauth/authorize?client_id=402674575044.402789997363&scope=channels:history,users.profile:read,chat:write:bot,bot,users:read"><img alt="Add to Slack" height="40" width="139" src="https://platform.slack-edge.com/img/add_to_slack.png" srcset="https://platform.slack-edge.com/img/add_to_slack.png 1x, https://platform.slack-edge.com/img/add_to_slack@2x.png 2x" /></a>

### Command Reference

| Command                                           | Description                                                                                                      |
| ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| `[@user/thing]++                                ` | Add a point to a tagged user or an arbitrary thing                                                               |
| `[@user/thing]+= [amount]                       ` | Add many points to a tagged user or an arbitrary thing                                                           |
| `[@user/thing]--                                ` | Remove a point from a tagged user or an arbitrary thing                                                          |
| `[@user/thing]-= [amount]                       ` | Remove many points to a tagged user or an arbitrary thing                                                        |
| `@judge-credd top [count]                       ` | Show a leaderboard of the top _n_ players, with the most points                                                  |
| `@judge-credd bottom [count]                    ` | Show a leaderboard of the bottom _n_ players, with the fewest or most negative points                            |
| `@judge-credd score of [@user/thing]            ` | Show the current score for a tagged user or an arbitrary thing                                                   |
| `@judge-credd [count?] reasons why [@user/thing]` | Show the current score for a tagged user or an arbitrary thing, along with a list of the _n_ most recent reasons |
| `@judge-credd help/halp                         ` | Show a list of available commands                                                                                |

### Coming Soon (maybe...)

- Steal points from someone! Take their point and give it to yourself, or to someone else.
- Things getting too heated? Revoke someone's `++` privileges for 30 seconds and watch them squirm! 

### Technologies Used

- Proudly hosted on [Netlify](https://www.netlify.com/)
- Slackbot is fully implemented using [Netlify Functions](https://www.netlify.com/features/functions/)
- Serverless functions are written in [Kotlin/JS](https://kotlinlang.org/docs/reference/js-overview.html)
- Database built with [Firebase Realtime Database](https://firebase.google.com/docs/database/)
- Documentation site built with [Orchid](https://orchid.netlify.com/) 

{% endfilter  %}
{% endmacro %}

{% macro rightCol() %}
{% filter compileAs('md') %}

# Example Conversation

![Example conversation]({{ 'assets/example-conversation.png'|asset }} "Example conversation")

{% endfilter  %}
{% endmacro %}