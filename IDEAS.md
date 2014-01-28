#Major Project Idea

##Security and settings

- Ability to encrypt sensitive data using SQLCipher
- Decrypt data using a PIN
- Pin will be set after users credentials have been verfied
- User can change this PIN
- User can reset PIN by reentering there credntials, this will delete the database and redownload all the sensitive data
- Store non-sensitive data (Settings) on the cloud

##Data retrieval

- Data will be sent over a JSON API.
- Users will login with credentials, on authentication JSON API will return an access_token which will be saved in the encrypted database
- Data will be requested from the JSON api using url's such as **https://example.com/fetchDrugs.json?last_update=123412321&access_token=XXXX**
- The above URL will return all drugs which have been updated since the last_update timestamp
- All data must be sent over **SSL**

#Notifications

- Notifications will be sent to all devices when a drug has been added or modified.

