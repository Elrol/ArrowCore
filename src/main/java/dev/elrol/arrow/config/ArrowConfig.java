package dev.elrol.arrow.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.arrow.ArrowCore;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ArrowConfig extends _BaseConfig {

    public static final Codec<ArrowConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("isDebug").forGetter(data -> data.isDebug) ,
        Codec.BOOL.fieldOf("loadOldData").forGetter(data -> data.loadOldData),
        Codec.BOOL.fieldOf("useDatabase").forGetter(data -> data.useDatabase),
        DatabaseInfo.CODEC.fieldOf("databaseInfo").forGetter(data -> data.databaseInfo),
        Codec.BOOL.fieldOf("transferImpactorToArrowEcon").forGetter(data -> data.transferImpactorToArrowEcon)
    ).apply(instance, (isDebug, loadOldData, useDatabase, databaseInfo, transferImpactorToArrowEcon) -> {
        ArrowConfig data = new ArrowConfig();
        data.isDebug = isDebug;
        data.loadOldData = loadOldData;
        data.useDatabase = useDatabase;
        data.databaseInfo = databaseInfo;
        data.transferImpactorToArrowEcon = transferImpactorToArrowEcon;
        return data;
    }));

    // Config Settings
    public boolean isDebug = false;
    public boolean loadOldData = false;
    public boolean transferImpactorToArrowEcon = false;

    // TODO Database not being used
    public boolean useDatabase = false;
    public DatabaseInfo databaseInfo = new DatabaseInfo();

    // Config Methods
    public ArrowConfig() {
        super("core");
    }

    @Override
    public <T extends _BaseConfig> T load() {
        T config = super.load();
        ArrowCore.CONFIG = (ArrowConfig) config;
        return config;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends _BaseConfig> Codec<T> getCodec() {
        return (Codec<T>) CODEC;
    }

    public static class DatabaseInfo {
        public static final Codec<DatabaseInfo> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("hostname").forGetter(data -> data.hostname),
                Codec.INT.fieldOf("port").forGetter(data -> data.port),
                Codec.STRING.fieldOf("username").forGetter(data -> data.username),
                Codec.STRING.fieldOf("password").forGetter(data -> data.password),
                Codec.STRING.fieldOf("schema").forGetter(data -> data.schema)

            ).apply(instance, (hostname, port, username, password, schema) -> {
                DatabaseInfo data = new DatabaseInfo();

                data.hostname = hostname;
                data.port = port;
                data.username = username;
                data.password = password;
                data.schema = schema;

                return data;
            }));

        public String hostname = "";
        public int port = 0;
        public String username = "";
        public String password = "";

        public String schema = "";

        public boolean isValid() {
            return (!hostname.isEmpty()) && (port > 0) && (!username.isEmpty()) && (!password.isEmpty() && (!schema.isEmpty()));
        }

    }

    public static class CustomizerSettings {
        public boolean canCustomize = true;
        public boolean canCustomizeIVs = true;
        public boolean canCustomizeEVs = true;
        public boolean canCustomizeNature = true;
        public boolean canCustomizeAbility = true;
        public boolean canCustomizeCaughtBall = true;
        public boolean canCustomizeGender = true;
        public boolean canCustomizeShiny = true;

        public boolean canCustomizeLegends = true;
        public boolean canCustomizeLegendIVs = true;
        public boolean canCustomizeLegendEVs = true;
        public boolean canCustomizeLegendNature = true;
        public boolean canCustomizeLegendAbility = true;
        public boolean canCustomizeLegendCaughtBall = true;
        public boolean canCustomizeLegendGender = true;
        public boolean canCustomizeLegendShiny = true;

        private final String statCostFormulaComment1 = "Valid Variables:";
        private final String statCostFormulaComment2 = "   C to represent total current IV for stat";
        private final String statCostFormulaComment3 = "   L is 2 if the pokemon is legendary and 1 if not";
        private final String statCostFormulaComment4 = "Example: \"(C + 1) * L\"";
        public String ivsCostFormula = "(C + 1) * L";
        public String evsCostFormula = "1 * L";

        private final String genderCostFormulaComment1 = "Valid Variables:";
        private final String genderCostFormulaComment2 = "   G to represent total percentage of that gender for that species";
        private final String genderCostFormulaComment3 = "   L is 2 if the pokemon is legendary and 1 if not";
        private final String genderCostFormulaComment4 = "Example: \"(G * 50) * L\"";
        public String genderCostFormula = "(G * 50) * L";

        private final String abilityCostFormulaComment1 = "Valid Variables:";
        private final String abilityCostFormulaComment2 = "   H is 2 if the desired ability is a hidden one and 1 if not";
        private final String abilityCostFormulaComment3 = "   L is 2 if the pokemon is legendary and 1 if not";
        private final String abilityCostFormulaComment4 = "Example: \"(H * 50) * L\"";
        public String abilityCostFormula = "(H * 25) * L";

        private final String natureCostFormulaComment1 = "Valid Variables:";
        private final String natureCostFormulaComment2 = "   L is 2 if the pokemon is legendary and 1 if not";
        private final String natureCostFormulaComment3 = "Example: \"10 * L\"";
        public String natureCostFormula = "10 * L";

        private final String shinyCostFormulaComment1 = "Valid Variables:";
        private final String shinyCostFormulaComment2 = "   S is 2 if the pokemon isn't shiny and 1 if they are";
        private final String shinyCostFormulaComment3 = "   L is 2 if the pokemon is legendary and 1 if not";
        private final String shinyCostFormulaComment5 = "Example: \"1 + ((S - 1) * 24 * L)\"";
        public String shinyCostFormula = "1 + ((S - 1) * 24 * L)";

        public int calcIVCost(int currentIV, boolean isLegendary) {
            Expression e = new ExpressionBuilder(ivsCostFormula).variables("C", "L").build();
            e.setVariable("C", currentIV);
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }

        public int calcEVCost(int currentEV, boolean isLegendary) {
            Expression e = new ExpressionBuilder(evsCostFormula).variables("C", "L").build();
            e.setVariable("C", currentEV);
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }

        public int calcGenderCost(float genderChance, boolean isLegendary) {
            Expression e = new ExpressionBuilder(genderCostFormula).variables("G", "L").build();
            e.setVariable("G", genderChance);
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }

        public int calcAbilityCost(boolean isHidden, boolean isLegendary) {
            Expression e = new ExpressionBuilder(abilityCostFormula).variables("H", "L").build();
            e.setVariable("H", isHidden ? 2 : 1);
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }

        public int calcNatureCost(boolean isLegendary) {
            Expression e = new ExpressionBuilder(natureCostFormula).variable("L").build();
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }

        public int calcShinyCost(boolean isShiny, boolean isLegendary) {
            Expression e = new ExpressionBuilder(shinyCostFormula).variables("S", "L").build();
            e.setVariable("S", isShiny ? 1 : 2);
            e.setVariable("L", isLegendary ? 2 : 1);
            return (int) e.evaluate();
        }
    }
}
