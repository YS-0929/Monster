package monster.dictionary.service;

import java.util.List;
import java.util.Map;

import monster.dictionary.domain.Evolution;
import monster.dictionary.domain.Monster;

/**
 * 進化データを扱うサービスクラス。
 * <p>EvolutionLoader によって構築された進化マップを保持し、
 * 進化条件の取得や進化先の検索を行う。</p>
 *
 * MonsterInstance は進化条件を持たないため、
 * 進化判定はこのサービスに委譲する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class EvolutionService {

    /** 進化前ID → Evolutionリスト のマップ */
    private final Map<Integer, List<Evolution>> evolutionMap;

    /** 種族データを取得するための辞書サービス */
    private final MonsterService monsterService;

    public EvolutionService(EvolutionLoader loader, MonsterService monsterService) {
        this.evolutionMap = loader.loadAsMap();
        this.monsterService = monsterService;
    }

    /**
     * 指定されたモンスターIDからの進化データを返す。
     *
     * @param fromId 進化前の種族ID
     * @return Evolution のリスト（存在しない場合は空リスト）
     */
    public List<Evolution> getEvolutionsFrom(int fromId) {
        return evolutionMap.getOrDefault(fromId, List.of());
    }

    /**
     * レベル進化が可能かどうか判定する。
     *
     * @param speciesId 種族ID
     * @param level 現在のレベル
     * @return 進化可能なら true
     */
    public boolean canEvolveByLevel(int speciesId, int level) {
        for (Evolution evo : getEvolutionsFrom(speciesId)) {
            if (evo.getConditionType().equalsIgnoreCase("level")) {
                int required = Integer.parseInt(evo.getConditionValue());
                if (level >= required) return true;
            }
        }
        return false;
    }

    /**
     * レベル進化の進化先を返す。
     *
     * @param speciesId 種族ID
     * @param level 現在のレベル
     * @return 進化先の Monster（進化不可なら null）
     */
    public Monster getNextEvolutionByLevel(int speciesId, int level) {
        for (Evolution evo : getEvolutionsFrom(speciesId)) {
            if (evo.getConditionType().equalsIgnoreCase("level")) {
                int required = Integer.parseInt(evo.getConditionValue());
                if (level >= required) {
                    return monsterService.findById(evo.getToId());
                }
            }
        }
        return null;
    }
}