package trainer.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import monster.instance.domain.MonsterInstance;
import monster.instance.service.MonsterInstanceService;
import trainer.domain.Trainer;

/**
 * Trainer（トレーナー）を CSV で永続化するリポジトリ。
 * <p>
 * 手持ちモンスターは MonsterInstance のインデックス（1始まり）で保存し、
 * 読み込み時に MonsterInstanceService を通して復元する。
 * </p>
 *
 * CSV 形式：
 * name,money,party
 * 例）Satoshi,3000,1;3;5;
 *
 * @author Suzuki
 * @version 1.0
 */
public class TrainerRepository {

    /** CSV ファイルパス */
    private final String filePath;

    /** MonsterInstance の復元に使用するサービス */
    private final MonsterInstanceService instanceService;

    public TrainerRepository(String filePath, MonsterInstanceService instanceService) {
        this.filePath = filePath;
        this.instanceService = instanceService;
    }

    /**
     * CSV からすべてのトレーナーを読み込む。
     *
     * @return トレーナーのリスト
     */
    public List<Trainer> loadAll() {
        List<Trainer> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // header

            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                String name = row[0];
                int money = Integer.parseInt(row[1]);

                // 列が足りない場合は空扱い
                String boxStr = row.length > 2 ? row[2] : "";
                String partyStr = row.length > 3 ? row[3] : "";

                // box 復元
                List<MonsterInstance> box = new ArrayList<>();
                for (String idStr : boxStr.split(";")) {
                    if (!idStr.isEmpty()) {
                        int idx = Integer.parseInt(idStr);
                        box.add(instanceService.findByIndex(idx - 1));
                    }
                }

                // party 復元
                List<MonsterInstance> party = new ArrayList<>();
                for (String idStr : partyStr.split(";")) {
                    if (!idStr.isEmpty()) {
                        int idx = Integer.parseInt(idStr);
                        party.add(instanceService.findByIndex(idx - 1));
                    }
                }

                list.add(new Trainer(name, money, box, party));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * すべてのトレーナーを CSV に保存する。
     *
     * @param trainers 保存対象のトレーナー一覧
     */
    public void saveAll(List<Trainer> trainers) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            bw.write("name,money,box,party");
            bw.newLine();

            for (Trainer t : trainers) {
                StringBuilder sb = new StringBuilder();

                sb.append(t.getName()).append(",");
                sb.append(t.getMoney()).append(",");

                // box
                for (MonsterInstance inst : t.getBox()) {
                    int idx = instanceService.findAll().indexOf(inst) + 1;
                    sb.append(idx).append(";");
                }
                sb.append(",");

                // party
                for (MonsterInstance inst : t.getParty()) {
                    int idx = instanceService.findAll().indexOf(inst) + 1;
                    sb.append(idx).append(";");
                }

                bw.write(sb.toString());
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}