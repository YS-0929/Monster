package monster.dictionary.domain.type;

import java.util.List;

class DragonTypeEffective implements TypeEffective {

    @Override
    public List<Type> getGoodList() {
        return List.of(
                Type.DRAGON
        );
    }

    @Override
    public List<Type> getBadList() {
        return List.of(
                Type.STEEL
        );
    }

    @Override
    public List<Type> getNoneList() {
        return List.of(
                Type.FAIRY
        );
    }
}