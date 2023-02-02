

# mailgun api creds of florian are in private gist:
# https://gist.github.com/awb99/

echo api:$MAILGUN_API_KEY

curl -s --user api:$MAILGUN_API_KEY -G \
   https://api.mailgun.net/v3/crbclean.com/bounces