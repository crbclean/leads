set "$ApiKey=api:key-xxxxxxxxxx"
set "$Url=https://api.mailgun.net/v3/crbclean.com/messages"
set "$From='David Araya <davida@crbclean.com>'"
set "$To='test@marketing.crbclean.com'"
set "$Subject='hello123'"
set "$Template='test'"

curl -s --user '%$ApiKey%' %$Url% -F from="%$From%" -F to="%$To%" -F subject="%$Subject%" -F template="%$Template%"