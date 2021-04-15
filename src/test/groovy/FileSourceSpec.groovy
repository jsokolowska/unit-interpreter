import source.FileSource
import spock.lang.Specification

class FileSourceSpec extends Specification{
    def "Scanner add method should return sum" (){
        given:
            def fileSource = new FileSource()
        when:
            def res = fileSource.add(x, y)
        then:
            res == expected
        where:
            x   | y || expected
            1   | 2 || 3
            12  |-2 || 10
    }
}
