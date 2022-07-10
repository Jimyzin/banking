# BANKING APPLICATION

### Assumptions
1. Transfer between 2 bank accounts are considered a "WITHDRAWAL" from the source bank account and a "DEPOSIT" into the destination bank account. In other words, all business rules for a withdrawal and a deposit are applied.
2. Using a custom Validation Service to keep all validation error messages consistent and also the bank account validation requires to connect to the database to validate it. To reduce to 2 calls to the database - (1) For validating the bank account by calling the database (2) For getting the Account entity for performing further business logic on it, ValidationService ensures the both can be done by just 1 database call. This optimises the performance.


### Evaluating the Application
1. BankingServiceIntegrationTest has comprehensive test cases encompassing all test cases mentioned in the task definition. Thus, the test cases in this class can be modified to test the business logic.
