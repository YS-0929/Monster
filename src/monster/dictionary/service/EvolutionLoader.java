package monster.dictionary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import monster.dictionary.domain.Evolution;
import monster.dictionary.repository.EvolutionRepository;

/**
 * CSV の生データから Evolution ドメインオブジェクトを組み立てるローダー。
 * <p>進化リポジトリから取得した生の文字列配列データを解析し、適切な型へと変換して
 * 進化関係のオブジェクトを構築する。また、特定のモンスターからの進化経路を高速に検索するための
 * マップ構造（グループ化）の生成処理も提供する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class EvolutionLoader {
	
	/** 進化データのリポジトリ */
    private final EvolutionRepository repo;

    /**
     * コンストラクタ。
     *
     * @param repo 進化データのリポジトリ
     */
    public EvolutionLoader(EvolutionRepository repo) {
        this.repo = repo;
    }

    /**
     * すべての進化データをCSVファイルから読み込み、List として返す。
     *
     * @return 構築された Evolution オブジェクトのリスト
     * @throws NumberFormatException IDを表す文字列が数値に変換できない場合
     */
    public List<Evolution> loadAll() {
        List<Evolution> list = new ArrayList<>();

        for (String[] row : repo.findAllRaw()) {
            int fromId = Integer.parseInt(row[0]);
            int toId = Integer.parseInt(row[1]);
            String type = row[2];
            String value = row[3];

            list.add(new Evolution(fromId, toId, type, value));
        }

        return list;
    }

    /**
     * すべての進化データを読み込み、進化前のモンスターIDをキーとしたグループ化マップとして返す。
     * <p>Java 8 の computeIfAbsent を用いて、1つの進化前IDに対して複数の進化先（分岐進化など）が
     * 存在する場合でも、自動的にリストへ集約してマッピングを行う。</p>
     *
     * @return 進化前のモンスターIDをキー、そこから派生する Evolution オブジェクトのリストを値とするマップ
     */
    public Map<Integer, List<Evolution>> loadAsMap() {
        Map<Integer, List<Evolution>> map = new HashMap<>();

        for (Evolution evo : loadAll()) {
            // キーが存在しない場合は新しい ArrayList を生成し、そこへ進化データを追加する
            map.computeIfAbsent(evo.getFromId(), k -> new ArrayList<>()).add(evo);
        }

        return map;
    }
}
