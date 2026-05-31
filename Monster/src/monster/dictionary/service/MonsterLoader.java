package monster.dictionary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monster.dictionary.domain.BaseStats;
import monster.dictionary.domain.LearnableMove;
import monster.dictionary.domain.Monster;
import monster.dictionary.domain.Move;
import monster.dictionary.domain.type.Type;
import monster.dictionary.repository.BaseStatsRepository;
import monster.dictionary.repository.LearnableMoveRepository;
import monster.dictionary.repository.MonsterRepository;
import monster.dictionary.repository.MonsterTypeRepository;

/**
 * CSV の生データを組み合わせ、Monster ドメインオブジェクトを組み立てるローダー。
 * <p>各リポジトリから取得したフラットなCSVデータを、モンスターIDをキーとしてマッピングし、
 * 依存関係のある関連オブジェクト（種族値、タイプ、習得技）を一元的に結合する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterLoader {
	/** モンスター基本情報のリポジトリ */
    private final MonsterRepository monsterRepo;
    
    /** ベース種族値のリポジトリ */
    private final BaseStatsRepository baseStatsRepo;
    
    /** モンスタータイプ紐付けのリポジトリ */
    private final MonsterTypeRepository monsterTypeRepo;
    
    /** レベルアップ習得技のリポジトリ */
    private final LearnableMoveRepository learnableMoveRepo;
    
    /** 技データのローダー */
    private final MoveLoader moveLoader;

    /**
     * コンストラクタ。
     *
     * @param monsterRepo       モンスター基本情報のリポジトリ
     * @param baseStatsRepo     ベース種族値のリポジトリ
     * @param monsterTypeRepo   モンスタータイプ紐付けのリポジトリ
     * @param learnableMoveRepo レベルアップ習得技のリポジトリ
     * @param moveLoader        技データのローダー
     */
    public MonsterLoader(
            MonsterRepository monsterRepo,
            BaseStatsRepository baseStatsRepo,
            MonsterTypeRepository monsterTypeRepo,
            LearnableMoveRepository learnableMoveRepo,
            MoveLoader moveLoader
    ) {
        this.monsterRepo = monsterRepo;
        this.baseStatsRepo = baseStatsRepo;
        this.monsterTypeRepo = monsterTypeRepo;
        this.learnableMoveRepo = learnableMoveRepo;
        this.moveLoader = moveLoader;
    }

    /**
     * すべてのリポジトリからデータを読み込み、完全に組み立てられた Monster ドメインオブジェクトのリストを返す。
     *
     * @return 完全に構築された Monster オブジェクトのリスト
     * @throws RuntimeException データのパースや数値変換、またはデータの不整合が発生した場合
     */
    public List<Monster> loadAll() {
        Map<String, Move> moveMap = moveLoader.loadAllAsMap();
        Map<Integer, BaseStats> statsMap = loadBaseStats();
        Map<Integer, List<Type>> typeMap = loadMonsterTypes();
        Map<Integer, List<LearnableMove>> learnMap = loadLearnableMoves(moveMap);

        List<Monster> monsters = new ArrayList<>();

        for (String[] row : monsterRepo.findAllRaw()) {
            int id = Integer.parseInt(row[0]);
            String name = row[1];
            String description = row[2];

            monsters.add(new Monster(
                    id,
                    name,
                    typeMap.getOrDefault(id, List.of()),
                    statsMap.get(id),
                    description,
                    learnMap.getOrDefault(id, List.of())
            ));
        }

        return monsters;
    }

    /**
     * ベース種族値のCSVデータを読み込み、モンスターIDをキーとしたマップに変換する。
     *
     * @return モンスターIDとベース種族値のマップ
     */
    private Map<Integer, BaseStats> loadBaseStats() {
        Map<Integer, BaseStats> map = new HashMap<>();
        
        for (String[] row : baseStatsRepo.findAllRaw()) {
            map.put(Integer.parseInt(row[0]), new BaseStats(
                    Integer.parseInt(row[1]),
                    Integer.parseInt(row[2]),
                    Integer.parseInt(row[3]),
                    Integer.parseInt(row[4]),
                    Integer.parseInt(row[5]),
                    Integer.parseInt(row[6])
            ));
        }
        
        return map;
    }

    /**
     * モンスターのタイプ紐付けCSVデータを読み込み、モンスターIDをキーとしたタイプのリストのマップに変換する。
     * <p>1つのモンスターIDに対して複数のタイプが紐付く（多対多）ことを想定している。</p>
     *
     * @return モンスターIDと所属タイプのリストのマップ
     */
    private Map<Integer, List<Type>> loadMonsterTypes() {
        Map<Integer, List<Type>> map = new HashMap<>();
        
        for (String[] row : monsterTypeRepo.findAllRaw()) {
            int id = Integer.parseInt(row[0]);
            Type type = Type.valueOf(row[1]);
            map.computeIfAbsent(id, k -> new ArrayList<>()).add(type);
        }
        
        return map;
    }

    /**
     * レベルアップ習得技のCSVデータを読み込み、モンスターIDをキーとした習得技リストのマップに変換する。
     *
     * @param moveMap 技名から技オブジェクトを引くためのマスタマップ
     * @return モンスターIDとレベルアップ習得技のリストのマップ
     */
    private Map<Integer, List<LearnableMove>> loadLearnableMoves(Map<String, Move> moveMap) {
        Map<Integer, List<LearnableMove>> map = new HashMap<>();
        
        for (String[] row : learnableMoveRepo.findAllRaw()) {
            int id = Integer.parseInt(row[0]);
            Move move = moveMap.get(row[1]);
            int level = Integer.parseInt(row[2]);
            map.computeIfAbsent(id, k -> new ArrayList<>()).add(new LearnableMove(move, level));
        }
        
        return map;
    }
}
