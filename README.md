
# user-allow-list-admin-frontend

This service is intended to reduce the amount of code service teams need to write, maintain and later remove for managing a list of allowed users (for example, as part of a private beta rollout). It allows you to maintain the list of allowed identifiers for services you are responsible for. Specifically you can:

- Add values to an allow list
- Remove values from an allow list
- Clear an allow list
- Check if a value is in an allow list

These actions can all be performed for multiple allow lists for a single service, by providing a "feature" argument that acts as a name for an allow list.

It is used in combination with [user-allow-list](https://github.com/hmrc/user-allow-list), which is responsible for storing the list of allowed identifiers that services can check against. The [readme for user allow list](https://github.com/hmrc/user-allow-list/blob/main/README.md) provides a more complete description of what is and isn't supported, along with integration instructions.


## Accessing the service

Access to the service is via the `admin-frontend-proxy`, using the URL route `/administer-user-allow-list` after the admin URL the desired environment. You will be required to login using LDAP as this identifies which services you can update identifier lists for.

For security and traceability purposes, any changes to an identifier list are audited including who made those changes.


## Making changes to an identifier list

When you log in using your LDAP credentials, you will be presented with a list of services. For example:

![An example list of services listed in the service](https://user-images.githubusercontent.com/687363/231569065-afd14fbf-90ae-44a2-910e-7a9a82f200d7.png)

This list is populated based upon teams you are a member of according to the User Management Portal. If you believe you are missing services, check your team membership there first.

Selecting a service will provide you with a summary screen, which provides details of any features that identifiers have been added for. For example:

![An example summary screen for a single service](https://user-images.githubusercontent.com/687363/231570079-aade30e1-6b3e-4b17-a667-d50b8ad9ca6d.png)


### Adding values

Using the "Add values to an allow list" option, you can add one or more identifier value for a given feature (separating multiple values with commas):

![An example of the screen for adding new values to an allow list](https://user-images.githubusercontent.com/687363/231570839-0dc194ca-80e4-4100-8b12-c4f806fad39a.png)


### Removing values

Using the "Remove values from an allow list" option, you can remove one or more identifier values for a given feature (separating multiple values with commas):

![An example of the screen for removing values from an allow list](https://user-images.githubusercontent.com/687363/231571410-f5c6e9ee-c1df-40e6-a960-eb533c7b90df.png)


### Clearing all values

Using the "Clear the allow list for a feature" option, you can remove all identifier values for a given feature at once, even if you don't know the values:

![An example of the screen for clearing all values from an allow list](https://user-images.githubusercontent.com/687363/231571745-f6bdaba5-7f24-4b1c-8dea-94c7484b5ad8.png)


### Check if a value is present

Using the "Check if a value exists in an allow list" option, you can check if a single identifier value is present for a given feature:

![An example of the screen for checking if values are present in an allow list](https://user-images.githubusercontent.com/687363/231572058-ce90e202-e68d-41c2-9abb-373b839cf3be.png)


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
