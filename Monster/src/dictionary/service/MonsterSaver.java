package dictionary.service;

import java.util.ArrayList;
import java.util.List;

import dictionary.domain.BaseStats;
import dictionary.domain.LearnableMove;
import dictionary.domain.Monster;
import dictionary.domain.Type;
import dictionary.repository.BaseStatsRepository;
import dictionary.repository.LearnableMoveRepository;
import dictionary.repository.MonsterRepository;
import dictionary.repository.MonsterTypeRepository;

/**
 * Monster ドメインオブジェクトを分解し、複数の CSV ファイルに保存するセーバー。
 * <p>オブジェクト指向的にカプセル化されたモンスターのデータを、関係データベースの正規化のように
 * 基本情報、種族値、タイプ、習得技のフラットな構造へ分解し、各リポジトリを介して保存する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterSaver {
	/** モンスター基本情報のリポジトリ */
    private final MonsterRepository monsterRepo;
    
    /** ベース種族値のリポジトリ */
    private final BaseStatsRepository baseStatsRepo;
    
    /** モンスタータイプ紐付けのリポジトリ */
    private final MonsterTypeRepository monsterTypeRepo;
    
    /** レベルアップ習得技のリポジトリ */
    private final LearnableMoveRepository learnableMoveRepo;

    /**
     * コンストラクタ。
     *
     * @param monsterRepo       モンスター基本情報のリポジトリ
     * @param baseStatsRepo     ベース種族値のリポジトリ
     * @param monsterTypeRepo   モンスタータイプ紐付けのリポジトリ
     * @param learnableMoveRepo レベルアップ習得技のリポジトリ
     */
    public MonsterSaver(
            MonsterRepository monsterRepo,
            BaseStatsRepository baseStatsRepo,
            MonsterTypeRepository monsterTypeRepo,
            LearnableMoveRepository learnableMoveRepo
    ) {
        this.monsterRepo = monsterRepo;
        this.baseStatsRepo = baseStatsRepo;
        this.monsterTypeRepo = monsterTypeRepo;
        this.learnableMoveRepo = learnableMoveRepo;
    }

    /**
     * 指定されたすべての Monster ドメインオブジェクトを分解し、各CSVファイルへ一括保存する。
     *
     * @param monsters 保存対象の Monster オブジェクトのリスト
     */
    public void saveAll(List<Monster> monsters) {
        saveMonsters(monsters);
        saveBaseStats(monsters);
        saveMonsterTypes(monsters);
        saveLearnableMoves(monsters);
    }

    /**
     * モンスターの基本情報（ID、名前、説明）を抽出し、CSVに保存する。
     *
     * @param monsters 保存対象の Monster オブジェクトのリスト
     */
    private void saveMonsters(List<Monster> monsters) {
        List<String[]> rows = new ArrayList<>();
        
        for (Monster m : monsters) {
            rows.add(new String[]{
                    String.valueOf(m.getId()),
                    m.getName(),
                    m.getDescription()
            });
        }
        
        monsterRepo.saveAllRaw(rows);
    }

    /**
     * モンスターのベース種族値を抽出し、CSVに保存する。
     *
     * @param monsters 保存対象の Monster オブジェクトのリスト
     */
    private void saveBaseStats(List<Monster> monsters) {
        List<String[]> rows = new ArrayList<>();
        
        for (Monster m : monsters) {
            BaseStats s = m.getBaseStats();
            rows.add(new String[]{
                    String.valueOf(m.getId()),
                    String.valueOf(s.getHp()),
                    String.valueOf(s.getAttack()),
                    String.valueOf(s.getDefense()),
                    String.valueOf(s.getSpAttack()),
                    String.valueOf(s.getSpDefense()),
                    String.valueOf(s.getSpeed())
            });
        }
        
        baseStatsRepo.saveAllRaw(rows);
    }

    /**
     * モンスターとタイプの紐付け情報を抽出し、多対多のCSV形式に平坦化して保存する。
     *
     * @param monsters 保存対象の Monster オブジェクトのリスト
     */
    private void saveMonsterTypes(List<Monster> monsters) {
        List<String[]> rows = new ArrayList<>();
        
        for (Monster m : monsters) {
            for (Type t : m.getTypes()) {
                rows.add(new String[]{
                        String.valueOf(m.getId()),
                        t.name()
                });
            }
        }
        
        monsterTypeRepo.saveAllRaw(rows);
    }

    /**
     * モンスターがレベルアップで覚える技の情報を抽出し、CSVに保存する。
     *
     * @param monsters 保存対象の Monster オブジェクトのリスト
     */
    private void saveLearnableMoves(List<Monster> monsters) {
        List<String[]> rows = new ArrayList<>();
        
        for (Monster m : monsters) {
            for (LearnableMove lm : m.getLearnableMoves()) {
                rows.add(new String[]{
                        String.valueOf(m.getId()),
                        lm.getMove().getName(),
                        String.valueOf(lm.getLevel())
                });
            }
        }
        
        learnableMoveRepo.saveAllRaw(rows);
    }
}
