package trainer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import monster.instance.domain.MonsterInstance;
import monster.instance.service.MonsterInstanceService;
import trainer.domain.Trainer;
import trainer.service.TrainerService;

/**
 * Trainer（トレーナー）を操作するコンソール UI クラス。
 * <p>
 * トレーナー一覧表示、登録、ボックス管理、手持ち選択などの操作を提供し、
 * 実際の処理は TrainerService に委譲する。
 * </p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class TrainerUI {

    private final Scanner scanner = new Scanner(System.in);

    private final TrainerService trainerService;
    private final MonsterInstanceService instanceService;

    public TrainerUI(
            TrainerService trainerService,
            MonsterInstanceService instanceService
    ) {
        this.trainerService = trainerService;
        this.instanceService = instanceService;
    }

    /**
     * メインメニューを表示し、ユーザー操作を受け付ける。
     */
    public void start() {
        while (true) {
            System.out.println("=== Trainer Menu ===");
            System.out.println("1. トレーナー一覧");
            System.out.println("2. トレーナー登録");
            System.out.println("3. ボックス表示");
            System.out.println("4. ボックスに個体を追加");
            System.out.println("5. 手持ち3体を選択");
            System.out.println("0. 戻る");
            System.out.print("選択: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> listTrainers();
                case 2 -> registerTrainer();
                case 3 -> showBox();
                case 4 -> addToBox();
                case 5 -> selectParty();
                case 0 -> { return; }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    // ===== トレーナー一覧 =====

    private void listTrainers() {
        System.out.println("--- トレーナー一覧 ---");
        List<Trainer> list = trainerService.findAll();

        for (int i = 0; i < list.size(); i++) {
            Trainer t = list.get(i);
            System.out.printf("%d: %s（所持金: %d）\n",
                    i + 1, t.getName(), t.getMoney());
        }
        System.out.println();
    }

    // ===== トレーナー登録 =====

    private void registerTrainer() {
        System.out.println("--- トレーナー登録 ---");

        System.out.print("名前: ");
        String name = scanner.nextLine();

        System.out.print("初期所持金: ");
        int money = readInt();

        trainerService.create(name, money);

        System.out.println("登録しました。\n");
    }

    // ===== ボックス表示 =====

    private void showBox() {
        Trainer trainer = selectTrainer();
        if (trainer == null) return;

        System.out.println("--- ボックス ---");
        List<MonsterInstance> box = trainer.getBox();

        if (box.isEmpty()) {
            System.out.println("ボックスは空です。\n");
            return;
        }

        for (int i = 0; i < box.size(); i++) {
            MonsterInstance inst = box.get(i);
            System.out.printf("%d: %s Lv.%d (%s)\n",
                    i + 1,
                    inst.getNickname(),
                    inst.getLevel(),
                    inst.getSpecies().getName()
            );
        }
        System.out.println();
    }

    // ===== ボックスに個体を追加 =====

    private void addToBox() {
    	Trainer trainer = selectTrainer();
        if (trainer == null) return;

        System.out.println("--- 個体一覧（手持ちに入っている個体は除外） ---");

        // 全個体
        List<MonsterInstance> all = instanceService.findAll();

        // 全トレーナーの手持ちを収集
        List<Trainer> trainers = trainerService.findAll();
        List<MonsterInstance> occupied = new ArrayList<>();

        for (Trainer t : trainers) {
            occupied.addAll(t.getParty()); // 手持ちにいる個体は除外対象
        }

        // 表示用リスト（手持ちに入っていない個体だけ）
        List<MonsterInstance> candidates = new ArrayList<>();
        for (MonsterInstance inst : all) {
            if (!occupied.contains(inst)) {
                candidates.add(inst);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("追加可能な個体がありません。\n");
            return;
        }

        // 表示
        for (int i = 0; i < candidates.size(); i++) {
            MonsterInstance inst = candidates.get(i);
            System.out.printf("%d: %s Lv.%d (%s)\n",
                    i + 1,
                    inst.getNickname(),
                    inst.getLevel(),
                    inst.getSpecies().getName()
            );
        }

        System.out.print("ボックスに追加する個体番号: ");
        int index = readInt() - 1;

        if (index < 0 || index >= candidates.size()) {
            System.out.println("存在しません。\n");
            return;
        }

        trainerService.addToBox(trainer, candidates.get(index));
        System.out.println("追加しました。\n");
    }

    // ===== 手持ち3体を選択 =====

    private void selectParty() {
        Trainer trainer = selectTrainer();
        if (trainer == null) return;

        List<MonsterInstance> box = trainer.getBox();

        if (box.isEmpty()) {
            System.out.println("ボックスが空です。\n");
            return;
        }

        System.out.println("--- ボックス ---");
        for (int i = 0; i < box.size(); i++) {
            MonsterInstance inst = box.get(i);
            System.out.printf("%d: %s Lv.%d (%s)\n",
                    i + 1,
                    inst.getNickname(),
                    inst.getLevel(),
                    inst.getSpecies().getName()
            );
        }

        System.out.println("手持ちにする個体を選んでください。");
        System.out.println("0 を入力すると終了します。（最大3体まで）");

        List<MonsterInstance> newParty = new ArrayList<>();

        while (newParty.size() < 3) {
            System.out.printf("%d 体目の選択（0で終了）: ", newParty.size() + 1);
            int index = readInt() - 1;

            if (index == -1) break; // 0 入力で終了

            if (index < 0 || index >= box.size()) {
                System.out.println("存在しません。");
                continue;
            }

            MonsterInstance inst = box.get(index);

            if (newParty.contains(inst)) {
                System.out.println("同じ個体は選べません。");
                continue;
            }

            newParty.add(inst);
        }

        trainerService.setParty(trainer, newParty);
        System.out.println("手持ちを更新しました。\n");
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

    private Trainer selectTrainer() {
        listTrainers();
        System.out.print("トレーナー番号: ");
        int index = readInt() - 1;

        List<Trainer> list = trainerService.findAll();
        if (index < 0 || index >= list.size()) {
            System.out.println("存在しません。\n");
            return null;
        }
        return list.get(index);
    }
}
