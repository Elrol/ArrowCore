package dev.elrol.arrow.libs;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CobblemonUtils {

    public static PlayerPartyStore getParty(ServerPlayerEntity player) {
        return Cobblemon.INSTANCE.getStorage().getParty(player);
    }

    public static int getTotalIvs(Pokemon pokemon) {
        int total = 0;
        for(Map.Entry<? extends Stat, ? extends Integer> entry : pokemon.getIvs()) {
            total += entry.getValue();
        }
        return total;
    }

    public static PCStore getPC(ServerPlayerEntity player){
        return Cobblemon.INSTANCE.getStorage().getPC(player);
    }

    public static void givePokemon(ServerPlayerEntity player, Pokemon pokemon) {
        PlayerPartyStore party = getParty(player);


        String name = pokemon.getSpecies().getName();

        if(party.add(pokemon)) {
            player.sendMessage(ModTranslations.msg("sent_to_party", name));
        } else {
            player.sendMessage(ModTranslations.msg("sent_to_pc", name));
        }
    }

    @Nullable
    public static Pokemon getSlot(ServerPlayerEntity player, int slot) {
        if(slot >= 6) return null;
        return CobblemonUtils.getParty(player).get(slot);
    }

    public static GuiElementBuilder addPokeStatElement(GuiElementBuilder original, @Nullable Pokemon pokemon) {
        if(pokemon == null) return original;
        //DaycareUtils.getEggStats(pokemon, pokemon);
        List<Text> lore = new ArrayList<Text>(Collections.singletonList(ModTranslations.info("species").formatted(Formatting.GREEN).append(pokemon.getSpecies().getTranslatedName().formatted(Formatting.GRAY))));

        lore.add(ModTranslations.info("gender").formatted(Formatting.DARK_PURPLE).append(Text.literal(pokemon.getGender().name()).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("ability").formatted(Formatting.AQUA).append(Text.translatable(pokemon.getAbility().getDisplayName()).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("nature").formatted(Formatting.DARK_AQUA).append(Text.translatable(pokemon.getNature().getDisplayName()).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("item").formatted(Formatting.DARK_BLUE).append(MutableText.of(pokemon.getHeldItem$common().getName().getContent()).formatted(Formatting.GRAY)));

        lore.add(Text.literal(" "));
        lore.add(ModTranslations.info("move_separator").formatted(Formatting.BOLD, Formatting.DARK_GRAY));

        int moveIndex = 0;
        for (Move move : pokemon.getMoveSet()) {
            moveIndex++;
            if(move != null) lore.add(ModTranslations.info("move", moveIndex).formatted(Formatting.DARK_GREEN).append(move.getDisplayName().formatted(Formatting.GRAY)));
        }

        lore.add(Text.literal(" "));
        lore.add(ModTranslations.info("iv_separator").formatted(Formatting.BOLD, Formatting.DARK_GRAY));

        IVs ivs = pokemon.getIvs();
        lore.add(ModTranslations.info("hp").formatted(Formatting.RED).append(Text.literal("" + ivs.get(Stats.HP)).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("attack").formatted(Formatting.GOLD).append(Text.literal("" + ivs.get(Stats.ATTACK)).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("defense").formatted(Formatting.YELLOW).append(Text.literal("" + ivs.get(Stats.DEFENCE)).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("special_attack").formatted(Formatting.GREEN).append(Text.literal("" + ivs.get(Stats.SPECIAL_ATTACK)).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("special_defense").formatted(Formatting.BLUE).append(Text.literal("" + ivs.get(Stats.SPECIAL_DEFENCE)).formatted(Formatting.GRAY)));
        lore.add(ModTranslations.info("speed").formatted(Formatting.LIGHT_PURPLE).append(Text.literal("" + ivs.get(Stats.SPEED)).formatted(Formatting.GRAY)));
        return original.setLore(lore);
    }

    public static boolean hasDestinyKnot(Pokemon... pokemon) {
        for (Pokemon poke : pokemon) {
            if(isHoldingItem(poke, CobblemonItems.DESTINY_KNOT)) return true;
        }
        return false;
    }

    public static boolean isDitto(Pokemon pokemon) {
        return isSpecies(pokemon, "ditto");
    }

    public static boolean isSpecies(Pokemon pokemon, String speciesName) {
        return pokemon.getSpecies().equals(PokemonSpecies.INSTANCE.getByName(speciesName));
    }

    public static boolean isHoldingItem(Pokemon pokemon, Item item) {
        return pokemon.heldItem().getItem().equals(item);
    }

    public static Species getSpecies(String name){
        return PokemonSpecies.INSTANCE.getByName(name);
    }

    public static boolean hasGender(Pokemon pokemon) {
        return pokemon.getSpecies().getPossibleGenders().contains(Gender.MALE) &&
                pokemon.getSpecies().getPossibleGenders().contains(Gender.FEMALE);
    }
}
