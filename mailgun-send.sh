

echo api:$MAILGUN_API_KEY

curl -s --user api:$MAILGUN_API_KEY \
https://api.mailgun.net/v3/crbclean.com/messages \
-F from='Mongo Marketing <info@crbclean.com>' \
-F to='internal@crbclean.com' \
-F subject='Hello' \
-F template='test' \
-F t:variables='{"title": "API documentation", "body": "Sending messages with templates"}'

# -F from='Mailgun Sandbox <postmaster@crbclean.com>' \