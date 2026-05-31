package monster.dictionary.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import monster.dictionary.domain.BaseStats;
import monster.dictionary.domain.Evolution;
import monster.dictionary.domain.LearnableMove;
import monster.dictionary.domain.Monster;
import monster.dictionary.domain.Move;
import monster.dictionary.domain.type.Type;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveService;

/**
 * モンスター図鑑のサブメニュー画面を制御するUIクラス。
 * <p>コンソールからの入力に応じて、登録されているモンスターの一覧表示、
 * 名前の一部による部分一致検索、およびID指定による詳細情報の表示を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterUI {

    /** モンスターに関するビジネスロジックを処理するサービス */
    private final MonsterService service;
    
    /** 技の存在チェックとオブジェクト取得に利用する技サービス */
    private final MoveService moveService;

    /**
     * コンストラクタ。
     *
     * @param service モンスターサービス
     */
    public MonsterUI(MonsterService service, MoveService moveService) {
        this.service = service;
        this.moveService = moveService;
    }

    /**
     * モンスター図鑑のサブメニューを表示し、ユーザーからの入力を受け付けるループ処理を開始する。
     * <p>「0」が入力されるとループを抜け、呼び出し元のメインメニュー（PokedexUI）に戻る。</p>
     */
    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
        	System.out.println();
            System.out.println("=== モンスター図鑑 ===");
            System.out.println("1. 一覧表示");
            System.out.println("2. 名前検索");
            System.out.println("3. ID検索");
            System.out.println("4. 新規登録");
            System.out.println("0. 戻る");
            System.out.print("選択: ");

            String input = sc.nextLine();

            switch (input) {
                case "1" -> showAll();
                case "2" -> searchByName(sc);
                case "3" -> searchById(sc);
                case "4" -> register(sc);
                case "0" -> { return; }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    /**
     * 登録されているすべてのモンスターのIDと名前をコンソールに一覧表示する。
     */
    private void showAll() {
    	System.out.println();
    	System.out.println("=== モンスター一覧 ===");
        for (Monster m : service.findAll()) {
            System.out.println(m.getId() + ": " + m.getName());
        }
    }

    /**
     * ユーザーに入力を促し、入力されたキーワードを名前に含むモンスターを一覧表示する。
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     */
    private void searchByName(Scanner sc) {
    	System.out.println();
    	System.out.println("=== 名前検索 ===");
        System.out.print("名前の一部を入力: ");
        String keyword = sc.nextLine();

        for (Monster m : service.searchByName(keyword)) {
            System.out.println(m.getId() + ": " + m.getName());
        }
    }

    /**
     * ユーザーにIDの入力を促し、該当するモンスターの詳細なパラメータ（ステータス、習得技など）を表示する。
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @throws NumberFormatException 入力された文字列が数値に変換できない場合
     */
    private void searchById(Scanner sc) {
    	System.out.println();
    	System.out.println("=== ID検索 ===");
        System.out.print("ID を入力: ");
        
        // 注意: 数値以外の文字列が入力されると例外が発生する
        int id = Integer.parseInt(sc.nextLine());

        Monster m = service.findById(id);
        if (m == null) {
            System.out.println("見つかりませんでした。");
            return;
        }

        System.out.println("=== 詳細 ===");
        System.out.println("名前: " + m.getName());
        System.out.println("説明: " + m.getDescription());
        System.out.println("タイプ: " + m.getTypes());
        System.out.println("種族値: " + formatBaseStats(m.getBaseStats()));
        System.out.println("覚える技: ");
        
        for (LearnableMove lm : m.getLearnableMoves()) {
            if (lm.getLevel() == 0) {
                System.out.println("  [非レベルアップ] " + lm.getMove().getName());
            } else {
                System.out.println("  Lv" + lm.getLevel() + " " + lm.getMove().getName());
            }
        }
        
        List<Evolution> evolutions = service.getEvolutions(m.getId());

        if (!evolutions.isEmpty()) {
            System.out.println("進化：");
            for (Evolution evo : evolutions) {
                System.out.println(" → " + evo.getToId() + " (" + evo.getConditionType() + ":" + evo.getConditionValue() + ")");
            }
        }
    }
    
    /**
     * コンソールからのインタラクティブな入力により、新しいモンスターを生成して図鑑に登録する。
     * <p>ID、名前、説明の基本情報から始まり、ループによる複数タイプの追加、各種族値のバリデーション入力、
     * および技マスタ（moveService）と連動したレベルアップ習得技の紐付けを順次行う。
     * すべてのデータ入力が正常に完了した段階で Monster オブジェクトを構築し、サービスを介して永続化する。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     */
    private void register(Scanner sc) {
    	System.out.println();
        System.out.println("=== モンスター登録 ===");

        int id = inputInt(sc, "ID: ");

        System.out.print("名前: ");
        String name = sc.nextLine();

        System.out.print("説明: ");
        String description = sc.nextLine();

        // --- タイプ ---
        System.out.println();
        List<Type> types = new ArrayList<>();
        
        while (types.size() < 2) {
            Type t = inputType(sc);
            if (t == null) break;

            if (types.contains(t)) {
                System.out.println("同じタイプは重複できません。");
                continue;
            }

            types.add(t);

            if (types.size() == 2) {
                System.out.println("タイプは2つまでです。");
                break;
            }
        }
        
        // --- 種族値 ---
        System.out.println();
        System.out.println("=== 種族値 ===");
        int hp = inputInt(sc, "HP: ");
        int attack = inputInt(sc, "攻撃: ");
        int defense = inputInt(sc, "防御: ");
        int spAttack = inputInt(sc, "特攻: ");
        int spDefense = inputInt(sc, "特防: ");
        int speed = inputInt(sc, "素早さ: ");

        BaseStats stats = new BaseStats(hp, attack, defense, spAttack, spDefense, speed);

        // --- 覚える技 ---
        System.out.println();
        List<LearnableMove> learnableMoves = new ArrayList<>();
        System.out.println("=== 覚える技 ===");
        
        for (Move mv : moveService.findAll()) {
            System.out.println(" - " + mv.getName() + " (" + mv.getType() + ")");
        }
        
        while (true) {
            // クラス内に定義されている moveService フィールドを利用して存在チェックを行う
            Move move = inputMove(sc, moveService);
            if (move == null) {
                break;
            }

            System.out.println("この技はレベルアップで覚えますか？");
            System.out.println("1. はい（レベルを入力）");
            System.out.println("2. いいえ（レベルアップでは覚えない）");

            int choice = inputInt(sc, "選択: ");

            int level;
            if (choice == 1) {
                level = inputInt(sc, "習得レベル: ");
            } else {
                level = 0; // ★ レベルアップでは覚えない
            }

            learnableMoves.add(new LearnableMove(move, level));
        }

        // すべての部品を組み合わせてドメインオブジェクトを生成
        Monster monster = new Monster(
                id,
                name,
                types,
                stats,
                description,
                learnableMoves
        );

        service.register(monster);
        System.out.println("登録しました！");
    }
    
    /**
     * コンソールから安全に整数（int）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが数値以外の文字列を入力した場合は、エラーメッセージを表示して入力を再試行させる。
     * 正しい整数値が入力されるまで無限ループで待機する。</p>
     *
     * @param sc      コンソール入力を監視する Scanner オブジェクト
     * @param message 入力を促す際にコンソールへ表示する案内メッセージ
     * @return ユーザーが入力した有効な整数値
     */
    private int inputInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("数字を入力してください。");
            }
        }
    }
    
    /**
     * コンソールから安全にモンスターのタイプ（Type 列挙型）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが入力を小文字で行った場合でも自動的に大文字に変換してパースを試みる。
     * 存在しないタイプ名が入力された場合はエラーメッセージを表示して再試行を促し、
     * 何も入力せずに Enter が押された場合は入力処理の終了とみなして null を返す。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @return 入力された文字列に対応する Type 列挙型、入力を終了した（空のEnterが押された）場合は null
     * @throws IllegalArgumentException 入力された文字列が Type 列挙型の定義のいずれとも一致しない場合（内部で捕捉される）
     */
    private Type inputType(Scanner sc) {
        while (true) {
            System.out.print("タイプを入力（終了=空 Enter）: ");
            String input = sc.nextLine();

            if (input.isBlank()) {
                return null;
            }

            try {
                return Type.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("存在しないタイプです。");
                System.out.println("有効なタイプ: " + java.util.Arrays.toString(Type.values()));
            }
        }
    }
    
    /**
     * コンソールから安全に技（Move ドメインオブジェクト）を入力させ、実在する技オブジェクトを取得するユーティリティメソッド。
     * <p>ユーザーが入力した技名をもとに MoveService を用いて検索を行い、実在する技であればそのオブジェクトを返す。
     * 存在しない技名が入力された場合はエラーメッセージを表示して再試行を促し、
     * 何も入力せずに Enter が押された場合は入力処理の終了とみなして null を返す。</p>
     *
     * @param sc          コンソール入力を監視する Scanner オブジェクト
     * @param moveService 技の存在チェックとオブジェクト取得に利用する技サービス
     * @return 入力された技名に対応する Move オブジェクト、入力を終了した（空のEnterが押された）場合は null
     */
    private Move inputMove(Scanner sc, MoveService moveService) {
        while (true) {
            System.out.print("技名（終了=空 Enter）: ");
            String name = sc.nextLine();

            if (name.isBlank()) {
                return null;
            }

            Move move = moveService.findByName(name);
            if (move != null) {
                return move;
            }

            System.out.println("技が見つかりません。");
        }
    }
    
    /** 実数値の表示用文字列 */
    private String formatBaseStats(BaseStats stats) {
		return String.format("HP:%d 攻:%d 防:%d 特攻:%d 特防:%d 素早:%d",
				stats.getHp(), stats.getAttack(), stats.getDefense(),
				stats.getSpAttack(), stats.getSpDefense(), stats.getSpeed());
	}
    
}