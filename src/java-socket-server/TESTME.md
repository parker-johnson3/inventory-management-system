Last Updated: 4/17/24

## Create Test
Create a test file in the 'test' directory that has an identical relative path 
as in the atual java project. All test files must have 'Test' appended to the end
of their name in order to run. 

## Testing Repositories

All query methods should test the following:
1. Format and Spelling of Query String
2. Schema Response fields

Handle method do not need to be tested -- this is because mock Gson is too difficult. 

Instead, we will ensure the handle methods work in our live tests.

## Run Test

Use `./gradlew test` to run a test.