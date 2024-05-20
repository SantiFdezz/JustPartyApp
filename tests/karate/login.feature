Feature: Login Feature on JPARTY API
    On this feature we are gonna test the login 
    
Scenario: User logged in succesfully
    Given url baseUrl+ '/user/session'
    Given def data = read('data/userValid.json')[1]
    Given request data
    When method post
    Then status 201
    And match response contains {"response": "ok"}

