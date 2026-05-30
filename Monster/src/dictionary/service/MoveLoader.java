package dictionary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dictionary.domain.Move;
import dictionary.domain.MoveCategory;
import dictionary.domain.StatChange;
import dictionary.domain.StatType;
import dictionary.domain.Status;
import dictionary.domain.Type;
import dictionary.repository.MoveRepository;

/**
 * CSV の生データを Move ドメインオブジェクトに組み立てるローダー。
 * <p>技リポジトリから取得したフラットなCSVデータを解析し、型変換や列挙型（Enum）のマッピング、
 * および能力変化（StatChanges）の複合文字列の分解処理を行って Move オブジェクトを構築する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MoveLoader {
	/** 技データのリポジトリ */
    private final MoveRepository moveRepo;

    /**
     * コンストラクタ。
     *
     * @param moveRepo 技データのリポジトリ
     */
    public MoveLoader(MoveRepository moveRepo) {
        this.moveRepo = moveRepo;
    }

    /**
     * すべての技データをCSVファイルから読み込み、List として返す。
     *
     * @return 構築された Move オブジェクトのリスト
     * @throws RuntimeException データのパースや型変換に失敗した場合
     */
    public List<Move> loadAll() {
        List<Move> moves = new ArrayList<>();

        for (String[] row : moveRepo.findAllRaw()) {
            moves.add(parseMove(row));
        }

        return moves;
    }

    /**
     * すべての技データをCSVファイルから読み込み、技名をキーとした Map として返す。
     * <p>このメソッドは MonsterLoader がモンスターデータに技を紐付ける（マスタデータを参照する）際に利用される。</p>
     *
     * @return 技名をキー、Move オブジェクトを値とするマップ
     * @throws RuntimeException データのパースや型変換に失敗した場合
     */
    public Map<String, Move> loadAllAsMap() {
        Map<String, Move> map = new HashMap<>();
        
        for (String[] row : moveRepo.findAllRaw()) {
            Move move = parseMove(row);
            map.put(move.getName(), move);
        }
        
        return map;
    }

    /**
     * 1行分の生の文字列配列データから Move オブジェクトをパースして生成する。
     *
     * @param row CSVの1行に対応する文字列配列
     * @return 構築された Move オブジェクト
     */
    private Move parseMove(String[] row) {
        String name = row[0];
        Type type = Type.valueOf(row[1]);
        MoveCategory category = MoveCategory.valueOf(row[2]);
        int power = Integer.parseInt(row[3]);
        int accuracy = Integer.parseInt(row[4]);
        double criticalRate = Double.parseDouble(row[5]);
        int priority = Integer.parseInt(row[6]);

        List<StatChange> statChanges = parseStatChanges(row[7]);

        Status statusEffect = Status.valueOf(row[8]);
        double statusChance = Double.parseDouble(row[9]);

        return new Move(
                name, type, category,
                power, accuracy, criticalRate,
                priority, statChanges,
                statusEffect, statusChance
        );
    }

    /**
     * 技に付随する能力変化（バフ・デバフ）の特殊な文字列表現をパースしてオブジェクトのリストに変換する。
     * <p>「STAT_TYPE:STAGES」のペア（例: ATTACK:1）を解析し、適切な列挙型と数値に分解する。
     * 複数の能力変化が存在する場合は、あらかじめカンマで区切られている必要がある。</p>
     *
     * @param raw 能力変化を表す生の文字列（nullや空文字も許容）
     * @return 解析された StatChange オブジェクトのリスト（データがない場合は空のリスト）
     */
    private List<StatChange> parseStatChanges(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        List<StatChange> list = new ArrayList<>();
        String[] parts = raw.split(";");

        for (String p : parts) {
            if (!p.contains(":")) continue;
            String[] kv = p.split(":");
            StatType stat = StatType.valueOf(kv[0]);
            int stages = Integer.parseInt(kv[1]);
            list.add(new StatChange(stat, stages));
        }
        
        return list;
    }
}
