package org.pommes2864.test.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.*;

public class Test implements ModInitializer {


    public static final String MOD_ID = "TestModPommes";
    private static ArrayList<Coordinates> ArrayListCoords = new ArrayList<Coordinates>();

    @Override
    public void onInitialize() {

        AddToCoordinates("test", 5, 5, 5);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("coord")
                .then(literal("list")
                        .executes(Test::ReturnAllCoordinates))

                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .executes(Test::AddCoordinatesOfPlayerToArrayList)


                                .then(argument("posx", IntegerArgumentType.integer())
                                        .then(argument("posy", IntegerArgumentType.integer())
                                                .then(argument("posz", IntegerArgumentType.integer())
                                                        .executes(Test::AddCustomCoordinatesToArrayList)
                                                )
                                        )
                                )

                        )
                )
                .then(literal("delete")
                .then(argument("name", StringArgumentType.string())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");

                            if (DeleteArrayListEntry(name)){
                                context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde gelöscht."), false);
                            }
                            else {
                                context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde nicht gefunden."), false);
                            }
                            return 1;

                        }
        )))));

        /*CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("listcoords")
                        .executes(Test::ReturnAllCoordinates)));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("addcoord")
                .then(argument("name", StringArgumentType.string())
                        .executes(Test::AddCoordinatesOfPlayerToArrayList)


                .then(argument("posx", IntegerArgumentType.integer())
                        .then(argument("posy", IntegerArgumentType.integer())
                                .then(argument("posz", IntegerArgumentType.integer())
                                        .executes(Test::AddCustomCoordinatesToArrayList)
                                )
                        )
                )

        )));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("delcoord")
                .then(argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    final String name = StringArgumentType.getString(context, "name");

                                    if (DeleteArrayListEntry(name)){
                                        context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde gelöscht."), false);
                                    }
                                    else {
                                        context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde nicht gefunden."), false);
                                    }
                                    return 1;

                                }))));*/

    }





    public static int ReturnAllCoordinates(CommandContext<ServerCommandSource> context){
        if(ArrayListCoords.stream().count() == 0){
            context.getSource().sendFeedback(() -> Text.literal("Es wurden noch keine Koordinaten gespeichert!"), false);
        }else {
            for (Coordinates c : ArrayListCoords) {
                context.getSource().sendFeedback(() -> Text.literal(c.toString()), false);
            }
        }
        return 1;
    }

    public static int AddCustomCoordinatesToArrayList(CommandContext<ServerCommandSource> context){
        boolean ifexist = false;

        final String name = StringArgumentType.getString(context, "name");
        final int posx = IntegerArgumentType.getInteger(context, "posx");
        final int posy = IntegerArgumentType.getInteger(context, "posy");
        final int posz = IntegerArgumentType.getInteger(context, "posz");

        for (Coordinates arrayListCoord : ArrayListCoords) {
            if (arrayListCoord.getName() != null && arrayListCoord.getName().equals(name)) {
                ifexist = true;
                break;
            }

        }
        if(ifexist) {
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " konnte nicht hinzugefügt werden, da bereits ein Punkt mit dem gleichen Namen vorhanden ist."), false);
        } else {
            AddToCoordinates(name, posx, posy, posz);
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde Hinzugefügt"), false);
        }



        return 1;
    }

    public static int AddCoordinatesOfPlayerToArrayList(@NotNull CommandContext<ServerCommandSource> context){
        boolean ifexist = false;
        ServerPlayerEntity player = context.getSource().getPlayer();
        BlockPos pos = player.getBlockPos();

        int PlayerX = pos.getX();
        int PlayerY = pos.getY();
        int PlayerZ = pos.getZ();

        final String name = StringArgumentType.getString(context, "name");

        for (Coordinates arrayListCoord : ArrayListCoords) {
            if (arrayListCoord.getName() != null && arrayListCoord.getName().equals(name)) {
                ifexist = true;
                break;
            }

        }
        if(ifexist) {
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " konnte nicht hinzugefügt werden, da bereits ein Punkt mit dem gleichen Namen vorhanden ist."), false);
        } else {
            AddToCoordinates(name, PlayerX, PlayerY, PlayerZ);
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde Hinzugefügt"), false);
        }

        return 1;
    }

    public static void AddToCoordinates(String name, int Xcoord, int Ycoord, int Zcoord){

        ArrayListCoords.add(new Coordinates(name, Xcoord, Ycoord, Zcoord));

    }

    public static boolean DeleteArrayListEntry(String filterArg){

        boolean ifdel = false;
        for (int i = 0; i < ArrayListCoords.size(); i++) {
            if (ArrayListCoords.get(i).getName() != null && ArrayListCoords.get(i).getName().equals(filterArg)) {
                ArrayListCoords.remove(ArrayListCoords.get(i));
                ifdel = true;
            }

        }
        return ifdel;

        }


}

