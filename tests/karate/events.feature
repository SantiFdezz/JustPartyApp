Feature: Session and Events Test

Background:
    Given url baseUrl+"/user/session"
    Given def data = read('data/userValid.json')[1]
    Given def sorted = read('functions/isSorted.js')

Scenario Outline: Get SessionToken and test events endpoint with different examples
    Given url baseUrl+"/events"
    And header SessionToken = data.SessionToken
    And params <searchParams>
    And method get
    When status 200
    * def responseEvent = response
    * print 'Events: ' + JSON.stringify(responseEvent)
    * def isSorted = sorted(responseEvent, <searchParams>, data)
    Then assert isSorted == true

    Examples:
    | searchParams                     |
    | read('data/validSearch.json')[0] |
    | read('data/validSearch.json')[1] |
    | read('data/validSearch.json')[2] |