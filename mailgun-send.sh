

# mailgun api creds of florian are in private gist:
# https://gist.github.com/awb99/

echo api:$MAILGUN_API_KEY

curl -s --user api:$MAILGUN_API_KEY \
https://api.mailgun.net/v3/crbclean.com/messages \
-F from='David Araya <davida@crbclean.com>' \
-F to='test@marketing.crbclean.com' \
-F subject='christmas special 2022' \
-F template='christmas1' \
-F t:variables='{"title": "API documentation", "body": "Sending messages with templates"}'
