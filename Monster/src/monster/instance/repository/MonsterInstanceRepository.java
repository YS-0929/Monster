package monster.instance.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import monster.dictionary.domain.Monster;
import monster.dictionary.domain.Move;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveService;
import monster.instance.domain.MonsterInstance;
import monster.instance.domain.Nature;
import monster.instance.domain.Stats;

/**
 * MonsterInstance（個体データ）を CSV で永続化するリポジトリ。
 * 辞書データ（種族・技）は MonsterService / MoveService から復元する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterInstanceRepository {

    private final String filePath;
    private final MonsterService monsterService;
    private final MoveService moveService;

    public MonsterInstanceRepository(
            String filePath,
            MonsterService monsterService,
            MoveService moveService
    ) {
        this.filePath = filePath;
        this.monsterService = monsterService;
        this.moveService = moveService;
    }

    /**
     * CSV からすべての個体データを読み込み、MonsterInstance のリストとして返す。
     */
    public List<MonsterInstance> loadAll() {
        List<MonsterInstance> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // ヘッダー行スキップ

            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                int index = 0;

                int instanceId = Integer.parseInt(row[index++]); // 今は未使用
                int speciesId = Integer.parseInt(row[index++]);
                String nickname = row[index++];
                int level = Integer.parseInt(row[index++]);
                int currentExp = Integer.parseInt(row[index++]);
                Nature nature = Nature.valueOf(row[index++]);

                // IV
                Stats iv = new Stats(
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++])
                );

                // EV
                Stats ev = new Stats(
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++]),
                        Integer.parseInt(row[index++])
                );

                // 技
                List<Move> moves = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    if (index < row.length && !row[index].isEmpty()) {
                        Move move = moveService.findByName(row[index]);
                        if (move != null) moves.add(move);
                    }
                    index++;
                }

                // 種族データを辞書から復元
                Monster species = monsterService.findById(speciesId);

                MonsterInstance instance = new MonsterInstance(
                        species,
                        nickname,
                        level,
                        currentExp,
                        nature,
                        iv,
                        ev,
                        moves
                );

                list.add(instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * すべての MonsterInstance を CSV に保存する。
     */
    public void saveAll(List<MonsterInstance> instances) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            bw.write("instanceId,speciesId,nickname,level,currentExp,nature,"
                    + "ivHp,ivAtk,ivDef,ivSpA,ivSpD,ivSpe,"
                    + "evHp,evAtk,evDef,evSpA,evSpD,evSpe,"
                    + "move1,move2,move3,move4");
            bw.newLine();

            int id = 1;
            for (MonsterInstance inst : instances) {
                StringBuilder sb = new StringBuilder();

                sb.append(id++).append(",");
                sb.append(inst.getSpecies().getId()).append(",");
                sb.append(inst.getNickname()).append(",");
                sb.append(inst.getLevel()).append(",");
                sb.append(inst.getCurrentExp()).append(",");
                sb.append(inst.getNature().name()).append(",");

                // IV
                sb.append(inst.getIv().getHp()).append(",");
                sb.append(inst.getIv().getAttack()).append(",");
                sb.append(inst.getIv().getDefense()).append(",");
                sb.append(inst.getIv().getSpAttack()).append(",");
                sb.append(inst.getIv().getSpDefense()).append(",");
                sb.append(inst.getIv().getSpeed()).append(",");

                // EV
                sb.append(inst.getEv().getHp()).append(",");
                sb.append(inst.getEv().getAttack()).append(",");
                sb.append(inst.getEv().getDefense()).append(",");
                sb.append(inst.getEv().getSpAttack()).append(",");
                sb.append(inst.getEv().getSpDefense()).append(",");
                sb.append(inst.getEv().getSpeed()).append(",");

                // 技
                List<Move> moves = inst.getMoves();
                for (int i = 0; i < 4; i++) {
                    if (i < moves.size()) {
                        sb.append(moves.get(i).getName());
                    }
                    sb.append(",");
                }

                bw.write(sb.toString());
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}