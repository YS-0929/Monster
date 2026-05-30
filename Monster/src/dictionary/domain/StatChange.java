package dictionary.domain;

/**
 * 技によって発生する能力変化を表すクラス。
 * 
 * @author Suzuki
 * @version 1.0
 */
public class StatChange {
	
	/** 対象ステータス */
	private final StatType stat;
	
	/** 変化段階 */
    private final int stages;

    /**
     * コンストラクタ。
     *
     * @param stat   対象ステータス
     * @param stages 変化段階（+1, -2 など）
     */
    public StatChange(StatType stat, int stages) {
        this.stat = stat;
        this.stages = stages;
    }

    /**
     * 対象ステータスを取得する。
     * 
     * @return 対象ステータスを表す {@link StatType} オブジェクト
     */
    public StatType getStat() {
        return stat;
    }

    /**
     * ステータスの変化段階を取得する。
     * 
     * @return
     */
    public int getStages() {
        return stages;
    }
}
