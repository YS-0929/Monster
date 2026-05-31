package monster.instance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import monster.dictionary.service.EvolutionService;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveService;
import monster.instance.domain.MonsterInstance;
import monster.instance.domain.Nature;
import monster.instance.domain.Stats;
import monster.instance.service.MonsterInstanceService;

/**
 * MonsterInstance（個体）を操作するコンソール UI クラス。
 * <p>
 * 個体一覧表示、登録、削除、詳細表示、進化などの操作を提供し、
 * 実際の処理は MonsterInstanceService に委譲する。
 * </p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterUI {

    private final Scanner scanner = new Scanner(System.in);

    private final MonsterInstanceService instanceService;
    private final MonsterService monsterService;
    private final MoveService moveService;
    private final EvolutionService evolutionService;

    public MonsterUI(
            MonsterInstanceService instanceService,
            MonsterService monsterService,
            MoveService moveService,
            EvolutionService evolutionService
    ) {
        this.instanceService = instanceService;
        this.monsterService = monsterService;
        this.moveService = moveService;
        this.evolutionService = evolutionService;
    }

    /**
     * メインメニューを表示し、ユーザー操作を受け付ける。
     */
    public void start() {
        while (true) {
            System.out.println("=== Monster Instance Menu ===");
            System.out.println("1. 個体一覧");
            System.out.println("2. 個体登録");
            System.out.println("3. 個体詳細");
            System.out.println("4. 個体削除");
            System.out.println("5. 進化");
            System.out.println("0. 戻る");
            System.out.print("選択: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> listInstances();
                case 2 -> registerInstance();
                case 3 -> showDetail();
                case 4 -> deleteInstance();
                case 5 -> evolveInstance();
                case 0 -> { return; }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    // ===== 個体一覧 =====

    private void listInstances() {
        System.out.println("--- 個体一覧 ---");
        List<MonsterInstance> list = instanceService.findAll();

        for (int i = 0; i < list.size(); i++) {
            MonsterInstance inst = list.get(i);
            System.out.printf("%d: %s Lv.%d (%s)\n",
                    i + 1,
                    inst.getNickname(),
                    inst.getLevel(),
                    inst.getSpecies().getName()
            );
        }
        System.out.println();
    }

    // ===== 個体登録 =====

    /**
     * 個体登録処理。
     * <p>
     * 種族一覧の表示、IV/EV の安全な入力、技選択など
     * ゲームとして自然な登録フローを提供する。
     * </p>
     */
    private void registerInstance() {
        System.out.println("--- 個体登録 ---");

        // ============================
        // 1. 種族一覧を表示
        // ============================
        System.out.println("=== 種族一覧 ===");
        monsterService.findAll().forEach(m ->
            System.out.printf("%d: %s\n", m.getId(), m.getName())
        );

        // 種族ID入力
        int speciesId;
        while (true) {
            System.out.print("種族IDを入力: ");
            speciesId = readInt();
            if (monsterService.findById(speciesId) != null) break;
            System.out.println("存在しない種族IDです。");
        }

        // ============================
        // 2. ニックネーム
        // ============================
        System.out.print("ニックネーム: ");
        String nickname = scanner.nextLine();

        // ============================
        // 3. レベル
        // ============================
        int level;
        while (true) {
            System.out.print("レベル(1〜100): ");
            level = readInt();
            if (level >= 1 && level <= 100) break;
            System.out.println("1〜100 の範囲で入力してください。");
        }

        // ============================
        // 4. 性格
        // ============================
        Nature nature;
        while (true) {
            System.out.print("性格（例：JOLLY）: ");
            try {
                nature = Nature.valueOf(scanner.nextLine().toUpperCase());
                break;
            } catch (Exception e) {
                System.out.println("存在しない性格です。");
            }
        }

        // ============================
        // 5. IV 入力（0〜31）
        // ============================
        System.out.println("IV を入力（0〜31）");

        int ivHp = readIV("HP");
        int ivAtk = readIV("攻撃");
        int ivDef = readIV("防御");
        int ivSpA = readIV("特攻");
        int ivSpD = readIV("特防");
        int ivSpe = readIV("素早さ");

        Stats iv = new Stats(ivHp, ivAtk, ivDef, ivSpA, ivSpD, ivSpe);

        // ============================
        // 6. EV 入力（最大32 / 合計66）
        // ============================
        System.out.println("EV を振るステータスを選び、数値を入力してください。");
        System.out.println("最大32ポイント、合計66ポイントまで。");

        int evHp = 0, evAtk = 0, evDef = 0, evSpA = 0, evSpD = 0, evSpe = 0;
        int remaining = 66;

        while (remaining > 0) {
            System.out.printf("残りポイント: %d\n", remaining);
            System.out.println("1: HP  2: 攻撃  3: 防御  4: 特攻  5: 特防  6: 素早さ  0: 終了");
            System.out.print("どこに振る？: ");

            int choice = readInt();
            if (choice == 0) break;

            System.out.print("何ポイント振る？(最大32): ");
            int amount = readInt();

            if (amount < 0 || amount > 32) {
                System.out.println("0〜32 の範囲で入力してください。");
                continue;
            }
            if (amount > remaining) {
                System.out.println("残りポイントを超えています。");
                continue;
            }

            switch (choice) {
                case 1 -> evHp += amount;
                case 2 -> evAtk += amount;
                case 3 -> evDef += amount;
                case 4 -> evSpA += amount;
                case 5 -> evSpD += amount;
                case 6 -> evSpe += amount;
                default -> {
                    System.out.println("無効な選択です。");
                    continue;
                }
            }

            remaining -= amount;
        }

        Stats ev = new Stats(evHp, evAtk, evDef, evSpA, evSpD, evSpe);

        // ============================
        // 7. 技選択（最大4つ）
        // ============================
        System.out.println("=== 覚えられる技一覧 ===");
        monsterService.findById(speciesId)
                .getLearnableMoves()
                .forEach(m -> System.out.println(" - " + m.getMove().getName()));

        System.out.println("覚える技名を入力（最大4つ、空で終了）");

        List<String> moves = new ArrayList<>();
        while (moves.size() < 4) {
            System.out.print("技名: ");
            String name = scanner.nextLine();

            if (name.isEmpty()) break;

            // 技が存在するかチェック
            if (moveService.findByName(name) == null) {
                System.out.println("存在しない技です。もう一度入力してください。");
                continue;
            }

            // その種族が覚えられるかチェック
            boolean canLearn = monsterService.findById(speciesId)
                    .getLearnableMoves()
                    .stream()
                    .anyMatch(m -> m.getMove().getName().equalsIgnoreCase(name));

            if (!canLearn) {
                System.out.println("この種族はその技を覚えられません。");
                continue;
            }

            moves.add(name);
        }

        // ============================
        // 8. 個体生成
        // ============================
        instanceService.create(speciesId, nickname, level, nature, iv, ev, moves);

        System.out.println("登録しました。\n");
    }

    // ===== 個体詳細 =====

    private void showDetail() {
        listInstances();
        System.out.print("詳細を表示する番号: ");
        int index = readInt() - 1;

        MonsterInstance inst = instanceService.findByIndex(index);
        if (inst == null) {
            System.out.println("存在しません。\n");
            return;
        }

        System.out.println("--- 個体詳細 ---");
        System.out.println("ニックネーム: " + inst.getNickname());
        System.out.println("種族: " + inst.getSpecies().getName());
        System.out.println("レベル: " + inst.getLevel());
        System.out.println("性格: " + inst.getNature());
        System.out.println("実数値: " + formatActual(inst.getActualStats()));
        System.out.println("技:");
        inst.getMoves().forEach(m -> System.out.println(" - " + m.getName()));
        System.out.println();
    }

    // ===== 個体削除 =====

    private void deleteInstance() {
        listInstances();
        System.out.print("削除する番号: ");
        int index = readInt() - 1;

        MonsterInstance inst = instanceService.findByIndex(index);
        if (inst == null) {
            System.out.println("存在しません。\n");
            return;
        }

        instanceService.delete(inst);
        System.out.println("削除しました。\n");
    }

    // ===== 進化 =====

    private void evolveInstance() {
        listInstances();
        System.out.print("進化させる番号: ");
        int index = readInt() - 1;

        MonsterInstance inst = instanceService.findByIndex(index);
        if (inst == null) {
            System.out.println("存在しません。\n");
            return;
        }

        instanceService.tryEvolve(inst, evolutionService);
        System.out.println("進化処理を実行しました。\n");
    }

    // ===== 入力補助 =====

    private int readInt() {
        while (true) {
            try {
                String s = scanner.nextLine();
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.print("数字を入力してください: ");
            }
        }
    }
    
    /** IV を 0〜31 の範囲で安全に入力させる */
    private int readIV(String label) {
        while (true) {
            System.out.printf("%s IV (0〜31): ", label);
            int v = readInt();
            if (v >= 0 && v <= 31) return v;
            System.out.println("0〜31 の範囲で入力してください。");
        }
    }
    
    /** 実数値の表示用文字列 */
    private String formatActual(Stats s) {
        return String.format(
            "HP:%d 攻:%d 防:%d 特攻:%d 特防:%d 速:%d",
            s.getHp(), s.getAttack(), s.getDefense(),
            s.getSpAttack(), s.getSpDefense(), s.getSpeed()
        );
    }

}