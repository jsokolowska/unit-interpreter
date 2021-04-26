package source
import spock.lang.Specification
import util.position.PositionWrapper

class PositionWrapperSpec extends Specification {
    def "PositionWrapper should enable position tracking for Source"(){
        def positionWrapper = new PositionWrapper(new StringSource(str))
        for (int i =0 ; i< line_list.size(); i++){
            positionWrapper.get()
            assert positionWrapper.getColumn() == col_list[i]
            assert positionWrapper.getLine() == line_list[i]
        }
        where:
        str         || line_list        | col_list
        "tak\nnie"  || [1,1,1,2,2,2,2]  | [1,2,3,0,1,2,3]
    }

}
