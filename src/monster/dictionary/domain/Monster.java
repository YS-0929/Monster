package monster.dictionary.domain;

import java.util.Collections;
import java.util.List;

import monster.dictionary.domain.type.Type;

/**
 * モンスターの基本データを表すクラス。
 * 図鑑として必要な情報のみを保持する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class Monster {
	
	/** 図鑑番号 */
    private final int id;
    
    /** モンスター名 */
    private final String name;
    
    /** タイプ（最大2つを想定） */
    private final List<Type> types;
    
    /** 種族値 */
    private final BaseStats baseStats;
    
    /** 図鑑の説明文 */
    private final String description;
    
    /** レベルアップで覚える技のリスト */
    private final List<LearnableMove> learnableMoves;

    /**
     * コンストラクタ。
     *
     * @param id             図鑑番号
     * @param name           モンスター名
     * @param types          タイプ
     * @param stats          種族値
     * @param description    図鑑の説明文
     * @param learnableMoves レベルアップで覚える技のリスト
     */
    public Monster(int id, String name, List<Type> types,
                   BaseStats baseStats, String description,
                   List<LearnableMove> learnableMoves) {

        this.id = id;
        this.name = name;
        this.types = List.copyOf(types);
        this.baseStats = baseStats;
        this.description = description;
        this.learnableMoves = List.copyOf(learnableMoves);
    }

    /**
     * 図鑑番号を取得する。
     *
     * @return 図鑑番号
     */
    public int getId() {
    	return id;
    }
    
    /**
     * モンスター名を取得する。
     *
     * @return モンスター名
     */
    public String getName() {
    	return name;
    }
    
    /**
     * タイプのリストを取得する。
     *
     * @return {@link Type} のリスト
     */
    public List<Type> getTypes() {
    	return Collections.unmodifiableList(types);
    }
    
    /**
     * 種族値を取得する。
     *
     * @return {@link Stats} オブジェクト
     */
    public BaseStats getBaseStats() {
    	return baseStats;
    }
    
    /**
     * 図鑑の説明文を取得する。
     *
     * @return 図鑑の説明文
     */
    public String getDescription() {
    	return description;
    }
    
    /**
     * レベルアップで覚える技のリストを取得する。
     *
     * @return {@link LearnableMove} のリスト
     */
    public List<LearnableMove> getLearnableMoves() {
    	return Collections.unmodifiableList(learnableMoves);
    }
}