import spock.lang.Specification

class SampleSpec extends Specification{
    def "check case-insensitive equality of two strings" () {
        given:
        String str1 = "Hello"
        String str2 = "HELlo"
        when:
        str1 = str1.toLowerCase()
        str2 = str2.toLowerCase()
        then:
        str1 == str2
    }
}
