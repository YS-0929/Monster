package monster.dictionary.domain;
/**
 * 	モンスターがレベルアップで覚える技を表すクラス。
 * 
 * * @author Suzuki
 * @version 1.0
 */
public class LearnableMove {
	
	/** 技 */
    private final Move move;
    
    /** 覚えるレベル */
    private final int level;

    /**
     * コンストラクタ。
     *
     * @param move  技
     * @param level 覚えるレベル
     */
    public LearnableMove(Move move, int level) {
        this.move = move;
        this.level = level;
    }

    /**
     * 技を取得する。
     *
     * @return {@link Move} オブジェクト
     */
    public Move getMove() {
    	return move;
    }
    
    /**
     * 技を覚えるレベルを取得する。
     *
     * @return 覚えるレベル
     */
    public int getLevel() {
    	return level;
    }
}
