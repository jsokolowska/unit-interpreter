package source


import spock.lang.Specification
class StringSourceSpec extends Specification {
    def "String source should point to first char or EOF after initializing"(){
        given:
        def source = new StringSource(str)

        when:
        def first = source.get()

        then:
        first == expected

        where:
        str     || expected
        ""      || Source.EOT
        "aaaa"  || "a"
    }
    def "String source should return next chars one by one and then EOF"(){
        def source = new StringSource(str)

        c_list.each{
            assert it == source.get()
        }

        where:
        str     || c_list
        "tak"   || ["t", "a", "k", Source.EOT]
    }
}
