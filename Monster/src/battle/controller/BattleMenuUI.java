package battle.controller;

import java.util.List;
import java.util.Scanner;

import battle.domain.Battle;
import battle.service.BattleService;
import trainer.domain.Trainer;
import trainer.service.TrainerService;

public class BattleMenuUI {

    private final Scanner scanner = new Scanner(System.in);
    private final TrainerService trainerService;
    private final BattleService battleService;

    public BattleMenuUI(TrainerService trainerService, BattleService battleService) {
        this.trainerService = trainerService;
        this.battleService = battleService;
    }

    public void start() {

        System.out.println("=== バトルメニュー ===");

        // トレーナー一覧取得
        List<Trainer> trainers = trainerService.findAll();

        if (trainers.size() < 2) {
            System.out.println("トレーナーが2人以上必要です。");
            return;
        }

        // プレイヤー側選択
        System.out.println("プレイヤー側のトレーナーを選択してください：");
        Trainer player = selectTrainer(trainers);

        // 敵側選択
        System.out.println("敵側のトレーナーを選択してください：");
        Trainer enemy = selectTrainer(trainers);

        // バトル開始
        Battle battle = new Battle(player, enemy);

        BattleUI ui = new BattleUI();
        ui.start(battle, battleService);
    }

    private Trainer selectTrainer(List<Trainer> trainers) {

        for (int i = 0; i < trainers.size(); i++) {
            System.out.println((i + 1) + ". " + trainers.get(i).getName());
        }

        while (true) {
            String input = scanner.nextLine();
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < trainers.size()) {
                    return trainers.get(idx);
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("無効な入力です。");
        }
    }
}