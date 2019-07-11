---
extraCss:
    - 'inline:homepage.scss:table td code {white-space: nowrap;}'
---

<div class="row">
    <div class="8u 12u(small)">
        {{ leftCol() }}
    </div>
    <div class="4u 12u(small) -1">
        {{ rightCol() }}
    </div>
</div>

{% macro leftCol() %}
{% filter compileAs('md') %}

![Geoffrey]({{'assets/geoffrey.jpg'|asset}} "Geoffrey")
> Meet Geoffrey, your new best friend.

Crederabot is a chat bot implemented using Kotlin multiplatform and JAMstack technologies, for fun and giggles at 
Credera. It is designed to be run as a Slack and Microsoft Teams bot on serverless functions, while offering an 
in-browser chat mode for playing around with. 

In addition, this site uses Orchid to statically generate all site pages and documentation, which is dynamically 
rendered from the same Kotlin code that runs the chat bot.  

### Technologies Used

- Proudly hosted on [Netlify](https://www.netlify.com/)
- Slackbot is fully implemented using [Netlify Functions](https://www.netlify.com/features/functions/)
- Serverless functions are written in [Kotlin/JS](https://kotlinlang.org/docs/reference/js-overview.html)
- Interactive chat app built with [Kotlin/JS](https://kotlinlang.org/docs/reference/js-overview.html)
- Database built with [Firebase Realtime Database](https://firebase.google.com/docs/database/)
- Homepage and documentation built with [Orchid](https://orchid.netlify.com/)

{% endfilter  %}
{% endmacro %}

{% macro rightCol() %}
{% filter compileAs('md') %}

# Example Conversation

![Example conversation]({{ 'assets/example-conversation.png'|asset }} "Example conversation")

{% endfilter  %}
{% endmacro %}
