set "$ApiKey=api:key-xxxxxxxxxx"
set "$Url=https://api.mailgun.net/v3/crbclean.com/messages"
set "$From='Mongo Marketing <info@crbclean.com>'"
set "$To='internal@crbclean.com'"
set "$Subject='hello123'"
set "$Template='test'"

curl -s --user '%$ApiKey%' %$Url% -F from="%$From%" -F to="%$To%" -F subject="%$Subject%" -F template="%$Template%"