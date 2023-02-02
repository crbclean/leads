

# mailgun api creds of florian are in private gist:
# https://gist.github.com/awb99/

echo api:$MAILGUN_API_KEY

curl -s --user api:$MAILGUN_API_KEY \
https://api.mailgun.net/v3/crbclean.com/messages \
-F from='David Araya <davida@crbclean.com>' \
-F to='carpetcleaner@marketing.crbclean.com' \
-F subject='UV Lights Special Offer' \
-F template='uv-lights' \
-F t:variables='{"title": "API documentation", "body": "Sending messages with templates"}'
